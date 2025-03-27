package com.example.sfbra_system_android.data

import com.example.sfbra_system_android.data.services.LoginService
import com.example.sfbra_system_android.data.services.PathRecordService
import com.example.sfbra_system_android.data.services.ProfileUpdateService
import com.example.sfbra_system_android.data.services.RegisterService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

// 서버 통신을 위한 Retrofit 클라이언트 객체 생성
object RetrofitClient {
    // todo : 서버 주소 입력
    private  const val BASE_URL = "https://3cf0-210-99-254-13.ngrok-free.app/api/" // base 주소

    // 로그인 API
    val loginService: LoginService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // JSON 변환기 추가
            .build()
            .create(LoginService::class.java)
    }

    // 회원가입 API
    val registerService: RegisterService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // JSON 변환기 추가
            .build()
            .create(RegisterService::class.java)
    }

    // JWT 토큰을 자동으로 헤더에 추가하는 Interceptor
    private fun getAuthInterceptor(token: String): Interceptor {
        return Interceptor { chain ->
            val newRequest: Request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token") // 토큰 추가
                .build()
            chain.proceed(newRequest)
        }
    }

    fun getRetrofitInstance(token: String): Retrofit {
        val client = OkHttpClient.Builder()
            .addInterceptor(getAuthInterceptor(token)) // 인터셉터 추가
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun getProfileUpdateService(token: String): ProfileUpdateService {
        val retrofit = getRetrofitInstance(token)
        return retrofit.create(ProfileUpdateService::class.java)
    }

    fun getPathRecordService(token: String): PathRecordService {
        val retrofit = getRetrofitInstance(token)
        return retrofit.create(PathRecordService::class.java)
    }
}