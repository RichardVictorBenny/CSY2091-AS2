package com.example.csy2091as2

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [StudentsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class StudentsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var db: DBHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        db = DBHelper(requireContext())
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_students, container, false)

        val rvHomeFrag = view.findViewById<RecyclerView>(R.id.rvStudentList)
        rvHomeFrag.layoutManager = LinearLayoutManager(requireContext())
        var students = db.getStudents()
        Log.d("TAG", "onCreateView: $students")
        rvHomeFrag.adapter = StudentFragAdapter(students,requireContext())

        return  view
    }

}