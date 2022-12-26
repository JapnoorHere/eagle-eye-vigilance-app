package com.japnoor.anticorruption

import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import com.japnoor.anticorruption.admin.AdminHomeScreen
import com.japnoor.anticorruption.databinding.ActivitySplashScreenBinding

class Splashscreen : AppCompatActivity() {

    var signUpEntity : SignUpEntity ? =null
    lateinit var binding : ActivitySplashScreenBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getStatus()

        var downAnim = AnimationUtils.loadAnimation(this, R.anim.down_anim)

        binding.ivMAimg.animation = downAnim

        Handler(Looper.getMainLooper()).postDelayed({

            if(signUpEntity?.status=="1" && signUpEntity?.category.equals("u")){
                var intent= Intent(this@Splashscreen, HomeScreen::class.java)
                intent.putExtra("userInfo", signUpEntity)
                startActivity(intent)
                finish()
            }

            else if(signUpEntity?.status=="1" && signUpEntity?.category.equals("a")){
                var intent= Intent(this@Splashscreen, AdminHomeScreen::class.java)
                intent.putExtra("userInfo", signUpEntity)
                startActivity(intent)
                finish()
            }
            else{
                var intent=Intent(this,MainActivity::class.java)
                startActivity(intent)
                finish()
            }

        }, 2500)

    }

    fun getStatus(){
        class Add : AsyncTask<Void, Void, Void>() {
            override fun doInBackground(vararg p0: Void?): Void? {
                signUpEntity=Datbase.getDatabase(this@Splashscreen).dao().getStatus()
                return null
            }

            override fun onPostExecute(result: Void?) {
                super.onPostExecute(result)
            }
        }
        Add().execute()
    }
}