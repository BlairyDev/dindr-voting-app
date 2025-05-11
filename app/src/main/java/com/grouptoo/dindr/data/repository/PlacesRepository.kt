package com.grouptoo.dindr.data.repository

import com.grouptoo.dindr.data.model.PlacesApiResponse


interface PlacesRepository {
    suspend fun nearbyPlace(longitude: Double, latitude: Double): PlacesApiResponse
    suspend fun getPlacePhoto(placeId: String?): String
}