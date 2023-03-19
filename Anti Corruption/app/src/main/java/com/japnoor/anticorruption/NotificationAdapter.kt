package com.japnoor.anticorruption

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.japnoor.anticorruption.databinding.EditUserComplaintDialogBinding
import com.japnoor.anticorruption.databinding.ItemComplaintBinding
import com.japnoor.anticorruption.databinding.ItemDemandtBinding
import com.japnoor.anticorruption.databinding.NotificationItemBinding
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

class NotificationAdapter(
    var context: NotificationActivity,
    var notificationList: ArrayList<Notification>,
    var notificationClick: NotificationClick
) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {


    class ViewHolder(var binding: NotificationItemBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            NotificationItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.itemView.setOnClickListener {
            when (notificationList[position].notificationType) {
                "c" -> {
                    when (notificationList[position].complaintStatus) {
                        "1" -> {
                            notificationClick.onClick(notificationList[position], "c", "1")

                        }
                        "2" -> {
                            notificationClick.onClick(notificationList[position], "c", "2")
                        }
                        "3" -> {
                            notificationClick.onClick(notificationList[position], "c", "3")
                        }
                    }
                }
                "d" -> {
                    when (notificationList[position].complaintStatus) {
                        "1" -> {
                            notificationClick.onClick(notificationList[position], "d", "1")
                        }
                        "2" -> {
                            notificationClick.onClick(notificationList[position], "d", "2")
                        }
                        "3" -> {
                            notificationClick.onClick(notificationList[position], "d", "3")
                        }
                    }
                }
            }
        }


        holder.itemView.setOnLongClickListener {
            when (notificationList[position].notificationType) {
                "c" -> {
                    var bottomSheet = BottomSheetDialog(context)
                    bottomSheet.setContentView(R.layout.complaint_menu_hold)
                    var chat = bottomSheet.findViewById<CardView>(R.id.chat)
                    var delete = bottomSheet.findViewById<CardView>(R.id.delete)
                    delete?.visibility = View.VISIBLE
                    delete?.setOnClickListener {
                        val connectivityManager =
                            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
                        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
                        if (isConnected) {
                            bottomSheet.dismiss()
                            FirebaseDatabase.getInstance().reference.child("Notifications")
                                .child(notificationList[position].notificationId).removeValue()
                        }
                        else{
                            Toast.makeText(context, "Check you internet connection please", Toast.LENGTH_LONG)
                                .show()
                        }
                    }


                    chat?.setOnClickListener {
                        val connectivityManager =
                            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
                        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
                        if (isConnected) {
                            FirebaseDatabase.getInstance().reference.child("Users")
                            var intent = Intent(context, ComplaintChatActivity::class.java)
                            intent.putExtra("uid", notificationList[position].userId)
                            intent.putExtra("profile", "")
                            intent.putExtra("cnumber", decrypt(notificationList[position].complaintNumber))
                            intent.putExtra("type", "c")
                            intent.putExtra("status", notificationList[position].complaintStatus)
                            intent.putExtra("against", decrypt(notificationList[position].complaintAgainst))
                            intent.putExtra("cid", notificationList[position].complaintId)
                            intent.putExtra("name", decrypt(notificationList[position].userName))
                            context.startActivity(intent)
                        }
                        else{
                            Toast.makeText(context, "Check you internet connection please", Toast.LENGTH_LONG)
                                .show()
                        }
                    }

                    bottomSheet.show()
                }
                "d" -> {
                    var bottomSheet = BottomSheetDialog(context)
                    bottomSheet.setContentView(R.layout.complaint_menu_hold)
                    var chat = bottomSheet.findViewById<CardView>(R.id.chat)
                    var delete = bottomSheet.findViewById<CardView>(R.id.delete)
                    delete?.visibility = View.VISIBLE
                    delete?.setOnClickListener {
                        val connectivityManager =
                            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
                        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
                        if (isConnected) {
                            bottomSheet.dismiss()
                            FirebaseDatabase.getInstance().reference.child("Notifications")
                                .child(notificationList[position].notificationId).removeValue()
                        }
                        else{
                            Toast.makeText(context, "Check you internet connection please", Toast.LENGTH_LONG)
                                .show()
                        }
                    }


                    chat?.setOnClickListener {
                        val connectivityManager =
                            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
                        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
                        if (isConnected) {
                            FirebaseDatabase.getInstance().reference.child("Users")
                            var intent = Intent(context, ComplaintChatActivity::class.java)
                            intent.putExtra("uid", notificationList[position].userId)
                            intent.putExtra("profile", "")
                            intent.putExtra("cnumber", decrypt(notificationList[position].complaintNumber))
                            intent.putExtra("type", "d")
                            intent.putExtra("status", notificationList[position].complaintStatus)
                            intent.putExtra("against", decrypt(notificationList[position].complaintAgainst))
                            intent.putExtra("cid", notificationList[position].complaintId)
                            intent.putExtra("name", decrypt(notificationList[position].userName))
                            context.startActivity(intent)
                        }
                        else{
                            Toast.makeText(context, "Check you internet connection please", Toast.LENGTH_LONG)
                                .show()
                        }
                    }

                    bottomSheet.show()
                }
            }
            true
        }

        when (notificationList[position].notificationType) {
            "c" -> {
                when (notificationList[position].complaintStatus) {
                    "1" -> {
                        holder.binding.compMessage.setText("Your Complaint has been accepted for further actions to be taken. ")
                        holder.binding.underLine.setBackgroundResource(R.color.accepted)
                    }
                    "2" -> {
                        holder.binding.underLine.setBackgroundResource(R.color.resolved)
                        holder.binding.compMessage.setText("Your Complaint has been resolved. Thank you for bringing our attention to it and helping us fight against it.")
                    }
                    "3" -> {
                        holder.binding.underLine.setBackgroundResource(R.color.rejected)
                        holder.binding.compMessage.setText("Your Complaint has been rejected due to insufficient evidence or other reasons. You may contact us.")
                    }
                }
                holder.binding.compnum.setText("Complaint Number : ")
                holder.binding.aga.setText("Against : ")
                holder.binding.time.setText(decrypt(notificationList[position].notificationTime))
                holder.binding.complaintNumber.setText(decrypt(notificationList[position].complaintNumber))
                holder.binding.compAgainst.setText(decrypt(notificationList[position].complaintAgainst))
            }
            "d" -> {
                when (notificationList[position].complaintStatus) {
                    "1" -> {
                        holder.binding.underLine.setBackgroundResource(R.color.accepted)
                        holder.binding.compMessage.setText("Your Demand Letter has been accepted for further actions to be taken. ")
                    }
                    "2" -> {
                        holder.binding.underLine.setBackgroundResource(R.color.resolved)
                        holder.binding.compMessage.setText("Your Demand Letter has been resolved. Thank you for bringing our attention to it .")
                    }
                    "3" -> {
                        holder.binding.underLine.setBackgroundResource(R.color.rejected)
                        holder.binding.compMessage.setText("Your Demand Letter has been rejected due to invalid points. You may contact us for any issue.")
                    }
                }
                holder.binding.compnum.setText("Demand Number : ")
                holder.binding.aga.setText("Subject : ")
                holder.binding.time.setText(decrypt(notificationList[position].notificationTime))
                holder.binding.complaintNumber.setText(decrypt(notificationList[position].complaintNumber))
                holder.binding.compAgainst.setText(decrypt(notificationList[position].complaintAgainst))
            }
        }
    }

    override fun getItemCount(): Int {
        return notificationList.size
    }
    private fun decrypt(input: String): String {
        var forgot = ForogotPasscode()
        var encryptionKey=forgot.key()
        var secretKeySpec = SecretKeySpec(encryptionKey!!.toByteArray(), "AES")

        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec)
        val decryptedBytes = cipher.doFinal(Base64.decode(input, Base64.DEFAULT))
        return String(decryptedBytes, Charsets.UTF_8)
    }
    private fun encrypt(input: String): String {
        var forgot = ForogotPasscode()
        var encryptionKey=forgot.key()
        var secretKeySpec = SecretKeySpec(encryptionKey!!.toByteArray(), "AES")
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec)
        val encryptedBytes = cipher.doFinal(input.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
    }

}
