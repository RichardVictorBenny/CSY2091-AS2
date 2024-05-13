package com.example.csy2091as2

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.csy2091as2.Functions.Functions
import com.example.csy2091as2.Functions.User
import kotlin.math.log

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

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

        val currentUser = db.fetchUser(userName)

        txtUsername.setText(userName)
        txtFullname.setText(getFullName(currentUser))

        val rvAccFrag = view.findViewById<RecyclerView>(R.id.rvAccountFrag)
        rvAccFrag.layoutManager = LinearLayoutManager(requireContext())
        var posts = db.getPostUser(userName)
        rvAccFrag.adapter = HomeFragAdapter(posts, requireContext())

//        sign out user and clear all data saved
        val btnLogout: Button = view.findViewById(R.id.btnLogout)
        btnLogout.setOnClickListener {
            Functions.logout(context)
            activity?.finish()

        }



        return view
    }

    private fun getFullName(user: User): String {
        return user.firstName+" "+user.lastName
    }


}