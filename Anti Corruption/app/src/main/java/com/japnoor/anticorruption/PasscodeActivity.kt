package com.japnoor.anticorruption

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.hanks.passcodeview.PasscodeView
import com.japnoor.anticorruption.databinding.ActivityPasscodeBinding
import com.japnoor.anticorruption.databinding.PasscodeDialogBinding

class PasscodeActivity : AppCompatActivity() {
    lateinit var binding : ActivityPasscodeBinding
     var id:String=""
    var passcode  : String=""
    var pass  : String=""
    lateinit var useref : DatabaseReference
    lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityPasscodeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        database=FirebaseDatabase.getInstance()
        useref=database.reference.child("Users")

        id=intent.getStringExtra("uid").toString()
        passcode=intent.getStringExtra("passcode").toString()
        pass=intent.getStringExtra("pass").toString()



        binding.passcodeView.setPasscodeLength(5)
            .setLocalPasscode(passcode).listener = object : PasscodeView.PasscodeViewListener {
            override fun onFail() {
                TODO("Not yet implemented")
            }

            override fun onSuccess(number: String?) {
                var intent=Intent(this@PasscodeActivity,HomeScreen::class.java)
                intent.putExtra("uid",id)
                intent.putExtra("pass",pass)
                startActivity(intent)
                finish()
            }



        }
          binding.tvForgotPassword.setOnClickListener {
              var dialog= Dialog(this)
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
                      Toast.makeText(this,"Wrong Password",Toast.LENGTH_LONG).show()
                  }
              }


                      dialogBinding.btnSignup.setOnClickListener {
                          val connectivityManager =  getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
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
                                                      Toast.makeText(this@PasscodeActivity,it.exception.toString(),Toast.LENGTH_LONG).show()
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
                                  Toast.makeText(this@PasscodeActivity,"Check you Internet connection please",Toast.LENGTH_LONG).show()
                              }
                      }
                      }
              dialog.show()
          }



    }
}