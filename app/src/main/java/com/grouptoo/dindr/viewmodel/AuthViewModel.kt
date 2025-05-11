package com.grouptoo.dindr.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.grouptoo.dindr.data.repository.AuthRepository
import com.grouptoo.dindr.data.repository.AuthRepositoryReal
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel(){

    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    val authState: StateFlow<AuthState> = _authState


    init {
        _authState.value = AuthState.Unauthenticated
        _authState.value = authRepository.checkAuthStatus()
    }


    fun getUserId(): String {
        return authRepository.getUserId()
    }

    fun logIn(email: String, password: String) {

        if(email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Email or password can't be empty")
            return
        }

        _authState.value = AuthState.Loading

        viewModelScope.launch {
            val result = authRepository.loginUser(email, password)
            _authState.value = result
        }

    }

    fun signUp(email: String, password: String) {

        if(email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Email or password can't be empty")
            return
        }

        _authState.value = AuthState.Loading

        viewModelScope.launch {
            val result = authRepository.signUpUser(email, password)
            _authState.value = result
        }

    }

    fun signOut() {
        authRepository.signOut()
        _authState.value = AuthState.Unauthenticated
    }

}

sealed class AuthState {
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    data class Error(val message : String) : AuthState()
}