package com.grouptoo.dindr.viewmodel

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.grouptoo.dindr.data.repository.DataRepository
import com.grouptoo.dindr.data.repository.DataRepositoryReal
import com.grouptoo.dindr.model.RestaurantPlaces
import com.grouptoo.dindr.model.Users
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import androidx.core.net.toUri

@HiltViewModel
class SessionSwipePageViewModel @Inject constructor(
    private val dataRepository: DataRepository
) : ViewModel() {

    val userList: StateFlow<MutableMap<String, Users>> = dataRepository.userList
    val restaurantList: StateFlow<List<RestaurantPlaces>> = dataRepository.restaurantList
    val votingFinished: StateFlow<Boolean?> = dataRepository.votingFinished

    fun getPlaces(sessionId: String?) {
        dataRepository.getPlaces(sessionId)
    }

    fun voteRestaurant(sessionId: String?, position: Int) {
        dataRepository.voteRestaurant(sessionId, position)
    }

    fun userVoted(sessionId: String?, userId: String?) {
        dataRepository.userVoted(sessionId, userId)
    }


    fun checkVoteFinished(sessionId: String?) {
        dataRepository.checkVoteFinished(sessionId)
    }

    fun resetVoteFinished() {
        dataRepository.resetVoteFinished()
    }

    suspend fun checkSession(sessionsId: String): Boolean {
        return dataRepository.checkSession(sessionsId)
    }

    fun viewRestaurantMap(restaurantName: String?, restaurantAddress: String?, latitude: String, longitude: String, context: Context){

        val gmmIntentUri = ("geo:${latitude},${longitude}?z=80&q=" + Uri.encode("$restaurantName $restaurantAddress")).toUri()
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        context.startActivity(mapIntent)

    }


}