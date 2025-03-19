package com.example.sfbra_system_android.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

// 프래그먼트 간 데이터 공유를 위한 뷰 모델 클래스
class BluetoothViewModel : ViewModel() {
    private val _bluetoothData = MutableLiveData<String>()
    val bluetoothData: LiveData<String> get() = _bluetoothData // 블루투스 데이터 저장

    fun updateBluetoothData(data: String) {
        _bluetoothData.value = data
    }
}