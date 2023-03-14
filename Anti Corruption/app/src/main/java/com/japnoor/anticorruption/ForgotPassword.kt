package com.japnoor.anticorruption

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        auth=FirebaseAuth.getInstance()
        var user=intent.getStringExtra("id")
        this.title="Change Password"
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
                    var loaddialog= Dialog(this)
                    loaddialog.setContentView(R.layout.dialog_sign_loading)
                    loaddialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    loaddialog.show()
                    loaddialog.setCancelable(false)
                    auth.sendPasswordResetEmail(binding.etEmail.text.toString().trim())
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                               loaddialog.dismiss()
                                Toast.makeText(this, "Reset Password link sent on your email!", Toast.LENGTH_LONG)
                                    .show()
                                var intent = Intent(this, LoginActivity::class.java)
                                FirebaseAuth.getInstance().signOut()
                                startActivity(intent)
                                finish()
                            }
                            else if(it.exception.toString().equals("com.google.firebase.auth.FirebaseAuthInvalidUserException: There is no user record corresponding to this identifier. The user may have been deleted."))
                            {
                                loaddialog.dismiss()
                                Toast.makeText(this,"Email does not exists", Toast.LENGTH_LONG)
                                    .show()
                            }
                            else if(it.exception.toString().equals("com.google.firebase.auth.FirebaseAuthInvalidCredentialsException: The email address is badly formatted."))
                            {
                                loaddialog.dismiss()

                                Toast.makeText(this,"Not a valid email", Toast.LENGTH_LONG)
                                    .show()
                            }
                            else {
                                println(it.exception.toString())
                                loaddialog.dismiss()

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
    override fun onStart() {
        super.onStart()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }
}