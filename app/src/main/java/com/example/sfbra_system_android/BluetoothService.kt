package com.example.sfbra_system_android

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat

class BluetoothService : Service() {

    override fun onCreate() {
        super.onCreate()
        startForeground(1, createNotification("블루투스 연결 대기 중"))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val deviceAddress = intent?.getStringExtra("DEVICE_ADDRESS") ?: return START_NOT_STICKY
        // BluetoothManager.connectToDevice() 호출 시 Context와 deviceAddress 두 인자 전달
        if (BluetoothManager.connectToDevice(applicationContext, deviceAddress)) {
            startForeground(1, createNotification("블루투스 연결됨: $deviceAddress"))
        } else {
            stopSelf()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        BluetoothManager.disconnect() // 연결 종료
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotification(content: String): Notification {
        val channelId = "bluetooth_channel"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(channelId, "Bluetooth Service", NotificationManager.IMPORTANCE_LOW)
        notificationManager.createNotificationChannel(channel)

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Bluetooth Service")
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_bluetooth) // 나중에 아이콘 크기변경 필요할 수 있음
            .build()
    }
}
