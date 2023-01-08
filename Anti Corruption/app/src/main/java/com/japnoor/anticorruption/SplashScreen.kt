package com.japnoor.anticorruption

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import com.japnoor.anticorruption.databinding.ActivitySplashScreenBinding

class SplashScreen : AppCompatActivity() {

    lateinit var binding : ActivitySplashScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var downAnim = AnimationUtils.loadAnimation(this, R.anim.down_anim)

        binding.ivMAimg.animation = downAnim

        Handler(Looper.getMainLooper()).postDelayed({


                var intent=Intent(this,LoginActivity::class.java)
                startActivity(intent)
                finish()


        }, 2500)

    }

}