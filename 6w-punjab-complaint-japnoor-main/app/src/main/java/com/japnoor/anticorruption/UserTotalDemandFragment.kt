package com.japnoor.anticorruption

import android.app.Dialog
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.provider.DocumentsContract
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.japnoor.anticorruption.databinding.EditUserDemandDialogBinding
import com.japnoor.anticorruption.databinding.FragmentUserTotalDemandBinding
import com.japnoor.anticorruption.databinding.ShowUserComplaintsDialogBinding
import com.japnoor.anticorruption.utils.UriPathHelper
import java.io.File


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class UserTotalDemandFragment : Fragment(),UserDemandClick{
    private var param1: String? = null
    private var param2: String? = null
    lateinit var arrayAdapter: ArrayAdapter<String>
    var image : String?=null

    lateinit var binding: FragmentUserTotalDemandBinding
    var arrayList: ArrayList<DemandEntity> = ArrayList()
    lateinit var adapter: UserTotalDemandAdapter
    lateinit var homeScreen: HomeScreen
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
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
    var getImage = registerForActivityResult(ActivityResultContracts.GetContent()){
        System.out.println("audio path $it")
        if (it != null) {
            isVirtualFile(it)
            val uriPathHelper = UriPathHelper()
            image = uriPathHelper.getPath(homeScreen, it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeScreen=activity as HomeScreen
        binding = FragmentUserTotalDemandBinding.inflate(layoutInflater, container, false)
        adapter = UserTotalDemandAdapter(arrayList,this)
        // Inflate the layout for this fragment
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
        getUserTotalDemands()
        return binding.root
    }
    fun getUserTotalDemands() {
        arrayList.clear()
        System.out.println("homeScreen.signUpEntity?.id ${homeScreen.signUpEntity?.id}")
        class Get : AsyncTask<Void, Void, Void>() {
            override fun doInBackground(vararg p0: Void?): Void? {
                arrayList.addAll(
                    Datbase.getDatabase(requireContext()).dao().getUserDemands(homeScreen.signUpEntity?.id ?: -1))
                return null
            }
            override fun onPostExecute(result: Void?) {
                super.onPostExecute(result)
                adapter.notifyDataSetChanged()
            }
        }
        Get().execute()
    }
    override fun onClick(demandEntity: DemandEntity) {
        var dialog = Dialog(requireContext())
        if(demandEntity.status.equals("acc")) {
            var dialogBind = ShowUserComplaintsDialogBinding.inflate(layoutInflater)
            dialog.setContentView(dialogBind.root)
            dialog.window?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )

            dialogBind.CS.setText("Demand Summary : ")
            dialogBind.CD.setText("Demand Details : ")
            dialogBind.CA.visibility=View.GONE
            dialogBind.tvAgainst.visibility=View.GONE
            dialogBind.stamp.visibility=View.VISIBLE
            dialogBind.stamp.setImageResource(R.drawable.accpeted_stamp)
            dialogBind.tvSummary.setText(demandEntity.demandSummary)
            dialogBind.tvDetails.setText(demandEntity.demandDetails)
            dialogBind.comDate.setText(demandEntity.dateDemand)
            dialogBind.tvDistrict.setText(demandEntity.districtDemand)
            dialogBind.Framelayout.setBackgroundResource(R.color.accepted1)
            dialogBind.image.visibility = View.VISIBLE
            dialogBind.audio.visibility = View.GONE
            dialogBind.video.visibility = View.GONE
            dialogBind.image.setOnClickListener {
                val imageFile = File(demandEntity.image)
                val fileUri: Uri =
                    FileProvider.getUriForFile(
                        homeScreen,
                        BuildConfig.APPLICATION_ID + ".provider",
                        imageFile
                    )

                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(fileUri, "image/*")
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) //DO NOT FORGET THIS EVER

                startActivity(intent)
            }

            dialogBind.fabAdd1.setOnClickListener {
                dialog.dismiss()
            }
        }else if(demandEntity.status.equals("res")) {
            var dialogBind = ShowUserComplaintsDialogBinding.inflate(layoutInflater)
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
            dialogBind.stamp.setImageResource(R.drawable.resolved_stamp)
            dialogBind.tvSummary.setText(demandEntity.demandSummary)
            dialogBind.tvDetails.setText(demandEntity.demandDetails)
            dialogBind.comDate.setText(demandEntity.dateDemand)
            dialogBind.tvDistrict.setText(demandEntity.districtDemand)

            dialogBind.image.visibility = View.VISIBLE
            dialogBind.audio.visibility = View.GONE
            dialogBind.video.visibility = View.GONE
            dialogBind.image.setOnClickListener {
                val imageFile = File(demandEntity.image)
                val fileUri: Uri =
                    FileProvider.getUriForFile(
                        homeScreen,
                        BuildConfig.APPLICATION_ID + ".provider",
                        imageFile
                    )

                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(fileUri, "image/*")
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) //DO NOT FORGET THIS EVER

                startActivity(intent)
            }
            dialogBind.fabAdd1.setOnClickListener {
                dialog.dismiss()
            }
        }else if(demandEntity.status.equals("rej")) {
            var dialogBind = ShowUserComplaintsDialogBinding.inflate(layoutInflater)
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
            dialogBind.tvSummary.setText(demandEntity.demandSummary)
            dialogBind.tvDetails.setText(demandEntity.demandDetails)
            dialogBind.comDate.setText(demandEntity.dateDemand)
            dialogBind.tvDistrict.setText(demandEntity.districtDemand)
            dialogBind.Framelayout.setBackgroundResource(R.color.rejected1)

            dialogBind.image.visibility = View.VISIBLE
            dialogBind.audio.visibility = View.GONE
            dialogBind.video.visibility = View.GONE
            dialogBind.image.setOnClickListener {
                val imageFile = File(demandEntity.image)
                val fileUri: Uri =
                    FileProvider.getUriForFile(
                        homeScreen,
                        BuildConfig.APPLICATION_ID + ".provider",
                        imageFile
                    )

                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(fileUri, "image/*")
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) //DO NOT FORGET THIS EVER

                startActivity(intent)
            }
            dialogBind.fabAdd1.setOnClickListener {
                dialog.dismiss()
            }
        }
        else {

            var dialogBind = EditUserDemandDialogBinding.inflate(layoutInflater)
            dialog.setContentView(dialogBind.root)
            dialog.window?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )

            dialogBind.Summ.setText(demandEntity.demandSummary)
            dialogBind.Details.setText(demandEntity.demandDetails)
            dialogBind.Date.setText(demandEntity.dateDemand)
            dialogBind.District.setText(demandEntity.districtDemand)

            var districts = resources.getStringArray(R.array.District)
            arrayAdapter = ArrayAdapter(requireContext(), R.layout.drop_down_item, districts)
            dialogBind.District.setAdapter(arrayAdapter)
            dialogBind.fabAdd1.setOnClickListener {
                demandEntity.demandSummary = dialogBind.Summ.text.toString()
                demandEntity.demandDetails = dialogBind.Details.text.toString()
                demandEntity.districtDemand = dialogBind.District.text.toString()
                demandEntity.image=image
                updateDemand(demandEntity)
                dialog.dismiss()
            }

            dialogBind.audioUpload.setOnClickListener{
                getImage.launch("image/*")
            }

            dialogBind.image.setOnClickListener {
                if (demandEntity.image.isNullOrEmpty())
                    Toast.makeText(requireContext(), "Image is not attached", Toast.LENGTH_LONG)
                        .show()
                else {
                    val imageFile = File(demandEntity.image)
                    val fileUri: Uri =
                        FileProvider.getUriForFile(
                            homeScreen,
                            BuildConfig.APPLICATION_ID + ".provider",
                            imageFile
                        )

                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.setDataAndType(fileUri, "image/*")
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) //DO NOT FORGET THIS EVER

                    startActivity(intent)
                }
            }
            dialogBind.fabAdd2.setOnClickListener {
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
                    deleteDemand(demandEntity)
                    bottomSheet.dismiss()
                    dialog.dismiss()
                }
            }
        }
        dialog.show()
    }

    fun deleteDemand(demandEntity: DemandEntity){
        arrayList.clear()
        class deleteN : AsyncTask<Void, Void,Void>(){
            override fun doInBackground(vararg p0: Void?): Void? {
                Datbase.getDatabase(requireContext()).dao().deleteDemand(demandEntity)
                return null
            }

            override fun onPostExecute(result: Void?) {
                super.onPostExecute(result)
                Toast.makeText(requireContext(), "Demand Delete", Toast.LENGTH_SHORT).show()
                getUserTotalDemands()
            }
        }

        deleteN().execute()

    }

    fun updateDemand(demandEntity: DemandEntity
    ) {
        class Update:AsyncTask<Void,Void,Void>(){
            override fun doInBackground(vararg p0: Void?): Void? {
                Datbase.getDatabase(requireContext()).dao().updateDemand(demandEntity)
                return null
            }

            override fun onPostExecute(result: Void?) {
                super.onPostExecute(result)
                getUserTotalDemands()

            }
        }
        Update().execute()
    }



}