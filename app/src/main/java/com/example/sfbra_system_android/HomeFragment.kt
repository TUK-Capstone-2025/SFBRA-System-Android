package com.example.sfbra_system_android

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView
import com.kakao.vectormap.camera.CameraAnimation
import com.kakao.vectormap.camera.CameraUpdate
import com.kakao.vectormap.camera.CameraUpdateFactory
import kotlinx.coroutines.Job
import org.json.JSONObject
import kotlinx.coroutines.*

// 홈 화면
class HomeFragment : Fragment() {

    private lateinit var mapView: MapView
    private var kakaoMap: KakaoMap? = null
    private lateinit var connectButton: Button
    private lateinit var startButton: Button
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private lateinit var fusedLocationClient: FusedLocationProviderClient // 위치 클라이언트
    private var isBluetoothConnected = false // 블루투스 연결 상태
    private lateinit var warningText: TextView // 후방 위험 문구
    private var isDriving = false // 주행 상태
    private val receivedData = StringBuilder() // 데이터 누적을 위한 StringBuilder
    private var blinkJob: Job? = null  // 깜빡임 효과를 위한 Job

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        warningText = view.findViewById(R.id.warningText) // 텍스트 뷰 id로 매칭

        // 위치 서비스 초기화
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        mapView = view.findViewById(R.id.mapView) // 맵뷰

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
                val cameraUpdate: CameraUpdate = CameraUpdateFactory.newCenterPosition(initialPosition)
                kakaoMap.moveCamera(cameraUpdate) // 카메라 이동
            }
        })

        connectButton = view.findViewById(R.id.connectButton) // 연결버튼
        connectButton.setOnClickListener {
            if (isBluetoothConnected) {
                // 연결해제버튼 상태일 때 클릭 시 블루투스 연결 해제
                showDisconnectDialog() // 연결 해제 팝업 함수
            } else {
                // 연결버튼 클릭 시 블루투스 연결
                connectBluetooth()
            }
        }

        startButton = view.findViewById(R.id.startButton) // 주행시작 버튼

        startButton.setOnClickListener {
            // 주행시작 or 주행종료 버튼 클릭 시
            if (!isBluetoothConnected) {
                Toast.makeText(requireContext(), "블루투스 연결이 필요합니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener // 블루투스 연결 안 됐으면 실행 안 함
            }

            if (isDriving) {
                startButton.text = "주행시작"
                isDriving = false
                blinkJob?.cancel()
                warningText.visibility = View.GONE
            } else {
                // 주행 중이 아니면 주행 시작
                // 주행 시작 전에 GPS 및 권한 확인
                checkGPSAndRequestPermission()
            }
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        mapView.resume() // 오류 방지를 위한 resume()
    }

    override fun onPause() {
        super.onPause()
        mapView.pause() // 오류 방지를 위한 pause()
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
        updateConnectButton()
    }

    private fun showDisconnectDialog() {
        val builder = android.app.AlertDialog.Builder(requireContext())
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
                Toast.makeText(requireContext(), "BLE 장치 발견: ${device.name}", Toast.LENGTH_SHORT).show()

                BluetoothLEManager.connectToDevice(requireContext(), device,
                    onConnected = {
                        Log.d("BluetoothLE", "장치 연결 성공!")
                        requireActivity().runOnUiThread {
                            Toast.makeText(requireActivity(), "장치 연결 성공!", Toast.LENGTH_SHORT).show()
                            isBluetoothConnected = true
                            updateConnectButton()
                        }
                    },
                    onDataReceived = { data ->
                        Log.d("BluetoothLEMassage", "$data")

                        // 주행중일 때만 데이터 처리
                        if (isDriving) {
                            handleBluetoothData(data)
                        }
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

    // GPS 권한 체크 및 권한 요청
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
            return
        }

        // 모든 조건 충족 시 주행 시작
        startButton.text = "주행종료"
        isDriving = true
        startDriving()
        return
    }

    // 위치 권한 요청 콜백
    private val requestLocationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                startButton.text = "주행종료"
                isDriving = true
                startDriving() // 권한 승인 시 주행 시작
            } else {
                Toast.makeText(requireContext(), "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }

    // 주행 시작 함수 (나중에 GPS 수신 로직 추가 예정)
    private fun startDriving() {
        updateCurrentLocation() // 현재 위치 업데이트
        Toast.makeText(requireContext(), "GPS 확인 완료. 주행을 시작합니다.", Toast.LENGTH_SHORT).show()
    }

    // 현재 위치 업데이트, 지도 수정 함수
    private fun updateCurrentLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return // 권한 없으면 실행 안 함
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                // 현재 위치 좌표 가져오기
                val currentLatLng = LatLng.from(location.latitude, location.longitude)

                // 카메라를 현재 위치로 이동(애니메이션 효과 포함)
                kakaoMap?.moveCamera(CameraUpdateFactory.newCenterPosition(currentLatLng), CameraAnimation.from(1000, true, true))
            } else {
                Toast.makeText(requireContext(), "현재 위치를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
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

                // "WARNING" 키가 있는 경우만 처리
                if (jsonObject.has("WARNING")) {
                    val warningValue = jsonObject.getDouble("WARNING")
                    Log.d("WarningMessage", "$warningValue")

                    // -1이면 경고 숨김, 그 외에는 경고 표시
                    requireActivity().runOnUiThread {
                        if (warningValue == -1.0) {
                            blinkJob?.cancel()
                            warningText.visibility = View.GONE
                        } else {
                            warningText.visibility = View.VISIBLE
                            startBlinkingWarningText()
                        }
                    }
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
}
