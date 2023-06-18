package ru.netology.nmedia.viewmodel

import android.media.session.MediaSession
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import ru.netology.nmedia.api.AuthApi

import ru.netology.nmedia.api.AuthApiService
import ru.netology.nmedia.api.PostsApi
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.dto.MediaUpload
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError
import ru.netology.nmedia.model.AuthModelState
import ru.netology.nmedia.model.PhotoModel
import java.io.File
import java.io.IOException

private val noPhoto = PhotoModel()

class RegistrationViewModel() : ViewModel() {


    private val _dataState = MutableLiveData<AuthModelState>()
    val dataState: LiveData<AuthModelState>
        get() = _dataState

    private val _photo = MutableLiveData(noPhoto)
    val photo: LiveData<PhotoModel>
        get() = _photo

    fun changePhoto(uri: Uri?, file: File?) {
        _photo.value = PhotoModel(uri, file)
    }

    fun registration(login: String, pass: String, name: String) = viewModelScope.launch {
        _dataState.value = AuthModelState(loading = true)
        try {
            val response =
                when (_photo.value) {
                    noPhoto -> AuthApi.service.registerUser(login, pass, name)
                    else -> _photo.value?.file?.let { file ->
                        AuthApi.service.registerWithPhoto(
                            login.toRequestBody(), pass.toRequestBody(), name.toRequestBody(),
                            MultipartBody.Part.createFormData(
                                "file",
                                file.name,
                                file.asRequestBody()
                            )
                        )
                    }
                }
            if (response != null) {
                if (!response.isSuccessful) {
        //                throw ApiError(response.code(), response.message())
                    _dataState.value =
                        AuthModelState(error = true, errorText = "Произошла ошибка при регистрации")
                } else {
                    val user = requireNotNull(response.body())
                    user.login = login
                    AppAuth.getInstance().setAuth(user.id, user.token)
                    _dataState.value = AuthModelState(success = true, login = login)
                }
            }else {
                AuthModelState(error = true, errorText = "Что-то пошло не так!")
            }
        } catch (e: Exception) {
            _dataState.value = AuthModelState(error = true, errorText = e.message.toString())

        }
    }

    fun clean() {
        _dataState.value = AuthModelState(loading = false, error = false, success = false)
    }


}
