package com.japnoor.anticorruption

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.japnoor.anticorruption.databinding.ActivityLoginBinding
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding
    lateinit var auth: FirebaseAuth
    var passcode: String? = null
    lateinit var database: FirebaseDatabase
    lateinit var useref: DatabaseReference
    lateinit var sharedPreferencesInst: SharedPreferences
    lateinit var editorInst: SharedPreferences.Editor
    var encryptionKey: String? =null
    var secretKeySpec: SecretKeySpec? =null
    private fun encrypt(input: String): String {
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec)
        val encryptedBytes = cipher.doFinal(input.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
    }

    private fun decrypt(input: String): String {
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec)
        val decryptedBytes = cipher.doFinal(Base64.decode(input, Base64.DEFAULT))
        return String(decryptedBytes, Charsets.UTF_8)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var forgot = ForogotPasscode()
        encryptionKey=forgot.key()
        secretKeySpec = SecretKeySpec(encryptionKey!!.toByteArray(), "AES")
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        useref = database.reference.child("Users")
        passcode = intent.getStringExtra("passcode").toString()
        supportActionBar?.setDisplayShowHomeEnabled(true)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        sharedPreferencesInst=getSharedPreferences("instructions",Context.MODE_PRIVATE)
        editorInst=sharedPreferencesInst.edit()
        this.title = "Sign Up"
        binding.tvSignUp.setOnClickListener {
            var intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }

        binding.btnLogin.setOnClickListener {
            //validations
            if (binding.etUsername.text.isNullOrEmpty()) {
                binding.etUsername.requestFocus()
                binding.etUsername.error = "Enter Username"
            } else if (binding.etPassword.text.isNullOrEmpty()) {
                binding.etPassword.requestFocus()
                binding.etPassword.error = "Enter Password"
            }
            else if(binding.etUsername.text.toString().equals("eagleeyevigilance@gmail.com")){
                Toast.makeText(this, "Your does not exists", Toast.LENGTH_LONG).show()
            }
            else {
                val connectivityManager =
                    getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
                val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
                if (isConnected) {
                    var laoddialog= Dialog(this)
                    laoddialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    laoddialog.setContentView(R.layout.dialog_loading)
                    laoddialog.show()
                    laoddialog.setCancelable(false)
                    auth.signInWithEmailAndPassword(
                        binding.etUsername.text.toString().trim(),
                        binding.etPassword.text.toString()
                    ).addOnCompleteListener(this) {
                        if (it.isSuccessful) {
                            Toast.makeText(this, "Welcome!", Toast.LENGTH_LONG).show()
                            var intent = Intent(this, HomeScreen::class.java)
                            intent.putExtra("uid", auth.currentUser?.uid)
                            intent.putExtra("pass",binding.etPassword.text.toString())
                            FirebaseDatabase.getInstance().reference.child("Users")
                                .child(auth.currentUser?.uid.toString())
                                .child("password")
                                .setValue(encrypt(binding.etPassword.text.toString())).addOnCompleteListener {
                                    editorInst.remove("tapTarget")
                                    editorInst.remove("tapTargetAudio")
                                    editorInst.remove("tapTargetProfile")
                                    editorInst.remove("tapTargetVideo")
                                    editorInst.apply()
                                    editorInst.commit()
                                    laoddialog.dismiss()
                                startActivity(intent)
                                finish()
                            }

                        } else if (it.exception.toString()
                                .equals("com.google.firebase.auth.FirebaseAuthInvalidUserException: There is no user record corresponding to this identifier. The user may have been deleted.")
                        ) {
                            Toast.makeText(this, "Your does not exists", Toast.LENGTH_LONG).show()
                            laoddialog.dismiss()
                        }
                        else if (it.exception.toString().equals("com.google.firebase.auth.FirebaseAuthInvalidCredentialsException: The email address is badly formatted."))
                        {
                            Toast.makeText(this, "Not a valid email", Toast.LENGTH_LONG).show()
                            laoddialog.dismiss()
                        }

                        else if (it.exception.toString().equals("com.google.firebase.FirebaseException: An internal error has occurred. [ Read error:ssl=0xbfe13d98: I/O error during system call, Software caused connection abort ]"))
                        {
                            Toast.makeText(this, "Check Your Internet Connection Please", Toast.LENGTH_LONG).show()
                            laoddialog.dismiss()
                        }

                        else if (it.exception.toString().equals("com.google.firebase.FirebaseException: An internal error has occurred. [ Read error:ssl=0xc7b4dc08: I/O error during system call, Software caused connection abort ]/O error during system call, Software caused connection abort ]"))
                        {
                            Toast.makeText(this, "Check Your Internet Connection Please", Toast.LENGTH_LONG).show()
                            laoddialog.dismiss()
                        }
                        else if (it.exception.toString()
                                .equals("com.google.firebase.auth.FirebaseAuthInvalidCredentialsException: The password is invalid or the user does not have a password.")
                        ) {
                            Toast.makeText(this, "Wrong Password", Toast.LENGTH_LONG).show()
                            laoddialog.dismiss()
                        } else {
                            laoddialog.dismiss()
                            println(it.exception.toString())
                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_LONG).show()
                        }
                    }

                } else {
                    Toast.makeText(this, "Check your internet connection please", Toast.LENGTH_LONG).show()

                }
            }
        }

        binding.tvForgotPassword.setOnClickListener {
            var intent = Intent(this, ForgotPassword::class.java)
            startActivity(intent)
        }
    }



}