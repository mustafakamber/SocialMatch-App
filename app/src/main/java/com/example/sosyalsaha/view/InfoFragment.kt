package com.example.sosyalsaha.view

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.edit
import com.example.sosyalsaha.R

import com.example.sosyalsaha.databinding.FragmentInfoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.util.*


@Suppress("DEPRECATION")
class InfoFragment : Fragment() {


    private lateinit var binding : FragmentInfoBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var database : FirebaseFirestore
    private lateinit var storage : FirebaseStorage

    private var imageUri : Uri? = null

    private var cityListClicked : Boolean? = null
    private var positionListClicked : Boolean? = null
    private var imageSelected : Boolean? = null
    private var registered : Boolean? = null

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPreferencesEditor: SharedPreferences.Editor



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()

    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?{
        binding = FragmentInfoBinding.inflate(layoutInflater)

        sharedPreferences = requireActivity().getSharedPreferences("com.example.sosyalsaha",Context.MODE_PRIVATE)
        sharedPreferencesEditor = sharedPreferences.edit()



        return binding.root

    }
    override fun onViewCreated(view : View,savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)


        getSharedData()
        getSharedBoolean()

        with(binding){


            if(registered == false){
                getInvisibleItems()
            }else{
                getVisibleItems()
                signupSaveButton.visibility = View.INVISIBLE
            }


            if(imageSelected == true){
                val rumble = arguments
                if(rumble != null){
                    imageUri = arguments?.getParcelable("resimUri")
                    signupProfileImageButton.setText(imageUri.toString()).toString()
                }
                imageSelected = false
                putSharedBoolean("imageSelected",imageSelected!!)
            }

            if(cityListClicked == true){
                val bundle = arguments
                if(bundle != null){
                    val cityData = bundle.getString("city")
                    signupCityButton.setText(cityData).toString()
                }
                cityListClicked = false
                putSharedBoolean("cityListClicked",cityListClicked!!)
            }

            if(positionListClicked == true){
                val jungle = arguments
                if(jungle != null){
                    var positionData = jungle.getStringArrayList("positions").toString()
                    positionData = positionData.replace("[","").replace("]","")
                    signupPositionButton.text = positionData
                }
                positionListClicked = false
                putSharedBoolean("positionListClicked",positionListClicked!!)
            }


            val imageFragment = ImageFragment()
            val positionFragment = PositionFragment()
            val cityListFragment = CityListFragment()


            var currentUser = auth.currentUser


            if(currentUser == null){

                //signupEmailText.setText(sharedPreferences.getString("eposta","").toString())

                signupSaveButton.setOnClickListener {
                    //Boş e-mail-şifre girişi kontrolü
                    if(TextUtils.isEmpty(signupEmailText.text.toString())){
                        signupEmailText.error = "E-postanızı giriniz"
                        return@setOnClickListener
                    }
                    else if(TextUtils.isEmpty(signupPasswordText.text.toString())){
                        signupPasswordText.error = "Şifrenizi giriniz"
                        return@setOnClickListener
                    }
                    else{
                        //E-mail ve şifre kaydı
                        auth.createUserWithEmailAndPassword(signupEmailText.text.toString(),signupPasswordText.text.toString())
                            .addOnSuccessListener {
                                //Kullanicinin email ve şifre bilgileri kaydedildi
                                currentUser = auth.currentUser

                                signupSaveButton.visibility = View.INVISIBLE

                                getVisibleItems()

                                registered = true
                                putSharedBoolean("registered",registered!!)

                            }.addOnFailureListener {
                                Toast.makeText(requireActivity(),it.localizedMessage,Toast.LENGTH_LONG)
                                    .show()
                            }
                    }

                }

            }
            else{
                signupEmailText.setText(currentUser?.email)
            }

            signupCityButton.setOnClickListener {
                putSharedData()
                cityListClicked = true
                putSharedBoolean("cityListClicked", cityListClicked!!)
                getFragment(cityListFragment)
            }

            signupPositionButton.setOnClickListener {
                putSharedData()
                positionListClicked = true
                putSharedBoolean("positionListClicked", positionListClicked!!)
                getFragment(positionFragment)
            }


            signupProfileImageButton.setOnClickListener {
                putSharedData()
                imageSelected = true
                putSharedBoolean("imageSelected", imageSelected!!)
                getFragment(imageFragment)
            }


            infoForward.setOnClickListener {

                //Boş veri girişi kontrolleri

                if(TextUtils.isEmpty(signupNickNameText.text.toString())){
                    signupNickNameText.error = "Kullanıcı adınızı giriniz"
                    return@setOnClickListener
                }
                else if(TextUtils.isEmpty(signupNameText.text.toString())){
                    signupNameText.error = "Adınızı giriniz"
                    return@setOnClickListener
                }
                else if(TextUtils.isEmpty(signupAgeText.text.toString())){
                    signupAgeText.error = "Yaşınızı giriniz"
                    return@setOnClickListener
                }
                else if(TextUtils.isEmpty(signupPhoneText.text.toString())){
                    signupPhoneText.error = "Telefon numaranızı giriniz"
                    return@setOnClickListener
                }
                else if(signupCityButton.text.toString() == "Seçiniz"){
                    signupCityButton.error = "Şehrinizi seçiniz"
                    return@setOnClickListener
                }
                else if(signupPositionButton.text.toString() == "Seçiniz"){
                    signupPositionButton.error = "Poziyon seçiniz"
                    return@setOnClickListener
                }
                else if(signupProfileImageButton.text.toString() == "Seçiniz"){
                    signupProfileImageButton.error = "Profil resmi seçiniz"
                    return@setOnClickListener
                }

                else{
                    //Info bilgilerini Firebase'e kaydetme işlemleri
                    //Nickname,Ad,Yaş,Bulunduğu şehir,Telefon Numarası kayıtları

                    //Random id üreteci
                    val uuid = UUID.randomUUID()

                    val storage = Firebase.storage
                    val reference = storage.reference
                    val imageReferences = reference.child("profileImages").child("$uuid.jpg")

                    if(imageUri != null){
                        imageReferences.putFile(imageUri!!).addOnSuccessListener {
                            taskSnapshot ->

                            val uploadedProfilePictureReference = storage.reference.child("profileImages")
                                .child("$uuid.jpg")

                            uploadedProfilePictureReference.downloadUrl.addOnSuccessListener{
                                uri ->

                                val downloadUrl = uri.toString()

                                val userMap = hashMapOf<String,Any>()

                                userMap.put("email",auth.currentUser!!.email.toString())
                                userMap.put("nickname",signupNickNameText.text.toString()).toString()
                                userMap.put("fullname",signupNameText.text.toString()).toString()
                                userMap.put("age",signupAgeText.text.toString()).toString()
                                userMap.put("phoneNumber",signupPhoneText.text.toString()).toString()
                                userMap.put("city",signupCityButton.text.toString()).toString()
                                userMap.put("position",signupPositionButton.text.toString()).toString()
                                userMap.put("downloadUrl",downloadUrl)


                                database.collection("Users").add(userMap)
                                    .addOnCompleteListener {
                                        task ->
                                        if(task.isComplete && task.isSuccessful){
                                            //Kayıt işlemleri başarılı, Profil Aktivitesine yönlendirilecek
                                            sharedPreferences.edit().clear().apply()
                                            registered = false
                                            getProfileActivity()
                                        }
                                    }
                                    .addOnFailureListener {
                                        //Kayıt işlemi başarısız,Hata mesajı...
                                        Toast.makeText(requireActivity(),it.localizedMessage,Toast.LENGTH_LONG)
                                            .show()
                                    }


                            }


                        }
                    }



                }
            }

        }

    }

    private fun getProfileActivity(){
        startActivity(Intent(requireActivity(),ProfileActivity::class.java))
        activity?.finish()
    }

    private fun getFragment(fragment: Fragment) {
        val fragmentTransaction = parentFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment).commit()
    }


    private fun putSharedData(){
        with(binding){
            sharedPreferencesEditor.putString("resim",signupProfileImageButton.text.toString())
            sharedPreferencesEditor.putString("sehir",signupCityButton.text.toString())
            sharedPreferencesEditor.putString("pozisyon",signupPositionButton.text.toString())
            sharedPreferencesEditor.putString("eposta",signupEmailText.text.toString())
            sharedPreferencesEditor.putString("kullaniciAdi",signupNickNameText.text.toString())
            sharedPreferencesEditor.putString("sifre",signupPasswordText.text.toString())
            sharedPreferencesEditor.putString("isim",signupNameText.text.toString())
            sharedPreferencesEditor.putString("yas",signupAgeText.text.toString())
            sharedPreferencesEditor.putString("telefon",signupPhoneText.text.toString())
            sharedPreferencesEditor.apply()
        }
    }


    private fun putSharedBoolean(key: String,value: Boolean){
        sharedPreferencesEditor.putBoolean(key,value)
        sharedPreferencesEditor.apply()
    }
    /*                            / \
                                   |
    //Aşağıdaki 3 Fonksiyonun Tekrarını önlemek |

    private fun putSharedBooleanForCity(){
        sharedPreferencesEditor.putBoolean("cityListClicked",cityListClicked!!)
        sharedPreferencesEditor.apply()
    }
    private fun putSharedBooleanForPosition(){
        sharedPreferencesEditor.putBoolean("positionListClicked",positionListClicked!!)
        sharedPreferencesEditor.apply()
    }
    private fun putSharedBooleanForImage(){
        sharedPreferencesEditor.putBoolean("imageSelected",imageSelected!!)
        sharedPreferencesEditor.apply()
    }
    */

    private fun getVisibleItems(){
        with(binding){
            signup1.visibility = View.VISIBLE
            signup2.visibility = View.VISIBLE
            signup3.visibility = View.VISIBLE
            signup4.visibility = View.VISIBLE
            signup5.visibility = View.VISIBLE
            signup8.visibility = View.VISIBLE
            signup9.visibility = View.VISIBLE

            signupNickNameText.visibility = View.VISIBLE
            signupNameText.visibility = View.VISIBLE
            signupAgeText.visibility = View.VISIBLE
            signupPhoneText.visibility = View.VISIBLE
            signupCityButton.visibility = View.VISIBLE
            signupPositionButton.visibility = View.VISIBLE
            signupProfileImageButton.visibility = View.VISIBLE
        }
    }
    private fun getInvisibleItems(){
        with(binding){
            signup1.visibility = View.INVISIBLE
            signup2.visibility = View.INVISIBLE
            signup3.visibility = View.INVISIBLE
            signup4.visibility = View.INVISIBLE
            signup5.visibility = View.INVISIBLE
            signup8.visibility = View.INVISIBLE
            signup9.visibility = View.INVISIBLE

            signupNickNameText.visibility = View.INVISIBLE
            signupNameText.visibility = View.INVISIBLE
            signupAgeText.visibility = View.INVISIBLE
            signupPhoneText.visibility = View.INVISIBLE
            signupCityButton.visibility = View.INVISIBLE
            signupPositionButton.visibility = View.INVISIBLE
            signupProfileImageButton.visibility = View.INVISIBLE
        }

    }

    private fun getSharedBoolean(){
        cityListClicked = sharedPreferences.getBoolean("cityListClicked",false)
        positionListClicked = sharedPreferences.getBoolean("positionListClicked",false)
        imageSelected = sharedPreferences.getBoolean("imageSelected",false)
        registered = sharedPreferences.getBoolean("registered",false)
    }

    private fun getSharedData() {
        with(binding){
            sharedPreferences.apply {
                signupProfileImageButton.setText(sharedPreferences.getString("resim","Seçiniz").toString()).toString()
                signupCityButton.setText(sharedPreferences.getString("sehir","Seçiniz")).toString()
                signupPositionButton.setText(sharedPreferences.getString("pozisyon","Seçiniz")).toString()
                signupPasswordText.setText(sharedPreferences.getString("sifre", "").toString())
                signupNameText.setText(sharedPreferences.getString("isim", "").toString())
                signupNickNameText.setText(sharedPreferences.getString("kullaniciAdi", "").toString())
                signupAgeText.setText(sharedPreferences.getString("yas", "").toString())
                signupPhoneText.setText(sharedPreferences.getString("telefon", "").toString())
            }
        }
    }

}