package com.example.csy2091as2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.app.DatePickerDialog
import android.widget.DatePicker
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import java.util.*

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val edtDatePicker = findViewById<TextInputEditText>(R.id.inpedtDateOfBirth)
        var dateOfBirth :String = ""

        edtDatePicker.setOnClickListener {
            showDatePickerDialog(edtDatePicker)
        }
    }

    private fun showDatePickerDialog(edtDatePicker: TextInputEditText){
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        var dateOfBirth:String = ""

        val datePickerDialog = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
            // Do something with the selected date
            dateOfBirth = "$dayOfMonth/${monthOfYear + 1}/$year"
            edtDatePicker.setText(dateOfBirth)

        }, year, month, day)

        datePickerDialog.show()
    }
}
