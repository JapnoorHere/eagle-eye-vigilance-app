package com.japnoor.anticorruption

import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.japnoor.anticorruption.admin.AdminHomeScreen
import com.japnoor.anticorruption.databinding.FragmentVerifySecurityorPassworduserBinding

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class VerifySecurtiyPasswordAdmin : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var adminHomeScreen: AdminHomeScreen
    var answer = ""
    var question = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        adminHomeScreen=activity as AdminHomeScreen

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        getAnswer()
        getQuestion()
        var binding=FragmentVerifySecurityorPassworduserBinding.inflate(layoutInflater,container,false)

        binding.Question.setText(question)
        binding.btnSave.setOnClickListener {
            if(binding.etAnswer.text.isNullOrEmpty()){
                binding.etAnswer.error="Enter Answer"
                binding.etAnswer.requestFocus()
            }
            else if(!(binding.etAnswer.text.toString().equals(answer))){
                binding.etAnswer.error="Wrong Answer"
            }
            else{
                adminHomeScreen.navController.navigate(R.id.changePasswordAdmin)
            }

        }

        return binding.root
    }

    fun getAnswer(){
        class Get():AsyncTask<Void,Void,Void>(){
            override fun doInBackground(vararg p0: Void?): Void? {
                answer=   Datbase.getDatabase(requireContext()).dao().getAnswer(adminHomeScreen.signUpEntity?.id)
                return null
            }

            override fun onPostExecute(result: Void?) {
                super.onPostExecute(result)
            }
        }

        Get().execute()
    }



    fun getQuestion(){
        class Get():AsyncTask<Void,Void,Void>(){
            override fun doInBackground(vararg p0: Void?): Void? {
                question=   Datbase.getDatabase(requireContext()).dao().getQuestion(adminHomeScreen.signUpEntity?.id)
                return null
            }

            override fun onPostExecute(result: Void?) {
                super.onPostExecute(result)
            }
        }

        Get().execute()
    }


}

