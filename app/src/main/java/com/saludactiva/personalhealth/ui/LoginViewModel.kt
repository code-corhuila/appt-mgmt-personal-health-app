package com.saludactiva.personalhealth.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.saludactiva.personalhealth.data.NetworkModule
import com.saludactiva.personalhealth.data.ApiException
import com.saludactiva.personalhealth.data.ProfileRepository
import com.saludactiva.personalhealth.data.SessionManager
import kotlinx.coroutines.launch
import java.io.IOException
import com.google.gson.JsonParseException
import retrofit2.HttpException

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val sessions = SessionManager(application)
    private val repository = ProfileRepository(NetworkModule.createApi(application), sessions)
    private val _state = MutableLiveData<LoginState>(LoginState.Idle)
    val state: LiveData<LoginState> = _state

    fun signIn(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _state.value = LoginState.Error("Ingresa tu correo y contraseña.")
            return
        }
        _state.value = LoginState.Loading
        viewModelScope.launch {
            try {
                repository.login(email.trim(), password)
                _state.value = LoginState.Authenticated
            } catch (error: ApiException) {
                _state.value = LoginState.Error(error.message ?: "No se pudo completar la solicitud.")
            } catch (error: IOException) {
                _state.value = LoginState.Error("No se pudo conectar con el servidor. Intenta de nuevo.")
            } catch (error: HttpException) {
                _state.value = LoginState.Error("No se pudo conectar con el servidor. Intenta de nuevo.")
            } catch (error: JsonParseException) {
                _state.value = LoginState.Error(
                    error.message ?: "El servidor devolvió una respuesta no válida."
                )
            }
        }
    }
}

sealed interface LoginState {
    data object Idle : LoginState
    data object Loading : LoginState
    data object Authenticated : LoginState
    data class Error(val message: String) : LoginState
}
