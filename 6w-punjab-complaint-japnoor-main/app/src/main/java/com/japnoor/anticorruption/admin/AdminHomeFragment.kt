package com.japnoor.anticorruption.admin

import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.japnoor.anticorruption.Datbase
import com.japnoor.anticorruption.R
import com.japnoor.anticorruption.databinding.FragmentAdminHomeBinding

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class AdminHomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var binding : FragmentAdminHomeBinding
    lateinit var adminHomeScreen: AdminHomeScreen
    var complaintCount=1
    var complaintCountAcc=1
    var complaintCountRes=1
    var complaintCountRej=1


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
        countAdminComplaints()
        countAdminResComplaints()
        countAdminRejComplaints()
        countAdminAccComplaints()
        binding= FragmentAdminHomeBinding.inflate(layoutInflater,container,false)
        adminHomeScreen=activity as AdminHomeScreen


        binding.cardTotal.setOnClickListener{
            adminHomeScreen.navController.navigate(R.id.action_adminHomeFragment_to_adminTotalComplaints)

        }
        binding.cardAccepted.setOnClickListener{
            adminHomeScreen.navController.navigate(R.id.action_adminHomeFragment_to_adminAcceptedFragment)

        }
        binding.cardResolved.setOnClickListener{
            adminHomeScreen.navController.navigate(R.id.action_adminHomeFragment_to_adminResolvedFragment)

        }
        binding.cardRejected.setOnClickListener {
            adminHomeScreen.navController.navigate(R.id.action_adminHomeFragment_to_adminRejectedFragment)

        }

        return binding.root
    }

    fun countAdminComplaints() {
        class Get : AsyncTask<Void, Void, Void>() {
            override fun doInBackground(vararg p0: Void?): Void? {
               complaintCount = Datbase.getDatabase(requireContext()).dao()
                    .adminComplaintsCount()
                return null
            }

            override fun onPostExecute(result: Void?) {
                super.onPostExecute(result)
                binding.tvNo1.setText(complaintCount.toString())

            }
        }
        Get().execute()
    }

    fun countAdminAccComplaints() {
        class Get : AsyncTask<Void, Void, Void>() {
            override fun doInBackground(vararg p0: Void?): Void? {
               complaintCountAcc = Datbase.getDatabase(requireContext()).dao()
                    .adminAccComplaintsCount()
                return null
            }

            override fun onPostExecute(result: Void?) {
                super.onPostExecute(result)
                binding.tvNo3.setText(complaintCountAcc.toString())

            }
        }
        Get().execute()
    }

    fun countAdminResComplaints() {
        class Get : AsyncTask<Void, Void, Void>() {
            override fun doInBackground(vararg p0: Void?): Void? {
               complaintCountRes = Datbase.getDatabase(requireContext()).dao()
                    .adminResComplaintsCount()
                return null
            }

            override fun onPostExecute(result: Void?) {
                super.onPostExecute(result)
                binding.tvNo2.setText(complaintCountRes.toString())

            }
        }
        Get().execute()
    }

    fun countAdminRejComplaints() {
        class Get : AsyncTask<Void, Void, Void>() {
            override fun doInBackground(vararg p0: Void?): Void? {
               complaintCountRej = Datbase.getDatabase(requireContext()).dao()
                    .adminRejComplaintsCount()
                return null
            }

            override fun onPostExecute(result: Void?) {
                super.onPostExecute(result)
                binding.tvNo4.setText(complaintCountRej.toString())

            }
        }
        Get().execute()
    }


}