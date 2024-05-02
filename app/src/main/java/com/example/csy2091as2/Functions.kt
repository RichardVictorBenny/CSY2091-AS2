package com.example.csy2091as2

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date

class Functions {

    fun getCurrentDate(): String {
        val date = Date()
        val calendar = Calendar.getInstance()

        // fetching date
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1 // Month is zero-based, so add 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        return formatDate("$year-$month-$day")
    }

    fun formatDate(dateString: String): String {
        // possible formats that can be used
        val possibleFormats = arrayOf("yyyy-M-d", "yyyy-M-dd", "yyyy-MM-d", "d/M/yyyy", "dd/MM/yyyy", "dd/M/yyyy", "d/MM/yyyy")

        for (format in possibleFormats) {
            try {
                val parsedDate = LocalDate.parse(dateString, DateTimeFormatter.ofPattern(format))
                return parsedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            } catch (e: Exception) {
                continue
            }
        }

        // original if non matches
        return dateString
    }
}