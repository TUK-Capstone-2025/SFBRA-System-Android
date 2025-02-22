package com.example.sfbra_system_android

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider

// 잠금 화면
class BicycleLockFragment : Fragment() {

    private lateinit var lockButton: ImageButton
    private lateinit var lockStatusText: TextView
    private var isLock = false // 잠금 상태
    private lateinit var bluetoothViewModel: BluetoothViewModel // 블루투스 뷰 모델
    private var isBluetoothConnected = false // 블루투스 연결 상태

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bicycle_lock, container, false)

        lockButton = view.findViewById(R.id.lock_button) as ImageButton
        lockStatusText = view.findViewById(R.id.lock_status_text) as TextView
        bluetoothViewModel = ViewModelProvider(requireActivity()).get(BluetoothViewModel::class.java)

        // 자물쇠 버튼 클릭 시
        lockButton.setOnClickListener {
            // 메인 액티비티에서 블루투스 연결 상태 가져오기
            isBluetoothConnected = (activity as? MainActivity)?.isBluetoothConnected ?: false

            // 블루투스 연결 안 됐으면 실행 안 함
            if (!isBluetoothConnected) {
                Toast.makeText(requireContext(), "블루투스 연결이 필요합니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (isLock) {
                unlockBicycle()
            } else {
                lockBicycle()
            }
        }

        // 블루투스 데이터 변경 시 UI 업데이트
        bluetoothViewModel.bluetoothData.observe(viewLifecycleOwner) { data ->
            if (data == "DETECT") {
                // todo 블루투스 데이터에 따른 UI 업데이트
                lockStatusText.text = "위험"
            }
        }

        return view
    }

    private fun lockBicycle() {
        // 자물쇠 잠금 함수
        lockButton.setImageResource(R.drawable.ic_lock)
        lockButton.setBackgroundResource(R.drawable.circle_button_background_red)
        lockStatusText.text = "잠금 상태"

        isLock = true
    }

    private fun unlockBicycle() {
        // 자물쇠 잠금 해제 함수
        lockButton.setImageResource(R.drawable.ic_unlock)
        lockButton.setBackgroundResource(R.drawable.circle_button_background)
        lockStatusText.text = "잠금해제 상태"

        isLock = false
    }
}