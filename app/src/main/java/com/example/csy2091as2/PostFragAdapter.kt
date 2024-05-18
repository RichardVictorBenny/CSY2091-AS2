package com.example.csy2091as2


import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.csy2091as2.Functions.Functions
import com.example.csy2091as2.Functions.Post
import java.util.Timer
import java.util.TimerTask


class PostFragAdapter(
    private val dataset: MutableList<Post>,
    private val context: Context

) : RecyclerView.Adapter<PostFragAdapter.ViewHolder>() {


    private lateinit var db: DBHelper
    private val userInfo = Functions.getUserinfo(context)
    private val username = userInfo["username"]!!
    private val usertype = userInfo["usertype"]!!
    private var likeId : Int? = null
    private val timer = Timer()


    companion object {
        val LIKE_ON = R.drawable.ic_thumb_up_on_24
        val LIKE_OFF = R.drawable.ic_thumb_up_off_alt_24
        val DISLIKE_ON = R.drawable.ic_thumb_down_on_alt_24
        val DISLIKE_OFF = R.drawable.ic_thumb_down_off_alt_24
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val txtUsername: TextView = view.findViewById(R.id.txtPostUsername)
        val btnLike: ImageView = view.findViewById(R.id.imgPostLike)
        val btnDislike: ImageView = view.findViewById(R.id.imgPostDislike)
        val imgPost: ImageView = view.findViewById(R.id.imgOnPost)
        val btnComment: ImageView = view.findViewById(R.id.imgPostComment)
        val txtDesp: TextView = view.findViewById(R.id.txtPostDesp)
        val btnMoreOption: ImageView = view.findViewById(R.id.imgMoreOptions)
        val rlAdminOption: RelativeLayout = view.findViewById(R.id.rlAdminOption)
        val btnApprove: Button = view.findViewById(R.id.btnApprove)
        val btnDecline: Button = view.findViewById(R.id.btnDecline)
        val txtLikeCount: TextView = view.findViewById(R.id.txtLikeCount)
        val txtDislikeCount: TextView = view.findViewById(R.id.txtDislikeCount)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.post_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        db = DBHelper(context)
        val postId = dataset[position].postID
        var likes = 0
        var dislikes = 0


        // just defaults to no likes, need to see if the user already liked and do appropriately, need to block user from liking and dislikeing at the same time
        likeId = db.getLikeId(username, postId)
//        Log.d("TAG", "onBindViewHolder: ${likeId.toString()}")

        try {
            if (db.isLiked(likeId!!)) {
                setButton(holder.btnLike, LIKE_ON)
                setButton(holder.btnDislike, DISLIKE_OFF)
            } else if (db.isDisliked(likeId!!)) {
                setButton(holder.btnDislike, DISLIKE_ON)
                setButton(holder.btnLike, LIKE_OFF)
            }
        } catch (_: Exception){
            setButton(holder.btnLike, LIKE_OFF)
            setButton(holder.btnDislike, DISLIKE_OFF)
        }








//        edit button visible only to authors of posts
        if (dataset[position].username == username || usertype == "admin") {
            holder.btnMoreOption.visibility = View.VISIBLE
            holder.txtLikeCount.visibility = View.VISIBLE
            holder.txtDislikeCount.visibility = View.VISIBLE
//getting likes and dislikes
            try{
                timer.scheduleAtFixedRate(object : TimerTask() {
                    override fun run() {
                        likes = db.getLikeCount(dataset[position].postID)
                        dislikes = db.getDislikeCount(dataset[position].postID)
                        holder.txtLikeCount.post {
                            holder.txtLikeCount.text = likes.toString()
                        }
                        holder.txtDislikeCount.post {
                            holder.txtDislikeCount.text = dislikes.toString()
                        }
                    }
                }, 0, 6000)
            } catch (_:Exception){}

        }

        //related to content moderation
        if(dataset[position].approval == 0 || dataset[position].approval == -1){
            holder.rlAdminOption.visibility = View.VISIBLE
            if(dataset[position].approval == -1){
                holder.btnDecline.visibility = View.GONE
            }
        }

        holder.btnDecline.setOnClickListener{
            db.declinePost(dataset[position].postID, usertype)
            refreshRecyclerView(db.getPostsToApprove())
        }

        holder.btnApprove.setOnClickListener{
            db.approvePost(dataset[position].postID, usertype)
            refreshRecyclerView(db.getPostsToApprove())
        }


        holder.txtUsername.text = dataset[position].username
        holder.txtDesp.text = dataset[position].txtDesp



        if (dataset[position].image != null) {
            try {

                holder.imgPost.setImageBitmap(Functions.byteArrayToBitmap(dataset[position].image!!))
            } catch (_: Exception) {
            }
        } else {
            holder.imgPost.visibility = View.GONE
        }

        //dec
        holder.btnComment.setOnClickListener {
            openComments(dataset[position].postID)

        }


        //set the like or dislike on or off as per each user. get the likeid

        holder.btnLike.setOnClickListener {
            if (holder.btnLike.tag == LIKE_ON) {
                setButton(holder.btnLike, LIKE_OFF)
//                remove like form db and refresh
                db.removeFromLike(likeId!!)
                likes--
                holder.txtLikeCount.text = likes.toString()



            } else if (holder.btnLike.tag == LIKE_OFF) {
                setButton(holder.btnLike, LIKE_ON)
                // add like to db and remove dislike if prev disliked  and todo refresh
                db.addLike(username, postId)
                likeId = db.getLikeId(username, postId)
                likes++
                holder.txtLikeCount.text = likes.toString()
                setButton(holder.btnDislike, DISLIKE_OFF)
            }
        }

        holder.btnDislike.setOnClickListener {
            if (holder.btnDislike.tag == DISLIKE_ON) {
                setButton(holder.btnDislike, DISLIKE_OFF)
                // remove dislike form db and refresh
                db.removeFromLike(likeId!!)
                dislikes--
                holder.txtDislikeCount.text = dislikes.toString()

            } else if (holder.btnDislike.tag == DISLIKE_OFF) {
                setButton(holder.btnDislike, DISLIKE_ON)
                // add dislike to db and remove like if prev liked and todo: refresh
                db.addDislike(username, postId)
                likeId = db.getLikeId(username, postId)
                dislikes++
                holder.txtDislikeCount.text = dislikes.toString()
                setButton(holder.btnLike, LIKE_OFF)

            }
        }

        holder.btnMoreOption.setOnClickListener { currentView ->
            val popUp = PopupMenu(context, currentView)

            if(username == dataset[position].username){
                popUp.menu.add("Edit")
                popUp.menu.add("Delete")
            }
            popUp.menu.add("Analytics")
            popUp.show()

            popUp.setOnMenuItemClickListener {
                when (it.title) {
                    "Delete" -> delete(position)
                    "Edit" -> edit(position)
                    "Analytics" -> openAnalytics(position)
                }
                true
            }
        }


    }

    private fun openAnalytics(position: Int) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.analytics_drawer_layout)

        val txtLike = dialog.findViewById<TextView>(R.id.txtLikeCount)
        val txtDislike = dialog.findViewById<TextView>(R.id.txtDislikeCount)
        val txtLikeness = dialog.findViewById<TextView>(R.id.txtLikeness)
        val btnRefresh = dialog.findViewById<ImageButton>(R.id.btnRefresh)

        val analytics = getAnalyticValues(position)

        txtLike.text = analytics["likes"]
        txtDislike.text = analytics["dislikes"]
        txtLikeness.text = analytics["ratio"]


        btnRefresh.setOnClickListener{
            val analytic = getAnalyticValues(position)

            txtLike.text = analytic["likes"]
            txtDislike.text = analytic["dislikes"]
            txtLikeness.text = analytic["ratio"]
        }


        dialog.show()
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.window?.setGravity(Gravity.BOTTOM)
    }

    private fun getAnalyticValues(position: Int): Map<String, String>{
        val likes = db.getLikeCount(dataset[position].postID).toString()
        val dislikes = db.getDislikeCount(dataset[position].postID).toString()
        val ratio: Double = if (dislikes.toInt() != 0) {
            likes.toDouble() / dislikes.toDouble()
        } else {
            likes.toDouble() // Handle division by zero scenario
        }
        val ratioString = String.format("%.2f", ratio)
        return mapOf("likes" to likes, "dislikes" to dislikes, "ratio" to ratioString)
    }


    private fun openComments(postID: Int) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.comment_drawer_layout)


        //set the adapter for commments here
        val rvComment = dialog.findViewById<RecyclerView>(R.id.rvComment)
        rvComment.layoutManager = LinearLayoutManager(context)
        val comments = db.getComment10(postID)
        rvComment.adapter = CommentAdapter(comments, context)


        //posting comments
        val edtComment: EditText = dialog.findViewById(R.id.edtComment)
        val btnPostComent: ImageButton = dialog.findViewById(R.id.btnPostComment)

        btnPostComent.setOnClickListener {
            if (edtComment.text.toString() != "") {
                if (db.addComment(
                        userInfo["username"]!!,
                        edtComment.text.toString(),
                        postID,
                        null
                    ) != -1L
                ) {
                    edtComment.setText("")
                    rvComment.adapter = CommentAdapter(db.getComment10(postID), context)
                }

            }
        }




        dialog.show()
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.window?.setGravity(Gravity.BOTTOM)
    }

    private fun setButton(imgView: ImageView, resource: Int) {
        imgView.setImageResource(resource)
        imgView.tag = resource
    }

    private fun delete(position: Int) {
        if (db.deletePost(dataset[position].postID) == 1) {
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


    @SuppressLint("NotifyDataSetChanged")
    fun refreshRecyclerView(newData: MutableList<Post>) {
        dataset.clear()
        dataset.addAll(newData)
        notifyDataSetChanged()
    }

    override fun getItemCount() = dataset.size

}