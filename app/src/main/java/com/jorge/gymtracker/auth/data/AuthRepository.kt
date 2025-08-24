package com.jorge.gymtracker.auth.data

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

sealed class AuthResult {
    data object Success : AuthResult()
    data class Error(val message: String) : AuthResult()
}

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    suspend fun login(email: String, pass: String): AuthResult = try {
        auth.signInWithEmailAndPassword(email, pass).await()
        AuthResult.Success
    } catch (e: Exception) {
        AuthResult.Error(e.message ?: "Error al iniciar sesión")
    }

    suspend fun register(email: String, pass: String): AuthResult = try {
        auth.createUserWithEmailAndPassword(email, pass).await()
        AuthResult.Success
    } catch (e: Exception) {
        AuthResult.Error(e.message ?: "Error al registrarse")
    }

    suspend fun reset(email: String): AuthResult = try {
        auth.sendPasswordResetEmail(email).await()
        AuthResult.Success
    } catch (e: Exception) {
        AuthResult.Error(e.message ?: "No se pudo enviar el correo de restablecimiento")
    }
}
