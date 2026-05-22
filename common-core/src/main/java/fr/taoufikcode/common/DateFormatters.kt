package fr.taoufikcode.common

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateFormatters {
    const val API_DATE_FORMAT = "yyyy-MM-dd"

    const val DISPLAY_DATE_FORMAT = "dd MMM yyyy"

    fun String.parseApiDate(): LocalDate = LocalDate.parse(this)

    fun LocalDate.toDisplayFormat(): String =
        this.format(DateTimeFormatter.ofPattern(DISPLAY_DATE_FORMAT, Locale.getDefault()))
}
