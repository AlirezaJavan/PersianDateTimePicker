package io.github.alirezajavan.shamsipicker.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
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
    visibleCount: Int = 5,
    itemHeight: Dp = 44.dp,
    enabledRange: IntRange = 0 until itemCount,
    textStyle: TextStyle = MaterialTheme.typography.titleLarge,
    selectedColor: Color = MaterialTheme.colorScheme.onSurface,
    unselectedColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    disabledColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.22f),
    fadeColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
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

    val loops = if (infinite) 2_000 else 1
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
                listState.animateScrollToItem((currentRaw + (target - current)).coerceIn(0, total - 1))
            }
        }
        currentOnSelected.value(target)
    }

    LaunchedEffect(listState, itemCount, infinite) {
        snapshotFlow { listState.isScrollInProgress }
            .distinctUntilChanged()
            .collect { scrolling -> if (!scrolling) settleOntoEnabled() }
    }

    LaunchedEffect(enabledRange) {
        if (!listState.isScrollInProgress) settleOntoEnabled()
    }

    Box(
        modifier = modifier.height(itemHeight * odd).clipToBounds(),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp)
                    .height(itemHeight)
                    .background(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f),
                        shape = RoundedCornerShape(12.dp),
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
                                .wheelTransform(listState, listIndex, half),
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
    visibleCount: Int = 5,
    itemHeight: Dp = 44.dp,
    enabledRange: IntRange = 0 until itemCount,
    textStyle: TextStyle = MaterialTheme.typography.titleLarge,
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
    ) { index ->
        Text(text = label(index), maxLines = 1)
    }
}

private fun Modifier.wheelTransform(
    listState: LazyListState,
    index: Int,
    half: Int,
): Modifier =
    graphicsLayer {
        val info = listState.layoutInfo
        val item = info.visibleItemsInfo.firstOrNull { it.index == index } ?: return@graphicsLayer
        val viewportCenter = (info.viewportStartOffset + info.viewportEndOffset) / 2f
        val itemCenter = item.offset + item.size / 2f
        val rows = ((itemCenter - viewportCenter) / item.size).coerceIn(-half.toFloat(), half.toFloat())
        val t = (abs(rows) / half).coerceIn(0f, 1f)

        alpha = lerp(1f, 0.1f, t)
        val scale = lerp(1f, 0.72f, t)
        scaleX = scale
        scaleY = scale
        rotationX = -rows * 26f
        cameraDistance = 10f * density
    }

@Composable
internal fun PickerDialogScaffold(
    title: String,
    confirmText: String,
    cancelText: String,
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
    header: (@Composable () -> Unit)? = null,
    body: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onCancel,
        properties =
            DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            modifier =
                modifier
                    .padding(24.dp)
                    .width(340.dp),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            tonalElevation = 0.dp,
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                if (header != null) {
                    Box(modifier = Modifier.padding(top = 16.dp)) { header() }
                }
                Box(modifier = Modifier.padding(vertical = 16.dp)) { body() }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    OutlinedButton(
                        onClick = onCancel,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                    ) { Text(cancelText) }
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                    ) { Text(confirmText) }
                }
            }
        }
    }
}
