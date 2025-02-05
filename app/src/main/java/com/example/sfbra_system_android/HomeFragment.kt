package com.example.sfbra_system_android

import android.Manifest
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView

class HomeFragment : Fragment() {

    private lateinit var mapView: MapView
    private lateinit var connectButton: Button
    private lateinit var startButton: Button
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
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
                Log.d("MapReady", "카카오 맵 로드 완료!")
            }
        })

        connectButton = view.findViewById(R.id.connectButton) // 연결버튼
        connectButton.setOnClickListener {
            // 연결버튼 클릭 시 블루투스 연결
            connectBluetooth()
        }

        startButton = view.findViewById(R.id.startButton) // 주행시작 버튼
        startButton.setOnClickListener {
            // 주행시작 버튼 클릭 시 주행시작
            checkGPSAndRequestPermission()
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

    // 블루투스 연결 함수
    private fun connectBluetooth() {
        if (bluetoothAdapter == null) {
            // 블루투스를 지원하지 않는 기기의 경우
            Toast.makeText(requireContext(), "이 기기는 블루투스를 지원하지 않습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        if (!bluetoothAdapter.isEnabled) {
            // 블루투스가 비활성화 되어있는 경우
            Toast.makeText(requireContext(), "블루투스를 활성화해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        // 블루투스 권한 확인 후 실행
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {  // Android 12 이상
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_CONNECT)
                != PackageManager.PERMISSION_GRANTED) {
                // 블루투스 권한이 없는 경우 권한 요청
                requestBluetoothPermission.launch(Manifest.permission.BLUETOOTH_CONNECT)
                return
            }
        }

        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter.bondedDevices
        if (pairedDevices.isNullOrEmpty()) {
            // 페어링된 장치가 없는 경우
            Toast.makeText(requireContext(), "페어링된 블루투스 장치가 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        // "Bicycle_Arduino"(아두이노 블루투스 장치명)로 시작하는 장치만 필터링
        val arduinoDevices = pairedDevices.filter { it.name.startsWith("Bicycle_Arduino") }

        if (arduinoDevices.isEmpty()) {
            // 아두이노 장치가 없는 경우
            Toast.makeText(requireContext(), "연결 가능한 제품이 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val deviceList = arduinoDevices.map { it.name to it.address }.toTypedArray()
        val deviceNames = deviceList.map { it.first }.toTypedArray()

        // 아두이노 검색 시 장치 선택 다이얼로그 표시
        AlertDialog.Builder(requireContext())
            .setTitle("연결할 장치를 선택해 주세요.")
            .setItems(deviceNames) { _, which ->
                val selectedDevice = deviceList[which]
                connectToSelectedDevice(selectedDevice.second)
            }
            .setNegativeButton("취소", null)
            .show()
    }

    // 선택한 장치와 연결
    private fun connectToSelectedDevice(deviceAddress: String) {
        val intent = Intent(requireContext(), BluetoothService::class.java)
        intent.putExtra("DEVICE_ADDRESS", deviceAddress)
        requireContext().startService(intent)

        Toast.makeText(requireContext(), "블루투스 연결 시도 중...", Toast.LENGTH_SHORT).show()
    }

    // 블루투스 권한 요청 콜백 변수
    private val requestBluetoothPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // 권한이 허용되었을 때 블루투스 연결을 시도
                connectBluetooth()
            } else {
                // 권한이 거부되었을 때 처리
                Toast.makeText(requireContext(), "블루투스 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }


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
        startDriving()
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

    // 주행 시작 함수 (나중에 GPS 수신 로직 추가 예정)
    private fun startDriving() {
        Toast.makeText(requireContext(), "GPS 확인 완료. 주행을 시작합니다.", Toast.LENGTH_SHORT).show()
    }
}
