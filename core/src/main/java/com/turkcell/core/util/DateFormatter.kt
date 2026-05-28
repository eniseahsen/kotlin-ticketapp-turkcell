package com.turkcell.core.util

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object DataFormatter{
    private val inputFormatter = DateTimeFormatter.ISO_DATE_TIME
    private val outputFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy HH:mm", Locale("tr"))

    fun format(isoDate: String): String = try {
        val zdt = ZonedDateTime.parse(isoDate, inputFormatter)
        zdt.format(outputFormatter)
    }
    catch (e: Exception){
        isoDate
    }
}

