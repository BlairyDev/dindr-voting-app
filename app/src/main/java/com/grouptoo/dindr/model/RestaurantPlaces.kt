package com.grouptoo.dindr.model

import okhttp3.Address

data class RestaurantPlaces(
    val name: String? = "",
    val img: String? = "",
    val address: String? = "",
    val ratings: Double? = 0.0,
    var votes: Int = 0,
    val latitude: Double? = 0.0,
    val longitude: Double? = 0.0
)