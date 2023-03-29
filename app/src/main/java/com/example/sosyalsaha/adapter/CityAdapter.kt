package com.example.sosyalsaha.adapter

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.sosyalsaha.R
import com.example.sosyalsaha.databinding.RecyclercityRowBinding
import com.example.sosyalsaha.view.InfoFragment
import com.example.sosyalsaha.view.SignupActivity

class CityAdapter(val cityList : ArrayList<String>) : RecyclerView.Adapter<CityAdapter.CityHolder>() {


    class CityHolder(val binding : RecyclercityRowBinding) : RecyclerView.ViewHolder(binding.root){




    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityHolder {
       //reycylerCity_row.xml ile bağlama işlemleri
        val binding = RecyclercityRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CityHolder(binding)
    }


    override fun onBindViewHolder(holder: CityHolder, position: Int) {
       //Ekranda nelerin gosterilecegi
        holder.binding.recyclerCityTextView.text = cityList.get(position)

        //InfoFragment'a seçilen şehir verisini vererek dön
        holder.itemView.setOnClickListener{


            if (position != RecyclerView.NO_POSITION) {
                val data = cityList[position]
                val bundle = Bundle()
                bundle.putString("city",data)
                val fragment = InfoFragment()
                fragment.arguments = bundle

                // Fragment geçişi için gereken kodlar
                val fragmentManager = (holder.itemView.context as AppCompatActivity).supportFragmentManager
                fragmentManager.beginTransaction().replace(R.id.frameLayout, fragment).addToBackStack(null).commit()
            }

        }

    }

    override fun getItemCount(): Int {
        //Gosterilecek dizinin eleman sayisi
        return cityList.size
    }



}