package com.grouptoo.dindr.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.grouptoo.dindr.data.repository.DataRepository
import com.grouptoo.dindr.data.repository.DataRepositoryReal
import com.grouptoo.dindr.model.Sessions
import com.grouptoo.dindr.model.Users
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SessionPageViewModel  @Inject constructor(
    private val dataRepository: DataRepository
) : ViewModel() {


    val userList: StateFlow<MutableMap<String, Users>> = dataRepository.userList
    val startSwipeSession: StateFlow<Boolean?> = dataRepository.startSwipeSession

    fun usersEventListener(sessionId: String?) {
        dataRepository.usersEventListener(sessionId)
    }

    fun startEventListener(sessionId: String) {
        dataRepository.startEventListener(sessionId)
    }

    fun userLeaveSession(sessionId: String, userId: String) {
        dataRepository.userLeaveSession(sessionId, userId)
    }

    suspend fun checkSession(sessionsId: String): Boolean {
        return dataRepository.checkSession(sessionsId)
    }


}