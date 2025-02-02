package com.example.sfbra_system_android

import android.Manifest
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class HomeFragment : Fragment() {

    private lateinit var connectButton: Button
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        connectButton = view.findViewById(R.id.connectButton)

        connectButton.setOnClickListener {
            // 연결버튼 클릭 시 블루투스 연결
            connectBluetooth()
        }

        return view
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

    // 권한 요청 콜백 변수
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
}
