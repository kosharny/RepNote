package com.example.repnote.ui.util

import com.example.repnote.R

fun weekdayNameRes(day: Int): Int = when (day) {
    1 -> R.string.day_monday
    2 -> R.string.day_tuesday
    3 -> R.string.day_wednesday
    4 -> R.string.day_thursday
    5 -> R.string.day_friday
    6 -> R.string.day_saturday
    else -> R.string.day_sunday
}

fun weekdayShortRes(day: Int): Int = when (day) {
    1 -> R.string.day_mon
    2 -> R.string.day_tue
    3 -> R.string.day_wed
    4 -> R.string.day_thu
    5 -> R.string.day_fri
    6 -> R.string.day_sat
    else -> R.string.day_sun
}
