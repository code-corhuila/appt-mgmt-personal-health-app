package com.saludactiva.personalhealth.data

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.gson.Gson

class SessionManager(context: Context) {
    private val preferences = EncryptedSharedPreferences.create(
        context,
        "personal_health_session",
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    private val gson = Gson()

    val token: String?
        get() = preferences.getString(TOKEN_KEY, null)

    fun user(): ProfileUser? =
        preferences.getString(USER_KEY, null)?.let { gson.fromJson(it, ProfileUser::class.java) }

    fun saveSession(token: String, user: ProfileUser) {
        preferences.edit()
            .putString(TOKEN_KEY, token)
            .putString(USER_KEY, gson.toJson(user))
            .apply()
    }

    fun saveUser(user: ProfileUser) {
        preferences.edit().putString(USER_KEY, gson.toJson(user)).apply()
    }

    fun clear() {
        preferences.edit().clear().apply()
    }

    private companion object {
        const val TOKEN_KEY = "jwt"
        const val USER_KEY = "profile_user"
    }
}
