package com.example.sosyalsaha.view

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.sosyalsaha.R
import com.example.sosyalsaha.databinding.ActivityProfileBinding
import com.example.sosyalsaha.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso


class ProfileActivity : AppCompatActivity() {

    private lateinit var binding : ActivityProfileBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var database : FirebaseFirestore

    private var alertDialogExit: AlertDialog? = null
    private var alertDialogDelete: AlertDialog? = null
    private var alertDialogImage : AlertDialog? = null

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPreferencesEditor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = Firebase.firestore

        sharedPreferences = getSharedPreferences("com.example.sosyalsaha",
            Context.MODE_PRIVATE)
        sharedPreferencesEditor = sharedPreferences.edit()

        with(binding){
            
            //nickname,profil resmi bilgilerini Firebase'den çekip ekranda gösterme
            database.collection("Users").addSnapshotListener{ value,error ->

                if(error != null){
                    Toast.makeText(this@ProfileActivity,error.localizedMessage, Toast.LENGTH_LONG)
                        .show()

                }else{
                    if(value != null){
                        if(!value.isEmpty){
                            val documents =  value.documents
                            for(document in documents){
                                val emailInfo = document.get("email") as String
                                if(emailInfo != null){
                                    if(emailInfo == auth.currentUser?.email.toString()){
                                        val downloadUrl = document.get("downloadUrl") as String
                                        val nickname = document.get("nickname") as String


                                        profileText.setText(nickname).toString()
                                        Picasso.get().load(downloadUrl).into(profilePictureButton)

                                        sharedPreferencesEditor.putString("alertImage",downloadUrl).apply()

                                    }
                                }
                            }

                        }
                    }

                }

            }


            //Yanıp sönen firebase logosu
            val blinkAnimation = AnimationUtils.loadAnimation(this@ProfileActivity,R.anim.animation)

            profileSplashImage.visibility = View.INVISIBLE

            profileExitText.setOnClickListener {
                //Çıkış yapma ekranı (exit_alert.xml)
                getCustomExitAlert()
            }

            profileManSearchButton.setOnClickListener {
                profileSplashImage.startAnimation(blinkAnimation)
                getPlayersActivity()
            }

            profileMyPitchButton.setOnClickListener {
                profileSplashImage.startAnimation(blinkAnimation)
                getPitchActivity()
            }

            profileDeleteButton.setOnClickListener {
                getCustomDeleteAlert()
            }

            profilePictureButton.setOnClickListener {
                getCustomImageAlert()
            }

        }

    }

    private fun getPlayersActivity(){
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this@ProfileActivity,PlayersActivity::class.java))
        },2250)
    }
    private fun getLoginActivity(){
        auth.signOut()
        startActivity(Intent(this@ProfileActivity,LoginActivity::class.java))
        finish()

        alertDialogExit = null
        alertDialogDelete = null
    }
    private fun getPitchActivity(){
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this@ProfileActivity,PitchActivity::class.java))
        },2250)
    }

    private fun getCustomExitAlert() {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.exit_alert, null)
        val yesButton = dialogLayout.findViewById<Button>(R.id.alertYesButton)
        val noButton = dialogLayout.findViewById<Button>(R.id.alertNoButton)


        //Evet butonu
        yesButton.setOnClickListener {
            alertDialogExit?.dismiss()
            getLoginActivity()
        }

        //Hayır butonu
        noButton.setOnClickListener {
            alertDialogExit?.dismiss()
            return@setOnClickListener
        }

        builder.setView(dialogLayout)
        alertDialogExit = builder.create()
        alertDialogExit?.show()
    }
    private fun getCustomDeleteAlert() {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.delete_alert, null)
        val deleteYesButton = dialogLayout.findViewById<Button>(R.id.deleteAlertYesButton)
        val deleteNoButton = dialogLayout.findViewById<Button>(R.id.deleteAlertNoButton)


        //Evet butonu
        deleteYesButton.setOnClickListener {
            alertDialogDelete?.dismiss()

            //Halihazırda giriş yapmış ve kayıtlı olan kişinin Firestore verilerinin authentication verileriyle beraber silinmesi

            //Firestore'dan silme

            //Kullanıcı'nın Kendi Verileri
            database.collection("Users")
                .whereEqualTo("email",auth.currentUser!!.email.toString())
                .get()
                .addOnSuccessListener { queryUsersSnapshot ->
                    for(document in queryUsersSnapshot.documents){
                        document.reference.delete()
                    }
                    //Kullanıcı'nın Kaydettiği Mekan Verileri
                    database.collection("Place")
                        .whereEqualTo("placeEmailInfo",auth.currentUser!!.email.toString())
                        .get()
                        .addOnSuccessListener {queryPlaceSnapshot ->
                            for(document in queryPlaceSnapshot.documents){
                                document.reference.delete()
                            }

                            //Authentication'dan silme
                            auth.currentUser?.delete()?.addOnCompleteListener {
                                    authDeleteTask ->
                                if(authDeleteTask.isSuccessful){
                                    auth.signOut()
                                    Toast.makeText(this@ProfileActivity,"Hesabınız Silindi",
                                        Toast.LENGTH_LONG).show()
                                    getLoginActivity()
                                }
                            }

                        }
                        .addOnFailureListener { exception ->
                            Toast.makeText(this@ProfileActivity, exception.localizedMessage, Toast.LENGTH_LONG).show()
                        }

                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this@ProfileActivity, exception.localizedMessage, Toast.LENGTH_LONG).show()
                }
        }

        //Hayır butonu
        deleteNoButton.setOnClickListener {
            alertDialogDelete?.dismiss()
            return@setOnClickListener
        }

        builder.setView(dialogLayout)
        alertDialogDelete = builder.create()
        alertDialogDelete?.show()
    }

    private fun getCustomImageAlert(){
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.image_alert, null)
        val image = dialogLayout.findViewById<ImageView>(R.id.alertImage)

        val sharedImage = sharedPreferences.getString("alertImage",null)
        println(sharedImage)
        Picasso.get().load(sharedImage).into(image)


        builder.setView(dialogLayout)
        alertDialogImage = builder.create()
        alertDialogImage?.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        alertDialogExit?.dismiss()
        alertDialogDelete?.dismiss()
        alertDialogImage?.dismiss()
    }

}


