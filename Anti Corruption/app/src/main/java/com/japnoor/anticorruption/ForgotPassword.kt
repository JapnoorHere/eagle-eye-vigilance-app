package com.japnoor.anticorruption

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.japnoor.anticorruption.databinding.ActivityForgotPasswordBinding
import com.japnoor.anticorruption.databinding.ActivityLoginBinding

class ForgotPassword : AppCompatActivity() {

    lateinit var binding: ActivityForgotPasswordBinding
    lateinit var auth : FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth=FirebaseAuth.getInstance()

        var user=intent.getStringExtra("id")

        binding.btnNext.setOnClickListener {

            if(binding.etEmail.text.toString().isNullOrEmpty()){
                binding.etEmail.error="Enter Email"
                binding.etEmail.requestFocus()
            }
            else {
                val connectivityManager =
                    getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
                val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
                if (isConnected) {
                    binding.btnNext.visibility = View.GONE
                    binding.progressbar.visibility = View.VISIBLE
                    auth.sendPasswordResetEmail(binding.etEmail.text.toString())
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                binding.btnNext.visibility = View.VISIBLE
                                binding.progressbar.visibility = View.GONE
                                Toast.makeText(this, "Reset Password link sent!", Toast.LENGTH_LONG)
                                    .show()
                                var intent = Intent(this, LoginActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                binding.btnNext.visibility = View.VISIBLE
                                binding.progressbar.visibility = View.GONE
                                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_LONG)
                                    .show()
                            }
                        }
                }
                else{
                    Toast.makeText(this,"Check your internet connection please",Toast.LENGTH_LONG).show()

                }
            }
        }

    }
}