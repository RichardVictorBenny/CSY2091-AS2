package com.example.csy2091as2

import android.app.DatePickerDialog
import android.database.CursorIndexOutOfBoundsException
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.DatePicker
import android.widget.TextView
import com.example.csy2091as2.Functions.Functions
import com.example.csy2091as2.Functions.Validations
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
    private val validations = Validations()
    private lateinit var db: DBHelper


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_register_details, container, false)
        val data = arguments

        if(data != null && data.getString("toUpdate") != ""){

            view.findViewById<TextView>(R.id.txtRegisterTagline).visibility = View.GONE
            view.findViewById<TextView>(R.id.txtRegisterWarning).visibility = View.GONE
        }



        db = DBHelper(requireContext())

        //populating the drop down menu
        val options = arrayOf("student", "admin")
        val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, options)
        val inptxtAutocomplete = view.findViewById<AutoCompleteTextView>(R.id.inpedtUserType)
        inptxtAutocomplete.setAdapter(adapter)





        //textfields and layouts
        val layFirstName: TextInputLayout = view.findViewById(R.id.inplayFirstName)
        val layMiddleName: TextInputLayout = view.findViewById(R.id.inplayMiddleName)
        val layLastName: TextInputLayout = view.findViewById(R.id.inplayLastName)
        val layEmail: TextInputLayout = view.findViewById(R.id.inplayEmail)
        val layDateOfBirth: TextInputLayout = view.findViewById(R.id.inplayDateOfBirth)
        val layUsername: TextInputLayout = view.findViewById(R.id.inplayUsername)
        val layUserType: TextInputLayout = view.findViewById(R.id.inplayUserType)



//        val layUserName: TextInputLayout = findViewById(R.id.inplayUsername)

        val edtUsername: TextInputEditText = view.findViewById(R.id.inpedtUsername)
        val edtFirstName: TextInputEditText = view.findViewById(R.id.inpedtFirstName)
        val edtMiddleName: TextInputEditText = view.findViewById(R.id.inpedtMiddleName)
        val edtLastName: TextInputEditText = view.findViewById(R.id.inpedtLastName)
        val edtEmail: TextInputEditText = view.findViewById(R.id.inpedtEmail)
        val edtDateOfBirth: TextInputEditText = view.findViewById(R.id.inpedtDateOfBirth)
//        val edtUserName: TextInputEditText = findViewById(R.id.inpedtUsername)
//        val edtPassword: TextInputEditText = findViewById(R.id.inpedtPassword)


        //user type field visibility by usertype
        if(Functions.getUserinfo(requireContext())["usertype"]!! == "admin"){
            layUserType.visibility = View.VISIBLE
        }

//        Log.d("TAG", "onCreateView: only works if user is admin: ${Functions.getUserinfo(requireContext())["usertype"]!!}")

        //populate data back into the textfields if went back }

        if(data != null){
            edtFirstName.setText(data.getString("firstname"))
            edtMiddleName.setText(data.getString("middlename"))
            edtLastName.setText(data.getString("lastname"))
            edtEmail.setText(data.getString("email"))
            edtDateOfBirth.setText(data.getString("dateofbirth"))
            edtUsername.setText(data.getString("username"))
            inptxtAutocomplete.setText(data.getString("usertype"))

        }

        layDateOfBirth.setEndIconOnClickListener {
            showDatePickerDialog(edtDateOfBirth, view)
        }
        edtDateOfBirth.setOnClickListener{
            showDatePickerDialog(edtDateOfBirth, view)
        }

        val btnRegisterNext = view.findViewById<Button>(R.id.btnRegisterNext)


        validations.setErrorOnChange(layFirstName, edtFirstName)
        validations.setErrorOnChange(layLastName, edtLastName)
        btnRegisterNext.setOnClickListener {


            //input validation.
            validations.emptyCheck(layFirstName, edtFirstName)
            validations.emptyCheck(layLastName, edtLastName)
            validations.emptyCheck(layEmail, edtEmail)
            validations.emptyCheck(layDateOfBirth, edtDateOfBirth)

            validations.emptyCheck(layUsername, edtUsername)

            //character validation
            validations.checkForSpecialChars(layFirstName, edtFirstName.text.toString())
            validations.checkForSpecialChars(layLastName, edtLastName.text.toString())
            validations.checkForSpecialChars(layMiddleName, edtMiddleName.text.toString())
            validations.setErrorOnChange(layUsername, edtUsername)

            validations.checkForSpecialChars(layUsername, edtUsername.text.toString())

            //date validation
            if(edtDateOfBirth.text.toString().isNotEmpty()){
                //date variable defined here
                val dateInput = LocalDate.parse(functions.formatDate(edtDateOfBirth.text.toString()))
                validations.valiateDate(dateInput, layDateOfBirth)

            }

            //email validation
            if(edtEmail.text.toString().isNotEmpty()){
                try {
                    validations.validateEmail(edtEmail, layEmail)
                    if(db.emailCheck(edtEmail.text.toString())){
                        layEmail.error = "Email already in use"
                    } else{
                        layEmail.error = null
                    }
                } catch(_: CursorIndexOutOfBoundsException){

                }
                catch (exception: Exception){
                    layEmail.error = "Invalid Email"
                }

            }

            // username validation
            layUsername.error =
                if (db.userCheck(edtUsername.text.toString())) "Username taken" else null

            
            // checking if the data is being updated or not
            if(data != null && data.getString("toUpdate") != ""){
                Log.d("TAG", "onCreateView: ${data.getString("toUpdate")}")
            } else
                //final checks
            if(layUsername.error == null && layFirstName.error==null && layLastName.error==null && layEmail.error==null && layDateOfBirth.error==null){
                val fragment = RegisterPasswordFragment()
                val bundle = Bundle()
                bundle.putString("firstname", edtFirstName.text.toString())
                bundle.putString("middlename", edtMiddleName.text.toString())
                bundle.putString("lastname", edtLastName.text.toString())
                bundle.putString("email", edtEmail.text.toString())
                bundle.putString("dateofbirth", edtDateOfBirth.text.toString())
                bundle.putString("username", edtUsername.text.toString())
                bundle.putString("usertype", if(inptxtAutocomplete.text.isEmpty()) "student" else {inptxtAutocomplete.text.toString()})

                fragment.arguments = bundle
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
                dateOfBirth = "$dayOfMonth/${monthOfYear + 1}/$year"
                edtDatePicker.setText(dateOfBirth)

            }, year, month, day)

            datePickerDialog.show()
        }

    // TODO: move validations to a different Class.








}