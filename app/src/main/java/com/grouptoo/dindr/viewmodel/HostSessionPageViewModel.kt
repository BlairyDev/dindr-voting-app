package com.grouptoo.dindr.viewmodel

import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.places.api.Places
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.grouptoo.dindr.data.model.PlacesApiResponse
import com.grouptoo.dindr.data.repository.DataRepository
import com.grouptoo.dindr.data.repository.DataRepositoryReal
import com.grouptoo.dindr.data.repository.PlacesRepository
import com.grouptoo.dindr.data.repository.PlacesRepositoryReal
import com.grouptoo.dindr.model.RestaurantPlaces
import com.grouptoo.dindr.model.Users
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.set


@HiltViewModel
class HostSessionPageViewModel @Inject constructor(
    private val dataRepository: DataRepository,
    private val placeRepository: PlacesRepository
) : ViewModel() {

    private val _restaurants = MutableStateFlow<RestaurantsState>(RestaurantsState.Loading)
    val restaurants: StateFlow<RestaurantsState> = _restaurants.asStateFlow()

    suspend fun createUserSession(
        userId: String,
        username: String,
        restaurants: List<RestaurantPlaces>
    ): String?{

        return dataRepository.createSession(userId, username, restaurants)

    }

    fun nearbyPlace(longitude: Double, latitude: Double) {

        viewModelScope.launch {
            when (val response = placeRepository.nearbyPlace(longitude, latitude)) {
                PlacesApiResponse.Error -> _restaurants.value = RestaurantsState.Failure
                is PlacesApiResponse.Success -> _restaurants.value = RestaurantsState.Success(response.restaurants)
            }
        }

    }


    fun startUserSession(sessionId: String?) {
        dataRepository.startSession(sessionId)
    }

    fun endSession(sessionId: String) {
        dataRepository.endSession(sessionId)
    }

    val userList: StateFlow<MutableMap<String, Users>> = dataRepository.userList

    fun usersEventListener(sessionId: String?) {
        dataRepository.usersEventListener(sessionId)
    }


    fun getQrCodeBitmap(qrCodeContent: String?): ImageBitmap {
        val size = 512
        val hints = hashMapOf<EncodeHintType, Int>().also {
            it[EncodeHintType.MARGIN] = 1
        }
        val bits = QRCodeWriter().encode(qrCodeContent, BarcodeFormat.QR_CODE, size, size, hints)
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565).also {
            for (x in 0 until size) {
                for (y in 0 until size) {
                    it.setPixel(x, y, if (bits[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
                }
            }
        }
        return bitmap.asImageBitmap()
    }

    sealed class RestaurantsState {
        data class Success(val restaurants: List<RestaurantPlaces>): RestaurantsState()
        data object Failure: RestaurantsState()
        data object Loading: RestaurantsState()
    }


}

