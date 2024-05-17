package com.example.csy2091as2

import android.R
import android.app.AlertDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.example.csy2091as2.Functions.Functions
import com.example.csy2091as2.Functions.Post
import com.example.csy2091as2.Functions.Validations
import com.example.csy2091as2.databinding.ActivityPostBinding
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class PostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPostBinding
    private lateinit var photo: File
    private lateinit var photoURI: Uri
    private val validaiton = Validations()
    private val db = DBHelper(this)
    private val functions = Functions()
    private lateinit var  username: String
    private lateinit var FILE_NAME: String
    private lateinit var imagePath: String
    private lateinit var imgFolder: File
    private var oldImg: File? = null


    companion object {
        val GALLERY_REQUEST_CODE = 100
        val CAMERA_REQUEST_CODE = 123
        var POST_ID = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

//       setting a locaiton for images to be stored.
        imgFolder = File(filesDir.absolutePath, "Pictures") //todo: make the folder if not exists
//getting username
        val userInfo = Functions.getUserinfo(this)
        username = userInfo["username"]!!
        val userType = userInfo["usertype"]!!


//        checking if the post is being edited or newly made
        if(intent.getIntExtra("postId", 0) != 0){
            POST_ID = intent.getIntExtra("postId", 0)
            val post: Post = db.getPostSingle(POST_ID)[0]
            var imageFile: File? = null
            val imageBitmap = BitmapFactory.decodeFile(post.postImgPath)

            if(post.postImgPath == ""){
                FILE_NAME = functions.generateRandomName(username)
                imageFile = File(imgFolder, "placeholder.jpg")
                binding.imgPost.setImageURI(imageFile.toUri())
            } else{
                imageFile = File(post.postImgPath)
                oldImg = imageFile
                FILE_NAME = imageFile.name
                binding.imgPost.setImageBitmap(imageBitmap)
            }
            imagePath = imageFile.absolutePath
            Log.d("TAG", "onCreate: $imagePath")


            binding.inpedtPostDesp.setText(post.txtDesp)
        } else {
            FILE_NAME = functions.generateRandomName(username)
            imagePath = File(imgFolder, "placeholder.jpg").absolutePath
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
                        imagePath = File(imgFolder, "placeholder.jpg").absolutePath
                        binding.imgPost.setImageURI(File(imgFolder, "placeholder.jpg").toUri())
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
//                var imagePath: String? = ""

//                goes in here only if any change is made to image.
                try {

                    val inputStream = contentResolver.openInputStream(photoURI)
                    var outputStream : OutputStream? = null

                    if (imgFolder.exists()) {
                        val newImage = File(imgFolder, FILE_NAME)

//                        deleting previous image if a new image is selected.
                        if(oldImg != null && oldImg?.name != newImage.name){
                            oldImg?.delete()
                        }

//                        copying image to internal storage.
                        // TODO: to be changed into a blob
                        outputStream = FileOutputStream(newImage)
                        inputStream?.use { input ->
                            outputStream.use { output ->
                                input.copyTo(output)
                            }
                        }
                        imagePath = newImage.absolutePath


                    }

                } catch (e: UninitializedPropertyAccessException) {
//                    Log.d("TAG", e.toString())
                } catch (e: Exception) {
//                    Log.d("TAG", e.toString())
                }

//                setting imagepath back to empty if no image is added
                if(imagePath == File(imgFolder, "placeholder.jpg").absolutePath){
                    imagePath = ""
                }

                if(db.savePost(POST_ID, username, desc, imagePath, userType) != -1L){
                    Toast.makeText(this, "Post sent for review", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "unsuccesfull", Toast.LENGTH_SHORT).show()
                }


                startActivity(Intent(this, MainActivity::class.java))

                finish()
                // TODO: go to account page or home page to see the post
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
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    /**
     * function to open a camera intent to get live images
     * a file path is provided to the camera intent for saving a high quality image.
     */
    private fun selectFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        photo = getPhoto(FILE_NAME)

        //need to wrap inside FileProvider because private file uri can't be shared between process
        val fileProvider =
            FileProvider.getUriForFile(this, "com.example.csy2091as2.fileprovider", photo)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
        startActivityForResult(intent, CAMERA_REQUEST_CODE)
    }

    /**
     * function makes a temp image file at a temp location.
     */
    private fun getPhoto(fileName: String): File {
        val storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(fileName, ".jpg", storageDirectory)
    }

    /**
     * function handles the retrieval of the image for both the gallery intent and camera intent.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == GALLERY_REQUEST_CODE) {
                binding.imgPost.setImageURI(data?.data)
                if (data != null) {
                    photoURI = data.data!!
                }
            }
            if (requestCode == CAMERA_REQUEST_CODE) {
                val imageBitmap = BitmapFactory.decodeFile(photo.absolutePath)
                photoURI = photo.toUri()
                binding.imgPost.setImageBitmap(imageBitmap)
            }
        }

    }


}