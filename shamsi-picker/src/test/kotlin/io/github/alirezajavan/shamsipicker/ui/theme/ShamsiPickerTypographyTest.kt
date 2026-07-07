package io.github.alirezajavan.shamsipicker.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class ShamsiPickerTypographyTest {
    private val base =
        ShamsiPickerTypography(
            titleStyle = TextStyle.Default,
            wheelItemStyle = TextStyle.Default,
            compactWheelItemStyle = TextStyle.Default,
            dayCellStyle = TextStyle.Default,
            weekdayLabelStyle = TextStyle.Default,
            navHeaderStyle = TextStyle.Default,
            segmentLabelStyle = TextStyle.Default,
            separatorStyle = TextStyle.Default,
            buttonTextStyle = TextStyle.Default,
        )

    @Test
    fun `copy overrides only the requested style, custom font applies via fontFamily`() {
        val brandFont = FontFamily.Serif
        val customized = base.copy(titleStyle = base.titleStyle.copy(fontFamily = brandFont))

        assertThat(customized.titleStyle.fontFamily).isEqualTo(brandFont)
        assertThat(customized.wheelItemStyle).isEqualTo(base.wheelItemStyle)
        assertThat(customized.dayCellStyle).isEqualTo(base.dayCellStyle)
    }

    @Test
    fun `two instances with the same field values are equal`() {
        val other = base.copy()

        assertThat(other).isEqualTo(base)
    }
}
