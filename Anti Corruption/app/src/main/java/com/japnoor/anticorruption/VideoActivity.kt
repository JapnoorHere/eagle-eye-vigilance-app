package com.japnoor.anticorruption

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import com.japnoor.anticorruption.databinding.ActivityVideoBinding


class VideoActivity : AppCompatActivity() {


    lateinit var binding: ActivityVideoBinding
    private lateinit var playerView: PlayerView
    private lateinit var player: SimpleExoPlayer
    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        playerView = binding.playerView
        player = SimpleExoPlayer.Builder(this).build()


        var offlineVideo = intent.getStringExtra("videoo")?.toUri()
        var type = intent.getStringExtra("type")?.toString()
        if (type.equals("o")) {
            if (offlineVideo != null) {
                var video = intent.getStringExtra("videoo")
                playerView.player = player
                player.addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(state: Int) {
                        when (state) {
                            Player.STATE_BUFFERING -> {
                                binding.progressBar.visibility = View.VISIBLE
                            }
                            Player.STATE_READY, Player.STATE_ENDED -> {
                                binding.progressBar.visibility = View.GONE
                            }
                        }
                    }
                })
                val mediaItem = MediaItem.fromUri(video.toString().toUri())
                player.setMediaItem(mediaItem)
                player.prepare()

                player.play()

            }
        } else {
            var video = intent.getStringExtra("video")
            val connectivityManager =
                getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
            val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
            if (isConnected) {

                playerView.player = player
                player.addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(state: Int) {
                        when (state) {
                            Player.STATE_BUFFERING -> {
                                binding.progressBar.visibility = View.VISIBLE
                            }
                            Player.STATE_READY, Player.STATE_ENDED -> {
                                binding.progressBar.visibility = View.GONE
                            }
                        }
                    }
                })
                val mediaItem = MediaItem.fromUri(video.toString().toUri())
                player.setMediaItem(mediaItem)
                player.prepare()

                player.play()

            } else {
                Toast.makeText(this, "Check you internet connection please", Toast.LENGTH_LONG)
                    .show()
            }
        }

    }

}