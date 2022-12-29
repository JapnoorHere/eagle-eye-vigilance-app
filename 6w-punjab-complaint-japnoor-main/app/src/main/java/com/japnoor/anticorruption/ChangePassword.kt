package com.japnoor.anticorruption

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import com.japnoor.anticorruption.databinding.FragmentChangePasswordBinding

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class ChangePassword : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    lateinit var forgotPasswordActivity: ForgotPasswordActivity
    var uid: Int = 0

    lateinit   var binding :FragmentChangePasswordBinding
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

         binding=FragmentChangePasswordBinding.inflate(layoutInflater,container,false)
        arguments?.let {
            uid = it.getInt("uid")
        }
        System.out.println("uid $uid")
            binding.rePassword.doOnTextChanged { text, start, before, count ->
                if (!(text.toString().equals(binding.password.text.toString())))
                    binding.rePassword.error = "Password is not same"
                else
                    binding.rePassword.error = null
            }
            binding.btnNext.setOnClickListener {
                if (binding.password.text.isNullOrEmpty()) {
                    binding.password.setError("Enter Password")
                    binding.password.requestFocus()
                } else if (binding.rePassword.text.isNullOrEmpty()) {
                    binding.rePassword.setError("Enter Password again")
                    binding.rePassword.requestFocus()
                }
                else {
                    changePassword()
                }
            }

        return binding.root
    }

    fun changePassword(){
        class getData : AsyncTask<Void, Void, Void>() {
            override fun doInBackground(vararg p0: Void?): Void? {
                Datbase.getDatabase(forgotPasswordActivity).dao().updatePassword(
                    binding.password.text.toString(), uid
                )

                return null
            }

            override fun onPostExecute(result: Void?) {
                super.onPostExecute(result)
                var intent=Intent(forgotPasswordActivity,MainActivity::class.java)
                startActivity(intent)
                forgotPasswordActivity.finish()
            }

        }
        getData().execute()
    }

}