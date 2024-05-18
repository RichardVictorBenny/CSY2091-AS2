package com.example.csy2091as2

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.WindowManager
import com.example.csy2091as2.Functions.Hashing
import com.example.csy2091as2.Functions.User

class BootActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_boot)

        val db = DBHelper(this)

        window.statusBarColor = Color.TRANSPARENT
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )


        if (db.fetchUser("admin") == null && db.getUserCount() == 0) {
            db.addUser(
                "admin",
                "admin",
                "admin",
                "admin",
                "admin@middlemore.co.uk",
                "",
                Hashing.doHashing("admin", "admin"),
                "admin"
            )
        }


        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }, 1500)

    }
}