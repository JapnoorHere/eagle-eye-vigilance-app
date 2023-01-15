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
import com.google.firebase.database.*
import com.japnoor.anticorruption.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    lateinit var binding : ActivityLoginBinding
    lateinit var auth: FirebaseAuth
     var passcode : String?=null
    lateinit var database: FirebaseDatabase
    lateinit var useref : DatabaseReference




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        database=FirebaseDatabase.getInstance()
        useref=database.reference.child("Users")
        passcode=intent.getStringExtra("passcode").toString()
        supportActionBar?.setDisplayShowHomeEnabled(true)


        binding.tvSignUp.setOnClickListener {
            var intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }

        binding.btnLogin.setOnClickListener {
            //validations
            if (binding.etUsername.text.isNullOrEmpty()) {
                binding.etUsername.requestFocus()
                binding.etUsername.error = "Enter Username"
            } else if (binding.etPassword.text.isNullOrEmpty()) {
                binding.etPassword.requestFocus()
                binding.etPassword.error = "Enter Password"
            }
            else {
                val connectivityManager =
                    getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
                val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
                if (isConnected) {
                    binding.progressbar.visibility = View.VISIBLE
                    binding.btnLogin.visibility = View.GONE
                    auth.signInWithEmailAndPassword(
                        binding.etUsername.text.toString(),
                        binding.etPassword.text.toString()
                    ).addOnCompleteListener(this) {
                        if (it.isSuccessful) {
                            binding.progressbar.visibility = View.GONE
                            binding.btnLogin.visibility = View.VISIBLE
                            Toast.makeText(this, "Login Successful", Toast.LENGTH_LONG).show()
                            var intent = Intent(this, HomeScreen::class.java)
                            intent.putExtra("uid", auth.currentUser?.uid)
                            startActivity(intent)
                            finish()
                        } else {
                            binding.progressbar.visibility = View.GONE
                            binding.btnLogin.visibility = View.VISIBLE
                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_LONG).show()
                        }
                    }

                }
                else{
                    Toast.makeText(this,"Check your internet connection please",Toast.LENGTH_LONG).show()

                }
            }
        }

        binding.tvForgotPassword.setOnClickListener {
            var intent=Intent(this,ForgotPassword::class.java)
            startActivity(intent)
        }
    }



    override fun onStart() {
        super.onStart()
        var user= auth.currentUser
        if(user!=null) {
            var intent = Intent(this@LoginActivity, HomeScreen::class.java)
            intent.putExtra("uid", user?.uid)
            startActivity(intent)
            finish()

        }
    }
}