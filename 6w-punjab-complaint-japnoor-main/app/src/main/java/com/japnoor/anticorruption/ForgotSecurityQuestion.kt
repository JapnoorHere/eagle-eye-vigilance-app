package com.japnoor.anticorruption

import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import com.japnoor.anticorruption.databinding.FragmentForgotSecurityQuestionBinding

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class ForgotSecurityQuestion : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    lateinit var forgotPasswordActivity: ForgotPasswordActivity
    lateinit var forgotPassword: ForgotPassword
    lateinit var binding:FragmentForgotSecurityQuestionBinding
    var answer : String?=null
    var question : String?=null


    var fetch:Int=0


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
        binding=FragmentForgotSecurityQuestionBinding.inflate(layoutInflater,container,false)
        arguments?.let {
            fetch = it.getInt("uId")
        }
        forgotPasswordActivity=activity as ForgotPasswordActivity
        forgotPassword= ForgotPassword()
        getQuestion()
        getAnswer()

        binding.btnSave.setOnClickListener {

            if(binding.etAnswer.text.isNullOrEmpty()){
                binding.etAnswer.setError("Enter Answer")
            }
            else if(!(binding.etAnswer.text.toString().equals(answer.toString()))){
                   Toast.makeText(requireContext(),"Wrong Answer",Toast.LENGTH_LONG).show()
                            }
            else{
                forgotPasswordActivity.navController.navigate(R.id.action_forgotSecurityQuestion_to_changePassword, bundleOf("uid" to fetch))
            }
        }
        return binding.root
    }


    fun getAnswer(){
        class getData : AsyncTask<Void, Void, Void>() {
            override fun doInBackground(vararg p0: Void?): Void? {
                answer= Datbase.getDatabase(requireContext()).dao().getAnswer(
                    fetch
                )

                return null
            }

            override fun onPostExecute(result: Void?) {
                super.onPostExecute(result)

            }

        }
        getData().execute()
    }

    fun getQuestion(){
        class getData : AsyncTask<Void, Void, Void>() {
            override fun doInBackground(vararg p0: Void?): Void? {
               question= Datbase.getDatabase(requireContext()).dao().getQuestion(
                    fetch
                )
                System.out.println(fetch)

                return null
            }

            override fun onPostExecute(result: Void?) {
                super.onPostExecute(result)
                binding.Question.setText(question.toString())
            }

        }
        getData().execute()
    }
   }