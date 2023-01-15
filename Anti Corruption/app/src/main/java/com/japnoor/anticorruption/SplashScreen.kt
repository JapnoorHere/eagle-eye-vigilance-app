package com.japnoor.anticorruption

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.japnoor.anticorruption.databinding.ActivitySplashScreenBinding

class SplashScreen : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    var passcode : String?=null
    var pass: String?=null
    lateinit var database: FirebaseDatabase
    lateinit var useref : DatabaseReference

    lateinit var binding : ActivitySplashScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding=ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var downAnim = AnimationUtils.loadAnimation(this, R.anim.down_anim)
        auth = FirebaseAuth.getInstance()
        database=FirebaseDatabase.getInstance()
        useref=database.reference.child("Users")

        binding.ivMAimg.animation = downAnim

        Handler(Looper.getMainLooper()).postDelayed({
            val connectivityManager =  getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
            val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
            if (isConnected) {
                var user = auth.currentUser
                if (user != null) {
                    useref.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            for (eachUser in snapshot.children) {
                                var userr = eachUser.getValue(Users::class.java)
                                if (userr != null && userr.userId.equals(auth.currentUser?.uid)) {
                                    passcode = userr.passcode
                                    pass = userr.password
                                    println("pass-$passcode")
                                    var intent =
                                        Intent(this@SplashScreen, PasscodeActivity::class.java)
                                    intent.putExtra("passcode", passcode)
                                    intent.putExtra("pass", pass)
                                    intent.putExtra("uid", auth.currentUser?.uid.toString())
                                    startActivity(intent)
                                    finish()
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                    })
                } else {
                    var intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()

                }
            }
                else {
                      Toast.makeText(this@SplashScreen,"Check your internet connection please",Toast.LENGTH_LONG).show()
                }
            }, 2500)




    }



}