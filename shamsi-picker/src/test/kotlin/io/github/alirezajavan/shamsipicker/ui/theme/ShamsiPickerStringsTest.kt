package io.github.alirezajavan.shamsipicker.ui.theme

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class ShamsiPickerStringsTest {
    @Test
    fun `date picker strings copy overrides only title and button text`() {
        val defaults =
            ShamsiDatePickerStrings(
                title = "انتخاب تاریخ",
                confirmText = "تایید",
                cancelText = "انصراف",
                styleWheelLabel = "چرخی",
                styleCalendarLabel = "تقویمی",
                prevMonthDescription = "ماه قبل",
                nextMonthDescription = "ماه بعد",
                prevYearDescription = "سال قبل",
                nextYearDescription = "سال بعد",
            )

        val customized =
            defaults.copy(
                title = "Pick a day",
                confirmText = "Done",
                cancelText = "Nevermind",
            )

        assertThat(customized.title).isEqualTo("Pick a day")
        assertThat(customized.confirmText).isEqualTo("Done")
        assertThat(customized.cancelText).isEqualTo("Nevermind")
        // Unrelated fields must be untouched by the override.
        assertThat(customized.styleWheelLabel).isEqualTo(defaults.styleWheelLabel)
        assertThat(customized.prevMonthDescription).isEqualTo(defaults.prevMonthDescription)
    }

    @Test
    fun `date range picker strings carry an independent selectToHint field`() {
        val strings =
            ShamsiDateRangePickerStrings(
                title = "Pick a range",
                confirmText = "Done",
                cancelText = "Cancel",
                styleWheelLabel = "Wheel",
                styleCalendarLabel = "Calendar",
                prevMonthDescription = "Previous month",
                nextMonthDescription = "Next month",
                prevYearDescription = "Previous year",
                nextYearDescription = "Next year",
                selectToHint = "Pick the end date",
            )

        assertThat(strings.selectToHint).isEqualTo("Pick the end date")
    }
}
