package com.example.csy2091as2

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.CursorIndexOutOfBoundsException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import com.example.csy2091as2.Functions.Comment
import com.example.csy2091as2.Functions.Functions
import com.example.csy2091as2.Functions.Post
import com.example.csy2091as2.Functions.User
import java.time.LocalDateTime

class DBHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        const val DATABASE_VERSION = 1
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
        val colPostImageBlob = "PostImageBlob"
        val colPostApprovalBy = "PostApprovalBy"

        val tblComment = "tblComments"
        val colCommentID = "CommentID"
        val colCommentAuthor = "CommentAuthor"
        val colCommentContent = "CommentContent"
        val colCommentPostId = "CommentPostId"
        val colCommentParentID = "CommentParent"
        val colCommentTime = "CommentTime"

        val tblLikes = "tblLikes"
        val colLikeId = "LikeId"
        val colLikeAuthor = "LikeAuthor"
        val colLikePost = "LikePost"
        val colLikeLiked = "Liked"
        val colLikeDisliked = "Disliked"
        val colLikeAddTime = "LikeAddTime"
        val colLikeUpdateTime = "LikeUpdateTime"


    }

    override fun onCreate(db: SQLiteDatabase?) {

        // TODO: insert an admin user by default and a student
        val qryAuthentication =
            "CREATE TABLE " + tblAuthentication + "(" + colUserID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + colAuthUserName + " TEXT, " + colAuthPassword + " TEXT, $colAuthType TEXT DEFAULT student)"
        val qryUser =
            "CREATE TABLE " + tblUsers + "(" + colUserID + " INTEGER PRIMARY KEY AUTOINCREMENT,  " + colAuthUserName + " TEXT, " + colUserFirstName + " TEXT, " +
                    colUserMiddleName + " TEXT, " + colUserLastName + " TEXT, " + colUserDOB + " TEXT, " + colUserEmail + " TEXT, " + colUserDateCreated + " TEXT, " + colUserDateUpdated + " TEXT)"
        val qryPost =
            "CREATE TABLE $tblPost ($colPostID INTEGER PRIMARY KEY AUTOINCREMENT, $colPostUsername TEXT , $colPostTime TEXT NOT NULL, $colPostUpdateTime TEXT NOT NULL, $colPostDesc TEXT, $colPostImage TEXT, $colPostApproval INTEGER,  $colPostImageBlob BLOB, $colPostApprovalBy INTEGER)"

        val qryComment =
            "CREATE TABLE $tblComment($colCommentID INTEGER PRIMARY KEY AUTOINCREMENT, $colCommentAuthor TEXT, $colCommentContent TEXT, $colCommentPostId INTEGER, $colCommentParentID INTEGER, $colCommentTime TEXT )"
        val qryLike =
            "CREATE TABLE $tblLikes ($colLikeId INTEGER PRIMARY KEY AUTOINCREMENT, $colLikePost INTEGER, $colLikeAuthor TEXT, $colLikeLiked INTEGER DEFAULT 0, $colLikeDisliked INTEGER DEFAULT 0, $colLikeAddTime TEXT)"




        if (db != null) {
            db.execSQL(qryUser)
            db.execSQL(qryAuthentication)
            db.execSQL(qryPost)
            db.execSQL(qryComment)
            db.execSQL(qryLike)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }

    private fun saveLike(likeId: Int, values: ContentValues): Boolean{
        val wdb = this.writableDatabase

        return if(likeId != 0 ){
            wdb.update(tblLikes, values, "$colLikeId = ?", arrayOf(likeId.toString())) > 0
        } else{
            var insert :Long? = null
            if (wdb != null) {
                insert = wdb.insert(tblLikes, null, values)
            }
            insert == -1L
        }
    }

    fun addLike(
        authorId: String,
        postId: Int
    ): Boolean {
        val dateTime = LocalDateTime.now().toString()
        val values = ContentValues()
        values.put(colLikeAuthor, authorId)
        values.put(colLikePost, postId)
        values.put(colLikeLiked, 1)
        values.put(colLikeDisliked, 0)
        values.put(colLikeAddTime, dateTime)

        val likeId = this.checkLikeExists(this.getLike(authorId, postId))
        return saveLike(likeId, values)

    }

    fun addDislike(
        authorId: String,
        postId: Int
    ): Boolean {
        val db = this.writableDatabase
        val dateTime = LocalDateTime.now().toString()

        val values = ContentValues()
        values.put(colLikeAuthor, authorId)
        values.put(colLikePost, postId)
        values.put(colLikeLiked, 0)
        values.put(colLikeDisliked, 1)
        values.put(colLikeAddTime, dateTime)

        val likeId = this.checkLikeExists(this.getLike(authorId, postId))

        return saveLike(likeId, values)
    }

    fun getLikeId(
        authorId: String,
        postId: Int
    ): Int? {
        val cursor = getLike(authorId, postId)
        val result =
            if (cursor.moveToNext()) cursor.getString(cursor.getColumnIndexOrThrow(colLikeId))
                .toInt() else null
        cursor.close()

        return result
    }

    private fun getLike(authorId: String, postId: Int): Cursor {
        val db = this.readableDatabase
        val query = "SELECT * FROM $tblLikes WHERE $colLikeAuthor = ? AND $colLikePost = ?"
        val args = arrayOf(authorId, postId.toString())
        return db.rawQuery(query, args)
    }

    private fun checkLikeExists(cursor: Cursor): Int{
        var likeId = 0
//        val exists = false
        if (cursor.moveToFirst()) {
            try {
                likeId = cursor.getString(cursor.getColumnIndexOrThrow(colLikeId)).toInt()
//                exists = true
            } catch (_: Exception) {
//                exists = false
            }
        }

        cursor.close()
        return likeId
    }

    fun removeFromLike(
        likeId: Int
    ): Boolean{
        val db = this.writableDatabase
        return db.delete(tblLikes, "$colLikeId = ?", arrayOf(likeId.toString())) > 0
    }

    fun getLikeCount(postId: Int): Int {
        var likes = 0
        val db = readableDatabase
        val query = "SELECT * FROM $tblLikes WHERE $colLikeLiked = 1 AND $colLikePost = ?"
        likes = db.rawQuery(query, arrayOf(postId.toString())).count
        return likes
    }

    fun getDislikeCount(postId: Int): Int {
        var likes = 0
        val db = readableDatabase
        val query = "SELECT * FROM $tblLikes WHERE $colLikeDisliked = 1 AND $colLikePost = ?"
        likes = db.rawQuery(query,  arrayOf(postId.toString())).count
        return likes
    }

    fun isLiked(
        likeId: Int
    ): Boolean {
        val db = this.readableDatabase
        val query = "SELECT * FROM $tblLikes WHERE $colLikeId = ? AND $colLikeLiked = 1"
        val cursor = db.rawQuery(query, arrayOf(likeId.toString()) )
        return cursor.count>0
    }

    fun isDisliked(
        likeId: Int
    ): Boolean {
        val db = this.readableDatabase
        val query = "SELECT * FROM $tblLikes WHERE $colLikeId = ?  AND $colLikeDisliked = 1"
        val cursor = db.rawQuery(query, arrayOf(likeId.toString()))
        return checkLikeExists(cursor) == likeId
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


    fun updateUser(
        oldUsername: String,
        bundle: Bundle
    ): Boolean {

        val newUsername = bundle.getString("username")
        val firstName = bundle.getString("firstname")
        val middleName = bundle.getString("middlename")
        val lastName = bundle.getString("lastname")
        val email = bundle.getString("email")
        val dob = bundle.getString("dateofbirth")
        val usertype = bundle.getString("usertype")

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
        userValues.put(colUserDateUpdated, currentDate)
        userValues.put(colAuthUserName, newUsername)

        val userCred = ContentValues()
        userCred.put(colAuthUserName, newUsername)
        userCred.put(colAuthType, usertype)

        val where = "$colAuthUserName = ?"
        val whereArgs = arrayOf(oldUsername)

        val statusUser = db.update(tblUsers, userValues, where, whereArgs) > 0
        val statusCred = db.update(tblAuthentication, userCred, where, whereArgs) > 0
        return statusUser && statusCred
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

    fun fetchUsername(email: String): String? {
        val db = this.readableDatabase
        val query = "SELECT $colAuthUserName FROM $tblUsers WHERE $colUserEmail = ?"
        val cursor = db.rawQuery(query, arrayOf(email))
        if (cursor.moveToFirst()) {
            return cursor.getString(cursor.getColumnIndexOrThrow(colAuthUserName))
        } else {
            return null
        }
    }

    fun fetchUser(username: String): User? {
        val db = this.readableDatabase
        val query = "SELECT * FROM $tblUsers WHERE $colAuthUserName = ?"
        val cursor = db.rawQuery(query, arrayOf(username))
        val auth = db.rawQuery(
            "SELECT * FROM $tblAuthentication WHERE $colAuthUserName = ?",
            arrayOf(username)
        )
        if(cursor.moveToFirst() && auth.moveToFirst()){
            try {
                return User(
                    cursor.getString(cursor.getColumnIndexOrThrow(colAuthUserName)),
                    cursor.getString(cursor.getColumnIndexOrThrow(colUserFirstName)),
                    cursor.getString(cursor.getColumnIndexOrThrow(colUserMiddleName)),
                    cursor.getString(cursor.getColumnIndexOrThrow(colUserLastName)),
                    cursor.getString(cursor.getColumnIndexOrThrow(colUserEmail)),
                    cursor.getString(cursor.getColumnIndexOrThrow(colUserDOB)),
                    auth.getString(auth.getColumnIndexOrThrow(colAuthType))
                )
            } catch (_: CursorIndexOutOfBoundsException){}
        }

        return null


    }

    fun userCheck(username: String): Boolean {
        val db = this.readableDatabase
        val query = "SELECT * FROM $tblAuthentication WHERE $colAuthUserName = ?"

        val cursor = db.rawQuery(query, arrayOf(username))
        val result = cursor.count > 0
        cursor.close()

        return result
    }

    fun updatePassword(username: String, newPassword: String): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(colAuthPassword, newPassword)

        val result =
            db.update(tblAuthentication, contentValues, "$colAuthUserName = ?", arrayOf(username))
        return (result > 0)
    }

    fun deleteUser(username: String): Boolean {
        val db = this.writableDatabase
        val result1 = db.delete(tblUsers, "$colAuthUserName = ?", arrayOf(username))
        val result2 = db.delete(tblAuthentication, "$colAuthUserName = ?", arrayOf(username))

        return (result1 + result2) > 0
    }


    fun emailCheck(email: String): Boolean {
        val db = this.readableDatabase
        val query = "SELECT * FROM $tblUsers WHERE $colUserEmail = ?"
        val cursor = db.rawQuery(query, arrayOf(email))
        cursor.moveToFirst()
        val result = cursor.count > 0
        cursor.close()
        return result

    }

    fun savePost(postId: Int, username: String, desc: String, usertype: String?, image: ByteArray?): Long {

        return if (postId == 0) {
            addPost(username, desc, usertype, image)
        } else {
            updatePost(postId, desc, usertype, image).toLong()
        }

    }

    fun addPost(username: String, desc: String, usertype: String?, image: ByteArray?): Long {
        val dateTime = LocalDateTime.now().toString()


        val db = this.writableDatabase
        val postValues = ContentValues()
        postValues.put(colPostUsername, username)
        postValues.put(colPostTime, dateTime)
        postValues.put(colPostUpdateTime, dateTime)
        postValues.put(colPostDesc, desc)
//        postValues.put(colPostImage, imagePath)
        postValues.put(colPostImageBlob, image)

        when (usertype) {
            "admin" -> postValues.put(colPostApproval, 1)
            "student" -> postValues.put(colPostApproval, 0)
            else -> postValues.put(colPostApproval, 0)
        }


        return db.insert(tblPost, null, postValues)


    }

    fun updatePost(postId: Int, desc: String, usertype: String?, image: ByteArray?): Int {
        val dateTime = LocalDateTime.now().toString()


        val db = this.writableDatabase
        val postValues = ContentValues()
        postValues.put(colPostUpdateTime, dateTime)
        postValues.put(colPostDesc, desc)
//        postValues.put(colPostImage, imagePath)
        postValues.put(colPostImageBlob, image)
        when (usertype) {
            "admin" -> postValues.put(colPostApproval, 1)
            "student" -> postValues.put(colPostApproval, 0)
            else -> postValues.put(colPostApproval, 0)
        }

        val where = "$colPostID = ?"
        val args = arrayOf(postId.toString())

        return db.update(tblPost, postValues, where, args)


    }



    fun getPosts(): MutableList<Post> {
        val query =
            "SELECT * FROM $tblPost WHERE $colPostApproval = 1 ORDER BY $colPostID DESC"
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
//                val imgPath = cursor.getString(cursor.getColumnIndexOrThrow(colPostImage))
                val txtDesp = cursor.getString(cursor.getColumnIndexOrThrow(colPostDesc))
                val approval = cursor.getString(cursor.getColumnIndexOrThrow(colPostApproval))
                val image = cursor.getBlob(cursor.getColumnIndexOrThrow(colPostImageBlob))
                postList.add(Post(postId, username, txtDesp, approval.toInt(), image))

            } while (cursor.moveToNext())
        }

        return postList
    }


    fun addComment(author: String, content: String, postId: Int, parent: Int?): Long {
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

    fun getComment(postId: Int): MutableList<Comment> {
        var commentList = mutableListOf<Comment>()
        val db = this.readableDatabase
        val query =
            "SELECT * FROM $tblComment WHERE $colCommentPostId = ? ORDER BY $colCommentID DESC"
        val cursor = db.rawQuery(query, arrayOf(postId.toString()))
        if (cursor.moveToFirst()) {
            do {
                val commentId = cursor.getInt(cursor.getColumnIndexOrThrow(colCommentID))
                val author = cursor.getString(cursor.getColumnIndexOrThrow(colCommentAuthor))
                val commentContent =
                    cursor.getString(cursor.getColumnIndexOrThrow(colCommentContent))

                val comment = Comment(commentId, author, commentContent)
                commentList.add(comment)
            } while (cursor.moveToNext())
        }

        return commentList

    }

    fun getPostsToApprove(): MutableList<Post>{
        val query = "SELECT * FROM $tblPost WHERE $colPostApproval = 0"
        return getPost(query, null)
    }

    fun getPostsDecline(): MutableList<Post>{
        val query = "SELECT * FROM $tblPost WHERE $colPostApproval = -1"
        return getPost(query, null)
    }

    fun getPostsToApproveCount(): String {
        val db = this.readableDatabase
        val query = "SELECT * FROM $tblPost WHERE $colPostApproval = 0"
        return db.rawQuery(query, null).count.toString()
    }

    fun getDeclinedPostsCount(): String{
        val db = this.readableDatabase
        val query = "SELECT * FROM $tblPost WHERE $colPostApproval = -1"
        return db.rawQuery(query, null).count.toString()
    }


    fun declinePost(postId: Int, superuser: String): Boolean{
        val db = writableDatabase
        val values = ContentValues()
        values.put(colPostApproval, -1)
        values.put(colPostApprovalBy, superuser)
        return db.update(tblPost, values, "$colPostID = ?", arrayOf(postId.toString())) > 0
    }

    fun approvePost(postId: Int, superuser: String): Boolean{
        val db = writableDatabase
        val values = ContentValues()
        values.put(colPostApproval, 1)
        values.put(colPostApprovalBy, superuser)
        return db.update(tblPost, values, "$colPostID = ?", arrayOf(postId.toString())) > 0
    }

    fun getUsers(userType: String): MutableList<User> {
        val users = mutableListOf<User>()
        val db = this.readableDatabase
        var query = when(userType){
            "admin" -> {"SELECT * FROM $tblUsers"}
            "student" -> "SELECT * FROM $tblUsers WHERE $colAuthUserName IN (SELECT $colAuthUserName FROM $tblAuthentication WHERE $colAuthType = 'student')"
            else->{""}
        }

        val cursor = db.rawQuery(query, null)
        if(cursor.moveToFirst()){
            do{val username = cursor.getString(cursor.getColumnIndexOrThrow(colAuthUserName))
                val firstName =cursor.getString(cursor.getColumnIndexOrThrow(colUserFirstName))
                val middleName =cursor.getString(cursor.getColumnIndexOrThrow(colUserMiddleName))
                val lastName =cursor.getString(cursor.getColumnIndexOrThrow(colUserLastName))
                val email =cursor.getString(cursor.getColumnIndexOrThrow(colUserEmail))
                val dob =cursor.getString(cursor.getColumnIndexOrThrow(colUserDOB))
                users.add(User(username, firstName, middleName, lastName, email, dob, "student"))

            } while (cursor.moveToNext())


        }

        return users
    }

    fun getUserCount(): Int{
        val db = this.readableDatabase
        val query = "SELECT * FROM $tblUsers WHERE $colAuthUserName IN (SELECT $colAuthUserName FROM $tblAuthentication WHERE $colAuthType = 'admin')"
        val cursor = db.rawQuery(query, null)
        return cursor.count
    }

}