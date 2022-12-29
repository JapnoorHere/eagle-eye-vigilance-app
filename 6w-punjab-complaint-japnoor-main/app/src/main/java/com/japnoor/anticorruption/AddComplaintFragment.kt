package com.japnoor.anticorruption

import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.japnoor.anticorruption.databinding.FragmentAddComplaintBinding
import com.japnoor.anticorruption.utils.UriPathHelper
import java.util.*


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
class AddComplaintFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private val PICK_AUDIO = 1
    lateinit var arrayAdapter: ArrayAdapter<String>
    lateinit var binding : FragmentAddComplaintBinding
    lateinit var homeScreen: HomeScreen
    var clicked=false
    var isReadPermissionGranted = false
    var isWritePermissionGranted = false
    var isRecordAudioGranted = false
    var audioo:String?=null
    var videoPath:String?=null
  //  lateinit var permissionLauncher : ActivityResultLauncher<Array<String>>
    var audio : MediaStore.Audio?=null
   var permissionLauncher=registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
        isReadPermissionGranted= it[android.Manifest.permission.READ_EXTERNAL_STORAGE] ?: isReadPermissionGranted
        isWritePermissionGranted= it[android.Manifest.permission.WRITE_EXTERNAL_STORAGE] ?: isWritePermissionGranted
        isRecordAudioGranted= it[android.Manifest.permission.RECORD_AUDIO] ?: isRecordAudioGranted

    }

    var getPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()){
            isGranted->
        if(isGranted){
            Toast.makeText(requireContext(), "Granted", Toast.LENGTH_SHORT).show()
            val audio = Intent()
            audio.type = "audio/*"
            audio.action = Intent.ACTION_OPEN_DOCUMENT
            startActivityForResult(Intent.createChooser(audio, "Select Audio"), PICK_AUDIO)
            getAudio.launch("audio/*")
        }else{
            Toast.makeText(requireContext(),"Not Granted", Toast.LENGTH_SHORT).show()
        }
    }

    var getAudio = registerForActivityResult(ActivityResultContracts.GetContent()){
        System.out.println("audio path $it")
        if (it != null) {
            isVirtualFile(it)
            val uriPathHelper = UriPathHelper()
            audioo = uriPathHelper.getPath(homeScreen, it)
            System.out.println("file path $audioo")
        }
    }
    var getVideo = registerForActivityResult(ActivityResultContracts.GetContent()){
        System.out.println("audio path $it")
        if (it != null) {
            isVirtualFile(it)
            val uriPathHelper = UriPathHelper()
            videoPath = uriPathHelper.getPath(homeScreen, it)
            System.out.println("file path $videoPath")
        }
    }

    private fun isVirtualFile(uri: Uri): Boolean {
        if (!DocumentsContract.isDocumentUri(homeScreen, uri)) {
            return false
        }

        val cursor: Cursor? = homeScreen.contentResolver.query(
            uri,
            arrayOf(DocumentsContract.Document.COLUMN_FLAGS),
            null,
            null,
            null
        )

        val flags: Int = cursor?.use {
            if (cursor.moveToFirst()) {
                cursor.getInt(0)
            } else {
                0
            }
        } ?: 0

        return flags and DocumentsContract.Document.FLAG_VIRTUAL_DOCUMENT != 0
    }

    fun requestPermmission(){
        isReadPermissionGranted=
            ContextCompat.checkSelfPermission(requireContext(),android.Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED
        isWritePermissionGranted=
            ContextCompat.checkSelfPermission(requireContext(),android.Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED
        isRecordAudioGranted=
            ContextCompat.checkSelfPermission(requireContext(),android.Manifest.permission.RECORD_AUDIO)== PackageManager.PERMISSION_GRANTED
        val permissionRequest : MutableList<String> = ArrayList()

        if(!isReadPermissionGranted){
            permissionRequest.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if(!isWritePermissionGranted){
            permissionRequest.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if(!isRecordAudioGranted){
            permissionRequest.add(android.Manifest.permission.RECORD_AUDIO)
        }

        if(permissionRequest.isNotEmpty()){
            permissionLauncher.launch(permissionRequest.toTypedArray())
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeScreen = activity as HomeScreen
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentAddComplaintBinding.inflate(layoutInflater,container,false)

        requestPermmission()

        binding.addAudio.setOnClickListener {
            isRecordAudioGranted=
                ContextCompat.checkSelfPermission(requireContext(),android.Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED
            val permissionRequest : MutableList<String> = ArrayList()

            if(!isRecordAudioGranted){
                permissionRequest.add(android.Manifest.permission.RECORD_AUDIO)
            }else{
                getAudio.launch("audio/*")

            }

            if(permissionRequest.isNotEmpty()){
                permissionLauncher.launch(permissionRequest.toTypedArray())
            }

        }
        binding.addVideo.setOnClickListener {
            isRecordAudioGranted=
                ContextCompat.checkSelfPermission(requireContext(),android.Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED
            val permissionRequest : MutableList<String> = ArrayList()

            if(!isRecordAudioGranted){
                permissionRequest.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }else{
                getVideo.launch("video/*")
            }

            if(permissionRequest.isNotEmpty()){
                permissionLauncher.launch(permissionRequest.toTypedArray())
            }

        }
        binding.btnSubmit.setOnClickListener {
            if(binding.compSumm.text.isNullOrEmpty()){
                binding.compSumm.requestFocus()
                binding.compSumm.error="Summary cannot be empty"
            }
            else if(binding.compAgainst.text.isNullOrEmpty()){
                binding.compAgainst.requestFocus()
                binding.compAgainst.error="Cannot be empty"
            }
            else if(binding.comDetails.text.isNullOrEmpty()){
                binding.comDetails.requestFocus()
                binding.comDetails.error="Cannot be empty"
            }
            else if(binding.District.text.isNullOrEmpty()){
                binding.District.requestFocus()
                binding.District.error="Cannot be empty"
            }
            else{
                Toast.makeText(requireContext(),"Complaint Submit Successfully",Toast.LENGTH_LONG).show()
                var d = Date()
                var complaintDate : CharSequence = DateFormat.format("MMMM d,yyyy", d.time)
                addComplaint(binding.compSumm.text.toString(),binding.compAgainst.text.toString(),binding.comDetails.text.toString(),binding.District.text.toString(),complaintDate.toString(),"", audioo.toString())
            }
        }

        return binding.root
    }


    fun addComplaint(
        compSummary: String,
        compAgainst: String,
        compDetails: String,
        district: String,
        date: String,
        status: String,audio:String
    ) {
        class Add:AsyncTask<Void,Void,Void>(){
            override fun doInBackground(vararg p0: Void?): Void? {
                var complaintsEntity = ComplaintsEntity()
                complaintsEntity.complaintSummary=compSummary
                complaintsEntity.complaintAgainst=compAgainst
                complaintsEntity.complaintDetails=compDetails
                complaintsEntity.selectDistrict=district
                complaintsEntity.date=date
                complaintsEntity.uId = homeScreen.signUpEntity?.id?:-1
                complaintsEntity.status=status
                complaintsEntity.audio=audioo
                complaintsEntity.video=videoPath
                System.out.println("audio $audio")
                Datbase.getDatabase(requireContext()).dao().addComplaint(complaintsEntity)
                return null
            }

            override fun onPostExecute(result: Void?) {
                super.onPostExecute(result)
                homeScreen.navController.navigate(R.id.homeFragment)
            }
        }
        Add().execute()
    }


    override fun onResume() {
        super.onResume()
        var districts = resources.getStringArray(R.array.District)
        arrayAdapter = ArrayAdapter(requireContext(), R.layout.drop_down_item, districts)
        binding.District.setAdapter(arrayAdapter)
    }


}