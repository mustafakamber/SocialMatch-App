package com.example.sosyalsaha.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import com.example.sosyalsaha.R
import com.example.sosyalsaha.databinding.ActivitySplashBinding
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {
    private lateinit var binding :  ActivitySplashBinding
    private lateinit var auth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()


        //Yanıp sönen uygulama logosu
        val blinkAnimation = AnimationUtils.loadAnimation(this@SplashActivity,R.anim.animation)
        binding.splashImage.startAnimation(blinkAnimation)


        /* Halihazırda giriş yapmış kullanıcı varsa Profil Aktivitesine yönlendirilecek
          yoksa Login Aktivitesine yönlendirilecek  */

        val currentUser = auth.currentUser
        if(currentUser != null){
            getProfileActivity()
        }else{
            getLoginActivity()
        }



    }


    private fun getProfileActivity(){
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this@SplashActivity, ProfileActivity::class.java))
            finish()
        },2750)
    }


    private fun getLoginActivity(){
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
            finish()
        },2750)
    }
}