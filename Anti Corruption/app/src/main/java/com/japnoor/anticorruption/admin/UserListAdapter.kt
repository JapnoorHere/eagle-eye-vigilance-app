package com.japnoor.anticorruption.admin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.japnoor.anticorruption.SignUpEntity
import com.japnoor.anticorruption.databinding.ItemUserBinding

class UserListAdapter(var signUpEntity: ArrayList<SignUpEntity>, var clickInterface: ClickInterface) : RecyclerView.Adapter<UserListAdapter.ViewHolder>() {
    class ViewHolder(var binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.name.setText(signUpEntity[position].firstName)
        holder.binding.phoneno.setText(signUpEntity[position].phoneN)
        holder.itemView.setOnClickListener{
            clickInterface.click(signUpEntity[position])
        }
    }

    override fun getItemCount(): Int {
        return signUpEntity.size
    }
}