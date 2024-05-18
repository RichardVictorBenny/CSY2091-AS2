package com.example.csy2091as2.Functions

/**
 * class representing user
 */
data class User(
    val username: String,
    val firstName:String,
    val middleName:String?,
    val lastName: String,
    val email: String,
    val dateOfBirth: String,
    val userType: String?

)
