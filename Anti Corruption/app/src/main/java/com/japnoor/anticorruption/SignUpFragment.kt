package com.japnoor.anticorruption

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil.setContentView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.japnoor.anticorruption.databinding.FragmentSignUpBinding
import com.japnoor.anticorruption.databinding.OtpBinding
import papaya.`in`.sendmail.SendMail
import kotlin.random.Random
import kotlin.random.nextInt

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class SignUpFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    lateinit var binding : FragmentSignUpBinding
    lateinit var auth : FirebaseAuth
    lateinit var userRef : DatabaseReference
    lateinit var database: FirebaseDatabase
    lateinit var signUp: SignUp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        signUp=activity as SignUp
        database=FirebaseDatabase.getInstance()
        userRef=database.reference.child("Users")
        auth=FirebaseAuth.getInstance()
        binding=FragmentSignUpBinding.inflate(layoutInflater,container,false)



        binding.etREPassword.doOnTextChanged { text, start, before, count ->
            if (!(text.toString() == binding.etPassword.text.toString()))
                binding.etREPassword.error = "Password is not same"
            else
                binding.etREPassword.error = null
        }

        binding.btnSignup.setOnClickListener {
            if(binding.etName.text.toString().isNullOrEmpty()){
                binding.etName.error="Enter Name"
                binding.etName.requestFocus()
            }
            else if(binding.etEmail.text.toString().isNullOrEmpty()){
                binding.etEmail.error="Enter email"
                binding.etEmail.requestFocus()

            }
            else if(!Patterns.EMAIL_ADDRESS.matcher(binding.etEmail.text.toString()).matches()){
                binding.etEmail.error ="Enter valid email"
                binding.etEmail.requestFocus()
            }
            else if (binding.etphone.text.toString().isNullOrEmpty()) {
                binding.etphone.requestFocus()
                binding.etphone.error = "Enter Phone NUMBER"}
            else if(binding.etphone.text.length<10 || binding.etphone.text.length>10){
                binding.etphone.error="Phone Number can be of 10 digits only"
                binding.etphone.requestFocus()
            }
            else if(binding.etphone.text.toString().startsWith("1")){
                binding.etphone.error="Not a Valid Phone Number"
                binding.etphone.requestFocus()
            }
            else if(binding.etphone.text.toString().startsWith("0")){
                binding.etphone.error="Not a Valid Phone Number"
                binding.etphone.requestFocus()
            }else if(binding.etphone.text.toString().startsWith("2")){
                binding.etphone.error="Not a Valid Phone Number"
                binding.etphone.requestFocus()
            }else if(binding.etphone.text.toString().startsWith("3")){
                binding.etphone.error="Not a Valid Phone Number"
                binding.etphone.requestFocus()
            }else if(binding.etphone.text.toString().startsWith("4")){
                binding.etphone.error="Not a Valid Phone Number"
                binding.etphone.requestFocus()
            }else if(binding.etphone.text.toString().startsWith("5")){
                binding.etphone.error="Not a Valid Phone Number"
                binding.etphone.requestFocus()
            }
            else if(binding.etPassword.text.toString().isNullOrEmpty()){
                binding.etPassword.error="Enter Password"
                binding.etPassword.requestFocus()
            }
            else if(binding.etPassword.text.toString().length<6){
                binding.etPassword.error="Password must be of at least 6 characters"
                binding.etPassword.requestFocus()
            }

            else if(binding.etREPassword.text.toString().isNullOrEmpty()){
                binding.etREPassword.error="Enter Password again"
                binding.etREPassword.requestFocus()
            }
            else {
                var bundle = Bundle()
                bundle.putString("email", binding.etEmail.text.toString())
                bundle.putString("name", binding.etName.text.toString())
                bundle.putString("phone", binding.etphone.text.toString())
                bundle.putString("pass", binding.etPassword.text.toString())
                signUp.navController.navigate(R.id.action_signUpFragment_to_OTPFragment,bundle)
            }
        }
        return binding.root
    }
}