package com.japnoor.anticorruption

import com.japnoor.anticorruption.R
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.icu.util.Calendar
import android.media.MediaRecorder
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.japnoor.anticorruption.databinding.ActivityHomeScreenBinding
import com.japnoor.anticorruption.databinding.BlockedUserDialogBinding
import com.japnoor.anticorruption.databinding.FragmentAudioRecordBinding
import com.japnoor.anticorruption.databinding.PasscodeDialogBinding
import com.japnoor.anticorruption.databinding.ShakedialogBinding
import java.io.File
import java.lang.Math.sqrt
import java.util.*


class HomeScreen : AppCompatActivity() {
    companion object {
        var REQUEST_RECORD_AUDIO_PERMISSION = 200
    }

    lateinit var binding: ActivityHomeScreenBinding
    lateinit var navController: NavController
    var id: String = ""
    lateinit var toggle: ActionBarDrawerToggle
    var pass: String = ""
    lateinit var sensorDialog: Dialog
    lateinit var database: FirebaseDatabase
    lateinit var useref: DatabaseReference
    private var sensorManager: SensorManager? = null
    private var acceleration = 0f
    var isRecording = false
    lateinit var profileBundle: Bundle
    lateinit var compProfileList : ArrayList<String>
    lateinit var demProfileList : ArrayList<String>
    private var currentAcceleration = 0f
    private var lastAcceleration = 0f
    lateinit var filee: File
    lateinit var mediaRecorder: MediaRecorder
    var sense = true
    var userSensor = ""

    var isSensorActive = true


