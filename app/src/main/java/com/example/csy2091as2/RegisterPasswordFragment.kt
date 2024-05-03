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
import com.example.csy2091as2.Functions.Hashing
import com.example.csy2091as2.Functions.Validations
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

/**
 * A simple [Fragment] subclass.
 * Use the [RegisterPasswordFragment.newInstance] factory method to
 * create an instance of this fragment.
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

//        Hashing.doHashing("passowrd",
        val btnRegisterBack: Button = view.findViewById(R.id.btnRegisterBack)
        val btnRegisterAccount: Button = view.findViewById(R.id.btnRegisterAccount)
        val edtUsername: TextInputEditText = view.findViewById(R.id.inpedtUsername)
        val edtPassword: TextInputEditText = view.findViewById(R.id.inpedtPassword)
        val edtConfirmPassword: TextInputEditText = view.findViewById(R.id.inpedtConfirmPassword)

        val layUsername: TextInputLayout = view.findViewById(R.id.inplayUsername)
        val layPassword: TextInputLayout = view.findViewById(R.id.inplayPassword)
        val layConfirmPassword: TextInputLayout = view.findViewById(R.id.inplayConfirmPassword)


        //input validaitons
        validation.setErrorOnChange(layUsername, edtUsername)

        btnRegisterBack.setOnClickListener {
            val fragment = RegisterDetailsFragment()
            fragment.arguments = bundle
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.replace(R.id.registerFragmentContainer, fragment)?.commit()
        }


        btnRegisterAccount.setOnClickListener {

            validation.emptyCheck(layUsername, edtUsername)
            validation.emptyCheck(layPassword, edtPassword)
            validation.emptyCheck(layConfirmPassword, edtConfirmPassword)
            validation.checkForSpecialChars(layUsername, edtUsername.text.toString())

            layUsername.error =
                if (db.userCheck(edtUsername.text.toString())) "Username taken" else null


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

            if (layUsername.error == null && layPassword.error == null && layConfirmPassword.error == null) {
                val firstName = bundle?.getString("firstname")
                val middleName = bundle?.getString("middlename")
                val lastName = bundle?.getString("lastname")
                val email = bundle?.getString("email")
                val dateOfBirth = bundle?.getString("dateofbirth")
                val userName = edtUsername.text.toString()
                val password = Hashing.doHashing(edtPassword.text.toString(), userName)
                // TODO: make account
                val result: Array<Long> = db.addUser(
                    userName,
                    firstName,
                    middleName,
                    lastName,
                    email,
                    dateOfBirth,
                    password
                )
                if (result[0] > 0) {
                    Toast.makeText(requireContext(), "User Added", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(requireContext(), LoginActivity::class.java))
                } else if (result[1] > 0) {
                    Toast.makeText(requireContext(), "User Authenticated", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(requireContext(), "Unsuccessful", Toast.LENGTH_SHORT).show()
                }
            }
        }


//        if(bundle?.getString("firstname") != null){
//            Log.d("TAG", "onCreateView: firstname found")
//        } else{
//            Log.d("TAG", "onCreateView: not found")
//        }


        return view
    }
}