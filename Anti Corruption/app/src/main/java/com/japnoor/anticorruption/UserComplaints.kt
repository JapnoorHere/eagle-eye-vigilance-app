package com.japnoor.anticorruption

import android.annotation.SuppressLint
import android.app.*
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.view.*
import android.util.Base64
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
import com.japnoor.anticorruption.databinding.DialogCDLoadingBinding
import com.japnoor.anticorruption.databinding.EditUserComplaintDialogBinding
import com.japnoor.anticorruption.databinding.FragmentUserComplaintsBinding
import com.japnoor.anticorruption.databinding.ShowUserComplaintsDialogBinding
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
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

    lateinit var dialogBindEdit: EditUserComplaintDialogBinding





    lateinit var activityResulLauncher: ActivityResultLauncher<Intent>
    lateinit var activityResulLauncher2: ActivityResultLauncher<Intent>
    lateinit var activityResulLauncher3: ActivityResultLauncher<Intent>
    var audioUrl: String = ""
    var audioUri: Uri? = null

    var videoUrl: String = ""
    var videoUri: Uri? = null

    var imageUrl: String = ""
    var imageUri: Uri? = null

    lateinit var database: FirebaseDatabase
    lateinit var compRef: DatabaseReference
    var c = 0
    lateinit var loadDialog: Dialog
    lateinit var loadDialogBind : DialogCDLoadingBinding

    var encryptionKey: String? =null
    var secretKeySpec: SecretKeySpec? =null
    lateinit var binding: FragmentUserComplaintsBinding

    lateinit var homeScreen: HomeScreen

    private fun decrypt(input: String): String {
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec)
        val decryptedBytes = cipher.doFinal(android.util.Base64.decode(input, android.util.Base64.DEFAULT))
        return String(decryptedBytes, Charsets.UTF_8)
    }

    private fun encrypt(input: String): String {
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec)
        val encryptedBytes = cipher.doFinal(input.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        registerActivityforResult()
        registerActivityforResult2()
        registerActivityforResult3()

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
        var forgot = ForogotPasscode()
        encryptionKey=forgot.key()
        secretKeySpec = SecretKeySpec(encryptionKey!!.toByteArray(), "AES")
        firebaseStorage = FirebaseStorage.getInstance()
        storegeref = firebaseStorage.reference
        homeScreen = activity as HomeScreen
        database = FirebaseDatabase.getInstance()
        compRef = database.reference.child("Complaints")

        binding = FragmentUserComplaintsBinding.inflate(layoutInflater, container, false)

        loadDialog = Dialog(homeScreen)
        loadDialogBind= DialogCDLoadingBinding.inflate(layoutInflater)
        loadDialog.setContentView(loadDialogBind.root)
        loadDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

//        val notificationManager = homeScreen.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        val channelId = "default"
//        val channelName = "Default Channel"
//        val importance = NotificationManager.IMPORTANCE_HIGH
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(channelId, channelName, importance)
//            notificationManager.createNotificationChannel(channel)
//        }
//
//        val notificationBuilder = androidx.core.app.NotificationCompat.Builder(homeScreen, channelId)
//            .setContentTitle("My Notification")
//            .setContentText("This is my notification message.")
//            .setPriority(NotificationCompat.PRIORITY_HIGH)
//            .setSmallIcon(R.drawable.ic_baseline_home_24)
//            .setAutoCancel(true)
//
//        val notificationIntent = Intent(homeScreen, HomeScreen::class.java)
//        notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
//        val pendingIntent = PendingIntent.getActivity(homeScreen, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
//        notificationBuilder.setContentIntent(pendingIntent)
//
//        val notificationId = 1
//        notificationManager.notify(notificationId, notificationBuilder.build())
//
//        val alarmManager = homeScreen.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//        val notificationTime = System.currentTimeMillis() + 5000 // 5 seconds from now
//        val notificationBroadcastIntent = Intent(homeScreen, HomeScreen::class.java)
//        val pendingBroadcastIntent = PendingIntent.getBroadcast(homeScreen, 0, notificationBroadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT)
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, notificationTime, pendingBroadcastIntent)
//        } else {
//            alarmManager.set(AlarmManager.RTC_WAKEUP, notificationTime, pendingBroadcastIntent)
//        }


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
                    complaintsList.reverse()
                    myComplaintsAdapter = MyComplaintsAdapter(homeScreen, complaintsList, this)
                    binding.recyclerView.layoutManager = LinearLayoutManager(homeScreen)
                    binding.recyclerView.adapter = myComplaintsAdapter
                    binding.shimmer.visibility = View.GONE
                    binding.shimmer.stopShimmer()
                    binding.recyclerView.visibility = View.VISIBLE
                    binding.search.addTextChangedListener(object : TextWatcher {
                        override fun beforeTextChanged(
                            s: CharSequence?,
                            start: Int,
                            count: Int,
                            after: Int
                        ) {
                        }

                        override fun onTextChanged(
                            s: CharSequence?,
                            start: Int,
                            before: Int,
                            count: Int
                        ) {
                        }

                        override fun afterTextChanged(s: Editable?) {
                            var filteredList = ArrayList<Complaints>()
                            for (item in complaintsList) {
                                if (decrypt(item.complaintAgainst).toLowerCase()
                                        .contains(s.toString().toLowerCase())
                                    || decrypt(item.complaintNumber).toLowerCase()
                                        .contains(s.toString().toLowerCase())
                                    || decrypt(item.complaintDate).toLowerCase()
                                        .contains(s.toString().toLowerCase())
                                    || decrypt(item.complaintDistrict).toLowerCase()
                                        .contains(s.toString().toLowerCase())
                                    || decrypt(item.complaintTime).toLowerCase()
                                        .contains(s.toString().toLowerCase())
                                )
                                    filteredList.add(item)
                            }
                            myComplaintsAdapter.FilteredList(filteredList)
                        }

                    })
                }
                binding.shimmer.visibility = View.GONE
                binding.shimmer.stopShimmer()
                binding.recyclerView.visibility = View.VISIBLE

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
                    dialogBind.actionsTakenLayout.visibility = View.GONE
                    dialogBind.Framelayout.setBackgroundResource(R.color.accepted1)
                    dialogBind.stamp.setImageResource(R.drawable.accpeted_stamp)
                    dialogBind.tvDept.setText(decrypt(complaints.complaintDept))
                    dialogBind.tvLocation.setText(decrypt(complaints.complaintLoc))
                    dialogBind.tvCategory.setText(decrypt(complaints.complaintCategory))
                    dialogBind.tvDetails.setText(decrypt(complaints.complaintDetails))
                    dialogBind.tvAgainst.setText(decrypt(complaints.complaintAgainst))
                    dialogBind.comDate.setText(decrypt(complaints.complaintDate))
                    dialogBind.tvDistrict.setText(decrypt(complaints.complaintDistrict))

                    dialogBind.fabAdd1.setOnClickListener {
                        dialog.dismiss()
                    }
                    dialogBind.image.setOnClickListener {
                        var url=decrypt(complaints.imageUrl)
                        val fileUri: Uri = url.toUri()
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.setDataAndType(fileUri, "image/*")
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) //DO NOT FORGET THIS EVER
                        startActivity(intent)
                    }
                    dialogBind.audio.setOnClickListener {
                        var url=decrypt(complaints.audioUrl)
                        val fileUri: Uri = url.toUri()
                        var intent = Intent(homeScreen, AudioActivity::class.java)
                        intent.putExtra("audio", fileUri.toString())
                        homeScreen.startActivity(intent)
                    }
                    dialogBind.video.setOnClickListener {
                        var url=decrypt(complaints.videoUrl)
                        val fileUri: Uri = url.toUri()
                        var intent = Intent(homeScreen, VideoActivity::class.java)
                        intent.putExtra("video", fileUri.toString())
                        homeScreen.startActivity(intent)
                    }


                    if (decrypt(complaints.audioUrl).isNullOrEmpty()) {
                        dialogBind.audio.visibility = View.GONE
                        dialogBind.audio.visibility = View.GONE
                    }

                    if (decrypt(complaints.imageUrl).isNullOrEmpty()) {
                        dialogBind.image.visibility = View.GONE
                        dialogBind.image.visibility = View.GONE
                    }

                    if (decrypt(complaints.videoUrl).isNullOrEmpty()) {
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
                    dialogBind.actionsTaken.setText(decrypt(complaints.statusDescription))
                    dialogBind.stamp.setImageResource(R.drawable.resolved_stamp)
                    dialogBind.Framelayout.setBackgroundResource(R.color.resolved1)
                    dialogBind.tvDept.setText(decrypt(complaints.complaintDept))
                    dialogBind.tvLocation.setText(decrypt(complaints.complaintLoc))
                    dialogBind.tvCategory.setText(decrypt(complaints.complaintCategory))
                    dialogBind.tvDetails.setText(decrypt(complaints.complaintDetails))
                    dialogBind.tvAgainst.setText(decrypt(complaints.complaintAgainst))
                    dialogBind.comDate.setText(decrypt(complaints.complaintDate))
                    dialogBind.tvDistrict.setText(decrypt(complaints.complaintDistrict))

                    dialogBind.fabAdd1.setOnClickListener {
                        dialog.dismiss()
                    }
                    dialogBind.image.setOnClickListener {
                        var url=decrypt(complaints.imageUrl)
                        val fileUri: Uri = url.toUri()
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.setDataAndType(fileUri, "image/*")
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) //DO NOT FORGET THIS EVER

                        startActivity(intent)
                    }
                    dialogBind.audio.setOnClickListener {
                        var url=decrypt(complaints.audioUrl)
                        val fileUri: Uri = url.toUri()
                        var intent = Intent(homeScreen, AudioActivity::class.java)
                        intent.putExtra("audio", fileUri.toString())
                        homeScreen.startActivity(intent)

                    }
                    dialogBind.video.setOnClickListener {
                        var url=decrypt(complaints.videoUrl)
                        val fileUri: Uri = url.toUri()
                        var intent = Intent(homeScreen, VideoActivity::class.java)
                        intent.putExtra("video", fileUri.toString())
                        homeScreen.startActivity(intent)
                    }


                    if (decrypt(complaints.audioUrl).isNullOrEmpty()) {
                        dialogBind.audio.visibility = View.GONE
                        dialogBind.audio.visibility = View.GONE
                    }

                    if (decrypt(complaints.imageUrl).isNullOrEmpty()) {
                        dialogBind.image.visibility = View.GONE
                        dialogBind.image.visibility = View.GONE
                    }


                    if (decrypt(complaints.videoUrl).isNullOrEmpty()) {
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
                    dialogBind.actionsTaken.setText(decrypt(complaints.statusDescription))
                    dialogBind.Framelayout.setBackgroundResource(R.color.rejected1)
                    dialogBind.stamp.setImageResource(R.drawable.rejected_stamp)
                    dialogBind.tvDept.setText(decrypt(complaints.complaintDept))
                    dialogBind.tvLocation.setText(decrypt(complaints.complaintLoc))
                    dialogBind.tvCategory.setText(decrypt(complaints.complaintCategory))
                    dialogBind.tvDetails.setText(decrypt(complaints.complaintDetails))
                    dialogBind.tvAgainst.setText(decrypt(complaints.complaintAgainst))
                    dialogBind.comDate.setText((decrypt(complaints.complaintDate)))
                    dialogBind.tvDistrict.setText(decrypt(complaints.complaintDistrict))

                    dialogBind.fabAdd1.setOnClickListener {
                        dialog.dismiss()
                    }
                    dialogBind.audio.setOnClickListener {
                        var url=decrypt(complaints.audioUrl)
                        val fileUri: Uri = url.toUri()
                        var intent = Intent(homeScreen, AudioActivity::class.java)
                        intent.putExtra("audio", fileUri.toString())
                        homeScreen.startActivity(intent)

                    }
                    dialogBind.image.setOnClickListener {
                        var url=decrypt(complaints.imageUrl)
                        val fileUri: Uri = url.toUri()
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.setDataAndType(fileUri, "image/*")
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) //DO NOT FORGET THIS EVER

                        startActivity(intent)

                    }
                    dialogBind.video.setOnClickListener {
                        var url=decrypt(complaints.videoUrl)
                        val fileUri: Uri = url.toUri()

                        var intent = Intent(homeScreen, VideoActivity::class.java)
                        intent.putExtra("video", fileUri.toString())
                        homeScreen.startActivity(intent)
                    }
                    if (decrypt(complaints.imageUrl).isNullOrEmpty()) {
                        dialogBind.image.visibility = View.GONE
                        dialogBind.image.visibility = View.GONE
                    }


                    if (decrypt(complaints.audioUrl).isNullOrEmpty()) {
                        dialogBind.audio.visibility = View.GONE
                        dialogBind.audio.visibility = View.GONE
                    }

                    if (decrypt(complaints.videoUrl).isNullOrEmpty()) {
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

                    dialogBindEdit.compDept.setText(decrypt(complaints.complaintDept))
                    dialogBindEdit.compLoca.setText(decrypt(complaints.complaintLoc))
                    dialogBindEdit.compCategory.setText(decrypt(complaints.complaintCategory))
                    dialogBindEdit.comDetails.setText(decrypt(complaints.complaintDetails))
                    dialogBindEdit.compAgainst.setText(decrypt(complaints.complaintAgainst))
                    dialogBindEdit.comDate.setText(decrypt(complaints.complaintDate))
                    dialogBindEdit.District.setText(decrypt(complaints.complaintDistrict))
                    var districts = resources.getStringArray(R.array.District)
                    arrayAdapter =
                        ArrayAdapter(requireContext(), R.layout.drop_down_item, districts)
                    dialogBindEdit.District.setAdapter(arrayAdapter)

                    var suspectCatory = resources.getStringArray(R.array.SuspectCategory)
                    arrayAdapter =
                        ArrayAdapter(requireContext(), R.layout.drop_down_item, suspectCatory)
                    dialogBindEdit.compCategory.setAdapter(arrayAdapter)


                    dialogBindEdit.fabAdd1.setOnClickListener {
                        val input: String = dialogBindEdit.compDept.getText().toString().trim()
                        val input1: String = dialogBindEdit.compAgainst.getText().toString().trim()
                        val input2: String = dialogBindEdit.comDetails.getText().toString().trim()
                        val input3: String = dialogBindEdit.compLoca.getText().toString().trim()
                        if (input.length == 0) {
                            dialogBindEdit.compDept.requestFocus()
                            dialogBindEdit.compDept.error = "Cannot be empty"
                        } else if (input1.length == 0) {
                            dialogBindEdit.compAgainst.requestFocus()
                            dialogBindEdit.compAgainst.error = "Cannot be empty"
                        } else if (input2.length == 0) {
                            dialogBindEdit.comDetails.requestFocus()
                            dialogBindEdit.comDetails.error = "Cannot be empty"
                        } else if (input3.length == 0) {
                            dialogBindEdit.compLoca.requestFocus()
                            dialogBindEdit.compLoca.error = "Cannot be empty"
                        } else if (dialogBindEdit.District.text.isNullOrEmpty()) {
                            dialogBindEdit.District.requestFocus()
                            dialogBindEdit.District.error = "Cannot be empty"
                        } else if (dialogBindEdit.compCategory.text.isNullOrEmpty()) {
                            dialogBindEdit.compCategory.requestFocus()
                            dialogBindEdit.compCategory.error = "Cannot be empty"
                        } else if (audioUri == null && videoUri == null && imageUri == null) {
                            update(dialogBindEdit, complaints, dialog)

                        } else {
                            loadDialog.show()
                            loadDialog.setCancelable(false)
                            uploadComplaintandAudio(dialogBindEdit, complaints, dialog)
                        }
                    }
                    dialogBindEdit.audio.setOnClickListener {
                        var url=decrypt(complaints.audioUrl)
                        val fileUri: Uri = url.toUri()
                        var intent = Intent(homeScreen, AudioActivity::class.java)
                        intent.putExtra("audio", fileUri.toString())
                        homeScreen.startActivity(intent)
                    }

                    dialogBindEdit.image.setOnClickListener {
                        var url=decrypt(complaints.imageUrl)
                        val fileUri: Uri = url.toUri()
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.setDataAndType(fileUri, "image/*")
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) //DO NOT FORGET THIS EVER

                        startActivity(intent)

                    }

                    dialogBindEdit.video.setOnClickListener {
                        var url=decrypt(complaints.videoUrl)
                        val fileUri: Uri = url.toUri()
                        var intent = Intent(homeScreen, VideoActivity::class.java)
                        intent.putExtra("video", fileUri.toString())
                        homeScreen.startActivity(intent)
                    }


                    if (decrypt(complaints.audioUrl).isNullOrEmpty()) {
                        dialogBindEdit.audioUpload.visibility = View.GONE
                        dialogBindEdit.audio.visibility = View.GONE
                    }

                    if (decrypt(complaints.imageUrl).isNullOrEmpty()) {
                        dialogBindEdit.imageUpload.visibility = View.GONE
                        dialogBindEdit.image.visibility = View.GONE
                    }

                    if (decrypt(complaints.videoUrl).isNullOrEmpty()) {
                        dialogBindEdit.videoUpload.visibility = View.GONE
                        dialogBindEdit.video.visibility = View.GONE
                    }

                    dialogBindEdit.audioUpload.setOnClickListener {
                        if (audioUri == null) {
                            chooseAudio()
                        } else if (audioUri != null) {
                            audioUri = null
                            dialogBindEdit.audioUpload.setBackgroundResource(R.drawable.buttonbg)
                            dialogBindEdit.audioUpload.setImageResource(R.drawable.ic_baseline_file_upload_24)

                        }
                    }

                    dialogBindEdit.videoUpload.setOnClickListener {
                        if (videoUri == null) {
                            chooseVideo()
                        } else if (videoUri != null) {
                            videoUri = null
                            dialogBindEdit.videoUpload.setBackgroundResource(R.drawable.buttonbg)
                            dialogBindEdit.videoUpload.setImageResource(R.drawable.ic_baseline_file_upload_24)

                        }
                    }

                    dialogBindEdit.imageUpload.setOnClickListener {
                        if (imageUri == null) {
                            chooseImage()
                        } else if (imageUri != null) {
                            imageUri = null
                            dialogBindEdit.imageUpload.setBackgroundResource(R.drawable.buttonbg)
                            dialogBindEdit.imageUpload.setImageResource(R.drawable.ic_baseline_file_upload_24)
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

                            if (complaints.videoUrl.isNullOrEmpty())
                                storegeref.child("audios").child(complaints.audioName).delete()
                            else if (complaints.audioUrl.isNullOrEmpty())
                                storegeref.child("videos").child(complaints.videoName).delete()

//                            complaints?.audioName?.let { it1 ->
//                                storegeref.child("audios").child(it1).delete()
//                                println("it > " + it)
//                            }
//                            complaints?.videoName?.let { it1 ->
//                                storegeref.child("videos").child(it1).delete()
//                            }
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

    fun chooseImage() {
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
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            activityResulLauncher3.launch(intent)
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
                if (audioUri != null) {
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
                    if (videoUri != null) {
                        dialogBindEdit.videoUpload.setBackgroundResource(R.drawable.buttonbg1)
                        dialogBindEdit.videoUpload.setImageResource(R.drawable.ic_baseline_cancel_241)
                    }
                }
            })


    }

    fun registerActivityforResult3() {

        activityResulLauncher3 = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback { result ->
                var resultcode = result.resultCode
                var imageData = result.data

                if (resultcode == Activity.RESULT_OK && imageData != null) {
                    imageUri = imageData.data
                    if (imageUri != null) {
                        dialogBindEdit.imageUpload.setBackgroundResource(R.drawable.buttonbg1)
                        dialogBindEdit.imageUpload.setImageResource(R.drawable.ic_baseline_cancel_241)
                    }
                }
            })


    }

    fun update(
        dialogBind: EditUserComplaintDialogBinding,
        complaints: Complaints,
        dialog: Dialog
    ) {
        var compMap = mutableMapOf<String, Any>()
        compMap["complaintLoc"] = encrypt(dialogBind.compLoca.text.toString())
        compMap["complaintDept"] = encrypt(dialogBind.compDept.text.toString())
        compMap["complaintCategory"] = encrypt(dialogBind.compCategory.text.toString())
        compMap["complaintAgainst"] = encrypt(dialogBind.compAgainst.text.toString())
        compMap["complaintDetails"] = encrypt(dialogBind.comDetails.text.toString())
        compMap["complaintDistrict"] = encrypt(dialogBind.District.text.toString())
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

        audioUri?.let { uri ->
            var audioName = complaints.audioName
            val audioreference = storegeref.child("audios").child(audioName)
            audioreference.putFile(uri).addOnProgressListener {
                val progress = (100.0 * it.bytesTransferred / it.totalByteCount).toInt()
                loadDialogBind.progressBar.progress = progress
                loadDialogBind.progressTV.setText("$progress%")
            }.addOnSuccessListener {
                var myUploadAudioRef = storegeref.child("audios").child(audioName)

                myUploadAudioRef.downloadUrl.addOnSuccessListener {
                    var d = Date()
                    var complaintDate: CharSequence = DateFormat.format("MMMM d,yyyy", d.time)
                    var cid = compRef.push().key
                    audioUrl = it.toString()

                    var compMap = mutableMapOf<String, Any>()
                    compMap["complaintLoc"] = encrypt(dialogBind.compLoca.text.toString())
                    compMap["complaintDept"] = encrypt(dialogBind.compDept.text.toString())
                    compMap["complaintCategory"] = encrypt(dialogBind.compCategory.text.toString())
                    compMap["complaintAgainst"] =encrypt(dialogBind.compAgainst.text.toString())
                    compMap["complaintDetails"] = encrypt(dialogBind.comDetails.text.toString())
                    compMap["complaintDistrict"] = encrypt(dialogBind.District.text.toString())
                    compMap["audioName"] = audioName
                    compMap["audioUrl"] = encrypt(audioUrl)
                    compMap["videoName"] = ""
                    compMap["videoUrl"] = ""
                    compMap["imageName"] = ""
                    compMap["imageUrl"] = ""
                    println("id->" + complaints.complaintId)
                    compRef.child(complaints.complaintId).updateChildren(compMap)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                Toast.makeText(
                                    requireContext(),
                                    "Updated Successfully",
                                    Toast.LENGTH_LONG
                                ).show()
                                loadDialog.dismiss()
                                dialog.dismiss()
                                audioUri = null
                                videoUri = null
                                imageUri = null
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    it.exception.toString(),
                                    Toast.LENGTH_LONG
                                ).show()
                                loadDialog.dismiss()
                                audioUri = null
                                videoUri = null
                                imageUri = null
                            }
                        }
                    println("Url 2-> " + audioUrl)
                }
            }.addOnFailureListener {

            }
        }


        videoUri?.let { uri ->
            var videoName = complaints.videoName
            val videoreference = storegeref.child("videos").child(videoName)
            videoreference.putFile(uri).addOnProgressListener {
                val progress = (100.0 * it.bytesTransferred / it.totalByteCount).toInt()
                loadDialogBind.progressBar.progress = progress
                loadDialogBind.progressTV.setText("$progress%")
            }.addOnSuccessListener {
                var myUploadVideoRef = storegeref.child("videos").child(videoName)

                myUploadVideoRef.downloadUrl.addOnSuccessListener {
                    var d = Date()
                    var complaintDate: CharSequence = DateFormat.format("MMMM d,yyyy", d.time)
                    var cid = compRef.push().key
                    videoUrl = it.toString()

                    var compMap = mutableMapOf<String, Any>()
                    compMap["complaintLoc"] = encrypt(dialogBind.compLoca.text.toString())
                    compMap["complaintDept"] = encrypt(dialogBind.compDept.text.toString())
                    compMap["complaintCategory"] = encrypt(dialogBind.compCategory.text.toString())
                    compMap["complaintAgainst"] = encrypt(dialogBind.compAgainst.text.toString())
                    compMap["complaintDetails"] = encrypt(dialogBind.comDetails.text.toString())
                    compMap["complaintDistrict"] = encrypt(dialogBind.District.text.toString())
                    compMap["audioName"] = ""
                    compMap["audioUrl"] = ""
                    compMap["videoName"] = videoName
                    compMap["videoUrl"] = encrypt(videoUrl)
                    compMap["imageName"] = ""
                    compMap["imageUrl"] = ""
                    println("id->" + complaints.complaintId)
                    compRef.child(complaints.complaintId).updateChildren(compMap)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                loadDialog.dismiss()
                                dialog.dismiss()
                                Toast.makeText(
                                    requireContext(),
                                    "Updated Successfully",
                                    Toast.LENGTH_LONG
                                ).show()
                                audioUri = null
                                videoUri = null
                                imageUri = null
                            } else {
                                loadDialog.dismiss()
                                Toast.makeText(
                                    requireContext(),
                                    it.exception.toString(),
                                    Toast.LENGTH_LONG
                                ).show()
                                audioUri = null
                                videoUri = null
                                imageUri = null
                            }
                        }
                    println("Url 2-> " + audioUrl)
                }
            }.addOnFailureListener {

            }
        }

        imageUri?.let { uri ->
            var imageName = complaints.imageName
            val imagereference = storegeref.child("cimages").child(imageName)
            imagereference.putFile(uri).addOnProgressListener {
                val progress = (100.0 * it.bytesTransferred / it.totalByteCount).toInt()
                loadDialogBind.progressBar.progress = progress
                loadDialogBind.progressTV.setText("$progress%")
            }.addOnSuccessListener {
                var myUploadVideoRef = storegeref.child("cimages").child(imageName)

                myUploadVideoRef.downloadUrl.addOnSuccessListener {
                    var d = Date()
                    var complaintDate: CharSequence = DateFormat.format("MMMM d,yyyy", d.time)
                    var cid = compRef.push().key
                    imageUrl = it.toString()

                    var compMap = mutableMapOf<String, Any>()
                    compMap["complaintLoc"] = encrypt(dialogBind.compLoca.text.toString())
                    compMap["complaintDept"] = encrypt(dialogBind.compDept.text.toString())
                    compMap["complaintCategory"] = encrypt(dialogBind.compCategory.text.toString())
                    compMap["complaintAgainst"] = encrypt(dialogBind.compAgainst.text.toString())
                    compMap["complaintDetails"] = encrypt(dialogBind.comDetails.text.toString())
                    compMap["complaintDistrict"] = encrypt(dialogBind.District.text.toString())
                    compMap["audioName"] = ""
                    compMap["audioUrl"] = ""
                    compMap["videoName"] = ""
                    compMap["videoUrl"] = ""
                    compMap["imageName"] = imageName
                    compMap["imageUrl"] = encrypt(imageUrl)
                    println("id->" + complaints.complaintId)
                    compRef.child(complaints.complaintId).updateChildren(compMap)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                loadDialog.dismiss()
                                dialog.dismiss()
                                Toast.makeText(
                                    requireContext(),
                                    "Updated Successfully",
                                    Toast.LENGTH_LONG
                                ).show()
                                audioUri = null
                                videoUri = null
                                imageUri = null
                            } else {
                                loadDialog.dismiss()
                                Toast.makeText(
                                    requireContext(),
                                    it.exception.toString(),
                                    Toast.LENGTH_LONG
                                ).show()
                                audioUri = null
                                videoUri = null
                                imageUri = null
                            }
                        }
                    println("Url 2-> " + audioUrl)
                }
            }.addOnFailureListener {

            }
        }
    }
}