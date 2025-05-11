package com.grouptoo.dindr.data.repository

import com.grouptoo.dindr.viewmodel.AuthState

interface AuthRepository {
    suspend fun loginUser(email: String, password: String): AuthState
    suspend fun signUpUser(email: String, password: String): AuthState
    fun checkAuthStatus(): AuthState
    fun getUserId(): String
    fun signOut()
}