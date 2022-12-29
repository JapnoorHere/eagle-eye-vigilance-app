package com.japnoor.anticorruption.admin.Demand

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.japnoor.anticorruption.*
import com.japnoor.anticorruption.admin.AdminHomeScreen
import com.japnoor.anticorruption.admin.AdminRejectedDemandAdapter
import com.japnoor.anticorruption.admin.signUpEntity
import com.japnoor.anticorruption.databinding.DemandDialogBinding
import com.japnoor.anticorruption.databinding.FragmentAdminRejectedDemandBinding
import java.io.File

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class AdminRejectedDemand : Fragment() ,DemandClick{
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var arrayList: ArrayList<DisplayDemandEntity> = ArrayList()
    lateinit var adapter: AdminRejectedDemandAdapter
    lateinit var adminHomeScreen : AdminHomeScreen

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
adminHomeScreen=activity as AdminHomeScreen
        var binding = FragmentAdminRejectedDemandBinding.inflate(layoutInflater,container,false)
        signUpEntity = SignUpEntity()
        adapter = AdminRejectedDemandAdapter(arrayList, this)
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerView.adapter = adapter

        getRejDemand()
        return binding.root
    }
    fun getRejDemand() {
        arrayList.clear()
        class Get : AsyncTask<Void, Void, Void>() {
            override fun doInBackground(vararg p0: Void?): Void? {
                arrayList.addAll(Datbase.getDatabase(requireContext()).dao().getRejDisplayDemand())
                return null
            }

            override fun onPostExecute(result: Void?) {
                super.onPostExecute(result)
                adapter.notifyDataSetChanged()
            }

        }
        Get().execute()
    }

    override fun onClick(demandEntity: DisplayDemandEntity) {
        var dialog = Dialog(requireContext())
        var dialogBind = DemandDialogBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBind.root)
        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        dialogBind.stamp.visibility=View.VISIBLE
        dialogBind.stamp.setImageResource(R.drawable.rejected_stamp)
        dialogBind.date.setText(demandEntity.demandEntity?.dateDemand)
        dialogBind.name.setText(demandEntity.signUpEntity?.firstName)
        dialogBind.phoneno.setText(demandEntity.signUpEntity?.phoneN)
        dialogBind.email.setText(demandEntity.signUpEntity?.email)
        dialogBind.tvSummary.setText(demandEntity.demandEntity?.demandSummary)
        dialogBind.tvDetails.setText(demandEntity.demandEntity?.demandDetails)
        dialogBind.tvDistrict.setText(demandEntity.demandEntity?.districtDemand)
        dialogBind.fabAccepted.visibility=View.INVISIBLE
        dialogBind.fabRejected.visibility=View.INVISIBLE
        dialogBind.fabResolved.visibility=View.INVISIBLE
        dialogBind.image.setOnClickListener {
            val imageFile = File(demandEntity.demandEntity?.image)
            val fileUri: Uri =
                FileProvider.getUriForFile(
                    adminHomeScreen,
                    BuildConfig.APPLICATION_ID + ".provider",
                    imageFile
                )

            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(fileUri, "image/*")
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) //DO NOT FORGET THIS EVER

            startActivity(intent)
        }
        dialog.show()
    }

    fun changeStatus(demandEntity: DisplayDemandEntity, dialog: Dialog) {
        class Update : AsyncTask<Void, Void, Void>() {
            override fun doInBackground(vararg p0: Void?): Void? {
                Datbase.getDatabase(requireContext()).dao().changeStatusDemand(demandEntity.demandEntity?: DemandEntity())
                return null
            }

            override fun onPostExecute(result: Void?) {
                super.onPostExecute(result)
                dialog.dismiss()
                getRejDemand()
            }
        }
        Update().execute()
    }


}