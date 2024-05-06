package com.example.csy2091as2

import android.R
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.net.toFile
import androidx.core.net.toUri
import com.example.csy2091as2.Functions.Functions
import com.example.csy2091as2.Functions.Validations
import com.example.csy2091as2.databinding.ActivityPostBinding
import com.github.drjacky.imagepicker.ImagePicker
import com.github.drjacky.imagepicker.constant.ImageProvider
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.OutputStream
import java.net.URI
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.math.log

class PostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPostBinding
    private lateinit var photo: File
    private lateinit var photoURI: Uri
    private val validaiton = Validations()
    private val db = DBHelper(this)
    private val functions = Functions()
    private lateinit var  username: String
    private lateinit var FILE_NAME: String


    companion object {
        val GALLERY_REQUEST_CODE = 100
        val CAMERA_REQUEST_CODE = 123
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //getting username
        val sharedPreferences = getSharedPreferences("currentUser", Context.MODE_PRIVATE)
        username = sharedPreferences.getString("username", null).toString()
        FILE_NAME = functions.generateRandomName(username)

        binding.imgPost.setOnClickListener {
            showOptionDialogBox()


        }


        binding.btnPost.setOnClickListener {
            validaiton.emptyCheck(binding.inplayPostDesp, binding.inpedtPostDesp)

            if (binding.inplayPostDesp.error == null) {
                var desc: String = binding.inpedtPostDesp.text.toString()
                var imagePath: String? = ""

                try {

                    val inputStream = contentResolver.openInputStream(photoURI)
                    val imgFolder = File(filesDir.absolutePath, "Pictures")
                    var outputStream : OutputStream? = null



                    if (imgFolder.exists()) {
                        outputStream = FileOutputStream(File(imgFolder, FILE_NAME))
                        inputStream?.use { input ->
                            outputStream.use { output ->
                                input.copyTo(output)
                            }
                        }
                        imagePath = File(imgFolder, FILE_NAME).absolutePath


                    }

                } catch (e: UninitializedPropertyAccessException) {
//                    Log.d("TAG", e.toString())
                } catch (e: Exception) {
//                    Log.d("TAG", e.toString())
                }

                if(db.addPost(username, desc!!, imagePath) != -1L){
                    Toast.makeText(this, "Post added", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "unsuccesful", Toast.LENGTH_SHORT).show()
                }




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