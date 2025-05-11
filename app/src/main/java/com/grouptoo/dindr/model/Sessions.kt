package com.grouptoo.dindr.model

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties


@IgnoreExtraProperties
data class Sessions(
    val userRoles: MutableMap<String, Any> = HashMap(),
    val restaurant: List<RestaurantPlaces>
) {

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "start" to false,
            "roles" to userRoles,
            "restaurant" to restaurant,
            "random" to restaurant.random()
        )
    }


}