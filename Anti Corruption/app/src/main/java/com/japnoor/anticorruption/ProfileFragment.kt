package com.japnoor.anticorruption

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.graphics.Typeface
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.util.Base64
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetView
import com.google.firebase.database.*
import com.japnoor.anticorruption.databinding.DialogSelectProfileBinding
import com.japnoor.anticorruption.databinding.FragmentProfileBinding
import com.japnoor.anticorruption.databinding.ProfileItemBinding
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import kotlin.collections.ArrayList

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ProfileFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    lateinit var homeScreen: HomeScreen

    var userName: String = ""
    var userEmail: String = ""
    var userProfile: String = ""
    var userPass: String = ""
    var userDate: String = ""
    var newdate: String = ""
    lateinit var compIdList: ArrayList<String>
    lateinit var demIdList: ArrayList<String>
    var profileValue: String = ""
    lateinit var sharedPreferences: SharedPreferences
    lateinit var editor: Editor
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

    lateinit var database: FirebaseDatabase
    lateinit var profileRef: DatabaseReference

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
        compIdList = ArrayList()
        demIdList = ArrayList()

        database = FirebaseDatabase.getInstance()
        profileRef = database.reference.child("Users")
        var binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        homeScreen = activity as HomeScreen
        arguments?.let {
            compIdList = it.getStringArrayList("compids") as ArrayList<String>
            demIdList = it.getStringArrayList("demids") as ArrayList<String>
        }

        sharedPreferences = homeScreen.getSharedPreferences("instructions", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()

        if(!(sharedPreferences.contains("tapTargetProfile"))) {
            TapTargetView.showFor(homeScreen,
                TapTarget.forView(
                    binding.Profile,
                    "Profile",
                    "Click here to change your Profile"
                )
                    .outerCircleColor(R.color.accepted)
                    .outerCircleAlpha(0.96f)
                    .targetCircleColor(R.color.blue)
                    .titleTextSize(20)
                    .titleTextColor(R.color.blue)
                    .descriptionTextSize(10)
                    .descriptionTextColor(R.color.blue)
                    .textColor(R.color.white)
                    .textTypeface(Typeface.SANS_SERIF)
                    .dimColor(R.color.blue)
                    .drawShadow(true)
                    .cancelable(false)
                    .tintTarget(true)
                    .titleTextSize(20)
                    .transparentTarget(true)
                    .targetRadius(60),
                object : TapTargetView.Listener() {
                    override fun onTargetClick(view: TapTargetView) {
                        super.onTargetClick(view)
                        TapTargetView.showFor(homeScreen,
                            TapTarget.forView(
                                binding.btnName,
                                "Edit Profile",
                                "Click on these buttons to change your Name,Email,Birth date and Password"
                            )
                                .outerCircleColor(R.color.accepted)
                                .outerCircleAlpha(0.96f)
                                .targetCircleColor(R.color.blue)
                                .titleTextSize(20)
                                .titleTextColor(R.color.blue)
                                .descriptionTextSize(10)
                                .descriptionTextColor(R.color.blue)
                                .textColor(R.color.white)
                                .textTypeface(Typeface.SANS_SERIF)
                                .dimColor(R.color.blue)
                                .drawShadow(true)
                                .cancelable(false)
                                .tintTarget(true)
                                .transparentTarget(true)
                                .targetRadius(60),
                            object : TapTargetView.Listener() {
                                override fun onTargetClick(view: TapTargetView) {
                                    super.onTargetClick(view)
                                    editor.putString("tapTargetProfile", "1")
                                    editor.apply()
                                    editor.commit()
                                }
                            })
                    }
                })
        }

        binding.shimmer.startShimmer()

        profileRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (user in snapshot.children) {
                    var info = user.getValue(Users::class.java)
                    if (info != null && info.userId.equals(homeScreen.id)) {
                        userName = decrypt(info.name)
                        userEmail = decrypt(info.email)
                        userProfile = info.profileValue
                        userPass = decrypt(info.password)
                        userDate = decrypt(info.birthdate)
                        println("Yeh " + userName)
                        println("Yeh " + userEmail)
                        binding.name.text = userName
                        binding.email.text = userEmail
                        binding.pass.text = userPass
                        binding.birthdate.text = userDate
                        when (userProfile) {
                            "1" -> binding.Profile.setImageResource(R.drawable.man1)
                            "2" -> binding.Profile.setImageResource(R.drawable.man2)
                            "3" -> binding.Profile.setImageResource(R.drawable.man3)
                            "4" -> binding.Profile.setImageResource(R.drawable.man4)
                            "5" -> binding.Profile.setImageResource(R.drawable.girl1)
                            "6" -> binding.Profile.setImageResource(R.drawable.girl2)
                            "7" -> binding.Profile.setImageResource(R.drawable.girl3)
                            "8" -> binding.Profile.setImageResource(R.drawable.girl4)
                        }
                        binding.shimmer.stopShimmer()
                        binding.shimmer.visibility = View.GONE
                        binding.scroll.visibility = View.VISIBLE

                    }

                }

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        binding.btndate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val currentYear = calendar.get(Calendar.YEAR)
            val currentMonth = calendar.get(Calendar.MONTH) + 1
            val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                homeScreen,
                DatePickerDialog.OnDateSetListener { _, year, month, day ->
                    val userBirthYear = year
                    val userBirthMonth = month + 1
                    val userBirthDay = day

                    var age = currentYear - userBirthYear
                    if (userBirthMonth > currentMonth || (userBirthMonth == currentMonth && userBirthDay > currentDay)) {
                        age--
                    }
                    if (age >= 18) {
                        binding.birthdate.text = "$userBirthDay/$userBirthMonth/$userBirthYear"
                        newdate = "$userBirthDay/$userBirthMonth/$userBirthYear"
                        val connectivityManager =
                            homeScreen.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
                        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
                        if (isConnected) {
                            profileRef.child(homeScreen.id).child("birthdate").setValue(encrypt(newdate))
                        } else {
                            Toast.makeText(
                                homeScreen,
                                "Check you internet connection please",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            homeScreen,
                            "At least 18 years of age is required",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }, 2004,
                0,
                1
            )
            datePickerDialog.show()
        }


        binding.Profile.setOnClickListener {
            var dialog = Dialog(homeScreen)
            var dialogBinding = DialogSelectProfileBinding.inflate(layoutInflater)
            dialog.setContentView(dialogBinding.root)
            dialog.window?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
            when (userProfile) {
                "1" -> dialogBinding.profile.setImageResource(R.drawable.man1)
                "2" -> dialogBinding.profile.setImageResource(R.drawable.man2)
                "3" -> dialogBinding.profile.setImageResource(R.drawable.man3)
                "4" -> dialogBinding.profile.setImageResource(R.drawable.man4)
                "5" -> dialogBinding.profile.setImageResource(R.drawable.girl1)
                "6" -> dialogBinding.profile.setImageResource(R.drawable.girl2)
                "7" -> dialogBinding.profile.setImageResource(R.drawable.girl3)
                "8" -> dialogBinding.profile.setImageResource(R.drawable.girl4)
            }
            dialogBinding.man1.setOnClickListener {
                profileValue = "1"
                dialogBinding.profile.setImageResource(R.drawable.man1)
                dialogBinding.donebtn.visibility = View.VISIBLE
            }
            dialogBinding.man2.setOnClickListener {
                profileValue = "2"
                dialogBinding.profile.setImageResource(R.drawable.man2)
                dialogBinding.donebtn.visibility = View.VISIBLE
            }
            dialogBinding.man3.setOnClickListener {
                profileValue = "3"
                dialogBinding.profile.setImageResource(R.drawable.man3)
                dialogBinding.donebtn.visibility = View.VISIBLE
            }
            dialogBinding.man4.setOnClickListener {
                profileValue = "4"
                dialogBinding.profile.setImageResource(R.drawable.man4)
                dialogBinding.donebtn.visibility = View.VISIBLE
            }

            dialogBinding.girl1.setOnClickListener {
                profileValue = "5"
                dialogBinding.profile.setImageResource(R.drawable.girl1)
                dialogBinding.donebtn.visibility = View.VISIBLE
            }
            dialogBinding.girl2.setOnClickListener {
                profileValue = "6"
                dialogBinding.profile.setImageResource(R.drawable.girl2)
                dialogBinding.donebtn.visibility = View.VISIBLE
            }
            dialogBinding.girl3.setOnClickListener {
                profileValue = "7"
                dialogBinding.profile.setImageResource(R.drawable.girl3)
                dialogBinding.donebtn.visibility = View.VISIBLE
            }
            dialogBinding.girl4.setOnClickListener {
                profileValue = "8"
                dialogBinding.profile.setImageResource(R.drawable.girl4)
                dialogBinding.donebtn.visibility = View.VISIBLE
            }
            val connectivityManager =
                homeScreen.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
            val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
            if (isConnected) {
                dialogBinding.donebtn.setOnClickListener {
                    profileRef.child(homeScreen.id).child("profileValue").setValue(profileValue)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                dialog.dismiss()
                            }
                        }
                }
                dialog.show()
            } else {
                Toast.makeText(
                    homeScreen,
                    "Check you internet connection please",
                    Toast.LENGTH_LONG
                ).show()

            }
        }



        binding.btnName.setOnClickListener {
            var dialog = Dialog(requireContext())
            var dialogBinding = ProfileItemBinding.inflate(layoutInflater)
            dialog.setContentView(dialogBinding.root)
            dialog.window?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            dialogBinding.et.setText(userName)
            dialogBinding.fab.setOnClickListener {
                if (dialogBinding.et.text.toString().equals(userName)) {
                    dialog.dismiss()
                } else {
                    val input: String = dialogBinding.et.getText().toString().trim()
                    if (dialogBinding.et.text.isNullOrEmpty()) {
                        dialogBinding.et.error = "Cannot be empty!"
                    } else if (input.length == 0) {
                        dialogBinding.et.requestFocus()
                        dialogBinding.et.error = "Enter Some characters "
                    } else {

                        val connectivityManager =
                            homeScreen.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
                        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
                        if (isConnected) {
                            profileRef.child(homeScreen.id).child("name")
                                .setValue(encrypt(dialogBinding.et.text.toString()))
                            dialog.dismiss()
                        } else {
                            Toast.makeText(
                                homeScreen,
                                "Check you internet connection please",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
            dialog.show()
        }
        binding.btnEmail.setOnClickListener {
            var intent = Intent(homeScreen, EmailChangeActivity::class.java)
            intent.putExtra("email", userEmail)
            intent.putExtra("pass", userPass)
            intent.putExtra("id", homeScreen.id)
            startActivity(intent)
        }

        binding.btnPass.setOnClickListener {
            var intent = Intent(homeScreen, ForgotPassword::class.java)
            startActivity(intent)
            homeScreen.finish()
        }
        return binding.root
    }
}
