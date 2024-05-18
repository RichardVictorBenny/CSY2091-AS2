package com.example.csy2091as2

import android.R
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.example.csy2091as2.Functions.Functions
import com.example.csy2091as2.Functions.Post
import com.example.csy2091as2.Functions.Validations
import com.example.csy2091as2.databinding.ActivityPostBinding
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class PostActivity : AppCompatActivity() {
    private lateinit var photo: File
    private lateinit var binding: ActivityPostBinding
    private lateinit var photoURI: Uri //to keep
    private val validaiton = Validations()
    private val db = DBHelper(this)
    private val functions = Functions()


    private  var image: ByteArray? = null

    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>
    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>


    companion object {
        var POST_ID = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val contentResolver = this.contentResolver

//       getting username
        val userInfo = Functions.getUserinfo(this)
        val username = userInfo["username"]!!
        val userType = userInfo["usertype"]!!


//        checking if the post is being edited or newly made
//        preload the text and image
        if(intent.getIntExtra("postId", 0) != 0){
            POST_ID = intent.getIntExtra("postId", 0)
            val post: Post = db.getPostSingle(POST_ID)[0]

            if(post.image == null){
                binding.imgPost.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.picture_frame)) //set placeholder here
            } else{
                binding.imgPost.setImageBitmap(Functions.byteArrayToBitmap(post.image)) // set img form post here
            }
            binding.inpedtPostDesp.setText(post.txtDesp)
        }






        binding.imgPost.setOnClickListener {
            showOptionDialogBox()
        }

        binding.imgPost.setOnLongClickListener{
            val popUp = PopupMenu(this, it)

            popUp.menu.add("Delete")
            popUp.show()

            popUp.setOnMenuItemClickListener {
                when(it.title){
                    "Delete" -> { //deleting image from folder if image is
                        binding.imgPost.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.star_big_on))
                        image = null


                    }
                }
                true
            }
            true
        }

        binding.btnPost.setOnClickListener {

            validaiton.emptyCheck(binding.inplayPostDesp, binding.inpedtPostDesp)

            if (binding.inplayPostDesp.error == null) {
                var desc: String = binding.inpedtPostDesp.text.toString()


                if(db.savePost(POST_ID, username, desc, userType, image) != -1L){
                    Toast.makeText(this, "Post sent for review", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "unsuccesfull", Toast.LENGTH_SHORT).show()
                }


                startActivity(Intent(this, MainActivity::class.java))

                finish()
                // TODO: go to account page or home page to see the post
            }
        }

        //setting the launchers for camera and gallery
        cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val imageBitmap = BitmapFactory.decodeFile(photo.absolutePath)
                binding.imgPost.setImageBitmap(imageBitmap)
                photoURI = photo.toUri()
                image = Functions.uriToByteArray(contentResolver, photoURI)!!
            }
        }
        galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                data?.let {
                    binding.imgPost.setImageURI(it.data)
                    photoURI = it.data!!
                    image = Functions.uriToByteArray(contentResolver, photoURI)!! //ByteArray needed here
                }
            }
        }



    }







        /**
     * function to give user the option to get images from gallery or camera
     */
    private fun showOptionDialogBox() {
        val options = arrayOf("Gallery", "Camera")

        val builder = AlertDialog.Builder(this)
        val adapter = ArrayAdapter<String>(this, R.layout.simple_list_item_1, options)
        builder.setTitle("Choose from")
        builder.setAdapter(adapter) { dialog, which ->
            handleOption(which)
            dialog.dismiss()
        }.setCancelable(false)

        val dialog = builder.create()
        dialog.show()
    }

    /**
     * extracted function to hande the option user picks
     */
    private fun handleOption(index: Int) {
        when (index) {
            0 -> selectFromGallery()
            1 -> selectFromCamera()
        }
    }

    /**
     * function that opens an intent to pick image from gallery
     */
    private fun selectFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        galleryLauncher.launch(intent)
    }

    /**
     * function to open a camera intent to get live images
     * a file path is provided to the camera intent for saving a high quality image.
     */
    private fun selectFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        photo = getPhoto()
        Log.d("TAG", "selectFromCamera: ${photo.absolutePath}")

        //need to wrap inside FileProvider because private file uri can't be shared between process
        val fileProvider =
            FileProvider.getUriForFile(this, "com.example.csy2091as2.fileprovider", photo)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)


        cameraLauncher.launch(intent)
    }

    /**
     * function makes a temp image file at a temp location.
     */
    private fun getPhoto(): File {
        val storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("image", ".jpg", storageDirectory)
    }


}