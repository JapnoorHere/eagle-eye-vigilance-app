package com.japnoor.anticorruption.admin

import android.app.Dialog
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.japnoor.anticorruption.Datbase
import com.japnoor.anticorruption.R
import com.japnoor.anticorruption.SignUpEntity
import com.japnoor.anticorruption.databinding.FragmentAdminAcceptedBinding
import com.japnoor.anticorruption.databinding.FragmentUsersBinding
import com.japnoor.anticorruption.databinding.ShowUserDeatailsBinding


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class UsersFragment : Fragment(),ClickInterface{
    private var param1: String? = null
    private var param2: String? = null
    var arrayList: ArrayList<SignUpEntity> = ArrayList()
    lateinit var adapter : UserListAdapter


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
        binding = FragmentAdminAcceptedBinding.inflate(layoutInflater, container, false)

        var binding=FragmentUsersBinding.inflate(layoutInflater,container,false)
        adapter = UserListAdapter(arrayList, this)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
        getUserList()

        return binding.root
    }

    fun getUserList() {
        arrayList.clear()
        class Get : AsyncTask<Void, Void, Void>() {
            override fun doInBackground(vararg p0: Void?): Void? {
                arrayList.addAll(Datbase.getDatabase(requireContext()).dao().getUserList())
                return null
            }

            override fun onPostExecute(result: Void?) {
                super.onPostExecute(result)
                adapter.notifyDataSetChanged()
            }

        }
        Get().execute()
    }

    override fun click(signUpEntity: SignUpEntity) {
        var dialog=Dialog(requireContext())
        var dialogBinding=ShowUserDeatailsBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)
        dialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT)
        dialogBinding.name.setText(signUpEntity.firstName)
        dialogBinding.phoneno.setText(signUpEntity.phoneN)
        dialogBinding.email.setText(signUpEntity.email)
        dialogBinding.fabAdd1.setOnClickListener{
            dialog.dismiss()
        }
        dialogBinding.fabAdd2.setOnClickListener{
            var bottomSheet = BottomSheetDialog(requireContext())
            bottomSheet.setContentView(R.layout.dialog_delete_users)
            bottomSheet.show()
            var tvYes = bottomSheet.findViewById<TextView>(R.id.tvYes)
            var tvNo = bottomSheet.findViewById<TextView>(R.id.tvNo)

            tvNo?.setOnClickListener{
                bottomSheet.dismiss()
            }
            tvYes?.setOnClickListener{
            deleteUsers(signUpEntity)
                bottomSheet.dismiss()
                dialog.dismiss()
            }
        }
        dialog.show()
    }


    fun deleteUsers(signUpEntity: SignUpEntity){
            class deleteN : AsyncTask<Void, Void,Void>(){
                override fun doInBackground(vararg p0: Void?): Void? {
                    Datbase.getDatabase(requireContext()).dao().deleteUser(signUpEntity)
                    return null
                }

                override fun onPostExecute(result: Void?) {
                    super.onPostExecute(result)
                    Toast.makeText(requireContext(), "User Delete", Toast.LENGTH_SHORT).show()
                    getUserList()
                }
            }

            deleteN().execute()

    }

}