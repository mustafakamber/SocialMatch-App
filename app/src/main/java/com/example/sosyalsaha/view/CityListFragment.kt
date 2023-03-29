package com.example.sosyalsaha.view

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sosyalsaha.adapter.CityAdapter
import com.example.sosyalsaha.databinding.FragmentListBinding


class CityListFragment : Fragment() {

    private lateinit var binding : FragmentListBinding
    private lateinit var cityList : ArrayList<String>
    private lateinit var filteredList: ArrayList<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListBinding.inflate(layoutInflater)
        return binding.root




    }

    override fun onViewCreated(view : View,savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)

        cityList = ArrayList<String>()
        filteredList = ArrayList<String>()

        //Sehirleri ekleme
        cityList.add("Adana")
        cityList.add("Adıyaman")
        cityList.add("Afyon")
        cityList.add("Ağrı")
        cityList.add("Amasya")
        cityList.add("Ankara")
        cityList.add("Antalya")
        cityList.add("Artvin")
        cityList.add("Aydın")
        cityList.add("Balıkesir")
        cityList.add("Bilecik")
        cityList.add("Bingöl")
        cityList.add("Bitlis")
        cityList.add("Bolu")
        cityList.add("Burdur")
        cityList.add("Bursa")
        cityList.add("Çanakkale")
        cityList.add("Çankırı")
        cityList.add("Çorum")
        cityList.add("Denizli")
        cityList.add("Diyarbakır")
        cityList.add("Edirne")
        cityList.add("Elazığ")
        cityList.add("Erzincan")
        cityList.add("Erzurum")
        cityList.add("Eskişehir")
        cityList.add("Gaziantep")
        cityList.add("Giresun")
        cityList.add("Gümüşhane")
        cityList.add("Hakkari")
        cityList.add("Hatay")
        cityList.add("Isparta")
        cityList.add("Mersin")
        cityList.add("İstanbul")
        cityList.add("İzmir")
        cityList.add("Kars")
        cityList.add("Kastamonu")
        cityList.add("Kayseri")
        cityList.add("Kırklareli")
        cityList.add("Kırşehir")
        cityList.add("Kocaeli")
        cityList.add("Konya")
        cityList.add("Kütahya")
        cityList.add("Malatya")
        cityList.add("Manisa")
        cityList.add("Kahramanmaraş")
        cityList.add("Mardin")
        cityList.add("Muğla")
        cityList.add("Muş")
        cityList.add("Nevşehir")
        cityList.add("Niğde")
        cityList.add("Ordu")
        cityList.add("Rize")
        cityList.add("Sakarya")
        cityList.add("Samsun")
        cityList.add("Siirt")
        cityList.add("Sinop")
        cityList.add("Sivas")
        cityList.add("Tekirdağ")
        cityList.add("Trabzon")
        cityList.add("Tunceli")
        cityList.add("Şanlıurfa")
        cityList.add("Uşak")
        cityList.add("Van")
        cityList.add("Yozgat")
        cityList.add("Zonguldak")
        cityList.add("Aksaray")
        cityList.add("Bayburt")
        cityList.add("Karaman")
        cityList.add("Kırıkkale")
        cityList.add("Batman")
        cityList.add("Şırnak")
        cityList.add("Bartın")
        cityList.add("Ardahan")
        cityList.add("Iğdır")
        cityList.add("Yalova")
        cityList.add("Karabük")
        cityList.add("Kilis")
        cityList.add("Osmaniye")
        cityList.add("Düzce")

        filteredList.addAll(cityList)

        with(binding){
            recyclerCityView.layoutManager = LinearLayoutManager(requireContext())
            val cityAdapter = CityAdapter(cityList)
            recyclerCityView.adapter = cityAdapter


            //Aramaya göre filtreleme işlemleri
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
                android.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    //----
                    return false
                }
                override fun onQueryTextChange(newText: String?): Boolean {
                    if(newText != null){
                        filteredList.clear()
                        val searchText = newText.lowercase()
                        cityList.forEach{
                            if(it.lowercase().contains(searchText)){
                                filteredList.add(it)
                                val filterAdapter = CityAdapter(filteredList)
                                recyclerCityView.adapter = filterAdapter
                            }
                        }
                        cityAdapter.notifyDataSetChanged()
                    }
                    return true
                }

            })
        }




    }



}


