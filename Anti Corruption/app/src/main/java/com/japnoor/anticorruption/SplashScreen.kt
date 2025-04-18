package com.japnoor.anticorruption

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.content.pm.ActivityInfo
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.japnoor.anticorruption.databinding.ActivitySplashScreenBinding
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

class SplashScreen : AppCompatActivity() {


    lateinit var navController: NavController
    lateinit var binding : ActivitySplashScreenBinding
    lateinit var sharedPreferences: SharedPreferences
    lateinit var editor: Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        sharedPreferences=getSharedPreferences("Instructions", Context.MODE_PRIVATE)
        editor=sharedPreferences.edit()

        binding=ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navController=findNavController(R.id.navController)

    }


}