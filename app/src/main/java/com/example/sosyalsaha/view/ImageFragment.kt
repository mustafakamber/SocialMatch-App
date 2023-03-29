package com.example.sosyalsaha.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.sosyalsaha.R
import com.example.sosyalsaha.databinding.FragmentImageBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.io.IOException
import java.nio.channels.InterruptedByTimeoutException
import java.util.*


class ImageFragment : Fragment() {

    private lateinit var auth : FirebaseAuth
    private lateinit var binding: FragmentImageBinding
    private lateinit var permissionLauncher : ActivityResultLauncher<String>
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    var selectedProfilePicture : Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()

        registerLauncher()



    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentImageBinding.inflate(layoutInflater)
        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val infoFragment = InfoFragment()

        with(binding){
            signupProfilePictureButton.setOnClickListener {
                activity?.let {
                    if(ContextCompat.checkSelfPermission(requireActivity().applicationContext,Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED){
                      //Daha önceden görsel seçme izni verilmedi.İzin istenecek
                        if(ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),Manifest.permission.READ_EXTERNAL_STORAGE)){
                            //Snackbarlı izin
                            Snackbar.make(view,"Galeriye ulaşmak için izniniz gerekiyor!",Snackbar.LENGTH_INDEFINITE)
                                .setAction("İzin Ver",View.OnClickListener {
                                    //Izin isteme
                                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                                }).show()
                        }
                        else{
                            //Snackbarsiz izin
                            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                        }
                    }
                    else{
                        //Halihazırda izin verilmiş.Galeriye gidilecek
                        activityResultLauncher.launch(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI))

                    }

                }
            }
            imageForward.setOnClickListener {


                //Fotoğraf seçildi mi kontrolü
                if(selectedProfilePicture != null){
                    val rumble = Bundle()
                    rumble.putParcelable("resimUri",selectedProfilePicture)
                    infoFragment.arguments = rumble
                    getFragment(infoFragment)
                }
                else{
                    Toast.makeText(requireActivity(),"Profil fotoğrafınızı seçmediniz.",Toast.LENGTH_LONG)
                        .show()
                }

            }

        }
    }

    private fun registerLauncher(){

        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
                galleryResult ->
            if(galleryResult.resultCode == AppCompatActivity.RESULT_OK){
                //Kullanici galeriden gorseli seçti
                val intentFromGalleryResult = galleryResult.data
                if(intentFromGalleryResult != null){
                    selectedProfilePicture = intentFromGalleryResult.data
                    selectedProfilePicture.let {
                        binding.signupProfilePictureButton.setImageURI(selectedProfilePicture)
                    }
                }
            }
        }


        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){
            permissionResult ->
            if(permissionResult){
                //Izin alindi galeriye gidilecek
                activityResultLauncher.launch(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI))
            }else{
                //Izin alinamadi hata mesaji
                Toast.makeText(requireContext(),"İzniniz Gerekiyor!",Toast.LENGTH_LONG).show()
            }
        }


    }

    private fun getFragment(fragment: Fragment) {
        val fragmentTransaction = parentFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment).commit()
    }

}