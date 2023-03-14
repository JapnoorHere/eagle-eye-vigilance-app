package com.japnoor.anticorruption

import android.content.Context
import android.content.pm.ActivityInfo
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.net.toUri
import com.japnoor.anticorruption.databinding.ActivityAudioBinding
import com.japnoor.anticorruption.databinding.EditUserDemandDialogBinding
import java.util.*

class AudioActivity : AppCompatActivity() {


    lateinit var binding: ActivityAudioBinding
    var mediaPlayer: MediaPlayer?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioBinding.inflate(layoutInflater)
        setContentView(binding.root)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        var offlineAudio = intent.getStringExtra("audioo")?.toUri()
        var type = intent.getStringExtra("type")?.toString()

        binding.btn.setOnClickListener {
            mediaPlayer?.stop()
            finish()
        }

        if (type.equals("o")) {
            if (offlineAudio != null) {
                controlSound(offlineAudio)
            }
        } else {
            val connectivityManager =
                getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
            val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
            if (isConnected) {
                var audio = intent.getStringExtra("audio")?.toUri()
                if (audio != null) {
                    controlSound(audio)
                }
            } else {
                Toast.makeText(this, "Check you internet connection please", Toast.LENGTH_LONG)
                    .show()
            }


        }
    }

    fun controlSound(audio : Uri){
        binding.play.setOnClickListener {
            if(mediaPlayer==null){
                mediaPlayer=MediaPlayer.create(this,audio)
                initialise()
            }
            mediaPlayer?.start()
        }

        binding.pause.setOnClickListener {
            if (mediaPlayer !== null) {
                mediaPlayer?.pause()
            }
        }

        binding.seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(fromUser)
                    mediaPlayer?.seekTo(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                mediaPlayer?.seekTo(seekBar!!.progress)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                mediaPlayer?.seekTo(seekBar!!.progress)
            }
        })


    }
    fun initialise(){
        binding.seekbar.max=mediaPlayer!!.duration
        var handler=Handler()
        handler.postDelayed(object : Runnable{
            override fun run() {
                try{
                    binding.seekbar.progress=mediaPlayer!!.currentPosition
                    handler.postDelayed(this,1000)
                }
                catch (e : java.lang.Exception)
                {
                    binding.seekbar.progress=0
                }                }

        },0)
    }

    override fun onStart() {
        super.onStart()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

}