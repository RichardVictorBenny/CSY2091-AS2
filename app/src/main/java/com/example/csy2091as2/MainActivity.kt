package com.example.csy2091as2

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.csy2091as2.Admin.AdminActivity
import com.example.csy2091as2.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var userType: String
    private lateinit var userName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = getSharedPreferences("currentUser", Context.MODE_PRIVATE)
        userType = sharedPreferences.getString("usertype", null).toString()
        userName = sharedPreferences.getString("username", null).toString()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(findViewById(R.id.my_toolbar))

        binding.bottomNavigationView.background = null

//         making options for admins visible
        if(userType == "admin"){
            binding.imgAdminOptions.visibility = View.VISIBLE
            binding.imgAdminOptions.setOnClickListener{
                startActivity(Intent(this, AdminActivity::class.java))
            }
        }


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