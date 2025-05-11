package com.grouptoo.dindr.viewmodel

import androidx.lifecycle.ViewModel
import com.grouptoo.dindr.data.repository.DataRepository
import com.grouptoo.dindr.data.repository.DataRepositoryReal
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ScanPageViewModel  @Inject constructor(
    private val dataRepository: DataRepository
) : ViewModel() {

    suspend fun checkSession(sessionId: String): Boolean{
        return dataRepository.checkSession(sessionId)
    }

    fun addUserToSession(sessionId: String, userId: String, username: String) {
        dataRepository.addUserToSession(sessionId, userId, username)
    }
}