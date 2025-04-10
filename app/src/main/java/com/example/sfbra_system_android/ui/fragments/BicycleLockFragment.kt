package com.example.sfbra_system_android.ui.fragments

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.sfbra_system_android.data.viewmodels.BluetoothViewModel
import com.example.sfbra_system_android.ui.activities.MainActivity
import com.example.sfbra_system_android.R

// 잠금 화면
class BicycleLockFragment : Fragment() {

    private lateinit var lockButton: ImageButton
    private lateinit var lockStatusText: TextView
    private lateinit var warningText: TextView
    private var isLock = false // 잠금 상태
    private lateinit var bluetoothViewModel: BluetoothViewModel // 블루투스 뷰 모델
    private var isBluetoothConnected = false // 블루투스 연결 상태
    private val handler = Handler(Looper.getMainLooper()) // 주기적 진동을 위한 UI 스레드 핸들러
    private lateinit var vibrator: Vibrator // 진동

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bicycle_lock, container, false)

        lockButton = view.findViewById(R.id.lock_button) as ImageButton
        lockStatusText = view.findViewById(R.id.lock_status_text) as TextView
        warningText = view.findViewById(R.id.warning_text) as TextView
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

        // 블루투스 데이터 변경(잠금 상태일 때 움직임 감지) 시 UI 업데이트
        bluetoothViewModel.bluetoothData.observe(viewLifecycleOwner) { data ->
            if (isLock) {
                if (data == "DETECT") {
                    contactNotification() // 자전거 움직임 시의 함수
                }
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
        (activity as? MainActivity)?.setBicycleLockState(isLock) // 홈 프래그먼트로 잠금 상태 전달
    }

    private fun unlockBicycle() {
        // 자물쇠 잠금 해제 함수
        lockButton.setImageResource(R.drawable.ic_unlock)
        lockButton.setBackgroundResource(R.drawable.circle_button_background)
        lockStatusText.text = "잠금해제 상태"
        warningText.visibility = View.INVISIBLE // 경고 문구 비활성화
        handler.removeCallbacks(vibrationRunnable) // 진동 중지

        isLock = false
        (activity as? MainActivity)?.setBicycleLockState(isLock) // 홈 프래그먼트로 잠금 해제 상태 전달
    }

    private val vibrationRunnable = object : Runnable {
        override fun run() {
            if (isLock) { // 잠겨있을 때만 실행
                vibrator.vibrate(VibrationEffect.createOneShot(400, VibrationEffect.DEFAULT_AMPLITUDE)) // 0.4초 진동
                handler.postDelayed(this, 1000) // 1초 후 다시 실행
            }
        }
    }

    // 움직임 발생 시의 함수
    private fun contactNotification() {
        warningText.visibility = View.VISIBLE // 경고 문구 표시
        vibrator = requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        // 처음 한 번 실행 후 1초 간격 진동 반복
        handler.post(vibrationRunnable)
    }
}