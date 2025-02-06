package com.example.sfbra_system_android

import android.app.Application
import com.kakao.vectormap.KakaoMapSdk

class SfbraApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        KakaoMapSdk.init(this, getString(R.string.kakao_map_key))
    }
}