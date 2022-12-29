package com.japnoor.anticorruption.admin

import android.app.Dialog
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.japnoor.anticorruption.*
import com.japnoor.anticorruption.databinding.EditComplaintDialogBinding
import com.japnoor.anticorruption.databinding.FragmentAdminResolvedBinding
import java.io.File

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AdminResolvedFragment : Fragment(),ComplaintClickedInterface {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var binding : FragmentAdminResolvedBinding
    var arrayList: ArrayList<DisplayComplaintsEntity> = ArrayList()
    lateinit var adapter : AdminResolvedAdapter
    lateinit var signUpEntity: SignUpEntity
    private var mediaPlayer =  MediaPlayer()
    lateinit var adminHomeScreen: AdminHomeScreen



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adminHomeScreen = activity as AdminHomeScreen

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        signUpEntity= SignUpEntity()
        binding=FragmentAdminResolvedBinding.inflate(layoutInflater,container,false)
        adapter= AdminResolvedAdapter(arrayList,this)
        binding.recyclerView.layoutManager= GridLayoutManager(requireContext(),2)
        binding.recyclerView.adapter=adapter

        getResComplaints()
        return binding.root
    }


    fun getResComplaints() {
        arrayList.clear()
        class Get : AsyncTask<Void, Void, Void>() {
            override fun doInBackground(vararg p0: Void?): Void? {
                arrayList.addAll(Datbase.getDatabase(requireContext()).dao().getResDisplayComplaints())
                return null
            }

            override fun onPostExecute(result: Void?) {
                super.onPostExecute(result)
                adapter.notifyDataSetChanged()
            }

        }
        Get().execute()
    }
    fun changeStatus(complaintsEntity: DisplayComplaintsEntity, dialog: Dialog){
        class Update : AsyncTask<Void,Void,Void>(){
            override fun doInBackground(vararg p0: Void?): Void? {
                Datbase.getDatabase(requireContext()).dao().changeStatus(complaintsEntity.complaintsEntity?:ComplaintsEntity())
                return null
            }

            override fun onPostExecute(result: Void?) {
                super.onPostExecute(result)
                dialog.dismiss()
                getResComplaints()
            }
        }
        Update().execute()
    }
    override fun onComplaintsClicked(complaintsEntity: DisplayComplaintsEntity) {
        var dialog= Dialog(requireContext())
        var dialogBind= EditComplaintDialogBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBind.root)
        dialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        if(complaintsEntity.complaintsEntity?.audio.isNullOrEmpty())
            dialogBind.audio.visibility  = View.GONE
        if(complaintsEntity.complaintsEntity?.video.isNullOrEmpty())
            dialogBind.video.visibility  = View.GONE
        dialogBind.stamp.visibility=View.VISIBLE
        dialogBind.stamp.setImageResource(R.drawable.resolved_stamp)
        dialogBind.name.setText(complaintsEntity.userEntity?.firstName)
        dialogBind.date.setText(complaintsEntity.complaintsEntity?.date)
        dialogBind.phoneno.setText(complaintsEntity.userEntity?.phoneN)
        dialogBind.email.setText(complaintsEntity.userEntity?.email)
        dialogBind.tvSummary.setText(complaintsEntity.complaintsEntity?.complaintSummary)
        dialogBind.tvAgainst.setText(complaintsEntity.complaintsEntity?.complaintAgainst)
        dialogBind.tvDetails.setText(complaintsEntity.complaintsEntity?.complaintDetails)
        dialogBind.tvDistrict.setText(complaintsEntity.complaintsEntity?.selectDistrict)
        dialogBind.fabAccepted.visibility=View.INVISIBLE
        dialogBind.fabRejected.visibility=View.INVISIBLE
        dialogBind.fabResolved.visibility=View.INVISIBLE
        dialogBind.audio.setOnClickListener {
            dialogBind.audio.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_baseline_pause_circle_24,0,0,0)
            var audiofile=File(complaintsEntity.complaintsEntity?.audio)
            val fileUri: Uri =
                FileProvider.getUriForFile(
                    adminHomeScreen,
                    BuildConfig.APPLICATION_ID + ".provider",
                    audiofile
                )
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(fileUri, "audio/*")
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) //DO NOT FORGET THIS EVER

            startActivity(intent)
        }
        dialogBind.video.setOnClickListener {
            val videoFile = File(complaintsEntity.complaintsEntity?.video)
            val fileUri: Uri =
                FileProvider.getUriForFile(adminHomeScreen, BuildConfig.APPLICATION_ID +".provider", videoFile)
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(fileUri, "video/*")
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) //DO NOT FORGET THIS EVER

            startActivity(intent)
        }
        dialog.show()
        }

}