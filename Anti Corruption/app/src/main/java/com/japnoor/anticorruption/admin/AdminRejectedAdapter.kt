package com.japnoor.anticorruption.admin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.japnoor.anticorruption.DisplayComplaintsEntity
import com.japnoor.anticorruption.R
import com.japnoor.anticorruption.databinding.ItemComlaintBinding

class AdminRejectedAdapter (var complaintsEntity: ArrayList<DisplayComplaintsEntity>, var complaintClickedInterface: ComplaintClickedInterface)  : RecyclerView.Adapter<AdminRejectedAdapter.ViewHolder>(){
    class ViewHolder(var binding : ItemComlaintBinding) : RecyclerView.ViewHolder(binding.root){
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemComlaintBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.CardView.setBackgroundResource(R.color.rejected)
        holder.binding.tvAgainst.setText(complaintsEntity[position].complaintsEntity?.complaintAgainst)
        holder.binding.tvSummary.setText(complaintsEntity[position].complaintsEntity?.complaintSummary)
        holder.binding.Date.setText(complaintsEntity[position].complaintsEntity?.date)
        holder.binding.userName.visibility=View.VISIBLE
        holder.binding.userName.setText(complaintsEntity[position].userEntity?.firstName)
        holder.binding.compno.setText(complaintsEntity[position].complaintsEntity?.id.toString())


        holder.itemView.setOnClickListener{
            complaintClickedInterface.onComplaintsClicked(complaintsEntity[position])
        }
    }

    override fun getItemCount(): Int {
        return complaintsEntity.size
    }
}