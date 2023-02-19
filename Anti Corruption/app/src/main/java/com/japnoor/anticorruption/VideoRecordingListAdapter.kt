package com.japnoor.anticorruption

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
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



        holder.binding.more.setOnClickListener {
            var bottomSheet = BottomSheetDialog(context)
            bottomSheet.setContentView(R.layout.dialog_more)
            bottomSheet.show()
            var tvShare = bottomSheet.findViewById<CardView>(R.id.share)
            var tvSend = bottomSheet.findViewById<CardView>(R.id.send)
            var tvDelete = bottomSheet.findViewById<CardView>(R.id.delete)

            tvShare?.setOnClickListener {
                bottomSheet.dismiss()
                val videoUri = Uri.parse(videoFiles[position].toString())
                val shareIntent = Intent()
                shareIntent.action = Intent.ACTION_SEND
                shareIntent.putExtra(Intent.EXTRA_STREAM, videoUri)
                shareIntent.type = "video/*"
                context.startActivity(Intent.createChooser(shareIntent, "Share Video"))
            }


            tvSend?.setOnClickListener {
                bottomSheet.dismiss()
                var bundle=Bundle()
            bundle.putString("video",videoFiles[position].toUri().toString())
            context.navController.navigate(R.id.addComplaintFragment,bundle)
            }

            tvDelete?.setOnClickListener {
                bottomSheet.dismiss()
                videoFiles[position].delete()
                context.navController.navigate(R.id.videoRecordingList)
            }
        }

    }

    override fun getItemCount(): Int {
        return videoFiles.size
    }


}
