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
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.net.toFile
import androidx.core.net.toUri
import com.example.csy2091as2.Functions.Validations
import com.example.csy2091as2.databinding.ActivityPostBinding
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.URI

class PostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPostBinding
    private lateinit var photo: File
    private lateinit var photoURI: Uri
    private val validaiton = Validations()


    companion object {
        val GALLERY_REQUEST_CODE = 100
        val CAMERA_REQUEST_CODE = 123
        val FILE_NAME = "photo.jpg" /* TODO: name should be randomissed*/
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imgPost.setOnClickListener {
            showOptionDialogBox()
        }

        binding.btnPost.setOnClickListener {
            validaiton.emptyCheck(binding.inplayPostDesp, binding.inpedtPostDesp)

            if (binding.inplayPostDesp.error == null) {
                try {
                    val inputStream = contentResolver.openInputStream(photoURI)
                    val imgFolder = File(filesDir.absolutePath, "Pictures")
                    if (imgFolder.exists()) {
                        val outputStream = FileOutputStream(File(imgFolder, FILE_NAME))

                        inputStream?.use { input ->
                            outputStream.use { output ->
                                input.copyTo(output)
                            }
                        }
                    }
                } catch (_: UninitializedPropertyAccessException) {
                } catch (_: Exception) {
                }


                val username = intent.getStringExtra("username")
                Toast.makeText(this, "Post added", Toast.LENGTH_SHORT).show()

                // TODO: make database
                // TODO: add the username, datetime, desc, imagePath, postID, tokenOfApproval
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