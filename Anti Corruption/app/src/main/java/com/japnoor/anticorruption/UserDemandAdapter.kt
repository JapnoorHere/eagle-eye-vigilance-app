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

class UserDemandAdapter(var context: HomeScreen, var demandlist: ArrayList<DemandLetter>,var userDemandClick: UserDemandClick)  : RecyclerView.Adapter<UserDemandAdapter.ViewHolder>() {


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
        holder.binding.Against1.setText("Summary : ")
        holder.binding.tvAgainst.setText(demandlist[position].demandSubject)
        holder.binding.tvSummary.setText(demandlist[position].demandDetails)
        holder.binding.Date.setText(demandlist[position].demandDate)
        holder.itemView.setOnClickListener{
            userDemandClick.onClick(demandlist[position])
        }
        when (demandlist[position].status) {
            "1" -> {
                holder.binding.CardView.setCardBackgroundColor(Color.parseColor("#FF9800"))
                holder.binding.inpriority.setImageResource(R.drawable.ic_baseline_file_download_done_24)
            }
            "3" -> {
                holder.binding.CardView.setCardBackgroundColor(Color.parseColor("#ff0000"))
                holder.binding.inpriority.setImageResource(R.drawable.ic_baseline_cancel_24)
            }
            "2" -> {
                holder.binding.CardView.setCardBackgroundColor(Color.parseColor("#3BAAFF"))
                holder.binding.inpriority.setImageResource(R.drawable.ic_baseline_done_all_24)
            }
            else->{
                holder.binding.inpriority.setImageResource(0)
            }
        }
        holder.binding.tvcomno.setText("Dem No : ")
    }

    override fun getItemCount(): Int {
        return demandlist.size
    }



}
