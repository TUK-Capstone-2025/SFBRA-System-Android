package com.example.sfbra_system_android

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.*
import android.bluetooth.le.*
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import java.util.*

object BluetoothLEManager {
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var bluetoothGatt: BluetoothGatt? = null
    private var scanCallback: ScanCallback? = null
    private var isScanning = false
    private val handler = Handler(Looper.getMainLooper())

    private val SERVICE_UUID = UUID.fromString("0000FFE0-0000-1000-8000-00805F9B34FB")
    private val CHARACTERISTIC_UUID = UUID.fromString("0000FFE1-0000-1000-8000-00805F9B34FB")

    // 블루투스 초기화 함수
    @SuppressLint("ServiceCast")
    fun initialize(context: Context): Boolean {
        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
        return bluetoothAdapter != null
    }

    // BLE 스캔 시작 함수
    @SuppressLint("MissingPermission")
    fun startScan(context: Context, onDeviceFound: (BluetoothDevice) -> Unit) {
        if (!hasBluetoothPermissions(context)) {
            Log.e("BluetoothLE", "블루투스 권한이 없습니다.")
            return
        }

        if (isScanning || bluetoothAdapter == null) return

        val scanner = bluetoothAdapter!!.bluetoothLeScanner
        if (scanner == null) {
            Log.e("BluetoothLE", "블루투스가 비활성화되어 스캔할 수 없습니다.")
            return
        }

        var isDeviceFound = false
        val scanFilters = listOf(ScanFilter.Builder().build())
        val scanSettings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()

        scanCallback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult?) {
                result?.device?.let { device ->
                    if (device.name?.startsWith("BicycleBT") == true) {
                        isDeviceFound = true
                        stopScan(context)
                        onDeviceFound(device)
                    }
                }
            }

            override fun onScanFailed(errorCode: Int) {
                Log.e("BluetoothLE", "BLE 스캔 실패: $errorCode")
            }
        }

        scanner.startScan(scanFilters, scanSettings, scanCallback)
        isScanning = true

        handler.postDelayed({
            stopScan(context)
            if (!isDeviceFound) {
                Toast.makeText(context, "장치를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }, 3000)
    }

    // BLE 스캔 중지 함수
    @SuppressLint("MissingPermission")
    fun stopScan(context: Context) {
        if (!hasBluetoothPermissions(context)) {
            Log.e("BluetoothLE", "블루투스 권한이 없습니다.")
            return
        }

        if (isScanning && bluetoothAdapter != null) {
            bluetoothAdapter!!.bluetoothLeScanner.stopScan(scanCallback)
            isScanning = false
        }
    }

    // BLE 장치 연결 함수
    @SuppressLint("MissingPermission")
    fun connectToDevice(
        context: Context,
        device: BluetoothDevice,
        onConnected: () -> Unit,
        onDataReceived: (String) -> Unit // 변경된 부분: 문자열을 넘겨줌
    ) {
        if (!hasBluetoothPermissions(context)) {
            Log.e("BluetoothLE", "블루투스 권한이 없습니다.")
            return
        }

        bluetoothGatt = device.connectGatt(context, false, object : BluetoothGattCallback() {
            override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    Log.d("BluetoothLE", "BLE 연결 성공")
                    gatt?.discoverServices()
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    Log.d("BluetoothLE", "BLE 연결 해제됨")
                    disconnect(context)
                }
            }

            override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    val characteristic = gatt?.getService(SERVICE_UUID)?.getCharacteristic(CHARACTERISTIC_UUID)
                    characteristic?.let {
                        gatt.setCharacteristicNotification(it, true)
                        Log.d("BluetoothLE", "BLE 감지됨")
                        onConnected()
                    }
                }
            }

            override fun onCharacteristicChanged(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?) {
                characteristic?.value?.let {
                    val receivedDataChunk = String(it)

                    // 문자열 데이터 수신 처리
                    onDataReceived(receivedDataChunk)
                }
            }
        })
    }

    // BLE 연결 해제 함수
    @SuppressLint("MissingPermission")
    fun disconnect(context: Context) {
        if (!hasBluetoothPermissions(context)) {
            Log.e("BluetoothLE", "블루투스 권한이 없습니다.")
            return
        }

        bluetoothGatt?.close()
        bluetoothGatt = null
    }

    // 블루투스 권한 확인 함수
    private fun hasBluetoothPermissions(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        }
    }
}
