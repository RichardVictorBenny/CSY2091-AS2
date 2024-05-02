/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.csy2091as2.Functions;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * the method takes in username and password, and then runs them through a hashing alogrithm. 
 * outputs the encoded password as a string.
 * reference: <a href="https://www.baeldung.com/java-password-hashing">...</a>, <a href="https://stackoverflow.com/questions/5531455/how-to-hash-some-string-with-sha-256-in-java">...</a>
 * made for AS2 of CSY2094
 *
 * @author Richard
 *
 */
public class Hashing {
    /**
     * performs one way hashing 
     * 
     * @param password String - user password
     * @param username String - user username
     * @return String - hashed password
     * @throws NoSuchAlgorithmException
     */
    public static String doHashing(String password, String username) throws NoSuchAlgorithmException{
        String passwordText = username + password;
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.update(passwordText.getBytes()); 
        byte[] encodedPasswordArray = digest.digest();

        StringBuilder encodedPassword = new StringBuilder();

        for (byte b : encodedPasswordArray){
            encodedPassword.append(Integer.toHexString(0xFF & b));
        }

        return encodedPassword.toString();
    }
}
