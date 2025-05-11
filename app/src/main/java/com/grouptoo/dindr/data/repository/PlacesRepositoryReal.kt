package com.grouptoo.dindr.data.repository

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Tasks
import com.google.android.libraries.places.api.model.AuthorAttributions
import com.google.android.libraries.places.api.model.CircularBounds
import com.google.android.libraries.places.api.model.PhotoMetadata
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchResolvedPhotoUriRequest
import com.google.android.libraries.places.api.net.FetchResolvedPhotoUriResponse
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.api.net.SearchNearbyRequest
import com.grouptoo.dindr.data.model.PlacesApiResponse
import com.grouptoo.dindr.model.RestaurantPlaces
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PlacesRepositoryReal @Inject constructor(
    private val placesClient: PlacesClient
) : PlacesRepository {


    override suspend fun nearbyPlace(longitude: Double, latitude: Double): PlacesApiResponse{
        Log.i("TAG", "finding places..")
        Log.i("TAG", latitude.toString() + "latitude" + longitude.toString() )
        return withContext(Dispatchers.IO) {
            try {
                val placeFields = listOf(
                    Place.Field.ID,
                    Place.Field.DISPLAY_NAME,
                    Place.Field.PHOTO_METADATAS,
                    Place.Field.FORMATTED_ADDRESS,
                    Place.Field.RATING,
                    Place.Field.LAT_LNG
                )


                val center = LatLng(latitude, longitude)
                val circle = CircularBounds.newInstance(center, 5000.0)
                val includedTypes = listOf("restaurant")
                val excludedTypes = listOf("movie_theater", "car_dealer", "drugstore", "convenience_store", "gas_station", "hotel")

                val request = SearchNearbyRequest.builder(circle, placeFields)
                    .setIncludedTypes(includedTypes)
                    .setExcludedTypes(excludedTypes)
                    .setMaxResultCount(5)
                    .build()

                val responseRestaurant = Tasks.await(placesClient.searchNearby(request))

                val restaurants = responseRestaurant.places.map { place ->
                    RestaurantPlaces(
                        name = place.displayName,
                        img = getPlacePhoto(place.id),
                        address = place.formattedAddress,
                        ratings = place.rating,
                        latitude = place.location?.latitude,
                        longitude = place.location?.longitude
                    )
                }

                PlacesApiResponse.Success(restaurants)


            } catch (error: Exception) {
                Log.i("TAG", "Failed in Repository Real", error)
                PlacesApiResponse.Error
            }

        }


    }
    override suspend fun getPlacePhoto(placeId: String?): String {
        return withContext(Dispatchers.IO) {
            try {
                val fields = listOf(Place.Field.PHOTO_METADATAS)
                val request = FetchPlaceRequest.newInstance(placeId.toString(), fields)

                val fetchPlaceResponse = Tasks.await(placesClient.fetchPlace(request))
                val place = fetchPlaceResponse.place

                val metadata = place.photoMetadatas
                if (metadata.isNullOrEmpty()) {
                    Log.w("TAG", "No photo metadata.")
                    "https://upload.wikimedia.org/wikipedia/commons/1/14/No_Image_Available.jpg?20200913095930"
                }

                val photoMetadata = metadata?.get(0)

                val photoRequest = FetchResolvedPhotoUriRequest.builder(photoMetadata)
                    .setMaxWidth(800)
                    .setMaxHeight(1000)
                    .build()

                val photoResponse = Tasks.await(placesClient.fetchResolvedPhotoUri(photoRequest))
                val photoUri = photoResponse.uri?.toString() ?: "null_uri"

                photoUri
            } catch (error: Exception) {
                Log.e("TAG", "Failed to fetch photo: ${error.message}")
                "https://upload.wikimedia.org/wikipedia/commons/1/14/No_Image_Available.jpg?20200913095930"
            }

        }


    }

}