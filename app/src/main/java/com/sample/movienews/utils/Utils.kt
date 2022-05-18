package com.sample.movienews.utils

import java.text.NumberFormat
import java.util.*
import java.util.concurrent.TimeUnit

object Utils {

    fun getDefaultLanguageCode(): String = Locale.getDefault().isO3Language

    fun fromMinutesToHHmm(minutes: Int): String {

        val hours = TimeUnit.MINUTES.toHours(minutes.toLong())
        val remainMinutes = minutes - TimeUnit.HOURS.toMinutes(hours)

        if (hours == 0L && minutes == 0) {
            return " - "
        } else if (hours == 0L) {
            return String.format("%02dmn", remainMinutes)
        }

        return String.format("%02dh %02dmn", hours, remainMinutes)
    }

    fun getFormattedPrice(price: Double): String =
        NumberFormat.getCurrencyInstance().format(price)
}