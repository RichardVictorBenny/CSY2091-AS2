package com.example.csy2091as2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.csy2091as2.Functions.Post
import com.example.csy2091as2.databinding.ActivityContentModerationBinding


class ContentModerationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityContentModerationBinding
    private lateinit var db: DBHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContentModerationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DBHelper(this)

        val rvHomeFrag = binding.rvContentModeration
        rvHomeFrag.layoutManager = LinearLayoutManager(this)
        val posts :MutableList<Post>? = when(intent.getStringExtra("modType")){
            "toApprove" -> db.getPostsToApprove()
            "declined" -> db.getPostsDecline()
            else -> {null}
        }
        rvHomeFrag.adapter = PostFragAdapter(posts!!,this)
    }
}