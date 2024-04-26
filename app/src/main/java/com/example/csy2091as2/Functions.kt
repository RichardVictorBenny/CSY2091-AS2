package com.example.csy2091as2

import java.util.Calendar

class Functions {

    fun getCurrentDate(): String {
        // Get a Calendar instance
        val calendar = Calendar.getInstance()

        // Get the current year, month, and day
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1 // Month is zero-based, so add 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Return the current date as a formatted string
        return "$year-$month-$day"
    }
}