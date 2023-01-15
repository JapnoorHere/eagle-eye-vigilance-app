package com.japnoor.anticorruption

import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.japnoor.anticorruption.databinding.ActivityHomeScreenBinding
import com.japnoor.anticorruption.databinding.PasscodeDialogBinding

class HomeScreen : AppCompatActivity() {

    lateinit var binding : ActivityHomeScreenBinding
    lateinit var navController:NavController
    var id : String=""
    var pass : String=""
    lateinit var database: FirebaseDatabase
    lateinit var useref : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityHomeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navController=findNavController(R.id.navController)
         id=intent.getStringExtra("uid").toString()
         pass=intent.getStringExtra("pass").toString()
         database=FirebaseDatabase.getInstance()
        useref=database.reference.child("Users")
        println("password->$pass")
        println("password->$id")


//        println("AAH reha -> "+ id.toString())

        binding.BottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.bnHome -> {
                    navController.navigate(R.id.homeFragment)
                    this.setTitle("Home")

                }
                R.id.bnAddComplaint -> {
                    navController.navigate(R.id.addComplaintFragment)
                    this.setTitle("Add Complaint")


                }
                R.id.bnDemandLetter -> {
                    navController.navigate(R.id.addDemandLetterFragment)
                    this.setTitle("Demand Letter")

                }
                R.id.audio -> {
                    navController.navigate(R.id.audiorecordingListFragment)
                    this.setTitle("Audio Recordings")

                }
                R.id.video -> {
                    navController.navigate(R.id.videoRecordingList)
                    this.setTitle("Video Recordings")

                }
                else -> {}
            }
            return@setOnItemSelectedListener true
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.add("Home")
        menu?.add("Profile")
        menu?.add("Change Passcode")
        menu?.add("Logout")
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.title?.equals("Home") == true) {
            navController.navigate(R.id.homeFragment)
        }
        else if (item.title?.equals("Profile") == true) {
            navController.navigate(R.id.profileFragment)
        }
        else if (item.title?.equals("Change Passcode") == true) {
            var dialog= Dialog(this)
            var dialogBinding= PasscodeDialogBinding.inflate(layoutInflater)
            dialog.setContentView(dialogBinding.root)
            dialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
            dialogBinding.etPasswordLayout1.visibility= View.GONE
            dialogBinding.etPasswordLayout2.visibility= View.GONE
            dialogBinding.btnSignup.visibility= View.GONE
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
                    dialogBinding.btnSignup.visibility= View.GONE
                    dialogBinding.progressbar.visibility= View.VISIBLE
                    useref.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            for(eachUser in snapshot.children){
                                var userr=eachUser.getValue(Users::class.java)
                                if(userr!=null && userr.userId.equals(id)){
                                    useref.child(id).child("passcode").setValue(dialogBinding.etPassword.text.toString()).addOnCompleteListener{
                                        if(it.isSuccessful) {
                                            dialogBinding.btnSignup.visibility = View.VISIBLE
                                            dialogBinding.progressbar.visibility = View.GONE
                                            dialog.dismiss()
                                        }
                                        else{
                                            dialogBinding.btnSignup.visibility = View.VISIBLE
                                            dialogBinding.progressbar.visibility = View.GONE
                                            Toast.makeText(this@HomeScreen,it.exception.toString(),Toast.LENGTH_LONG).show()
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
            }

            dialog.show()
        }
        else {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Logout")
            builder.setMessage("Are you sure you want to logout?")
            builder.setPositiveButton("Yes") { dialog, which ->
                var intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
                FirebaseAuth.getInstance().signOut()
                Toast.makeText(this, "Logout Successful", Toast.LENGTH_LONG).show()
            }
            builder.setNegativeButton("No") { dialog, which ->
                dialog.dismiss()
            }
            builder.show()

        }
        return super.onOptionsItemSelected(item)
    }
}