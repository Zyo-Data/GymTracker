package com.jorge.gymtracker.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface SessionState {
    data object Loading : SessionState
    data object LoggedIn : SessionState
    data object LoggedOut : SessionState
}

class AuthViewModel(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    private val _session = MutableStateFlow<SessionState>(SessionState.Loading)
    val session: StateFlow<SessionState> = _session

    init { checkSession() }

    fun checkSession() {
        viewModelScope.launch {
            _session.value = if (auth.currentUser != null) SessionState.LoggedIn else SessionState.LoggedOut
        }
    }
}
