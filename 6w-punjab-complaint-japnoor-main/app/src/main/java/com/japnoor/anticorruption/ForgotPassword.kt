package com.japnoor.anticorruption

import android.os.AsyncTask
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.japnoor.anticorruption.databinding.FragmentForgotPasswordBinding


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class ForgotPassword : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    lateinit var forgotPasswordActivity: ForgotPasswordActivity
    var signUpEntity:SignUpEntity?=null

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

        forgotPasswordActivity=activity as ForgotPasswordActivity
        var binding=FragmentForgotPasswordBinding.inflate(layoutInflater,container,false)

        binding.btnNext.setOnClickListener {
            if (binding.etEmail.text.isNullOrEmpty()) {
                binding.etEmail.setError("Enter Email")
            }
            else {
                class getData : AsyncTask<Void, Void, Void>() {
                    override fun doInBackground(vararg p0: Void?): Void? {
                        signUpEntity = Datbase.getDatabase(requireContext()).dao().getEmail(
                            binding.etEmail.text.toString(),
                        )
                        System.out.println("entityy $signUpEntity")
                        return null
                    }

                    override fun onPostExecute(result: Void?) {
                        super.onPostExecute(result)
                        if(signUpEntity!=null){
                            println(signUpEntity?.id)
                            var idd= signUpEntity?.id.toString()
                            var bundle = Bundle()
                            bundle.putInt("uId",Integer.parseInt(idd))

                          /*  var fragment = ForgotSecurityQuestion()
                            fragment.arguments=bundle
                            fragmentManager?.beginTransaction()?.replace(R.id.forgotSecurityQuestion,fragment)?.commit()*/
                            forgotPasswordActivity.navController.navigate(R.id.action_forgotPassword2_to_forgotSecurityQuestion, bundle)
                        }
                        else{
                            Toast.makeText(requireContext(), resources.getString(R.string.user_doesnot_exists), Toast.LENGTH_LONG).show()

                        }
                    }

                }
                getData().execute()

            }
        }
        return binding.root
    }



}