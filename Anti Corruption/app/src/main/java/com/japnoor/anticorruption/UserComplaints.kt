package com.japnoor.anticorruption

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.japnoor.anticorruption.databinding.EditUserComplaintDialogBinding
import com.japnoor.anticorruption.databinding.FragmentUserComplaintsBinding
import com.japnoor.anticorruption.databinding.ShowUserComplaintsDialogBinding
import java.util.*
import kotlin.collections.ArrayList

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class UserComplaints : Fragment(), UserComplaintClick {
    private var param1: String? = null
    private var param2: String? = null

    lateinit var arrayAdapter: ArrayAdapter<String>
    var complaintsList: ArrayList<Complaints> = ArrayList()
    lateinit var myComplaintsAdapter: MyComplaintsAdapter
    lateinit var firebaseStorage: FirebaseStorage
    lateinit var storegeref: StorageReference

    lateinit var dialogBindEdit : EditUserComplaintDialogBinding


    lateinit var activityResulLauncher: ActivityResultLauncher<Intent>
    lateinit var activityResulLauncher2: ActivityResultLauncher<Intent>
    var audioUrl: String = ""
    var audioUri: Uri? = null

    var videoUrl: String = ""
    var videoUri: Uri? = null

    lateinit var database: FirebaseDatabase
    lateinit var compRef: DatabaseReference
    var c=0

    lateinit var binding: FragmentUserComplaintsBinding

    lateinit var homeScreen: HomeScreen

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        registerActivityforResult()
        registerActivityforResult2()

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        firebaseStorage = FirebaseStorage.getInstance()
        storegeref = firebaseStorage.reference
        homeScreen = activity as HomeScreen
        database = FirebaseDatabase.getInstance()
        compRef = database.reference.child("Complaints")

        binding = FragmentUserComplaintsBinding.inflate(layoutInflater, container, false)


        binding.search.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= (binding.search.right - binding.search.compoundDrawables[2].bounds.width())) {
                             binding.search.text.clear()
                    return@setOnTouchListener true
                }
            }
            return@setOnTouchListener false
        }


        binding.shimmer.startShimmer()
        compRef.addValueEventListener(object : ValueEventListener, UserComplaintClick {

            override fun onDataChange(snapshot: DataSnapshot) {
                complaintsList.clear()
                for (eachComplaint in snapshot.children) {
                         val complaint = eachComplaint.getValue(Complaints::class.java)

                         if (complaint != null && complaint.userId.equals(homeScreen.id)) {
                             complaintsList.add(complaint)
                         }
                    myComplaintsAdapter = MyComplaintsAdapter(homeScreen, complaintsList, this)
                    binding.recyclerView.layoutManager = LinearLayoutManager(homeScreen)
                    binding.recyclerView.adapter = myComplaintsAdapter
                    binding.shimmer.visibility=View.GONE
                    binding.shimmer.stopShimmer()
                    binding.recyclerView.visibility=View.VISIBLE
                    binding.search.addTextChangedListener(object : TextWatcher{
                        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                        }

                        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        }

                        override fun afterTextChanged(s: Editable?) {
                            var filteredList = ArrayList<Complaints>()
                            for (item in complaintsList){
                                if(item.complaintAgainst.toLowerCase().contains(s.toString().toLowerCase())
                                    || item.complaintNumber.toLowerCase().contains(s.toString().toLowerCase())
                                    || item.complaintDate.toLowerCase().contains(s.toString().toLowerCase())
                                    || item.complaintTime.toLowerCase().contains(s.toString().toLowerCase())
                                )
                                    filteredList.add(item)
                            }
                            myComplaintsAdapter.FilteredList(filteredList)
                        }

                    })
                }
                binding.shimmer.visibility=View.GONE
                binding.shimmer.stopShimmer()
                binding.recyclerView.visibility=View.VISIBLE

            }

            override fun onCancelled(error: DatabaseError) {

            }


            override fun onClick(complaints: Complaints) {
                var dialog = Dialog(requireContext())
                if (complaints.status.equals("1")) {
                    var dialogBind = ShowUserComplaintsDialogBinding.inflate(layoutInflater)
                    dialog.setContentView(dialogBind.root)
                    dialog.window?.setLayout(
                        WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.WRAP_CONTENT
                    )

                    dialogBind.Framelayout.setBackgroundResource(R.color.accepted1)
                    dialogBind.stamp.setImageResource(R.drawable.accpeted_stamp)
                    dialogBind.tvSummary.setText(complaints.complaintSummary)
                    dialogBind.tvDetails.setText(complaints.complaintDetails)
                    dialogBind.tvAgainst.setText(complaints.complaintAgainst)
                    dialogBind.comDate.setText(complaints.complaintDate)
                    dialogBind.tvDistrict.setText(complaints.complaintDistrict)

                    dialogBind.fabAdd1.setOnClickListener {
                        dialog.dismiss()
                    }
                    dialogBind.audio.setOnClickListener {
                        val fileUri: Uri = complaints.audioUrl.toUri()
                        var intent=Intent(homeScreen,AudioActivity::class.java)
                        intent.putExtra("audio",fileUri.toString())
                        homeScreen.startActivity(intent)

                    }
                    dialogBind.video.setOnClickListener {
                        val fileUri: Uri = complaints.videoUrl.toUri()

                        var intent = Intent(homeScreen, VideoActivity::class.java)
                        intent.putExtra("video", fileUri.toString())
                        homeScreen.startActivity(intent)
                    }


                    if (complaints.audioUrl.isNullOrEmpty()) {
                        dialogBind.audio.visibility = View.GONE
                        dialogBind.audio.visibility = View.GONE
                    }

                    if (complaints.videoUrl.isNullOrEmpty()) {
                        dialogBind.video.visibility = View.GONE
                        dialogBind.video.visibility = View.GONE
                    }


                    dialog.show()
                } else if (complaints.status.equals("2")) {
                    var dialogBind = ShowUserComplaintsDialogBinding.inflate(layoutInflater)
                    dialog.setContentView(dialogBind.root)
                    dialog.window?.setLayout(
                        WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.WRAP_CONTENT
                    )
                    dialogBind.stamp.setImageResource(R.drawable.resolved_stamp)
                    dialogBind.Framelayout.setBackgroundResource(R.color.resolved1)
                    dialogBind.tvSummary.setText(complaints.complaintSummary)
                    dialogBind.tvDetails.setText(complaints.complaintDetails)
                    dialogBind.tvAgainst.setText(complaints.complaintAgainst)
                    dialogBind.comDate.setText(complaints.complaintDate)
                    dialogBind.tvDistrict.setText(complaints.complaintDistrict)

                    dialogBind.fabAdd1.setOnClickListener {
                        dialog.dismiss()
                    }
                    dialogBind.audio.setOnClickListener {
                        val fileUri: Uri = complaints.audioUrl.toUri()
                        var intent=Intent(homeScreen,AudioActivity::class.java)
                        intent.putExtra("audio",fileUri.toString())
                        homeScreen.startActivity(intent)

                    }
                    dialogBind.video.setOnClickListener {
                        val fileUri: Uri = complaints.videoUrl.toUri()

                        var intent = Intent(homeScreen, VideoActivity::class.java)
                        intent.putExtra("video", fileUri.toString())
                        homeScreen.startActivity(intent)
                    }


                    if (complaints.audioUrl.isNullOrEmpty()) {
                        dialogBind.audio.visibility = View.GONE
                        dialogBind.audio.visibility = View.GONE
                    }

                    if (complaints.videoUrl.isNullOrEmpty()) {
                        dialogBind.video.visibility = View.GONE
                        dialogBind.video.visibility = View.GONE
                    }


                    dialog.show()
                } else if (complaints.status.equals("3")) {
                    var dialogBind = ShowUserComplaintsDialogBinding.inflate(layoutInflater)
                    dialog.setContentView(dialogBind.root)
                    dialog.window?.setLayout(
                        WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.WRAP_CONTENT
                    )
                    dialogBind.Framelayout.setBackgroundResource(R.color.rejected1)
                    dialogBind.stamp.setImageResource(R.drawable.rejected_stamp)
                    dialogBind.tvSummary.setText(complaints.complaintSummary)
                    dialogBind.tvDetails.setText(complaints.complaintDetails)
                    dialogBind.tvAgainst.setText(complaints.complaintAgainst)
                    dialogBind.comDate.setText(complaints.complaintDate)
                    dialogBind.tvDistrict.setText(complaints.complaintDistrict)

                    dialogBind.fabAdd1.setOnClickListener {
                        dialog.dismiss()
                    }
                    dialogBind.audio.setOnClickListener {
                        val fileUri: Uri = complaints.audioUrl.toUri()
                        var intent=Intent(homeScreen,AudioActivity::class.java)
                        intent.putExtra("audio",fileUri.toString())
                        homeScreen.startActivity(intent)

                    }

                    dialogBind.video.setOnClickListener {
                        val fileUri: Uri = complaints.videoUrl.toUri()

                        var intent = Intent(homeScreen, VideoActivity::class.java)
                        intent.putExtra("video", fileUri.toString())
                        homeScreen.startActivity(intent)
                    }


                    if (complaints.audioUrl.isNullOrEmpty()) {
                        dialogBind.audio.visibility = View.GONE
                        dialogBind.audio.visibility = View.GONE
                    }

                    if (complaints.videoUrl.isNullOrEmpty()) {
                        dialogBind.video.visibility = View.GONE
                        dialogBind.video.visibility = View.GONE
                    }


                    dialog.show()
                } else {
                     dialogBindEdit = EditUserComplaintDialogBinding.inflate(layoutInflater)
                    dialog.setContentView(dialogBindEdit.root)
                    dialog.window?.setLayout(
                        WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.MATCH_PARENT
                    )

                    dialogBindEdit.compSumm.setText(complaints.complaintSummary)
                    dialogBindEdit.comDetails.setText(complaints.complaintDetails)
                    dialogBindEdit.compAgainst.setText(complaints.complaintAgainst)
                    dialogBindEdit.comDate.setText(complaints.complaintDate)
                    dialogBindEdit.District.setText(complaints.complaintDistrict)
                    var districts = resources.getStringArray(R.array.District)
                    arrayAdapter =
                        ArrayAdapter(requireContext(), R.layout.drop_down_item, districts)
                    dialogBindEdit.District.setAdapter(arrayAdapter)
                    dialogBindEdit.fabAdd1.setOnClickListener {
                        val input: String = dialogBindEdit.compSumm.getText().toString().trim()
                        val input1: String = dialogBindEdit.compAgainst.getText().toString().trim()
                        val input2: String = dialogBindEdit.comDetails.getText().toString().trim()
                        if(input.length==0){
                            dialogBindEdit.compSumm.requestFocus()
                            dialogBindEdit.compSumm.error = "Cannot be empty"
                        }
                        else if(input1.length==0){
                            dialogBindEdit.compAgainst.requestFocus()
                            dialogBindEdit.compAgainst.error = "Cannot be empty"
                        }
                        else if(input2.length==0){
                            dialogBindEdit.comDetails.requestFocus()
                            dialogBindEdit.comDetails.error = "Cannot be empty"
                        }
                        else if (dialogBindEdit.District.text.isNullOrEmpty()) {
                            dialogBindEdit.District.requestFocus()
                            dialogBindEdit  .District.error = "Cannot be empty"
                        }
                        else if (audioUri == null && videoUri == null) {
                            update(dialogBindEdit,complaints,dialog)
                        } else {
                            dialogBindEdit.progressbar.visibility = View.VISIBLE
                            uploadComplaintandAudio(dialogBindEdit, complaints, dialog)
                        }
                    }
                    dialogBindEdit.audio.setOnClickListener {
                        val fileUri: Uri = complaints.audioUrl.toUri()
                        var intent=Intent(homeScreen,AudioActivity::class.java)
                        intent.putExtra("audio",fileUri.toString())
                        homeScreen.startActivity(intent)

                    }

                    dialogBindEdit.video.setOnClickListener {
                        val fileUri: Uri = complaints.videoUrl.toUri()

                        var intent = Intent(homeScreen, VideoActivity::class.java)
                        intent.putExtra("video", fileUri.toString())
                        homeScreen.startActivity(intent)
                    }
                    if (complaints.audioUrl.isNullOrEmpty()) {
                        dialogBindEdit.audioUpload.visibility = View.GONE
                        dialogBindEdit.audio.visibility = View.GONE
                    }

                    if (complaints.videoUrl.isNullOrEmpty()) {
                        dialogBindEdit.videoUpload.visibility = View.GONE
                        dialogBindEdit.video.visibility = View.GONE
                    }

                    dialogBindEdit.audioUpload.setOnClickListener {
                        if(audioUri==null) {
                            chooseAudio()
                        }
                        else if(audioUri!=null){
                                audioUri=null
                            dialogBindEdit.audioUpload.setBackgroundResource(R.drawable.buttonbg)
                            dialogBindEdit.audioUpload.setImageResource(R.drawable.ic_baseline_file_upload_24)

                        }
                    }

                    dialogBindEdit.videoUpload.setOnClickListener {
                        if(videoUri==null) {
                            chooseVideo()
                        }
                        else if(videoUri!=null){
                            videoUri=null
                            dialogBindEdit.videoUpload.setBackgroundResource(R.drawable.buttonbg)
                            dialogBindEdit.videoUpload.setImageResource(R.drawable.ic_baseline_file_upload_24)

                        }
                    }

                    dialogBindEdit.fabAdd2.setOnClickListener {
                        var bottomSheet = BottomSheetDialog(requireContext())
                        bottomSheet.setContentView(R.layout.dialog_delete_users)
                        bottomSheet.show()
                        var text = bottomSheet.findViewById<TextView>(R.id.textmsg)
                        var tvYes = bottomSheet.findViewById<TextView>(R.id.tvYes)
                        var tvNo = bottomSheet.findViewById<TextView>(R.id.tvNo)
                        text?.setText("Are you sure you want \n to delete this Complaint ?")
                        tvNo?.setOnClickListener {
                            bottomSheet.dismiss()
                        }
                        tvYes?.setOnClickListener {
                            compRef.child(complaints.complaintId).removeValue()
                            complaints?.audioName?.let { it1 ->
                                storegeref.child("audios").child(it1).delete()
                            }
                            complaints?.videoName?.let { it1 ->
                                storegeref.child("videos").child(it1).delete()
                            }
                            bottomSheet.dismiss()
                            dialog.dismiss()
                            homeScreen.navController.navigate(R.id.homeFragment)
                        }
                    }

                    dialog.show()
                }
            }
        })



        return binding.root
    }

    override fun onClick(complaints: Complaints) {
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            var intent = Intent()
            intent.type = "audio/*"
            intent.action = Intent.ACTION_GET_CONTENT
            activityResulLauncher.launch(intent)
        }
    }


    fun chooseAudio() {
        if (ContextCompat.checkSelfPermission(
                homeScreen,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                homeScreen,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                1
            )
        } else {
            var intent = Intent()
            intent.type = "audio/*"
            intent.action = Intent.ACTION_GET_CONTENT
            activityResulLauncher.launch(intent)
        }
    }

    fun chooseVideo() {
        if (ContextCompat.checkSelfPermission(
                homeScreen,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                homeScreen,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                1
            )
        } else {
            var intent = Intent()
            intent.type = "video/*"
            intent.action = Intent.ACTION_GET_CONTENT
            activityResulLauncher2.launch(intent)
        }
    }

    fun registerActivityforResult() {

        activityResulLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback { result ->
                var resultcode = result.resultCode
                var audioData = result.data

                if (resultcode == Activity.RESULT_OK && audioData != null)
                    audioUri = audioData.data
                if(audioUri!=null){
                    dialogBindEdit.audioUpload.setBackgroundResource(R.drawable.buttonbg1)
                    dialogBindEdit.audioUpload.setImageResource(R.drawable.ic_baseline_cancel_241)
                }
            })
    }

    fun registerActivityforResult2() {

        activityResulLauncher2 = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback { result ->
                var resultcode = result.resultCode
                var videoData = result.data

                if (resultcode == Activity.RESULT_OK && videoData != null) {
                    videoUri = videoData.data
                    if(videoUri!=null){
                        dialogBindEdit.videoUpload.setBackgroundResource(R.drawable.buttonbg1)
                        dialogBindEdit.videoUpload.setImageResource(R.drawable.ic_baseline_cancel_241)
                    }
                }
            })


    }

    fun update(dialogBind: EditUserComplaintDialogBinding,
                    complaints: Complaints,
                    dialog: Dialog){
        var compMap = mutableMapOf<String, Any>()
        compMap["complaintSummary"] = dialogBind.compSumm.text.toString()
        compMap["complaintAgainst"] = dialogBind.compAgainst.text.toString()
        compMap["complaintDetails"] = dialogBind.comDetails.text.toString()
        compMap["complaintDistrict"] = dialogBind.District.text.toString()
        println("id->" + complaints.complaintId)
        compRef.child(complaints.complaintId).updateChildren(compMap)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    dialog.dismiss()
                } else {
                    Toast.makeText(
                        requireContext(),
                        it.exception.toString(),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }


    }

    fun uploadComplaintandAudio(
        dialogBind: EditUserComplaintDialogBinding,
        complaints: Complaints,
        dialog: Dialog
    ) {

        var audioName = complaints.audioName
        var videoName = complaints.videoName

        val audioreference = storegeref.child("audios").child(audioName)
        val videoreference = storegeref.child("videos").child(videoName)


        audioUri?.let { uri ->
            audioreference.putFile(uri).addOnSuccessListener {
                var myUploadAudioRef = storegeref.child("audios").child(audioName)

                myUploadAudioRef.downloadUrl.addOnSuccessListener {
                    var d = Date()
                    var complaintDate: CharSequence = DateFormat.format("MMMM d,yyyy", d.time)
                    var cid = compRef.push().key
                    audioUrl = it.toString()

                    var compMap = mutableMapOf<String, Any>()
                    compMap["complaintSummary"] = dialogBind.compSumm.text.toString()
                    compMap["complaintAgainst"] = dialogBind.compAgainst.text.toString()
                    compMap["complaintDetails"] = dialogBind.comDetails.text.toString()
                    compMap["complaintDistrict"] = dialogBind.District.text.toString()
                    compMap["audioName"] = audioName
                    compMap["audioUrl"] = audioUrl
                    compMap["videoName"] = videoName
                    compMap["videoUrl"] = videoUrl
                    println("id->" + complaints.complaintId)
                    compRef.child(complaints.complaintId).updateChildren(compMap)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                Toast.makeText(
                                    requireContext(),
                                    "Updated Successfully",
                                    Toast.LENGTH_LONG
                                ).show()
                                dialogBind.progressbar.visibility = View.GONE
                                dialog.dismiss()
                                audioUri=null
                                videoUri=null
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    it.exception.toString(),
                                    Toast.LENGTH_LONG
                                ).show()
                                dialogBind.progressbar.visibility = View.GONE
                                audioUri=null
                                videoUri=null
                            }
                        }
                    println("Url 2-> " + audioUrl)
                }
            }.addOnFailureListener {

            }
        }


        videoUri?.let { uri ->
            videoreference.putFile(uri).addOnSuccessListener {
                var myUploadVideoRef = storegeref.child("videos").child(videoName)

                myUploadVideoRef.downloadUrl.addOnSuccessListener {
                    var d = Date()
                    var complaintDate: CharSequence = DateFormat.format("MMMM d,yyyy", d.time)
                    var cid = compRef.push().key
                    videoUrl = it.toString()

                    var compMap = mutableMapOf<String, Any>()
                    compMap["complaintSummary"] = dialogBind.compSumm.text.toString()
                    compMap["complaintAgainst"] = dialogBind.compAgainst.text.toString()
                    compMap["complaintDetails"] = dialogBind.comDetails.text.toString()
                    compMap["complaintDistrict"] = dialogBind.District.text.toString()
                    compMap["audioName"] = audioName
                    compMap["audioUrl"] = audioUrl
                    compMap["videoName"] = videoName
                    compMap["videoUrl"] = videoUrl
                    println("id->" + complaints.complaintId)
                    compRef.child(complaints.complaintId).updateChildren(compMap)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                dialogBind.progressbar.visibility = View.GONE
                                dialog.dismiss()
                                Toast.makeText(
                                    requireContext(),
                                    "Updated Successfully",
                                    Toast.LENGTH_LONG
                                ).show()
                                audioUri=null
                                videoUri=null
                            } else {
                                dialogBind.progressbar.visibility = View.GONE
                                Toast.makeText(
                                    requireContext(),
                                    it.exception.toString(),
                                    Toast.LENGTH_LONG
                                ).show()
                                audioUri=null
                                videoUri=null
                            }
                        }
                    println("Url 2-> " + audioUrl)
                }
            }.addOnFailureListener {

            }
        }
    }
}