package com.example.csy2091as2

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.csy2091as2.Functions.Functions


/**
 * A simple [Fragment] subclass.
 * Use the [StudentsFragment.newInstance] factory method to
 * create an instance of this fragment.
 * loads the
 */
class StudentsFragment : Fragment() {
    private lateinit var db: DBHelper
    private lateinit var userInfo: Map<String, String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        db = DBHelper(requireContext())
        userInfo = Functions.getUserinfo(requireContext())
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_students, container, false)

        val rvHomeFrag = view.findViewById<RecyclerView>(R.id.rvStudentList)
        rvHomeFrag.layoutManager = LinearLayoutManager(requireContext())

        val students = db.getUsers(userInfo["usertype"]!!)
        rvHomeFrag.adapter = StudentFragAdapter(students,requireContext())

        return  view
    }

}