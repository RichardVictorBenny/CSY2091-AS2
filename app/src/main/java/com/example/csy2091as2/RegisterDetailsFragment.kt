package com.example.csy2091as2

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.time.LocalDate
import java.util.Calendar

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RegisterDetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RegisterDetailsFragment : Fragment() {

    private val functions = Functions()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_register_details, container, false)

        //textfields and layouts
        val layFirstName: TextInputLayout = view.findViewById(R.id.inplayFirstName)
        val layLastName: TextInputLayout = view.findViewById(R.id.inplayLastName)
        val layEmail: TextInputLayout = view.findViewById(R.id.inplayEmail)
        val layDateOfBirth: TextInputLayout = view.findViewById(R.id.inplayDateOfBirth)
//        val layUserName: TextInputLayout = findViewById(R.id.inplayUsername)

        val edtFirstName: TextInputEditText = view.findViewById(R.id.inpedtFirstName)
        val edtMiddleName: TextInputEditText = view.findViewById(R.id.inpedtMiddleName)
        val edtLastName: TextInputEditText = view.findViewById(R.id.inpedtLastName)
        val edtEmail: TextInputEditText = view.findViewById(R.id.inpedtEmail)
        val edtDateOfBirth: TextInputEditText = view.findViewById(R.id.inpedtDateOfBirth)
//        val edtUserName: TextInputEditText = findViewById(R.id.inpedtUsername)
//        val edtPassword: TextInputEditText = findViewById(R.id.inpedtPassword)
        // TODO: way to populate data back into the textfields if went back }

        layDateOfBirth.setEndIconOnClickListener {
            showDatePickerDialog(edtDateOfBirth, view)
        }
        edtDateOfBirth.setOnClickListener{
            showDatePickerDialog(edtDateOfBirth, view)
        }

        val btnRegisterNext = view.findViewById<Button>(R.id.btnRegisterNext)



        btnRegisterNext.setOnClickListener {

            // TODO: add input validation

            //input validation.
            emptyCheck(layFirstName, edtFirstName)
            emptyCheck(layLastName, edtLastName)
            emptyCheck(layEmail, edtEmail)
            emptyCheck(layDateOfBirth, edtDateOfBirth)

            setErrorOnChange(layFirstName, edtFirstName)
            setErrorOnChange(layLastName, edtLastName)
            setErrorOnChange(layEmail, edtEmail)

            //date validation
            val dateInput = LocalDate.parse(functions.formatDate(edtDateOfBirth.text.toString()))
            valiateDate(dateInput, layDateOfBirth)

            //email validation
            validateEmail(edtEmail, layEmail)

            
            
            //final checks
            if(layFirstName.error==null && layLastName.error==null && layEmail.error==null && layDateOfBirth.error==null){
                val fragment = RegisterPasswordFragment()
                // TODO: pass the values to the next fragment.
                val transaction = activity?.supportFragmentManager?.beginTransaction()
                transaction?.replace(R.id.registerFragmentContainer, fragment)?.commit()
            }
        }

        return view
    }

        private fun showDatePickerDialog(edtDatePicker: TextInputEditText, view: View){

            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            var dateOfBirth:String = ""

            val datePickerDialog = DatePickerDialog(view.context, DatePickerDialog.OnDateSetListener { view: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                // Do something with the selected date
                dateOfBirth = "$dayOfMonth/${monthOfYear + 1}/$year"
                edtDatePicker.setText(dateOfBirth)

            }, year, month, day)

            datePickerDialog.show()
        }

    // TODO: move validations to a different Class.
    private fun emptyCheck(inputLayout: TextInputLayout, inputEditText: TextInputEditText){
        inputLayout.error = if(inputEditText.text.toString().isEmpty()) "Can't be empty" else null
    }
    private fun setErrorOnChange(inputLayout: TextInputLayout, editText: TextInputEditText) {
        editText.doOnTextChanged { text, start, before, count ->
            inputLayout.error = if (text.isNullOrBlank()) "Can't be empty" else null
            checkForSpecialChars(inputLayout, editText)
        }
    }

    private fun checkForSpecialChars(inputLayout: TextInputLayout, editText: TextInputEditText){
        inputLayout.error = if(editText.text.toString().contains("[!\"#$%&'()*+,-./:;\\\\<=>?@\\[\\]^_`{|}~]".toRegex())) "Invalid Character" else null
    }

    private fun valiateDate(inpDate: LocalDate, layout: TextInputLayout){
        val currentDate = LocalDate.now()

        if(!inpDate.isBefore(currentDate)){
            layout.error = "Invalid Date Entry"
        }
        if(currentDate.compareTo(inpDate)<6){
            layout.error = "Too Young"
        }
    }

    private fun validateEmail(editText: TextInputEditText, layout: TextInputLayout){
        editText.doOnTextChanged { text, start, before, count ->
            val email: String = editText.text.toString()
            layout.error = if(!Patterns.EMAIL_ADDRESS.matcher(text).matches()) "Invalid email" else null
        }

        val emailDomains = arrayOf("middlemore.co.uk", "my.middlemore.co.uk")
        val inputDomain = editText.text.toString().split("@")[1]

        layout.error = if(!emailDomains.contains(inputDomain)) "Invalid email" else null
    }







}