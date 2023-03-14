package com.japnoor.anticorruption

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.japnoor.anticorruption.databinding.ActivityAnnouncementsBinding

class AnnouncementsActivity : AppCompatActivity() {

    lateinit var announcementsList: ArrayList<Announcements>
    lateinit var announcementAdapter: AnnouncementsAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var binding=ActivityAnnouncementsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        announcementsList=ArrayList()
        announcementAdapter=AnnouncementsAdapter(this@AnnouncementsActivity,announcementsList)

        FirebaseDatabase.getInstance().reference.child("Announcements").addValueEventListener(object : ValueEventListener
             {
            override fun onDataChange(snapshot: DataSnapshot) {
                announcementsList.clear()
                for(each in snapshot.children){
                    var announcemnt = each.getValue(Announcements::class.java)
                    if(announcemnt!=null ){
                        announcementsList.add(announcemnt)
                    }
                    announcementsList.reverse()
                    announcementAdapter= AnnouncementsAdapter(this@AnnouncementsActivity,announcementsList)
                    binding.recyclerView.layoutManager= LinearLayoutManager(this@AnnouncementsActivity)
                    binding.recyclerView.adapter=announcementAdapter
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }


        })

    }
}