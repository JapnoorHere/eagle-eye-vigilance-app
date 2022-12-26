package com.japnoor.anticorruption

import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.japnoor.anticorruption.databinding.ActivityHomeScreenBinding

class HomeScreen : AppCompatActivity() {

    lateinit var binding: ActivityHomeScreenBinding
    lateinit var navController: NavController
     var signUpEntity : SignUpEntity?= null
    var securityQuestionEntity : SecurityQuestionEntity?=null
    private var PICK_AUDIO = 1
    var AudioUri: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)


        navController = findNavController(R.id.navController)
//        addStatus("1",signUpEntity?.id)

        supportActionBar?.setDisplayShowHomeEnabled(true)
        this.setTitle("Home")
        intent?.let {
            signUpEntity = it.getSerializableExtra("userInfo") as SignUpEntity
//            securityQuestionEntity= it.getSerializableExtra("que") as SecurityQuestionEntity
            System.out.println("signUpEntity name ${signUpEntity?.firstName}")

        }

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
                    this.setTitle(signUpEntity?.firstName)

                }
                R.id.bnDemandLetter -> {
                    navController.navigate(R.id.demandLetterFragment)
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
        menu?.add("Change Security Answer")
        menu?.add("Logout")

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

        }
        if (item.title?.equals("Home") == true) {
            navController.navigate(R.id.homeFragment)
        }
        else if (item.title?.equals("Change Password") == true) {
            navController.navigate(R.id.verifySecurityorPassworduser)
        }
        else if (item.title?.equals("Change Security Answer") == true) {
            navController.navigate(R.id.forgotSecurityQuestionUser)

        }
        else {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Logout")
            builder.setMessage("Are you sure you want to logout?")
            builder.setPositiveButton("Yes") { dialog, which ->
                var intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
                Toast.makeText(this,"Logout Successful",Toast.LENGTH_LONG).show()
                addStatus("0",signUpEntity?.id)
            }
            builder.setNegativeButton("No") { dialog, which ->
                dialog.dismiss()
            }
            builder.show()

        }
        return super.onOptionsItemSelected(item)
    }
    fun addStatus(status : String, id :Int?){

        class Add : AsyncTask<Void, Void, Void>() {
            override fun doInBackground(vararg p0: Void?): Void? {
                Datbase.getDatabase(this@HomeScreen).dao().addStatus(status,id)
                return null
            }

            override fun onPostExecute(result: Void?) {
                super.onPostExecute(result)
            }
        }
        Add().execute()
    }
}