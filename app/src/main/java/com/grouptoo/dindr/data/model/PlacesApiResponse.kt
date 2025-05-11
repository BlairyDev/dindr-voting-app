package com.grouptoo.dindr.data.model

import com.grouptoo.dindr.model.RestaurantPlaces

sealed class PlacesApiResponse {
    data class Success(val restaurants: List<RestaurantPlaces>): PlacesApiResponse()
    data object Error: PlacesApiResponse()
}