package com.japnoor.anticorruption

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Base64
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
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
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
        super.onCreate(savedInstanceState)
        var binding = FragmentNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var forgot = ForogotPasscode()
        encryptionKey=forgot.key()
        secretKeySpec = SecretKeySpec(encryptionKey!!.toByteArray(), "AES")
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

            override fun onClick(notifications: Notification, type: String, status: String) {

            }


        })

    }


    override fun onClick(notification: Notification, type: String,status : String) {
        TODO("Not yet implemented")
    }

}