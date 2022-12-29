package com.japnoor.anticorruption.admin

import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.japnoor.anticorruption.Datbase
import com.japnoor.anticorruption.MainActivity
import com.japnoor.anticorruption.R
import com.japnoor.anticorruption.SignUpEntity
import com.japnoor.anticorruption.databinding.ActivityAdminHomeScreenBinding

class AdminHomeScreen : AppCompatActivity() {

    lateinit var binding: ActivityAdminHomeScreenBinding
    lateinit var navController: NavController
    var signUpEntity: SignUpEntity? = null
    lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminHomeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navController = findNavController(R.id.navControllerAdmin)
        supportActionBar?.setDisplayShowHomeEnabled(true)


        intent?.let {
            signUpEntity = it.getSerializableExtra("userInfo") as SignUpEntity
            System.out.println("signUpEntity name ${signUpEntity?.firstName}")
        }

        binding.BottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.bnComplaint -> {
                    navController.navigate(R.id.adminHomeFragment)
                    this.setTitle("Complaints")

                }
                R.id.bnDemandLetter -> {
                    navController.navigate(R.id.adminDemandFragment)
                    this.setTitle("Demand Letters")


                }
                R.id.bnProfile -> {
                    navController.navigate(R.id.adminProfile)
                    this.setTitle(signUpEntity?.firstName)

                }
                else -> {}
            }
            return@setOnItemSelectedListener true


        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.add("Home")
        menu?.add("Users")
        menu?.add("Change Password")
        menu?.add("Change Security Answer")
        menu?.add("Logout")

        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.title?.equals("Home") == true) {
            navController.navigate(R.id.adminHomeFragment)
        } else if (item.title?.equals("Users") == true) {
            navController.navigate(R.id.usersFragment)
        } else if (item.title?.equals("Change Password") == true) {
           navController.navigate(R.id.verifySecurtiyPasswordAdmin)
        } else if (item.title?.equals("Change Security Answer") == true) {
            navController.navigate(R.id.changeSecurityQuestionAdmin)
        } else {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Logout")
            builder.setMessage("Are you sure you want to logout?")
            builder.setPositiveButton("Yes") { dialog, which ->
                var intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
                Toast.makeText(this,"Logout Successful", Toast.LENGTH_LONG).show()
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
                Datbase.getDatabase(this@AdminHomeScreen).dao().addStatus(status,id)
                return null
            }

            override fun onPostExecute(result: Void?) {
                super.onPostExecute(result)
            }
        }
        Add().execute()
    }
}



