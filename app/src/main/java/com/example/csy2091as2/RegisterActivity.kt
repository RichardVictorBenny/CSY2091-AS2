package com.example.csy2091as2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.app.DatePickerDialog
import android.widget.Button
import android.widget.DatePicker
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.util.*

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val db = DBHelper(this)
        val functions = Functions()

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.registerFragmentContainer, RegisterDetailsFragment()).commit()

//        val layFirstName: TextInputLayout = findViewById()
//
//        val edtFirstName: TextInputEditText = findViewById(R.id.inpedtFirstName)
//        val edtMiddleName: TextInputEditText = findViewById(R.id.inpedtMiddleName)
//        val edtLastName: TextInputEditText = findViewById(R.id.inpedtLastName)
//        val edtEmail: TextInputEditText = findViewById(R.id.inpedtEmail)
//        val edtUserName: TextInputEditText = findViewById(R.id.inpedtUsername)
//        val edtPassword: TextInputEditText = findViewById(R.id.inpedtPassword)
//
//        val btnRegister: Button = findViewById(R.id.btnRegister)
//
//
//
//        val edtDatePicker = findViewById<TextInputEditText>(R.id.inpedtDateOfBirth)
//        var dateOfBirth :String = ""
//
//        //TODO: setting the domain for email
//
//
//        edtDatePicker.setOnClickListener {
//            showDatePickerDialog(edtDatePicker)
//        }
//
//        btnRegister.setOnClickListener {
//
//            if(edtFirstName.text.toString() == ""){
//                Toast.makeText(this, "firstname error", Toast.LENGTH_SHORT).show()
//                edtFirstName.error = "invalid firstname"
//            } else{
//                edtFirstName.error = null
//            }
//
//
//            // TODO: data validation and adding th
//            val firstName:String = edtFirstName.text.toString()
//            val middleName: String = edtMiddleName.text.toString()
//            val lastName: String = edtLastName.text.toString()
//            val email: String = edtEmail.text.toString()
//            val dateOfBirth: String = functions.formatDate(edtDatePicker.text.toString())
//            val userName: String = edtUserName.text.toString()
//            val password: String = edtPassword.text.toString()
//
//            val result:Array<Long> = db.addUser(userName, firstName, middleName, lastName, email, dateOfBirth, password)
            
//            if(result[0]>0){
//                Toast.makeText(this, "User Added", Toast.LENGTH_SHORT).show()
//            } else if(result[1]>0){
//                Toast.makeText(this, "User Authenticated", Toast.LENGTH_SHORT).show()
//            } else{
//                Toast.makeText(this, "Unsuccessful", Toast.LENGTH_SHORT).show()
//            }


//        }
    }

//    private fun showDatePickerDialog(edtDatePicker: TextInputEditText){
//        val calendar = Calendar.getInstance()
//        val year = calendar.get(Calendar.YEAR)
//        val month = calendar.get(Calendar.MONTH)
//        val day = calendar.get(Calendar.DAY_OF_MONTH)
//        var dateOfBirth:String = ""
//
//        val datePickerDialog = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
//            // Do something with the selected date
//            dateOfBirth = "$dayOfMonth/${monthOfYear + 1}/$year"
//            edtDatePicker.setText(dateOfBirth)
//
//        }, year, month, day)
//
//        datePickerDialog.show()
//    }
}
