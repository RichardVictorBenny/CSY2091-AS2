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
        if(inputLayout.error == null){
            inputLayout.error = if(inputEditText.text.toString().isEmpty()) "Can't be empty" else null
//            Log.d("TAG", "emptyCheck: ${inputLayout.error}")
        }
    }
    fun setErrorOnChange(inputLayout: TextInputLayout, editText: TextInputEditText) {
        editText.doOnTextChanged { text, start, before, count ->

                checkForSpecialChars(inputLayout, text)
                inputLayout.error = if (text.isNullOrBlank()) "Can't be empty" else null


        }
    }

    fun checkForSpecialChars(inputLayout: TextInputLayout, text: CharSequence?){
//        Log.d("TAG", "checkForSpecialChars: starrt")
        if (text != null && inputLayout.error == null) {
//            Log.d("TAG", text.toString())
            inputLayout.error = if(text.contains("[!\"#$%&'()*+,-./:;\\\\<=>?@\\[\\]^_`{|}~0123456789]".toRegex())) "Invalid Character" else null
        }
//        Log.d("TAG", "checkForSpecialChars: ${inputLayout.error}")
    }

    fun checkForSpecialChars(inputLayout: TextInputLayout, text: String){
        if(inputLayout.error == null){
            inputLayout.error = if(text.contains("[!\"#$%&'()*+,-./:;\\\\<=>?@\\[\\]^_`{|}~0123456789]".toRegex())) "Invalid Character" else null
        }

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

    fun validateEmail(editText: TextInputEditText, layout: TextInputLayout): Boolean {


        val emailDomains = arrayOf("middlemore.co.uk", "my.middlemore.co.uk")
        val inputDomain = editText.text.toString().split("@")[1]

        if((!emailDomains.contains(inputDomain))){
            Log.d("TAG", "validateEmail: $emailDomains")
            layout.error = "invalid email"
//            Log.d("TAG", "validateEmail: $inputDomain")
        } else{
            layout.error = null
        }
        return emailDomains.contains(inputDomain)
    }

    fun validateEmailOnChange(editText: TextInputEditText, layout: TextInputLayout){
        editText.doOnTextChanged { text, start, before, count ->
            val email: String = editText.text.toString()
            layout.error =
                if (!Patterns.EMAIL_ADDRESS.matcher(text).matches()) "Invalid email" else null
        }
        validateEmail(editText, layout)
    }



}