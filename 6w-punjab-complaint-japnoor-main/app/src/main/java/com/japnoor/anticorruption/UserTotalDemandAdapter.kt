package com.japnoor.anticorruption

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.japnoor.anticorruption.databinding.ItemComlaintBinding

class UserTotalDemandAdapter(var demandEntity: ArrayList<DemandEntity>,var userDemandClick: UserDemandClick)  : RecyclerView.Adapter<UserTotalDemandAdapter.ViewHolder>()  {

    class ViewHolder(var binding : ItemComlaintBinding) : RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemComlaintBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.Against1.setText("Summary : ")
        holder.binding.tvAgainst.setText(demandEntity[position].demandSummary)
        holder.binding.tvSummary.setText(demandEntity[position].demandDetails)
        holder.binding.Date.setText(demandEntity[position].dateDemand)
        holder.itemView.setOnClickListener{
            userDemandClick.onClick(demandEntity[position])
        }
        when (demandEntity[position].status) {
            "acc" -> {
                holder.binding.CardView.setBackgroundResource(R.color.accepted)
                holder.binding.inpriority.setImageResource(R.drawable.ic_baseline_file_download_done_24)
            }
            "rej" -> {
                holder.binding.CardView.setBackgroundResource(R.color.rejected)
                holder.binding.inpriority.setImageResource(R.drawable.ic_baseline_cancel_24)
            }
            "res" -> {
                holder.binding.CardView.setBackgroundResource(R.color.resolved)
                holder.binding.inpriority.setImageResource(R.drawable.ic_baseline_done_all_24)
            }
            else->{
                holder.binding.inpriority.setImageResource(0)
            }
        }
        holder.binding.tvcomno.setText("Dem No : ")
        holder.binding.compno.setText(demandEntity[position].id.toString())
    }

    override fun getItemCount(): Int {
        return demandEntity.size
    }
}