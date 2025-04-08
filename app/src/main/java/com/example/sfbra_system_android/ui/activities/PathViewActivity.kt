package com.example.sfbra_system_android.ui.activities

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.sfbra_system_android.R
import com.example.sfbra_system_android.data.services.LocationPoint
import com.example.sfbra_system_android.databinding.ActivityPathViewBinding
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.LatLngBounds
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapOverlay
import com.kakao.vectormap.MapView
import com.kakao.vectormap.camera.CameraUpdate
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.Label
import com.kakao.vectormap.label.LabelManager
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles
import com.kakao.vectormap.label.LabelTextBuilder
import com.kakao.vectormap.route.RouteLine
import com.kakao.vectormap.route.RouteLineManager
import com.kakao.vectormap.route.RouteLineOptions
import com.kakao.vectormap.route.RouteLineSegment
import com.kakao.vectormap.route.RouteLineStyle
import com.kakao.vectormap.route.RouteLineStyles
import com.kakao.vectormap.route.RouteLineStylesSet

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

        val routePoints = getRouteToLatLng()
        drawRouteLine(routePoints)
        addStartEndLabels(routePoints)
        moveCameraToFitRoute(routePoints)
    }

    // LocationPoint 리스트를 LatLng 리스트로 변환하는 함수
    private fun getRouteToLatLng(): List<LatLng> {
        return listOf(
            LocationPoint(37.340179, 126.733591, 0),
            LocationPoint(37.340300, 126.733700, 0),
            LocationPoint(37.340450, 126.733850, 1),
            LocationPoint(37.340600, 126.734000, 0),
            LocationPoint(37.340750, 126.734150, 2) // 더미데이터
        ).map { LatLng.from(it.latitude, it.longitude) }
    }

    // 카카오맵의 RouteLine으로 경로 그리기 함수
    private fun drawRouteLine(routePoints: List<LatLng>) {
        val routeLineManager: RouteLineManager = kakaoMap?.getRouteLineManager()!! // 경로 매니저
        val routeLineLayer = routeLineManager.getLayer() // 레이어

        val styles = RouteLineStyles.from(RouteLineStyle.from(16.0f, Color.BLUE)) // 스타일
        val stylesSet = RouteLineStylesSet.from(styles)
        val segment = RouteLineSegment.from(routePoints, styles)

        val routeLine: RouteLine = routeLineLayer.addRouteLine(RouteLineOptions.from(segment).setStylesSet(stylesSet))
    }

    // 시작/종료 라벨 추가 함수
    private fun addStartEndLabels(routePoints: List<LatLng>) {
        if (routePoints.isEmpty()) return

        val labelManager: LabelManager = kakaoMap?.getLabelManager()!!
        val labelLayer = labelManager.getLayer()

        val labelStyle = LabelStyle.from().setTextStyles(32, R.color.red)

        val startLabel: Label? = labelLayer?.addLabel(
            LabelOptions.from(routePoints.first())
                .setStyles(labelStyle)
                .setTexts(LabelTextBuilder().setTexts("출발"))
        )

        val endLabel: Label? = labelLayer?.addLabel(
            LabelOptions.from(routePoints.last())
                .setStyles(labelStyle)
                .setTexts(LabelTextBuilder().setTexts("도착"))
        )
    }

    // 전체 경로가 보이도록 카메라 조정하는 함수
    private fun moveCameraToFitRoute(routePoints: List<LatLng>) {
        // todo 카메라 잘 움직이기
    }


}