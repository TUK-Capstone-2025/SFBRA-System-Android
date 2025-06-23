package com.example.sfbra_system_android.data

import com.example.sfbra_system_android.data.services.ApplicantMemberService
import com.example.sfbra_system_android.data.services.CreateTeamService
import com.example.sfbra_system_android.data.services.DeleteRequestService
import com.example.sfbra_system_android.data.services.JoinTeamService
import com.example.sfbra_system_android.data.services.KickMemberService
import com.example.sfbra_system_android.data.services.LoginService
import com.example.sfbra_system_android.data.services.MemberProfileService
import com.example.sfbra_system_android.data.services.MyTeamInfoService
import com.example.sfbra_system_android.data.services.PathRecordRouteService
import com.example.sfbra_system_android.data.services.PathRecordService
import com.example.sfbra_system_android.data.services.ProfileService
import com.example.sfbra_system_android.data.services.ProfileUpdateService
import com.example.sfbra_system_android.data.services.RegisterService
import com.example.sfbra_system_android.data.services.RequestListService
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
    private const val BASE_URL = "https://339c-210-99-254-13.ngrok-free.app/api/" // base 주소

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

    fun getCreateTeamService(token: String): CreateTeamService =
        getRetrofitInstance(token).create(CreateTeamService::class.java)

    fun getJoinTeamService(token: String): JoinTeamService =
        getRetrofitInstance(token).create(JoinTeamService::class.java)

    fun getRequestListService(token: String): RequestListService =
        getRetrofitInstance(token).create(RequestListService::class.java)

    fun getDeleteRequestService(token: String): DeleteRequestService =
        getRetrofitInstance(token).create(DeleteRequestService::class.java)

    fun getMyTeamInfoService(): MyTeamInfoService = getRetrofitInstance().create(MyTeamInfoService::class.java)

    fun getApplicantMemberService(token: String): ApplicantMemberService =
        getRetrofitInstance(token).create(ApplicantMemberService::class.java)

    fun getPathRecordRouteService(token: String): PathRecordRouteService =
        getRetrofitInstance(token).create(PathRecordRouteService::class.java)

    fun getKickMemberService(token: String): KickMemberService =
        getRetrofitInstance(token).create(KickMemberService::class.java)

    fun getMemberProfileService(token: String): MemberProfileService =
        getRetrofitInstance(token).create(MemberProfileService::class.java)
}