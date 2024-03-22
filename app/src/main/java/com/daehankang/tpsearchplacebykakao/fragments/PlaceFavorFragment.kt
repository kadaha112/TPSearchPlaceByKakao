package com.daehankang.tpsearchplacebykakao.fragments


import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.daehankang.tpsearchplacebykakao.adapter.PlaceListRecyclerAdapter
import com.daehankang.tpsearchplacebykakao.data.Place
import com.daehankang.tpsearchplacebykakao.databinding.FragmentPlaceFavorBinding


class PlaceFavorFragment : Fragment() {

    lateinit var binding : FragmentPlaceFavorBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlaceFavorBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadData()

    }

    override fun onResume() {
        super.onResume()

        loadData()
    }
    private fun loadData(){
        // SQLite DB ... [ place.db ] 파일 안에.. "favor" 테이블에 저장된 장소정보들을 읽어오기
        val db = requireContext().openOrCreateDatabase("place", Activity.MODE_PRIVATE, null)

        val cursor= db.rawQuery("SELECT * FROM favor", null)
        // 레코드의 개수만큼 반복하면서 값들을 읽어오기
        cursor?.apply {
            moveToFirst()

            val placeList : MutableList<Place> = mutableListOf()
            for (i in 0 until count){
                val id : String = getString(0)
                val place_name = getString(1)
                val category_name = getString(2)
                val phone = getString(3)
                val address_name = getString(4)
                val road_address_name = getString(5)
                val x = getString(6)
                val y = getString(7)
                val place_url = getString(8)
                val distance = getString(9)

                val place: Place = Place(id, place_name, category_name, phone, address_name, road_address_name, x, y, place_url, distance)
                placeList.add(place)

                moveToNext()
            }// for..

            // 리스트 데이터를 리사이클러뷰에 아이템 뷰로 보이도록. 아답터 설정
            binding.recyclerView.adapter = PlaceListRecyclerAdapter(requireContext(),placeList)
        }// apply..

    }// loadData method..


}