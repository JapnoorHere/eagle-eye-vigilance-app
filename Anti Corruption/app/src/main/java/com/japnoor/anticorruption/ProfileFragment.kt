package com.japnoor.anticorruption

import android.app.Dialog
import android.os.AsyncTask
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.google.firebase.database.*
import com.google.firebase.database.ktx.snapshots
import com.japnoor.anticorruption.databinding.FragmentProfileBinding
import com.japnoor.anticorruption.databinding.ProfileItemBinding

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ProfileFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    lateinit var homeScreen: HomeScreen

    var userName : String=""
    var userPhone : String=""
    var userEmail : String=""

    lateinit var database : FirebaseDatabase
    lateinit var profileRef : DatabaseReference

//    lateinit var dialogbindingEmail: ProfileEmailItemBinding
//    lateinit var dialogBindingPhone: ProfileItemPhoneBinding

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

        database=FirebaseDatabase.getInstance()
        profileRef=database.reference.child("Users")

        var binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        homeScreen = activity as HomeScreen


        profileRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
              for(user in snapshot.children){
                  var info=user.getValue(Users::class.java)
                  if(info!=null && info.userId.equals(homeScreen.id)) {
                      userName = info.name
                      userPhone = info.phone
                      userEmail = info.email
                      println("Yeh " + userName)
                      println("Yeh " + userEmail)
                      println("Yeh " + userPhone)
                      binding.name.text = userName
                      binding.phoneno.setText(userPhone)
                      binding.email.setText(userEmail)
                  }
              }
             }

            override fun onCancelled(error: DatabaseError) {

            }

        })



