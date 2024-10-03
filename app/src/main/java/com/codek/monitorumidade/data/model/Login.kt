package com.codek.monitorumidade.data.model

data class Login(
    val id: Int? = null,
    val email: String,
    val senha: String? = null,
    val nome: String? = null,
    val erro: String? = null,
    val message: String? = null
)

data class LoginResponse(
    val message: String?
)