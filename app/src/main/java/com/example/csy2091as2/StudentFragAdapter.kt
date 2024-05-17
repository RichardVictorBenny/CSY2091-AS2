package com.example.csy2091as2

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.*
import com.example.csy2091as2.Functions.User

class StudentFragAdapter(
    private val dataset: MutableList<User>,
    private val context: Context
): Adapter<StudentFragAdapter.ViewHolder>(){

    private lateinit var db: DBHelper
    class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val txtUsername: TextView = view.findViewById(R.id.txtStudentUsername)
        val txtStudentName: TextView = view .findViewById(R.id.txtStudentName)
    }

    override fun getItemCount(): Int = dataset.size


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
         val view = LayoutInflater.from(parent.context).inflate(R.layout.student_card_layout, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        db = DBHelper(context)
        val student = dataset[position]

        val studentName = "${student.firstName} ${student.middleName} ${student.lastName}"

        holder.txtStudentName.text = studentName
        holder.txtUsername.text = student.username

    }

}