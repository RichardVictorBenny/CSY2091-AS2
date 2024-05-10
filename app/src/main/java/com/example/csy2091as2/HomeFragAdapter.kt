package com.example.csy2091as2


import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.csy2091as2.Functions.Post


class HomeFragAdapter(
    private val dataset: MutableList<Post>,
    private val context: Context,
    private val userInfo: Map<String, String>

): RecyclerView.Adapter<HomeFragAdapter.ViewHolder>() {


    private lateinit var db: DBHelper

    companion object{
        val LIKE_ON = R.drawable.ic_thumb_up_on_24
        val LIKE_OFF = R.drawable.ic_thumb_up_off_alt_24
        val DISLIKE_ON = R.drawable.ic_thumb_down_on_alt_24
        val DISLIKE_OFF = R.drawable.ic_thumb_down_off_alt_24
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view){

        val txtUsername = view.findViewById<TextView>(R.id.txtPostUsername)
        val btnLike: ImageView = view.findViewById(R.id.imgPostLike)
        val btnDislike: ImageView = view.findViewById(R.id.imgPostDislike)
        val imgPost: ImageView = view.findViewById(R.id.imgOnPost)
        val btnComment: ImageView = view.findViewById(R.id.imgPostComment)
        val txtDesp: TextView = view.findViewById(R.id.txtPostDesp)
        val btnMoreOption: ImageView = view.findViewById(R.id.imgMoreOptions)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeFragAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.post_item_layout, parent, false)
        return  ViewHolder(view)
    }

    override fun onBindViewHolder(holder: HomeFragAdapter.ViewHolder, position: Int) {
        db = DBHelper(context)
        val imgPath = dataset[position].postImgPath
//        holder.btnLike.setImageResource(LIKE_ON)
//        holder.btnLike.tag = LIKE_ON

        // TODO: just defaults to no likes, need to see if the user already liked and do appropriately, need to block user from liking and dislikeing at the same time
        setButton(holder.btnLike, LIKE_OFF)
        setButton(holder.btnDislike, DISLIKE_OFF)

//        edit button visible only to authors of posts
        if(dataset[position].username == userInfo.get("username")){
            holder.btnMoreOption.visibility = View.VISIBLE
        }

        holder.txtUsername.text = dataset[position].username
        holder.txtDesp.text = dataset[position].txtDesp



        if(imgPath != ""){
            try {
                val imageBitmap = BitmapFactory.decodeFile(dataset[position].postImgPath)
                holder.imgPost.setImageBitmap(imageBitmap)
            } catch (_: Exception){

            }
        } else{
            holder.imgPost.visibility = View.GONE
        }
        
        holder.btnComment.setOnClickListener{
            openComments(dataset[position].postID)
        }

        holder.btnLike.setOnClickListener{
            if(holder.btnLike.tag == LIKE_ON){
                setButton(holder.btnLike, LIKE_OFF)
            } else if(holder.btnLike.tag == LIKE_OFF){
                setButton(holder.btnLike, LIKE_ON)
            }
        }

        holder.btnDislike.setOnClickListener{
            if(holder.btnDislike.tag == DISLIKE_ON){
                setButton(holder.btnDislike, DISLIKE_OFF)
            } else if(holder.btnDislike.tag == DISLIKE_OFF){
                setButton(holder.btnDislike, DISLIKE_ON)

            }
        }

        holder.btnMoreOption.setOnClickListener{
                val popUp = PopupMenu(context, it)

                popUp.menu.add("Edit")
                popUp.menu.add("Delete")
                popUp.show()

                popUp.setOnMenuItemClickListener {
                    when(it.title){
                        "Delete" -> delete(position)
                        "Edit" -> edit(position)
                     }
                    true
                }
                true
        }
        



    }



    private fun openComments(postID: Int) {

    }

    private fun setButton(imgView: ImageView, resource: Int){
        imgView.setImageResource(resource)
        imgView.tag = resource
    }

    private fun delete(position: Int){
        if(db.deletePost(dataset[position].postID) == 1){
            Toast.makeText(context, "Post deleted", Toast.LENGTH_SHORT).show()
            refreshRecyclerView(db.getPost10())
        } else {
            Toast.makeText(context, "Action Unsuccessful", Toast.LENGTH_SHORT).show()
        }
    }

    private fun edit(position: Int) {
        val activity = Intent(context, PostActivity::class.java)
        activity.putExtra("postId", dataset[position].postID)
        context.startActivity(activity)
    }


    fun refreshRecyclerView(newData: MutableList<Post>){
        dataset.clear()
        dataset.addAll(newData)
        notifyDataSetChanged()
    }

    override fun getItemCount() = dataset.size
}