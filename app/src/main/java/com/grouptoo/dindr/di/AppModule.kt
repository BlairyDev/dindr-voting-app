package com.grouptoo.dindr.di

import android.content.Context
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import com.grouptoo.dindr.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseDb(): DatabaseReference = Firebase.database.reference;

    @Provides
    @Singleton
    fun providePlaceClient(@ApplicationContext context: Context): PlacesClient {
        val apiKey = BuildConfig.PLACES_API_KEY
        Places.initializeWithNewPlacesApiEnabled(context, apiKey)
        return Places.createClient(context)
    }

}