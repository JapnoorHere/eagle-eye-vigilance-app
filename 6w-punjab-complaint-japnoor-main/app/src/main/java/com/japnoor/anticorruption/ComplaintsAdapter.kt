package com.japnoor.anticorruption

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.japnoor.anticorruption.databinding.ItemComlaintBinding

class ComplaintsAdapter(var complaintsEntity: ArrayList<ComplaintsEntity>,var userComplaintClick: UserComplaintClick)  : RecyclerView.Adapter<ComplaintsAdapter.ViewHolder>()  {

class ViewHolder(var binding : ItemComlaintBinding) : RecyclerView.ViewHolder(binding.root){

}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemComlaintBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
         holder.binding.tvAgainst.setText(complaintsEntity[position].complaintAgainst)
        holder.binding.tvSummary.setText(complaintsEntity[position].complaintSummary)
        holder.binding.Date.setText(complaintsEntity[position].date)
        when (complaintsEntity[position].status) {
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
        holder.binding.compno.setText(complaintsEntity[position].id.toString())
       holder.itemView.setOnClickListener{
           userComplaintClick.onClick(complaintsEntity[position])
       }
    }

    override fun getItemCount(): Int {
return complaintsEntity.size
    }
}