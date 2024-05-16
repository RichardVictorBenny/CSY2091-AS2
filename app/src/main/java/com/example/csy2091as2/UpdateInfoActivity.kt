package com.example.csy2091as2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.csy2091as2.Functions.Functions
import com.example.csy2091as2.Functions.User
import com.example.csy2091as2.databinding.ActivityUpdateInfoBinding

class UpdateInfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding =ActivityUpdateInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val db = DBHelper(this)
        val username = Functions.getUserinfo(this)["username"]!!
        val usertype = Functions.getUserinfo(this)["usertype"]!!

        var fragment = RegisterDetailsFragment()
        var transaction = supportFragmentManager.beginTransaction()
        var studentInfo: User? = null

        //checks if the user is coming in form account page or admin page
//            admin page needs a user to be searched in order to

        if(intent.getStringExtra("toUpdateUser") == "y"){
            studentInfo = db.fetchUser(username)
            val bundle = makeBundle(studentInfo)
            fragment.arguments = bundle

            transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fcUpdateInfo, fragment).commit()

            binding.llUpdateSearch.visibility = View.GONE
        }


        binding.btnInfoSearch.setOnClickListener{
            transaction.remove(fragment).commitNow()
            if(binding.edtUpdateInfo.text.isEmpty()){
                Toast.makeText(this, "Username required", Toast.LENGTH_SHORT).show()
            } else{
                val userSearch = binding.edtUpdateInfo.text.toString()
                try {

                    studentInfo = db.fetchUser(userSearch)
                    val bundle = makeBundle(studentInfo!!)
                    fragment.arguments = bundle


                    transaction.replace(R.id.fcUpdateInfo, fragment).commitNow()
                    fragment = RegisterDetailsFragment()

                } catch (e: Exception) {
                    Toast.makeText(this, "User does not exist", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun makeBundle(studentInfo: User): Bundle{
        val bundle = Bundle()
        bundle.putString("firstname", studentInfo.firstName)
        bundle.putString("middlename", studentInfo.middleName)
        bundle.putString("lastname", studentInfo.lastName)
        bundle.putString("email", studentInfo.email)
        bundle.putString("dateofbirth", studentInfo.dateOfBirth)
        bundle.putString("username", studentInfo.username)
        bundle.putString("usertype", studentInfo.userType)
        bundle.putString("toUpdate", studentInfo.username)//used to see if it is an update or a new user
        return bundle
    }
}