package com.acacioswork.ui.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.acacioswork.model.LoginRequest
import com.acacioswork.network.RetrofitClient
import com.acacioswork.network.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    object Success : LoginState()
    data class Error(val message: String) : LoginState()
}

class LoginViewModel : ViewModel() {
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    fun login(context: Context, usuario: String, clave: String) {
        if (usuario.isBlank() || clave.isBlank()) {
            _loginState.value = LoginState.Error("Por favor completa todos los campos")
            return
        }

        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                val response = RetrofitClient.apiService.login(LoginRequest(usuario, clave))
                if (response.success && response.data != null) {
                    val loginResponse = response.data
                    val sessionManager = SessionManager.getInstance(context)
                    val fullName = "${loginResponse.usuario.nombre} ${loginResponse.usuario.apellido}"
                    
                    // Guardar la sesión persistente
                    sessionManager.saveSession(
                        jwtToken = loginResponse.token,
                        name = fullName,
                        role = loginResponse.usuario.idRol.toString(),
                        id = loginResponse.usuario.id
                    )
                    
                    _loginState.value = LoginState.Success
                } else {
                    _loginState.value = LoginState.Error(response.message)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _loginState.value = LoginState.Error("Error de conexión: Credenciales incorrectas o servidor inaccesible.")
            }
        }
    }

    fun resetState() {
        _loginState.value = LoginState.Idle
    }
}
