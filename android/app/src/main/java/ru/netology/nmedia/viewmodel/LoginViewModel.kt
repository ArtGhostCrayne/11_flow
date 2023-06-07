package ru.netology.nmedia.viewmodel

import android.media.session.MediaSession
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.netology.nmedia.api.AuthApi

import ru.netology.nmedia.api.AuthApiService
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.model.AuthModelState


class LoginViewModel() : ViewModel() {


    private val _dataState = MutableLiveData<AuthModelState>()
    val dataState: LiveData<AuthModelState>
        get() = _dataState

    fun signIn(login: String, pass: String) = viewModelScope.launch {
        _dataState.value = AuthModelState(loading = true)
        try {
            val response = AuthApi.service.updateUser(login, pass)
            if (!response.isSuccessful) {
//                throw ApiError(response.code(), response.message())
                _dataState.value = AuthModelState(error = true, errorText = "Неверный логин или пароль")
            }else {
                val user = requireNotNull(response.body())
                user.login = login
                AppAuth.getInstance().setAuth(user.id, user.token)
                _dataState.value = AuthModelState(success = true, login = login)
            }
        } catch (e: Exception) {
            _dataState.value = AuthModelState(error = true, errorText = e.message.toString())

        }
    }

    fun clean() {
        _dataState.value = AuthModelState(loading = false, error = false, success = false)
    }


}
