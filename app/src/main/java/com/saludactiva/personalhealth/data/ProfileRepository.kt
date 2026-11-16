package com.saludactiva.personalhealth.data

import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response

class ProfileRepository(
    private val api: ApiService,
    private val sessions: SessionManager
) {
    suspend fun login(email: String, password: String): ProfileUser {
        val response = api.login(LoginRequest(email, password))
        val auth = response.requireBody("No fue posible iniciar sesión. Revisa tus credenciales.")
        val token = auth.token?.takeIf(String::isNotBlank)
            ?: throw ApiException(response.code(), "El servidor no devolvió un token de sesión.")
        val user = auth.usuario
            ?: throw ApiException(response.code(), "El servidor no devolvió los datos del perfil.")
        sessions.saveSession(token, user)
        return user
    }

    suspend fun updateProfile(name: String, phone: String): ProfileUser {
        val response = api.updateProfile(UpdateProfileRequest(name, phone))
        val updated = response.requireBody("No fue posible actualizar tu perfil.")
        sessions.saveUser(updated)
        return updated
    }

    private fun <T> Response<T>.requireBody(fallbackMessage: String): T {
        if (!isSuccessful) {
            val serverMessage = errorBody()?.string()?.let(::messageFromErrorBody)
            throw ApiException(code(), serverMessage ?: fallbackMessage)
        }
        return body() ?: throw ApiException(code(), "El servidor devolvió una respuesta vacía.")
    }

    private fun messageFromErrorBody(body: String): String? =
        try {
            JSONObject(body).optString("mensaje").takeIf(String::isNotBlank)
        } catch (_: JSONException) {
            null
        }
}

class ApiException(val statusCode: Int, message: String) : Exception(message)
