package io.github.alirezajavan.shamsipicker.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import io.github.alirezajavan.shamsipicker.ui.theme.ShamsiPickerColors
import io.github.alirezajavan.shamsipicker.ui.theme.ShamsiPickerDimens
import io.github.alirezajavan.shamsipicker.ui.theme.ShamsiPickerTypography
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlin.math.abs

/**
 * iOS-style "drum" wheel picker.
 */
@Composable
public fun WheelPicker(
    itemCount: Int,
    initialIndex: Int,
    onSelectedIndexChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    infinite: Boolean = true,
    visibleCount: Int = ShamsiPickerDimens.WHEEL_DEFAULT_VISIBLE_COUNT,
    itemHeight: Dp = ShamsiPickerDimens.WHEEL_ITEM_HEIGHT_DP.dp,
    enabledRange: IntRange = 0 until itemCount,
    textStyle: TextStyle = MaterialTheme.typography.titleLarge,
    selectedColor: Color = MaterialTheme.colorScheme.onSurface,
    unselectedColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    disabledColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = ShamsiPickerDimens.DISABLED_CONTENT_ALPHA),
    fadeColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
    dimAlpha: Float = ShamsiPickerDimens.WHEEL_DIM_ALPHA,
    content: @Composable (index: Int) -> Unit,
) {
    require(itemCount > 0) { "itemCount must be > 0" }
    val odd = if (visibleCount % 2 == 0) visibleCount + 1 else visibleCount
    val half = odd / 2

    val currentEnabled = rememberUpdatedState(enabledRange)
    val currentOnSelected = rememberUpdatedState(onSelectedIndexChange)

    fun clampToEnabled(logical: Int): Int {
        val range = currentEnabled.value
        return if (range.isEmpty()) logical else logical.coerceIn(range.first, range.last)
    }

    val loops = if (infinite) ShamsiPickerDimens.WHEEL_INFINITE_LOOP_COUNT else 1
    val total = loops * itemCount
    val base = if (infinite) (loops / 2) * itemCount else 0
    val startRaw = (base + initialIndex).coerceIn(0, total - 1)

    val listCount = total + 2 * half
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = startRaw)
    val flingBehavior = rememberSnapFlingBehavior(listState)
    val haptics = LocalHapticFeedback.current

    fun rawToLogical(raw: Int) = if (infinite) ((raw % itemCount) + itemCount) % itemCount else raw

    val centeredLogical by remember(listState, itemCount, infinite) {
        derivedStateOf {
            val info = listState.layoutInfo
            val viewportCenter = (info.viewportStartOffset + info.viewportEndOffset) / 2f
            val nearest =
                info.visibleItemsInfo
                    .filter { it.index in half until half + total }
                    .minByOrNull { abs((it.offset + it.size / 2f) - viewportCenter) }
            if (nearest == null) initialIndex else rawToLogical(nearest.index - half)
        }
    }

    LaunchedEffect(listState, itemCount, infinite) {
        var first = true
        snapshotFlow { centeredLogical }
            .distinctUntilChanged()
            .collect {
                if (!first) haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                first = false
            }
    }

    suspend fun settleOntoEnabled() {
        val current = centeredLogical
        val target = clampToEnabled(current)
        if (target != current) {
            val info = listState.layoutInfo
            val centerPx = (info.viewportStartOffset + info.viewportEndOffset) / 2f
            val centeredItem =
                info.visibleItemsInfo
                    .filter { it.index in half until half + total }
                    .minByOrNull { abs((it.offset + it.size / 2f) - centerPx) }
            if (centeredItem != null) {
                val currentRaw = centeredItem.index - half
                val destination = (currentRaw + (target - current)).coerceIn(0, total - 1)
                try {
                    listState.animateScrollToItem(destination)
                } catch (_: CancellationException) {
                    // A new user gesture grabbed the same list mid-animation and cancelled it
                    // via the scroll mutex. That is not "this coroutine got cancelled" - rethrow
                    // only if it actually is, otherwise bail out without emitting a target we
                    // never reached and let the gesture's own scroll-stop drive the next settle.
                    currentCoroutineContext().ensureActive()
                    return
                }
            }
        }
        currentOnSelected.value(target)
    }

    // A single collector drives every settle so concurrent corrections can never race on
    // the same listState: both "scrolling stopped" and "enabledRange changed" (e.g. the
    // range picker's "to" wheel bounds shifting while its "from" wheel moves) funnel through
    // this one flow instead of two independent LaunchedEffects.
    LaunchedEffect(listState, itemCount, infinite) {
        snapshotFlow { listState.isScrollInProgress to currentEnabled.value }
            .distinctUntilChanged()
            .collect { (scrolling, _) ->
                if (!scrolling) settleOntoEnabled()
            }
    }

    Box(
        modifier = modifier.height(itemHeight * odd).clipToBounds(),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = ShamsiPickerDimens.WHEEL_HIGHLIGHT_HORIZONTAL_INSET_DP.dp)
                    .height(itemHeight)
                    .background(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = ShamsiPickerDimens.WHEEL_HIGHLIGHT_ALPHA),
                        shape = RoundedCornerShape(ShamsiPickerDimens.WHEEL_HIGHLIGHT_CORNER_RADIUS_DP.dp),
                    ),
        )

        LazyColumn(
            state = listState,
            flingBehavior = flingBehavior,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .drawWithContent {
                        drawContent()
                        val fade = size.height * (half / odd.toFloat())
                        drawRect(
                            brush =
                                Brush.verticalGradient(
                                    0f to fadeColor,
                                    (fade / size.height) to Color.Transparent,
                                    1f - (fade / size.height) to Color.Transparent,
                                    1f to fadeColor,
                                ),
                            blendMode = BlendMode.SrcOver,
                        )
                    },
        ) {
            items(listCount) { listIndex ->
                val raw = listIndex - half
                if (raw !in 0..<total) {
                    Box(modifier = Modifier.height(itemHeight))
                } else {
                    val logical = rawToLogical(raw)
                    val isEnabled = enabledRange.contains(logical)
                    val selected = isEnabled && logical == centeredLogical
                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(itemHeight)
                                .wheelTransform(listState, listIndex, half, dimAlpha),
                        contentAlignment = Alignment.Center,
                    ) {
                        ProvideTextStyle(
                            textStyle.copy(
                                color =
                                    when {
                                        !isEnabled -> disabledColor
                                        selected -> selectedColor
                                        else -> unselectedColor
                                    },
                                textAlign = TextAlign.Center,
                            ),
                        ) {
                            content(logical)
                        }
                    }
                }
            }
        }
    }
}

