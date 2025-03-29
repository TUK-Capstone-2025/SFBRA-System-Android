package com.example.sfbra_system_android.ui.activities

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.sfbra_system_android.databinding.ActivityPathViewBinding
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapOverlay
import com.kakao.vectormap.MapView
import com.kakao.vectormap.camera.CameraUpdate
import com.kakao.vectormap.camera.CameraUpdateFactory

// 주행기록 경로 화면
class PathViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPathViewBinding
    private lateinit var mapView: MapView
    private var kakaoMap: KakaoMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPathViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "주행 기록"

        // API 레벨 26 이상에서만 실행
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 시스템 내비게이션 바 버튼 색상 변경
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        }

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)  // 액션바 뒤로가기 버튼 활성화
        }

        mapView = binding.mapView
        // 카카오 맵 api로 지도 띄우기
        mapView.start(object : MapLifeCycleCallback() {
            override fun onMapDestroy() {
                // 지도 API가 정상적으로 종료될 때 호출
            }

            override fun onMapError(error: Exception) {
                // 인증 실패 및 지도 사용 중 에러가 발생할 때 호출
                Log.e("MapError", "지도 오류 발생: ${error.message}")
            }
        }, object : KakaoMapReadyCallback() {
            override fun onMapReady(kakaoMap: KakaoMap) {
                // 지도 준비 완료
                this@PathViewActivity.kakaoMap = kakaoMap // 카카오맵 객체 저장(프래그먼트 내에서 사용할 수 있도록)
                Log.d("MapReady", "카카오 맵 로드 완료!")

                // 기본 시작 위치(한국공대)
                val initialPosition = LatLng.from(37.340179, 126.733591) // todo 경로 데이터로 변경
                val cameraUpdate: CameraUpdate = CameraUpdateFactory.newCenterPosition(initialPosition, 16)
                kakaoMap.moveCamera(cameraUpdate) // 카메라 이동
                kakaoMap.showOverlay(MapOverlay.BICYCLE_ROAD)
            }
        })
    }
}