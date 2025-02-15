package com.daehankang.tpsearchplacebykakao.activities

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import android.widget.Toast
import com.daehankang.tpsearchplacebykakao.R
import com.daehankang.tpsearchplacebykakao.data.Place
import com.daehankang.tpsearchplacebykakao.databinding.ActivityPlaceDetailBinding
import com.google.gson.Gson

class PlaceDetailActivity : AppCompatActivity() {

    private val binding by lazy { ActivityPlaceDetailBinding.inflate(layoutInflater) }

    private var isFavorite = false

    // SQLite Database를 제어하는 객체 참조변수
    private lateinit var db : SQLiteDatabase

    // 현재 장소에 대한 정보 객체 참조변수
    private lateinit var place : Place

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        
        // 인텐트로부터 데이터 전달받기
        val s : String? = intent.getStringExtra("place")
        s?.also {
            // json --> 객체
            place = Gson().fromJson(it,Place::class.java)

            // 웹뷰를 사용할때 반드시 해야 할 3가지 설정

            binding.wv.webViewClient= WebViewClient() // 현재 웹뷰안에서 웹 문서를 열리도록..
            binding.wv.webChromeClient= WebChromeClient() // 웹문서안에서 다이얼로그나 팝업 같은 것들이 발동하도록..

            binding.wv.settings.javaScriptEnabled=true // 웹뷰는 기본적으로 보안문제로 JS 동작을 막아놓았기에.. 허용하도록

            binding.wv.loadUrl(place.place_url)
        }

        // "Place.db"라는 이름으로 데이터베이스 파일을 만들거나 열어서 참조하기
        db = openOrCreateDatabase("place", MODE_PRIVATE, null)

        // "favor" 라는 이름의 표(테이블) 만들기 - SQL 쿼리문을 사용하여..CRUD 작업수행
        db.execSQL("CREATE TABLE IF NOT EXISTS favor(id TEXT PRIMARY KEY, place_name TEXT, category_name TEXT, phone TEXT, address_name TEXT, road_address_name TEXT, x TEXT,y TEXT,place_url TEXT, distance TEXT)")


        // 찜 상태 체크하기
        isFavorite = checkFavorite()

        if (isFavorite) binding.fabFavor.setImageResource(R.drawable.baseline_favorite_24)
        else binding.fabFavor.setImageResource(R.drawable.baseline_favorite_border_24)

        // 찜 버튼 클릭 처리
        binding.fabFavor.setOnClickListener {
            if (isFavorite){
                // 찜 DB의 데이터를 삭제
                place.apply {
                    db.execSQL("DELETE FROM favor WHERE id = ?", arrayOf(id))
                }

                Toast.makeText(this, "찜 목록에서 제거되었습니다.", Toast.LENGTH_SHORT).show()
            }else{
                // 찜 DB에 데이터를 저장
                place.apply {
                    db.execSQL("INSERT INTO favor VALUES('$id','$place_name','$category_name','$phone','$address_name','$road_address_name','$x','$y','$place_url','$distance')")
                }
                Toast.makeText(this, "찜 목록에 추가되었습니다.", Toast.LENGTH_SHORT).show()
            }

            isFavorite= !isFavorite
            if (isFavorite) binding.fabFavor.setImageResource(R.drawable.baseline_favorite_24)
            else binding.fabFavor.setImageResource(R.drawable.baseline_favorite_border_24)
        }

    }//onCreate

    // SQLite Database의 찜 목록에 저장된 장소정보인지 체크하여 결과 여부를 리턴 [ true/ false ]
    private fun checkFavorite() : Boolean{

        // SQLite DB 의 "favor" 테이블에 현재장소에 대한 데이터가 있는지 확인
        place.apply {
            val cursor:Cursor = db.rawQuery("SELECT * FROM favor WHERE id = ?", arrayOf(id))
            // cursor는 검색조건에 해당하는 데이터를 가져와 만든 가상의 결과표 객체임
            // cursor.count : 총 레코드의 수
            if (cursor.count>0) return true
        }



        return false
    }

    // 뒤로가기 버튼처리
    override fun onBackPressed() {
        if (binding.wv.canGoBack()) binding.wv.goBack()
        else super.onBackPressed()
    }


}