package com.japnoor.anticorruption

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Base64
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.japnoor.anticorruption.databinding.FragmentSelectProfileBinding
import java.lang.Byte.decode
import java.text.SimpleDateFormat
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import kotlin.math.sign

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class SelectProfile : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var sharedPreferences: SharedPreferences
    lateinit var editor: Editor
    lateinit var sharedPreferencesInst: SharedPreferences
    lateinit var editorInst: Editor
    var id : String=""
    var pass : String=""
    var passcode : String=""
    var birthdate : String=""
    var name : String=""
    var email : String=""
    var profileValue : String=""
    lateinit var userRef : DatabaseReference
    lateinit var database : FirebaseDatabase
    lateinit var signUp: SignUp
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
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var forgot = ForogotPasscode()
        encryptionKey=forgot.key()
        secretKeySpec = SecretKeySpec(encryptionKey!!.toByteArray(), "AES")
        var binding=FragmentSelectProfileBinding.inflate(layoutInflater,container,false)

        signUp=activity as SignUp

        sharedPreferences=signUp.getSharedPreferences("Instructions",Context.MODE_PRIVATE)
        editor=sharedPreferences.edit()
        editor.putString("instructionsOnce", "0")
        editor.putString("instructionsOnceDem", "0")
        editor.remove("instRemind")
        editor.apply()
        editor.commit()

        sharedPreferencesInst=signUp.getSharedPreferences("instructions",Context.MODE_PRIVATE)
        editorInst=sharedPreferencesInst.edit()

        arguments.let {
            id = encrypt(it?.getString("id").toString())
            pass = it?.getString("pass").toString()
            name = encrypt(it?.getString("name").toString())
            email = it?.getString("email").toString()
            passcode = encrypt(it?.getString("passcode").toString())
            birthdate = encrypt(it?.getString("birthdate").toString())

        }
        println("Password pro -> " + pass)
        database=FirebaseDatabase.getInstance()
        userRef=database.reference.child("Users")

        binding.man1.setOnClickListener {
            profileValue="1"
            binding.profile.setImageResource(R.drawable.man1)
            binding.donebtn.visibility=View.VISIBLE
        }
        binding.man2.setOnClickListener {
            profileValue="2"
            binding.profile.setImageResource(R.drawable.man2)
            binding.donebtn.visibility=View.VISIBLE
        }
        binding.man3.setOnClickListener {
            profileValue="3"
            binding.profile.setImageResource(R.drawable.man3)
            binding.donebtn.visibility=View.VISIBLE
        }
        binding.man4.setOnClickListener {
            profileValue="4"
            binding.profile.setImageResource(R.drawable.man4)
            binding.donebtn.visibility=View.VISIBLE
        }

        binding.girl1.setOnClickListener {
            profileValue="5"
            binding.profile.setImageResource(R.drawable.girl1)
            binding.donebtn.visibility=View.VISIBLE
        }
        binding.girl2.setOnClickListener {
            profileValue="6"
            binding.profile.setImageResource(R.drawable.girl2)
            binding.donebtn.visibility=View.VISIBLE
        }
        binding.girl3.setOnClickListener {
            profileValue="7"
            binding.profile.setImageResource(R.drawable.girl3)
            binding.donebtn.visibility=View.VISIBLE
        }
        binding.girl4.setOnClickListener {
            profileValue="8"
            binding.profile.setImageResource(R.drawable.girl4)
            binding.donebtn.visibility=View.VISIBLE
        }


        binding.donebtn.setOnClickListener {
            val connectivityManager =
                signUp.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
            val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
            if (isConnected) {
                var loaddialog=Dialog(signUp)
                loaddialog.setContentView(R.layout.dialog_sign_loading)
                loaddialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                loaddialog.show()
                loaddialog.setCancelable(false)
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, pass)
                    .addOnCompleteListener {
                        val format = SimpleDateFormat("HH:mm", Locale.getDefault())
                        val userTime = format.format(Date())
                        var userDate: CharSequence = DateFormat.format("MMMM d,yyyy", Date().time)
                        if (it.isSuccessful) {
                            var user = FirebaseAuth.getInstance().currentUser
                            var id = user?.uid
                            var users = Users(
                                name,
                                encrypt(email),
                                id.toString(),
                                profileValue,
                                encrypt(pass),
                                "0",
                                passcode,birthdate,"",encrypt(userDate.toString()),encrypt(userTime.toString())
                            )
                            userRef.child(id.toString()).setValue(users).addOnCompleteListener {
                                if (it.isSuccessful) {
                                    editorInst.remove("tapTarget")
                                    editorInst.remove("tapTargetAudio")
                                    editorInst.remove("tapTargetProfile")
                                    editorInst.remove("tapTargetVideo")
                                    editorInst.apply()
                                    editorInst.commit()
                                  loaddialog.dismiss()
                                    var intent = Intent(signUp, HomeScreen::class.java)
                                    intent.putExtra("uid", id.toString())
                                    intent.putExtra("pass", pass.toString())
                                    startActivity(intent)
                                    signUp.finish()
                                }
                            }
                        } else {
                            Toast.makeText(signUp, it.exception.toString(), Toast.LENGTH_LONG)
                                .show()
                            loaddialog.dismiss()
                        }
                    }
            }
            else{
                Toast.makeText(signUp,"Check your internet connection please",Toast.LENGTH_LONG).show()
            }
        }
        return binding.root
    }


}
