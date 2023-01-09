package com.japnoor.anticorruption


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle

import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import androidx.fragment.app.Fragment
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.japnoor.anticorruption.databinding.FragmentAddComplaintBinding
import java.util.*


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AddComplaintFragment : Fragment() {


    private var param1: String? = null
    private var param2: String? = null

    lateinit var firebaseStorage: FirebaseStorage
    lateinit var storegeref : StorageReference


    lateinit var activityResulLauncher : ActivityResultLauncher<Intent>
    lateinit var activityResulLauncher2 : ActivityResultLauncher<Intent>

    lateinit var binding: FragmentAddComplaintBinding
    lateinit var homeScreen: HomeScreen

    lateinit var database: FirebaseDatabase
    lateinit var compRef: DatabaseReference
    lateinit var userRef: DatabaseReference

    lateinit var arrayAdapter: ArrayAdapter<String>


    var audioUrl : String=""
    var audioUri : Uri? =null
    var userName : String=""
    var userEmail : String=""
    var userPhone : String=""
    var videoUrl : String=""
    var videoUri : Uri? =null



    override fun onCreate(savedInstanceState: Bundle?) {
        registerActivityforResult()
        registerActivityforResult2()
        super.onCreate(savedInstanceState)
        homeScreen = activity as HomeScreen
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        firebaseStorage=FirebaseStorage.getInstance()
        storegeref=firebaseStorage.reference


        database = FirebaseDatabase.getInstance()
        compRef = database.reference.child("Complaints")
        userRef=database.reference.child("Users")
        binding = FragmentAddComplaintBinding.inflate(layoutInflater, container, false)

        binding.addAudio.setOnClickListener {
            if(videoUri==null&&audioUri==null){
                chooseAudio()
            }
            else if(audioUri!=null){
                    audioUri=null
                    binding.addAudio.setText("  Add Audio")
                    binding.addAudio.setCompoundDrawablesWithIntrinsicBounds(resources.getDrawable(R.drawable.ic_baseline_mic_24),null,resources.getDrawable(R.drawable.ic_baseline_control_point_24),null)
                    binding.addAudio.setBackgroundResource(R.drawable.upload_photo)

            }
            else{
                Toast.makeText(homeScreen," Either You choose video or audio",Toast.LENGTH_LONG).show()
            }
        }
        binding.addVideo.setOnClickListener {
            if(audioUri==null&&videoUri==null){
                chooseVideo()
            }
            else if(videoUri!=null){
                videoUri=null
                binding.addVideo.setText(" Add Video")
                binding.addVideo.setCompoundDrawablesWithIntrinsicBounds(resources.getDrawable(R.drawable.ic_baseline_videocam_24),null,resources.getDrawable(R.drawable.ic_baseline_control_point_24),null)
                binding.addVideo.setBackgroundResource(R.drawable.upload_photo)
            }
            else{
                Toast.makeText(homeScreen," Either You can choose video or audio",Toast.LENGTH_LONG).show()
            }
        }


        binding.btnSubmit.setOnClickListener {
            if (binding.compSumm.text.isNullOrEmpty()) {
                binding.compSumm.requestFocus()
                binding.compSumm.error = "Summary cannot be empty"
            } else if (binding.compAgainst.text.isNullOrEmpty()) {
                binding.compAgainst.requestFocus()
                binding.compAgainst.error = "Cannot be empty"
            } else if (binding.comDetails.text.isNullOrEmpty()) {
                binding.comDetails.requestFocus()
                binding.comDetails.error = "Cannot be empty"
            } else if (binding.District.text.isNullOrEmpty()) {
                binding.District.requestFocus()
                binding.District.error = "Cannot be empty"
            }
            else if(audioUri==null && videoUri==null)
            {
                Toast.makeText(homeScreen, "Upload Video or Audio", Toast.LENGTH_LONG).show()
            }
            else {
                binding.addAudio.isClickable=false
                binding.addVideo.isClickable=false
                binding.progressbar.visibility=View.VISIBLE
                binding.btnSubmit.visibility=View.GONE
                uploadComplaintandAudio()
                userRef.addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for(eachUser in snapshot.children) {
                            var user = eachUser.getValue(Users::class.java)
                            println("uSers" + user?.userId)
                            if (user != null && user.userId.equals(homeScreen.id)){
                                userName = user.name.toString()
                                userEmail = user.email.toString()
                                userPhone = user.phone.toString()
                                println("name" + userName)
                                break
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })
            }
        }
        return binding.root

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode==1 && grantResults.isNotEmpty() && grantResults[0]== PackageManager.PERMISSION_GRANTED){
            var intent = Intent()
            intent.type="audio/*"
            intent.action= Intent.ACTION_GET_CONTENT
            activityResulLauncher.launch(intent)
        }
    }



    fun chooseAudio(){
        if(ContextCompat.checkSelfPermission(homeScreen,android.Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(homeScreen, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),1)
        }
        else{

            var intent = Intent()
            intent.type="audio/*"
            intent.action= Intent.ACTION_GET_CONTENT
            activityResulLauncher.launch(intent)

        }
    }
    fun chooseVideo(){
        if(ContextCompat.checkSelfPermission(homeScreen,android.Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(homeScreen, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),1)
        }
        else{
            var intent = Intent()
            intent.type="video/*"
            intent.action= Intent.ACTION_GET_CONTENT
            activityResulLauncher2.launch(intent)
        }
    }

    fun registerActivityforResult(){

        activityResulLauncher=registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback { result ->
                var resultcode=result.resultCode
                var audioData=result.data

                if(resultcode== Activity.RESULT_OK && audioData!=null )
                    audioUri=audioData.data
                if(audioUri!=null){
                    binding.addAudio.setText("  Audio Selected")
                    binding.addAudio.setCompoundDrawablesWithIntrinsicBounds(resources.getDrawable(R.drawable.ic_baseline_mic_24),null,resources.getDrawable(R.drawable.ic_baseline_cancel_24),null)
                    binding.addAudio.setBackgroundResource(R.drawable.upload_photo1)
                }
            })


    }

    fun registerActivityforResult2(){

        activityResulLauncher2=registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback { result ->
                var resultcode=result.resultCode
                var videoData=result.data

                if(resultcode== Activity.RESULT_OK && videoData!=null )
                    videoUri=videoData.data
                if(videoUri!=null) {
                    binding.addVideo.setText("  Video Selected")
                    binding.addVideo.setCompoundDrawablesWithIntrinsicBounds(
                        resources.getDrawable(R.drawable.ic_baseline_videocam_24), null, resources.getDrawable(R.drawable.ic_baseline_cancel_24),
                        null)
                    binding.addVideo.setBackgroundResource(R.drawable.upload_photo1)
                }
            })


    }

    fun uploadComplaintandAudio(){

        var audioName=UUID.randomUUID().toString()
        var videoName=UUID.randomUUID().toString()

        val audioreference=storegeref.child("audios").child(audioName)
        val videoreference=storegeref.child("videos").child(videoName)

        audioUri?.let{ uri ->
            audioreference.putFile(uri).addOnSuccessListener {
                var myUploadAudioRef=storegeref.child("audios").child(audioName)

                myUploadAudioRef.downloadUrl.addOnSuccessListener {
                    var d = Date()
                    var complaintDate: CharSequence = DateFormat.format("MMMM d,yyyy", d.time)
                    var cid = compRef.push().key
                    audioUrl=it.toString()


                    var complaints = Complaints(
                        binding.compSumm.text.toString(),
                        binding.compAgainst.text.toString(),
                        binding.comDetails.text.toString(),
                        binding.District.text.toString(),
                        homeScreen.id,
                        complaintDate.toString(),
                        cid.toString(),audioName,audioUrl,videoName,videoUrl,userName,userEmail,userPhone,"")

                    compRef.child(cid.toString()).setValue(complaints).addOnCompleteListener {task->
                        if (task.isSuccessful) {
                            Toast.makeText(requireContext(), "Complaint Submit", Toast.LENGTH_LONG)
                                .show()
                            binding.progressbar.visibility=View.GONE
                            binding.btnSubmit.visibility=View.VISIBLE
                            homeScreen.navController.navigate(R.id.homeFragment)
                        } else {
                            binding.progressbar.visibility=View.GONE
                            binding.btnSubmit.visibility=View.VISIBLE
                            Toast.makeText(requireContext(), task.exception.toString(), Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                    println("Url 2-> " + audioUrl)
                }
            }.addOnFailureListener{

            }
        }
        videoUri?.let{ uri ->
            videoreference.putFile(uri).addOnSuccessListener {
                var myUploadVideoRef=storegeref.child("videos").child(videoName)

                myUploadVideoRef.downloadUrl.addOnSuccessListener {
                    var d = Date()
                    var complaintDate: CharSequence = DateFormat.format("MMMM d,yyyy", d.time)
                    var cid = compRef.push().key
                    videoUrl=it.toString()

                    var complaints = Complaints(
                        binding.compSumm.text.toString(),
                        binding.compAgainst.text.toString(),
                        binding.comDetails.text.toString(),
                        binding.District.text.toString(),
                        homeScreen.id,
                        complaintDate.toString(),
                        cid.toString(),audioName,audioUrl,videoName,videoUrl,userName,userEmail,userPhone,""
                    )

                    compRef.child(cid.toString()).setValue(complaints).addOnCompleteListener {task->
                        if (task.isSuccessful) {
                            Toast.makeText(requireContext(), "Complaint Submit", Toast.LENGTH_LONG)
                                .show()
                            binding.progressbar.visibility=View.GONE
                            binding.btnSubmit.visibility=View.VISIBLE
                            homeScreen.navController.navigate(R.id.homeFragment)
                        } else {
                            binding.progressbar.visibility=View.GONE
                            binding.btnSubmit.visibility=View.VISIBLE
                            Toast.makeText(requireContext(), task.exception.toString(), Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                    println("Url 2-> " + audioUrl)
                }
            }.addOnFailureListener{

            }
        }

    }


    override fun onResume() {
        super.onResume()
        var districts = resources.getStringArray(R.array.District)
        arrayAdapter = ArrayAdapter(requireContext(), R.layout.drop_down_item, districts)
        binding.District.setAdapter(arrayAdapter)
    }
}