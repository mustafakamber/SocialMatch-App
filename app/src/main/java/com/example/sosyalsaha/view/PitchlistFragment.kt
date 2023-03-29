package com.example.sosyalsaha.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sosyalsaha.R
import com.example.sosyalsaha.adapter.PlaceAdapter
import com.example.sosyalsaha.adapter.PlayerAdapter
import com.example.sosyalsaha.databinding.FragmentPitchlistBinding
import com.example.sosyalsaha.model.Place
import com.example.sosyalsaha.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class PitchlistFragment : Fragment() {


    private lateinit var binding : FragmentPitchlistBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var database : FirebaseFirestore
    private lateinit var placeList : ArrayList<Place>
    private lateinit var placeAdapter : PlaceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        database = Firebase.firestore

        placeList = ArrayList<Place>()

        getData()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPitchlistBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding){
            recyclerMapView.layoutManager = LinearLayoutManager(requireActivity())
            placeAdapter = PlaceAdapter(placeList)
            recyclerMapView.adapter = placeAdapter
        }

    }

    private fun getData(){


        database.collection("Place").addSnapshotListener{ value,error ->

            if(activity != null){
                if(error != null){
                    Toast.makeText(activity,error.localizedMessage, Toast.LENGTH_LONG)
                        .show()

                }else{
                    if(value != null){
                        if(!value.isEmpty){
                            val documents =  value.documents
                            for(document in documents){
                                val placeEmailInfo = document.get("placeEmailInfo")
                                if(placeEmailInfo == auth.currentUser!!.email.toString()){
                                    val placeName = document.get("placeName") as String
                                    val placeLatitude = document.get("placeLatitude") as String
                                    val placeLongitude = document.get("placeLongitude") as String
                                    val place = Place(placeName,placeLatitude,placeLongitude)
                                    placeList.add(place)
                                }

                            }
                            placeAdapter.notifyDataSetChanged()
                        }
                    }

                }
            }
        }


    }
}