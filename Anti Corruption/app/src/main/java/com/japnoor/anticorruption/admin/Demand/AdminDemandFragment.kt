package com.japnoor.anticorruption.admin.Demand

import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.japnoor.anticorruption.Datbase
import com.japnoor.anticorruption.R
import com.japnoor.anticorruption.admin.AdminHomeScreen
import com.japnoor.anticorruption.databinding.FragmentAdminDemandBinding


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AdminDemandFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    lateinit var binding:FragmentAdminDemandBinding
    lateinit var adminHomeScreen:AdminHomeScreen
    var demandCount=1
    var demandCountAcc=1
    var demandCountRes=1
    var demandCountRej=1

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
        binding=FragmentAdminDemandBinding.inflate(layoutInflater,container,false)

        countAdminDemands()
        countAdminAccDemands()
        countAdminResDemands()
        countAdminRejDemands()
        binding.cardTotal.setOnClickListener{
            adminHomeScreen.navController.navigate(R.id.action_adminDemandFragment_to_adminTotalDemand)

        }
        binding.cardAccepted.setOnClickListener{
            adminHomeScreen.navController.navigate(R.id.action_adminDemandFragment_to_adminAcceptedDemand)

        }
        binding.cardResolved.setOnClickListener{
            adminHomeScreen.navController.navigate(R.id.action_adminDemandFragment_to_adminresolvedDemand)

        }
        binding.cardRejected.setOnClickListener {
            adminHomeScreen.navController.navigate(R.id.action_adminDemandFragment_to_adminRejectedDemand)

        }
        return binding.root
    }
    fun countAdminDemands() {
        class Get : AsyncTask<Void, Void, Void>() {
            override fun doInBackground(vararg p0: Void?): Void? {
                demandCount = Datbase.getDatabase(requireContext()).dao()
                    .adminDemandCount()
                return null
            }

            override fun onPostExecute(result: Void?) {
                super.onPostExecute(result)
                binding.tvNo1.setText(demandCount.toString())

            }
        }
        Get().execute()
    }

    fun countAdminAccDemands() {
        class Get : AsyncTask<Void, Void, Void>() {
            override fun doInBackground(vararg p0: Void?): Void? {
                demandCountAcc = Datbase.getDatabase(requireContext()).dao()
                    .adminAccDemandCount()
                return null
            }

            override fun onPostExecute(result: Void?) {
                super.onPostExecute(result)
                binding.tvNo3.setText(demandCountAcc.toString())

            }
        }
        Get().execute()
    }

    fun countAdminResDemands() {
        class Get : AsyncTask<Void, Void, Void>() {
            override fun doInBackground(vararg p0: Void?): Void? {
                demandCountRes = Datbase.getDatabase(requireContext()).dao()
                    .adminResDemandCount()
                return null
            }

            override fun onPostExecute(result: Void?) {
                super.onPostExecute(result)
                binding.tvNo2.setText(demandCountRes.toString())

            }
        }
        Get().execute()
    }

    fun countAdminRejDemands() {
        class Get : AsyncTask<Void, Void, Void>() {
            override fun doInBackground(vararg p0: Void?): Void? {
                demandCountRej = Datbase.getDatabase(requireContext()).dao()
                    .adminRejDemandCount()
                return null
            }

            override fun onPostExecute(result: Void?) {
                super.onPostExecute(result)
                binding.tvNo4.setText(demandCountRej.toString())

            }
        }
        Get().execute()
    }
}