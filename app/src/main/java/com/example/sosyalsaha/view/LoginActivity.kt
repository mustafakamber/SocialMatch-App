package com.example.sosyalsaha.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.sosyalsaha.R
import com.example.sosyalsaha.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var auth : FirebaseAuth
    private lateinit var binding : ActivityLoginBinding

    private var alertDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()


        with(binding){
            loginButton.setOnClickListener {
                //Boş email ve şifre girişi kontrolü
                if(TextUtils.isEmpty(loginEmailText.text.toString())){
                    loginEmailText.error = "E-mailinizi giriniz"
                }
                else if(TextUtils.isEmpty(loginPasswordText.text.toString())){
                    loginPasswordText.error = "Şifrenizi giriniz"
                }
                else{
                    //Giriş yap butonu işlemleri
                    auth.signInWithEmailAndPassword(loginEmailText.text.toString(),loginPasswordText.text.toString())
                        .addOnSuccessListener {
                            //Kullanıcı girişi başarılı,Profil Aktivitesine yönlendirilecek
                            getProfileActivity()
                        }
                        .addOnFailureListener {
                            //Kullanıcı girişi başarısız, Hata mesajı gösterilecek
                            Toast.makeText(this@LoginActivity,it.localizedMessage,Toast.LENGTH_LONG)
                                .show()
                        }
                }

            }


            //Kayıt ol butonu işlemleri
            loginSignupButton.setOnClickListener {
                //Kayıt olma aktivitesi
                getSignupActivity()
            }

            //Parolamı unuttum işlemleri
            loginForgetPasswordText.setOnClickListener {
                //Şifre sıfırlama ekranı (reset_alert.xml)
                getCustomAlert()
            }

        }



    }

    private fun getProfileActivity(){
        startActivity(Intent(this@LoginActivity,ProfileActivity::class.java))
        finish()
    }
    private fun getCustomAlert(){
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.reset_alert, null)
        val emailText = dialogLayout.findViewById<EditText>(R.id.resetEmailText)
        val sendButton = dialogLayout.findViewById<Button>(R.id.resetSendButton)
        val backButton = dialogLayout.findViewById<Button>(R.id.resetBackButton)
        val infoText = dialogLayout.findViewById<TextView>(R.id.resetInfoText)

        backButton.visibility = View.GONE

        //Gönder butonu
        sendButton.setOnClickListener {
            //Boş email girişi kontrolü
            if(TextUtils.isEmpty(emailText.text.toString().trim())){
                emailText.error = "E-mail adresinizi girin"
            }
            else{
                auth.sendPasswordResetEmail(emailText.text.toString().trim())
                    .addOnCompleteListener {
                        if(it.isSuccessful){
                            infoText.text = "E-mail adresinize sıfırlama bağlantısı gönderildi"
                            sendButton.visibility = View.GONE
                            backButton.visibility = View.VISIBLE

                            //Geri Dön Butonu
                            backButton.setOnClickListener {
                                alertDialog?.dismiss()
                                alertDialog = null
                                return@setOnClickListener

                            }
                        }
                        else{
                            Toast.makeText(this,it.exception.toString(),Toast.LENGTH_LONG)
                                .show()
                        }
                    }

            }
        }
        builder.setView(dialogLayout)
        alertDialog = builder.create()
        alertDialog?.show()
    }
    private fun getSignupActivity(){
        startActivity(Intent(this@LoginActivity,SignupActivity::class.java))
    }

    override fun onDestroy() {
        super.onDestroy()
        alertDialog?.dismiss()
    }
}