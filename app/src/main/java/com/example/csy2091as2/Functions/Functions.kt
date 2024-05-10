package com.example.csy2091as2.Functions

import android.content.Context
import android.util.Log
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import kotlin.random.Random

class Functions {

    companion object {
        fun getUserinfo(context: Context): Map<String, String> {
            var userType: String = ""
            var userName: String = ""
            var map = mapOf<String, String>()
            val currentUser = context.getSharedPreferences("currentUser", Context.MODE_PRIVATE)
            val userInfo = context.getSharedPreferences("userinfo", Context.MODE_PRIVATE)

            if (userInfo.contains("usertype") && currentUser.contains("username")) {

                userType = userInfo.getString("usertype", null).toString()
                userName = userInfo.getString("username", null).toString()

            } else
                if (currentUser.contains("username") && currentUser.contains("usertype")) {
                    userType = currentUser?.getString("usertype", null).toString()
                    userName = currentUser?.getString("username", null).toString()
                }

            map = mapOf(
                "username" to userName,
                "usertype" to userType
            )
            return map
        }
    }

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
        val possibleFormats = arrayOf(
            "yyyy-M-d",
            "yyyy-M-dd",
            "yyyy-MM-d",
            "d/M/yyyy",
            "dd/MM/yyyy",
            "dd/M/yyyy",
            "d/MM/yyyy"
        )

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

    fun generateRandomName(username: String): String {
        val currentDateTime = LocalDateTime.now()
        val formattedDateTime =
            currentDateTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
        val randomSuffix = Random.nextInt(100000) // Generate a random number between 0 and 999
        return "$formattedDateTime-$username-$randomSuffix.jpg"
    }
}