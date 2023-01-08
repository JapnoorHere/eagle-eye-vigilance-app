package com.japnoor.anticorruption

import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.japnoor.anticorruption.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    lateinit var binding : ActivityLoginBinding
    lateinit var auth: FirebaseAuth



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

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
            } else {
                auth.signInWithEmailAndPassword(
                    binding.etUsername.text.toString(),
                    binding.etPassword.text.toString()
                ).addOnCompleteListener(this) {
                    if (it.isSuccessful) {
                        Toast.makeText(this, "Login Successful", Toast.LENGTH_LONG).show()
                        var intent = Intent(this, HomeScreen::class.java)
                        intent.putExtra("uid",auth.currentUser?.uid)
                        startActivity(intent)
                        finish()
                    }
                    else {
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_LONG).show()
                    }
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
        if(user!=null){
            var intent=Intent(this,HomeScreen::class.java)
            intent.putExtra("uid",user.uid)
            startActivity(intent)
            finish()
        }
    }

}