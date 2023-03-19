package com.japnoor.anticorruption

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.view.View
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

class ChatActivity : AppCompatActivity() {
    lateinit var binding: ActivityChatBinding
    lateinit var chatAdapter: ChatAdapter
    lateinit var chatList: ArrayList<Chat>
    var senderUid: String = ""
    var recieverUid: String = ""

    var recieverRoom : String = ""
    var senderRoom : String = ""
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
        var forgot = ForogotPasscode()
        encryptionKey=forgot.key()
        secretKeySpec = SecretKeySpec(encryptionKey!!.toByteArray(), "AES")
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        senderUid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        recieverUid = "gZ5CEuC5pRTJhzAfQNtdRYv1TiJ3"

        chatList= ArrayList()
        chatAdapter= ChatAdapter(this@ChatActivity,chatList)

        binding.horizScroll.visibility= View.GONE

        senderRoom = recieverUid + senderUid
        recieverRoom = senderUid + recieverUid


        FirebaseDatabase.getInstance().reference.child("Chats").child(senderRoom).child("messages")
            .addValueEventListener(object  : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    chatList.clear()
                    for(each in snapshot.children){
                        var msg=each.getValue(Chat::class.java)
                        if(msg!=null)
                        chatList.add(msg)
                    }
                    chatAdapter.notifyDataSetChanged()
                    chatAdapter= ChatAdapter(this@ChatActivity,chatList)
                    binding.recyclerView.layoutManager=LinearLayoutManager(this@ChatActivity)
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
           var msg =binding.etMessage.text.toString().trim()
              var chats=Chat(encrypt(binding.etMessage.text.toString().trim()),encrypt(time.toString()),FirebaseAuth.getInstance().currentUser?.uid.toString(),"")
            FirebaseDatabase.getInstance().reference.child("Chats").child(senderRoom).child("messages")
                .push().setValue(chats).addOnCompleteListener {
                    FirebaseDatabase.getInstance().reference.child("Chats").child(recieverRoom).child("messages")
                        .push().setValue(chats).addOnCompleteListener {
                            FirebaseDatabase.getInstance().reference.child("Users").child(FirebaseAuth.getInstance().currentUser?.uid.toString()).child("name").get().addOnCompleteListener {
                                var notificationID =
                                    FirebaseDatabase.getInstance().reference.child("NotificationChat")
                                        .push().key.toString()
                                var notification = NotificationChat(
                                    notificationID,
                                    "",
                                    encrypt(time),
                                    FirebaseAuth.getInstance().currentUser?.uid.toString(),
                                    "",
                                    it.result.value.toString(),
                                    "",
                                    "",
                                    "b",
                                    encrypt(msg)
                                )
                                FirebaseDatabase.getInstance().reference.child("NotificationChat")
                                    .child(notificationID).setValue(notification)
                            }
                        }
                }
            binding.etMessage.text.clear()
        }

    }
            }


