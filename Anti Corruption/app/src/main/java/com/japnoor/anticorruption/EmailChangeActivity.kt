package com.japnoor.anticorruption

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.findNavController

class EmailChangeActivity : AppCompatActivity() {

    var id : String=""
    var email : String=""
    var pass : String=""
    lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email_change)
        id=intent.getStringExtra("id").toString()
        email=intent.getStringExtra("email").toString()
        pass=intent.getStringExtra("pass").toString()

        navController=findNavController(R.id.navControllerEmail)

    }
}