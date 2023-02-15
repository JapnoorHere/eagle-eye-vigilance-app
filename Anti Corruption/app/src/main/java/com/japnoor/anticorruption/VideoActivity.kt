package com.japnoor.anticorruption

import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.content.res.Configuration.ORIENTATION_PORTRAIT
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.view.Surface
import android.view.View
import android.view.ViewGroup
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
            binding.videoView.setOnPreparedListener { mp ->
                val videoWidth = mp.videoWidth
                val videoHeight = mp.videoHeight
                val deviceOrientation = getDeviceOrientation()
                if (videoWidth > videoHeight && deviceOrientation == ORIENTATION_PORTRAIT) {
                    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                } else if (videoWidth < videoHeight && deviceOrientation == ORIENTATION_LANDSCAPE) {
                    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                }
                mp.start()
            }
            var video = intent.getStringExtra("video")
            binding.videoView.setVideoURI(video?.toUri())
            binding.videoView.start()

            var mediaController = MediaController(this)
            binding.videoView.setMediaController(mediaController)
            mediaController.setAnchorView(binding.videoView)

        }
        else{
            Toast.makeText(this,"Check you internet connection please",Toast.LENGTH_LONG).show()
        }

//        binding.btn.setOnClickListener{
//            finish()
//        }


    }

    override fun onStart() {
        super.onStart()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

    }
    private fun getDeviceOrientation(): Int {
        val rotation = windowManager.defaultDisplay.rotation
        val dm = resources.displayMetrics
        val width = dm.widthPixels
        val height = dm.heightPixels
        val orientation: Int
        if (width < height) {
            orientation = if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_270) {
                ORIENTATION_PORTRAIT
            } else {
                ORIENTATION_LANDSCAPE
            }
        } else {
            orientation = if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_90) {
                ORIENTATION_PORTRAIT
            } else {
                ORIENTATION_LANDSCAPE
            }
        }
        return orientation
    }

    companion object {
        private const val ORIENTATION_PORTRAIT = 1
        private const val ORIENTATION_LANDSCAPE = 2
    }

}