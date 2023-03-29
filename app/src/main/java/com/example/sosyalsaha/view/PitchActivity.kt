package com.example.sosyalsaha.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.sosyalsaha.R
import com.example.sosyalsaha.databinding.ActivityPitchBinding

class PitchActivity : AppCompatActivity() {


    private lateinit var binding: ActivityPitchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityPitchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pitchlistFragment = PitchlistFragment()

        with(binding){

            getFragment(pitchlistFragment)

            //Backspace butonu işlemi
            pitchText.setOnClickListener {
                getProfileActivity()
            }

            pitchOptionText.setOnClickListener {
                //Saha ekle butonu işlemleri
                val data = Bundle()
                data.putString("info","new")
                val pitchMapFragment = PitchmapsFragment()
                pitchMapFragment.arguments = data
                getFragment(pitchMapFragment)

            }

        }


    }

    private fun getFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.framePitchLayout, fragment).commit()
    }
    private fun getProfileActivity(){
        startActivity(Intent(this@PitchActivity,ProfileActivity::class.java))
        finish()
    }
}