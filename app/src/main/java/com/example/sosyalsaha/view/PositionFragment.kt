package com.example.sosyalsaha.view

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.sosyalsaha.R
import com.example.sosyalsaha.databinding.FragmentPositionBinding

class PositionFragment : Fragment() {



    private lateinit var binding : FragmentPositionBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPositionBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val positionList = ArrayList<String>()
        val infoFragment = InfoFragment()

        //Seçilen pozisyon kontrolü
        with(binding){


            positionForward.setOnClickListener {
                //Poziyon bilgisini InfoFragment'a gönderme

                if(positionGoalKeeper.isChecked && positionRightBack.isChecked && positionCentralBack.isChecked && positionLeftBack.isChecked
                    && positionDeffensiveMiddle.isChecked && positionMiddle.isChecked && positionOffensiveMiddle.isChecked && positionRightWinger.isChecked
                    && positionSantrafor.isChecked && positionLeftWinger.isChecked){
                    positionList.add("Hepsi")
                }
                else{
                    if(positionGoalKeeper.isChecked){
                        positionList.add("K")
                    }
                    if(positionRightBack.isChecked){
                        positionList.add("SĞB")
                    }
                    if(positionCentralBack.isChecked){
                        positionList.add("STP")
                    }
                    if(positionLeftBack.isChecked){
                        positionList.add("SLB")
                    }
                    if(positionDeffensiveMiddle.isChecked){
                        positionList.add("DOS")
                    }
                    if(positionMiddle.isChecked){
                        positionList.add("OS")
                    }
                    if(positionOffensiveMiddle.isChecked){
                        positionList.add("OOS")
                    }
                    if(positionRightWinger.isChecked){
                        positionList.add("SĞK")
                    }
                    if(positionSantrafor.isChecked){
                        positionList.add("ST")
                    }
                    if(positionLeftWinger.isChecked){
                        positionList.add("SLK")
                    }

                }

                val jungle = Bundle()
                jungle.putStringArrayList("positions",positionList)

                infoFragment.arguments = jungle


                getFragment(infoFragment)
            }
        }
    }


    private fun getFragment(fragment: Fragment) {
        val fragmentTransaction = parentFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment).commit()
    }






}