    private val sensorListener: SensorEventListener = object : SensorEventListener {

        fun startRecording() {
            val currentTime = Date().time
            filee = File(
                this@HomeScreen.getExternalFilesDir(Environment.DIRECTORY_MUSIC),
                "Audio $currentTime.mp3"
            )
            mediaRecorder = MediaRecorder()
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            mediaRecorder.setOutputFile(filee.absolutePath)
            mediaRecorder.prepare()
            mediaRecorder.start()
            isRecording = true
        }


        override fun onSensorChanged(event: SensorEvent) {
            FirebaseDatabase.getInstance().reference.child("Users")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (sensor in snapshot.children) {
                            var sensecheckid = intent.getStringExtra("uid").toString()
                            var sensestatus = sensor.getValue(Users::class.java)
                            if (sensestatus != null && sensestatus.userId.equals(sensecheckid) && sensestatus.userSensor.equals(
                                    "1"
                                )
                            ) {
                                userSensor = "1"
                                if (sense && userSensor.equals("1") && isSensorActive) {
                                    val x = event.values[0]
                                    val y = event.values[1]
                                    val z = event.values[2]
                                    lastAcceleration = currentAcceleration
                                    currentAcceleration =
                                        sqrt((x * x + y * y + z * z).toDouble()).toFloat()
                                    val delta: Float = currentAcceleration - lastAcceleration
                                    acceleration = acceleration * 0.9f + delta
                                    if (acceleration > 17) {
                                        var dialog = Dialog(this@HomeScreen)
                                        var dialogB =
                                            FragmentAudioRecordBinding.inflate(layoutInflater)
                                        dialog.setContentView(dialogB.root)
                                        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                                        if (!isRecording) {
                                            dialogB.audiorecord.visibility = View.GONE
                                            dialogB.lottie.visibility = View.VISIBLE
                                            dialogB.lottie.playAnimation()
                                            startRecording()

                                            dialogB.tv.text = "Recording has started"
//                        dialogB.audiorecord.setImageResource(R.drawable.audiostart)
                                            dialog.setCancelable(false)
                                            dialog.show()
                                        }

                                        dialogB.lottie.setOnClickListener {
                                            dialogB.tv.text = "Recording is stopped"
                                            mediaRecorder.stop()
                                            dialogB.audiorecord.visibility = View.VISIBLE
                                            dialogB.lottie.visibility = View.GONE
                                            mediaRecorder.reset()
                                            mediaRecorder.release()
                                            dialog.dismiss()
                                            dialog.setCancelable(true)
                                            navController.navigate(R.id.audiorecordingListFragment)
//                        dialogB.audiorecord.setImageResource(R.drawable.audiostop)
                                            isRecording = false
                                        }
                                    }
                                }

                            } else {
                                userSensor = "0"
                            }
                        }

                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })


        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
    }

    override fun onResume() {
        sensorManager?.registerListener(
            sensorListener, sensorManager!!.getDefaultSensor(
                Sensor.TYPE_ACCELEROMETER
            ), SensorManager.SENSOR_DELAY_NORMAL
        )
        super.onResume()
    }

    override fun onPause() {
        sensorManager!!.unregisterListener(sensorListener)
        super.onPause()
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        profileBundle=Bundle()
        compProfileList= ArrayList()
        demProfileList=ArrayList()
        navController = findNavController(R.id.navController)

        id = intent.getStringExtra("uid").toString()
        pass = intent.getStringExtra("pass").toString()


        database = FirebaseDatabase.getInstance()
        useref = database.reference.child("Users")
        println("password->$pass")
        println("password->$id")


        val headerView = binding.navView.getHeaderView(0)
        val name = headerView.findViewById<TextView>(R.id.namee)
        val email = headerView.findViewById<TextView>(R.id.emaill)
        val profile = headerView.findViewById<ImageView>(R.id.profilee)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        Objects.requireNonNull(sensorManager)?.registerListener(
            sensorListener, sensorManager!!
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL
        )
        acceleration = 10f
        currentAcceleration = SensorManager.GRAVITY_EARTH
        lastAcceleration = SensorManager.GRAVITY_EARTH


        FirebaseDatabase.getInstance().reference.child("Complaints").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(each in snapshot.children){
                    var compdetail=each.getValue(Complaints::class.java)
                    if (compdetail != null) {
                        if(compdetail.equals(id)){
                            compProfileList.add(compdetail.complaintId.toString())
                        }
                    }
                }
                profileBundle.putStringArrayList("compids",compProfileList)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        FirebaseDatabase.getInstance().reference.child("Demand Letter").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(each in snapshot.children){
                    var demdetail=each.getValue(DemandLetter::class.java)
                    if (demdetail != null) {
                        if(demdetail.equals(id)){
                            demProfileList.add(demdetail.demandId.toString())
                        }
                    }
                }
                profileBundle.putStringArrayList("demids",demProfileList)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        useref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var dialog = Dialog(this@HomeScreen)
                for (each in snapshot.children) {
                    var userr = each.getValue(Users::class.java)
                    if (userr != null && userr.userId.equals(id) && userr.userStatus!="0"
                    ) {
                        var dialogB = BlockedUserDialogBinding.inflate(layoutInflater)
                        dialog.setContentView(dialogB.root)
                        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                        dialog.setCancelable(false)
                        val currentTime = System.currentTimeMillis()
                        //                    val sevenDaysInMillisecond = 7 * 24 * 60 * 60 * 1000
                        val sevenDaysInMillisecond : Long = 604800000
                        var timecheck : Long=0
                        useref.addValueEventListener(object  : ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {
                                for(each in snapshot.children){
                                    var userdetail=each.getValue(Users::class.java)
                                    if(userdetail!=null && userdetail.userId.equals(id) && userdetail.userStatus!="0"){
                                         timecheck=userdetail.userStatus.toLong() + sevenDaysInMillisecond
                                        println("Time->" + timecheck)
                                        if (currentTime >= timecheck) {
                                            useref.child(id).child("userStatus").setValue("0")
                                            dialog.dismiss()
                                        }
                                    }
                                }

                                var timeleft=timecheck-currentTime
                                val days = timeleft / (1000 * 60 * 60 * 24)
                                val hours = (timeleft / (1000 * 60 * 60)) % 24
                                dialogB.timeMsg.text = days.toString() + "d " + hours.toString() +"h"
                            }

                            override fun onCancelled(error: DatabaseError) {

                            }

                        })
                        dialog.show()

                        dialogB.ok.setOnClickListener {
                            dialog.dismiss()
                            FirebaseAuth.getInstance().signOut()
                            var intent = Intent(this@HomeScreen, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        }

                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        useref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                for (fetchuser in snapshot.children) {
                    var fetchuser1 = fetchuser.getValue(Users::class.java)
                    if (fetchuser1 != null && fetchuser1.userId.equals(id)) {
                        name.text = fetchuser1.name
                        email.text = fetchuser1.email
                        when (fetchuser1.profileValue) {
                            "1" -> profile.setImageResource(R.drawable.man1)
                            "2" -> profile.setImageResource(R.drawable.man2)
                            "3" -> profile.setImageResource(R.drawable.man3)
                            "4" -> profile.setImageResource(R.drawable.man4)
                            "5" -> profile.setImageResource(R.drawable.girl1)
                            "6" -> profile.setImageResource(R.drawable.girl2)
                            "7" -> profile.setImageResource(R.drawable.girl3)
                            "8" -> profile.setImageResource(R.drawable.girl4)
                        }

                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        binding.apply {
            toggle =
                ActionBarDrawerToggle(this@HomeScreen, drawerLayout, R.string.open, R.string.close)
            drawerLayout.addDrawerListener(toggle)
            toggle.syncState()
            sidebar.setOnClickListener {
                drawerLayout.open()
            }
            supportActionBar?.setDisplayHomeAsUpEnabled(true)

            profile.setOnClickListener {
                navController.navigate(R.id.profileFragment,profileBundle)
                drawerLayout.close()
            }

            navView.setNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.logoutt -> {
                        drawerLayout.close()
                        val builder = AlertDialog.Builder(this@HomeScreen)
                        builder.setTitle("Logout")
                        builder.setMessage("Are you sure you want to logout?")
                        builder.setPositiveButton("Yes") { dialog, which ->
                            var intent = Intent(this@HomeScreen, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                            FirebaseAuth.getInstance().signOut()
                            Toast.makeText(this@HomeScreen, "Logout Successful", Toast.LENGTH_LONG)
                                .show()
                        }
                        builder.setNegativeButton("No") { dialog, which ->
                            dialog.dismiss()
                        }
                        builder.show()
                    }


                    R.id.sensorShake -> {
                        sensorDialog = Dialog(this@HomeScreen)
                        var dialogB = ShakedialogBinding.inflate(layoutInflater)
                        sensorDialog.setContentView(dialogB.root)
                        drawerLayout.close()
                        sensorDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                        useref.addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                for (eachuserr in snapshot.children) {
                                    var eachuser = eachuserr.getValue(Users::class.java)
                                    if (eachuser != null && eachuser.userId.equals(id)) {
                                        dialogB.onswitch.isChecked = eachuser.userSensor.equals("1")
                                    }
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }

                        })
                        dialogB.lottie.setMinAndMaxFrame(10, 35)
                        dialogB.onswitch.setOnCheckedChangeListener { _, isChecked ->

                            if (isChecked) {
                                useref.child(id).child("userSensor").setValue("1")
                            } else {
                                useref.child(id).child("userSensor").setValue("0")
                            }
                        }
                        if (ContextCompat.checkSelfPermission(
                                this@HomeScreen,
                                android.Manifest.permission.RECORD_AUDIO
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            ActivityCompat.requestPermissions(
                                this@HomeScreen, arrayOf(android.Manifest.permission.RECORD_AUDIO),
                                REQUEST_RECORD_AUDIO_PERMISSION
                            )
                        } else {
                            sensorDialog.show()

                        }
                    }

                    R.id.Homeee -> {
                        navController.navigate(R.id.homeFragment)
                        drawerLayout.close()
                    }

                    R.id.passcodee -> {
                        drawerLayout.close()
                        println(pass.toString())
                        var dialog = Dialog(this@HomeScreen)
                        var dialogBinding = PasscodeDialogBinding.inflate(layoutInflater)
                        dialog.setContentView(dialogBinding.root)
                        dialog.window?.setLayout(
                            WindowManager.LayoutParams.MATCH_PARENT,
                            WindowManager.LayoutParams.WRAP_CONTENT
                        )
                        dialogBinding.etPasswordLayout1.visibility = View.GONE
                        dialogBinding.etPasswordLayout2.visibility = View.GONE
                        dialogBinding.btnSignup.visibility = View.GONE
                        dialogBinding.passw.visibility = View.VISIBLE
                        dialogBinding.btn.visibility = View.VISIBLE
                        dialogBinding.tv.text = "Enter the 6 digit password which you entered while making your account"
                        dialogBinding.btn.setOnClickListener {
                            if (dialogBinding.etpass.text.toString().isNullOrEmpty()) {
                                dialogBinding.etpass.error = "Enter Password"
                                dialogBinding.etpass.requestFocus()
                            } else if (dialogBinding.etpass.text.toString().equals(pass)) {
                                val connectivityManager =
                                    getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                                val activeNetwork: NetworkInfo? =
                                    connectivityManager.activeNetworkInfo
                                val isConnected: Boolean =
                                    activeNetwork?.isConnectedOrConnecting == true
                                if (isConnected) {
                                    dialogBinding.tv.text = "Enter the 5 digit passcode which will help \n to keep your app safe"
                                    dialogBinding.etPasswordLayout1.visibility = View.VISIBLE
                                    dialogBinding.etPasswordLayout2.visibility = View.VISIBLE
                                    dialogBinding.btnSignup.visibility = View.VISIBLE
                                    dialogBinding.passw.visibility = View.GONE
                                    dialogBinding.btn.visibility = View.GONE
                                } else {
                                    Toast.makeText(
                                        this@HomeScreen,
                                        "Check you internet connection please",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            } else {
                                Toast.makeText(this@HomeScreen, "Wrong Password", Toast.LENGTH_LONG)
                                    .show()
                            }
                        }


                        dialogBinding.btnSignup.setOnClickListener {
                            if (dialogBinding.etPassword.text.toString().isNullOrEmpty()) {
                                dialogBinding.etPassword.error = "Enter Passcode"
                                dialogBinding.etPassword.requestFocus()
                            } else if (dialogBinding.etPassword.text.toString().length < 5) {
                                dialogBinding.etPassword.error =
                                    "Passcode must be of at least 5 characters"
                                dialogBinding.etPassword.requestFocus()
                            } else if (dialogBinding.etPassword.text.toString().length > 5) {
                                dialogBinding.etPassword.error =
                                    "Passcode must be of 5 characters only "
                                dialogBinding.etPassword.requestFocus()
                            } else if (dialogBinding.etREPassword.text.toString().isNullOrEmpty()) {
                                dialogBinding.etREPassword.error = "Enter Passcode again"
                                dialogBinding.etREPassword.requestFocus()
                            } else if ((!dialogBinding.etPassword.text.toString()
                                    .equals(dialogBinding.etREPassword.text.toString()))
                            ) {
                                dialogBinding.etREPassword.error = "Passcode must be same"
                                dialogBinding.etREPassword.requestFocus()
                            } else {
                                dialogBinding.btnSignup.visibility = View.GONE
                                dialogBinding.progressbar.visibility = View.VISIBLE
                                useref.addValueEventListener(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        for (eachUser in snapshot.children) {
                                            var userr = eachUser.getValue(Users::class.java)
                                            if (userr != null && userr.userId.equals(id)) {
                                                useref.child(id).child("passcode")
                                                    .setValue(dialogBinding.etPassword.text.toString())
                                                    .addOnCompleteListener {
                                                        if (it.isSuccessful) {
                                                            dialogBinding.btnSignup.visibility =
                                                                View.VISIBLE
                                                            dialogBinding.progressbar.visibility =
                                                                View.GONE
                                                            dialog.dismiss()
                                                        } else {
                                                            dialogBinding.btnSignup.visibility =
                                                                View.VISIBLE
                                                            dialogBinding.progressbar.visibility =
                                                                View.GONE
                                                            Toast.makeText(
                                                                this@HomeScreen,
                                                                it.exception.toString(),
                                                                Toast.LENGTH_LONG
                                                            ).show()
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

                    R.id.report -> {
                        drawerLayout.close()
                        val builder = AlertDialog.Builder(this@HomeScreen)
                        builder.setTitle("Report a Problem")
                        builder.setMessage("Dear valued user, we hope you're enjoying our Android app. We sincerely appreciate your taking the time to let us know if you run into any bugs or problems by sending an email to us.")
                        builder.setPositiveButton("Report") { dialog, which ->
                            val email = "anticorruptionpunjab75@gmail.com"
                            val emailIntent = Intent(Intent.ACTION_SENDTO)
                            emailIntent.data = Uri.parse("mailto:$email")
                            startActivity(emailIntent)
                        }
                        builder.setNegativeButton("No") { dialog, which ->
                            dialog.dismiss()
                        }
                        builder.show()
                    }

                    R.id.credit -> {
                        var dialog = Dialog(this@HomeScreen)
                        dialog.setContentView(R.layout.credit_dialog)
                        dialog.window?.setLayout(
                            WindowManager.LayoutParams.MATCH_PARENT,
                            WindowManager.LayoutParams.MATCH_PARENT
                        )
                        dialog.show()
                    }

                }
                true
            }

        }
//        println("AAH reha -> "+ id.toString())

        binding.BottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.bnHome -> {
                    navController.navigate(R.id.homeFragment)
                    this.title = "Home"

                }
                R.id.bnAddComplaint -> {
                    navController.navigate(R.id.addComplaintFragment)
                    this.title = "Add Complaint"
                }
                R.id.bnDemandLetter -> {
                    navController.navigate(R.id.addDemandLetterFragment)
                    this.title = "Demand Letter"

                }
                R.id.audio -> {
                    navController.navigate(R.id.audiorecordingListFragment)
                    this.title = "Audio Recordings"

                }
                R.id.video -> {
                    navController.navigate(R.id.videoRecordingList)
                    this.title = "Video Recordings"

                }
                else -> {}
            }
            return@setOnItemSelectedListener true
        }

    }



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sensorDialog
            }
        }
    }

}