package com.example.sosyalsaha.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.sosyalsaha.R
import com.example.sosyalsaha.databinding.ActivityPlayersBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class PlayersActivity : AppCompatActivity() {


    private lateinit var binding : ActivityPlayersBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityPlayersBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val playerListFragment = PlayerlistFragment()
        getFragment(playerListFragment)

        with(binding){
            //Backspace butonu işlemi
            profileText.setOnClickListener {
                getProfileActivity()
            }
        }

    }

    private fun getProfileActivity(){
        startActivity(Intent(this@PlayersActivity,ProfileActivity::class.java))
        finish()
    }



    private fun getFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.framePlayerLayout, fragment).commit()
    }


}