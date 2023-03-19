package com.japnoor.anticorruption

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.japnoor.anticorruption.databinding.ActivityChatBinding
import java.text.SimpleDateFormat
import java.util.Date
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

class ComplaintChatActivity : AppCompatActivity() {
    lateinit var binding: ActivityChatBinding
    lateinit var chatAdapter: ComplaintChatAdapter
    lateinit var chatList: ArrayList<Chat>
    var senderUid: String = ""
    var recieverUid: String = ""
    var compId: String = ""
    var recieverRoom : String = ""
    var senderRoom : String = ""
    var cnumber : String? = null
    var type : String? = null
    var status : String = ""
    var against : String = ""
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

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        compId = ""
        cnumber= ""
        type= ""
        status=""
        against=""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var forgot = ForogotPasscode()
        encryptionKey=forgot.key()
        secretKeySpec = SecretKeySpec(encryptionKey!!.toByteArray(), "AES")
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        senderUid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        recieverUid = "gZ5CEuC5pRTJhzAfQNtdRYv1TiJ3"
        compId = intent.getStringExtra("cid").toString()
        cnumber= intent.getStringExtra("cnumber").toString()
        type= intent.getStringExtra("type").toString()
        status=intent.getStringExtra("status").toString()
        against=intent.getStringExtra("against").toString()


        if(type.equals("c")){
            binding.complaintNumber.setText(cnumber)
            binding.comNo.setText("Complaint Number : ")
            binding.against.setText(against)
            binding.aga.setText("Complaint Against : ")
        }
         if(type.equals("d")){
            binding.against.setText(against)
            binding.sta.setText("Demand Status : ")
            binding.aga.setText("Demand Subject : ")
            binding.complaintNumber.setText(cnumber)
            binding.comNo.setText("Demand Number : ")
        }

        when(status){
            "1"->{binding.status.setText("Accepted")}
            "2"->{binding.status.setText("Resolved")}
            "3"->{binding.status.setText("Rejected")}
            else->{
                binding.status.setText("No actions taken")
            }
        }


        chatList= ArrayList()
        chatAdapter= ComplaintChatAdapter(this@ComplaintChatActivity,chatList)


        senderRoom = recieverUid + senderUid + compId
        recieverRoom = senderUid + recieverUid + compId


        FirebaseDatabase.getInstance().reference.child("ComplaintChats").child(senderRoom).child("messages")
            .addValueEventListener(object  : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    chatList.clear()
                    for(each in snapshot.children){
                        var msg=each.getValue(Chat::class.java)
                        if(msg!=null)
                        chatList.add(msg)
                    }
                    chatAdapter.notifyDataSetChanged()
                    chatAdapter= ComplaintChatAdapter(this@ComplaintChatActivity,chatList)
                    binding.recyclerView.layoutManager=LinearLayoutManager(this@ComplaintChatActivity)
                    binding.recyclerView.adapter=chatAdapter
                    binding.recyclerView.scrollToPosition(chatList.size - 1)
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })


        binding.sendButton.setOnClickListener{
            val currentTimeMillis = System.currentTimeMillis()
            val formatter = SimpleDateFormat("dd/MM/yyyy-HH:mm")
            val dateTime = Date(currentTimeMillis)
            val time = formatter.format(dateTime)
              var msg=binding.etMessage.text.toString().trim()
              var chats=Chat(encrypt(binding.etMessage.text.toString().trim()),encrypt(time.toString()),FirebaseAuth.getInstance().currentUser?.uid.toString(),"")
            FirebaseDatabase.getInstance().reference.child("ComplaintChats").child(senderRoom).child("messages")
                .push().setValue(chats).addOnCompleteListener {
                    FirebaseDatabase.getInstance().reference.child("ComplaintChats").child(recieverRoom).child("messages")
                        .push().setValue(chats).addOnCompleteListener {
                            FirebaseDatabase.getInstance().reference.child("Users").child(FirebaseAuth.getInstance().currentUser?.uid.toString()).child("name").get().addOnCompleteListener {
                                if (type.equals("c")) {
                                    var notificationID =
                                        FirebaseDatabase.getInstance().reference.child("NotificationChat")
                                            .push().key.toString()
                                    var notification = NotificationChat(
                                        notificationID,
                                        encrypt(against),
                                        encrypt(time),
                                        FirebaseAuth.getInstance().currentUser?.uid.toString(),
                                        compId,
                                        it.result.value.toString(),
                                        encrypt(status),
                                        encrypt(cnumber.toString()),
                                        "c",
                                        encrypt(msg)
                                    )
                                    FirebaseDatabase.getInstance().reference.child("NotificationChat")
                                        .child(notificationID).setValue(notification)
                                } else if (type.equals("d")) {
                                    var notificationID =
                                        FirebaseDatabase.getInstance().reference.child("NotificationChat")
                                            .push().key.toString()
                                    var notification = NotificationChat(
                                        notificationID,
                                        encrypt(against),
                                        encrypt(time),
                                        FirebaseAuth.getInstance().currentUser?.uid.toString(),
                                        compId,
                                        it.result.value.toString(),
                                        encrypt(status),
                                        encrypt(cnumber.toString()),
                                        "d",
                                        encrypt(msg)
                                    )
                                    FirebaseDatabase.getInstance().reference.child("NotificationChat")
                                        .child(notificationID).setValue(notification)
                                }
                            }
                        }
            }
            binding.etMessage.text.clear()

        }


    }
}