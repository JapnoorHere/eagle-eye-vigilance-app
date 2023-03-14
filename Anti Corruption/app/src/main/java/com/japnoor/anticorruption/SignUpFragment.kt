package com.japnoor.anticorruption

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.japnoor.anticorruption.databinding.FragmentSignUpBinding
import com.japnoor.anticorruption.databinding.PasscodeDialogBinding
import java.util.*
import kotlin.collections.ArrayList

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class SignUpFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    lateinit var binding : FragmentSignUpBinding
    lateinit var auth : FirebaseAuth
    lateinit var userRef : DatabaseReference
    lateinit var database: FirebaseDatabase
    lateinit var signUp: SignUp
     var emailList = ArrayList<String>()
     var age : Int=0
    var date : String=""

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
        signUp=activity as SignUp
        database=FirebaseDatabase.getInstance()
        userRef=database.reference.child("Users")
        auth=FirebaseAuth.getInstance()
        binding=FragmentSignUpBinding.inflate(layoutInflater,container,false)
        val auth = FirebaseAuth.getInstance()

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)


        binding.btntrans.setOnClickListener {
            val calendar = Calendar.getInstance()
            val currentYear = calendar.get(Calendar.YEAR)
            val currentMonth = calendar.get(Calendar.MONTH) + 1
            val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                signUp,
                DatePickerDialog.OnDateSetListener { _, year, month, day ->
                    val userBirthYear = year
                    val userBirthMonth = month + 1
                    val userBirthDay = day

                    var age = currentYear - userBirthYear
                    if (userBirthMonth > currentMonth || (userBirthMonth == currentMonth && userBirthDay > currentDay)) {
                        age--
                    }
                    if (age >= 18) {
                           binding.birthDate.setText("$userBirthDay/$userBirthMonth/$userBirthYear")
                        date="$userBirthDay/$userBirthMonth/$userBirthYear"
                    } else {
                         Toast.makeText(signUp,"At least 18 years of age is required",Toast.LENGTH_LONG).show()
                    }
                },
                2004,
                0,
                1
            )
            datePickerDialog.show()
        }


            binding.btnSignup.setOnClickListener {
                 if (binding.etEmail.text.toString().isNullOrEmpty()) {
                    binding.etEmail.error = "Enter email"
                    binding.etEmail.requestFocus()

                } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.etEmail.text.toString())
                        .matches()
                ) {
                    binding.etEmail.error = "Enter valid email"
                    binding.etEmail.requestFocus()
                } else if (binding.etPassword.text.toString().isNullOrEmpty()) {
                    binding.etPassword.error = "Enter Password"
                    binding.etPassword.requestFocus()
                } else if (binding.etPassword.text.toString().length < 6) {
                    binding.etPassword.error = "Password must be of at least 6 characters"
                    binding.etPassword.requestFocus()
                } else if (binding.etREPassword.text.toString().isNullOrEmpty()) {
                    binding.etREPassword.error = "Enter Password again"
                    binding.etREPassword.requestFocus()
                }
                else if(!(binding.etPassword.text.toString().equals(binding.etREPassword.text.toString()))){
                    binding.etREPassword.requestFocus()
                    binding.etREPassword.error="Password should be same"
                }
                else if (binding.birthDate.text.isNullOrEmpty()) {
                    binding.birthDate.requestFocus()
                    binding.birthDate.error = "Enter Your Birth Date"
                } else {
                    val connectivityManager =
                        signUp.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                    val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
                    val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
                    if (isConnected) {
                        var loaddialog=Dialog(signUp)
                        loaddialog.setContentView(R.layout.dialog_sign_loading)
                        loaddialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                        loaddialog.show()
                        loaddialog.setCancelable(false)

                        auth.fetchSignInMethodsForEmail(binding.etEmail.text.toString().trim())
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val signInMethods = task.result.signInMethods
                                    println(signInMethods.toString())
                                    if (signInMethods != null && signInMethods.contains("password")) {
                                        loaddialog.dismiss()
                                        Toast.makeText(
                                            signUp,
                                            "Email already exists",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    } else {
                                        println("Age : " + age)
                                        loaddialog.dismiss()
                                        var dialog = Dialog(signUp)
                                        var dialogBinding =
                                            PasscodeDialogBinding.inflate(layoutInflater)
                                        dialog.setContentView(dialogBinding.root)
                                        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                                        dialogBinding.btnSignup.setOnClickListener {
                                            if (dialogBinding.etPassword.text.toString()
                                                    .isNullOrEmpty()
                                            ) {
                                                dialogBinding.etPassword.error = "Enter Passcode"
                                                dialogBinding.etPassword.requestFocus()
                                            } else if (dialogBinding.etPassword.text.toString().length < 5) {
                                                dialogBinding.etPassword.error =
                                                    "Passcode must be of at least 5 characters"
                                                dialogBinding.etPassword.requestFocus()
                                            } else if (dialogBinding.etPassword.text.toString().length > 5) {
                                                dialogBinding.etPassword.error =
                                                    "Passcode must be of 5 characters only"
                                                dialogBinding.etPassword.requestFocus()
                                            } else if (dialogBinding.etREPassword.text.toString()
                                                    .isNullOrEmpty()
                                            ) {
                                                dialogBinding.etREPassword.error =
                                                    "Enter Passcode again"
                                                dialogBinding.etREPassword.requestFocus()
                                            }
                                            else if(!(dialogBinding.etPassword.text.toString().equals(dialogBinding.etREPassword.text.toString()))){
                                                dialogBinding.etREPassword.requestFocus()
                                                dialogBinding.etREPassword.error="Enter Passcode Again"
                                            }

                                            else {
                                                var name=""
                                                if(binding.etName.text.toString().isNullOrEmpty() || binding.etName.text.toString().trim().length==0){
                                                    name="Anonymous"
                                                }
                                                else{
                                                    name=binding.etName.text.toString().trim()
                                                }

                                                var bundle = Bundle()
                                                bundle.putString(
                                                    "email",
                                                    binding.etEmail.text.toString().trim()
                                                )
                                                bundle.putString(
                                                    "name",
                                                    name
                                                )
                                                bundle.putString(
                                                    "pass",
                                                    binding.etPassword.text.toString()
                                                )
                                                bundle.putString("passcode", dialogBinding.etPassword.text.toString())
                                                bundle.putString("birthdate", date)
                                                dialog.dismiss()
                                                signUp.navController.navigate(
                                                    R.id.action_signUpFragment_to_OTPFragment,
                                                    bundle
                                                )
                                            }
                                        }
                                        dialog.show()
                                    }
                                }
                            }
                    } else {
                        Toast.makeText(
                            signUp,
                            "Check your internet connection please",
                            Toast.LENGTH_LONG
                        ).show()

                    }
                }
            }
            return binding.root
        }
}