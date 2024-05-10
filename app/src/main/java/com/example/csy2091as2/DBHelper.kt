package com.example.csy2091as2

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.csy2091as2.Functions.Functions
import com.example.csy2091as2.Functions.Post
import java.time.LocalDateTime

class DBHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        const val DATABASE_VERSION = 12
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

        val tblPost = "tblPost"
        val colPostID = "PostID"
        val colPostUsername = "PostUsername"
        val colPostTime = "PostTime"
        val colPostUpdateTime = "PostUpdateTime"
        val colPostDesc = "PostDesc"
        val colPostImage = "PostImage"
        val colPostApproval = "PostApproval"


    }

    override fun onCreate(db: SQLiteDatabase?) {
        val qryAuthentication =
            "CREATE TABLE " + tblAuthentication + "(" + colUserID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + colAuthUserName + " TEXT, " + colAuthPassword + " TEXT, $colAuthType TEXT)"
        val qryUser =
            "CREATE TABLE " + tblUsers + "(" + colUserID + " INTEGER PRIMARY KEY AUTOINCREMENT,  " + colAuthUserName + " TEXT, " + colUserFirstName + " TEXT, " +
                    colUserMiddleName + " TEXT, " + colUserLastName + " TEXT, " + colUserDOB + " TEXT, " + colUserEmail + " TEXT, " + colUserDateCreated + " TEXT, " + colUserDateUpdated + " TEXT)"
        val qryPost =
            "CREATE TABLE $tblPost ($colPostID INTEGER PRIMARY KEY AUTOINCREMENT, $colPostUsername TEXT , $colPostTime TEXT NOT NULL, $colPostUpdateTime TEXT NOT NULL, $colPostDesc TEXT, $colPostImage TEXT, $colPostApproval INTEGER)"

//        if (db != null){
//            db.execSQL(qryUser)
//            db.execSQL(qryAuthentication)
//            db.execSQL(qryPost)
//        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val qry = "DROP TABLE $tblPost"
        val qryPost =
            "CREATE TABLE $tblPost ($colPostID INTEGER PRIMARY KEY AUTOINCREMENT, $colPostUsername TEXT , $colPostTime TEXT NOT NULL, $colPostUpdateTime TEXT NOT NULL, $colPostDesc TEXT, $colPostImage TEXT, $colPostApproval INTEGER)"

        if (db != null) {
            db.execSQL(qry)
            db.execSQL(qryPost)

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
    ): Array<Long> {

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


        val statusUser = db.insert(tblUsers, null, userValues)
        val statusCred = db.insert(tblAuthentication, null, userCred)
        return arrayOf(statusUser, statusCred)
//        return arrayOf(statusCred)
    }

    fun authenticate(username: String, password: String): String? {

        val db: SQLiteDatabase = this.readableDatabase
        val query =
            "SELECT * FROM " + tblAuthentication + " WHERE " + colAuthUserName + " = ? AND " + colAuthPassword + " = ?"
        val cursor = db.rawQuery(query, arrayOf(username, password))
        val result =
            if (cursor.moveToNext()) cursor.getString(cursor.getColumnIndexOrThrow(colAuthType)) else null
        cursor.close()

        return result
    }

    fun userCheck(username: String): Boolean {
        val db = this.readableDatabase
        val query = "SELECT * FROM $tblAuthentication WHERE $colAuthUserName = ?"

        val cursor = db.rawQuery(query, arrayOf(username))
        val result = cursor.count > 0
        cursor.close()

        return result


    }

    fun addPost(username: String, desc: String, imagePath: String?): Long {
        val dateTime = LocalDateTime.now().toString()


        val db = this.writableDatabase
        val postValues = ContentValues()
        postValues.put(colPostUsername, username)
        postValues.put(colPostTime, dateTime)
        postValues.put(colPostUpdateTime, dateTime)
        postValues.put(colPostDesc, desc)
        postValues.put(colPostImage, imagePath)
        postValues.put(colPostApproval, 1)

        return db.insert(tblPost, null, postValues)


    }

    fun getPost10(): MutableList<Post> {
        val query =
            "SELECT * FROM $tblPost WHERE $colPostApproval = 1 ORDER BY $colPostID DESC LIMIT 10"
        return getPost(query, null)

    }

    fun getPostUser(username: String): MutableList<Post>{
        val query = "SELECT * FROM $tblPost WHERE $colPostApproval = 1 AND $colPostUsername = ? ORDER BY $colPostID DESC"
        val args = arrayOf(username)
        return getPost(query, args)
    }

    fun getPostSingle(postId: Int): MutableList<Post>{
        val query = "SELECT * FROM $tblPost WHERE $colPostID = ?"
        val args = arrayOf(postId.toString())
        return getPost(query, args)
    }

    fun deletePost(postId: Int): Int {
        val db = this.writableDatabase
        val where = "$colPostID = ?"
        val args = arrayOf(postId.toString())
        return db.delete(tblPost, where, args)
    }

    private fun getPost(query: String, args: Array<String>?): MutableList<Post>{
        var postList = mutableListOf<Post>()
        val db = this.readableDatabase
        val cursor = db.rawQuery(query, args)
        if (cursor.moveToFirst()) {
            do {
                val postId: Int = cursor.getInt(cursor.getColumnIndexOrThrow(colPostID))
                val username = cursor.getString(cursor.getColumnIndexOrThrow(colPostUsername))
                val imgPath = cursor.getString(cursor.getColumnIndexOrThrow(colPostImage))
                val txtDesp = cursor.getString(cursor.getColumnIndexOrThrow(colPostDesc))
                postList.add(Post(postId, username, imgPath, txtDesp))

            } while (cursor.moveToNext())
        }

        return postList
    }
}