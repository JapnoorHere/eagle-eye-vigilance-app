package com.japnoor.anticorruption

import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import com.japnoor.anticorruption.databinding.FragmentForgotSecurityQuestionUserBinding

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class ChangeSecurityQuestionUser : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    lateinit var binding:FragmentForgotSecurityQuestionUserBinding
    lateinit var homeScreen: HomeScreen
    var answer =""
    lateinit var arrayAdapter: ArrayAdapter<String>
    var question =""
    var states = 0

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
        homeScreen=activity as HomeScreen
        getQuestion()
        getAnswer()
        binding = FragmentForgotSecurityQuestionUserBinding.inflate(layoutInflater,container,false)
        println("ID : " + homeScreen.signUpEntity?.id)
        println("question : " + question)
        println("asnwer : " + answer)
        binding.Question.setText(question)
        binding.btnSave.setOnClickListener {
            if(binding.etAnswer.text.isNullOrEmpty()){
                binding.etAnswer.setError("Enter Answer")
            }
            else if(!(binding.etAnswer.text.toString().equals(answer))){
                Toast.makeText(requireContext(),"Wrong Answer",Toast.LENGTH_LONG).show()
            }
            else {
                binding.Question.visibility = View.GONE
                binding.Question1.visibility = View.VISIBLE
                binding.Question2.visibility = View.VISIBLE
                binding.etAnswer1.visibility=View.VISIBLE
                binding.etAnswer.visibility=View.GONE
                binding.btnSave.visibility=View.GONE
                binding.btnSave1.visibility=View.VISIBLE
                binding.etAnswer.text.clear()
            }
        }
        binding.btnSave1.setOnClickListener {
            binding.tvSetUp.setText("Setup a new Security Question and Answer!")
            if(binding.etAnswer1.text.isNullOrEmpty()){
                binding.etAnswer1.error="Enter Answer"
            }
            else {
                updateQuestion()
                updateAnser()
            }
        }
        return binding.root
    }

    fun getAnswer(){
        class getData : AsyncTask<Void, Void, Void>() {
            override fun doInBackground(vararg p0: Void?): Void? {
             answer = Datbase.getDatabase(requireContext()).dao().getAnswer(
                    homeScreen.signUpEntity?.id
                )

                return null
            }

            override fun onPostExecute(result: Void?) {
                super.onPostExecute(result)

            }

        }
        getData().execute()
    }

    fun updateQuestion(){
        class update : AsyncTask<Void, Void, Void>() {
            override fun doInBackground(vararg p0: Void?): Void? {
                Datbase.getDatabase(requireContext()).dao().updateQuestion(binding.Question1.text.toString(),
                    homeScreen.signUpEntity?.id
                )
                return null
            }

            override fun onPostExecute(result: Void?) {
                super.onPostExecute(result)
                println()
                println()
                Toast.makeText(requireContext(),"security question updated",Toast.LENGTH_LONG).show()
                homeScreen.navController.navigate(R.id.homeFragment)
            }

        }
        update().execute()
    }

fun updateAnser(){
        class update : AsyncTask<Void, Void, Void>() {
            override fun doInBackground(vararg p0: Void?): Void? {
                Datbase.getDatabase(requireContext()).dao().updateAnswer(binding.etAnswer1.text.toString(),
                    homeScreen.signUpEntity?.id
                )
                return null
            }

            override fun onPostExecute(result: Void?) {
                super.onPostExecute(result)
                println()
                println()
                Toast.makeText(requireContext(),"security question updated",Toast.LENGTH_LONG).show()
                homeScreen.navController.navigate(R.id.homeFragment)
            }

        }
        update().execute()
    }

    fun getQuestion(){
        class getData : AsyncTask<Void, Void, Void>() {
            override fun doInBackground(vararg p0: Void?): Void? {
              question=  Datbase.getDatabase(requireContext()).dao().getQuestion(
                    homeScreen.signUpEntity?.id
                )
                System.out.println(fetch)

                return null
            }

            override fun onPostExecute(result: Void?) {
                super.onPostExecute(result)
            }

        }
        getData().execute()
    }

    override fun onResume() {
        super.onResume()
        var questions = resources.getStringArray(R.array.Question)
        arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, questions)
        binding.Question1.setAdapter(arrayAdapter)
    }

}