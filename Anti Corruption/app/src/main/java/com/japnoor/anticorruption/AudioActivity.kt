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
import android.os.Looper
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.net.toUri
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.firebase.firestore.EventListener
import com.japnoor.anticorruption.databinding.ActivityAudioBinding
import com.japnoor.anticorruption.databinding.EditUserDemandDialogBinding
import java.util.*

class AudioActivity : AppCompatActivity() {

    private lateinit var player: SimpleExoPlayer
    private lateinit var playPauseButton: ImageView
    private lateinit var progressBar: ProgressBar
    private lateinit var seekBar: SeekBar
    lateinit var positionTextView : TextView
    var duration : Int=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio)


        val audio = intent.getStringExtra("audio")?.toUri()
        val offlineaudio = intent.getStringExtra("audioo")?.toUri()
        val type = intent.getStringExtra("type")


        if(type.toString().equals("o")){

            playPauseButton = findViewById(R.id.playPauseButton)
            progressBar = findViewById(R.id.progressBar)
            seekBar = findViewById(R.id.seekBar)
            positionTextView = findViewById(R.id.positionTextView)

            // Initialize ExoPlayer
            player = SimpleExoPlayer.Builder(this).build()
            player.addListener(object : Player.Listener {
                override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                    // Update play/pause button based on player state
                    if (playbackState == Player.STATE_READY && player.playWhenReady) {
                        playPauseButton.setImageResource(R.drawable.pause_latest)
                    } else {
                        playPauseButton.setImageResource(R.drawable.play_latest)
                    }
                }
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    // Update seek bar and position text view when player starts playing
                    if (isPlaying) {
                        duration = player.duration.toInt() / 1000
                        seekBar.max = duration
                        positionTextView.text = "0:00 / " + getTimeString(duration)
                    }
                }
                override fun onPositionDiscontinuity(reason: Int) {
                    // Update seek bar progress when position changes
                    val currentPosition = player.currentPosition.toInt() / 1000
                    seekBar.progress = currentPosition
                    positionTextView.text = getTimeString(currentPosition) + " / " + getTimeString(player.duration.toInt() / 1000)

                }

            })

            // Prepare audio source
            val mediaItem = MediaItem.fromUri(offlineaudio!!)
            player.setMediaItem(mediaItem)
            player.prepare()
            player.play()

            // Set click listener for play/pause button
            playPauseButton.setOnClickListener {
                player.playWhenReady = !player.playWhenReady
            }

            seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    // Update player position when seek bar changes
                    if (fromUser) {
                        player.seekTo(progress.toLong() * 1000)
                        positionTextView.text = getTimeString(progress) + " / " + getTimeString(duration)
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    // Do nothing
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    // Do nothing
                }
            })

            // Update seek bar progress periodically
            val handler = Handler(Looper.getMainLooper())
            handler.post(object : Runnable {
                override fun run() {
                    val currentPosition = player.currentPosition.toInt() / 1000
                    seekBar.progress = currentPosition
                    positionTextView.text = getTimeString(currentPosition) + " / " + getTimeString(duration)
                    handler.postDelayed(this, 1000)
                }
            })

            player.addListener(object :  Player.Listener {
                override fun onPlaybackStateChanged(state: Int) {
                    if (state == Player.STATE_BUFFERING) {
                        progressBar.visibility = View.VISIBLE
                    } else {
                        progressBar.visibility = View.GONE
                    }
                }
            })
        }
        else{

            playPauseButton = findViewById(R.id.playPauseButton)
            progressBar = findViewById(R.id.progressBar)
            seekBar = findViewById(R.id.seekBar)
            positionTextView = findViewById(R.id.positionTextView)



            // Initialize ExoPlayer
            player = SimpleExoPlayer.Builder(this).build()
            player.addListener(object : Player.Listener {
                override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                    // Update play/pause button based on player state
                    if (playbackState == Player.STATE_READY && player.playWhenReady) {
                        playPauseButton.setImageResource(R.drawable.pause_latest)
                    } else {
                        playPauseButton.setImageResource(R.drawable.play_latest)
                    }
                }
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    // Update seek bar and position text view when player starts playing
                    if (isPlaying) {
                        duration = player.duration.toInt() / 1000
                        seekBar.max = duration
                        positionTextView.text = "0:00 / " + getTimeString(duration)
                    }
                }
                override fun onPositionDiscontinuity(reason: Int) {
                    // Update seek bar progress when position changes
                    val currentPosition = player.currentPosition.toInt() / 1000
                    seekBar.progress = currentPosition
                    positionTextView.text = getTimeString(currentPosition) + " / " + getTimeString(player.duration.toInt() / 1000)

                }

            })

            // Prepare audio source
            val mediaItem = MediaItem.fromUri(audio!!)
            player.setMediaItem(mediaItem)
            player.prepare()
            player.play()

            // Set click listener for play/pause button
            playPauseButton.setOnClickListener {
                player.playWhenReady = !player.playWhenReady
            }

            seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    // Update player position when seek bar changes
                    if (fromUser) {
                        player.seekTo(progress.toLong() * 1000)
                        positionTextView.text = getTimeString(progress) + " / " + getTimeString(duration)
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    // Do nothing
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    // Do nothing
                }
            })

            // Update seek bar progress periodically
            val handler = Handler(Looper.getMainLooper())
            handler.post(object : Runnable {
                override fun run() {
                    val currentPosition = player.currentPosition.toInt() / 1000
                    seekBar.progress = currentPosition
                    positionTextView.text = getTimeString(currentPosition) + " / " + getTimeString(duration)
                    handler.postDelayed(this, 1000)
                }
            })

            player.addListener(object :  Player.Listener {
                override fun onPlaybackStateChanged(state: Int) {
                    if (state == Player.STATE_BUFFERING) {
                        progressBar.visibility = View.VISIBLE
                    } else {
                        progressBar.visibility = View.GONE
                    }
                }
            })
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }
    private fun getTimeString(time: Int): String {
        val minutes = time / 60
        val seconds = time % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}