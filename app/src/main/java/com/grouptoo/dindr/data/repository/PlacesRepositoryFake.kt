package com.grouptoo.dindr.data.repository

import android.util.Log
import com.google.android.libraries.places.api.net.PlacesClient
import com.grouptoo.dindr.data.model.PlacesApiResponse
import com.grouptoo.dindr.model.RestaurantPlaces
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PlacesRepositoryFake @Inject constructor(
    private val placesClient: PlacesClient
) : PlacesRepository {


    val restaurant: List<RestaurantPlaces> = listOf(
            RestaurantPlaces(
                name = "Jollibee",
                img = "https://www.thedailymeal.com/img/gallery/13-ordering-mistakes-you-might-be-making-at-jollibee/l-intro-1683420243.jpg",
                address = "116 S Michigan Ave, Chicago, IL 60603",
                ratings = 5.0,
                votes = 0,
                latitude = 41.880589,
                longitude = -87.624580
            ),
            RestaurantPlaces(
                name = "Chick-Fil-A",
                img = "https://www.pressconnects.com/gcdn/presto/2021/11/02/PROC/f2e7eafb-f5c3-4e78-b796-3cc1fc7e9d09-IrondequoitChickFilA.jpg",
                address = "8050 Mall Rd, Florence, KY 41042",
                ratings = 4.9,
                votes = 0,
                latitude = 38.985290,
                longitude = -84.649475
            ),
            RestaurantPlaces(
                name = "Popeyes",
                img = "https://fox4kc.com/wp-content/uploads/sites/16/2021/06/PopeyesExteriorGettyImages-643471694.jpg?w=2560&h=1440&crop=1",
                address = "7777 Burlington Pike, Florence, KY 41042",
                ratings = 4.4,
                votes = 0,
                latitude = 39.000130,
                longitude = -84.644669
            ),
            RestaurantPlaces(
                name = "Raising Cane's Chicken Fingers",
                img = "https://static.wixstatic.com/media/0c47eb_b2cdb171ad32427eb7a45ce0d8b33ae3~mv2.jpg/v1/fill/w_2119,h_1415,al_c,q_90/Raising%20Cane's.jpg",
                address = "8020 Burlington Pike, Florence, KY 41042",
                ratings = 4.3,
                votes = 0,
                latitude = 39.002918,
                longitude = -84.650742
            ),
            RestaurantPlaces(
                name = "Taco Bell",
                img = "https://image.cnbcfm.com/api/v1/image/107408345-1714498561917-gettyimages-1945793217-1006_32_nc230605111.jpeg?v=1731697909",
                address = "8526 US-42, Florence, KY 41042",
                ratings = 4.3,
                votes = 0,
                latitude = 38.974999,
                longitude = -84.660982
            )

    )


    override suspend fun nearbyPlace(longitude: Double, latitude: Double): PlacesApiResponse {

        return withContext(Dispatchers.IO) {
            try {
                PlacesApiResponse.Success(restaurant)

            } catch (error: Exception) {
                Log.i("TAG", "Failed in Repository Fake", error)
                PlacesApiResponse.Error
            }
        }
    }

    override suspend fun getPlacePhoto(placeId: String?): String {
        Log.i("TAG", "placeRepositoryFake getPlacePhoto")
        return ""
    }
}