@Composable
public fun WheelPicker(
    itemCount: Int,
    initialIndex: Int,
    label: (Int) -> String,
    onSelectedIndexChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    infinite: Boolean = true,
    visibleCount: Int = ShamsiPickerDimens.WHEEL_DEFAULT_VISIBLE_COUNT,
    itemHeight: Dp = ShamsiPickerDimens.WHEEL_ITEM_HEIGHT_DP.dp,
    enabledRange: IntRange = 0 until itemCount,
    textStyle: TextStyle = MaterialTheme.typography.titleLarge,
    selectedColor: Color = MaterialTheme.colorScheme.onSurface,
    unselectedColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    disabledColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = ShamsiPickerDimens.DISABLED_CONTENT_ALPHA),
    fadeColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
    dimAlpha: Float = ShamsiPickerDimens.WHEEL_DIM_ALPHA,
) {
    WheelPicker(
        itemCount = itemCount,
        initialIndex = initialIndex,
        onSelectedIndexChange = onSelectedIndexChange,
        modifier = modifier,
        infinite = infinite,
        visibleCount = visibleCount,
        itemHeight = itemHeight,
        enabledRange = enabledRange,
        textStyle = textStyle,
        selectedColor = selectedColor,
        unselectedColor = unselectedColor,
        disabledColor = disabledColor,
        fadeColor = fadeColor,
        dimAlpha = dimAlpha,
    ) { index ->
        Text(text = label(index), maxLines = 1)
    }
}

