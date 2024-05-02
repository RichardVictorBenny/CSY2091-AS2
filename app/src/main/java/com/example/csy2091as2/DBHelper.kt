package com.example.csy2091as2

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.csy2091as2.Functions.Functions

class DBHelper (context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION){
    companion object{
        const val DATABASE_VERSION = 7
        const val DATABASE_NAME = "campusconnect"

        val tblAuthentication = "tblAuthentication"
        val colUserID = "UserID"
        val colAuthPassword = "AuthPassword"
        val colAuthUserName = "AuthUserName"
        val colAuthType = "AuthType"

        val tblUsers = "tblUsers"
        val colUserFirstName = "UserFirstName"
        val colUserMiddleName = "UserMiddleName"
        val colUserLastName = "UserLastName"
        val colUserEmail = "UserEmail"
        val colUserDOB = "UserDOB"
        val colUserDateCreated = "UserDateCreated"
        val colUserDateUpdated = "UserDateUpdated"

    }

    override fun onCreate(db: SQLiteDatabase?) {
        val qryAuthentication = "CREATE TABLE " + tblAuthentication + "("+ colUserID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+ colAuthUserName+" TEXT, "+ colAuthPassword + " TEXT, $colAuthType TEXT)"
        val qryUser = "CREATE TABLE " + tblUsers + "("+ colUserID+" INTEGER PRIMARY KEY AUTOINCREMENT,  "+ colAuthUserName + " TEXT, "+ colUserFirstName+" TEXT, "+
                colUserMiddleName+" TEXT, "+ colUserLastName+" TEXT, "+ colUserDOB+" TEXT, "+ colUserEmail+" TEXT, "+ colUserDateCreated+" TEXT, "+ colUserDateUpdated+" TEXT)"

//        if (db != null){
//            db.execSQL(qryUser)
//            db.execSQL(qryAuthentication)
//        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
//        val qryUser = "DROP TABLE IF EXISTS " + tblUsers
        val qryAuthentication = "ALTER TABLE $tblAuthentication ADD COLUMN $colAuthType TEXT DEFAULT 'student'"
        if(db != null){
            db.execSQL(qryAuthentication)
//            db.execSQL(qryUser)

        }
    }

    fun addUser(
        studentID: String,
        firstName: String?,
        middleName: String?,
        lastName: String?,
        email: String?,
        dob: String?,
        password: String
        ): Array<Long>{

        // gets the current date
        val functions = Functions()
        val currentDate = functions.getCurrentDate()


        val db = this.writableDatabase
        val userValues = ContentValues()
        userValues.put(colUserFirstName, firstName)
        userValues.put(colUserMiddleName, middleName)
        userValues.put(colUserLastName, lastName)
        userValues.put(colUserEmail, email)
        userValues.put(colUserDOB, dob)
        userValues.put(colUserDateCreated, currentDate)
        userValues.put(colUserDateUpdated, currentDate)
        userValues.put(colAuthUserName, studentID)

        val userCred = ContentValues()
        userCred.put(colAuthUserName, studentID)
        userCred.put(colAuthPassword, password)


        val statusUser =  db.insert(tblUsers, null, userValues)
        val statusCred = db.insert(tblAuthentication, null, userCred)
        return arrayOf(statusUser,statusCred)
//        return arrayOf(statusCred)
    }

    fun authenticate(username: String, password: String): String?{

        val db: SQLiteDatabase = this.readableDatabase
        val query = "SELECT * FROM "+ tblAuthentication +" WHERE "+ colAuthUserName + " = ? AND "+ colAuthPassword+" = ?"
        val cursor = db.rawQuery(query, arrayOf(username, password))
        val result = if(cursor.moveToNext()) cursor.getString(cursor.getColumnIndexOrThrow(colAuthType)) else null
        cursor.close()

        return result
    }

    fun userCheck(username: String): Boolean{
        val db = this.readableDatabase
        val query = "SELECT * FROM $tblAuthentication WHERE $colAuthUserName = ?"

        val cursor = db.rawQuery(query, arrayOf(username))
        val result = cursor.count>0
        cursor.close()

        return result


    }
}