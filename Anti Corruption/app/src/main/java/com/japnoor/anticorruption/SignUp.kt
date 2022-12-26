package com.japnoor.anticorruption

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.japnoor.anticorruption.databinding.ActivitySignUpBinding

class SignUp : AppCompatActivity() {

    lateinit var binding: ActivitySignUpBinding
    var category: String = "u"
    lateinit var sharedPreferences: SharedPreferences
    lateinit var editor : SharedPreferences.Editor
    lateinit var daao: Daao
    var flag=false
    var signUpEntity : SignUpEntity?=null
    var emailcheck :  String?=null
    var phoneNocheck :  String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreferences=getSharedPreferences(resources.getString(R.string.app_name),Context.MODE_PRIVATE)
        editor=sharedPreferences.edit()

        daao=Datbase.getDatabase(this).dao()


        binding.rgCategorySU.setOnCheckedChangeListener {group,id->
            when (id) {
                R.id.rbUser -> {
                    category = "u"
                }
                R.id.rbAdmin->{
                    category="a"

                }
                else -> {
                }
            }

        }
        binding.etRePasswordSU.doOnTextChanged { text, start, before, count ->
            if (!(text.toString() == binding.etPasswordSU.text.toString()))
                binding.etRePasswordSU.error = "Password is not same"
            else
                binding.etRePasswordSU.error = null
        }

