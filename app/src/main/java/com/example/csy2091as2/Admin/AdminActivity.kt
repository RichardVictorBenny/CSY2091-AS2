package com.example.csy2091as2.Admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.csy2091as2.R
import com.example.csy2091as2.RegisterActivity
import com.example.csy2091as2.databinding.ActivityAdminBinding
import com.example.csy2091as2.databinding.ActivityPostBinding

class AdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.layAddStudent.setOnClickListener{
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}