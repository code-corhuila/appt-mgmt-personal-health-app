package com.saludactiva.personalhealth.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.saludactiva.personalhealth.data.ApiException
import com.saludactiva.personalhealth.data.NetworkModule
import com.saludactiva.personalhealth.data.ProfileInputValidator
import com.saludactiva.personalhealth.data.ProfileRepository
import com.saludactiva.personalhealth.data.ProfileUser
import com.saludactiva.personalhealth.data.SessionManager
import com.google.gson.JsonParseException
import kotlinx.coroutines.launch
import java.io.IOException
import retrofit2.HttpException

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val sessions = SessionManager(application)
    private val repository = ProfileRepository(NetworkModule.createApi(application), sessions)

    private val _user = MutableLiveData(sessions.user())
    val user: LiveData<ProfileUser?> = _user
    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading
    private val _message = MutableLiveData<String?>()
    val message: LiveData<String?> = _message
    private val _sessionExpired = MutableLiveData(false)
    val sessionExpired: LiveData<Boolean> = _sessionExpired

    fun updateProfile(name: String, phone: String) {
        val nameError = ProfileInputValidator.validateName(name)
        if (nameError != null) {
            _message.value = nameError
            return
        }
        val phoneError = ProfileInputValidator.validatePhone(phone)
        if (phoneError != null) {
            _message.value = phoneError
            return
        }

        _loading.value = true
        viewModelScope.launch {
            try {
                _user.value = repository.updateProfile(name.trim(), phone.trim())
                _message.value = "Perfil actualizado."
            } catch (error: ApiException) {
                if (error.statusCode == 401) {
                    sessions.clear()
                    _sessionExpired.value = true
                } else {
                    _message.value = error.message ?: "No se pudo completar la solicitud. Intenta de nuevo."
                }
            } catch (_: IOException) {
                _message.value = "No se pudo conectar con el servidor. Intenta de nuevo."
            } catch (_: HttpException) {
                _message.value = "No se pudo conectar con el servidor. Intenta de nuevo."
            } catch (error: JsonParseException) {
                _message.value = error.message ?: "El servidor devolvió una respuesta no válida."
            } finally {
                _loading.value = false
            }
        }
    }

    fun signOut() {
        sessions.clear()
        _sessionExpired.value = true
    }

    fun clearMessage() {
        _message.value = null
    }
}
