package com.japnoor.anticorruption

import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import com.japnoor.anticorruption.admin.AdminHomeScreen
import com.japnoor.anticorruption.databinding.FragmentChangePasswordUserBinding


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ChangePasswordAdmin : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var binding: FragmentChangePasswordUserBinding
    lateinit var adminHomeScreen: AdminHomeScreen

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adminHomeScreen = activity as AdminHomeScreen
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChangePasswordUserBinding.inflate(layoutInflater, container, false)


        binding.rePassword.doOnTextChanged { text, start, before, count ->
            if (!(text.toString() == binding.password.text.toString()))
                binding.rePassword.error = "Password is not same"
            else
                binding.rePassword.error = null
        }
        binding.btnNext.setOnClickListener {
            if (binding.password.text.isNullOrEmpty()) {
                binding.password.setError("Enter Password")
            } else {
                changePassword()
            }
        }

        return binding.root


    }

    fun changePassword() {
        class getData : AsyncTask<Void, Void, Void>() {
            override fun doInBackground(vararg p0: Void?): Void? {
                Datbase.getDatabase(requireContext()).dao().updatePassword(
                    binding.rePassword.text.toString(), adminHomeScreen.signUpEntity?.id
                )

                return null
            }

            override fun onPostExecute(result: Void?) {
                super.onPostExecute(result)
                adminHomeScreen.navController.navigate(R.id.adminHomeFragment)
            }

        }
        getData().execute()
    }

}