package com.example.sfbra_system_android.ui.fragments

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.telephony.SmsManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.example.sfbra_system_android.data.BluetoothLEManager
import com.example.sfbra_system_android.data.viewmodels.BluetoothViewModel
import com.example.sfbra_system_android.ui.activities.MainActivity
import com.example.sfbra_system_android.R
import com.example.sfbra_system_android.data.services.LocationPoint
import com.example.sfbra_system_android.data.services.PathRecord
import com.example.sfbra_system_android.data.viewmodels.PathRecordRouteViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapOverlay
import com.kakao.vectormap.MapView
import com.kakao.vectormap.camera.CameraAnimation
import com.kakao.vectormap.camera.CameraUpdate
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.route.RouteLine
import com.kakao.vectormap.route.RouteLineManager
import com.kakao.vectormap.route.RouteLineOptions
import com.kakao.vectormap.route.RouteLineSegment
import com.kakao.vectormap.route.RouteLineStyle
import com.kakao.vectormap.route.RouteLineStyles
import kotlinx.coroutines.Job
import org.json.JSONObject
import kotlinx.coroutines.*
import kotlinx.coroutines.Runnable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// 홈 화면
class HomeFragment : Fragment() {

    private lateinit var mapView: MapView
    private var kakaoMap: KakaoMap? = null
    private lateinit var connectButton: Button
    private lateinit var startButton: Button
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private lateinit var fusedLocationClient: FusedLocationProviderClient // 위치 클라이언트
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private var isBluetoothConnected = false // 블루투스 연결 상태
    private lateinit var warningText: TextView // 후방 위험 문구
    private lateinit var speedText: TextView // 속도 텍스트
    private var isDriving = false // 주행 상태
    private val receivedData = StringBuilder() // 데이터 누적을 위한 StringBuilder
    private var blinkJob: Job? = null  // 위험 문구 깜빡임 효과를 위한 Job
    private lateinit var bluetoothViewModel: BluetoothViewModel // 블루투스 뷰 모델
    private var isBicycleLock = false // 자전거 잠금 상태
    private var lockTiltValue: Double? = null
    private val REQUEST_SMS_PERMISSION = 101
    private var emergencyNumber = "01025376247" // 긴급 메시지를 보낼 전화번호 (테스트용 개발자 폰번호)
    private var emergencyMessage = "사용자에게 긴급 상황이 발생했습니다." // 보낼 메시지 내용
    private var isAccident = false // 사고 발생 유무
    private var route: List<LocationPoint>? = null // 좌표 리스트
    private var warningStatus: Int = 0 // 위험 요소 상태
    private val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()) // 시간 형식 포맷
    private var startTime: String? = null // 시작 시간 기록용 변수
    private var endTime: String? = null // 종료 시간 기록용 변수
    private val pathRecordRouteViewModel: PathRecordRouteViewModel by viewModels() // 기록 업로드용 뷰 모델

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // 긴급 연락처 임시로 개발자 번호 사용
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val choice = sharedPrefs.getString("emergency_number_choice", "01025376247")
        val customNumber = sharedPrefs.getString("custom_emergency_number", "")
        emergencyNumber = if (choice == "custom" && !customNumber.isNullOrEmpty()) {
            customNumber
        } else {
            choice ?: "01025376247"
        }

        warningText = view.findViewById(R.id.warning_text) // 텍스트 뷰 id로 매칭
        speedText = view.findViewById(R.id.speed_text) // 속도 텍스트
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext()) // 위치 서비스 초기화
        mapView = view.findViewById(R.id.map_view) // 맵뷰
        bluetoothViewModel = ViewModelProvider(requireActivity()).get(BluetoothViewModel::class.java)

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
                this@HomeFragment.kakaoMap = kakaoMap // 카카오맵 객체 저장(프래그먼트 내에서 사용할 수 있도록)
                Log.d("MapReady", "카카오 맵 로드 완료!")

                // 기본 시작 위치(한국공대)
                val initialPosition = LatLng.from(37.340179, 126.733591)
                val cameraUpdate: CameraUpdate = CameraUpdateFactory.newCenterPosition(initialPosition, 16)
                kakaoMap.moveCamera(cameraUpdate) // 카메라 이동
                kakaoMap.showOverlay(MapOverlay.BICYCLE_ROAD)

                val addressText = getAddressFromLocation(37.340179, 126.733591) // 초기화 좌표
                (activity as? MainActivity)?.setTitleFromLocation(addressText) // 액션바 타이틀 변경
            }
        })

        // 프래그먼트 시작 시 메시지 권한 미리 요청
        if (!checkSmsPermission(requireContext())) {
            requestSmsPermission(requireContext())
        }

        connectButton = view.findViewById(R.id.connect_button) // 연결버튼
        connectButton.setOnClickListener {
            if (isBluetoothConnected) {
                // 연결해제버튼 상태일 때 클릭 시 블루투스 연결 해제
                showDisconnectDialog() // 연결 해제 팝업 함수
            } else {
                // 연결버튼 클릭 시 블루투스 연결
                connectBluetooth()
            }
        }

        startButton = view.findViewById(R.id.start_button) // 주행시작 버튼
        startButton.setOnClickListener {
            // 주행시작 or 주행종료 버튼 클릭 시
            if (!isBluetoothConnected) {
                Toast.makeText(requireContext(), "블루투스 연결이 필요합니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener // 블루투스 연결 안 됐으면 실행 안 함
            }

            if (isDriving) {
                // 주행 종료
                stopDriving()
            } else {
                // 주행 중이 아니면 주행 시작, 주행 시작 전에 GPS 및 권한 확인
                checkGPSAndRequestPermission()
            }
        }

        // 위치 요청 설정
        locationRequest = LocationRequest.create().apply {
            interval = 5000 // 위치 갱신 주기
            fastestInterval = 2000
            priority = Priority.PRIORITY_HIGH_ACCURACY
        }

        // 위치 콜백 정의
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location = locationResult.lastLocation
                if (location != null && isDriving) {
                    updateCurrentLocation(location)
                }
            }
        }

        // 테스트 버튼
        val testButton = view.findViewById<Button>(R.id.test_button)
        testButton.setOnClickListener {
            isBluetoothConnected = true
            testButton.visibility = View.GONE
        }

        val testButton2 = view.findViewById<Button>(R.id.test_button2)
        testButton2.setOnClickListener {
            showAccidentAlert(requireContext())
            testButton2.visibility = View.GONE
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pathRecordRouteViewModel.pathRecordUploadResponse.observe(viewLifecycleOwner, Observer { response ->
            if (response != null && response.success) {
                Toast.makeText(requireContext(), "주행 기록을 성공적으로 전송했습니다.", Toast.LENGTH_SHORT).show()

                route = null // 좌표 리스트 초기화
                warningStatus = 0 // 위험 요소 초기화
                startTime = null
                endTime = null // 출발, 도착 시간 초기화
            } else {
                AlertDialog.Builder(requireContext())
                    .setTitle("전송 실패")
                    .setMessage("주행 기록 전송에 실패했습니다.\n다시 시도하시겠습니까?")
                    .setPositiveButton("재시도") { _, _ ->
                        postPathRecord(startTime!!, endTime!!, route!!)
                    }
                    .setNegativeButton("취소") { dialog, _ ->
                        route = null // 좌표 리스트 초기화
                        warningStatus = 0 // 위험 요소 초기화
                        startTime = null
                        endTime = null // 출발, 도착 시간 초기화
                        dialog.dismiss() // 다이얼로그 닫기
                    }
                    .show()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        mapView.resume() // 오류 방지를 위한 resume()

        // 위치 갱신 시작
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        }
    }

    override fun onPause() {
        super.onPause()
        mapView.pause() // 오류 방지를 위한 pause()

        // 위치 갱신 중지
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("HomeFragment", "홈 프래그먼트가 종료되었습니다.")
        BluetoothLEManager.disconnect(requireContext())
    }

    // 연결 버튼 텍스트 변경 함수
    private fun updateConnectButton() {
        connectButton.text = if (isBluetoothConnected) "연결 해제" else "연결"
    }

    // 블루투스 연결 해제 함수
    private fun disconnectBluetooth() {
        BluetoothLEManager.disconnect(requireContext())
        Toast.makeText(requireContext(), "장치 연결 해제", Toast.LENGTH_SHORT).show()
        isBluetoothConnected = false
        (activity as? MainActivity)?.setBluetoothConnectionState(isBluetoothConnected) // 메인 액티비티로 상태 업데이트
        updateConnectButton()
    }

    // 블루투스 연결 해제 팝업 함수
    private fun showDisconnectDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("블루투스 연결 해제")
            .setMessage("장치와의 블루투스 연결을 해제하시겠습니까?")
            .setPositiveButton("예") { _, _ ->
                disconnectBluetooth()  // 연결 해제
            }
            .setNegativeButton("아니요", null) // 아무 동작 없이 닫힘
            .show()
    }

    // 블루투스 연결 함수
    private fun connectBluetooth() {
        try {
            if (!BluetoothLEManager.initialize(requireContext())) {
                // 해당 기기가 블루투스를 지원하지 않는 경우
                Toast.makeText(requireContext(), "블루투스를 지원하지 않는 기기입니다.", Toast.LENGTH_SHORT).show()
                return
            }

            if (!checkBluetoothPermissions()) {
                // 앱의 블루투스 권한이 없는 경우
                requestBluetoothPermissions()
                return
            }

            if (bluetoothAdapter?.isEnabled == false) {
                // 블루투스가 꺼져 있으면 활성화 요청
                Toast.makeText(requireContext(), "블루투스가 비활성화 되어 있습니다.", Toast.LENGTH_SHORT).show()
                return
            }

            BluetoothLEManager.startScan(requireContext()) { device ->
                Toast.makeText(requireContext(), "BLE 장치 발견: ${device.name}", Toast.LENGTH_SHORT)
                    .show()

                BluetoothLEManager.connectToDevice(requireContext(), device,
                    onConnected = {
                        Log.d("BluetoothLE", "장치 연결 성공!")
                        requireActivity().runOnUiThread {
                            Toast.makeText(requireActivity(), "장치 연결 성공!", Toast.LENGTH_SHORT)
                                .show()
                            isBluetoothConnected = true
                            (activity as? MainActivity)?.setBluetoothConnectionState(
                                isBluetoothConnected
                            ) // 메인 액티비티로 상태 업데이트
                            updateConnectButton()
                        }
                    },
                    onDataReceived = { data ->
                        Log.d("BluetoothLEMassage", "$data")

                        // 블루투스 데이터 수신 시 처리 함수
                        handleBluetoothData(data)
                    }
                )
            }
        } catch (e: SecurityException) {
            // 예외 발생 시
            Log.e("Bluetooth", "블루투스 연결 중 예외 발생: ${e.message}")
            Toast.makeText(requireContext(), "블루투스 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
    }

    // 블루투스 권한 체크
    private fun checkBluetoothPermissions(): Boolean {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
            } else {
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            }
        } catch (e: SecurityException) {
            Log.e("Bluetooth", "권한 체크 중 예외 발생: ${e.message}")
            false
        }
    }

    // 블루투스 권한 요청
    private fun requestBluetoothPermissions() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.BLUETOOTH_SCAN,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ), 1001
                )
            } else {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ), 1001
                )
            }
        } catch (e: Exception) {
            Log.e("Bluetooth", "권한 요청 중 예외 발생: ${e.message}")
        }
    }

    // GPS 권한 체크 및 권한 요청 + 주행 시작
    private fun checkGPSAndRequestPermission() {
        val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (!requireContext().packageManager.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS)) {
            // GPS를 지원하지 않는 기기의 경우
            Toast.makeText(requireContext(), "이 기기는 GPS를 지원하지 않습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (!isGPSEnabled) {
            // GPS가 비활성화 되어 있는 경우
            Toast.makeText(requireContext(), "GPS가 비활성화되어 있습니다. 활성화 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        // 위치 권한 확인
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            // 권한 없는 경우 요청
            requestLocationPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
            return
        }

        // 모든 조건 충족 시 주행 시작
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())

        startDriving()
        return
    }

    // 위치 권한 요청 콜백
    private val requestLocationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                startDriving() // 권한 승인 시 주행 시작
            } else {
                Toast.makeText(requireContext(), "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }

    // 주행 시작 함수
    private fun startDriving() {
        startTime = getCurrentTime()
        route = emptyList()
        startButton.text = getString(R.string.finish_drive)
        isDriving = true
        speedText.visibility = View.VISIBLE // 속도 텍스트 표시
        Toast.makeText(requireContext(), "GPS 확인 완료. 주행을 시작합니다.", Toast.LENGTH_SHORT).show()
    }

    // 현재 위치 업데이트, 지도 수정 함수
    private fun updateCurrentLocation(location: Location) {
        val addressText = getAddressFromLocation(location.latitude, location.longitude) // 현재 위치 주소
        (activity as? MainActivity)?.setTitleFromLocation(addressText) // 액션바 타이틀 수정
        updateRoute(location.latitude, location.longitude, warningStatus) // 경로 리스트에 추가
        updateRouteLine(route!!) // 경로 그림 업데이트
        warningStatus = 0 // 위험 요소 초기화

        val currentLatLng = LatLng.from(location.latitude, location.longitude)
        val cameraUpdate = CameraUpdateFactory.newCenterPosition(currentLatLng, 16)
        kakaoMap?.moveCamera(cameraUpdate, CameraAnimation.from(1000, true, true))
    }

    private fun updateRouteLine(routePoints: List<LocationPoint>) {
        // 경로가 최소 2개 이상이어야 그리기
        if (routePoints.size < 2) return

        val routeLineManager: RouteLineManager = kakaoMap?.getRouteLineManager()!!
        val routeLineLayer = routeLineManager.getLayer()

        routeLineLayer.removeAll() // 기존 경로 삭제

        val blueStyle = RouteLineStyle.from(12.0f, Color.BLUE) // 기본 스타일
        val yellowStyle = RouteLineStyle.from(12.0f, Color.YELLOW) // 후방경고 스타일
        val redStyle = RouteLineStyle.from(12.0f, Color.RED) // 사고발생 스타일

        // 스타일 추가
        val blueStyles = RouteLineStyles.from(blueStyle)
        val yellowStyles = RouteLineStyles.from(yellowStyle)
        val redStyles = RouteLineStyles.from(redStyle)

        val segments = mutableListOf<RouteLineSegment>()

        for (i in 1 until routePoints.size) {
            val prev = routePoints[i - 1]
            val curr = routePoints[i]
            // LocationPoint -> LatLng으로 변경
            val segmentPoints = listOf(
                LatLng.from(prev.latitude, prev.longitude),
                LatLng.from(curr.latitude, curr.longitude)
            )

            // 경고에 따라 스타일 변경
            val style = when (curr.warning) {
                1 -> yellowStyles
                2 -> redStyles
                else -> blueStyles
            }

            segments.add(RouteLineSegment.from(segmentPoints, style))
        }

        // 하나의 RouteLine에 여러 Segment를 적용
        val routeLine: RouteLine = routeLineLayer.addRouteLine(
            RouteLineOptions.from(segments)
        )
    }

    // 경로 리스트 갱신 함수
    private fun updateRoute(latitude: Double, longitude: Double, warning: Int) {
        val locationPoint = LocationPoint(latitude, longitude, warning)
        route = route?.plus(locationPoint) ?: listOf(locationPoint)
    }

    // 좌표값 -> 주소값 변환 함수
    private fun getAddressFromLocation(latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        return try {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses?.isNotEmpty() == true) {
                addresses[0].getAddressLine(0) // 전체 주소
            } else {
                "주소를 찾을 수 없음"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "주소를 가져올 수 없음"
        }
    }

    // 블루투스 데이터 처리 함수
    private fun handleBluetoothData(data: String) {
        try {
            // 수신된 데이터가 JSON 형식이 아닐 경우 여러 조각을 하나로 합침
            val cleanedData = receivedData.append(data) // 수신된 데이터를 계속 누적

            // 유효한 JSON 형식을 유지할 수 있도록 중간 데이터 처리
            if (cleanedData.contains("}")) {
                // 중간에 잘린 JSON을 찾아서 조합
                val validJsonData = cleanedData.toString().substringBeforeLast("}") + "}"
                receivedData.clear() // 이미 처리한 데이터는 지우고

                // JSON 형식으로 파싱
                val jsonObject = JSONObject(validJsonData)
                Log.d("BluetoothLE", "수신된 데이터: $validJsonData") // 로깅 추가

                // 주행 중일 때만 작동
                if (isDriving) {
                    // 2미터 이내 후방 경고
                    if (jsonObject.has("WARNING")) {
                        val warningValue = jsonObject.getDouble("WARNING")
                        Log.d("WarningMessage", "$warningValue")

                        // 0이면 경고 숨김, 그 외에는 경고 표시
                        requireActivity().runOnUiThread {
                            if (warningValue == 0.0) {
                                blinkJob?.cancel()
                                warningText.visibility = View.GONE
                            } else {
                                warningStatus = 1 // 후방 주의 : 위험 요소 1
                                warningText.visibility = View.VISIBLE
                                startBlinkingWarningText()
                            }
                        }
                    }

                    // 사고 발생
                    if (jsonObject.has("ACCIDENT")) {
                        if (!isAccident) { // 사고 발생 팝업 중복 방지
                            val accidentValue = jsonObject.getDouble("ACCIDENT")
                            Log.d("CrashMessage", "$accidentValue")

                            requireActivity().runOnUiThread {
                                if (accidentValue == 1.0) {
                                    // 사고 관련 값 받을 시, 20초동안 반응 없을시 긴급 연락처로 메세지 발송
                                    isAccident = true
                                    warningStatus = 2 // 사고 발생 : 위험 요소 2
                                    showAccidentAlert(requireContext())
                                }
                            }
                        }
                    }

                    // 속도 표시 관련
                    if (jsonObject.has("SPEED")) {
                        val speedValue = jsonObject.getInt("SPEED")
                        Log.d("SpeedMessage", "$speedValue")

                        speedText.text = "$speedValue"
                    }
                }

                // 잠금 상태일 경우 움직임 감지
                isBicycleLock = (activity as? MainActivity)?.isBicycleLock ?: false // 잠금상태 받아오기
                if (isBicycleLock) {
                    // 잠금 상태에서 움직임 감지
                    if (jsonObject.has("TILT")) {
                        val tiltValue = jsonObject.getDouble("TILT")
                        Log.d("TiltMessage", "$tiltValue")

                        requireActivity().runOnUiThread {
                            // 잠금 상태에서 최초 틸트 값 저장
                            if (lockTiltValue == null) {
                                lockTiltValue = tiltValue
                            }

                            // 최초 값과 비교하여 ±15 이상 차이 나면 잠금 프래그먼트로 값 전달
                            if (lockTiltValue != null && kotlin.math.abs(tiltValue - lockTiltValue!!) >= 15.0) {
                                bluetoothViewModel.updateBluetoothData("DETECT")
                            }
                        }
                    }
                } else {
                    lockTiltValue = null // 잠금 해제되면 다시 틸트 값 초기화
                }
            }
        } catch (e: Exception) {
            Log.e("BluetoothLE", "JSON 파싱 오류: ${e.message}")
        }
    }

    // 텍스트 계속 깜빡이는 함수
    private fun startBlinkingWarningText() {
        blinkJob?.cancel() // 기존 Job이 있다면 취소
        blinkJob = CoroutineScope(Dispatchers.Main).launch {
            while (true) {
                warningText.alpha = 1f
                delay(500) // 0.5초 대기
                warningText.alpha = 0f
                delay(500) // 0.5초 대기

            }
        }
    }

    // 주행 종료 함수
    private fun stopDriving() {
        endTime = getCurrentTime()
        startButton.text = getString(R.string.start_drive)
        isDriving = false
        isAccident = false
        blinkJob?.cancel() // 깜빡임 중지
        //locationHandler.removeCallbacks(updateLocationRunnable) // 위치 업데이트 중지
        fusedLocationClient.removeLocationUpdates(locationCallback)
        warningText.visibility = View.GONE // 후방 알림 비활성화
        speedText.visibility = View.GONE // 속도 텍스트 비활성화

        // route가 비어있으면 서버 전송을 건너뛰거나, 빈 리스트로 보내기
        if (!route.isNullOrEmpty() && startTime != null) {
            postPathRecord(startTime!!, endTime!!, route!!) // 서버로 주행 데이터 전송
        }
        // 전송 성공 시 데이터 초기화
    }

    private fun getCurrentTime(): String = sdf.format(Date()) // 현재 시간 가져오는 함수

    // 서버로 주행 데이터 전송 함수
    private fun postPathRecord(startTime: String, endTime: String, route: List<LocationPoint>) {
        val pathRecord = PathRecord(startTime, endTime, route)
        Log.d("PostPathRecord","$startTime, $endTime")

        pathRecordRouteViewModel.postRecord(pathRecord)
    }

    // 긴급 상황 시 팝업 알림 함
    fun showAccidentAlert(context: Context) {
        val handler = Handler(Looper.getMainLooper()) // 카운트다운을 위한 핸들러
        var countdown = 20 // 20초 카운트다운, but 임시로 5초

        // 다이얼로그 생성
        val dialog = AlertDialog.Builder(context)
            .setTitle("사고 감지")
            .setMessage("긴급 연락처로 메시지가 전송됩니다.\n남은 시간: $countdown")
            .setCancelable(false) // 뒤로 가기 버튼으로 닫히지 않도록 설정
            .setNegativeButton("취소") { _, _ ->
                handler.removeCallbacksAndMessages(null) // 타이머 중단
                isAccident = false
                Toast.makeText(context, "메시지 전송이 취소되었습니다.", Toast.LENGTH_SHORT).show()
                stopDriving()
            }
            .create()
        dialog.show()

        // 1초 주기로 카운트다운 업데이트
        val countdownRunnable = object : Runnable {
            override fun run() {
                countdown--
                dialog.setMessage("긴급 연락처로 메시지가 전송됩니다.\n남은 시간: $countdown")

                if (countdown > 0) {
                    handler.postDelayed(this, 1000) // 1초 후 다시 실행
                } else {
                    dialog.dismiss()
                    if (checkSmsPermission(context)) {
                        sendEmergencyMessage(context)
                    } else {
                        Toast.makeText(context, "SMS 전송 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        handler.postDelayed(countdownRunnable, 1000) // 1초 후 실행 시작
    }

    // 문자메시지 권한 체크
    private fun checkSmsPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED
    }

    // 문자메시지 권한 허용 함수
    private fun requestSmsPermission(context: Context) {
        ActivityCompat.requestPermissions(context as Activity, arrayOf(Manifest.permission.SEND_SMS), REQUEST_SMS_PERMISSION)
    }

    // 권한 요청 결과 처리
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_SMS_PERMISSION) {
            if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(requireContext(), "SMS 전송 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 긴급 메시지 전송 함수
    fun sendEmergencyMessage(context: Context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            return
        }

        // SMS 전송
        val smsManager = SmsManager.getDefault()
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val latitude = location.latitude
                val longitude = location.longitude
                val addressText = getAddressFromLocation(latitude, longitude) // 주소로 변환

                emergencyMessage = """
                (테스트) 사용자에게 긴급 상황이 발생했습니다.
                현재 위치:
                - 주소: $addressText
                - 좌표: $latitude, $longitude
                """.trimIndent() // todo 추후 테스트 삭제

                Log.d("EmergencyMessage", "전송할 메시지: $emergencyMessage")
                val messageParts = smsManager.divideMessage(emergencyMessage) // 메시지 분할
                smsManager.sendMultipartTextMessage(emergencyNumber, null, messageParts, null, null)

                Toast.makeText(context, "긴급 메시지를 전송했습니다.", Toast.LENGTH_LONG).show()
            } else {
                smsManager.sendTextMessage(emergencyNumber, null, emergencyMessage, null, null)
                Toast.makeText(context, "현재 위치를 제외한 메시지를 전송했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
        stopDriving()
    }
}
