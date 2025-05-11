package com.grouptoo.dindr.data.repository

import com.grouptoo.dindr.model.RestaurantPlaces
import com.grouptoo.dindr.model.Users
import kotlinx.coroutines.flow.StateFlow

interface DataRepository {
    suspend fun getRandomRestaurant(sessionId: String?)
    val randomRestaurant: StateFlow<RestaurantPlaces?>

    val restaurantList: StateFlow<List<RestaurantPlaces>>
    fun getPlaces(sessionId: String?)

    val votingFinished: StateFlow<Boolean?>
    fun voteRestaurant(sessionId: String?, position: Int)

    fun userVoted(sessionId: String?, userId: String?)
    fun checkVoteFinished(sessionId: String?)
    fun resetVoteFinished()

    fun createSession(userId: String, username: String, restaurants: List<RestaurantPlaces>): String?
    suspend fun checkSession(sessionId: String): Boolean
    fun addUserToSession(sessionId: String, userId: String, username: String)
    fun userLeaveSession(sessionId: String, userId: String)
    fun endSession(sessionId: String)

    val userList: StateFlow<MutableMap<String, Users>>
    fun usersEventListener(sessionId: String?)

    val startSwipeSession: StateFlow<Boolean?>
    fun startEventListener(sessionId: String)

    fun startSession(sessionId: String?)

}