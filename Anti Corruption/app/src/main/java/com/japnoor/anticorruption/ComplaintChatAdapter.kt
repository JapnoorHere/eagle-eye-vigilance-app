package com.japnoor.anticorruption

import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

class ComplaintChatAdapter(var context : ComplaintChatActivity, var chatList : ArrayList<Chat>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val ITEM_RECIEVE=1
    val ITEM_SENT=2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(viewType==1){
        var view : View =LayoutInflater.from(context).inflate(R.layout.item_recieve_message,parent,false)
            return RecieveViewHolder(view)
        }
        else{
            var view : View =LayoutInflater.from(context).inflate(R.layout.item_send_message,parent,false)
            return SentViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
          if(holder.javaClass == SentViewHolder::class.java){
              var viewHolder=holder as SentViewHolder
              holder.sentMessage.text=decrypt(chatList[position].message)
              holder.sentDate.text=decrypt(chatList[position].msgdate)
          }
        else{
              var viewHolder=holder as RecieveViewHolder
              holder.recieveMessage.text=decrypt(chatList[position].message)
              holder.recieveDate.text=decrypt(chatList[position].msgdate)
          }
    }

    override fun getItemViewType(position: Int): Int {
        if(FirebaseAuth.getInstance().currentUser?.uid.equals(chatList[position].senderId)) {
            return ITEM_SENT
        }
        else{
            return ITEM_RECIEVE
        }

    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    class SentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
         var sentMessage= itemView.findViewById<TextView>(R.id.sentMessage)
         var sentDate= itemView.findViewById<TextView>(R.id.sentDate)
    }

    class RecieveViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var recieveMessage= itemView.findViewById<TextView>(R.id.recieveMessage)
        var recieveDate= itemView.findViewById<TextView>(R.id.recieveDate)
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