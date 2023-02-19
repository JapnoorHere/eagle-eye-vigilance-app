package com.japnoor.anticorruption

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.findNavController

class EmailChangeActivity : AppCompatActivity() {

    var id : String=""
    lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email_change)
        id=intent.getStringExtra("id").toString()

        navController=findNavController(R.id.navControllerEmail)

    }
}