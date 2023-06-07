package ru.netology.nmedia.model

import ru.netology.nmedia.dto.User

data class AuthModelState(
    val loading: Boolean = false,
    val error: Boolean = false,
    val success: Boolean = false,
    val errorText: String = "",
    val login: String = "",
)
