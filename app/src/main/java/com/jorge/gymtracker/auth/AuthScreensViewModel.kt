package com.jorge.gymtracker.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jorge.gymtracker.auth.data.AuthRepository
import com.jorge.gymtracker.auth.data.AuthResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val loading: Boolean = false,
    val error: String? = null
)

class AuthScreensViewModel(
    private val repo: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _login = MutableStateFlow(AuthUiState())
    val login: StateFlow<AuthUiState> = _login

    private val _register = MutableStateFlow(AuthUiState())
    val register: StateFlow<AuthUiState> = _register

    private val _reset = MutableStateFlow(AuthUiState())
    val reset: StateFlow<AuthUiState> = _reset

    fun updateLogin(email: String? = null, pass: String? = null) {
        _login.value = _login.value.copy(
            email = email ?: _login.value.email,
            password = pass ?: _login.value.password,
            error = null
        )
    }
    fun updateRegister(email: String? = null, pass: String? = null) {
        _register.value = _register.value.copy(
            email = email ?: _register.value.email,
            password = pass ?: _register.value.password,
            error = null
        )
    }
    fun updateReset(email: String? = null) {
        _reset.value = _reset.value.copy(
            email = email ?: _reset.value.email,
            error = null
        )
    }

    fun doLogin(onSuccess: () -> Unit) = viewModelScope.launch {
        _login.value = _login.value.copy(loading = true, error = null)
        when (val r = repo.login(_login.value.email.trim(), _login.value.password)) {
            is AuthResult.Success -> onSuccess()
            is AuthResult.Error -> _login.value = _login.value.copy(error = r.message)
        }
        _login.value = _login.value.copy(loading = false)
    }

    fun doRegister(onSuccess: () -> Unit) = viewModelScope.launch {
        _register.value = _register.value.copy(loading = true, error = null)
        when (val r = repo.register(_register.value.email.trim(), _register.value.password)) {
            is AuthResult.Success -> onSuccess()
            is AuthResult.Error -> _register.value = _register.value.copy(error = r.message)
        }
        _register.value = _register.value.copy(loading = false)
    }

    fun doReset(onSent: () -> Unit) = viewModelScope.launch {
        _reset.value = _reset.value.copy(loading = true, error = null)
        when (val r = repo.reset(_reset.value.email.trim())) {
            is AuthResult.Success -> onSent()
            is AuthResult.Error -> _reset.value = _reset.value.copy(error = r.message)
        }
        _reset.value = _reset.value.copy(loading = false)
    }
}
