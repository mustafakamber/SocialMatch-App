package com.example.sosyalsaha.adapter

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.sosyalsaha.R

import com.example.sosyalsaha.databinding.RecyclermapRowBinding
import com.example.sosyalsaha.model.Place
import com.example.sosyalsaha.view.PitchmapsFragment
import com.example.sosyalsaha.view.PlayerdetailsFragment

class PlaceAdapter(var placeList: ArrayList<Place>) : RecyclerView.Adapter<PlaceAdapter.PlaceHolder>() {

    class PlaceHolder(val recyclermapRowBinding: RecyclermapRowBinding) : RecyclerView.ViewHolder(recyclermapRowBinding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceHolder {
        val binding: RecyclermapRowBinding = RecyclermapRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlaceHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaceHolder, position: Int) {

        with(holder){
            recyclermapRowBinding.recyclerMapTextView.setText(placeList[position].pitchName)
            itemView.setOnClickListener {
                val data = Bundle()

                data.putString("placeName",placeList[position].pitchName)
                data.putString("placeLatitude",placeList[position].latitude)
                data.putString("placeLongitude",placeList[position].longitude)
                data.putString("info", "old")
                val pitchMapFragment = PitchmapsFragment()
                pitchMapFragment.arguments = data

                val fragmentManager = (holder.itemView.context as AppCompatActivity).supportFragmentManager
                fragmentManager.beginTransaction().replace(R.id.framePitchLayout, pitchMapFragment).addToBackStack(null).commit()
            } }

    }

    override fun getItemCount(): Int {
        return placeList.size
    }


}