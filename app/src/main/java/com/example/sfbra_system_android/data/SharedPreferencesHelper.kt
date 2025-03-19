package com.example.sfbra_system_android.data

import android.content.Context
import android.content.SharedPreferences

// shared preference 사용 유틸리티
object SharedPreferencesHelper {
    private const val PREFS_NAME = "sfbra_prefs" // shared preference 저장소 이름
    private const val TOKEN_KEY = "auth_token"  // 접근 키값

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    // 토큰 저장
    fun saveToken(context: Context, token: String) {
        val editor = getPreferences(context).edit()
        editor.putString(TOKEN_KEY, token)
        editor.apply()
    }

    // 토큰 불러오기
    fun getToken(context: Context): String? {
        return getPreferences(context).getString(TOKEN_KEY, null)
    }

    // 토큰 초기화
    fun clearToken(context: Context) {
        val editor = getPreferences(context).edit()
        editor.remove(TOKEN_KEY)
        editor.apply()
    }
}