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
import android.widget.Toast
import com.example.csy2091as2.Functions.Functions
import com.example.csy2091as2.Functions.Validations
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.time.LocalDate
import java.util.Calendar


/**
 * A simple [Fragment] subclass.
 * Use the [RegisterDetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RegisterDetailsFragment : Fragment() {

    private val functions = Functions()
    private val validations = Validations()
    private lateinit var db: DBHelper


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_register_details, container, false)
        val data = arguments

        if (data != null && data.getString("toUpdate") != "") {

            view.findViewById<TextView>(R.id.txtRegisterTagline).visibility = View.GONE
            view.findViewById<TextView>(R.id.txtRegisterWarning).visibility = View.GONE
            view.findViewById<Button>(R.id.btnRegisterNext).setText("Save")
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
        if (Functions.getUserinfo(requireContext())["usertype"]!! == "admin") {
            layUserType.visibility = View.VISIBLE
        }

//        Log.d("TAG", "onCreateView: only works if user is admin: ${Functions.getUserinfo(requireContext())["usertype"]!!}")

        //populate data back into the textfields if went back }

        if (data != null) {
            edtFirstName.setText(data.getString("firstname"))
            edtMiddleName.setText(data.getString("middlename"))
            edtLastName.setText(data.getString("lastname"))
            edtEmail.setText(data.getString("email"))
            edtDateOfBirth.setText(data.getString("dateofbirth"))
            edtUsername.setText(data.getString("username"))
            inptxtAutocomplete.setText(data.getString("usertype"), false)

        }


        layDateOfBirth.setEndIconOnClickListener {
            showDatePickerDialog(edtDateOfBirth, view)
        }
        edtDateOfBirth.setOnClickListener {
            showDatePickerDialog(edtDateOfBirth, view)
        }

        val btnRegisterNext = view.findViewById<Button>(R.id.btnRegisterNext)


        validations.setErrorOnChange(layFirstName, edtFirstName)
        validations.setErrorOnChange(layLastName, edtLastName)
        validations.validateEmailOnChange(edtEmail, layEmail)
        btnRegisterNext.setOnClickListener {


            //input validation.
            validations.emptyCheck(layFirstName, edtFirstName)
            validations.emptyCheck(layLastName, edtLastName)
            validations.emptyCheck(layEmail, edtEmail)
            validations.emptyCheck(layDateOfBirth, edtDateOfBirth)
            validations.emptyCheck(layUsername, edtUsername)

            //character validation
            validations.checkForSpecialCharsNum(layFirstName, edtFirstName.text.toString())
            validations.checkForSpecialCharsNum(layLastName, edtLastName.text.toString())
            validations.checkForSpecialCharsNum(layMiddleName, edtMiddleName.text.toString())
            validations.checkForSpecialChars(layUsername, edtUsername.text.toString())
            validations.setErrorOnChange(layUsername, edtUsername)

            validations.checkForSpecialChars(layUsername, edtUsername.text.toString())

            //date validation
            if (edtDateOfBirth.text.toString().isNotEmpty()) {
                //date variable defined here
                val dateInput =
                    LocalDate.parse(functions.formatDate(edtDateOfBirth.text.toString()))
                validations.valiateDate(dateInput, layDateOfBirth)

            }
            validations.validateEmailOnChange(edtEmail, layEmail)
            //email validation
            if (edtEmail.text.toString().isNotEmpty() && layEmail.error == null) {
                try {
                    if (layEmail.error == null && edtEmail.text.toString() == data?.getString("email")) {
                        layEmail.error = null
                    } else if (layEmail.error == null && db.emailCheck(edtEmail.text.toString())) {
                        layEmail.error = "Email already in use"
                    } else {
                        layEmail.error = null
                    }
                } catch (_: CursorIndexOutOfBoundsException) {
                } catch (exception: Exception) {
                    layEmail.error = "Invalid Email"
                }

            }
            if (layUsername.error == null) {

//                    Log.d("TAG", "onCreateView: ${data?.getString("username")}")
                try {
                    if (layUsername.error == null && edtUsername.text.toString() == data?.getString(
                            "username"
                        )
                    ) {
                        layUsername.error = null
                    } else if (db.userCheck(edtUsername.text.toString())) {
                        layUsername.error = "Username already in use"
                    } else {
                        layUsername.error = null
                    }
                } catch (_: Exception) {
                }
// username validation

            }


            //final checks
            if (layUsername.error == null && layFirstName.error == null && layLastName.error == null && layEmail.error == null && layDateOfBirth.error == null) {
                val bundle = Bundle()
                bundle.putString("firstname", edtFirstName.text.toString())
                bundle.putString("middlename", edtMiddleName.text.toString())
                bundle.putString("lastname", edtLastName.text.toString())
                bundle.putString("email", edtEmail.text.toString())
                bundle.putString("dateofbirth", edtDateOfBirth.text.toString())
                bundle.putString("username", edtUsername.text.toString())
                bundle.putString(
                    "usertype",
                    if (inptxtAutocomplete.text.isEmpty()) "student" else {
                        inptxtAutocomplete.text.toString()
                    }
                )
                // checking if the data is being updated or not
                if (data != null && data.getString("toUpdate") != "") {
                    bundle.putString("toUpdate", data.getString("toUpdate"))
                    //updating details of a user
                    try {
                        if (db.updateUser(data.getString("toUpdate")!!, bundle)) {
                            Toast.makeText(
                                requireContext(),
                                "User Details updated",
                                Toast.LENGTH_SHORT
                            )
                                .show()

                            // clearing the current user
                            val parent = requireActivity() as UpdateInfoActivity
                            parent.clearSearch(
                                activity?.supportFragmentManager?.beginTransaction(),
                                this
                            )


                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Action unsuccessful",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    } catch (_: Exception) {
                        Toast.makeText(requireContext(), "action unsuccessful", Toast.LENGTH_SHORT)
                            .show()
                    }


                } else {

                    //adding a new user
                    val fragment = RegisterPasswordFragment()

                    fragment.arguments = bundle
                    val transaction = activity?.supportFragmentManager?.beginTransaction()
                    transaction?.replace(R.id.registerFragmentContainer, fragment)?.commit()
                }
            }
        }

        return view
    }

    /**
     * opens the dataPicker
     */
    private fun showDatePickerDialog(edtDatePicker: TextInputEditText, view: View) {

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        var dateOfBirth: String = ""

        val datePickerDialog = DatePickerDialog(
            view.context,
            DatePickerDialog.OnDateSetListener { view: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                dateOfBirth = "$dayOfMonth/${monthOfYear + 1}/$year"
                edtDatePicker.setText(dateOfBirth)

            },
            year,
            month,
            day
        )

        datePickerDialog.show()
    }


}