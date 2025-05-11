package com.grouptoo.dindr.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.grouptoo.dindr.viewmodel.AuthState
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class AuthRepositoryReal @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository{

    override suspend fun loginUser(email: String, password: String): AuthState {
        return try {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
            AuthState.Authenticated
        } catch (e: Exception) {
            e.printStackTrace()
            AuthState.Unauthenticated
        }

    }

    override suspend fun signUpUser(email: String, password: String): AuthState {
        return try {
            firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            AuthState.Authenticated
        } catch (e: Exception) {
            e.printStackTrace()
            AuthState.Unauthenticated
        }
    }

    override fun getUserId(): String {
        return firebaseAuth.currentUser?.uid.toString()
    }


    override fun checkAuthStatus(): AuthState {
        return if(firebaseAuth.currentUser == null) {
                AuthState.Unauthenticated
            }
            else {
                AuthState.Authenticated
            }


    }

    override fun signOut(){
        firebaseAuth.signOut()
    }




}

