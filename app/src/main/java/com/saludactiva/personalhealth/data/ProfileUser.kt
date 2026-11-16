package com.saludactiva.personalhealth.data

import com.google.gson.annotations.SerializedName

data class ProfileUser(
    @SerializedName(value = "id", alternate = ["_id"])
    val id: String,
    val nombre: String,
    val email: String,
    val telefono: String? = null
)

data class LoginRequest(
    val email: String,
    val contrasena: String
)

data class AuthResponse(
    val mensaje: String? = null,
    val usuario: ProfileUser? = null,
    val token: String? = null
)

data class UpdateProfileRequest(
    val nombre: String,
    val telefono: String
)
