package com.japnoor.anticorruption

import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Intent
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

     var profileValue : String=""


    lateinit var database: FirebaseDatabase
    lateinit var profileRef: DatabaseReference

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
        database = FirebaseDatabase.getInstance()
        profileRef = database.reference.child("Users")

        var binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        homeScreen = activity as HomeScreen
        profileRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var dialog= Dialog(homeScreen)
                for(each in snapshot.children){
                    var userr=each.getValue(Users::class.java)
                    if(userr!=null &&  userr.userId.equals(homeScreen.id) &&  userr.userStatus.equals("1")){
                        var dialogB= BlockedUserDialogBinding.inflate(layoutInflater)
                        dialog.setContentView(dialogB.root)
                        dialog.window?.setLayout(
                            WindowManager.LayoutParams.MATCH_PARENT,
                            WindowManager.LayoutParams.WRAP_CONTENT
                        )
                        dialog.setCancelable(false)

                        dialogB.btn.setOnClickListener {
                            dialog.dismiss()
                            FirebaseAuth.getInstance().signOut()
                            var intent= Intent(homeScreen,LoginActivity::class.java)
                            homeScreen.startActivity(intent)
                            homeScreen.finish()
                        }
                        dialog.show()

                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        binding.shimmer.startShimmer()

        profileRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (user in snapshot.children) {
                    var info = user.getValue(Users::class.java)
                    if (info != null && info.userId.equals(homeScreen.id)) {
                        userName = info.name
                        userEmail = info.email
                        userProfile = info.profileValue
                        userPass=info.password
                        println("Yeh " + userName)
                        println("Yeh " + userEmail)
                        binding.name.text = userName
                        binding.email.setText(userEmail)
                        binding.pass.text=userPass
                        when(userProfile) {
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
                        binding.shimmer.visibility=View.GONE
                        binding.scroll.visibility=View.VISIBLE

                    }

                }

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        binding.Profile.setOnClickListener {
            var dialog=Dialog(homeScreen)
            var dialogBinding = DialogSelectProfileBinding.inflate(layoutInflater)
            dialog.setContentView(dialogBinding.root)
            dialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT)
            when(userProfile) {
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
                profileValue="1"
                dialogBinding.profile.setImageResource(R.drawable.man1)
                dialogBinding.donebtn.visibility=View.VISIBLE
            }
            dialogBinding.man2.setOnClickListener {
                profileValue="2"
                dialogBinding.profile.setImageResource(R.drawable.man2)
                dialogBinding.donebtn.visibility=View.VISIBLE
            }
            dialogBinding.man3.setOnClickListener {
                profileValue="3"
                dialogBinding.profile.setImageResource(R.drawable.man3)
                dialogBinding.donebtn.visibility=View.VISIBLE
            }
            dialogBinding.man4.setOnClickListener {
                profileValue="4"
                dialogBinding.profile.setImageResource(R.drawable.man4)
                dialogBinding.donebtn.visibility=View.VISIBLE
            }

            dialogBinding.girl1.setOnClickListener {
                profileValue="5"
                dialogBinding.profile.setImageResource(R.drawable.girl1)
                dialogBinding.donebtn.visibility=View.VISIBLE
            }
            dialogBinding.girl2.setOnClickListener {
                profileValue="6"
                dialogBinding.profile.setImageResource(R.drawable.girl2)
                dialogBinding.donebtn.visibility=View.VISIBLE
            }
            dialogBinding.girl3.setOnClickListener {
                profileValue="7"
                dialogBinding.profile.setImageResource(R.drawable.girl3)
                dialogBinding.donebtn.visibility=View.VISIBLE
            }
            dialogBinding.girl4.setOnClickListener {
                profileValue="8"
                dialogBinding.profile.setImageResource(R.drawable.girl4)
                dialogBinding.donebtn.visibility=View.VISIBLE
            }

            dialogBinding.donebtn.setOnClickListener {
                profileRef.child(homeScreen.id).child("profileValue").setValue(profileValue).addOnCompleteListener {
                    if(it.isSuccessful){
                        dialog.dismiss()
                    }
                }
            }
            dialog.show()
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
                if (dialogBinding.et.text.isNullOrEmpty()) {
                    dialogBinding.et.error = "Cannot be empty!"
                } else {
                    profileRef.child(homeScreen.id).child("name")
                        .setValue(dialogBinding.et.text.toString())
                    dialog.dismiss()
                }
            }
            dialog.show()
        }
        binding.btnEmail.setOnClickListener {
            var bundle=Bundle()
            bundle.putString("email",userEmail)
            bundle.putString("pass",userPass)
            homeScreen.navController.navigate(R.id.action_profileFragment_to_checkPassword,bundle)

        }

        binding.btnPass.setOnClickListener {
            var intent=Intent(homeScreen,ForgotPassword::class.java)
            startActivity(intent)
        }
        return binding.root
    }
}
