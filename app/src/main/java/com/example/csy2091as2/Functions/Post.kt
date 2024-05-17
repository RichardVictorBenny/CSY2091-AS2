package com.example.csy2091as2.Functions

import java.sql.Blob

class Post (
    val postID: Int,
    val username: String,
    val postImgPath: String,
    val txtDesp: String,
    val image: Blob? = null,
    val LikeCount: Int? = null,
    val DislikeCount: Int? = null
)