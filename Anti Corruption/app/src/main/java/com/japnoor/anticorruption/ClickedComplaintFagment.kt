package com.japnoor.anticorruption

import android.app.Dialog
import android.content.Intent
import android.database.Cursor
import android.media.MediaPlayer
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
import com.japnoor.anticorruption.databinding.EditUserComplaintDialogBinding
import com.japnoor.anticorruption.databinding.ShowUserComplaintsDialogBinding
import com.japnoor.anticorruption.databinding.TotalComplaintsDialogBinding
import com.japnoor.anticorruption.utils.UriPathHelper
import java.io.File

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ClickedComplaintFagment : Fragment(), UserComplaintClick {
    private var param1: String? = null
    private var param2: String? = null
    lateinit var arrayAdapter: ArrayAdapter<String>
    var arrayList: ArrayList<ComplaintsEntity> = ArrayList()
    lateinit var adapter: ComplaintsAdapter
    lateinit var homeScreen: HomeScreen
    lateinit var binding: TotalComplaintsDialogBinding
    private var mediaPlayer = MediaPlayer()
    var audioo:String?=null
    var videoPath:String?=null

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
        binding = TotalComplaintsDialogBinding.inflate(layoutInflater, container, false)
        adapter = ComplaintsAdapter(arrayList, this)
        // Inflate the layout for this fragment
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
        getUserComplaints()
        return binding.root
    }

    fun updateComplaint(
        complaintsEntity: ComplaintsEntity
    ) {
        class Update : AsyncTask<Void, Void, Void>() {
            override fun doInBackground(vararg p0: Void?): Void? {
                Datbase.getDatabase(requireContext()).dao().updateComplaint(complaintsEntity)
                return null
            }

            override fun onPostExecute(result: Void?) {
                super.onPostExecute(result)
                getUserComplaints()

            }
        }
        Update().execute()
    }


    fun getUserComplaints() {
        arrayList.clear()
        System.out.println("homeScreen.signUpEntity?.id ${homeScreen.signUpEntity?.id}")
        class Get : AsyncTask<Void, Void, Void>() {
            override fun doInBackground(vararg p0: Void?): Void? {
                arrayList.addAll(
                    Datbase.getDatabase(requireContext()).dao()
                        .getUserComplaints(homeScreen.signUpEntity?.id ?: -1)
                )
                return null
            }

            override fun onPostExecute(result: Void?) {
                super.onPostExecute(result)
                adapter.notifyDataSetChanged()
            }
        }
        Get().execute()
    }

    override fun onClick(complaintsEntity: ComplaintsEntity) {
        var dialog = Dialog(requireContext())
        if (complaintsEntity.status.equals("acc")) {
            var dialogBind = ShowUserComplaintsDialogBinding.inflate(layoutInflater)
            dialog.setContentView(dialogBind.root)
            dialog.window?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            if (complaintsEntity.audio.isNullOrEmpty())
                dialogBind.audio.visibility = View.GONE
            if (complaintsEntity.video.isNullOrEmpty())
                dialogBind.video.visibility = View.GONE
            dialogBind.stamp.visibility = View.VISIBLE
            dialogBind.stamp.setImageResource(R.drawable.accpeted_stamp)
            dialogBind.tvSummary.setText(complaintsEntity.complaintSummary)
            dialogBind.tvDetails.setText(complaintsEntity.complaintDetails)
            dialogBind.tvAgainst.setText(complaintsEntity.complaintAgainst)
            dialogBind.comDate.setText(complaintsEntity.date)
            dialogBind.tvDistrict.setText(complaintsEntity.selectDistrict)
            dialogBind.Framelayout.setBackgroundResource(R.color.accepted1)
            dialogBind.audio.setOnClickListener {
                var audiofile=File(complaintsEntity.audio)
                val fileUri: Uri =
                    FileProvider.getUriForFile(
                        homeScreen,
                        BuildConfig.APPLICATION_ID + ".provider",
                        audiofile
                    )
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(fileUri, "audio/*")
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) //DO NOT FORGET THIS EVER

                startActivity(intent)
            }
            dialogBind.video.setOnClickListener {
                val videoFile = File(complaintsEntity.video)
                val fileUri: Uri =
                    FileProvider.getUriForFile(
                        homeScreen,
                        BuildConfig.APPLICATION_ID + ".provider",
                        videoFile
                    )
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(fileUri, "video/*")
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) //DO NOT FORGET THIS EVER

                startActivity(intent)
            }
            dialogBind.fabAdd1.setOnClickListener {
                dialog.dismiss()
            }
        } else if (complaintsEntity.status.equals("res")) {
            var dialogBind = ShowUserComplaintsDialogBinding.inflate(layoutInflater)
            dialog.setContentView(dialogBind.root)
            dialog.window?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            if (complaintsEntity.audio.isNullOrEmpty())
                dialogBind.audio.visibility = View.GONE
            if (complaintsEntity.video.isNullOrEmpty())
                dialogBind.video.visibility = View.GONE
            dialogBind.stamp.visibility = View.VISIBLE
            dialogBind.stamp.setImageResource(R.drawable.resolved_stamp)
            dialogBind.tvSummary.setText(complaintsEntity.complaintSummary)
            dialogBind.tvDetails.setText(complaintsEntity.complaintDetails)
            dialogBind.tvAgainst.setText(complaintsEntity.complaintAgainst)
            dialogBind.comDate.setText(complaintsEntity.date)
            dialogBind.tvDistrict.setText(complaintsEntity.selectDistrict)
            dialogBind.fabAdd1.setOnClickListener {
                dialog.dismiss()
            }
            dialogBind.audio.setOnClickListener {
                var audiofile=File(complaintsEntity.audio)
                val fileUri: Uri =
                    FileProvider.getUriForFile(
                        homeScreen,
                        BuildConfig.APPLICATION_ID + ".provider",
                        audiofile
                    )
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(fileUri, "audio/*")
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) //DO NOT FORGET THIS EVER

                startActivity(intent)
            }
            dialogBind.video.setOnClickListener {
                val videoFile = File(complaintsEntity.video)
                val fileUri: Uri =
                    FileProvider.getUriForFile(
                        homeScreen,
                        BuildConfig.APPLICATION_ID + ".provider",
                        videoFile
                    )
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(fileUri, "video/*")
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) //DO NOT FORGET THIS EVER
                startActivity(intent)
            }
        } else if (complaintsEntity.status.equals("rej")) {
            var dialogBind = ShowUserComplaintsDialogBinding.inflate(layoutInflater)
            dialog.setContentView(dialogBind.root)
            dialog.window?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            if (complaintsEntity.audio.isNullOrEmpty())
                dialogBind.audio.visibility = View.GONE
            if (complaintsEntity.video.isNullOrEmpty())
                dialogBind.video.visibility = View.GONE
            dialogBind.stamp.visibility = View.VISIBLE
            dialogBind.stamp.setImageResource(R.drawable.rejected_stamp)
            dialogBind.tvSummary.setText(complaintsEntity.complaintSummary)
            dialogBind.tvDetails.setText(complaintsEntity.complaintDetails)
            dialogBind.tvAgainst.setText(complaintsEntity.complaintAgainst)
            dialogBind.comDate.setText(complaintsEntity.date)
            dialogBind.tvDistrict.setText(complaintsEntity.selectDistrict)
            dialogBind.Framelayout.setBackgroundResource(R.color.rejected1)

            dialogBind.fabAdd1.setOnClickListener {
                dialog.dismiss()
            }
            dialogBind.audio.setOnClickListener {
                var audiofile=File(complaintsEntity.audio)
                val fileUri: Uri =
                    FileProvider.getUriForFile(
                        homeScreen,
                        BuildConfig.APPLICATION_ID + ".provider",
                        audiofile
                    )
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(fileUri, "audio/*")
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) //DO NOT FORGET THIS EVER

                startActivity(intent)
            }
            dialogBind.video.setOnClickListener {
                val videoFile = File(complaintsEntity.video)
                val fileUri: Uri =
                    FileProvider.getUriForFile(
                        homeScreen,
                        BuildConfig.APPLICATION_ID + ".provider",
                        videoFile
                    )
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(fileUri, "video/*")
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) //DO NOT FORGET THIS EVER

                startActivity(intent)
            }
        } else {
            var dialogBind = EditUserComplaintDialogBinding.inflate(layoutInflater)
            dialog.setContentView(dialogBind.root)
            dialog.window?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )


            dialogBind.audioUpload.setOnClickListener{
              getAudio.launch("audio/*")
            }
            dialogBind.videoUpload.setOnClickListener{
                getVideo.launch("video/*")
            }


            dialogBind.compSumm.setText(complaintsEntity.complaintSummary)
            dialogBind.comDetails.setText(complaintsEntity.complaintDetails)
            dialogBind.compAgainst.setText(complaintsEntity.complaintAgainst)
            dialogBind.comDate.setText(complaintsEntity.date)
            dialogBind.District.setText(complaintsEntity.selectDistrict)
            var districts = resources.getStringArray(R.array.District)
            arrayAdapter = ArrayAdapter(requireContext(), R.layout.drop_down_item, districts)
            dialogBind.District.setAdapter(arrayAdapter)
            dialogBind.fabAdd1.setOnClickListener {
                complaintsEntity.complaintSummary = dialogBind.compSumm.text.toString()
                complaintsEntity.complaintAgainst = dialogBind.compAgainst.text.toString()
                complaintsEntity.complaintDetails = dialogBind.comDetails.text.toString()
                complaintsEntity.selectDistrict = dialogBind.District.text.toString()
                complaintsEntity.audio=audioo
                complaintsEntity.video=videoPath
                updateComplaint(complaintsEntity)
                dialog.dismiss()
            }
            dialogBind.audio.setOnClickListener {
                if (complaintsEntity.audio.isNullOrEmpty())

                    Toast.makeText(requireContext(), "Audio is not attached", Toast.LENGTH_LONG)
                        .show()
                    else{
                        var audiofile=File(complaintsEntity.audio)
                    val fileUri: Uri =
                        FileProvider.getUriForFile(
                            homeScreen,
                            BuildConfig.APPLICATION_ID + ".provider",
                            audiofile
                        )
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.setDataAndType(fileUri, "audio/*")
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) //DO NOT FORGET THIS EVER

                    startActivity(intent)
                    }
