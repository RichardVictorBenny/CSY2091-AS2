package com.example.csy2091as2

import android.content.Context
import android.service.autofill.Dataset
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.csy2091as2.Functions.Comment
import com.example.csy2091as2.Functions.Functions

class CommentAdapter(
    private val dataset: MutableList<Comment>,
    private val context: Context
) : RecyclerView.Adapter<CommentAdapter.ViewHolder>() {

    private lateinit var db: DBHelper
    private val userInfo = Functions.getUserinfo(context)

    class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val author: TextView = view.findViewById(R.id.txtCommentAuthor)
        val comment: TextView = view.findViewById(R.id.txtCommentContent)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.comment_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = dataset.size

    override fun onBindViewHolder(holder: CommentAdapter.ViewHolder, position: Int) {
        db = DBHelper(context)

        holder.author.setText(dataset[position].author)
        holder.comment.setText(dataset[position].commentContent)


    }
}