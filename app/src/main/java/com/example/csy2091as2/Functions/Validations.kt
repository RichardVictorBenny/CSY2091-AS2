package com.example.csy2091as2.Functions

import android.util.Patterns
import androidx.core.widget.doOnTextChanged
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.time.LocalDate

/**
 * class that handles all the validation
 */
class Validations {

    /**
     * check if no value is provided and sets appropriate errors
     */
    fun emptyCheck(inputLayout: TextInputLayout, inputEditText: TextInputEditText){
        if(inputLayout.error == null){
            inputLayout.error = if(inputEditText.text.toString().isEmpty()) "Can't be empty" else null
        }
    }

    /**
     * checks if no value is provided after starting to type
     */
    fun setErrorOnChange(inputLayout: TextInputLayout, editText: TextInputEditText) {
        editText.doOnTextChanged { text, start, before, count ->


                inputLayout.error = if (text.isNullOrBlank()) "Can't be empty" else null
            checkForSpecialCharsNum(inputLayout, text)

        }
    }


    /**
     * checks if no value is provided after starting to type
     */
    fun setErrorOnChange(inputLayout: TextInputLayout, editText: TextInputEditText, text1: String) {
        editText.doOnTextChanged { text, start, before, count ->


            inputLayout.error = if (text.isNullOrBlank()) "Can't be empty" else null
            checkForSpecialChars(inputLayout, text.toString())

        }
    }

    /**
     * checks if only the allowed characters are entered for a charSequence
     */
    fun checkForSpecialCharsNum(inputLayout: TextInputLayout, text: CharSequence?){

        if (text != null && inputLayout.error == null) {

            inputLayout.error = if(text.contains("[!\"#$%&'()*+,-./:;\\\\<=>?@\\[\\]^_`{|}~0123456789]".toRegex())) "Invalid Character" else null
        }

    }

    /**
     * checks if only the allowed characters are entered
     * overloaded function
     */
    fun checkForSpecialCharsNum(inputLayout: TextInputLayout, text: String){
        if(inputLayout.error == null){
            inputLayout.error = if(text.contains("[!\"#$%&'()*+,-./:;\\\\<=>?@\\[\\]^_`{|}~0123456789]".toRegex())) "Invalid Character" else null
        }

    }

    /**
     * checks if only the allowed characters are entered
     */
    fun checkForSpecialChars(inputLayout: TextInputLayout, text: String){
        if(inputLayout.error == null){
            inputLayout.error = if(text.contains("[!\"#$%&'()*+,-./:;\\\\<=>?@\\[\\]^_`{|}]".toRegex())) "Invalid Character" else null
        }

    }

    /**
     * validated the date. has to be within the range of the secondary school student and tutor
     */
    fun valiateDate(inpDate: LocalDate, layout: TextInputLayout){
        val currentDate = LocalDate.now()

        if(!inpDate.isBefore(currentDate)){
            layout.error = "Invalid Date Entry"
        }else {
            layout.error = null
        }
        if(currentDate.compareTo(inpDate)<6){
            layout.error = "Too Young"
        } else{
            layout.error = null
        }
    }

    /**
     * checks if the email used is the school email
     */
    fun validateEmail(editText: TextInputEditText, layout: TextInputLayout): Boolean {


        val emailDomains = arrayOf("middlemore.co.uk", "my.middlemore.co.uk")
        var inputDomain = ""
        try{
            inputDomain = editText.text.toString().split("@")[1]

            if((!emailDomains.contains(inputDomain))){
                layout.error = "invalid email"
            } else{
                layout.error = null
            }
        } catch (_: Exception){
        }


        return emailDomains.contains(inputDomain)
    }

    fun validateGivenEmail(text: String): Boolean {


        val emailDomains = arrayOf("middlemore.co.uk", "my.middlemore.co.uk")
        val inputDomain = text.split("@")[1]

        return emailDomains.contains(inputDomain)
    }

    /**
     * checks if the email is in the correct format
     */
    fun validateEmailOnChange(editText: TextInputEditText, layout: TextInputLayout){
        editText.doOnTextChanged { text, start, before, count ->
            val email: String = editText.text.toString()
            layout.error =
                if (!Patterns.EMAIL_ADDRESS.matcher(text).matches()) "Invalid email" else null
        }
        validateEmail(editText, layout)
    }



}