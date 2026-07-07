package io.github.alirezajavan.shamsipicker.ui.theme

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class ShamsiPickerDimensTest {
    @Test
    fun `alpha constants are valid opacity fractions`() {
        val alphas =
            listOf(
                ShamsiPickerDimens.WHEEL_HIGHLIGHT_ALPHA,
                ShamsiPickerDimens.WHEEL_DIM_ALPHA,
                ShamsiPickerDimens.RANGE_WHEEL_DIM_ALPHA,
                ShamsiPickerDimens.DISABLED_CONTENT_ALPHA,
                ShamsiPickerDimens.RANGE_STRIP_ALPHA,
            )

        for (alpha in alphas) {
            assertThat(alpha).isAtLeast(0f)
            assertThat(alpha).isAtMost(1f)
        }
    }

    @Test
    fun `dp and count magnitudes are positive`() {
        val magnitudes =
            listOf(
                ShamsiPickerDimens.WHEEL_ITEM_HEIGHT_DP,
                ShamsiPickerDimens.WHEEL_DEFAULT_VISIBLE_COUNT,
                ShamsiPickerDimens.DIALOG_WIDTH_DP,
                ShamsiPickerDimens.COMPACT_WHEEL_WIDTH_DP,
                ShamsiPickerDimens.MONTH_WHEEL_WIDTH_DP,
                ShamsiPickerDimens.YEAR_WHEEL_WIDTH_DP,
                ShamsiPickerDimens.DAY_CELL_SIZE_DP,
                ShamsiPickerDimens.RANGE_WHEEL_VISIBLE_COUNT,
            )

        for (magnitude in magnitudes) {
            assertThat(magnitude).isGreaterThan(0)
        }
    }

    @Test
    fun `range day row height matches the wheel item height it was deduplicated from`() {
        assertThat(ShamsiPickerDimens.RANGE_DAY_ROW_HEIGHT_DP)
            .isEqualTo(ShamsiPickerDimens.WHEEL_ITEM_HEIGHT_DP)
    }
}
