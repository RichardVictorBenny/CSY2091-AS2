package com.example.csy2091as2.Functions

import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.time.LocalDate

class Validations {

    fun emptyCheck(inputLayout: TextInputLayout, inputEditText: TextInputEditText){
        inputLayout.error = if(inputEditText.text.toString().isEmpty()) "Can't be empty" else null
    }
    fun setErrorOnChange(inputLayout: TextInputLayout, editText: TextInputEditText) {
        editText.doOnTextChanged { text, start, before, count ->
            inputLayout.error = if (text.isNullOrBlank()) "Can't be empty" else null
            checkForSpecialChars(inputLayout, text)
        }
    }

    fun checkForSpecialChars(inputLayout: TextInputLayout, text: CharSequence?){
//        Log.d("TAG", "checkForSpecialChars: starrt")
        if (text != null) {
//            Log.d("TAG", text.toString())
            inputLayout.error = if(text.contains("[!\"#$%&'()*+,-./:;\\\\<=>?@\\[\\]^_`{|}~]".toRegex())) "Invalid Character" else null
        }
//        Log.d("TAG", "checkForSpecialChars: end")
    }

    fun checkForSpecialChars(inputLayout: TextInputLayout, text: String){
        inputLayout.error = if(text.contains("[!\"#$%&'()*+,-./:;\\\\<=>?@\\[\\]^_`{|}~]".toRegex())) "Invalid Character" else null
    }

    fun valiateDate(inpDate: LocalDate, layout: TextInputLayout){
        val currentDate = LocalDate.now()

        if(!inpDate.isBefore(currentDate)){
            layout.error = "Invalid Date Entry"
        }
        if(currentDate.compareTo(inpDate)<6){
            layout.error = "Too Young"
        }
    }

    fun validateEmail(editText: TextInputEditText, layout: TextInputLayout){
        editText.doOnTextChanged { text, start, before, count ->
            val email: String = editText.text.toString()
            layout.error = if(!Patterns.EMAIL_ADDRESS.matcher(text).matches()) "Invalid email" else null
        }

        val emailDomains = arrayOf("middlemore.co.uk", "my.middlemore.co.uk")
        val inputDomain = editText.text.toString().split("@")[1]

        layout.error = if(!emailDomains.contains(inputDomain)) "Invalid email" else null
    }


}