package com.japnoor.anticorruption

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.AsyncTask
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.snapshots
import com.japnoor.anticorruption.databinding.BlockedUserDialogBinding
import com.japnoor.anticorruption.databinding.DialogSelectProfileBinding
import com.japnoor.anticorruption.databinding.FragmentProfileBinding
import com.japnoor.anticorruption.databinding.ProfileItemBinding
import com.japnoor.anticorruption.databinding.ProfileItemEmailBinding
import java.util.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ProfileFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    lateinit var homeScreen: HomeScreen

    var userName: String = ""
    var userEmail: String = ""
    var userProfile: String = ""
    var userPass: String = ""
    var userDate: String = ""
    var newdate: String = ""
    var profileValue: String = ""


    lateinit var database: FirebaseDatabase
    lateinit var profileRef: DatabaseReference

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
        database = FirebaseDatabase.getInstance()
        profileRef = database.reference.child("Users")
        var binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        homeScreen = activity as HomeScreen


        binding.shimmer.startShimmer()

        profileRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (user in snapshot.children) {
                    var info = user.getValue(Users::class.java)
                    if (info != null && info.userId.equals(homeScreen.id)) {
                        userName = info.name
                        userEmail = info.email
                        userProfile = info.profileValue
                        userPass = info.password
                        userDate = info.birthdate
                        println("Yeh " + userName)
                        println("Yeh " + userEmail)
                        binding.name.text = userName
                        binding.email.text = userEmail
                        binding.pass.text = userPass
                        binding.birthdate.text = userDate
                        when (userProfile) {
                            "1" -> binding.Profile.setImageResource(R.drawable.man1)
                            "2" -> binding.Profile.setImageResource(R.drawable.man2)
                            "3" -> binding.Profile.setImageResource(R.drawable.man3)
                            "4" -> binding.Profile.setImageResource(R.drawable.man4)
                            "5" -> binding.Profile.setImageResource(R.drawable.girl1)
                            "6" -> binding.Profile.setImageResource(R.drawable.girl2)
                            "7" -> binding.Profile.setImageResource(R.drawable.girl3)
                            "8" -> binding.Profile.setImageResource(R.drawable.girl4)
                        }
                        binding.shimmer.stopShimmer()
                        binding.shimmer.visibility = View.GONE
                        binding.scroll.visibility = View.VISIBLE

                    }

                }

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        binding.btndate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val currentYear = calendar.get(Calendar.YEAR)
            val currentMonth = calendar.get(Calendar.MONTH) + 1
            val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                homeScreen,
                DatePickerDialog.OnDateSetListener { _, year, month, day ->
                    val userBirthYear = year
                    val userBirthMonth = month + 1
                    val userBirthDay = day

                    var age = currentYear - userBirthYear
                    if (userBirthMonth > currentMonth || (userBirthMonth == currentMonth && userBirthDay > currentDay)) {
                        age--
                    }
                    if (age >= 18) {
                        binding.birthdate.text = "$userBirthDay/$userBirthMonth/$userBirthYear"
                        newdate = "$userBirthDay/$userBirthMonth/$userBirthYear"
                        val connectivityManager =
                            homeScreen.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
                        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
                        if (isConnected) {
                            homeScreen.finish()
                            profileRef.child(homeScreen.id).child("birthdate").setValue(newdate)
                        } else {
                            Toast.makeText(
                                homeScreen,
                                "Check you internet connection please",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            homeScreen,
                            "At least 18 years of age is required",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }, 2004,
                0,
                1
            )
            datePickerDialog.show()
        }


        binding.Profile.setOnClickListener {
            var dialog = Dialog(homeScreen)
            var dialogBinding = DialogSelectProfileBinding.inflate(layoutInflater)
            dialog.setContentView(dialogBinding.root)
            dialog.window?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
            when (userProfile) {
                "1" -> dialogBinding.profile.setImageResource(R.drawable.man1)
                "2" -> dialogBinding.profile.setImageResource(R.drawable.man2)
                "3" -> dialogBinding.profile.setImageResource(R.drawable.man3)
                "4" -> dialogBinding.profile.setImageResource(R.drawable.man4)
                "5" -> dialogBinding.profile.setImageResource(R.drawable.girl1)
                "6" -> dialogBinding.profile.setImageResource(R.drawable.girl2)
                "7" -> dialogBinding.profile.setImageResource(R.drawable.girl3)
                "8" -> dialogBinding.profile.setImageResource(R.drawable.girl4)
            }
            dialogBinding.man1.setOnClickListener {
                profileValue = "1"
                dialogBinding.profile.setImageResource(R.drawable.man1)
                dialogBinding.donebtn.visibility = View.VISIBLE
            }
            dialogBinding.man2.setOnClickListener {
                profileValue = "2"
                dialogBinding.profile.setImageResource(R.drawable.man2)
                dialogBinding.donebtn.visibility = View.VISIBLE
            }
            dialogBinding.man3.setOnClickListener {
                profileValue = "3"
                dialogBinding.profile.setImageResource(R.drawable.man3)
                dialogBinding.donebtn.visibility = View.VISIBLE
            }
            dialogBinding.man4.setOnClickListener {
                profileValue = "4"
                dialogBinding.profile.setImageResource(R.drawable.man4)
                dialogBinding.donebtn.visibility = View.VISIBLE
            }

            dialogBinding.girl1.setOnClickListener {
                profileValue = "5"
                dialogBinding.profile.setImageResource(R.drawable.girl1)
                dialogBinding.donebtn.visibility = View.VISIBLE
            }
            dialogBinding.girl2.setOnClickListener {
                profileValue = "6"
                dialogBinding.profile.setImageResource(R.drawable.girl2)
                dialogBinding.donebtn.visibility = View.VISIBLE
            }
            dialogBinding.girl3.setOnClickListener {
                profileValue = "7"
                dialogBinding.profile.setImageResource(R.drawable.girl3)
                dialogBinding.donebtn.visibility = View.VISIBLE
            }
            dialogBinding.girl4.setOnClickListener {
                profileValue = "8"
                dialogBinding.profile.setImageResource(R.drawable.girl4)
                dialogBinding.donebtn.visibility = View.VISIBLE
            }
            val connectivityManager =
                homeScreen.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
            val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
            if (isConnected) {
                dialogBinding.donebtn.setOnClickListener {
                    profileRef.child(homeScreen.id).child("profileValue").setValue(profileValue)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                homeScreen.finish()
                                dialog.dismiss()
                            }
                        }
                }
                dialog.show()
            } else {
                Toast.makeText(
                    homeScreen,
                    "Check you internet connection please",
                    Toast.LENGTH_LONG
                ).show()

            }
        }



        binding.btnName.setOnClickListener {

            var dialog = Dialog(requireContext())
            var dialogBinding = ProfileItemBinding.inflate(layoutInflater)
            dialog.setContentView(dialogBinding.root)
            dialog.window?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            dialogBinding.et.setText(userName)
            dialogBinding.fab.setOnClickListener {
                val input: String = dialogBinding.et.getText().toString().trim()
                if (dialogBinding.et.text.isNullOrEmpty()) {
                    dialogBinding.et.error = "Cannot be empty!"
                } else if (input.length == 0) {
                    dialogBinding.et.requestFocus()
                    dialogBinding.et.error = "Enter Some characters "
                } else {

                    val connectivityManager =
                        homeScreen.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                    val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
                    val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
                    if (isConnected) {
                        homeScreen.finish()
                        profileRef.child(homeScreen.id).child("name")
                            .setValue(dialogBinding.et.text.toString())
                        dialog.dismiss()
                    } else {
                        Toast.makeText(
                            homeScreen,
                            "Check you internet connection please",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
            dialog.show()
        }
        binding.btnEmail.setOnClickListener {
            var bundle = Bundle()
            bundle.putString("email", userEmail)
            bundle.putString("pass", userPass)
            homeScreen.navController.navigate(R.id.action_profileFragment_to_checkPassword, bundle)

        }

        binding.btnPass.setOnClickListener {
            var intent = Intent(homeScreen, ForgotPassword::class.java)
            startActivity(intent)
        }
        return binding.root
    }
}
