package com.example.sosyalsaha.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sosyalsaha.adapter.PlayerAdapter
import com.example.sosyalsaha.databinding.FragmentPlayerlistBinding
import com.example.sosyalsaha.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class PlayerlistFragment : Fragment() {

    private lateinit var auth : FirebaseAuth
    private lateinit var database : FirebaseFirestore
    private lateinit var binding : FragmentPlayerlistBinding
    private lateinit var playerList : ArrayList<User>
    private lateinit var filteredPlayerList: ArrayList<User>
    private lateinit var playerAdapter : PlayerAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        database = Firebase.firestore

        playerList = ArrayList<User>()
        filteredPlayerList = ArrayList<User>()

        getData()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlayerlistBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding){
            recyclerPlayerView.layoutManager = LinearLayoutManager(requireActivity())
            playerAdapter = PlayerAdapter(playerList)
            recyclerPlayerView.adapter = playerAdapter

            filteredPlayerList.addAll(playerList)

            //Aramaya göre filtreleme işlemleri
            playerSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
                android.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    //----
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if(newText != null){
                        filteredPlayerList.clear()
                        val searchText = newText.lowercase()
                        playerList.forEach{
                            if(it.nickname.lowercase().contains(searchText)){
                                filteredPlayerList.add(it)
                                val filterAdapter = PlayerAdapter(filteredPlayerList)
                                recyclerPlayerView.adapter = filterAdapter
                            }

                        }
                        playerAdapter.notifyDataSetChanged()
                    }
                    return true
                }

            })

        }
    }

    private fun getData(){

        database.collection("Users").addSnapshotListener{ value,error ->

            if(activity != null){
                if(error != null){
                    Toast.makeText(activity,error.localizedMessage,Toast.LENGTH_LONG)
                        .show()

                }else{
                    if(value != null){
                        if(!value.isEmpty){
                            val documents =  value.documents
                            for(document in documents){
                                val playersEmailInfo = document.get("email")
                                if(playersEmailInfo != auth.currentUser!!.email.toString()){
                                    val fullName = document.get("fullname") as String
                                    val age = document.get("age") as String
                                    val city = document.get("city") as String
                                    val downloadUrl = document.get("downloadUrl") as String
                                    val nickname = document.get("nickname") as String
                                    val position = document.get("position") as String
                                    val phoneNumber = document.get("phoneNumber") as String


                                    val player = User(city,nickname,position,downloadUrl,phoneNumber,fullName,age)
                                    playerList.add(player)
                                }

                            }

                            playerAdapter.notifyDataSetChanged()
                        }
                    }
            }


            }

        }


    }
}