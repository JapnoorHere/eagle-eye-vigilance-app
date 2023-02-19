package com.japnoor.anticorruption

import com.japnoor.anticorruption.R
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.japnoor.anticorruption.databinding.FragmentOTPEmailChangeBinding
import papaya.`in`.sendmail.SendMail
import kotlin.random.Random
import kotlin.random.nextInt

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class OTPEmailChange : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var email: String = ""
    var random: Int = 0
    lateinit var emailChangeActivity: EmailChangeActivity
    lateinit var user: FirebaseAuth
    lateinit var userRef: DatabaseReference
    lateinit var database: FirebaseDatabase
    lateinit var comparraylist: ArrayList<String>
    lateinit var demarraylist: ArrayList<String>

    lateinit var userArrayList: ArrayList<String>

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
        database = FirebaseDatabase.getInstance()
        userRef = database.reference.child("Users")
        emailChangeActivity = activity as EmailChangeActivity
        user = FirebaseAuth.getInstance()
        userArrayList = ArrayList<String>()
        var check=""
        comparraylist= ArrayList()
        demarraylist= ArrayList()
        var binding = FragmentOTPEmailChangeBinding.inflate(layoutInflater, container, false)

        arguments?.let {
            email = it.getString("email").toString()
            comparraylist= it.getStringArrayList("cids") as ArrayList<String>
            demarraylist= it.getStringArrayList("ddids") as ArrayList<String>
        }

        binding.tvEmail.setText(email)
        binding.otp1.doOnTextChanged { text, start, before, count ->
            if (!binding.otp1.text.toString().isNullOrEmpty())
                binding.otp2.requestFocus()
        }
        binding.otp2.doOnTextChanged { text, start, before, count ->
            if (!binding.otp2.text.toString().isNullOrEmpty())
                binding.otp3.requestFocus()
//           else if()
            else
                binding.otp1.requestFocus()
        }
        binding.otp3.doOnTextChanged { text, start, before, count ->
            if (!binding.otp3.text.toString().isNullOrEmpty())
                binding.otp4.requestFocus()
            else
                binding.otp2.requestFocus()
        }
        binding.otp4.doOnTextChanged { text, start, before, count ->
            if (!binding.otp4.text.toString().isNullOrEmpty())
                binding.otp5.requestFocus()
            else
                binding.otp3.requestFocus()
        }
        binding.otp5.doOnTextChanged { text, start, before, count ->
            if (!binding.otp5.text.toString().isNullOrEmpty())
                binding.otp6.requestFocus()
            else
                binding.otp4.requestFocus()
        }
        binding.otp6.doOnTextChanged { text, start, before, count ->
            if (binding.otp6.text.toString().isNullOrEmpty()) {
                binding.otp5.requestFocus()
            }
        }


        val connectivityManager =
            emailChangeActivity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
        if (isConnected) {
            OTP()
        } else {
            Toast.makeText(emailChangeActivity, "Check your internet connection please", Toast.LENGTH_LONG)
                .show()

        }
        binding.resendOtp.setOnClickListener {
            val connectivityManager =
                emailChangeActivity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
            val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
            if (isConnected) {
                OTP()
                Toast.makeText(emailChangeActivity, "OTP sent", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(
                    emailChangeActivity,
                    "Check your internet connection please",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        binding.btnVerify.setOnClickListener {
            var otp1 = binding.otp1.text.toString()
            var otp2 = binding.otp2.text.toString()
            var otp3 = binding.otp3.text.toString()
            var otp4 = binding.otp4.text.toString()
            var otp5 = binding.otp5.text.toString()
            var otp6 = binding.otp6.text.toString()
            var otp = "$otp1$otp2$otp3$otp4$otp5$otp6";
            println("OTP" + otp)
            if (binding.otp1.text.toString().isNullOrEmpty() ||
                binding.otp1.text.toString().isNullOrEmpty() ||
                binding.otp1.text.toString().isNullOrEmpty() ||
                binding.otp1.text.toString().isNullOrEmpty() ||
                binding.otp1.text.toString().isNullOrEmpty() ||
                binding.otp1.text.toString().isNullOrEmpty()
            ) {
                Toast.makeText(emailChangeActivity, "Enter OTP", Toast.LENGTH_LONG).show()
            } else if (otp.equals(random.toString())) {
                val connectivityManager =
                    emailChangeActivity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
                val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
                if (isConnected) {

                    binding.btnVerify.visibility = View.GONE
                    binding.progressbar.visibility = View.VISIBLE
                    user.currentUser?.updateEmail(email)?.addOnCompleteListener {
                        if (it.isSuccessful) {

                            userRef.child(emailChangeActivity.id).child("email")
                                .setValue(email)
                            binding.progressbar.visibility = View.GONE
                            binding.btnVerify.visibility = View.VISIBLE
                            var intent=Intent(emailChangeActivity,HomeScreen::class.java)
                            emailChangeActivity.startActivity(intent)
                            emailChangeActivity.finish()
                            for(eachh in comparraylist){
                                FirebaseDatabase.getInstance().reference.child("Complaints").child(eachh).child("userEmail").setValue(email)
                            }
                            for(eachh1 in demarraylist){
                                FirebaseDatabase.getInstance().reference.child("Demand Letter").child(eachh1).child("userEmail").setValue(email)
                            }
                        } else if (it.exception.toString()
                                .equals("com.google.firebase.auth.FirebaseAuthUserCollisionException: The email address is already in use by another account.")
                        ) {
                            Toast.makeText(emailChangeActivity, "Email already Exists", Toast.LENGTH_LONG)
                                .show()
                        } else {
                            binding.progressbar.visibility = View.GONE
                            binding.btnVerify.visibility = View.VISIBLE
                            Toast.makeText(emailChangeActivity, it.exception.toString(), Toast.LENGTH_LONG)
                                .show()
                            println("Emaol " + it.exception.toString())
                        }
                    }

                }
                else {
                    Toast.makeText(
                        emailChangeActivity,
                        "Check your internet connection please",
                        Toast.LENGTH_LONG
                    ).show()

                }
            } else {
                binding.btnVerify.visibility = View.VISIBLE
                binding.progressbar.visibility = View.GONE
                Toast.makeText(emailChangeActivity, "Wrong Otp", Toast.LENGTH_LONG).show()
            }
        }

        return binding.root
    }

    fun OTP() {
        random = Random.nextInt(100000..999999)
        val mail = SendMail(
            "bpunjabvigilance@gmail.com", "wwpxryeuxsavbhur",
            email, "Your One Time Password",
            "Use the following One Time Password (OTP) to log into Anti Corruption App : $random"
        )
        mail.execute()
    }

}