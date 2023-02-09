package com.japnoor.anticorruption

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.japnoor.anticorruption.databinding.FragmentChangeEmailBinding

// TODO: Rename parameter arguments, choose names that match

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ChangeEmail : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var homeScreen: HomeScreen
    var email: String = ""
    var pass: String = ""
    lateinit var user: FirebaseAuth
    lateinit var database: FirebaseDatabase
    lateinit var userRef: DatabaseReference

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
        homeScreen = activity as HomeScreen
        database = FirebaseDatabase.getInstance()
        userRef = database.reference.child("Users")

        user = FirebaseAuth.getInstance()
        println(user.currentUser?.uid.toString())
        arguments.let {
            email = it?.getString("email").toString()
            pass = it?.getString("pass").toString()
        }
        var binding = FragmentChangeEmailBinding.inflate(layoutInflater, container, false)


        // Inflate the layout for this fragment

        binding.btnNext.setOnClickListener {
            if (binding.etEmail.text.toString().isNullOrEmpty()) {
                binding.etEmail.error = "Enter Email"
                binding.etEmail.requestFocus()
            } else if (binding.etEmail.text.toString().equals(email)) {
                homeScreen.navController.navigate(R.id.profileFragment)
            } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.etEmail.text.toString()).matches()) {
                binding.etEmail.error = "Enter valid email"
                binding.etEmail.requestFocus()
            } else {
                val connectivityManager =
                    homeScreen.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
                val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
                if (isConnected) {
                    binding.progressbar.visibility=View.VISIBLE
                    binding.btnNext.visibility=View.GONE
                    user.fetchSignInMethodsForEmail(binding.etEmail.text.toString())
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val signInMethods = task.result.signInMethods
                                println(signInMethods.toString())
                                if (signInMethods != null && signInMethods.contains("password")) {
                                    binding.progressbar.visibility = View.GONE
                                    binding.btnNext.visibility = View.VISIBLE
                                    Toast.makeText(
                                        homeScreen,
                                        "Email already exists",
                                        Toast.LENGTH_LONG
                                    )
                                        .show()
                                } else {
                                    binding.btnNext.visibility = View.GONE
                                    binding.progressbar.visibility = View.VISIBLE
                                    val credential = EmailAuthProvider.getCredential(email, pass)
                                    FirebaseAuth.getInstance().currentUser?.reauthenticate(
                                        credential
                                    )
                                        ?.addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                Toast.makeText(
                                                    homeScreen,
                                                    "OTP sent",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                                binding.btnNext.visibility = View.VISIBLE
                                                binding.progressbar.visibility = View.GONE
                                                var bundle = Bundle()
                                                bundle.putString(
                                                    "email",
                                                    binding.etEmail.text.toString()
                                                )
                                                homeScreen.navController.navigate(
                                                    R.id.action_changeEmail_to_OTPEmailChange,
                                                    bundle
                                                )


                                            } else {
                                                binding.btnNext.visibility = View.VISIBLE
                                                binding.progressbar.visibility = View.GONE
                                            }
                                        }
                                }

                        }
                }
            }
            else{
            Toast.makeText(homeScreen, "Check your internet connection please", Toast.LENGTH_LONG)
                .show()

        }
        }
    }
    return binding.root
}

}