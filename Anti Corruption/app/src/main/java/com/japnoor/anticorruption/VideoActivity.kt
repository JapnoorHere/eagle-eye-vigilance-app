package com.japnoor.anticorruption

import android.os.Bundle
import android.widget.MediaController
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.japnoor.anticorruption.databinding.ActivityVideoBinding


class VideoActivity : AppCompatActivity() {

    lateinit var binding: ActivityVideoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)
       var video=intent.getStringExtra("video")
       binding.videoview.setVideoURI(video?.toUri())
       binding.videoview.start()
       var mediaController = MediaController(this)
       binding.videoview.setMediaController(mediaController)
       mediaController.setAnchorView(binding.videoview)


    }




}