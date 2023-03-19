package com.japnoor.anticorruption

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.japnoor.anticorruption.databinding.EditUserComplaintDialogBinding
import com.japnoor.anticorruption.databinding.EditUserDemandDialogBinding
import com.japnoor.anticorruption.databinding.FragmentUserDemandLettersBinding
import com.japnoor.anticorruption.databinding.ShowUserComplaintsDialogBinding
import com.japnoor.anticorruption.databinding.ShowUserDemandDialogBinding
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import kotlin.collections.ArrayList


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class UserTotalDemandFragment : Fragment(),UserDemandClick{
    private var param1: String? = null
    private var param2: String? = null
    lateinit var arrayAdapter: ArrayAdapter<String>
    lateinit var database : FirebaseDatabase
    lateinit var demRef : DatabaseReference

    lateinit var dialogBindEdit : EditUserDemandDialogBinding
    lateinit var loadDialog : Dialog

    lateinit var firebaseStorage: FirebaseStorage
    lateinit var storegeref: StorageReference

    var imageUrl : String=""

    var imageUri : Uri? =null

    lateinit var activityResulLauncher : ActivityResultLauncher<Intent>

    var demandList : ArrayList<DemandLetter> = ArrayList()
    lateinit var binding: FragmentUserDemandLettersBinding
    lateinit var userDemandAdapter: UserDemandAdapter
    lateinit var homeScreen: HomeScreen
    var encryptionKey: String? =null
    var secretKeySpec: SecretKeySpec? =null

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
    override fun onCreate(savedInstanceState: Bundle?) {
        registerActivityforResult()
        super.onCreate(savedInstanceState)
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
        storegeref=firebaseStorage.reference

        homeScreen=activity as HomeScreen
        database=FirebaseDatabase.getInstance()
        demRef=database.reference.child("Demand Letter")
        var forgot = ForogotPasscode()
        encryptionKey=forgot.key()
        secretKeySpec = SecretKeySpec(encryptionKey!!.toByteArray(), "AES")
        binding = FragmentUserDemandLettersBinding.inflate(layoutInflater, container, false)
binding.refreshlayout.setOnRefreshListener {
    homeScreen.navController.navigate(R.id.userTotalDemandFragment)
}
        loadDialog = Dialog(homeScreen)
        loadDialog.setContentView(R.layout.dialog_c_d_loading)
        loadDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

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
        // Inflate the layout for this fragment
        demRef.addValueEventListener(object : ValueEventListener, UserComplaintClick, UserDemandClick {
            override fun onDataChange(snapshot: DataSnapshot) {
                demandList.clear()
                for (eachDemand in snapshot.children) {
                    val demand = eachDemand.getValue(DemandLetter::class.java)

                    if (demand != null && demand.userId.equals(homeScreen.id)) {
                        demandList.add(demand)
                    }
                    demandList.reverse()
                    userDemandAdapter = UserDemandAdapter(homeScreen, demandList, this)
                    binding.recyclerView.layoutManager = LinearLayoutManager(homeScreen)
                    binding.recyclerView.adapter = userDemandAdapter
                    binding.shimmer.stopShimmer()
                    binding.shimmer.visibility=View.GONE
                    binding.recyclerView.visibility=View.VISIBLE
                    binding.search.addTextChangedListener(object : TextWatcher {
                        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                        }

                        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        }

                        override fun afterTextChanged(s: Editable?) {
                            var filteredList = ArrayList<DemandLetter>()
                            for (item in demandList){
                                if(item.demandSubject.toLowerCase().contains(s.toString().toLowerCase())
                                    || item.demandNumber.toLowerCase().contains(s.toString().toLowerCase())
                                    || item.demandDate.toLowerCase().contains(s.toString().toLowerCase())
                                    || item.demandTime.toLowerCase().contains(s.toString().toLowerCase())
                                )
                                    filteredList.add(item)
                            }
                            userDemandAdapter.FilteredList(filteredList)
                        }

                    })
                }
                binding.shimmer.stopShimmer()
                binding.shimmer.visibility=View.GONE
                binding.recyclerView.visibility=View.VISIBLE
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onClick(demandLetter: DemandLetter) {
                var dialog = Dialog(requireContext())
                if (demandLetter.status.equals("1")) {
                    var dialogBind = ShowUserDemandDialogBinding.inflate(layoutInflater)
                    dialog.setContentView(dialogBind.root)
                    dialog.window?.setLayout(
                        WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.WRAP_CONTENT
                    )
                    dialogBind.actionsTakenLayout.visibility=View.GONE
                    dialogBind.CS.setText("Demand Summary : ")
                    dialogBind.CD.setText("Demand Details : ")
                    dialogBind.CA.visibility=View.GONE
                    dialogBind.stamp.visibility=View.VISIBLE
                    dialogBind.tvAgainst.visibility=View.GONE
                    dialogBind.stamp.setImageResource(R.drawable.accpeted_stamp)
                    dialogBind.Framelayout.setBackgroundResource(R.color.accepted1)
                    dialogBind.tvSummary.setText(decrypt(demandLetter.demandSubject))
                    dialogBind.tvDetails.setText(decrypt(demandLetter.demandDetails))
                    dialogBind.comDate.setText(decrypt(demandLetter.demandDate))
                    dialogBind.tvDistrict.setText(decrypt(demandLetter.demandDistrict))

                    dialogBind.image.visibility = View.VISIBLE
                    dialogBind.audio.visibility = View.GONE
                    dialogBind.video.visibility = View.GONE

                    dialogBind.image.setOnClickListener {
                        var url=decrypt(demandLetter.imageUrl)
                        val fileUri: Uri = url.toUri()

                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.setDataAndType(fileUri, "image/*")
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) //DO NOT FORGET THIS EVER

                        startActivity(intent)
                    }

                    dialogBind.fabAdd1.setOnClickListener {
                        dialog.dismiss()
                    }

                    dialog.show()
                }
                 else if (demandLetter.status.equals("2")) {
                    var dialogBind = ShowUserDemandDialogBinding.inflate(layoutInflater)
                    dialog.setContentView(dialogBind.root)
                    dialog.window?.setLayout(
                        WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.WRAP_CONTENT
                    )

                    dialogBind.CS.setText("Demand Summary : ")
                    dialogBind.CD.setText("Demand Details : ")
                    dialogBind.CA.visibility=View.GONE
                    dialogBind.stamp.visibility=View.VISIBLE
                    dialogBind.tvAgainst.visibility=View.GONE
                    dialogBind.actionsTaken.setText(decrypt(demandLetter.statusDescription))
                    dialogBind.stamp.setImageResource(R.drawable.resolved_stamp)
                    dialogBind.Framelayout.setBackgroundResource(R.color.resolved1)
                    dialogBind.tvSummary.setText(decrypt(demandLetter.demandSubject))
                    dialogBind.tvDetails.setText(decrypt(demandLetter.demandDetails))
                    dialogBind.comDate.setText(decrypt(demandLetter.demandDate))
                    dialogBind.tvDistrict.setText(decrypt(demandLetter.demandDistrict))

                    dialogBind.image.visibility = View.VISIBLE
                    dialogBind.audio.visibility = View.GONE
                    dialogBind.video.visibility = View.GONE

                    dialogBind.image.setOnClickListener {
                         var url=decrypt(demandLetter.imageUrl)
                        val fileUri: Uri = url.toUri()

                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.setDataAndType(fileUri, "image/*")
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) //DO NOT FORGET THIS EVER

                        startActivity(intent)
                    }

                    dialogBind.fabAdd1.setOnClickListener {
                        dialog.dismiss()
                    }

                    dialog.show()
                }

                else if (demandLetter.status.equals("3")) {
                    var dialogBind = ShowUserDemandDialogBinding.inflate(layoutInflater)
                    dialog.setContentView(dialogBind.root)
                    dialog.window?.setLayout(
                        WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.WRAP_CONTENT
                    )
                    dialogBind.CS.setText("Demand Summary : ")
                    dialogBind.CD.setText("Demand Details : ")
                    dialogBind.CA.visibility=View.GONE
                    dialogBind.stamp.visibility=View.VISIBLE
                    dialogBind.tvAgainst.visibility=View.GONE
                    dialogBind.stamp.setImageResource(R.drawable.rejected_stamp)
                    dialogBind.Framelayout.setBackgroundResource(R.color.rejected1)
                    dialogBind.actionsTaken.setText(decrypt(demandLetter.statusDescription))
                    dialogBind.tvSummary.setText(decrypt(demandLetter.demandSubject))
                    dialogBind.tvDetails.setText(decrypt(demandLetter.demandDetails))
                    dialogBind.comDate.setText(decrypt(demandLetter.demandDate))
                    dialogBind.tvDistrict.setText(decrypt(demandLetter.demandDistrict))

                    dialogBind.image.visibility = View.VISIBLE
                    dialogBind.audio.visibility = View.GONE
                    dialogBind.video.visibility = View.GONE

                    dialogBind.image.setOnClickListener {
                        var url=decrypt(demandLetter.imageUrl)
                        val fileUri: Uri = url.toUri()
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.setDataAndType(fileUri, "image/*")
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) //DO NOT FORGET THIS EVER

                        startActivity(intent)
                    }

                    dialogBind.fabAdd1.setOnClickListener {
                        dialog.dismiss()
                    }

                    dialog.show()
                }
                else {
                    dialogBindEdit = EditUserDemandDialogBinding.inflate(layoutInflater)
                    dialog.setContentView(dialogBindEdit.root)
                    dialog.window?.setLayout(
                        WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.MATCH_PARENT
                    )

                    dialogBindEdit.Summ.setText(decrypt(demandLetter.demandSubject))
                    dialogBindEdit.Details.setText(decrypt(demandLetter.demandDetails))
                    dialogBindEdit.Date.setText(decrypt(demandLetter.demandDate))
                    dialogBindEdit.District.setText(decrypt(demandLetter.demandDistrict))

                    var districts = resources.getStringArray(R.array.District)
                    arrayAdapter =
                        ArrayAdapter(requireContext(), R.layout.drop_down_item, districts)
                    dialogBindEdit.District.setAdapter(arrayAdapter)
                    dialogBindEdit.fabAdd1.setOnClickListener {
                        val input: String = dialogBindEdit.Summ.getText().toString().trim()
                        val input1: String = dialogBindEdit.Details.getText().toString().trim()
                        if(input.length==0){
                            dialogBindEdit.Summ.requestFocus()
                            dialogBindEdit.Summ.error = "Cannot be empty"
                        }
                        else if(input1.length==0){
                            dialogBindEdit.Details.requestFocus()
                            dialogBindEdit.Details.error = "Cannot be empty"
                        }
                        else if(dialogBindEdit.District.text.isNullOrEmpty()){
                            dialogBindEdit.District.setError("Cannot be empty")
                            dialogBindEdit.District.requestFocus()
                        }
                        else if (imageUri == null) {
                            update(demandLetter,dialogBindEdit,dialog)
                        } else {
                             loadDialog.setCancelable(false)
                            loadDialog.show()
                            uploadDemandLetterAndImage(dialogBindEdit, demandLetter, dialog)
                        }
                    }

                    dialogBindEdit.image.setOnClickListener {
                        var url=decrypt(demandLetter.imageUrl)
                        val fileUri: Uri = url.toUri()

                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.setDataAndType(fileUri, "image/*")
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) //DO NOT FORGET THIS EVER

                        startActivity(intent)
                    }

                    dialogBindEdit.audioUpload.setOnClickListener {
                        if(imageUri==null) {
                            chooseImage()
                        }
                        else if(imageUri!=null){
                            imageUri=null
                            dialogBindEdit.audioUpload.setBackgroundResource(R.drawable.buttonbg)
                            dialogBindEdit.audioUpload.setImageResource(R.drawable.ic_baseline_file_upload_24)

                        }                    }

                    dialogBindEdit.fabAdd2.setOnClickListener {
                        var bottomSheet = BottomSheetDialog(requireContext())
                        bottomSheet.setContentView(R.layout.dialog_delete_users)
                        bottomSheet.show()
                        var text = bottomSheet.findViewById<TextView>(R.id.textmsg)
                        var tvYes = bottomSheet.findViewById<TextView>(R.id.tvYes)
                        var tvNo = bottomSheet.findViewById<TextView>(R.id.tvNo)
                        text?.setText("Are you sure you want \n to delete this Demand ?")
                        tvNo?.setOnClickListener {
                            bottomSheet.dismiss()
                        }
                        tvYes?.setOnClickListener {
                            demRef.child(demandLetter.demandId).removeValue().addOnCompleteListener {
                                storegeref.child("images").child(demandLetter.imageName).delete()
                            }
                            homeScreen.navController.navigate(R.id.homeFragment)
                            bottomSheet.dismiss()
                            dialog.dismiss()
                        }
                    }
                    dialog.show()
                }
            }

            override fun onClick(complaints: Complaints) {
                TODO("Not yet implemented")
            }
        })
        return binding.root
    }

    override fun onClick(demandLetter: DemandLetter) {

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

        activityResulLauncher=registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback { result ->
                var resultcode=result.resultCode
                var imageData=result.data

                if(resultcode== Activity.RESULT_OK && imageData!=null ) {

                    imageUri = imageData.data
                    println("Image - > " + imageUri)
                    if(imageUri!=null){
                        dialogBindEdit.audioUpload.setBackgroundResource(R.drawable.buttonbg1)
                        dialogBindEdit.audioUpload.setImageResource(R.drawable.ic_baseline_cancel_241)
                    }
                }

            })


    }

    fun update(demandLetter : DemandLetter,dialogBind : EditUserDemandDialogBinding,dialog : Dialog){
        var demMap= mutableMapOf<String,Any>()
        demMap["demandSubject"]=encrypt(dialogBind.Summ.text.toString())
        demMap["demandDetails"]=encrypt(dialogBind.Details.text.toString())
        demMap["demandDistrict"]=encrypt(dialogBind.District.text.toString())
        demRef.child(demandLetter.demandId).updateChildren(demMap).addOnCompleteListener {
            if(it.isSuccessful){
                dialogBind.progressbar.visibility = View.GONE
                dialog.dismiss()
            }
            else{
                Toast.makeText(requireContext(),it.exception.toString(), Toast.LENGTH_LONG).show()
                dialogBind.progressbar.visibility = View.GONE
                dialog.dismiss()
            }
        }
    }

    fun uploadDemandLetterAndImage(dialogBind : EditUserDemandDialogBinding,demandLetter: DemandLetter,dialog : Dialog){




        var imageName= demandLetter.imageName

        val imgreference=storegeref.child("images").child(imageName)

        imageUri?.let{ uri ->
            imgreference.putFile(uri).addOnSuccessListener {
                var myUploadImageRef=storegeref.child("images").child(imageName)

                myUploadImageRef.downloadUrl.addOnSuccessListener {
                    imageUrl=it.toString()
                    var demMap= mutableMapOf<String,Any>()
                    demMap["demandSubject"]=encrypt(dialogBind.Summ.text.toString())
                    demMap["demandDetails"]=encrypt(dialogBind.Details.text.toString())
                    demMap["demandDistrict"]=encrypt(dialogBind.District.text.toString())
                    demMap["imageUrl"]=encrypt(imageUrl)
                    demMap["imageName"]=imageName
                    demRef.child(demandLetter.demandId).updateChildren(demMap).addOnCompleteListener {
                        if(it.isSuccessful){
                            Toast.makeText(requireContext(),"Updated Successfully", Toast.LENGTH_LONG).show()
                            loadDialog.dismiss()
                            dialog.dismiss()
                            imageUri=null
                             }

                        else{
                            Toast.makeText(requireContext(),it.exception.toString(), Toast.LENGTH_LONG).show()
                            loadDialog.dismiss()
                            dialog.dismiss()
                            imageUri=null
                        }
                    }
                }
            }.addOnFailureListener{
            }
        }

    }


}