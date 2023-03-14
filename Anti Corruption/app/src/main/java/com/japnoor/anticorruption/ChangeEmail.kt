package com.japnoor.anticorruption

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
import android.widget.Toast
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.japnoor.anticorruption.databinding.FragmentChangeEmailBinding

// TODO: Rename parameter arguments, choose names that match

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ChangeEmail : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var emailChangeActivity: EmailChangeActivity
    var email: String = ""
    var pass: String = ""
    var demarraylist = ArrayList<String>()
    lateinit var user: FirebaseAuth
    lateinit var database: FirebaseDatabase
    lateinit var userRef: DatabaseReference

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
        emailChangeActivity = activity as EmailChangeActivity
        database = FirebaseDatabase.getInstance()
        userRef = database.reference.child("Users")

        user = FirebaseAuth.getInstance()
        println(user.currentUser?.uid.toString())
        arguments.let {
            email = it?.getString("email").toString()
            pass = it?.getString("pass").toString()
            demarraylist = it?.getStringArrayList("ddids") as ArrayList<String>
        }
        var binding = FragmentChangeEmailBinding.inflate(layoutInflater, container, false)


        // Inflate the layout for this fragment

        binding.btnNext.setOnClickListener {
            if (binding.etEmail.text.toString().isNullOrEmpty()) {
                binding.etEmail.error = "Enter Email"
                binding.etEmail.requestFocus()
            } else if (binding.etEmail.text.toString().equals(email)) {
                emailChangeActivity.finish()
            } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.etEmail.text.toString()).matches()) {
                binding.etEmail.error = "Enter valid email"
                binding.etEmail.requestFocus()
            } else {
                var dialog = Dialog(emailChangeActivity)
                dialog.setContentView(R.layout.dialog_loading)
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.setCancelable(false)
                val connectivityManager =
                    emailChangeActivity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
                val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
                if (isConnected) {
                    dialog.show()
                    user.fetchSignInMethodsForEmail(binding.etEmail.text.toString())
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val signInMethods = task.result.signInMethods
                                println(signInMethods.toString())
                                if (signInMethods != null && signInMethods.contains("password")) {
                                    dialog.dismiss()
                                    Toast.makeText(
                                        emailChangeActivity,
                                        "Email already exists",
                                        Toast.LENGTH_LONG
                                    )
                                        .show()
                                } else {
                                    dialog.dismiss()
                                    val credential = EmailAuthProvider.getCredential(email, pass)
                                    FirebaseAuth.getInstance().currentUser?.reauthenticate(
                                        credential
                                    )
                                        ?.addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                Toast.makeText(
                                                    emailChangeActivity,
                                                    "OTP sent",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                                var checkvaluevent = false
                                                var comparraylist = ArrayList<String>()
                                                var bundle = Bundle()

                                                FirebaseDatabase.getInstance().reference.child("Complaints")
                                                    .addValueEventListener(object :
                                                        ValueEventListener {
                                                        override fun onDataChange(snapshot: DataSnapshot) {
                                                            for (eachcompl in snapshot.children) {
                                                                var complaintdetail =
                                                                    eachcompl.getValue(Complaints::class.java)
                                                                if (complaintdetail != null && complaintdetail.userId.equals(
                                                                        emailChangeActivity.id
                                                                    )
                                                                ) {
                                                                    comparraylist.add(
                                                                        complaintdetail.complaintId
                                                                    )
                                                                    println("Array->" + comparraylist)
                                                                    println("Array Dem->" + demarraylist)

                                                                }
                                                            }
                                                            dialog.dismiss()
                                                            bundle.putString(
                                                                "email",
                                                                binding.etEmail.text.toString()
                                                            )
                                                            bundle.putStringArrayList(
                                                                "cids",
                                                                comparraylist
                                                            )
                                                            bundle.putStringArrayList(
                                                                "ddids",
                                                                demarraylist
                                                            )
                                                            emailChangeActivity.navController.navigate(
                                                                R.id.OTPEmailChange,
                                                                bundle
                                                            )
                                                        }

                                                        override fun onCancelled(error: DatabaseError) {
                                                            TODO("Not yet implemented")
                                                        }

                                                    })


                                            } else {
                                                dialog.dismiss()
                                            }
                                        }
                                }

                            }
                        }
                } else {
                    Toast.makeText(
                        emailChangeActivity,
                        "Check your internet connection please",
                        Toast.LENGTH_LONG
                    )
                        .show()

                }
            }
        }
        return binding.root
    }

}