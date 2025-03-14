package com.example.sfbra_system_android

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// 서버 통신을 위한 Retrofit 클라이언트 객체 생성
object RetrofitClient {
    // todo : 서버 주소 입력
    private  const val BASE_URL = "" // base 주소

    val instance: LoginService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(LoginService::class.java)

    }
}