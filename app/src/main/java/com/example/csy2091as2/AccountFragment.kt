package com.example.csy2091as2

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.csy2091as2.Functions.Functions
import com.example.csy2091as2.Functions.User



/**
 * A simple [Fragment] subclass.
 * Use the [AccountFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AccountFragment : Fragment() {




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val userInfo = Functions.getUserinfo(requireContext())
        val userName = userInfo["username"]!!
        val userType = userInfo["usertype"]!!

        val db = DBHelper(requireContext())
        val view =  inflater.inflate(R.layout.fragment_account, container, false)

        val txtUsername = view.findViewById<TextView>(R.id.txtUserUsername)
        val txtFullname = view.findViewById<TextView>(R.id.txtUserFullName)

        val currentUser = db.fetchUser(userName)!!

        txtUsername.setText(userName)
        txtFullname.setText(getFullName(currentUser))

        val rvAccFrag = view.findViewById<RecyclerView>(R.id.rvAccountFrag)
        rvAccFrag.layoutManager = LinearLayoutManager(requireContext())
        var posts = db.getPostUser(userName)
        rvAccFrag.adapter = PostFragAdapter(posts, requireContext())

//        sign out user and clear all data saved
        val btnLogout: Button = view.findViewById(R.id.btnLogout)
        btnLogout.setOnClickListener {
            Functions.logout(context)
            activity?.finish()

        }

        // setting edit info button
        val btnEditInfo = view.findViewById<ImageView>(R.id.imgEditInfo)
        btnEditInfo.setOnClickListener{
            val activity = Intent(requireContext(), UpdateInfoActivity::class.java)

            //to know that the activity is invoked by edit account button.
            activity.putExtra("toUpdateUser", "y")
            startActivity(activity)
        }



        return view
    }

    /**
     * generates full name of the user
     */
    private fun getFullName(user: User): String {
        return user.firstName+" "+user.lastName
    }


}