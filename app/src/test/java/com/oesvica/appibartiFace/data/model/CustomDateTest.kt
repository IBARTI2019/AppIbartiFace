package com.oesvica.appibartiFace.data.model

import org.junit.Assert.*
import org.junit.Test
import java.util.*

class CustomDateTest {

    @Test
    fun `should convert string dates into CustomDate`(){
        fun testDate(year: Int, month: Int, day: Int) {
            // Given
            val strDate = "$year-$month-$day"

            // When
            val customDate = strDate.toCustomDate()

            // Then
            assertNotNull(customDate)
            assertEquals(year, customDate?.year)
            assertEquals(month-1, customDate?.month)
            assertEquals(day, customDate?.day)
        }
        testDate(2020, 8, 16)
        testDate(1985, 4, 15)
        testDate(1870, 1, 1)
        testDate(1470, 12, 31)
        testDate(2010, 11, 15)
        testDate(1998, 10, 5)
    }

    @Test
    fun `should return currentDay`(){
        // Given
        val cal = Calendar.getInstance()
        // When
        val currentDay = currentDay()
        // Then
        assertEquals(cal.get(Calendar.YEAR), currentDay.year)
        assertEquals(cal.get(Calendar.MONTH), currentDay.month)
        assertEquals(cal.get(Calendar.DAY_OF_MONTH), currentDay.day)
    }

}