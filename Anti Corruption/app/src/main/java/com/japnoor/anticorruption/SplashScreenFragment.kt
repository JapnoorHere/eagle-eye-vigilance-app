package com.japnoor.anticorruption

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.japnoor.anticorruption.databinding.FragmentSplashScreenBinding

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class SplashScreenFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var auth: FirebaseAuth
    var passcode: String? = null
    var pass: String? = null
    lateinit var splashScreen: SplashScreen
    lateinit var database: FirebaseDatabase
    lateinit var useref: DatabaseReference
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
        var binding = FragmentSplashScreenBinding.inflate(layoutInflater)
        splashScreen = activity as SplashScreen
        var downAnim = AnimationUtils.loadAnimation(splashScreen, R.anim.down_anim)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        useref = database.reference.child("Users")

//        binding.ivMAimg.animation = downAnim

        Handler(Looper.getMainLooper()).postDelayed({
            val connectivityManager=splashScreen.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
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
                                    var bundle = Bundle()
                                    bundle.putString("passcode", passcode)
                                    bundle.putString("pass", pass)
                                    bundle.putString("uid", auth.currentUser?.uid.toString())
                                    splashScreen.navController.navigate(
                                        R.id.passcodeFragment,
                                        bundle
                                    )
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                    })
                } else {
                    var intent = Intent(splashScreen, LoginActivity::class.java)
                    startActivity(intent)
                    splashScreen.finish()
                }
            } else {
                Toast.makeText(
                    splashScreen,
                    "Check your internet connection please",
                    Toast.LENGTH_LONG
                ).show()
                FirebaseAuth.getInstance().signOut()
                var intent = Intent(splashScreen, LoginActivity::class.java)
                startActivity(intent)
                splashScreen.finish()
            }
        },2500)

        return binding.root
    }

}