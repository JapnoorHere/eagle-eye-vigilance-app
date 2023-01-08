package com.japnoor.anticorruption

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity.RESULT_OK
import android.app.appsearch.SetSchemaRequest.READ_EXTERNAL_STORAGE
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.format.DateFormat
import androidx.fragment.app.Fragment
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
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.japnoor.anticorruption.databinding.FragmentAddDemandLetterBinding
import java.util.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AddDemandLetterFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null
    lateinit var homeScreen:HomeScreen
    lateinit var arrayAdapter: ArrayAdapter<String>
    lateinit var binding : FragmentAddDemandLetterBinding

     var userName : String=""
     var userPhone : String=""
    lateinit var userRef: DatabaseReference
     var userEmail : String=""

    lateinit var firebaseStorage : FirebaseStorage
    lateinit var storegeref : StorageReference
    lateinit var database: FirebaseDatabase
    lateinit var demRef : DatabaseReference

    var imageUrl : String=""

    var imageUri : Uri? =null

    lateinit var activityResulLauncher : ActivityResultLauncher<Intent>


    override fun onCreate(savedInstanceState: Bundle?) {
        registerActivityforResult()

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
        firebaseStorage= FirebaseStorage.getInstance()
        storegeref=firebaseStorage.reference

        binding =FragmentAddDemandLetterBinding.inflate(layoutInflater,container,false)
        homeScreen=activity as HomeScreen

        database=FirebaseDatabase.getInstance()
        demRef=database.reference.child("Demand Letter")
        userRef=database.reference.child("Users")

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
            else if (imageUri.toString().isNullOrEmpty()){
                Toast.makeText(homeScreen,"Upload an Image",Toast.LENGTH_LONG).show()
            }
            else{
                binding.progressbar.visibility=View.VISIBLE
                binding.btnSubmit.visibility=View.GONE
                uploadDemandLetterAndImage()
                userRef.addValueEventListener(object : ValueEventListener {
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
                println("Url->" + imageUrl)
            }
        }

        binding.addImage.setOnClickListener {
            chooseImage()


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
            intent.type="image/*"
            intent.action=Intent.ACTION_GET_CONTENT
            activityResulLauncher.launch(intent)
        }
    }


    fun chooseImage(){
        if(ContextCompat.checkSelfPermission(homeScreen,android.Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(homeScreen, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),1)
        }
        else{
            var intent = Intent()
            intent.type="image/*"
            intent.action=Intent.ACTION_GET_CONTENT
            activityResulLauncher.launch(intent)
        }
    }

    fun registerActivityforResult(){

        activityResulLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback { result ->
                var resultcode=result.resultCode
                var imageData=result.data

                if(resultcode== RESULT_OK  && imageData!=null )

                    imageUri=imageData.data
                println("Image - > " + imageUri)

            })


    }

    fun uploadDemandLetterAndImage(){

        var imageName=UUID.randomUUID().toString()

        val imgreference=storegeref.child("images").child(imageName)

        imageUri?.let{ uri ->
            imgreference.putFile(uri).addOnSuccessListener {
                var myUploadImageRef=storegeref.child("images").child(imageName)

                myUploadImageRef.downloadUrl.addOnSuccessListener {
                    var d = Date()
                    var demDate : CharSequence = DateFormat.format("MMMM d,yyyy", d.time)
                    var did = demRef.push().key
                    imageUrl=it.toString()
                    var demands = DemandLetter(
                        binding.DemandSubject.text.toString(),
                        binding.DemandDetails.text.toString(),
                        demDate.toString(),binding.District.text.toString()
                        ,homeScreen.id,
                        did.toString(),imageUrl,imageName,userName,userEmail,userPhone,"")

                    demRef.child(did.toString()).setValue(demands).addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(requireContext(), "Demand letter Submit", Toast.LENGTH_LONG)
                                .show()
                            binding.progressbar.visibility=View.GONE
                            binding.btnSubmit.visibility=View.VISIBLE
                            homeScreen.navController.navigate(R.id.homeFragment)
                        } else {
                            binding.progressbar.visibility=View.GONE
                            binding.btnSubmit.visibility=View.VISIBLE
                            Toast.makeText(requireContext(), it.exception.toString(), Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                    println("Url 2-> " + imageUrl)
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
