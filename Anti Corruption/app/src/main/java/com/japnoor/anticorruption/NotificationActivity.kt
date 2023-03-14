package com.japnoor.anticorruption

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.japnoor.anticorruption.databinding.FragmentNotificationBinding
import com.japnoor.anticorruption.databinding.ShowUserComplaintsDialogBinding
import com.japnoor.anticorruption.databinding.ShowUserDemandDialogBinding
import kotlin.collections.ArrayList

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class NotificationActivity : AppCompatActivity() , NotificationClick{
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var id : String=""
    lateinit var notificationList: ArrayList<Notification>
    lateinit var notificationAdapter: NotificationAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var binding = FragmentNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        notificationList= ArrayList()
        notificationAdapter=NotificationAdapter(this@NotificationActivity,notificationList,this)

        id=intent.getStringExtra("id").toString()

        FirebaseDatabase.getInstance().reference.child("Notifications").addValueEventListener(object : ValueEventListener,
            NotificationClick {
            override fun onDataChange(snapshot: DataSnapshot) {
                notificationList.clear()
                 for(each in snapshot.children){
                     var notifi=each.getValue(Notification::class.java)
                     if(notifi != null && notifi.userId.equals(id))
                         notificationList.add(notifi)
                 }
                notificationList.reverse()
                notificationAdapter=NotificationAdapter(this@NotificationActivity,notificationList,this)
                binding.recyclerView.layoutManager=LinearLayoutManager(this@NotificationActivity)
                binding.recyclerView.adapter=notificationAdapter
            }

            override fun onCancelled(error: DatabaseError) {

            }

            override fun onClick(notifications: Notification, type: String,status :String) {
//                when(type) {
//                    "c"->{
//                        FirebaseDatabase.getInstance().reference.child("Complaints").addValueEventListener(object  : ValueEventListener{
//                            override fun onDataChange(snapshot: DataSnapshot) {
//                                for (each in snapshot.children) {
//                                    var complaints = each.getValue(Complaints::class.java)
//                                    if (complaints != null && complaints.complaintId.equals(
//                                            notifications.complaintId
//                                        )
//                                    ) {
//                                        if (status.equals("1")) {
//                                            var dialog=Dialog(this@NotificationActivity)
//
//                                            var dialogBind =
//                                                ShowUserComplaintsDialogBinding.inflate(
//                                                    layoutInflater
//                                                )
//                                            dialog.setContentView(dialogBind.root)
//                                            dialog.window?.setLayout(
//                                                WindowManager.LayoutParams.MATCH_PARENT,
//                                                WindowManager.LayoutParams.WRAP_CONTENT
//                                            )
//
//                                            dialogBind.Framelayout.setBackgroundResource(R.color.accepted1)
//                                            dialogBind.stamp.setImageResource(R.drawable.accpeted_stamp)
//                                            dialogBind.tvDept.setText(complaints.complaintDept)
//                                            dialogBind.tvLocation.setText(complaints.complaintLoc)
//                                            dialogBind.tvCategory.setText(complaints.complaintCategory)
//                                            dialogBind.tvDetails.setText(complaints.complaintDetails)
//                                            dialogBind.tvAgainst.setText(complaints.complaintAgainst)
//                                            dialogBind.comDate.setText(complaints.complaintDate)
//                                            dialogBind.tvDistrict.setText(complaints.complaintDistrict)
//
//                                            dialogBind.fabAdd1.setOnClickListener {
//                                                dialog.dismiss()
//                                            }
//                                            dialogBind.audio.setOnClickListener {
//                                                val fileUri: Uri = complaints.audioUrl.toUri()
//                                                var intent =
//                                                    Intent(this@NotificationActivity, AudioActivity::class.java)
//                                                intent.putExtra("audio", fileUri.toString())
//                                                startActivity(intent)
//
//                                            }
//                                            dialogBind.video.setOnClickListener {
//                                                val fileUri: Uri = complaints.videoUrl.toUri()
//
//                                                var intent =
//                                                    Intent(this@NotificationActivity, VideoActivity::class.java)
//                                                intent.putExtra("video", fileUri.toString())
//                                                startActivity(intent)
//                                            }
//
//
//                                            if (complaints.audioUrl.isNullOrEmpty()) {
//                                                dialogBind.audio.visibility = View.GONE
//                                                dialogBind.audio.visibility = View.GONE
//                                            }
//
//                                            if (complaints.videoUrl.isNullOrEmpty()) {
//                                                dialogBind.video.visibility = View.GONE
//                                                dialogBind.video.visibility = View.GONE
//                                            }
//
//
//                                            dialog.show()
//                                        } else if (status.equals("2")) {
//                                            var dialog=Dialog(this@NotificationActivity)
//
//                                            var dialogBind =
//                                                ShowUserComplaintsDialogBinding.inflate(
//                                                    layoutInflater
//                                                )
//                                            dialog.setContentView(dialogBind.root)
//                                            dialog.window?.setLayout(
//                                                WindowManager.LayoutParams.MATCH_PARENT,
//                                                WindowManager.LayoutParams.WRAP_CONTENT
//                                            )
//                                            dialogBind.stamp.setImageResource(R.drawable.resolved_stamp)
//                                            dialogBind.Framelayout.setBackgroundResource(R.color.resolved1)
//                                            dialogBind.tvDept.setText(complaints.complaintDept)
//                                            dialogBind.tvLocation.setText(complaints.complaintLoc)
//                                            dialogBind.tvCategory.setText(complaints.complaintCategory)
//                                            dialogBind.tvDetails.setText(complaints.complaintDetails)
//                                            dialogBind.tvAgainst.setText(complaints.complaintAgainst)
//                                            dialogBind.comDate.setText(complaints.complaintDate)
//                                            dialogBind.tvDistrict.setText(complaints.complaintDistrict)
//
//                                            dialogBind.fabAdd1.setOnClickListener {
//                                                dialog.dismiss()
//                                            }
//                                            dialogBind.audio.setOnClickListener {
//                                                val fileUri: Uri = complaints.audioUrl.toUri()
//                                                var intent =
//                                                    Intent(this@NotificationActivity, AudioActivity::class.java)
//                                                intent.putExtra("audio", fileUri.toString())
//                                                startActivity(intent)
//
//                                            }
//                                            dialogBind.video.setOnClickListener {
//                                                val fileUri: Uri = complaints.videoUrl.toUri()
//
//                                                var intent =
//                                                    Intent(this@NotificationActivity, VideoActivity::class.java)
//                                                intent.putExtra("video", fileUri.toString())
//                                                startActivity(intent)
//                                            }
//
//
//                                            if (complaints.audioUrl.isNullOrEmpty()) {
//                                                dialogBind.audio.visibility = View.GONE
//                                                dialogBind.audio.visibility = View.GONE
//                                            }
//
//                                            if (complaints.videoUrl.isNullOrEmpty()) {
//                                                dialogBind.video.visibility = View.GONE
//                                                dialogBind.video.visibility = View.GONE
//                                            }
//
//
//                                            dialog.show()
//                                        } else if (status.equals("3")) {
//                                            var dialog=Dialog(this@NotificationActivity)
//
//                                            var dialogBind =
//                                                ShowUserComplaintsDialogBinding.inflate(
//                                                    layoutInflater
//                                                )
//                                            dialog.setContentView(dialogBind.root)
//                                            dialog.window?.setLayout(
//                                                WindowManager.LayoutParams.MATCH_PARENT,
//                                                WindowManager.LayoutParams.WRAP_CONTENT
//                                            )
//                                            dialogBind.Framelayout.setBackgroundResource(R.color.rejected1)
//                                            dialogBind.stamp.setImageResource(R.drawable.rejected_stamp)
//                                            dialogBind.tvDept.setText(complaints.complaintDept)
//                                            dialogBind.tvLocation.setText(complaints.complaintLoc)
//                                            dialogBind.tvCategory.setText(complaints.complaintCategory)
//                                            dialogBind.tvDetails.setText(complaints.complaintDetails)
//                                            dialogBind.tvAgainst.setText(complaints.complaintAgainst)
//                                            dialogBind.comDate.setText(complaints.complaintDate)
//                                            dialogBind.tvDistrict.setText(complaints.complaintDistrict)
//
//                                            dialogBind.fabAdd1.setOnClickListener {
//                                                dialog.dismiss()
//                                            }
//                                            dialogBind.audio.setOnClickListener {
//                                                val fileUri: Uri = complaints.audioUrl.toUri()
//                                                var intent =
//                                                    Intent(this@NotificationActivity, AudioActivity::class.java)
//                                                intent.putExtra("audio", fileUri.toString())
//                                                startActivity(intent)
//
//                                            }
//
//                                            dialogBind.video.setOnClickListener {
//                                                val fileUri: Uri = complaints.videoUrl.toUri()
//                                                var intent =
//                                                    Intent(this@NotificationActivity, VideoActivity::class.java)
//                                                intent.putExtra("video", fileUri.toString())
//                                                startActivity(intent)
//                                            }
//
//
//                                            if (complaints.audioUrl.isNullOrEmpty()) {
//                                                dialogBind.audio.visibility = View.GONE
//                                                dialogBind.audio.visibility = View.GONE
//                                            }
//
//                                            if (complaints.videoUrl.isNullOrEmpty()) {
//                                                dialogBind.video.visibility = View.GONE
//                                                dialogBind.video.visibility = View.GONE
//                                            }
//                                            dialog.show()
//                                        }
//
//                                    }
//                                }
//
//                            }
//
//                            override fun onCancelled(error: DatabaseError) {
//                                TODO("Not yet implemented")
//                            }
//                        })
//                    }
//                    "d"->{
//                        FirebaseDatabase.getInstance().reference.child("Demand Letter").addValueEventListener(object  : ValueEventListener {
//                            override fun onDataChange(snapshot: DataSnapshot) {
//                                for (each in snapshot.children) {
//                                    var demandLetter = each.getValue(DemandLetter::class.java)
//                                    if (demandLetter != null && demandLetter.demandId.equals(notifications.complaintId)) {
//                                        if (status.equals("1")) {
//                                            var dialog = Dialog(this@NotificationActivity)
//                                            var dialogBind = ShowUserDemandDialogBinding.inflate(layoutInflater)
//                                            dialog.setContentView(dialogBind.root)
//                                            dialog.window?.setLayout(
//                                                WindowManager.LayoutParams.MATCH_PARENT,
//                                                WindowManager.LayoutParams.WRAP_CONTENT
//                                            )
//                                            dialogBind.CS.setText("Demand Summary : ")
//                                            dialogBind.CD.setText("Demand Details : ")
//                                            dialogBind.CA.visibility=View.GONE
//                                            dialogBind.stamp.visibility=View.VISIBLE
//                                            dialogBind.tvAgainst.visibility=View.GONE
//                                            dialogBind.stamp.setImageResource(R.drawable.accpeted_stamp)
//                                            dialogBind.Framelayout.setBackgroundResource(R.color.accepted1)
//                                            dialogBind.tvSummary.setText(demandLetter.demandSubject)
//                                            dialogBind.tvDetails.setText(demandLetter.demandDetails)
//                                            dialogBind.comDate.setText(demandLetter.demandDate)
//                                            dialogBind.tvDistrict.setText(demandLetter.demandDistrict)
//
//                                            dialogBind.image.visibility = View.VISIBLE
//                                            dialogBind.audio.visibility = View.GONE
//                                            dialogBind.video.visibility = View.GONE
//
//                                            dialogBind.image.setOnClickListener {
//                                                val fileUri: Uri = demandLetter.imageUrl.toUri()
//
//                                                val intent = Intent(Intent.ACTION_VIEW)
//                                                intent.setDataAndType(fileUri, "image/*")
//                                                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) //DO NOT FORGET THIS EVER
//                                                startActivity(intent)
//                                            }
//
//                                            dialogBind.fabAdd1.setOnClickListener {
//                                                dialog.dismiss()
//                                            }
//
//                                            dialog.show()
//                                        }
//                                        else if (status.equals("2")) {
//                                            var dialog = Dialog(this@NotificationActivity)
//                                            var dialogBind = ShowUserDemandDialogBinding.inflate(layoutInflater)
//                                            dialog.setContentView(dialogBind.root)
//                                            dialog.window?.setLayout(
//                                                WindowManager.LayoutParams.MATCH_PARENT,
//                                                WindowManager.LayoutParams.WRAP_CONTENT
//                                            )
//                                            dialogBind.CS.setText("Demand Summary : ")
//                                            dialogBind.CD.setText("Demand Details : ")
//                                            dialogBind.CA.visibility=View.GONE
//                                            dialogBind.stamp.visibility=View.VISIBLE
//                                            dialogBind.tvAgainst.visibility=View.GONE
//                                            dialogBind.stamp.setImageResource(R.drawable.resolved_stamp)
//                                            dialogBind.Framelayout.setBackgroundResource(R.color.resolved1)
//                                            dialogBind.tvSummary.setText(demandLetter.demandSubject)
//                                            dialogBind.tvDetails.setText(demandLetter.demandDetails)
//                                            dialogBind.comDate.setText(demandLetter.demandDate)
//                                            dialogBind.tvDistrict.setText(demandLetter.demandDistrict)
//
//                                            dialogBind.image.visibility = View.VISIBLE
//                                            dialogBind.audio.visibility = View.GONE
//                                            dialogBind.video.visibility = View.GONE
//
//                                            dialogBind.image.setOnClickListener {
//                                                val fileUri: Uri = demandLetter.imageUrl.toUri()
//
//                                                val intent = Intent(Intent.ACTION_VIEW)
//                                                intent.setDataAndType(fileUri, "image/*")
//                                                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) //DO NOT FORGET THIS EVER
//
//                                                startActivity(intent)
//                                            }
//
//                                            dialogBind.fabAdd1.setOnClickListener {
//                                                dialog.dismiss()
//                                            }
//
//                                            dialog.show()
//                                        }
//
//                                        else if (status.equals("3")) {
//                                            var dialog = Dialog(this@NotificationActivity)
//
//                                            var dialogBind = ShowUserDemandDialogBinding.inflate(layoutInflater)
//                                            dialog.setContentView(dialogBind.root)
//                                            dialog.window?.setLayout(
//                                                WindowManager.LayoutParams.MATCH_PARENT,
//                                                WindowManager.LayoutParams.WRAP_CONTENT
//                                            )
//                                            dialogBind.CS.setText("Demand Summary : ")
//                                            dialogBind.CD.setText("Demand Details : ")
//                                            dialogBind.CA.visibility=View.GONE
//                                            dialogBind.stamp.visibility=View.VISIBLE
//                                            dialogBind.tvAgainst.visibility=View.GONE
//                                            dialogBind.stamp.setImageResource(R.drawable.rejected_stamp)
//                                            dialogBind.Framelayout.setBackgroundResource(R.color.rejected1)
//                                            dialogBind.tvSummary.setText(demandLetter.demandSubject)
//                                            dialogBind.tvDetails.setText(demandLetter.demandDetails)
//                                            dialogBind.comDate.setText(demandLetter.demandDate)
//                                            dialogBind.tvDistrict.setText(demandLetter.demandDistrict)
//
//                                            dialogBind.image.visibility = View.VISIBLE
//                                            dialogBind.audio.visibility = View.GONE
//                                            dialogBind.video.visibility = View.GONE
//
//                                            dialogBind.image.setOnClickListener {
//                                                val fileUri: Uri = demandLetter.imageUrl.toUri()
//
//                                                val intent = Intent(Intent.ACTION_VIEW)
//                                                intent.setDataAndType(fileUri, "image/*")
//                                                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) //DO NOT FORGET THIS EVER
//
//                                                startActivity(intent)
//                                            }
//
//                                            dialogBind.fabAdd1.setOnClickListener {
//                                                dialog.dismiss()
//                                            }
//
//                                            dialog.show()
//                                        }
//                                    }
//                                }
//                            }
//
//                            override fun onCancelled(error: DatabaseError) {
//
//                            }
//                        })
//                    }
//                }
//
            }

        })

    }


    override fun onClick(notification: Notification, type: String,status : String) {
        TODO("Not yet implemented")
    }

}