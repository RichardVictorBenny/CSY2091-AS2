package com.example.csy2091as2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.example.csy2091as2.Functions.Functions
import com.example.csy2091as2.Functions.Hashing
import com.example.csy2091as2.Functions.Validations
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

/**
 * A simple [Fragment] subclass.
 * Use the [RegisterPasswordFragment.newInstance] factory method to
 * create an instance of this fragment.
 * adds the a user to the db.
 */
class RegisterPasswordFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_register_password, container, false)
        val bundle = arguments
        val validation = Validations()
        val db = DBHelper(requireContext())

        val btnRegisterBack: Button = view.findViewById(R.id.btnRegisterBack)
        val btnRegisterAccount: Button = view.findViewById(R.id.btnRegisterAccount)

        val edtPassword: TextInputEditText = view.findViewById(R.id.inpedtPassword)
        val edtConfirmPassword: TextInputEditText = view.findViewById(R.id.inpedtConfirmPassword)


        val layPassword: TextInputLayout = view.findViewById(R.id.inplayPassword)
        val layConfirmPassword: TextInputLayout = view.findViewById(R.id.inplayConfirmPassword)


        //input validaitons


        btnRegisterBack.setOnClickListener {
            val fragment = RegisterDetailsFragment()
            fragment.arguments = bundle
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.replace(R.id.registerFragmentContainer, fragment)?.commit()
        }


        btnRegisterAccount.setOnClickListener {


            validation.emptyCheck(layPassword, edtPassword)
            validation.emptyCheck(layConfirmPassword, edtConfirmPassword)

            //checking if the passwords match
            if (edtPassword.text.toString() != edtConfirmPassword.text.toString()) {
                layPassword.error = "passwords do not match"
                layConfirmPassword.error = "passwords do not match"
            } else if (edtPassword.text.toString() == "") {
                layPassword.error = "Can't be empty"
                layConfirmPassword.error = "Can't be empty"
            } else {
                layPassword.error = null
                layConfirmPassword.error = null
            }

            if (layPassword.error == null && layConfirmPassword.error == null) {
                val firstName = bundle?.getString("firstname")!!
                val middleName = bundle.getString("middlename")!!
                val lastName = bundle.getString("lastname")!!
                val email = bundle.getString("email")!!
                val dateOfBirth = bundle.getString("dateofbirth")!!
                val userName = bundle.getString("username")!!
                val password = Hashing.doHashing(edtPassword.text.toString(), userName)
                val newUserType = bundle.getString("usertype")

                val currentUserType =
                    if (Functions.getUserinfo(requireContext())["usertype"]?.isNotEmpty() == true) Functions.getUserinfo(
                        requireContext()
                    )["usertype"] else "student"

                //make account
                val result: Array<Long> = db.addUser(
                    userName,
                    firstName,
                    middleName,
                    lastName,
                    email,
                    dateOfBirth,
                    password,
                    newUserType
                )


                //check if user was added to both user and authentication table
                if (result[0] > 0 && result[1] > 0) {
                    Toast.makeText(requireContext(), "User Added", Toast.LENGTH_SHORT).show()
                    when(currentUserType){
                        "admin"-> activity?.finish()
                        "student" -> {
                            activity?.finish()
                            startActivity(Intent(requireContext(), LoginActivity::class.java))
                        }
                        else-> {}
                    }
                } else {
                    Toast.makeText(requireContext(), "Unsuccessful", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return view
    }
}