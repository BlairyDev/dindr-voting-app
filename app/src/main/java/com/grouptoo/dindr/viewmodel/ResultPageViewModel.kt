package com.grouptoo.dindr.viewmodel

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grouptoo.dindr.data.repository.DataRepository
import com.grouptoo.dindr.data.repository.DataRepositoryReal
import com.grouptoo.dindr.model.RestaurantPlaces
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResultPageViewModel @Inject constructor(
    private val dataRepository: DataRepository
) : ViewModel() {

    val restaurantList: StateFlow<List<RestaurantPlaces>> = dataRepository.restaurantList
    val randomRestaurant: StateFlow<RestaurantPlaces?> = dataRepository.randomRestaurant

    fun getPlaces(sessionId: String?) {
        dataRepository.getPlaces(sessionId)
    }

    suspend fun getRandomRestaurant(sessionId: String) {
        dataRepository.getRandomRestaurant(sessionId)
    }


    fun viewRestaurantMap(restaurantName: String?, restaurantAddress: String?, latitude: String, longitude: String, context: Context){

        val gmmIntentUri = ("geo:${latitude},${longitude}?z=80&q=" + Uri.encode("$restaurantName $restaurantAddress")).toUri()
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        context.startActivity(mapIntent)

    }
}