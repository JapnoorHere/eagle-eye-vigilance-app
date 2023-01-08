package com.japnoor.anticorruption

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.japnoor.anticorruption.databinding.ActivityHomeScreenBinding

class HomeScreen : AppCompatActivity() {

    lateinit var binding : ActivityHomeScreenBinding
    lateinit var navController:NavController
    var id : String=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityHomeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navController=findNavController(R.id.navController)
         id=intent.getStringExtra("uid").toString()

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
                R.id.bnProfile -> {
                    navController.navigate(R.id.profileFragment)
                    this.setTitle("Profile")

                }
                R.id.bnDemandLetter -> {
                    navController.navigate(R.id.addDemandLetterFragment)
                    this.setTitle("Demand Letter")

                }
                else -> {}
            }
            return@setOnItemSelectedListener true
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.add("Home")
        menu?.add("Change Password")
        menu?.add("Logout")
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.title?.equals("Home") == true) {
            navController.navigate(R.id.homeFragment)
        }
        else if (item.title?.equals("Change Password") == true) {
            var intent=Intent(this,ForgotPassword::class.java)
            intent.putExtra("id",id)
            startActivity(intent)
        }
        else {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Logout")
            builder.setMessage("Are you sure you want to logout?")
            builder.setPositiveButton("Yes") { dialog, which ->
                FirebaseAuth.getInstance().signOut()
                var intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
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