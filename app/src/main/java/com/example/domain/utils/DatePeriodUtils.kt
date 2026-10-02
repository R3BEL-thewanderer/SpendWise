package com.example.domain.utils

import com.example.domain.model.CustomDateRange
import com.example.domain.model.DatePeriod
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField
import java.util.Locale

object DatePeriodUtils {

    private val dtfFullDayMonthYear: DateTimeFormatter = DateTimeFormatterBuilder()
        .parseCaseInsensitive()
        .appendPattern("d MMM yyyy")
        .toFormatter(Locale.ENGLISH)

    private val dtfIso: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    private val dtfMonthYear: DateTimeFormatter = DateTimeFormatterBuilder()
        .parseCaseInsensitive()
        .appendPattern("MMMM yyyy")
        .toFormatter(Locale.ENGLISH)

    private val dtfShortMonthYear: DateTimeFormatter = DateTimeFormatterBuilder()
        .parseCaseInsensitive()
        .appendPattern("MMM yyyy")
        .toFormatter(Locale.ENGLISH)

    /**
     * Parses a date string commonly found in SpendWise into a LocalDate.
     * Supports "Today", "Yesterday", "12 Feb 2025", "2025-02-12", etc.
     */
    fun parseDate(dateStr: String, now: LocalDate = LocalDate.now()): LocalDate? {
        val trimmed = dateStr.trim()
        if (trimmed.isEmpty()) return null

        if (trimmed.equals("Today", ignoreCase = true) || trimmed.equals("Now", ignoreCase = true)) {
            return now
        }
        if (trimmed.equals("Yesterday", ignoreCase = true)) {
            return now.minusDays(1)
        }

        // Try "d MMM yyyy" e.g. "12 Feb 2025" or "1 Mar 2026"
        try {
            return LocalDate.parse(trimmed, dtfFullDayMonthYear)
        } catch (_: Exception) {}

        // Try ISO "yyyy-MM-dd" e.g. "2025-02-12"
        try {
            return LocalDate.parse(trimmed, dtfIso)
        } catch (_: Exception) {}

        // Try standard fallback variations like "dd-MM-yyyy" or "dd/MM/yyyy"
        try {
            val parts = trimmed.split('-', '/')
            if (parts.size == 3) {
                val d = parts[0].toIntOrNull()
                val m = parts[1].toIntOrNull()
                val y = parts[2].toIntOrNull()
                if (d != null && m != null && y != null) {
                    val fullYear = if (y < 100) 2000 + y else y
                    return LocalDate.of(fullYear, m, d)
                }
            }
        } catch (_: Exception) {}

        // Try "d MMM" assuming current year
        try {
            val formatterWithoutYear = DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern("d MMM")
                .parseDefaulting(ChronoField.YEAR, now.year.toLong())
                .toFormatter(Locale.ENGLISH)
            return LocalDate.parse(trimmed, formatterWithoutYear)
        } catch (_: Exception) {}

        return null
    }

    /**
     * Formats a LocalDate into standard SpendWise date string "d MMM yyyy"
     */
    fun formatDate(date: LocalDate): String {
        return date.format(dtfFullDayMonthYear)
    }

    /**
     * Parses a budget month string like "March 2025" or "Mar 2025" into a YearMonth.
     */
    fun parseBudgetMonth(monthStr: String): YearMonth? {
        val trimmed = monthStr.trim()
        if (trimmed.isEmpty()) return null

        try {
            return YearMonth.parse(trimmed, dtfMonthYear)
        } catch (_: Exception) {}

        try {
            return YearMonth.parse(trimmed, dtfShortMonthYear)
        } catch (_: Exception) {}

        // Fallback: check if single month name like "March"
        for (month in java.time.Month.values()) {
            if (trimmed.startsWith(month.name, ignoreCase = true) ||
                trimmed.startsWith(month.name.take(3), ignoreCase = true)) {
                // try to extract year
                val yearPart = trimmed.filter { it.isDigit() }
                val year = yearPart.toIntOrNull() ?: LocalDate.now().year
                return YearMonth.of(year, month)
            }
        }
        return null
    }

    /**
     * Checks whether a transaction date string belongs to the given DatePeriod.
     */
    fun isDateInPeriod(
        dateStr: String,
        period: DatePeriod,
        now: LocalDate = LocalDate.now(),
        customRange: CustomDateRange? = null
    ): Boolean {
        if (period == DatePeriod.ALL_TIME) return true

        val txDate = parseDate(dateStr, now) ?: return false

        return when (period) {
            DatePeriod.ALL_TIME -> true

            DatePeriod.CURRENT_WEEK -> {
                // Week starts on Monday and ends on Sunday
                val startOfWeek = now.with(DayOfWeek.MONDAY)
                val endOfWeek = now.with(DayOfWeek.SUNDAY)
                !txDate.isBefore(startOfWeek) && !txDate.isAfter(endOfWeek)
            }

            DatePeriod.CURRENT_MONTH -> {
                txDate.year == now.year && txDate.month == now.month
            }

            DatePeriod.PREVIOUS_MONTH -> {
                val prev = now.minusMonths(1)
                txDate.year == prev.year && txDate.month == prev.month
            }

            DatePeriod.CUSTOM -> {
                if (customRange == null) return true
                val start = parseDate(customRange.startDate, now)
                val end = parseDate(customRange.endDate, now)
                val afterStart = start == null || !txDate.isBefore(start)
                val beforeEnd = end == null || !txDate.isAfter(end)
                afterStart && beforeEnd
            }
        }
    }

    /**
     * Checks if a transaction date matches a budget month.
     * If budget has month "March 2025", checks if transaction falls into March 2025.
     */
    fun isDateInBudgetMonth(dateStr: String, budgetMonth: String, now: LocalDate = LocalDate.now()): Boolean {
        val ym = parseBudgetMonth(budgetMonth) ?: return true // If no valid month, match all
        val txDate = parseDate(dateStr, now) ?: return false
        return txDate.year == ym.year && txDate.month == ym.month
    }
}
