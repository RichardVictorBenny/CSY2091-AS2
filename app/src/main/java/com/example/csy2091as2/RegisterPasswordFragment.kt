package com.example.csy2091as2

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

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


        val btnRegisterBack: Button = view.findViewById(R.id.btnRegisterBack)
        btnRegisterBack.setOnClickListener {
            val fragment = RegisterDetailsFragment()
            fragment.arguments = bundle
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.replace(R.id.registerFragmentContainer, fragment)?.commit()
        }

//        if(bundle?.getString("firstname") != null){
//            Log.d("TAG", "onCreateView: firstname found")
//        } else{
//            Log.d("TAG", "onCreateView: not found")
//        }





        return view
    }
}