//        binding.name.setOnClickListener {
//            var dialog = Dialog(requireContext())
//            var dialogBinding = ProfileItemBinding.inflate(layoutInflater)
//            dialog.setContentView(dialogBinding.root)
//            dialog.window?.setLayout(
//                WindowManager.LayoutParams.MATCH_PARENT,
//                WindowManager.LayoutParams.WRAP_CONTENT
//            )
//            dialogBinding.et.setText(username)
//            dialogBinding.fab.setOnClickListener {
//                if (dialogBinding.et.text.isNullOrEmpty()) {
//                    dialogBinding.et.error = "Cannot be empty!"
//                } else {
//                    homeScreen.signUpEntity?.firstName = dialogBinding.et.text.toString()
//                    updateProfile(homeScreen.signUpEntity!!, dialog)
//                    homeScreen.navController.navigate(R.id.profileFragment)
//                }
//            }
//            dialog.show()
//        }
//        binding.name1.setOnClickListener {
//            var dialog = Dialog(requireContext())
//            var dialogBinding = ProfileItemBinding.inflate(layoutInflater)
//            dialog.setContentView(dialogBinding.root)
//            dialog.window?.setLayout(
//                WindowManager.LayoutParams.MATCH_PARENT,
//                WindowManager.LayoutParams.WRAP_CONTENT
//            )
//            dialogBinding.et.setText(homeScreen.signUpEntity?.firstName)
//            dialogBinding.fab.setOnClickListener {
//                if (dialogBinding.et.text.isNullOrEmpty()) {
//                    dialogBinding.et.error = "Cannot be empty!"
//                } else {
//                    homeScreen.signUpEntity?.firstName = dialogBinding.et.text.toString()
//                    updateProfile(homeScreen.signUpEntity!!, dialog)
//                    homeScreen.navController.navigate(R.id.profileFragment)
//                }
//            }
//            dialog.show()
//        }
//
//        binding.lastname.setOnClickListener {
//            var dialog = Dialog(requireContext())
//            var dialogBinding = ProfileItemBinding.inflate(layoutInflater)
//            dialog.setContentView(dialogBinding.root)
//            dialog.window?.setLayout(
//                WindowManager.LayoutParams.MATCH_PARENT,
//                WindowManager.LayoutParams.WRAP_CONTENT
//            )
//            dialogBinding.tv.setText("Last Name : ")
//            dialogBinding.tv.setHint("Enter Last Name")
//            dialogBinding.et.setText(homeScreen.signUpEntity?.lastName)
//            dialogBinding.fab.setOnClickListener {
//                if (dialogBinding.et.text.isNullOrEmpty()) {
//                    dialogBinding.et.error = "Cannot be empty!"
//                } else {
//                    homeScreen.signUpEntity?.lastName = dialogBinding.et.text.toString()
//                    updateProfile(homeScreen.signUpEntity!!, dialog)
//                    homeScreen.navController.navigate(R.id.profileFragment)
//                }
//            }
//            dialog.show()
//        }
//        binding.lastname1.setOnClickListener {
//            var dialog = Dialog(requireContext())
//            var dialogBinding = ProfileItemBinding.inflate(layoutInflater)
//            dialog.setContentView(dialogBinding.root)
//            dialog.window?.setLayout(
//                WindowManager.LayoutParams.MATCH_PARENT,
//                WindowManager.LayoutParams.WRAP_CONTENT
//            )
//            dialogBinding.tv.setText("Last Name : ")
//            dialogBinding.tv.setHint("Enter Last Name")
//            dialogBinding.et.setText(homeScreen.signUpEntity?.lastName)
//            dialogBinding.fab.setOnClickListener {
//                if (dialogBinding.et.text.isNullOrEmpty()) {
//                    dialogBinding.et.error = "Cannot be empty!"
//                } else {
//                    homeScreen.signUpEntity?.lastName = dialogBinding.et.text.toString()
//                    updateProfile(homeScreen.signUpEntity!!, dialog)
//                    homeScreen.navController.navigate(R.id.profileFragment)
//                }
//            }
//            dialog.show()
//        }
//
//        binding.phoneno.setOnClickListener {
//            var dialog = Dialog(requireContext())
//            dialogBindingPhone = ProfileItemPhoneBinding.inflate(layoutInflater)
//            dialog.setContentView(dialogBindingPhone.root)
//            dialog.window?.setLayout(
//                WindowManager.LayoutParams.MATCH_PARENT,
//                WindowManager.LayoutParams.WRAP_CONTENT
//            )
//            dialogBindingPhone.et.setText(homeScreen.signUpEntity?.phoneN)
//            dialogBindingPhone.fab.setOnClickListener {
//                if (dialogBindingPhone.et.text.isNullOrEmpty()) {
//                    dialogBindingPhone.et.error = "Cannot be empty!"
//                } else if (dialogBindingPhone.et.text.length < 10 || dialogBindingPhone.et.text.length > 10) {
//                    dialogBindingPhone.et.error = "Phone Number can be of 10 digits only"
//                } else if (dialogBindingPhone.et.text.toString().startsWith("5")) {
//                    dialogBindingPhone.et.error = "Not a Valid Phone Number"
//                    dialogBindingPhone.et.requestFocus()
//                } else if (dialogBindingPhone.et.text.toString().startsWith("4")) {
//                    dialogBindingPhone.et.error = "Not a Valid Phone Number"
//                    dialogBindingPhone.et.requestFocus()
//                } else if (dialogBindingPhone.et.text.toString().startsWith("3")) {
//                    dialogBindingPhone.et.error = "Not a Valid Phone Number"
//                    dialogBindingPhone.et.requestFocus()
//                } else if (dialogBindingPhone.et.text.toString().startsWith("2")) {
//                    dialogBindingPhone.et.error = "Not a Valid Phone Number"
//                    dialogBindingPhone.et.requestFocus()
//                } else if (dialogBindingPhone.et.text.toString().startsWith("1")) {
//                    dialogBindingPhone.et.error = "Not a Valid Phone Number"
//                    dialogBindingPhone.et.requestFocus()
//                } else if (dialogBindingPhone.et.text.toString().startsWith("0")) {
//                    dialogBindingPhone.et.error = "Not a Valid Phone Number"
//                    dialogBindingPhone.et.requestFocus()
//                } else {
//                    homeScreen.signUpEntity?.phoneN = dialogBindingPhone.et.text.toString()
//                    checkPhone(dialogBindingPhone.et.text.toString(),homeScreen.signUpEntity!!,dialog)
////                    updateProfile(homeScreen.signUpEntity!!, dialog)
//                    homeScreen.navController.navigate(R.id.profileFragment)
//                }
//            }
//            dialog.show()
//        }
//
//        binding.phoneno1.setOnClickListener {
//            var dialog = Dialog(requireContext())
//            dialogBindingPhone = ProfileItemPhoneBinding.inflate(layoutInflater)
//            dialog.setContentView(dialogBindingPhone.root)
//            dialog.window?.setLayout(
//                WindowManager.LayoutParams.MATCH_PARENT,
//                WindowManager.LayoutParams.WRAP_CONTENT
//            )
//            dialogBindingPhone.et.setText(homeScreen.signUpEntity?.phoneN)
//            dialogBindingPhone.fab.setOnClickListener {
//                if (dialogBindingPhone.et.text.isNullOrEmpty()) {
//                    dialogBindingPhone.et.error = "Cannot be empty!"
//                } else if (dialogBindingPhone.et.text.length < 10 || dialogBindingPhone.et.text.length > 10) {
//                    dialogBindingPhone.et.error = "Phone Number can be of 10 digits only"
//                } else if (dialogBindingPhone.et.text.toString().startsWith("5")) {
//                    dialogBindingPhone.et.error = "Not a Valid Phone Number"
//                    dialogBindingPhone.et.requestFocus()
//                } else if (dialogBindingPhone.et.text.toString().startsWith("4")) {
//                    dialogBindingPhone.et.error = "Not a Valid Phone Number"
//                    dialogBindingPhone.et.requestFocus()
//                } else if (dialogBindingPhone.et.text.toString().startsWith("3")) {
//                    dialogBindingPhone.et.error = "Not a Valid Phone Number"
//                    dialogBindingPhone.et.requestFocus()
//                } else if (dialogBindingPhone.et.text.toString().startsWith("2")) {
//                    dialogBindingPhone.et.error = "Not a Valid Phone Number"
//                    dialogBindingPhone.et.requestFocus()
//                } else if (dialogBindingPhone.et.text.toString().startsWith("1")) {
//                    dialogBindingPhone.et.error = "Not a Valid Phone Number"
//                    dialogBindingPhone.et.requestFocus()
//                } else if (dialogBindingPhone.et.text.toString().startsWith("0")) {
//                    dialogBindingPhone.et.error = "Not a Valid Phone Number"
//                    dialogBindingPhone.et.requestFocus()
//                } else {
//                    homeScreen.signUpEntity?.phoneN = dialogBindingPhone.et.text.toString()
//                    checkPhone(dialogBindingPhone.et.text.toString(),homeScreen.signUpEntity!!,dialog)
////                    updateProfile(homeScreen.signUpEntity!!, dialog)
//                    homeScreen.navController.navigate(R.id.profileFragment)
//                }
//            }
//            dialog.show()
//        }
//
//        binding.email.setOnClickListener {
//            var dialog = Dialog(requireContext())
//            dialogbindingEmail = ProfileEmailItemBinding.inflate(layoutInflater)
//            dialog.setContentView(dialogbindingEmail.root)
//            dialog.window?.setLayout(
//                WindowManager.LayoutParams.MATCH_PARENT,
//                WindowManager.LayoutParams.WRAP_CONTENT
//            )
//            dialogbindingEmail.et.setText(homeScreen.signUpEntity?.email)
//            dialogbindingEmail.fab.setOnClickListener {
//                if (dialogbindingEmail.et.text.isNullOrEmpty()) {
//                    dialogbindingEmail.et.error = "Cannot be empty!"
//                } else if (!Patterns.EMAIL_ADDRESS.matcher(dialogbindingEmail.et.text.toString())
//                        .matches()
//                ) {
//                    dialogbindingEmail.et.error = "Enter valid email"
//                    dialogbindingEmail.et.requestFocus()
//                } else {
//                    homeScreen.signUpEntity?.email = dialogbindingEmail.et.text.toString()
//                    checkEmail(dialogbindingEmail.et.text.toString(),homeScreen.signUpEntity!!,dialog)
////                    updateProfile(homeScreen.signUpEntity!!, dialog)
//                    homeScreen.navController.navigate(R.id.profileFragment)
//                }
//            }
//            dialog.show()
//        }
//
//        binding.email1.setOnClickListener {
//            var dialog = Dialog(requireContext())
//            dialogbindingEmail = ProfileEmailItemBinding.inflate(layoutInflater)
//            dialog.setContentView(dialogbindingEmail.root)
//            dialog.window?.setLayout(
//                WindowManager.LayoutParams.MATCH_PARENT,
//                WindowManager.LayoutParams.WRAP_CONTENT
//            )
//            dialogbindingEmail.et.setText(homeScreen.signUpEntity?.email)
//            dialogbindingEmail.fab.setOnClickListener {
//                if (dialogbindingEmail.et.text.isNullOrEmpty()) {
//                    dialogbindingEmail.et.error = "Cannot be empty!"
//                } else if (!Patterns.EMAIL_ADDRESS.matcher(dialogbindingEmail.et.text.toString())
//                        .matches()
//                ) {
//                    dialogbindingEmail.et.error = "Enter valid email"
//                    dialogbindingEmail.et.requestFocus()
//                } else {
//                    homeScreen.signUpEntity?.email = dialogbindingEmail.et.text.toString()
//                    checkEmail(dialogbindingEmail.et.text.toString(),homeScreen.signUpEntity!!,dialog)
////                    updateProfile(homeScreen.signUpEntity!!, dialog)
//                    homeScreen.navController.navigate(R.id.profileFragment)
//                }
//            }
//            dialog.show()
//        }

        return binding.root
    }


}
