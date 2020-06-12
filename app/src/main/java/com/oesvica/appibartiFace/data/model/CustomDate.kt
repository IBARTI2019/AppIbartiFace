package com.oesvica.appibartiFace.data.model

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

data class CustomDate(var year: Int, var month: Int, var day: Int): Comparable<CustomDate> {
    override fun toString(): String {
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, month)
        cal.set(Calendar.DAY_OF_MONTH, day)
        return defaultDateFormat().format(cal.time)
    }

    override fun compareTo(other: CustomDate): Int {
        if (this.year > other.year) return 1
        if (this.year < other.year) return -1
        if (this.month > other.month) return 1
        if (this.month < other.month) return -1
        if (this.day > other.day) return 1
        if (this.day < other.day) return -1
        return 0
    }
}



fun String.toCustomDate(): CustomDate? {
    val cal = Calendar.getInstance()
    cal.time = defaultDateFormat().parse(this) ?: return null
    return CustomDate(
        cal.get(Calendar.YEAR),
        cal.get(Calendar.MONTH),
        cal.get(Calendar.DAY_OF_MONTH)
    )
}

fun defaultDate(): CustomDate? {
    return defaultDateFormat().format(Date()).toCustomDate()
}

fun currentDay(): CustomDate {
    val cal = Calendar.getInstance()
    cal.time = Date()
    return CustomDate(
        cal.get(Calendar.YEAR),
        cal.get(Calendar.MONTH),
        cal.get(Calendar.DAY_OF_MONTH)
    )
}

@SuppressLint("SimpleDateFormat")
private fun defaultDateFormat() = SimpleDateFormat("yyyy-MM-dd")