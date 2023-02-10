package com.japnoor.anticorruption

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.hanks.passcodeview.PasscodeView
import com.japnoor.anticorruption.databinding.ActivityPasscodeBinding
import com.japnoor.anticorruption.databinding.FragmentPasscodeBinding
import com.japnoor.anticorruption.databinding.PasscodeDialogBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PasscodeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PasscodeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var id:String=""
    var passcode  : String=""
    var pass  : String=""
    lateinit var useref : DatabaseReference
    lateinit var database: FirebaseDatabase
    lateinit var splashScreen: SplashScreen

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

        splashScreen=activity as SplashScreen
        var binding = FragmentPasscodeBinding.inflate(layoutInflater,container,false)

        database= FirebaseDatabase.getInstance()
        useref=database.reference.child("Users")

        arguments.let {
            id = it?.getString("uid").toString()
            passcode = it?.getString("passcode").toString()
            pass = it?.getString("pass").toString()
        }

        binding.passcodeView.setPasscodeLength(5)
            .setLocalPasscode(passcode).listener = object : PasscodeView.PasscodeViewListener {
            override fun onFail() {
                TODO("Not yet implemented")
            }

            override fun onSuccess(number: String?) {
                var intent= Intent(splashScreen,LoginActivity::class.java)
                intent.putExtra("uid",id)
                intent.putExtra("pass",pass)
                splashScreen.startActivity(intent)
               splashScreen.finish()
            }

        }
        binding.tvForgotPassword.setOnClickListener {
            var dialog= Dialog(splashScreen)
            var dialogBinding= PasscodeDialogBinding.inflate(layoutInflater)
            dialog.setContentView(dialogBinding.root)
            dialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
            dialogBinding.etPasswordLayout1.visibility=View.GONE
            dialogBinding.etPasswordLayout2.visibility=View.GONE
            dialogBinding.btnSignup.visibility=View.GONE
            dialogBinding.passw.visibility = View.VISIBLE
            dialogBinding.btn.visibility = View.VISIBLE
            dialogBinding.tv.setText("Enter the 6 digit password which you entered while making your account")
            dialogBinding.btn.setOnClickListener {
                if (dialogBinding.etpass.text.toString().isNullOrEmpty()) {
                    dialogBinding.etpass.error = "Enter Password"
                    dialogBinding.etpass.requestFocus()
                } else if(dialogBinding.etpass.text.toString().equals(pass))
                {
                    dialogBinding.tv.setText("Enter the 5 digit passcode which will help \n to keep your app safe")
                    dialogBinding.etPasswordLayout1.visibility = View.VISIBLE
                    dialogBinding.etPasswordLayout2.visibility = View.VISIBLE
                    dialogBinding.btnSignup.visibility = View.VISIBLE
                    dialogBinding.passw.visibility = View.GONE
                    dialogBinding.btn.visibility = View.GONE

                }
                else{
                    Toast.makeText(splashScreen,"Wrong Password", Toast.LENGTH_LONG).show()
                }
            }


            dialogBinding.btnSignup.setOnClickListener {
                val connectivityManager =  splashScreen.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
                val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
                if(dialogBinding.etPassword.text.toString().isNullOrEmpty()){
                    dialogBinding.etPassword.error="Enter Passcode"
                    dialogBinding.etPassword.requestFocus()
                }
                else if(dialogBinding.etPassword.text.toString().length<5){
                    dialogBinding.etPassword.error="Passcode must be of at least 5 characters"
                    dialogBinding.etPassword.requestFocus()
                }
                else if(dialogBinding.etPassword.text.toString().length>5){
                    dialogBinding.etPassword.error="Passcode must be of 5 characters only "
                    dialogBinding.etPassword.requestFocus()
                }

                else if(dialogBinding.etREPassword.text.toString().isNullOrEmpty()){
                    dialogBinding.etREPassword.error="Enter Passcode again"
                    dialogBinding.etREPassword.requestFocus()
                }
                else if((!dialogBinding.etPassword.text.toString().equals(dialogBinding.etREPassword.text.toString()))){
                    dialogBinding.etREPassword.error="Passcode must be same"
                    dialogBinding.etREPassword.requestFocus()
                }


                else{
                    if(isConnected){
                        dialogBinding.btnSignup.visibility=View.GONE
                        dialogBinding.progressbar.visibility=View.VISIBLE
                        useref.addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                for(eachUser in snapshot.children){
                                    var userr=eachUser.getValue(Users::class.java)
                                    if(userr!=null && userr.userId.equals(FirebaseAuth.getInstance().currentUser?.uid)){
                                        useref.child(userr.userId).child("passcode").setValue(dialogBinding.etPassword.text.toString()).addOnCompleteListener{
                                            if(it.isSuccessful) {
                                                dialogBinding.btnSignup.visibility = View.VISIBLE
                                                dialogBinding.progressbar.visibility = View.GONE
                                                dialog.dismiss()
                                            }
                                            else{
                                                dialogBinding.btnSignup.visibility = View.VISIBLE
                                                dialogBinding.progressbar.visibility = View.GONE
                                                Toast.makeText(splashScreen,it.exception.toString(),
                                                    Toast.LENGTH_LONG).show()
                                            }
                                        }
                                    }
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }

                        })
                    }
                    else{
                        Toast.makeText(splashScreen,"Check you Internet connection please",
                            Toast.LENGTH_LONG).show()
                    }
                }
            }
            dialog.show()
        }
        return binding.root
    }


}