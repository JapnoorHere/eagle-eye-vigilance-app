package com.japnoor.anticorruption

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.hanks.passcodeview.PasscodeView
import com.japnoor.anticorruption.databinding.ActivityPasscodeBinding
import com.japnoor.anticorruption.databinding.PasscodeDialogBinding

class PasscodeActivity : AppCompatActivity() {
    lateinit var binding : ActivityPasscodeBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        binding=ActivityPasscodeBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }

    override fun onStart() {
        super.onStart()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

    }
}