package com.example.csy2091as2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.csy2091as2.Functions.Functions

/**
 * sets the fragment container with the necessary fragment
 */
class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.registerFragmentContainer, RegisterDetailsFragment()).commit()


    }

}
