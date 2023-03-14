package com.japnoor.anticorruption

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.startActivity
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.FirebaseDatabase
import com.japnoor.anticorruption.databinding.AudioItemBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class AudioRecordingListAdapter(var context: HomeScreen, val audioList:List<File>)  : RecyclerView.Adapter<AudioRecordingListAdapter.ViewHolder>() {


    var mediaPlayer : MediaPlayer?=null
    var isplayi=false
    var database=FirebaseDatabase.getInstance()
    var audioref=database.reference.child("audioRecording")



    class ViewHolder(var binding: AudioItemBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            AudioItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.name.text=audioList[position].name.removeSuffix(".mp3")
        var last=audioList[position].lastModified()
        var date=Date(last)
        val format = SimpleDateFormat("dd/MM/yyyy HH:mm")
        val dateString = format.format(date)
        holder.binding.Date.setText(dateString)
        holder.binding.playpause.setOnClickListener {
            var intent=Intent(context,AudioActivity::class.java)
            intent.putExtra("audioo",audioList[position].toUri().toString())
            intent.putExtra("type","o")
            context.startActivity(intent)
//                    if(!isplayi) {
//                        mediaPlayer = MediaPlayer.create(context, audioList[position].toUri())
//                        holder.binding.playpause.setImageResource(R.drawable.pause2)
//                        mediaPlayer?.start()
//                        isplayi=true
//                        mediaPlayer?.setOnCompletionListener{
//                            mediaPlayer?.reset()
//                            mediaPlayer?.pause()
//                            holder.binding.playpause.setImageResource(R.drawable.play2)
//                            isplayi=false
//                        }
//                    }
//            else if (mediaPlayer?.isPlaying == true) {
//                isplayi=false
//                holder.binding.playpause.setImageResource(R.drawable.play2)
//                mediaPlayer?.pause()
//            }
                }


        holder.binding.more.setOnClickListener {
            var bottomSheet = BottomSheetDialog(context)
            bottomSheet.setContentView(R.layout.dialog_more)
            bottomSheet.show()
            var tvShare = bottomSheet.findViewById<CardView>(R.id.share)
            var tvSend = bottomSheet.findViewById<CardView>(R.id.send)
            var tvDelete = bottomSheet.findViewById<CardView>(R.id.delete)

            tvShare?.setOnClickListener {
                bottomSheet.dismiss()
            val videoUri = Uri.parse(audioList[position].toString())
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.putExtra(Intent.EXTRA_STREAM, videoUri)
            shareIntent.type = "audio/*"
            context.startActivity(Intent.createChooser(shareIntent, "Share Audio"))
            }


            tvSend?.setOnClickListener {
                bottomSheet.dismiss()
                mediaPlayer?.stop()
                mediaPlayer?.reset()
                var bundle = Bundle()
                bundle.putString("audio",audioList[position].toUri().toString())
                context.navController.navigate(R.id.addComplaintFragment,bundle)
            }

            tvDelete?.setOnClickListener {
                bottomSheet.dismiss()
                mediaPlayer?.stop()
                mediaPlayer?.reset()
                audioList[position].delete()
                context.navController.navigate(R.id.audiorecordingListFragment)
            }
        }




    }

    override fun getItemCount(): Int {
        return audioList.size
    }


}
