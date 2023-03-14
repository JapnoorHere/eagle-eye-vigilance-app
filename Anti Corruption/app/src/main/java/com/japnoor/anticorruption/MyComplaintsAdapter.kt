package com.japnoor.anticorruption

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.renderscript.Sampler.Value
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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.japnoor.anticorruption.databinding.EditUserComplaintDialogBinding
import com.japnoor.anticorruption.databinding.ItemComplaintBinding

class MyComplaintsAdapter(var context: HomeScreen, var complaintsList: ArrayList<Complaints>,var userComplaintClick: UserComplaintClick)  : RecyclerView.Adapter<MyComplaintsAdapter.ViewHolder>() {


    class ViewHolder(var binding: ItemComplaintBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemComplaintBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.compAgainst.setText(complaintsList[position].complaintAgainst)
        holder.binding.tvDistrict.setText(complaintsList[position].complaintDistrict)
        holder.binding.compdetails.setText(complaintsList[position].complaintDetails)
        holder.binding.date.setText(complaintsList[position].complaintDate)
        holder.binding.time.setText(complaintsList[position].complaintTime)
        holder.binding.complaintNumber.setText(complaintsList[position].complaintNumber)
        when (complaintsList[position].status) {
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

//        if(complaintsList[position].audioUrl.isNullOrEmpty()){
//            holder.binding.icon.setImageResource(R.drawable.videoitem)
//        }
//        else if(complaintsList[position].videoUrl.isNullOrEmpty()){
//            holder.binding.icon.setImageResource(R.drawable.audioitem)
//        }
        holder.itemView.setOnClickListener{
               userComplaintClick.onClick(complaintsList[position])

        }

        holder.binding.more.setOnClickListener  {
            var bottomSheet = BottomSheetDialog(context)
            bottomSheet.setContentView(R.layout.complaint_menu_hold)
            var chat=bottomSheet.findViewById<CardView>(R.id.chat)
            var delete=bottomSheet.findViewById<CardView>(R.id.delete)
            when (complaintsList[position].status) {
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
                            FirebaseDatabase.getInstance().reference.child("Complaints")
                                .child(complaintsList[position].complaintId).removeValue()
                            if (complaintsList[position].videoUrl.isNullOrEmpty() && complaintsList[position].imageUrl.isNullOrEmpty())
                                FirebaseStorage.getInstance().reference.child("audios")
                                    .child(complaintsList[position].audioName).delete()
                            else if (complaintsList[position].audioUrl.isNullOrEmpty() && complaintsList[position].imageUrl.isNullOrEmpty())
                                FirebaseStorage.getInstance().reference.child("videos")
                                    .child(complaintsList[position].videoName).delete()
                            else if (complaintsList[position].audioUrl.isNullOrEmpty() && complaintsList[position].videoUrl.isNullOrEmpty())
                                FirebaseStorage.getInstance().reference.child("cimages")
                                    .child(complaintsList[position].imageName).delete()
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
                    FirebaseDatabase.getInstance().reference.child("Users")
                        .child(complaintsList[position].userId).child("profileValue").get()
                        .addOnCompleteListener {
                            var intent = Intent(context, ComplaintChatActivity::class.java)
                            intent.putExtra("uid", complaintsList[position].userId)
                            intent.putExtra("profile", it.result.value.toString())
                            intent.putExtra("type", "c")
                            intent.putExtra("cnumber", complaintsList[position].complaintNumber)
                            intent.putExtra("status", complaintsList[position].status)
                            intent.putExtra("against", complaintsList[position].complaintAgainst)
                            intent.putExtra("cid", complaintsList[position].complaintId)
                            intent.putExtra("name", complaintsList[position].userName)
                            context.startActivity(intent)
                        }
                }
                else{
                    Toast.makeText(context, "Check you internet connection please", Toast.LENGTH_LONG)
                        .show()
                }
            }

            bottomSheet.show()
            true
        }

//        holder.itemView.setOnLongClickListener {
//            FirebaseDatabase.getInstance().reference.child("Complaints").child(complaintsList[position].complaintId)
//                .child("status").get().addOnCompleteListener{
//                  var getstatus=it.result.value as String
//                   if(!getstatus.equals("1") || !getstatus.equals("2") || !getstatus.equals("3")){
//
//                   }
//                }
//
//            true
//        }


    }

    override fun getItemCount(): Int {
        return complaintsList.size
    }


    fun FilteredList(filteredList: ArrayList<Complaints>) {
           complaintsList=filteredList
        notifyDataSetChanged()
    }

}
