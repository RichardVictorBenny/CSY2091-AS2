package com.example.csy2091as2.Functions

import java.sql.Blob

class Post(
    val postID: Int,
    val username: String,
    val txtDesp: String,
    val approval: Int,
    val image: ByteArray? = null

)