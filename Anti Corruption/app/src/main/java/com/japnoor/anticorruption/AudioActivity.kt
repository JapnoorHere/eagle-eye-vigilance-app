package com.japnoor.anticorruption

import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.SeekBar
import androidx.core.net.toUri
import com.japnoor.anticorruption.databinding.ActivityAudioBinding
import com.japnoor.anticorruption.databinding.EditUserDemandDialogBinding
import java.util.*

class AudioActivity : AppCompatActivity() {


    lateinit var binding: ActivityAudioBinding
     var mediaPlayer: MediaPlayer?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityAudioBinding.inflate(layoutInflater)
        setContentView(binding.root)


        var audio = intent.getStringExtra("audio")?.toUri()
        if (audio != null) {
            controlSound(audio)
        }

        binding.btn.setOnClickListener {
            mediaPlayer?.stop()
            finish()
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

}