package com.japnoor.anticorruption

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
            auth.sendPasswordResetEmail(binding.etEmail.text.toString()).addOnCompleteListener {
                if(it.isSuccessful){
                    Toast.makeText(this,"Reset Password link sent!",Toast.LENGTH_LONG).show()
                    if(user!=null){
                        var intent=Intent(this,HomeScreen::class.java)
                        startActivity(intent)
                        finish()
                    }
                    else{
                    var intent=Intent(this,LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                    }
                }
                else{
                    Toast.makeText(this,it.exception.toString(),Toast.LENGTH_LONG).show()
                }
            }
        }

    }
}