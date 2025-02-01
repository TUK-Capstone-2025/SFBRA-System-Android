package com.example.sfbra_system_android

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.IOException
import java.util.*

object BluetoothManager {
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private var bluetoothSocket: BluetoothSocket? = null
    private var device: BluetoothDevice? = null
    private val UUID_SPP = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    // 블루투스 권한을 확인하는 함수
    fun checkBluetoothPermission(context: Context): Boolean {
        val bluetoothPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.BLUETOOTH_CONNECT
        )
        val bluetoothAdminPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.BLUETOOTH_ADMIN
        )

        return bluetoothPermission == PackageManager.PERMISSION_GRANTED &&
                bluetoothAdminPermission == PackageManager.PERMISSION_GRANTED
    }

    // 권한 요청 함수
    fun requestBluetoothPermission(activity: android.app.Activity) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_ADMIN),
            1 // 요청 코드
        )
    }

    // 블루투스 장치에 연결하는 함수
    @SuppressLint("MissingPermission")
    fun connectToDevice(context: Context, deviceAddress: String): Boolean {
        // 권한이 있는지 확인
        if (!checkBluetoothPermission(context)) {
            // 권한이 없으면 권한을 요청
            requestBluetoothPermission(context as android.app.Activity)
            return false
        }

        try {
            device = bluetoothAdapter?.getRemoteDevice(deviceAddress)
            bluetoothSocket = device?.createRfcommSocketToServiceRecord(UUID_SPP)
            bluetoothSocket?.connect()
            return true
        } catch (e: IOException) {
            Log.e("BluetoothHelper", "연결 실패: ${e.message}")
            disconnect()
        }
        return false
    }

    // 블루투스 연결 종료 함수
    fun disconnect() {
        try {
            bluetoothSocket?.close()
            bluetoothSocket = null
        } catch (e: IOException) {
            Log.e("BluetoothHelper", "소켓 닫기 실패: ${e.message}")
        }
    }

    // 데이터 전송 함수
    fun sendData(data: String) {
        try {
            bluetoothSocket?.outputStream?.write(data.toByteArray())
        } catch (e: IOException) {
            Log.e("BluetoothHelper", "데이터 전송 실패: ${e.message}")
        }
    }
}