private fun Modifier.wheelTransform(
    listState: LazyListState,
    index: Int,
    half: Int,
    dimAlpha: Float,
): Modifier =
    graphicsLayer {
        val info = listState.layoutInfo
        val item = info.visibleItemsInfo.firstOrNull { it.index == index } ?: return@graphicsLayer
        val viewportCenter = (info.viewportStartOffset + info.viewportEndOffset) / 2f
        val itemCenter = item.offset + item.size / 2f
        val rows = ((itemCenter - viewportCenter) / item.size).coerceIn(-half.toFloat(), half.toFloat())
        val t = if (half == 0) 0f else (abs(rows) / half).coerceIn(0f, 1f)

        alpha = lerp(1f, dimAlpha, t)
        val scale = lerp(1f, ShamsiPickerDimens.WHEEL_MIN_SCALE, t)
        scaleX = scale
        scaleY = scale
        rotationX = -rows * ShamsiPickerDimens.WHEEL_MAX_ROTATION_DEGREES
        cameraDistance = ShamsiPickerDimens.WHEEL_CAMERA_DISTANCE_MULTIPLIER * density
    }

@Composable
internal fun PickerDialogScaffold(
    title: String,
    confirmText: String,
    cancelText: String,
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    colors: ShamsiPickerColors,
    typography: ShamsiPickerTypography,
    modifier: Modifier = Modifier,
    header: (@Composable () -> Unit)? = null,
    body: @Composable () -> Unit,
) {
    val screenHeight = LocalWindowInfo.current.containerSize.height.dp

    Dialog(
        onDismissRequest = onCancel,
        properties =
            DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            modifier =
                modifier
                    .padding(ShamsiPickerDimens.DIALOG_OUTER_PADDING_DP.dp)
                    .width(ShamsiPickerDimens.DIALOG_WIDTH_DP.dp)
                    .heightIn(max = screenHeight - ShamsiPickerDimens.DIALOG_HEIGHT_INSET_DP.dp),
            shape = RoundedCornerShape(ShamsiPickerDimens.DIALOG_CORNER_RADIUS_DP.dp),
            color = colors.dialogContainerColor,
            tonalElevation = ShamsiPickerDimens.DIALOG_TONAL_ELEVATION_DP.dp,
        ) {
            Column(
                modifier =
                    Modifier.padding(
                        horizontal = ShamsiPickerDimens.DIALOG_CONTENT_PADDING_DP.dp,
                        vertical = ShamsiPickerDimens.DIALOG_CONTENT_PADDING_DP.dp,
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = title,
                    style = typography.titleStyle,
                    color = colors.titleColor,
                )
                if (header != null) {
                    Box(modifier = Modifier.padding(top = ShamsiPickerDimens.DIALOG_HEADER_SPACING_DP.dp)) { header() }
                }
                Box(
                    modifier =
                        Modifier
                            .padding(vertical = ShamsiPickerDimens.DIALOG_BODY_SPACING_DP.dp)
                            .weight(1f, fill = false)
                            .verticalScroll(rememberScrollState()),
                ) {
                    body()
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(ShamsiPickerDimens.DIALOG_BUTTON_SPACING_DP.dp),
                ) {
                    OutlinedButton(
                        onClick = onCancel,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(ShamsiPickerDimens.DIALOG_BUTTON_CORNER_RADIUS_DP.dp),
                        colors = colors.cancelButtonColors,
                    ) { Text(cancelText, style = typography.buttonTextStyle) }
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(ShamsiPickerDimens.DIALOG_BUTTON_CORNER_RADIUS_DP.dp),
                        colors = colors.confirmButtonColors,
                    ) { Text(confirmText, style = typography.buttonTextStyle) }
                }
            }
        }
    }
}
