package com.japnoor.anticorruption

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.japnoor.anticorruption.databinding.AudioItemBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

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

        holder.binding.name.text=audioList[position].name.removeSuffix(".3gp")
        var last=audioList[position].lastModified()
        var date=Date(last)
        val format = SimpleDateFormat("dd/MM/yyyy HH:mm")
        val dateString = format.format(date)
        holder.binding.Date.setText(dateString)
        holder.binding.playpause.setOnClickListener {
                    if(!isplayi) {
                        mediaPlayer = MediaPlayer.create(context, audioList[position].toUri())
                        holder.binding.playpause.setImageResource(R.drawable.pause2)
                        mediaPlayer?.start()
                        isplayi=true
                        mediaPlayer?.setOnCompletionListener{
                            mediaPlayer?.reset()
                            mediaPlayer?.pause()
                            holder.binding.playpause.setImageResource(R.drawable.play2)
                            isplayi=false
                        }
                    }
            else if (mediaPlayer?.isPlaying == true) {
                isplayi=false
                holder.binding.playpause.setImageResource(R.drawable.play2)
                mediaPlayer?.pause()
            }
                }

        holder.binding.delete.setOnClickListener {
            mediaPlayer?.stop()
            mediaPlayer?.reset()
            audioList[position].delete()
            context.navController.navigate(R.id.audiorecordingListFragment)
        }



        holder.binding.share.setOnClickListener {
            mediaPlayer?.stop()
            mediaPlayer?.reset()
            var bundle =Bundle()
            bundle.putString("audio",audioList[position].toUri().toString())
            context.navController.navigate(R.id.addComplaintFragment,bundle)
        }


//        holder.binding.share.setOnClickListener {
//            val share = Intent(Intent.ACTION_SEND)
//            share.type = "video/3gpp"
//            share.setDataAndType(Uri.fromFile(audioList[position]), context.contentResolver.getType(Uri.fromFile(audioList[position])))
//            share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(audioList[position]))
//            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//            context.startActivity(Intent.createChooser(share, "Share Sound File"))
//        }

    }

    override fun getItemCount(): Int {
        return audioList.size
    }


}
