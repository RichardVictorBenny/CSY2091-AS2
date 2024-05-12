package com.example.csy2091as2

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import com.example.csy2091as2.Functions.Hashing
import com.example.csy2091as2.Functions.Validations
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlin.math.log

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)



        val sharedPref = getSharedPreferences("userinfo", Context.MODE_PRIVATE)
//        val editor = sharedPref.edit()
//        editor.clear()
//        editor.commit()

        if (sharedPref.contains("username") && sharedPref.contains("usertype")) {
            val activity = Intent(this, MainActivity::class.java)
            startActivity(activity)
        }

        val db = DBHelper(this)
        val validation = Validations()
        val txtSignUp: TextView = findViewById(R.id.textViewSignUp)
        val edtUserName: TextInputEditText = findViewById(R.id.inpedtUsername)
        val edtPassword: TextInputEditText = findViewById(R.id.inpedtPassword)
        val layUserName: TextInputLayout = findViewById(R.id.inplayUsername)
        val layPassword: TextInputLayout = findViewById(R.id.inplayPassword)
        val chkSaveUser: CheckBox = findViewById(R.id.chkSaveUser)

        val btnLogin: Button = findViewById(R.id.btn_login)



        txtSignUp.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))


        }

        btnLogin.setOnClickListener {

            validation.emptyCheck(layUserName, edtUserName)
            validation.emptyCheck(layPassword, edtPassword)

//            determining username if user enters an email

            var username = edtUserName.text.toString()
            try {
//                Log.d("TAG", "onCreate: inside try")
                if(validation.validateEmail(edtUserName, layUserName)){
//                    Log.d("TAG", "onCreate: email is correct")
                    if(db.fetchUsername(username) != null){
                        username = db.fetchUsername(username)!!
//                        Log.d("TAG", "onCreate: username fetched")
                    } else{
                        Toast.makeText(this, "invalid Email", Toast.LENGTH_SHORT).show()
//                        Log.d("TAG", "onCreate: invalid email")
                    }
                } else{
//                    Log.d("TAG", "onCreate: email validationfailsed")
                    Toast.makeText(this, "Invalid username or Email", Toast.LENGTH_SHORT).show()
                }
            } catch (_: Exception){
                layUserName.error= null
            }


            val userType =
                db.authenticate(username, Hashing.doHashing(edtPassword.text.toString(), username))



            if (userType == "student" || userType == "admin") {
//                Log.d("TAG", "onCreate: Authentication Successful")
                //making a file with username and access level for global access.
                val userInfo = this.getSharedPreferences("currentUser", Context.MODE_PRIVATE)
                val editor = userInfo.edit()
                editor.putString("username", username)
                editor.putString("usertype", userType)
                editor.apply()

                edtUserName.setText("")
                edtPassword.setText("")
                layUserName.error= null

                val activity = Intent(this, MainActivity::class.java)
                if (chkSaveUser.isChecked) {
                    val userInfo = this.getSharedPreferences("userinfo", Context.MODE_PRIVATE)
                    val editor = userInfo.edit()
                    editor.putString("username", username)
                    editor.putString("usertype", userType)
                    editor.apply()
                }
                startActivity(activity)
            } else {
                layPassword.error = " "
                layUserName.error = " "
                Toast.makeText(this, "Invalid Username/email or Password", Toast.LENGTH_SHORT).show()
            }
        }

        // TODO: work on reseting password
    }
}