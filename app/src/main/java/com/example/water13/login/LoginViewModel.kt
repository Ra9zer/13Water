package com.example.water13.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.water13.source.Repo
import com.example.water13.bean.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val viewModelJob = Job()

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    val uiUsername = MutableLiveData<String>()

    val uiPassword = MutableLiveData<String>()

    val message = MutableLiveData<String>()

    val success = MutableLiveData<Boolean>()


    fun onLoginClicked() {
        if (uiUsername.value.isNullOrEmpty() || uiPassword.value.isNullOrEmpty()) {
            message.value = "账号或密码不能为空"
        } else {
            uiScope.launch {
                login()
            }
        }
    }

    fun onRegisterClicked() {
        if (uiUsername.value.isNullOrEmpty() || uiPassword.value.isNullOrEmpty()) {
            message.value = "账号或密码不能为空"
        } else {
            uiScope.launch {
                register()
            }
        }
    }

    private suspend fun login() {
        try {
            Repo.login(User(uiUsername.value!!, uiPassword.value!!))
            success.postValue(true)
            message.postValue("登陆成功")
        } catch (e: Exception) {
            message.postValue(e.message)
        }
    }

    private suspend fun register() {
        try {
            Repo.register(User(uiUsername.value!!, uiPassword.value!!))
            message.postValue("注册成功")
        } catch (e: Exception) {
            message.postValue(e.message)
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}