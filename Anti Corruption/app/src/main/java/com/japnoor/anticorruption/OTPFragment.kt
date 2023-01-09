package com.japnoor.anticorruption

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.japnoor.anticorruption.databinding.FragmentOTPBinding
import papaya.`in`.sendmail.SendMail
import kotlin.math.sign
import kotlin.random.Random
import kotlin.random.nextInt

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class OTPFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null
    lateinit var binding: FragmentOTPBinding
    var email: String = ""
    var phone: String = ""
    var name: String = ""
    var pass: String = ""
    lateinit var auth: FirebaseAuth
    lateinit var userRef: DatabaseReference
    lateinit var database: FirebaseDatabase
    lateinit var signUp: SignUp
    var random: Int = 0;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    fun OTP() {
        random = Random.nextInt(100000..999999)
        val mail = SendMail("anticorruptionpunjab75@gmail.com", "fgqzvmpzigmfpygr",
            email, "Your One Time Password",
            "Use the following One Time Password (OTP) to log into Anti Corruption App : $random")
        mail.execute()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        signUp = activity as SignUp
        database = FirebaseDatabase.getInstance()
        userRef = database.reference.child("Users")
        auth = FirebaseAuth.getInstance()
        binding = FragmentOTPBinding.inflate(layoutInflater, container, false)
        arguments?.let {
            email = it.getString("email").toString()
            phone = it.getString("phone").toString()
            name = it.getString("name").toString()
            pass = it.getString("pass").toString()

        }
        binding.tvEmail.setText(email)
        binding.otp1.doOnTextChanged { text, start, before, count ->
            if (!binding.otp1.text.toString().isNullOrEmpty())
                binding.otp2.requestFocus()
        }
        binding.otp2.doOnTextChanged { text, start, before, count ->
            if (!binding.otp2.text.toString().isNullOrEmpty())
                binding.otp3.requestFocus()
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

        OTP()

        binding.resendOtp.setOnClickListener {
            OTP()
            Toast.makeText(signUp, "OTP sent again", Toast.LENGTH_LONG).show()
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
                Toast.makeText(signUp, "Enter OTP", Toast.LENGTH_LONG).show()
            } else if (otp.equals(random.toString())) {
                binding.btnVerify.visibility=View.GONE
                binding.progressbar.visibility=View.VISIBLE
                auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
                    if (it.isSuccessful) {
                        var user = auth.currentUser
                        var id = user?.uid
                        var users = Users(
                            name, phone,
                            email, id.toString()
                        )
                        userRef.child(id.toString()).setValue(users).addOnCompleteListener {
                            if (it.isSuccessful) {
                                Toast.makeText(
                                    signUp,
                                    "Your account has been created",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                                binding.btnVerify.visibility= View.VISIBLE
                                binding.progressbar.visibility=View.GONE
                        var intent = Intent(signUp, HomeScreen::class.java)
                        intent.putExtra("uid", id.toString())
                        startActivity(intent)
                        signUp.finish()
                    } else {
                        Toast.makeText(signUp, it.exception.toString(), Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                Toast.makeText(signUp, "Wrong Otp", Toast.LENGTH_LONG).show()
            }
        }
        return binding.root
    }


}