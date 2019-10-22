package com.example.water13.source

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.example.water13.bean.User
import com.example.water13.component.NETWORK_ERROR_STRING
import com.example.water13.component.SPF_FILE_NAME_USER
import com.example.water13.component.getStatusMsg
import retrofit2.HttpException
import java.net.UnknownHostException

object Repo {

    private lateinit var spf: SharedPreferences

    fun init(app: Application) {
        spf = app.getSharedPreferences(SPF_FILE_NAME_USER, Context.MODE_PRIVATE)
        if (spf.contains("username")) {
            User.instance = getUser()
        }
    }

    private fun saveUser(user: User) {
        spf.edit().putString("username", user.username)
            .putString("password", user.password)
            .putInt("id", user.id)
            .putString("token", user.token)
            .apply()
    }

    fun getUser(): User {
        return User(
            spf.getString("username", "")!!,
            spf.getString("password", "")!!,
            spf.getInt("id", -1),
            spf.getString("token", "")!!
        )
    }

    fun clearUser() {
        spf.edit().clear().apply()
        User.instance = User()
    }

    @Deprecated(message = "use register2 instead")
    suspend fun register(newUser: User) {
        withCatch {
            Network.api.registerAsync(newUser.toDto()).await()
        }
    }

    suspend fun register2(newUserWithJwch: RegisterDto) {
        withCatch {
            Network.api.register2Async(newUserWithJwch).await()
        }
    }

    suspend fun login(user: User) {
        withCatch {
            val data =
                Network.api.loginAsync(user.toDto()).await().data
            user.id = data.user_id
            user.token = data.token
            User.instance = user
            saveUser(user)
        }
    }

    suspend fun logout(user: User) {
        withCatch {
            Network.api.logoutAsync(user.token).await().data
            clearUser()
        }
    }

    suspend fun check(user: User) {
        val data = Network.api.checkAsync(user.token).await().data
        if (data.user_id == null) {
            throw Exception(data.result)
        }
    }

    suspend fun open(user: User): OpenGameResponse.Data {
        return withCatch {
            Network.api.openGameAsync(user.token).await().data
        }
    }

    suspend fun submit(id: Int, cards: List<String>, user: User) {
        withCatch {
            Network.api.submitCardsAsync(user.token, CardsDto(id, cards)).await()
        }
    }

    suspend fun getHistoryList(gamerId:Int, token:String, limit:Int, page:Int):List<HistoryListResponse.Data> {
        return withCatch{
            Network.api.getHistoryListAsync(token, gamerId, limit, page).await().data
        }
    }

    suspend fun getHistoryDetail(user:User,gameId:Int) :HistoryDetailResponse{
        return withCatch {
            Network.api.getHistoryDetailAsync(user.token, gameId).await()
        }
    }

    suspend fun getRank() :List<RankResponse>{
        return withCatch {
            Network.api.getRankAsync().await()
        }
    }

    private suspend fun <T> withCatch(block: suspend () -> T): T {
        try {
            return block.invoke()
        } catch (e: HttpException) {
            throw(Exception(getStatusMsg(e)))
        } catch (e: UnknownHostException) {
            throw(Exception(NETWORK_ERROR_STRING))
        }
    }
}