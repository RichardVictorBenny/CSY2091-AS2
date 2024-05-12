/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.csy2091as2.Functions

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 * the method takes in username and password, and then runs them through a hashing alogrithm.
 * outputs the encoded password as a string.
 * reference: [ 1 ](https://www.baeldung.com/java-password-hashing), [ 2 ](https://stackoverflow.com/questions/5531455/how-to-hash-some-string-with-sha-256-in-java)
 * made for AS2 of CSY2094
 *
 * @author Richard
 */
object Hashing {
    /**
     * performs one way hashing
     *
     * @param password String - user password
     * @param username String - user username
     * @return String - hashed password
     * @throws NoSuchAlgorithmException
     */
    @Throws(NoSuchAlgorithmException::class)
    fun doHashing(password: String, username: String): String {
        val passwordText = username + password
        val digest = MessageDigest.getInstance("SHA-256")
        digest.update(passwordText.toByteArray())
        val encodedPasswordArray = digest.digest()
        val encodedPassword = StringBuilder()
        for (b in encodedPasswordArray) {
            encodedPassword.append(Integer.toHexString(0xFF and b.toInt()))
        }
        return encodedPassword.toString()
    }
}
