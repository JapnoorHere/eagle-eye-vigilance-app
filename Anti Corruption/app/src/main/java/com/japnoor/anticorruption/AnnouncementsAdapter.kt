package com.japnoor.anticorruption

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.japnoor.anticorruption.databinding.AnnouncementItemBinding

class AnnouncementsAdapter(
    var context: AnnouncementsActivity, var announcementsList: ArrayList<Announcements>
) : RecyclerView.Adapter<AnnouncementsAdapter.ViewHolder>() {
    class ViewHolder(var binding: AnnouncementItemBinding) : RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            AnnouncementItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.detail.text=announcementsList[position].detail
        holder.binding.subject.text=announcementsList[position].subject
        holder.binding.time.text=announcementsList[position].time
    }

    override fun getItemCount(): Int {
        return announcementsList.size
    }

}