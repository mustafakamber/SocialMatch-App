package com.example.sosyalsaha.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context.LOCATION_SERVICE
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.sosyalsaha.R
import com.example.sosyalsaha.databinding.FragmentInfoBinding
import com.example.sosyalsaha.databinding.FragmentPitchmapsBinding
import com.example.sosyalsaha.model.Place

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PitchmapsFragment : Fragment(),GoogleMap.OnMapLongClickListener {


    private lateinit var binding : FragmentPitchmapsBinding
    private lateinit var locationManager : LocationManager
    private lateinit var locationListener : LocationListener

    private lateinit var permissionLauncher : ActivityResultLauncher<String>
    private lateinit var sharedPreferences : SharedPreferences

    private var googleMap : GoogleMap? = null
    private  var trackBoolean : Boolean? = null
    private var selectedLatitude : Double? = null
    private var selectedLongitude : Double? = null

    private lateinit var auth : FirebaseAuth
    private lateinit var database : FirebaseFirestore

    val pitchListFragment = PitchlistFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()

        registerLauncher()


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentPitchmapsBinding.inflate(layoutInflater)

        sharedPreferences = requireActivity().getSharedPreferences("package com.example.sosyalsaha",
            MODE_PRIVATE)

        trackBoolean = false
        selectedLatitude = 0.0
        selectedLongitude = 0.0



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)



        with(binding){

            pitchSaveButton.setOnClickListener {

                //Boş Saha Ismi- Marker kontrolü
                if(TextUtils.isEmpty(pitchNameText.text.toString())){
                    pitchNameText.error = "Mekan ismini giriniz"
                    return@setOnClickListener
                }
                else if(selectedLatitude == 0.0 || selectedLongitude == 0.0){
                    Toast.makeText(requireActivity(),"Mekan seçiniz",Toast.LENGTH_SHORT)
                        .show()
                }
                else{

                    //Konum bilgileri kayıt işlemleri
                    val placeMap = hashMapOf<String,Any>()

                    placeMap.put("placeName",pitchNameText.text.toString())
                    placeMap.put("placeLatitude",selectedLatitude.toString())
                    placeMap.put("placeLongitude",selectedLongitude.toString())
                    placeMap.put("placeEmailInfo",auth.currentUser!!.email.toString())


                    database.collection("Place")
                        .add(placeMap)
                        .addOnCompleteListener {
                                task ->
                            if(task.isComplete && task.isSuccessful){
                                getFragment(pitchListFragment)
                            }
                        }
                        .addOnFailureListener {
                            Toast.makeText(requireActivity(),it.localizedMessage,Toast.LENGTH_LONG)
                                .show()
                        }


                }

            }


        }


    }

    @SuppressLint("MissingPermission")
    private val callback = OnMapReadyCallback { googleMap ->
        //latitude -> enlem , longitude -> boylam

        googleMap?.setOnMapLongClickListener{
            googleMap!!.clear()
            googleMap!!.addMarker(MarkerOptions().position(it))

            selectedLatitude = it.latitude
            selectedLongitude = it.longitude
        }

        val data = arguments
        if(data != null){
            val info = data.getString("info")
            val name = data.getString("placeName")
            val latitude = data.getString("placeLatitude")
            val longitude = data.getString("placeLongitude")
            if(info == "new"){
                //Yeni map verisi girilecek

                with(binding){
                    pitchSaveButton.visibility = View.VISIBLE
                    pitchDeleteButton.visibility = View.GONE
                }
                locationManager = requireActivity().getSystemService(LOCATION_SERVICE) as LocationManager

                locationListener = object : LocationListener {
                    override fun onLocationChanged(location: Location) {
                        trackBoolean = sharedPreferences.getBoolean("trackBoolean",false)
                        if(!trackBoolean!!){
                            //Her konum değişikliğinde yapılacaklar
                            val userLocation = LatLng(location.latitude,location.longitude)
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,15f))
                            sharedPreferences.edit().putBoolean("trackBoolean",true).apply()
                        }
                    }

                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                        super.onStatusChanged(provider, status, extras)
                    }
                }

                if(ContextCompat.checkSelfPermission(requireActivity(),Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                    //Daha önce hiç konum izni alınmadı,Izin istenecek
                    if(ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),Manifest.permission.ACCESS_FINE_LOCATION)){
                        Snackbar.make(binding.root,"Konumunuz için izniniz gerekli",Snackbar.LENGTH_INDEFINITE)
                            .setAction("Izin ver"){
                                //Izin iste
                                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                            }.show()
                    }else{
                        //Izin iste
                        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    }

                }
                else{
                    //Izin alındı
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0f,locationListener)
                    val lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    if(lastLocation != null){
                        val lastUserLocation = LatLng(lastLocation.latitude,lastLocation.longitude)
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation,15f))
                    }
                    googleMap.isMyLocationEnabled = true

                }

            }else{
                //Firestore'dan çekilen map verisi
                googleMap.clear()

                val latLng = LatLng(latitude!!.toDouble(),longitude!!.toDouble())
                googleMap.addMarker(MarkerOptions().position(latLng).title(name))
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))

                with(binding){
                    pitchNameText.setText(name)
                    pitchSaveButton.visibility = View.GONE
                    pitchDeleteButton.visibility = View.VISIBLE

                    //Konum Silme İşlemleri
                    pitchDeleteButton.setOnClickListener {
                        database.collection("Place")
                            .whereEqualTo("placeEmailInfo",auth.currentUser!!.email.toString())
                            .whereEqualTo("placeLatitude",latitude)
                            .whereEqualTo("placeLongitude",longitude)
                            .whereEqualTo("placeName",name)
                            .get()
                            .addOnSuccessListener { querySnapshot ->
                                for(document in querySnapshot.documents){
                                    document.reference.delete()
                                    getFragment(pitchListFragment)
                                }
                            }
                            .addOnFailureListener { exception ->
                                Toast.makeText(activity, exception.localizedMessage, Toast.LENGTH_LONG).show()
                            }

                    }
                }

            }
        }




    }

    private fun registerLauncher(){

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){
            result ->
            if(result){
                if(ContextCompat.checkSelfPermission(requireContext().applicationContext,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    //Izin verildi
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0f,locationListener)
                    val lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    if(lastLocation != null){
                        val lastUserLocation = LatLng(lastLocation.latitude,lastLocation.longitude)
                        if(googleMap != null){
                            googleMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation,15f))
                            googleMap!!.isMyLocationEnabled = true
                        }

                    }

                }

            }else{
                //Izin verilmedi
                Toast.makeText(requireContext(),"Izniniz gerekiyor!",Toast.LENGTH_LONG)
                    .show()

            }

        }
    }

    private fun getFragment(fragment: Fragment) {
        val fragmentTransaction = parentFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.framePitchLayout, fragment).commit()
    }

    override fun onMapLongClick(p0: LatLng) {

    }

}




