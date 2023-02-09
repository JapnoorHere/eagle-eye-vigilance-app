package com.japnoor.anticorruption

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.transition.Hold
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.japnoor.anticorruption.databinding.FragmentCheckPasswordBinding

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class CheckPassword : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var homeScreen: HomeScreen
     var email: String=""
     var pass: String=""

    lateinit var database: FirebaseDatabase
    lateinit var userReference: DatabaseReference

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
        homeScreen=activity as HomeScreen
arguments.let {
     email=it?.getString("email").toString()
     pass=it?.getString("pass").toString()
}
        var binding=FragmentCheckPasswordBinding.inflate(layoutInflater,container,false)

        binding.lottie.playAnimation()

        binding.lottie.setOnClickListener{
            binding.lottie.playAnimation()
        }

        binding.btnNext.setOnClickListener {
            if(binding.etEmail.text.toString().isNullOrEmpty()){
                binding.etEmail.requestFocus()
                binding.etEmail.error="Enter Password"
            }
            else if(pass.equals(binding.etEmail.text.toString())){
                val connectivityManager =
                    homeScreen.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
                val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
                if (isConnected) {
                    var bundle = Bundle()
                    bundle.putString("email", email)
                    bundle.putString("pass", pass)
                    homeScreen.navController.navigate(
                        R.id.action_checkPassword_to_changeEmail,
                        bundle
                    )
                }
                else{
                    Toast.makeText(homeScreen,"Check your internet connection please",Toast.LENGTH_LONG).show()

                }
            }
            else{
                Toast.makeText(homeScreen,"Password is Wrong",Toast.LENGTH_LONG).show()
            }
        }

        return binding.root
    }

}