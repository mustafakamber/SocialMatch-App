package com.example.sosyalsaha.view

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.sosyalsaha.databinding.FragmentPlayerdetailsBinding
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso


class PlayerdetailsFragment : Fragment() {


    private lateinit var binding : FragmentPlayerdetailsBinding

    private lateinit var permissionLauncher : ActivityResultLauncher<String>
    var phoneNumber : String? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        registerLauncher()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentPlayerdetailsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding){
            val data = arguments
            if(data != null){
                detailsNameText.setText(data.getString("fullname")).toString()
                detailsAgeText.setText(data.getString("age")).toString()
                detailsCityText.setText(data.getString("city")).toString()
                detailsPositionText.setText(data.getString("position")).toString()
                Picasso.get().load(data.getString("image")).into(detailsProfilePicture)

                phoneNumber = data.getString("phonenumber").toString()

                detailsCallButton.setOnClickListener {
                    activity?.let {
                        if(ContextCompat.checkSelfPermission(requireActivity().applicationContext,
                                Manifest.permission.CALL_PHONE)
                            != PackageManager.PERMISSION_GRANTED){
                            //Daha önceden çağrı gönderme izni verilmedi.İzin istenecek
                            if(ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                                    Manifest.permission.CALL_PHONE)){
                                //Snackbarlı izin
                                Snackbar.make(view,"Kullanıcıya ulaşmak için izniniz gerekiyor!",
                                    Snackbar.LENGTH_INDEFINITE)
                                    .setAction("İzin Ver",View.OnClickListener {
                                        //Izin isteme
                                        permissionLauncher.launch(Manifest.permission.CALL_PHONE)
                                    }).show()
                            }
                            else{
                                //Snackbarsiz izin
                                permissionLauncher.launch(Manifest.permission.CALL_PHONE)
                            }
                        }
                        else{
                            //Halihazırda izin verilmiş.Kullanıcı Aranacak

                            val intentToCall = Intent(Intent.ACTION_CALL,Uri.parse("tel:$phoneNumber"))
                            startActivity(intentToCall)

                        }

                    }
                }
            }
        }



    }



    private fun registerLauncher(){
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){
                permissionResult ->
            if(permissionResult){
                //Izin alindi kullanıcı aranacak
                val intentToCall = Intent(Intent.ACTION_CALL,Uri.parse("tel:$phoneNumber"))
                startActivity(intentToCall)
            }else{
                //Izin alinamadi hata mesaji
                Toast.makeText(requireContext(),"İzniniz Gerekiyor!",Toast.LENGTH_LONG).show()
            }
        }
    }




}