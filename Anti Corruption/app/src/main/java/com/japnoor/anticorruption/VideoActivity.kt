package com.japnoor.anticorruption

import android.content.Context
import android.content.pm.ActivityInfo
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.view.View
import android.widget.MediaController
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.japnoor.anticorruption.databinding.ActivityVideoBinding


class VideoActivity : AppCompatActivity() {

    lateinit var binding: ActivityVideoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
        if (isConnected) {
            var video = intent.getStringExtra("video")
            binding.videoview.setVideoURI(video?.toUri())
            binding.videoview.start()

            var mediaController = MediaController(this)
            binding.videoview.setMediaController(mediaController)
            mediaController.setAnchorView(binding.videoview)

        }
        else{
            Toast.makeText(this,"Check you internet connection please",Toast.LENGTH_LONG).show()
        }

        binding.btn.setOnClickListener{
            finish()
        }


    }

    override fun onStart() {
        super.onStart()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

    }


}