package com.example.csy2091as2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.example.csy2091as2.Functions.Hashing
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val db = DBHelper(this)
        val txtSignUp:TextView = findViewById(R.id.textViewSignUp)
        val edtUserName: TextInputEditText = findViewById(R.id.inpedtUsername)
        val edtPassword: TextInputEditText = findViewById(R.id.inpedtPassword)
        val layUserName: TextInputLayout = findViewById(R.id.inplayUsername)
        val layPassword: TextInputLayout = findViewById(R.id.inplayPassword)

        val btnLogin: Button = findViewById(R.id.btn_login)



        txtSignUp.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))


        }

        btnLogin.setOnClickListener{
            val username = edtUserName.text.toString()
            if(db.authenticate(username, Hashing.doHashing(edtPassword.text.toString(), username))){
                startActivity(Intent(this, MainActivity::class.java))
            }
        }
    }
}