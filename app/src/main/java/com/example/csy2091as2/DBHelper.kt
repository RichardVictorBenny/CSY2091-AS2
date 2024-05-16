package com.example.csy2091as2

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.csy2091as2.Functions.Comment
import com.example.csy2091as2.Functions.Functions
import com.example.csy2091as2.Functions.Post
import com.example.csy2091as2.Functions.User
import java.time.LocalDateTime

class DBHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        const val DATABASE_VERSION = 13
//        const val DATABASE_VERSION = 1
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

        val tblComment = "tblComments"
        val colCommentID = "CommentID"
        val colCommentAuthor = "CommentAuthor"
        val colCommentContent = "CommentContent"
        val colCommentPostId = "CommentPostId"
        val colCommentParentID = "CommentParent"
        val colCommentTime = "CommentTime"


    }

    override fun onCreate(db: SQLiteDatabase?) {
        val qryAuthentication =
            "CREATE TABLE " + tblAuthentication + "(" + colUserID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + colAuthUserName + " TEXT, " + colAuthPassword + " TEXT, $colAuthType TEXT DEFAULT student)"
        val qryUser =
            "CREATE TABLE " + tblUsers + "(" + colUserID + " INTEGER PRIMARY KEY AUTOINCREMENT,  " + colAuthUserName + " TEXT, " + colUserFirstName + " TEXT, " +
                    colUserMiddleName + " TEXT, " + colUserLastName + " TEXT, " + colUserDOB + " TEXT, " + colUserEmail + " TEXT, " + colUserDateCreated + " TEXT, " + colUserDateUpdated + " TEXT)"
        val qryPost =
            "CREATE TABLE $tblPost ($colPostID INTEGER PRIMARY KEY AUTOINCREMENT, $colPostUsername TEXT , $colPostTime TEXT NOT NULL, $colPostUpdateTime TEXT NOT NULL, $colPostDesc TEXT, $colPostImage TEXT, $colPostApproval INTEGER)"

        val qryComment =
            "CREATE TABLE $tblComment($colCommentID INTEGER PRIMARY KEY AUTOINCREMENT, $colCommentAuthor TEXT, $colCommentContent TEXT, $colCommentPostId INTEGER, $colCommentParentID INTEGER, $colCommentTime TEXT )"

        if (db != null){
            db.execSQL(qryUser)
            db.execSQL(qryAuthentication)
            db.execSQL(qryPost)
            db.execSQL(qryComment)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
//        val qryPost =
//            "CREATE TABLE $tblComment($colCommentID INTEGER PRIMARY KEY AUTOINCREMENT, $colCommentAuthor TEXT, $colCommentContent TEXT, $colCommentPostId INTEGER, $colCommentParentID INTEGER, $colCommentTime TEXT )"
//
//        if (db != null) {
//            db.execSQL(qryPost)
//
//        }

        onCreate(db)
    }

    fun addUser(
        studentID: String,
        firstName: String,
        middleName: String,
        lastName: String,
        email: String,
        dob: String,
        password: String,
        usertype: String?
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
        userCred.put(colAuthType, usertype)


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

    fun fetchUsername(email: String): String?{
        val db = this.readableDatabase
        val query = "SELECT $colAuthUserName FROM $tblUsers WHERE $colUserEmail = ?"
        val cursor = db.rawQuery(query, arrayOf(email))
        if(cursor.moveToFirst()){
            return cursor.getString(cursor.getColumnIndexOrThrow(colAuthUserName))
        } else{
            return null
        }
    }

    fun fetchUser(username: String): User{
        val db = this.readableDatabase
        val query = "SELECT * FROM $tblUsers WHERE $colAuthUserName = ?"
        val cursor = db.rawQuery(query, arrayOf(username))
        val auth = db.rawQuery("SELECT * FROM $tblAuthentication WHERE $colAuthUserName = ?", arrayOf(username))
        cursor.moveToFirst()
        auth.moveToFirst()
        return User(
            cursor.getString(cursor.getColumnIndexOrThrow(colAuthUserName)),
            cursor.getString(cursor.getColumnIndexOrThrow(colUserFirstName)),
            cursor.getString(cursor.getColumnIndexOrThrow(colUserMiddleName)),
            cursor.getString(cursor.getColumnIndexOrThrow(colUserLastName)),
            cursor.getString(cursor.getColumnIndexOrThrow(colUserEmail)),
            cursor.getString(cursor.getColumnIndexOrThrow(colUserDOB)),
            auth.getString(auth.getColumnIndexOrThrow(colAuthType))
        )
    }

    fun userCheck(username: String): Boolean {
        val db = this.readableDatabase
        val query = "SELECT * FROM $tblAuthentication WHERE $colAuthUserName = ?"

        val cursor = db.rawQuery(query, arrayOf(username))
        val result = cursor.count > 0
        cursor.close()

        return result
    }
    fun updatePassword(username: String, newPassword: String): Boolean{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(colAuthPassword, newPassword)

        val result = db.update(tblAuthentication, contentValues, "$colAuthUserName = ?", arrayOf(username) )
        return (result>0)
    }
    fun deleteUser(username: String): Boolean{
        val db = this.writableDatabase
        val result1 = db.delete(tblUsers, "$colAuthUserName = ?", arrayOf(username))
        val result2 = db.delete(tblAuthentication,"$colAuthUserName = ?", arrayOf(username) )

        return (result1+result2)>0
    }



    fun emailCheck(email: String): Boolean{
        val db = this.readableDatabase
        val query = "SELECT * FROM $tblUsers WHERE $colUserEmail = ?"
        val cursor = db.rawQuery(query, arrayOf(email))
        cursor.moveToFirst()
        Log.d("TAG", "emailCheck: ${cursor.getString(cursor.getColumnIndexOrThrow(colUserEmail))}")
        val result = cursor.count > 0
        cursor.close()
        return result

    }

    fun savePost(postId: Int, username: String, desc: String, imagePath: String?): Long {

        return if (postId == 0) {
            addPost(username, desc, imagePath)
        } else {
            updatePost(postId, desc, imagePath).toLong()
        }

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

    fun updatePost(postId: Int, desc: String, imagePath: String?): Int {
        val dateTime = LocalDateTime.now().toString()


        val db = this.writableDatabase
        val postValues = ContentValues()
        postValues.put(colPostUpdateTime, dateTime)
        postValues.put(colPostDesc, desc)
        postValues.put(colPostImage, imagePath)
        postValues.put(colPostApproval, 1)

        val where = "$colPostID = ?"
        val args = arrayOf(postId.toString())

        return db.update(tblPost, postValues, where, args)


    }

    fun getPost10(): MutableList<Post> {
        val query =
            "SELECT * FROM $tblPost WHERE $colPostApproval = 1 ORDER BY $colPostID DESC LIMIT 10"
        return getPost(query, null)

    }

    fun getPostUser(username: String): MutableList<Post> {
        val query =
            "SELECT * FROM $tblPost WHERE $colPostApproval = 1 AND $colPostUsername = ? ORDER BY $colPostID DESC"
        val args = arrayOf(username)
        return getPost(query, args)
    }

    fun getPostSingle(postId: Int): MutableList<Post> {
        val query = "SELECT * FROM $tblPost WHERE $colPostID = ?"
        val args = arrayOf(postId.toString())
        return getPost(query, args)
    }

    fun deletePost(postId: Int): Int {
        val db = this.writableDatabase
        val where = "$colPostID = ?"
        val args = arrayOf(postId.toString())
        this.deleteComments(postId)
        return db.delete(tblPost, where, args)
    }

    private fun deleteComments(postId: Int) {
        val db = this.writableDatabase
        val where = "$colCommentPostId = ?"
        val args = arrayOf(postId.toString())
        db.delete(tblComment, where, args)
    }

    private fun getPost(query: String, args: Array<String>?): MutableList<Post> {
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


    fun addComment(author: String, content: String, postId: Int, parent: Int?) : Long{
        val dateTime = LocalDateTime.now().toString()
        val db = this.writableDatabase
        val commentValues = ContentValues()

        commentValues.put(colCommentAuthor, author)
        commentValues.put(colCommentContent, content)
        commentValues.put(colCommentPostId, postId)
        commentValues.put(colCommentParentID, parent)
        commentValues.put(colCommentTime, dateTime)

        return db.insert(tblComment, null, commentValues)

    }

    fun getComment10(postId: Int): MutableList<Comment>{
        var commentList = mutableListOf<Comment>()
        val db = this.readableDatabase
        val query = "SELECT * FROM $tblComment WHERE $colCommentPostId = ? ORDER BY $colCommentID DESC LIMIT 10"
        val cursor = db.rawQuery(query, arrayOf(postId.toString()))
        if(cursor.moveToFirst()){
            do {
                val commentId = cursor.getInt(cursor.getColumnIndexOrThrow(colCommentID))
                val author = cursor.getString(cursor.getColumnIndexOrThrow(colCommentAuthor))
                val commentContent = cursor.getString(cursor.getColumnIndexOrThrow(colCommentContent))

                val comment = Comment(commentId, author, commentContent)
                commentList.add(comment)
            } while (cursor.moveToNext())
        }

        return commentList

    }

}