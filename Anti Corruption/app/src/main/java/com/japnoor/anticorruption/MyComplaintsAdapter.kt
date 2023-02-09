package com.japnoor.anticorruption

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
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
        holder.binding.compSumm.setText(complaintsList[position].complaintSummary)
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
    }

    override fun getItemCount(): Int {
        return complaintsList.size
    }

    fun getuserId(position: Int): Complaints {
        return complaintsList[position]
    }

    fun FilteredList(filteredList: ArrayList<Complaints>) {
           complaintsList=filteredList
        notifyDataSetChanged()
    }

}
