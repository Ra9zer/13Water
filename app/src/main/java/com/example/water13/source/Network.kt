package com.example.water13.source

import com.example.water13.BuildConfig
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface Api {

    /* 注册 */
    @Deprecated(message = "use register2 instead")
    @POST("auth/register")
    fun registerAsync(@Body user: UserDto): Deferred<RegisterResponse>

    /* 注册 */
    @POST("auth/register2")
    fun register2Async(@Body registerInfo: RegisterDto): Deferred<RegisterResponse>

    /* 登录 */
    @POST("auth/login")
    fun loginAsync(@Body user: UserDto): Deferred<LoginResponse>

    /* 注销 */
    @POST("auth/logout")
    fun logoutAsync(@Header("X-Auth-Token") token: String): Deferred<LogoutResponse>

    /* 验证 */
    @GET("auth/validate")
    fun checkAsync(@Header("X-Auth-Token") token: String): Deferred<CheckResponse>

    /* 开局 */
    @POST("game/open")
    fun openGameAsync(
        @Header("X-Auth-Token") token: String
    ): Deferred<OpenGameResponse>

    /* 出牌 */
    @POST("game/submit")
    fun submitCardsAsync(
        @Header("X-Auth-Token") token: String,
        @Body cards: CardsDto
    ): Deferred<SubmitResponse>

    /* 历史对局 */
    @GET("history")
    fun getHistoryListAsync(
        @Header("X-Auth-Token") token: String,
        @Query("player_id") playerId: Int,
        @Query("limit") limit: Int,
        @Query("page") page: Int
    ): Deferred<HistoryListResponse>

    /* 对局详情 */
    @GET("history/{id}")
    fun getHistoryDetailAsync(
        @Header("X-Auth-Token") token: String,
        @Path("id") id: Int
    ): Deferred<HistoryDetailResponse>

    /* 排行榜 */
    @GET("rank")
    fun getRankAsync(): Deferred<List<RankResponse>>
}

object Network {

    private val clientBuilder = OkHttpClient.Builder()

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
        if (BuildConfig.DEBUG) {
            clientBuilder.addInterceptor(this)
        }
    }
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.shisanshui.rtxux.xyz/")
        .addConverterFactory(MoshiConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .client(
            clientBuilder.connectTimeout(10L, TimeUnit.SECONDS)
                .readTimeout(10L, TimeUnit.SECONDS)
                .build()
        )
        .build()
    val api = retrofit.create(Api::class.java)
}


data class UserDto(
    val username: String,
    val password: String
)

data class RegisterDto(
    val username: String,
    val password: String,
    val student_number:String,
    val student_password:String
)

data class CardsDto(
    val id: Int,
    val card: List<String>
)


data class RegisterResponse(
    val `data`: Data,
    val status: Int
) {
    data class Data(
        val msg: String,
        val user_id: Int
    )
}

data class LoginResponse(
    val `data`: Data,
    val status: Int
) {
    data class Data(
        val token: String,
        val user_id: Int
    )
}

data class LogoutResponse(
    val `data`: Data,
    val status: Int
) {
    data class Data(
        val result: String
    )
}

data class CheckResponse(
    val `data`: Data,
    val status: Int
) {
    class Data {
        var result: String? = null
        var user_id: Int? = null
    }
}

data class OpenGameResponse(
    val `data`: Data,
    val status: Int
) {
    data class Data(
        val card: String,
        val id: Int
    )
}

data class SubmitResponse(
    val `data`: Data,
    val status: Int
) {
    data class Data(
        val msg: String
    )
}

data class HistoryListResponse(
    val `data`: List<Data>,
    val status: Int
) {
    data class Data(
        val card: List<String>,
        val id: Int,
        val score: Int,
        val timestamp: Int
    )
}

data class HistoryDetailResponse(
    val `data`: Data,
    val status: Int
) {
    data class Data(
        val detail: List<Detail>,
        val id: Int,
        val timestamp: Int
    )

    data class Detail(
        val card: List<String>,
        val name: String,
        val playerId: Int,
        val score: Int
    )
}

data class RankResponse(
    val name: String,
    val player_id: Int,
    val score: Int
)



