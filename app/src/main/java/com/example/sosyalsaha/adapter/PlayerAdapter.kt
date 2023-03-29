package com.example.sosyalsaha.adapter

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.sosyalsaha.R
import com.example.sosyalsaha.databinding.RecyclerplayerRowBinding
import com.example.sosyalsaha.model.User
import com.example.sosyalsaha.view.InfoFragment
import com.example.sosyalsaha.view.PlayerdetailsFragment
import com.squareup.picasso.Picasso
import kotlinx.coroutines.NonDisposableHandle.parent

import java.nio.channels.InterruptedByTimeoutException


class PlayerAdapter (val playerList : ArrayList<User>) : RecyclerView.Adapter<PlayerAdapter.PlayerHolder>(){



    class PlayerHolder(val binding: RecyclerplayerRowBinding) : RecyclerView.ViewHolder(binding.root){


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerHolder {
        //recyclerPlayer_row.xml ile bağlama islemleri
        val binding = RecyclerplayerRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PlayerHolder(binding)


    }

   override fun onBindViewHolder(holder: PlayerHolder,position: Int){
       //Ekranda gösterilecekler

       with(holder){
           with(binding){
               recyclerPlayerNickName.text = playerList.get(position).nickname
               recyclerPlayerPosition.text = playerList.get(position).position
               Picasso.get().load(playerList.get(position).downloadUrl).into(recyclerPlayerImage)


               itemView.setOnClickListener {

                   val data = Bundle()

                   data.putString("nickname",playerList.get(position).nickname)
                   data.putString("image",playerList.get(position).downloadUrl)
                   data.putString("fullname",playerList.get(position).fullName)
                   data.putString("age",playerList.get(position).age)
                   data.putString("city",playerList.get(position).city)
                   data.putString("position",playerList.get(position).position)
                   data.putString("phonenumber",playerList.get(position).phoneNumber)

                   val detailsFragment = PlayerdetailsFragment()
                   detailsFragment.arguments = data



                   val fragmentManager = (holder.itemView.context as AppCompatActivity).supportFragmentManager
                   fragmentManager.beginTransaction().replace(R.id.framePlayerLayout, detailsFragment).addToBackStack(null).commit()
               }
           }
       }




   }


    override fun getItemCount(): Int {

        //Dizinin eleman sayisi
        return playerList.size
    }

}


