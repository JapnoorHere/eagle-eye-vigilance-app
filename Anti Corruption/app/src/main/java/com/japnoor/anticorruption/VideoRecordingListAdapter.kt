package com.japnoor.anticorruption

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.japnoor.anticorruption.databinding.AudioItemBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class VideoRecordingListAdapter (var context: HomeScreen, var videoFiles: List<File>)  : RecyclerView.Adapter<VideoRecordingListAdapter.ViewHolder>() {

    var database=FirebaseDatabase.getInstance()
    var audioref=database.reference.child("videoRecording")



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

        holder.binding.name.text=videoFiles[position].name.removeSuffix(".mp4")
        var last=videoFiles[position].lastModified()
        var date= Date(last)
        val format = SimpleDateFormat("dd/MM/yyyy HH:mm")
        val dateString = format.format(date)
        holder.binding.Date.setText(dateString)

        holder.binding.playpause.setOnClickListener {
            var intent=Intent(context,VideoActivity::class.java)
            intent.putExtra("video",videoFiles[position].toUri().toString())
            context.startActivity(intent)
        }

        holder.binding.share.setOnClickListener {
            var bundle=Bundle()
            bundle.putString("video",videoFiles[position].toUri().toString())
            context.navController.navigate(R.id.addComplaintFragment,bundle)
        }

        holder.binding.delete.setOnClickListener {
            videoFiles[position].delete()
            context.navController.navigate(R.id.videoRecordingList)
        }
    }

    override fun getItemCount(): Int {
        return videoFiles.size
    }


}
