package com.japnoor.anticorruption

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
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
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

class UserDemandAdapter(var context: HomeScreen, var demandlist: ArrayList<DemandLetter>,var userDemandClick: UserDemandClick)  : RecyclerView.Adapter<UserDemandAdapter.ViewHolder>() {


    class ViewHolder(var binding: ItemDemandtBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemDemandtBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.demSubject.setText(decrypt(demandlist[position].demandSubject))
        holder.binding.demandDetails.setText(decrypt(demandlist[position].demandDetails))
        holder.binding.date.setText(decrypt(demandlist[position].demandDate))
        holder.binding.time.setText(decrypt(demandlist[position].demandTime))
        holder.binding.demNumber.setText(decrypt(demandlist[position].demandNumber))

        holder.itemView.setOnClickListener{
            userDemandClick.onClick(demandlist[position])
        }

        holder.binding.more.setOnClickListener {
            var bottomSheet = BottomSheetDialog(context)
            bottomSheet.setContentView(R.layout.complaint_menu_hold)
            var chat=bottomSheet.findViewById<CardView>(R.id.chat)
            var delete=bottomSheet.findViewById<CardView>(R.id.delete)
            when (demandlist[position].status) {
                "1" -> {
                    delete?.visibility=View.GONE
                }
                "2" -> {
                    delete?.visibility=View.GONE
                }
                "3" -> {
                    delete?.visibility=View.GONE
                }
                else->{
                    delete?.visibility=View.VISIBLE
                    delete?.setOnClickListener {
                        bottomSheet.dismiss()
                        val connectivityManager =
                            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
                        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
                        if (isConnected) {
                            FirebaseDatabase.getInstance().reference.child("Demand Letter")
                                .child(demandlist[position].demandId).removeValue()
                            FirebaseStorage.getInstance().reference.child("images")
                                .child(demandlist[position].imageName).delete()
                        }
                        else{
                            Toast.makeText(context, "Check you internet connection please", Toast.LENGTH_LONG)
                                .show()
                        }

                    }
                }

            }


            chat?.setOnClickListener {
                val connectivityManager =
                    context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
                val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
                if (isConnected) {
                    var intent = Intent(context, ComplaintChatActivity::class.java)
                    intent.putExtra("uid", demandlist[position].userId)
                    intent.putExtra("cid", demandlist[position].demandId)
                    intent.putExtra("cnumber", decrypt(demandlist[position].demandNumber))
                    intent.putExtra("type", "d")
                    intent.putExtra("status", demandlist[position].status)
                    intent.putExtra("against", decrypt(demandlist[position].demandSubject))
                    intent.putExtra("name", decrypt(demandlist[position].userName))
                    context.startActivity(intent)
                }
                else{
                    Toast.makeText(context, "Check you internet connection please", Toast.LENGTH_LONG)
                        .show()
                }
            }


            bottomSheet.show()
            true
        }



        when (demandlist[position].status) {
            "1" -> {
                holder.binding.rightline.setBackgroundResource(R.drawable.acceptedback)
                holder.binding.leftline.setBackgroundResource(R.drawable.acceptedback)
            //                holder.binding.inpriority.setImageResource(R.drawable.ic_baseline_file_download_done_24)
            }
            "2" -> {
                holder.binding.rightline.setBackgroundResource(R.drawable.resolvedback)
                holder.binding.leftline.setBackgroundResource(R.drawable.resolvedback)
//                holder.binding.inpriority.setImageResource(R.drawable.ic_baseline_cancel_24)
            }
            "3" -> {
                holder.binding.rightline.setBackgroundResource(R.drawable.rejectedback)
                holder.binding.leftline.setBackgroundResource(R.drawable.rejectedback)
            //                holder.binding.inpriority.setImageResource(R.drawable.ic_baseline_done_all_24)
            }
            else->{
//                holder.binding.inpriority.setImageResource(0)
            }
        }
//        holder.binding.icon.setImageResource(R.drawable.imageitem)
    }

    override fun getItemCount(): Int {
        return demandlist.size
    }

    fun FilteredList(filteredList: ArrayList<DemandLetter>) {
             demandlist=filteredList
             notifyDataSetChanged()
    }

    private fun decrypt(input: String): String {
        var forgot = ForogotPasscode()
        var encryptionKey=forgot.key()
        var secretKeySpec = SecretKeySpec(encryptionKey.toByteArray(), "AES")
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec)
        val decryptedBytes = cipher.doFinal(android.util.Base64.decode(input, android.util.Base64.DEFAULT))
        return String(decryptedBytes, Charsets.UTF_8)
    }
}
