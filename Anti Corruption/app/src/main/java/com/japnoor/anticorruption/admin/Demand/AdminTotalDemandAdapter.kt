package com.japnoor.anticorruption.admin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.japnoor.anticorruption.*
import com.japnoor.anticorruption.admin.Demand.DemandClick
import com.japnoor.anticorruption.databinding.ItemComlaintBinding

class AdminTotalDemandAdapter (var demandEntity: ArrayList<DisplayDemandEntity>, var demandClick: DemandClick)  : RecyclerView.Adapter<AdminTotalDemandAdapter.ViewHolder>(){
    class ViewHolder(var binding : ItemComlaintBinding) : RecyclerView.ViewHolder(binding.root){
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemComlaintBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.Against1.setText("Summary : ")
        holder.binding.tvAgainst.setText(demandEntity[position].demandEntity?.demandSummary)
        holder.binding.tvSummary.setText(demandEntity[position].demandEntity?.demandDetails)
        holder.binding.Date.setText(demandEntity[position].demandEntity?.dateDemand)
        holder.binding.userName.visibility=View.VISIBLE
        holder.binding.userName.setText(demandEntity[position].signUpEntity?.firstName)
        holder.binding.compno.setText(demandEntity[position].demandEntity?.id.toString())
        holder.binding.tvcomno.setText("Dem No : ")
        holder.itemView.setOnClickListener{
            demandClick.onClick(demandEntity[position])
        }
    }

    override fun getItemCount(): Int {
        return demandEntity.size
    }
}