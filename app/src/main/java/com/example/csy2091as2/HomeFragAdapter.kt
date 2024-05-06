package com.example.csy2091as2


import android.graphics.BitmapFactory
import android.provider.CalendarContract.Events
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.csy2091as2.Functions.Post


class HomeFragAdapter(
    private val dataset: MutableList<Post>
): RecyclerView.Adapter<HomeFragAdapter.ViewHolder>() {

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

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeFragAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.post_item_layout, parent, false)
        return  ViewHolder(view)
    }

    override fun onBindViewHolder(holder: HomeFragAdapter.ViewHolder, position: Int) {
        val imgPath = dataset[position].imgPath
//        holder.btnLike.setImageResource(LIKE_ON)
//        holder.btnLike.tag = LIKE_ON

        // TODO: just defaults to no likes, need to see if the user already liked and do appropriately, need to block user from liking and dislikeing at the same time
        setButton(holder.btnLike, LIKE_OFF)
        setButton(holder.btnDislike, DISLIKE_OFF)

        holder.txtUsername.text = dataset[position].username
        holder.txtDesp.text = dataset[position].txtDesp



        if(imgPath != ""){
            try {
                val imageBitmap = BitmapFactory.decodeFile(dataset[position].imgPath)
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
        



    }

    private fun openComments(postID: Int) {

    }

    private fun setButton(imgView: ImageView, resource: Int){
        imgView.setImageResource(resource)
        imgView.tag = resource
    }

    override fun getItemCount() = dataset.size
}