//                else if (!mediaPlayer.isPlaying) {
//                    dialogBind.audio.setCompoundDrawablesRelativeWithIntrinsicBounds(
//                        R.drawable.ic_baseline_pause_circle_24,
//                        0,
//                        0,
//                        0
//                    )
//                    mediaPlayer.reset()
//                    mediaPlayer.apply {
//                        setDataSource(complaintsEntity.audio)
//                        prepare()
//                        start()
//                    }
//                } else {
//                    dialogBind.audio.setCompoundDrawablesRelativeWithIntrinsicBounds(
//                        R.drawable.ic_baseline_play_circle_24,
//                        0,
//                        0,
//                        0
//                    )
//                    mediaPlayer.stop()
//                }
            }
            dialogBind.video.setOnClickListener {
                if (complaintsEntity.video.isNullOrEmpty())
                    Toast.makeText(requireContext(), "Video is not attached", Toast.LENGTH_LONG)
                        .show()
                else {
                    val videoFile = File(complaintsEntity.video)
                    val fileUri: Uri =
                        FileProvider.getUriForFile(
                            homeScreen,
                            BuildConfig.APPLICATION_ID + ".provider",
                            videoFile
                        )
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.setDataAndType(fileUri, "video/*")
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
                text?.setText("Are you sure you want \n to delete this Complaint ?")
                tvNo?.setOnClickListener {
                    bottomSheet.dismiss()
                }
                tvYes?.setOnClickListener {
                    deleteComplaint(complaintsEntity)
                    bottomSheet.dismiss()
                    dialog.dismiss()
                }
            }
        }
        dialog.show()
    }

    fun deleteComplaint(complaintEntity: ComplaintsEntity) {
        class deleteN : AsyncTask<Void, Void, Void>() {
            override fun doInBackground(vararg p0: Void?): Void? {
                Datbase.getDatabase(requireContext()).dao().deleteComplaint(complaintEntity)
                return null
            }

            override fun onPostExecute(result: Void?) {
                super.onPostExecute(result)
                Toast.makeText(requireContext(), "Complaint Delete", Toast.LENGTH_SHORT).show()
                getUserComplaints()
            }
        }

        deleteN().execute()

    }

}