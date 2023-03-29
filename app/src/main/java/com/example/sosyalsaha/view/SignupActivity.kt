package com.example.sosyalsaha.view

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.sosyalsaha.R
import com.example.sosyalsaha.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth

class SignupActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySignupBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPreferencesEditor: SharedPreferences.Editor
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        sharedPreferences = getSharedPreferences("com.example.sosyalsaha",
            Context.MODE_PRIVATE)
        sharedPreferencesEditor = sharedPreferences.edit()

        /* InfoFragment'da kullanıcı email-parola girişi yapıp,diğer girişleri yapmadan LoginActivity'e
        geri dönerse InfoFragment'daki veriler sıfırlanacak */
        sharedPreferences.edit().clear().apply()
        auth.currentUser?.delete()?.addOnCompleteListener {
                deleteTask ->
            if(deleteTask.isSuccessful){
                auth.signOut()
            }
        }


       val infoFragment = InfoFragment()


        with(binding){

            getFragment(infoFragment)

            //Backspace butonu işlemi
            signupText.setOnClickListener {
                getLoginActivity()
            }

        }


    }

    private fun getFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment).commit()
    }


    private fun getLoginActivity(){
        startActivity(Intent(this@SignupActivity,LoginActivity::class.java))
        finish()
    }

}