package com.example.sfbra_system_android.data

import com.example.sfbra_system_android.data.services.LoginService
import com.example.sfbra_system_android.data.services.PathRecordService
import com.example.sfbra_system_android.data.services.ProfileService
import com.example.sfbra_system_android.data.services.ProfileUpdateService
import com.example.sfbra_system_android.data.services.RegisterService
import com.example.sfbra_system_android.data.services.TeamCheckService
import com.example.sfbra_system_android.data.services.TeamListService
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

    // JWT 토큰을 자동으로 헤더에 추가하는 Interceptor
    private fun getAuthInterceptor(token: String): Interceptor {
        return Interceptor { chain ->
            val newRequest: Request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token") // 토큰 추가
                .build()
            chain.proceed(newRequest)
        }
    }

    // 헤더 달린 API 요청을 위한 Retrofit 인스턴스 생성, 헤더 없는 인스턴스도 생성
    private fun getRetrofitInstance(token: String? = null): Retrofit {
        val clientBuilder = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)

        token?.let {
            clientBuilder.addInterceptor(getAuthInterceptor(it))
        }

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(clientBuilder.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun getLoginService(): LoginService = getRetrofitInstance().create(LoginService::class.java)

    fun getRegisterService(): RegisterService = getRetrofitInstance().create(RegisterService::class.java)

    fun getProfileUpdateService(token: String): ProfileUpdateService =
        getRetrofitInstance(token).create(ProfileUpdateService::class.java)

    fun getPathRecordService(token: String): PathRecordService =
        getRetrofitInstance(token).create(PathRecordService::class.java)

    fun getUserInfoService(token: String): ProfileService =
        getRetrofitInstance(token).create(ProfileService::class.java)

    fun getTeamCheckService(token: String): TeamCheckService =
        getRetrofitInstance(token).create(TeamCheckService::class.java)

    fun getTeamListService(): TeamListService = getRetrofitInstance().create(TeamListService::class.java)


    // 리팩토링 이전
    /*
    // 로그인 API Retorfit 객체
    val loginService: LoginService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // JSON 변환기 추가
            .build()
            .create(LoginService::class.java)
    }

    // 회원가입 API Retorfit 객체
    val registerService: RegisterService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // JSON 변환기 추가
            .build()
            .create(RegisterService::class.java)
    }

    fun getProfileUpdateService(token: String): ProfileUpdateService {
        val retrofit = getRetrofitInstance(token)
        return retrofit.create(ProfileUpdateService::class.java)
    }

    fun getPathRecordService(token: String): PathRecordService {
        val retrofit = getRetrofitInstance(token)
        return retrofit.create(PathRecordService::class.java)
    }

    fun getUserInfo(token: String): ProfileService {
        val retrofit = getRetrofitInstance(token)
        return retrofit.create(ProfileService::class.java)
    }

    fun checkUserHasTeam(token: String): TeamCheckService {
        val retrofit = getRetrofitInstance(token)
        return retrofit.create(TeamCheckService::class.java)
    }
     */
}