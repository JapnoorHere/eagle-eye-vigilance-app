package com.japnoor.anticorruption

import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.provider.DocumentsContract
import android.text.format.DateFormat
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.japnoor.anticorruption.databinding.FragmentDemandLetterBinding
import com.japnoor.anticorruption.utils.UriPathHelper
import java.util.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class DemandLetterFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var homeScreen:HomeScreen
    lateinit var arrayAdapter: ArrayAdapter<String>
    lateinit var binding:FragmentDemandLetterBinding
    var image : String?=null
    var isReadPermissionGranted = false
    var isWritePermissionGranted = false
    var isRecordAudioGranted = false


    var permissionLauncher=registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
        isReadPermissionGranted= it[android.Manifest.permission.READ_EXTERNAL_STORAGE] ?: isReadPermissionGranted
        isWritePermissionGranted= it[android.Manifest.permission.WRITE_EXTERNAL_STORAGE] ?: isWritePermissionGranted
        isRecordAudioGranted= it[android.Manifest.permission.RECORD_AUDIO] ?: isRecordAudioGranted
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
    var getImage = registerForActivityResult(ActivityResultContracts.GetContent()){
        System.out.println("audio path $it")
        if (it != null) {
            isVirtualFile(it)
            val uriPathHelper = UriPathHelper()
            image = uriPathHelper.getPath(homeScreen, it)
            System.out.println("file path $image")
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


        if(permissionRequest.isNotEmpty()){
            permissionLauncher.launch(permissionRequest.toTypedArray())
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDemandLetterBinding.inflate(layoutInflater,container,false)
        homeScreen=activity as HomeScreen
        requestPermmission()
        binding.btnSubmit.setOnClickListener {
            if (binding.DemandSubject.text.isNullOrEmpty()) {
              binding.DemandSubject.setError("Enter Subject of Demand")
                binding.DemandSubject.requestFocus()
            }
            else if (binding.DemandDetails.text.isNullOrEmpty()) {
              binding.DemandDetails.setError("Enter details of Demand")
                binding.DemandDetails.requestFocus()
            }
            else if(binding.District.text.isNullOrEmpty()){
                binding.District.setError("Enter District")
                binding.District.requestFocus()
            }
            else{
                var d = Date()
                var demDate : CharSequence = DateFormat.format("MMMM d,yyyy", d.time)
             addDemand(binding.DemandSubject.text.toString(),binding.DemandDetails.text.toString(),binding.District.text.toString(),demDate.toString(),"")

            }
        }
        binding.addImage.setOnClickListener {
            isRecordAudioGranted=
                ContextCompat.checkSelfPermission(requireContext(),android.Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED
            val permissionRequest : MutableList<String> = ArrayList()

            if(!isRecordAudioGranted){
                permissionRequest.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }else{
                getImage.launch("image/*")
            }

            if(permissionRequest.isNotEmpty()){
                permissionLauncher.launch(permissionRequest.toTypedArray())
            }

        }

        return binding.root
    }
    override fun onResume() {
        super.onResume()
        var districts = resources.getStringArray(R.array.District)
        arrayAdapter = ArrayAdapter(requireContext(), R.layout.drop_down_item, districts)
        binding.District.setAdapter(arrayAdapter)
    }
    fun addDemand(
        demSumm: String,
        demDetails: String,
        demDistrict: String,
        demDate: String,
        demStatus: String,
    ) {
        class Add: AsyncTask<Void, Void, Void>(){
            override fun doInBackground(vararg p0: Void?): Void? {
                var demandEntity = DemandEntity()
                demandEntity.demandSummary=demSumm
                demandEntity.demandDetails=demDetails
                demandEntity.districtDemand=demDistrict
                demandEntity.dateDemand=demDate
                demandEntity.uId = homeScreen.signUpEntity?.id?:-1
                demandEntity.status=demStatus
                demandEntity.image=image
                Datbase.getDatabase(requireContext()).dao().addDemand(demandEntity)
                return null
            }

            override fun onPostExecute(result: Void?) {
                super.onPostExecute(result)
                homeScreen.navController.navigate(R.id.homeFragment)
            }

        }
        Add().execute()
    }

}