        binding.btnSignup.setOnClickListener {
            if (binding.etFirstNameSU.text.toString().isNullOrEmpty()) {
                binding.etFirstNameSU.requestFocus()
                binding.etFirstNameSU.error = "Enter First name"
            } else if (binding.etLastNameSU.text.toString().isNullOrEmpty()) {
                binding.etLastNameSU.requestFocus()
                binding.etLastNameSU.error = "Enter last name"
            } else if (binding.etEmailSU.text.toString().isNullOrEmpty()) {
                binding.etEmailSU.requestFocus()
                binding.etEmailSU.error = "Enter email"
            }
            else if(!Patterns.EMAIL_ADDRESS.matcher(binding.etEmailSU.text.toString()).matches()){
                binding.etEmailSU.error ="Enter valid email"
                binding.etEmailSU.requestFocus()
            }
            else if (binding.etPhoneNoSU.text.toString().isNullOrEmpty()) {
                binding.etPhoneNoSU.requestFocus()
                binding.etPhoneNoSU.error = "Enter Phone NUMBER"}
            else if(binding.etPhoneNoSU.text.length<10 || binding.etPhoneNoSU.text.length>10){
                binding.etPhoneNoSU.error="Phone Number can be of 10 digits only"
                binding.etPhoneNoSU.requestFocus()
            }
            else if(binding.etPhoneNoSU.text.toString().startsWith("1")){
                binding.etPhoneNoSU.error="Not a Valid Phone Number"
                binding.etPhoneNoSU.requestFocus()
            }
            else if(binding.etPhoneNoSU.text.toString().startsWith("0")){
                binding.etPhoneNoSU.error="Not a Valid Phone Number"
                binding.etPhoneNoSU.requestFocus()
            }else if(binding.etPhoneNoSU.text.toString().startsWith("2")){
                binding.etPhoneNoSU.error="Not a Valid Phone Number"
                binding.etPhoneNoSU.requestFocus()
            }else if(binding.etPhoneNoSU.text.toString().startsWith("3")){
                binding.etPhoneNoSU.error="Not a Valid Phone Number"
                binding.etPhoneNoSU.requestFocus()
            }else if(binding.etPhoneNoSU.text.toString().startsWith("4")){
                binding.etPhoneNoSU.error="Not a Valid Phone Number"
                binding.etPhoneNoSU.requestFocus()
            }else if(binding.etPhoneNoSU.text.toString().startsWith("5")){
                binding.etPhoneNoSU.error="Not a Valid Phone Number"
                binding.etPhoneNoSU.requestFocus()
            }
            else if (binding.etPasswordSU.text.toString().isNullOrEmpty()) {
                binding.etPasswordSU.requestFocus()
                binding.etPasswordSU.error = "Enter Password"
            }
            else if(binding.etPasswordSU.text.length<6){
                binding.etPasswordSU.error="Password must be of at least 6 characters"
            } else if (binding.etRePasswordSU.text.toString().isNullOrEmpty()) {
                binding.etRePasswordSU.requestFocus()
                binding.etRePasswordSU.error = "Enter Password again"
            }
            else if (!(binding.etRePasswordSU.text.toString() == binding.etPasswordSU.text.toString())) {
                binding.etRePasswordSU.requestFocus()
                binding.etRePasswordSU.error = "Password must be same"
            }
            else {
                checkEmail(binding.etEmailSU.text.toString(),binding.etPhoneNoSU.text.toString())
            }
        }
    }



    fun addAccount(
        firstName: String,
        lastName: String,
        email: String,
        phoneNo: String,
        password: String,
        category: String
    ) {
        var signUpEntity = SignUpEntity()

        class Add : AsyncTask<Void, Void, Void>() {
            override fun doInBackground(vararg p0: Void?): Void? {
                signUpEntity.firstName = firstName
                signUpEntity.lastName = lastName
                signUpEntity.email = email
                signUpEntity.phoneN = phoneNo
                signUpEntity.pass = password
                signUpEntity.category = category
                var id = Datbase.getDatabase(this@SignUp).dao().addAccount(signUpEntity)

                print("Email $email")
                print("Id$id")
                signUpEntity.id = id.toString().toInt()

                return null
            }
            override fun onPostExecute(result: Void?) {
                super.onPostExecute(result)

                        var intent = Intent(this@SignUp, SecurityQuestion::class.java)
                        intent.putExtra("userInfo", signUpEntity)
                        startActivity(intent)


            }
        }
        Add().execute()
    }

    fun checkEmail(
        email : String,phoneNo: String
    ) {
        class Add : AsyncTask<Void, Void, Void>() {
            override fun doInBackground(vararg p0: Void?): Void? {
                emailcheck = Datbase.getDatabase(this@SignUp).dao().checkEmail(email)
                phoneNocheck = Datbase.getDatabase(this@SignUp).dao().checkPhonen(phoneNo)
                return null
            }

            override fun onPostExecute(result: Void?) {
                super.onPostExecute(result)
                if(emailcheck.equals(binding.etEmailSU.text.toString())){
                    binding.etEmailSU.error="Email already exists"
                    binding.etEmailSU.requestFocus()
                }
                else if(phoneNocheck.equals(binding.etPhoneNoSU.text.toString())){
                    binding.etPhoneNoSU.error="Phone Number already exists"
                    binding.etPhoneNoSU.requestFocus()
                }
                else{
                    if (category == "a") {
                        class getData : AsyncTask<Void, Void, Void>() {
                            override fun doInBackground(vararg p0: Void?): Void? {
                                signUpEntity = Datbase.getDatabase(this@SignUp).dao().checkAdmin()
                                System.out.println("entityy $signUpEntity")
                                return null
                            }
                            override fun onPostExecute(result: Void?) {
                                super.onPostExecute(result)
                                if (signUpEntity == null) {
                                    addAccount(
                                        binding.etFirstNameSU.text.toString(),
                                        binding.etLastNameSU.text.toString(),
                                        binding.etEmailSU.text.toString(),
                                        binding.etPhoneNoSU.text.toString(),
                                        binding.etPasswordSU.text.toString(),
                                        category
                                    )

                                } else {
                                    Toast.makeText(
                                        this@SignUp,
                                        "Admin already exists",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }
                        getData().execute()


                    }
                    else{
                        addAccount(
                            binding.etFirstNameSU.text.toString(),
                            binding.etLastNameSU.text.toString(),
                            binding.etEmailSU.text.toString(),
                            binding.etPhoneNoSU.text.toString(),
                            binding.etPasswordSU.text.toString(),
                            category
                        )

                    }
                }
            }
        }
        Add().execute()
    }

}