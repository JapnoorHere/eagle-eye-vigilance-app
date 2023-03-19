package com.japnoor.anticorruption


import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle

import android.text.format.DateFormat
import android.util.Base64
import android.util.Base64.encodeToString
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
import androidx.core.net.toUri

import androidx.fragment.app.Fragment
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.japnoor.anticorruption.databinding.FragmentAddComplaintBinding
import com.japnoor.anticorruption.databinding.InstructionsBlockedUserDialogBinding
import java.text.SimpleDateFormat
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AddComplaintFragment : Fragment() {
    lateinit var sharedPreferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor

    private var param1: String? = null
    private var param2: String? = null

    lateinit var firebaseStorage: FirebaseStorage
    lateinit var storegeref: StorageReference

    private var REQUEST_CAMERA_PERMISSION = 100
    private var REQUEST_CAMERA = 100
    lateinit var activityResulLauncher: ActivityResultLauncher<Intent>
    lateinit var activityResulLauncher2: ActivityResultLauncher<Intent>
    lateinit var activityResulLauncher3: ActivityResultLauncher<Intent>

    lateinit var binding: FragmentAddComplaintBinding
    lateinit var homeScreen: HomeScreen

    lateinit var database: FirebaseDatabase
    lateinit var compRef: DatabaseReference
    lateinit var userrrRef: DatabaseReference
    lateinit var loadDialog: Dialog

    lateinit var arrayAdapter: ArrayAdapter<String>


    var audioUrl: String = ""
    var audioUri: Uri? = null
    var userName: String = ""
    var userEmail: String = ""
    var videoUrl: String = ""
    var videoUri: Uri? = null

    var imageUrl: String = ""
    var imageUri: Uri? = null

     var encryptionKey: String? =null
     var secretKeySpec: SecretKeySpec? =null


    override fun onCreate(savedInstanceState: Bundle?) {
        registerActivityforResult()
        registerActivityforResult2()
        registerActivityforResult3()
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
    private fun encrypt(input: String): String {
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec)
        val encryptedBytes = cipher.doFinal(input.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
    }

    private fun decrypt(input: String): String {
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec)
        val decryptedBytes = cipher.doFinal(Base64.decode(input, Base64.DEFAULT))
        return String(decryptedBytes, Charsets.UTF_8)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddComplaintBinding.inflate(layoutInflater, container, false)
        var forgot = ForogotPasscode()
        encryptionKey=forgot.key()
          secretKeySpec = SecretKeySpec(encryptionKey!!.toByteArray(), "AES")

//        var str="I am Japnoor Singh"
//        println("PPP " + encrypt(str))
//        println("PPP " + decrypt(encrypt(str)))

        firebaseStorage = FirebaseStorage.getInstance()
        storegeref = firebaseStorage.reference
        homeScreen = activity as HomeScreen
        sharedPreferences = homeScreen.getSharedPreferences("Instructions", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()


        loadDialog = Dialog(homeScreen)
        loadDialog.setContentView(R.layout.dialog_c_d_loading)
        loadDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        var checkInstOnce = sharedPreferences.getString("instructionsOnce", null)
        if (sharedPreferences.contains("instructionsOnce") && checkInstOnce.equals("0") && !(sharedPreferences.contains(
                "instRemind"
            ))
        ) {
            var dialog = Dialog(homeScreen)
            var diaologB = InstructionsBlockedUserDialogBinding.inflate(layoutInflater)
            dialog.setContentView(diaologB.root)
            dialog.show()
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            diaologB.radiogroup.setOnCheckedChangeListener { group, checkeId ->
                when (checkeId) {
                    R.id.reminInst -> {
                        editor.putString("instRemind", "1")
                        editor.apply()
                        editor.commit()
                    }
                }
            }
            diaologB.ok.setOnClickListener {
                dialog.dismiss()
                editor.putString("instructionsOnce", "1")
                editor.apply()
                editor.commit()
            }
        }

        database = FirebaseDatabase.getInstance()
        compRef = database.reference.child("Complaints")
        userrrRef = database.reference.child("Users")


        arguments.let {
            audioUri = it?.getString("audio")?.toUri()
            videoUri = it?.getString("video")?.toUri()
            println("hnji uri" + audioUri.toString())
        }
        if (audioUri != null) {
            binding.addAudio.setText("  Audio Selected")
            binding.addAudio.setCompoundDrawablesWithIntrinsicBounds(
                resources.getDrawable(R.drawable.ic_baseline_mic_24),
                null,
                resources.getDrawable(R.drawable.ic_baseline_cancel_24),
                null
            )
            binding.addAudio.setBackgroundResource(R.drawable.upload_photo1)
        }
        else if (audioUri == null) {
            binding.addAudio.setText("  Add Audio")
            binding.addAudio.setCompoundDrawablesWithIntrinsicBounds(
                resources.getDrawable(R.drawable.ic_baseline_mic_24),
                null,
                resources.getDrawable(R.drawable.ic_baseline_control_point_24),
                null
            )
            binding.addAudio.setBackgroundResource(R.drawable.upload_photo)

        }
        if (videoUri != null) {
            binding.addVideo.setText("  Video Selected")
            binding.addVideo.setCompoundDrawablesWithIntrinsicBounds(
                resources.getDrawable(R.drawable.ic_baseline_videocam_24),
                null,
                resources.getDrawable(R.drawable.ic_baseline_cancel_24),
                null
            )
            binding.addVideo.setBackgroundResource(R.drawable.upload_photo1)
        } else if (videoUri != null) {
            binding.addVideo.setText(" Add Video")
            binding.addVideo.setCompoundDrawablesWithIntrinsicBounds(
                resources.getDrawable(R.drawable.ic_baseline_videocam_24),
                null,
                resources.getDrawable(R.drawable.ic_baseline_control_point_24),
                null
            )
            binding.addVideo.setBackgroundResource(R.drawable.upload_photo)
        }

        binding.addAudio.setOnClickListener {
            if (audioUri == null && videoUri == null && imageUri==null) {
                if (Build.VERSION.RELEASE >= "13") {
                    var intent = Intent()
                    intent.type = "audio/*"
                    intent.action = Intent.ACTION_GET_CONTENT
                    activityResulLauncher2.launch(intent)
                } else {
                    chooseAudio()
                }
            }
            else if (audioUri != null) {
                audioUri = null
                binding.addAudio.setText("  Add Audio")
                binding.addAudio.setCompoundDrawablesWithIntrinsicBounds(
                    resources.getDrawable(R.drawable.ic_baseline_mic_24),
                    null,
                    resources.getDrawable(R.drawable.ic_baseline_control_point_24),
                    null
                )
                binding.addAudio.setBackgroundResource(R.drawable.upload_photo)

            } else {
                Toast.makeText(homeScreen,  "Either You can choose image,video or audio", Toast.LENGTH_LONG)
                    .show()
            }
        }
        binding.addVideo.setOnClickListener {
            if (audioUri == null && videoUri == null && imageUri==null) {
                if (Build.VERSION.RELEASE >= "13") {
                    var intent = Intent()
                    intent.type = "video/*"
                    intent.action = Intent.ACTION_GET_CONTENT
                    activityResulLauncher2.launch(intent)
                } else {
                    chooseVideo()
                }
            } else if (videoUri != null) {
                videoUri = null
                binding.addVideo.setText(" Add Video")
                binding.addVideo.setCompoundDrawablesWithIntrinsicBounds(
                    resources.getDrawable(R.drawable.ic_baseline_videocam_24),
                    null,
                    resources.getDrawable(R.drawable.ic_baseline_control_point_24),
                    null
                )
                binding.addVideo.setBackgroundResource(R.drawable.upload_photo)
            } else {
                Toast.makeText(
                    homeScreen,
                    " Either You can choose image,video or audio",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        binding.addImage.setOnClickListener {
            if (audioUri == null && videoUri == null && imageUri==null) {
                if (Build.VERSION.RELEASE >= "13") {
                    var intent = Intent()
                    intent.type = "image/*"
                    intent.action = Intent.ACTION_GET_CONTENT
                    activityResulLauncher3.launch(intent)
                } else {
                    chooseImage()
                }
            } else if (imageUri != null) {
                imageUri = null
                binding.addImage.setText(" Add Image")
                binding.addImage.setCompoundDrawablesWithIntrinsicBounds(
                    resources.getDrawable(R.drawable.ic_baseline_image_24),
                    null,
                    resources.getDrawable(R.drawable.ic_baseline_control_point_24),
                    null
                )
                binding.addImage.setBackgroundResource(R.drawable.upload_photo)
            } else {
                Toast.makeText(
                    homeScreen,
                    " Either You can choose image,video or audio",
                    Toast.LENGTH_LONG
                ).show()
            }
        }


        binding.btnSubmit.setOnClickListener {
            val input: String = binding.compDept.getText().toString().trim()
            val input1: String = binding.compAgainst.getText().toString().trim()
            val input2: String = binding.comDetails.getText().toString().trim()
            val input3: String = binding.compLoc.getText().toString().trim()
            if (input1.length == 0) {
                binding.compAgainst.requestFocus()
                binding.compAgainst.error = "Cannot be empty"
            } else if (input.length == 0) {
                binding.compDept.requestFocus()
                binding.compDept.error = "Cannot be empty"
            } else if (binding.compCategory.text.isNullOrEmpty()) {
                binding.compCategory.requestFocus()
                binding.compCategory.error = "Cannot be empty"
            } else if (input2.length == 0) {
                binding.comDetails.requestFocus()
                binding.comDetails.error = "Cannot be empty"
            } else if (input3.length == 0) {
                binding.compLoc.requestFocus()
                binding.compLoc.error = "Cannot be empty"
            } else if (binding.comDetails.text.isNullOrEmpty()) {
                binding.comDetails.requestFocus()
                binding.comDetails.error = "Cannot be empty"
            } else if (binding.compLoc.text.isNullOrEmpty()) {
                binding.compLoc.requestFocus()
                binding.compLoc.error = "Cannot be empty"
            } else if (binding.compAgainst.text.isNullOrEmpty()) {
                binding.compAgainst.requestFocus()
                binding.compAgainst.error = "Cannot be empty"
            } else if (binding.comDetails.text.isNullOrEmpty()) {
                binding.comDetails.requestFocus()
                binding.comDetails.error = "Cannot be empty"
            } else if (binding.District.text.isNullOrEmpty()) {
                binding.District.requestFocus()
                binding.District.error = "Cannot be empty"
            } else if (audioUri == null && videoUri == null && imageUri==null) {
                Toast.makeText(homeScreen, "Upload Video, Audio or Image for proof", Toast.LENGTH_LONG).show()
            } else {
                val connectivityManager =
                    homeScreen.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
                val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
                if (isConnected) {
                    homeScreen.isSensorActive=false
                    binding.addAudio.isClickable = false
                    binding.addVideo.isClickable = false
                    binding.addImage.isClickable = false
                    loadDialog.show()
                    loadDialog.setCancelable(false)
                    uploadComplaintandAudio()
                    userrrRef.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            for (eachUser in snapshot.children) {
                                var user = eachUser.getValue(Users::class.java)
                                println("uSers" + user?.userId)
                                if (user != null && user.userId.equals(homeScreen.id)) {
                                    userName = decrypt(user.name).toString()
                                    userEmail = decrypt(user.email).toString()
                                    println("name" + userName)
                                    break
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }
                    })
                } else {
                    Toast.makeText(
                        homeScreen,
                        "Check your internet connection please",
                        Toast.LENGTH_LONG
                    ).show()

                }
            }
        }
        return binding.root
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                var intent = Intent()
                intent.type = "audio/*"
                intent.action = Intent.ACTION_GET_CONTENT
                activityResulLauncher.launch(intent)
            }
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
                REQUEST_CAMERA_PERMISSION
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
                REQUEST_CAMERA
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
                REQUEST_CAMERA
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
                    binding.addAudio.setText("  Audio Selected")
                    binding.addAudio.setCompoundDrawablesWithIntrinsicBounds(
                        resources.getDrawable(R.drawable.ic_baseline_mic_24),
                        null,
                        resources.getDrawable(R.drawable.ic_baseline_cancel_24),
                        null
                    )
                    binding.addAudio.setBackgroundResource(R.drawable.upload_photo1)
                }
            })


    }

    fun registerActivityforResult2() {

        activityResulLauncher2 = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback { result ->
                var resultcode = result.resultCode
                var videoData = result.data

                if (resultcode == Activity.RESULT_OK && videoData != null)
                    videoUri = videoData.data
                if (videoUri != null) {
                    binding.addVideo.setText("  Video Selected")
                    binding.addVideo.setCompoundDrawablesWithIntrinsicBounds(
                        resources.getDrawable(R.drawable.ic_baseline_videocam_24),
                        null,
                        resources.getDrawable(R.drawable.ic_baseline_cancel_24),
                        null
                    )
                    binding.addVideo.setBackgroundResource(R.drawable.upload_photo1)
                }
            })
    }
        fun registerActivityforResult3() {
            activityResulLauncher3 = registerForActivityResult(
                ActivityResultContracts.StartActivityForResult(),
                ActivityResultCallback { result ->
                    var resultcode = result.resultCode
                    var imageData = result.data

                    if (resultcode == Activity.RESULT_OK && imageData != null)
                        imageUri = imageData.data
                    if (imageUri != null) {
                        binding.addImage.setText("  Image Selected")
                        binding.addImage.setCompoundDrawablesWithIntrinsicBounds(
                            resources.getDrawable(R.drawable.ic_baseline_image_24),
                            null,
                            resources.getDrawable(R.drawable.ic_baseline_cancel_24),
                            null
                        )
                        binding.addImage.setBackgroundResource(R.drawable.upload_photo1)
                    }
                })

    }

    fun uploadComplaintandAudio() {

        var audioName = compRef.push().key.toString()

//        println(audioName)
//        println(videoName)

        val audioreference = storegeref.child("audios").child(audioName)
        println("let" + audioUri.toString())
        audioUri?.let { uri ->
            audioreference.putFile(uri).addOnSuccessListener {
                var myUploadAudioRef = storegeref.child("audios").child(audioName)
                myUploadAudioRef.downloadUrl.addOnSuccessListener {
                    var d = Date()
                    var complaintDate: CharSequence = DateFormat.format("MMMM d,yyyy", d.time)
                    var cid = compRef.push().key
                    audioUrl = it.toString()
                    var timestamp = System.currentTimeMillis()
                    val randomNumber = (100..999).random()
                    var timestamp1 = timestamp.toString().substring(0, 5)
                    var complaintNumber = "$timestamp1$randomNumber"
                    println("Number->" + complaintNumber)

                    val format = SimpleDateFormat("HH:mm", Locale.getDefault())
                    val complaintTime = format.format(Date())
                    var complaints = Complaints(
                        encrypt(binding.comDetails.text.toString()),
                        encrypt(binding.compLoc.text.toString()),
                        encrypt(binding.compCategory.text.toString()),
                        encrypt(binding.compAgainst.text.toString()),
                        encrypt(binding.comDetails.text.toString()),
                        encrypt(binding.District.text.toString()),
                        homeScreen.id,
                        encrypt(complaintDate.toString()),
                        cid.toString(),
                        audioName, encrypt(audioUrl),
                        "",
                        videoUrl, encrypt(userName),
                        encrypt(userEmail), encrypt(userEmail), "", encrypt(complaintNumber), encrypt(complaintTime), "","",imageUrl
                    )

                    compRef.child(cid.toString()).setValue(complaints)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                homeScreen.isSensorActive=true
                                Toast.makeText(
                                    requireContext(),
                                    "Complaint Submit",
                                    Toast.LENGTH_LONG
                                ).show()
                                loadDialog.dismiss()

                                homeScreen.navController.navigate(R.id.homeFragment)
                            } else {
                                homeScreen.isSensorActive=true
                                loadDialog.dismiss()
                                Toast.makeText(
                                    requireContext(),
                                    task.exception.toString(),
                                    Toast.LENGTH_LONG
                                )
                                    .show()
                            }
                        }
                    println("Url 2-> " + audioUrl)
                }
            }.addOnFailureListener {

            }
        }
        videoUri?.let { uri ->
            var videoName = compRef.push().key.toString()
            val videoreference = storegeref.child("videos").child(videoName)
            videoreference.putFile(uri).addOnSuccessListener {
                var myUploadVideoRef = storegeref.child("videos").child(videoName)
                myUploadVideoRef.downloadUrl.addOnSuccessListener {
                    var d = Date()
                    var complaintDate: CharSequence = DateFormat.format("MMMM d,yyyy", d.time)
                    var cid = compRef.push().key
                    videoUrl = it.toString()

                    var timestamp = System.currentTimeMillis()
                    val randomNumber = (100..999).random()
                    var timestamp1 = timestamp.toString().substring(0, 5)
                    var complaintNumber = "$timestamp1$randomNumber"

                    val format = SimpleDateFormat("HH:mm", Locale.getDefault())
                    val complaintTime = format.format(Date())
                    var complaints = Complaints(
                        encrypt(binding.compDept.text.toString()),
                        encrypt(binding.compLoc.text.toString()),
                        encrypt(binding.compCategory.text.toString()),
                        encrypt(binding.compAgainst.text.toString()),
                        encrypt(binding.comDetails.text.toString()),
                        encrypt(binding.District.text.toString()),
                        homeScreen.id,
                        encrypt(complaintDate.toString()),
                        cid.toString(), "", audioUrl, videoName, encrypt(videoUrl),
                        encrypt(userName), encrypt(userEmail), encrypt(userEmail), "", encrypt(complaintNumber),
                        encrypt(complaintTime), "","",imageUrl
                    )

                    compRef.child(cid.toString()).setValue(complaints)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                homeScreen.isSensorActive=true

                                Toast.makeText(
                                    requireContext(),
                                    "Complaint Submit",
                                    Toast.LENGTH_LONG
                                )
                                    .show()
                                loadDialog.dismiss()
                                homeScreen.navController.navigate(R.id.homeFragment)
                            } else {
                                homeScreen.isSensorActive=true

                                loadDialog.dismiss()
                                Toast.makeText(
                                    requireContext(),
                                    task.exception.toString(),
                                    Toast.LENGTH_LONG
                                )
                                    .show()
                            }
                        }
                    println("Url 2-> " + audioUrl)
                }
            }.addOnFailureListener {

            }
        }

        imageUri?.let { uri ->
            var imageName = compRef.push().key.toString()
            val imagereference = storegeref.child("cimages").child(imageName)
            imagereference.putFile(uri).addOnSuccessListener {
                var myUploadImageRef = storegeref.child("cimages").child(imageName)
                myUploadImageRef.downloadUrl.addOnSuccessListener {
                    var d = Date()
                    var complaintDate: CharSequence = DateFormat.format("MMMM d,yyyy", d.time)
                    var cid = compRef.push().key
                    imageUrl = it.toString()

                    var timestamp = System.currentTimeMillis()
                    val randomNumber = (100..999).random()
                    var timestamp1 = timestamp.toString().substring(0, 5)
                    var complaintNumber = "$timestamp1$randomNumber"

                    val format = SimpleDateFormat("HH:mm", Locale.getDefault())
                    val complaintTime = format.format(Date())
                    var complaints = Complaints(
                        encrypt(binding.compDept.text.toString()),
                        encrypt(binding.compLoc.text.toString()),
                        encrypt(binding.compCategory.text.toString()),
                        encrypt(binding.compAgainst.text.toString()),
                        encrypt(binding.comDetails.text.toString()),
                        encrypt(binding.District.text.toString()),
                        homeScreen.id,
                        encrypt(complaintDate.toString()),
                        cid.toString(), "", audioUrl, "", videoUrl,
                        encrypt(userName), encrypt(userEmail), encrypt(userEmail), "", encrypt(complaintNumber),
                        encrypt(complaintTime), "",imageName.toString(),encrypt(imageUrl)
                    )

                    compRef.child(cid.toString()).setValue(complaints)
                        .addOnCompleteListener { task ->

                            if (task.isSuccessful) {
                                homeScreen.isSensorActive=true

                                Toast.makeText(
                                    requireContext(),
                                    "Complaint Submit",
                                    Toast.LENGTH_LONG
                                )
                                    .show()
                                loadDialog.dismiss()
                                homeScreen.navController.navigate(R.id.homeFragment)
                            } else {
                                homeScreen.isSensorActive=true

                                loadDialog.dismiss()
                                Toast.makeText(
                                    requireContext(),
                                    task.exception.toString(),
                                    Toast.LENGTH_LONG
                                )
                                    .show()
                            }
                        }
                    println("Url 2-> " + audioUrl)
                }
            }.addOnFailureListener {

            }
        }


    }


    override fun onResume() {
        super.onResume()
        var districts = resources.getStringArray(R.array.District)
        arrayAdapter = ArrayAdapter(requireContext(), R.layout.drop_down_item, districts)
        binding.District.setAdapter(arrayAdapter)

        var suspectCatory = resources.getStringArray(R.array.SuspectCategory)
        arrayAdapter = ArrayAdapter(requireContext(), R.layout.drop_down_item, suspectCatory)
        binding.compCategory.setAdapter(arrayAdapter)
    }

}