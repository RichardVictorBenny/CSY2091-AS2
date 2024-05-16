package com.example.csy2091as2.Admin

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.csy2091as2.CommentAdapter
import com.example.csy2091as2.DBHelper
import com.example.csy2091as2.Functions.Functions
import com.example.csy2091as2.Functions.Hashing
import com.example.csy2091as2.R
import com.example.csy2091as2.RegisterActivity
import com.example.csy2091as2.UpdateInfoActivity
import com.example.csy2091as2.databinding.ActivityAdminBinding
import com.example.csy2091as2.databinding.ActivityPostBinding

class AdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminBinding
    private lateinit var db: DBHelper
    private lateinit var userInfo: Map<String, String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userInfo = Functions.getUserinfo(this)
        db = DBHelper(this)

        binding.layAddStudent.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.layDeleteStudent.setOnClickListener {
            openDeleteDrawer()
        }

        binding.layUpdatePassword.setOnClickListener {
            openUpdatePasswordDrawer()
        }

        binding.layUpdateInfo.setOnClickListener{
            val activity = Intent(this, UpdateInfoActivity::class.java)
            startActivity(activity)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun openDeleteDrawer() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.delete_drawer_layout)

        val edtStudent: EditText = dialog.findViewById(R.id.edtDeleteStudent)
        val btnSeach: Button = dialog.findViewById(R.id.btnDeleteSearch)
        val llDetails: LinearLayout = dialog.findViewById(R.id.llDeleteDetails)
        val txtName: TextView = dialog.findViewById(R.id.txtDeleteFullName)
        val txtEmail: TextView = dialog.findViewById(R.id.txtDeleteEmail)
        val txtDob: TextView = dialog.findViewById(R.id.txtDeleteDOB)
        val btnDelete: Button = dialog.findViewById(R.id.btnDeleteStudent)
        var student = ""

        btnSeach.setOnClickListener {
            student = edtStudent.text.toString()

            if (Patterns.EMAIL_ADDRESS.matcher(student).matches()) {
                try {
                    student = db.fetchUsername(student)!!
                } catch (_: Exception) {
                    Toast.makeText(this, "Email does not exist", Toast.LENGTH_SHORT).show()
                }
            }
            if (student != userInfo.get("username")) { //check to prevent self deletion

                try {
                    val studentInfo = db.fetchUser(student)
                    llDetails.visibility = View.VISIBLE
                    txtName.setText("${studentInfo.firstName} ${studentInfo.lastName}")
                    txtEmail.setText(studentInfo.email)
                    txtDob.setText(studentInfo.dateOfBirth)
                } catch (_: Exception) {
                    Toast.makeText(this, "User does not exist", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Cannot delete yourself", Toast.LENGTH_SHORT).show()
            }

        }

        btnDelete.setOnClickListener {
            try {
                if (llDetails.visibility == View.VISIBLE) {
                    if (db.deleteUser(student)) {
                        llDetails.visibility = View.GONE
                        Toast.makeText(this, "$student is deleted", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
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

    private fun openUpdatePasswordDrawer() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.update_password_drawer_layout)

        val edtStudent: EditText = dialog.findViewById(R.id.edtUpdateStudent)
        val llUpdate: LinearLayout = dialog.findViewById(R.id.llUpdatePassword)
        val txtName: TextView = dialog.findViewById(R.id.txtUpdateFullName)
        val txtEmail: TextView = dialog.findViewById(R.id.txtUpdateEmail)
        val txtDob: TextView = dialog.findViewById(R.id.txtUpdateDOB)
        val edtNewPassword: EditText = dialog.findViewById(R.id.edtNewPassword)
        val btnGenerate: Button = dialog.findViewById(R.id.btnGeneratePassword)
        val btnCopy: Button = dialog.findViewById(R.id.btnCopyPassword)
        val btnUpdate: Button = dialog.findViewById(R.id.btnUpdatePassword)
        val btnSearch: Button = dialog.findViewById(R.id.btnUpdateSearch)
        var student = ""

        btnSearch.setOnClickListener {
            student = edtStudent.text.toString()

            if (Patterns.EMAIL_ADDRESS.matcher(student).matches()) {
                try {
                    student = db.fetchUsername(student)!!
                } catch (_: Exception) {
                    Toast.makeText(this, "Email does not exist", Toast.LENGTH_SHORT).show()
                }
            }

            try {
                val studentInfo = db.fetchUser(student)
                llUpdate.visibility = View.VISIBLE
                txtName.setText("${studentInfo.firstName} ${studentInfo.lastName}")
                txtEmail.setText(studentInfo.email)
                txtDob.setText(studentInfo.dateOfBirth)
            } catch (_: Exception) {
                Toast.makeText(this, "User does not exist", Toast.LENGTH_SHORT).show()
            }
        }

        btnGenerate.setOnClickListener {
            if (edtNewPassword.text != null && llUpdate.visibility == View.VISIBLE) {
                val function = Functions()
                val input = function.generateRandomName(student)
                val newPassword= if (input.endsWith(".jpg")) {
                    input.substring(0, input.length - 4)
                } else {
                    input
                }
                edtNewPassword.setText(newPassword)
            }
        }

        btnCopy.setOnClickListener {

        }

        btnUpdate.setOnClickListener {
            try {
                if (edtNewPassword.text != null && llUpdate.visibility == View.VISIBLE) {
                    if (db.updatePassword(
                            student,
                            Hashing.doHashing(edtNewPassword.text.toString(), student)
                        )
                    ) {
                        llUpdate.visibility = View.GONE
                        Toast.makeText(this, "Password updated", Toast.LENGTH_SHORT).show()
                        if (student == userInfo.get("username")) {
                            Functions.logout(this)
                            finish()
                        }
                    }
                } else {
                    Toast.makeText(this, "No new Password set", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
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
}