package com.example.csy2091as2.Functions

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import com.example.csy2091as2.LoginActivity
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import kotlin.random.Random


/**
 * this is a class that has some general purpose functions
 */
class Functions {

    companion object {
        /**
         * function to get the userinfo saved inside the sharedPreference file
         */
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

        /**
         * functions that removes sharedPreferences files
         */
        fun logout(context: Context?) {
            context?.getSharedPreferences("userinfo", Context.MODE_PRIVATE)?.edit()?.clear()?.commit()
            context?.getSharedPreferences("currentUser", Context.MODE_PRIVATE)?.edit()?.clear()?.apply()
            context?.startActivity(Intent(context, LoginActivity::class.java))
        }

        /**
         * converts a URI object to ByteArray
         */
        fun uriToByteArray(contentResolver: ContentResolver, uri: Uri): ByteArray? {
            var byteArray: ByteArray? = null
            try {
                contentResolver.openInputStream(uri).use { inputStream ->
                    byteArray = inputStream?.readBytes()
                }
            } catch (_: Exception){}
            return byteArray
        }


        /**
         * converts ByteArray to Bitmap
         */
        fun byteArrayToBitmap(byteArray: ByteArray): Bitmap? {
            return try {
                val inputStream = ByteArrayInputStream(byteArray)
                BitmapFactory.decodeStream(inputStream)
            } catch (e: Exception) {
                null
            }
        }

    }


    /**
     * Function to get the current date in specific format of yyyy-MM-dd
     */
    fun getCurrentDate(): String {
        val date = Date()
        val calendar = Calendar.getInstance()

        // fetching date
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        return formatDate("$year-$month-$day")
    }

    /**
     * converts date format to yyyy-MM-dd
     */
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

    /**
     * Generates a unique random String
     */
    fun generateRandomName(username: String): String {
        val currentDateTime = LocalDateTime.now()
        val formattedDateTime =
            currentDateTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
        val randomSuffix = Random.nextInt(100000)
        return "$formattedDateTime-$username-$randomSuffix.jpg"
    }

    /**
     * function that convets ByteArray to URI
     */
    fun byteArrayToUri(context: Context, byteArray: ByteArray): Uri? {
        val fileName = "temp_image_file"
        val file = File(context.cacheDir, fileName)

        try {
            FileOutputStream(file).use { outputStream ->
                outputStream.write(byteArray)
            }
            return FileProvider.getUriForFile(context, "com.example.csy2091as2.fileprovider", file)
        } catch (_: Exception) {
        }

        return null
    }
}