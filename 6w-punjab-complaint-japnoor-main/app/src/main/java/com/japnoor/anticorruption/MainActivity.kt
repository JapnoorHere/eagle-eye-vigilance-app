package com.japnoor.anticorruption

import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.japnoor.anticorruption.admin.AdminHomeScreen
import com.japnoor.anticorruption.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding : ActivityMainBinding
    lateinit var datbase: Datbase
    var signUpEntity : SignUpEntity?= null
    var securityQuestionEntity : SecurityQuestionEntity?= null
    var signUp  = SignUp()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        datbase = Datbase.getDatabase(this)

        binding.btnLogin.setOnClickListener {
            //validations
            if (binding.etUsername.text.isNullOrEmpty()) {
                binding.etUsername.requestFocus()
                binding.etUsername.error = "Enter Username"
            } else if (binding.etPassword.text.isNullOrEmpty()) {
                binding.etPassword.requestFocus()
                binding.etPassword.error = "Enter Password"
            } else {
                class getData : AsyncTask<Void, Void, Void>() {
                    override fun doInBackground(vararg p0: Void?): Void? {
                        signUpEntity = Datbase.getDatabase(this@MainActivity).dao().getUser(
                            binding.etUsername.text.toString(),
                            binding.etPassword.text.toString()
                        )

                        System.out.println("entityy $signUpEntity")
                        return null
                    }

                    override fun onPostExecute(result: Void?) {
                        super.onPostExecute(result)
                        if(signUpEntity != null) {
//                            class get : AsyncTask<Void, Void, Void>() {
//                                override fun doInBackground(vararg p0: Void?): Void? {
//                                    securityQuestionEntity=Datbase.getDatabase(this@MainActivity).dao()
//                                        .getQue(signUpEntity?.id)
//                                    return null
//                                }
//
//                                override fun onPostExecute(result: Void?) {
//                                    super.onPostExecute(result)
//                                }
//                            }
//                            get().execute()

                            if(signUpEntity?.category.equals("a", true)){
                                addStatus("1",signUpEntity?.id)
                                var intent=Intent(this@MainActivity,AdminHomeScreen::class.java)
                                intent.putExtra("userInfo", signUpEntity)
                                startActivity(intent)
                                finish()
                            }else {
                                addStatus("1",signUpEntity?.id)
                                var intent = Intent(this@MainActivity, HomeScreen::class.java)
                                System.out.println("signUpEntity ${signUpEntity?.id}")
                                intent.putExtra("userInfo", signUpEntity)
//                                intent.putExtra("que", securityQuestionEntity)
                                startActivity(intent)
                                finish()
                            }
                        }else{
                            Toast.makeText(this@MainActivity, "Email or Password is wrong", Toast.LENGTH_LONG).show()
                        }
                    }
                }
                getData().execute()
            }
        }
        binding.tvSignUp.setOnClickListener {
            var intent = Intent(this,SignUp::class.java)
            startActivity(intent)
        }

        binding.tvForgotPassword.setOnClickListener {
            var intent=Intent(this,ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
    }
    fun addStatus(status : String, id :Int?){

        class Add : AsyncTask<Void, Void, Void>() {
            override fun doInBackground(vararg p0: Void?): Void? {
                Datbase.getDatabase(this@MainActivity).dao().addStatus(status,id)
                return null
            }

            override fun onPostExecute(result: Void?) {
                super.onPostExecute(result)
            }
        }
        Add().execute()
    }
}