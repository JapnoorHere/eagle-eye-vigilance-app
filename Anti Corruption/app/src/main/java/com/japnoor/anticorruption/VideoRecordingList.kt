package com.japnoor.anticorruption

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.japnoor.anticorruption.databinding.FragmentVideoRecordingListBinding
import java.io.File
import java.io.FileOutputStream
import java.net.URLConnection
import java.util.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class VideoRecordingList : Fragment() {
         private  val REQUEST_CAMERA_PERMISSION = 100
        private  val REQUEST_STORAGE_PERMISSION = 101

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var homeScreen: HomeScreen
    lateinit var videoref : DatabaseReference
    lateinit var database: FirebaseDatabase
    lateinit var firebaseStorage: FirebaseStorage
    lateinit var storageReference: StorageReference
    var videoList: ArrayList<Video> = ArrayList()
    var arrayList: ArrayList<Int> = ArrayList()
    lateinit var videoRecordingListAdapter: VideoRecordingListAdapter
    lateinit var binding: FragmentVideoRecordingListBinding

    companion object {
        private const val REQUEST_VIDEO_CAPTURE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeScreen=activity as HomeScreen
        database=FirebaseDatabase.getInstance()
        videoref=database.reference.child("videoRecording")
        firebaseStorage=FirebaseStorage.getInstance()
        storageReference=firebaseStorage.reference.child("videoRecording")
         binding = FragmentVideoRecordingListBinding.inflate(layoutInflater,container,false)

        val videoDir = File("/storage/emulated/0/Android/data/com.japnoor.anticorruption/files/Movies/")
        val videoFiles = videoDir.listFiles { file ->
            val mimeType = URLConnection.guessContentTypeFromName(file.name)
            mimeType != null && mimeType.startsWith("video/mp4")
        }
        if (videoFiles != null) {
            videoRecordingListAdapter= VideoRecordingListAdapter(homeScreen, videoFiles.toList())
            binding.recyclerView.adapter=videoRecordingListAdapter
            binding.recyclerView.layoutManager= LinearLayoutManager(homeScreen)
        }


        binding.fabbtn.setOnClickListener {
            requestCameraPermission()
        }


        return binding.root
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                startRecording()
            }
        }
    }
    fun startRecording(){
        val videoCaptureIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        startActivityForResult(videoCaptureIntent, REQUEST_VIDEO_CAPTURE)
    }

    private fun requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(homeScreen,android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                homeScreen,
                arrayOf(android.Manifest.permission.CAMERA),
                REQUEST_CAMERA_PERMISSION
            )
        } else {
            startRecording()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            val videoUri = data?.data
            val currentTime = Date().time
            val file = File(context?.getExternalFilesDir(Environment.DIRECTORY_MOVIES), "Video $currentTime.mp4")
            val outputStream = FileOutputStream(file)
            val inputStream = context?.contentResolver?.openInputStream(videoUri!!)
            inputStream.use { input ->
                outputStream.use { output ->
                    input?.copyTo(output)
                }
            }
            homeScreen.navController.navigate(R.id.videoRecordingList)
        }
    }
}