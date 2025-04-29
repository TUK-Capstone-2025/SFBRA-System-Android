package com.example.sfbra_system_android.ui.activities

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.sfbra_system_android.R
import com.example.sfbra_system_android.data.services.LocationPoint
import com.example.sfbra_system_android.data.viewmodels.PathRecordRouteViewModel
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
    private val pathRecordRouteViewModel: PathRecordRouteViewModel by viewModels() // 기록 불러오기용 뷰모델

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPathViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "주행 기록"

        setupActionBar()

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
                val initialPosition = LatLng.from(37.340179, 126.733591) // 시작 포인트
                val cameraUpdate: CameraUpdate = CameraUpdateFactory.newCenterPosition(initialPosition, 16)
                kakaoMap.moveCamera(cameraUpdate) // 카메라 이동
                kakaoMap.showOverlay(MapOverlay.BICYCLE_ROAD)

                val recordId = intent.getIntExtra("recordId", 0)
                Log.d("PathViewActivity", "recordId: $recordId")
                val memberId = intent.getIntExtra("memberId", -1)
                Log.d("PathViewActivity", "memberId: $memberId")

                if (memberId != -1) {
                    getMemberRecordRoute(recordId)
                } else {
                    getRecordRoute(recordId)
                }

            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed() // 또는 finish()(종료)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // 액션바 색깔 수정 함수
    private fun setupActionBar() {
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setBackgroundDrawable(ContextCompat.getDrawable(this@PathViewActivity, R.color.my_primary))

            val titleText = SpannableString(title ?: "주행기록")
            titleText.setSpan(ForegroundColorSpan(Color.WHITE), 0, titleText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            title = titleText

            // 뒤로가기 아이콘 색상 변경
            val upArrow = ContextCompat.getDrawable(this@PathViewActivity, R.drawable.ic_arrow_back)
            upArrow?.setTint(Color.WHITE)
            setHomeAsUpIndicator(upArrow)
        }

        // API 레벨 26 이상에서만 실행
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 시스템 내비게이션 바 버튼 색상 변경
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        }
    }

    // 경로 가져오기 함수
    private fun getRecordRoute(recordId: Int) {
        pathRecordRouteViewModel.getRecordRoute(recordId)

        pathRecordRouteViewModel.pathRecordRoute.observe(this) { response ->
            if (response != null && response.success) {
                val startTime = response.data.startTime
                val endTime = response.data.endTime
                val route = response.data.route

                val routePoints = setMinimumRoutePoints(getRouteToLatLng(route))
                drawRouteLine(routePoints)
                addStartEndLabels(routePoints)
                changeZoomLevel(routePoints)

                // 시간 ui에 띄우기
                binding.startTime.text = startTime
                binding.endTime.text = endTime
            } else {
                // 불러오기 실패
                Toast.makeText(this,"좌표 목록 불러오기 실패", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 팀원 경로 가져오기 함수
    private fun getMemberRecordRoute(recordId: Int) {
        pathRecordRouteViewModel.getMemberRecordRoute(recordId)

        pathRecordRouteViewModel.pathRecordRoute.observe(this) { response ->
            if (response != null && response.success) {
                val startTime = response.data.startTime
                val endTime = response.data.endTime
                val route = response.data.route

                val routePoints = setMinimumRoutePoints(getRouteToLatLng(route))
                drawRouteLine(routePoints)
                addStartEndLabels(routePoints)
                changeZoomLevel(routePoints)

                // 시간 ui에 띄우기
                binding.startTime.text = startTime
                binding.endTime.text = endTime
            } else {
                // 불러오기 실패
                Toast.makeText(this,"좌표 목록 불러오기 실패", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 좌표 개수 최소값 맞춰주는 함수
    private fun setMinimumRoutePoints(points: List<LatLng>): List<LatLng> {
        return when {
            points.size >= 2 -> points
            points.size == 1 -> listOf(points[0], points[0]) // 동일 좌표 두 번
            else -> emptyList()
        }
    }

    // LocationPoint 리스트를 LatLng 리스트로 변환하는 함수
    private fun getRouteToLatLng(route: List<LocationPoint>): List<LatLng> {
        return route.map { LatLng.from(it.latitude, it.longitude) }
    }

    // 경로에 맞춰 줌 레벨 변경, 카메라 이동
    fun changeZoomLevel(points: List<LatLng>) {
        val boundsBuilder = LatLngBounds.Builder()
        for (point in points) {
            boundsBuilder.include(point)
        }
        val bounds = boundsBuilder.build()

        val padding = 200 // px 단위의 패딩 설정
        val cameraUpdate = CameraUpdateFactory.fitMapPoints(bounds, padding)
        kakaoMap?.moveCamera(cameraUpdate) // 줌 변경, 카메라 이동
    }

    // 카카오맵의 RouteLine으로 경로 그리기 함수
    private fun drawRouteLine(routePoints: List<LatLng>) {
        val routeLineManager: RouteLineManager = kakaoMap?.getRouteLineManager()!! // 경로 매니저
        val routeLineLayer = routeLineManager.getLayer() // 레이어

        val styles = RouteLineStyles.from(RouteLineStyle.from(12.0f, Color.BLUE)) // 스타일
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
}