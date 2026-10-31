package com.example.repnote.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import java.text.DateFormat
import java.text.NumberFormat
import java.util.Date
import java.util.TimeZone

@Composable
fun formatSessionDate(epochMillis: Long, timeZoneId: String): String {
    val locale = LocalConfiguration.current.locales[0]
    return DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, locale).apply {
        timeZone = TimeZone.getTimeZone(timeZoneId)
    }.format(Date(epochMillis))
}

@Composable
fun formatWeight(weight: Double): String = NumberFormat.getNumberInstance(LocalConfiguration.current.locales[0]).apply {
    maximumFractionDigits = 2
}.format(weight)
