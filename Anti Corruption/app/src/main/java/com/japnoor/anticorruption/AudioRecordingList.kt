package com.japnoor.anticorruption

import android.Manifest.permission.RECORD_AUDIO
import android.app.Dialog
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.japnoor.anticorruption.databinding.FragmentAudioRecordBinding
import com.japnoor.anticorruption.databinding.FragmentAudioRecordingListBinding
import java.io.File
import java.net.URLConnection
import java.util.*

class AudioRecordingList : Fragment() {
companion object{
    var REQUEST_RECORD_AUDIO_PERMISSION=200
}

     var uri : Uri?=null
    lateinit var homeScreen : HomeScreen
    lateinit var audioref : DatabaseReference
    lateinit var database: FirebaseDatabase
    var audioList: ArrayList<Audio> = ArrayList()
    var arrayList: ArrayList<String> = ArrayList()
    lateinit var audioRecordingListAdapter: AudioRecordingListAdapter
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startRecording()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var binding= FragmentAudioRecordingListBinding.inflate(layoutInflater,container,false)
        database=FirebaseDatabase.getInstance()
        homeScreen=activity as HomeScreen
        audioref=database.reference.child("audioRecording")

        arguments.let {
            uri=it?.getString("uri").toString().toUri()
        }

        println("URI- > " + uri.toString())


        val musicDir = File("/storage/emulated/0/Android/data/com.japnoor.anticorruption/files/Music/")
        val audioFiles = musicDir.listFiles { file ->
            val mimeType = URLConnection.guessContentTypeFromName(file.name)
            mimeType != null && mimeType.startsWith("video/3gpp")
        }
            if (audioFiles != null) {
            audioRecordingListAdapter= AudioRecordingListAdapter(homeScreen, audioFiles.toList())
            binding.recyclerView.adapter=audioRecordingListAdapter
            binding.recyclerView.layoutManager=LinearLayoutManager(homeScreen)
        }

        binding.fabbtn.setOnClickListener {

             requestAudioPermission()
        }




        return binding.root
    }

    fun startRecording(){

        var filee: File?=null
        var isRecording = false
        var isPlayi = false
        var mediaRecorder: MediaRecorder? = null

        var dialog=Dialog(homeScreen)
        var dialogB=FragmentAudioRecordBinding.inflate(layoutInflater)
        dialog.setContentView(dialogB.root)
        dialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT)
        dialogB.audiorecord.setOnClickListener {
            if (!isRecording) {
                val currentTime = Date().time
                filee = File(requireContext().getExternalFilesDir(Environment.DIRECTORY_MUSIC),"Audio $currentTime.3gp")
                mediaRecorder = MediaRecorder()
                mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
                mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                mediaRecorder?.setOutputFile(filee?.absolutePath)
                dialogB.tv.setText("Recording has started")
                mediaRecorder?.prepare()
                mediaRecorder?.start()
                isRecording = true
                dialogB.audiorecord.setImageResource(R.drawable.audiostart)
                dialog.setCancelable(false)
            } else {
                dialogB.tv.setText("Recording is stopped")
                mediaRecorder?.stop()
                mediaRecorder?.reset()
                mediaRecorder?.release()
                dialog.dismiss()
                dialog.setCancelable(true)
                homeScreen.navController.navigate(R.id.audiorecordingListFragment)
                dialogB.audiorecord.setImageResource(R.drawable.audiostop)
                isRecording = false
            }
        }
        dialog.show()

    }

    private fun requestAudioPermission() {
        if (ContextCompat.checkSelfPermission(homeScreen, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(homeScreen, arrayOf(android.Manifest.permission.RECORD_AUDIO), REQUEST_RECORD_AUDIO_PERMISSION)
        } else {
            startRecording()
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeScreen=activity as HomeScreen


    }


}