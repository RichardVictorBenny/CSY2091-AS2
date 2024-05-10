package com.example.csy2091as2

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.csy2091as2.databinding.ActivityMainBinding
import java.util.jar.Attributes

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var userType: String
    private lateinit var userName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(findViewById(R.id.my_toolbar))

        binding.bottomNavigationView.background = null

        val sharedPreferences = getSharedPreferences("currentUser", Context.MODE_PRIVATE)
//        userType = intent.getStringExtra("usertype").toString()
//        userName = intent.getStringExtra("username").toString()
        userType = sharedPreferences.getString("usertype", null).toString()
        userName = sharedPreferences.getString("username", null).toString()
//        Log.d("TAG", "MainActivity username: $userName")
//        Log.d("TAG", "MainActivity username: $userType")

        replaceFragment(HomeFragment())

        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.btmHome -> replaceFragment(HomeFragment())
                R.id.btmChat -> null
                R.id.btmGroup -> null
                R.id.btmAccount -> replaceFragment(AccountFragment())
                R.id.btmPost -> openDrawer() // make new activity for polls etc.
            }
            true
        }




    }

    private fun replaceFragment(fragment: Fragment){
        val fragmentTransaction : FragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_container, fragment)
        fragmentTransaction.commitNow()
    }

    private fun openDrawer(){
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.post_drawer_layout)

        val post: LinearLayout = dialog.findViewById(R.id.layPost)
        val poll: LinearLayout = dialog.findViewById(R.id.layPoll)
        
        post.setOnClickListener{
//            Toast.makeText(applicationContext, "Make new post", Toast.LENGTH_SHORT).show()
            val activity = Intent(this, PostActivity::class.java)

            activity.putExtra("usertype", userType)
            activity.putExtra("username", userName)
            startActivity(activity)
        }
        
        poll.setOnClickListener{
            Toast.makeText(applicationContext, "Make new poll", Toast.LENGTH_SHORT).show()
        }

        dialog.show()
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.window?.setGravity(Gravity.BOTTOM)


    }
}