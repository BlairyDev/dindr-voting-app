import org.gradle.kotlin.dsl.implementation
import org.jetbrains.kotlin.gradle.idea.proto.com.google.protobuf.SourceCodeInfoKt.location

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")

    kotlin("plugin.serialization") version "2.0.21"

    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin") //Added due to Google Places API

}

android {
    namespace = "com.grouptoo.dindr"
    compileSdk = 35
    buildFeatures.buildConfig = true

    defaultConfig {
        applicationId = "com.grouptoo.dindr"
        minSdk = 26 //Originally 24 if there is a problem change it back to this number
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
//        sourceCompatibility = JavaVersion.VERSION_1_8
//        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        buildConfig = true //added due to Google Places API
        compose = true
        viewBinding = true //added due to qrcode generator
    }


}

secrets {
    propertiesFileName = "secrets.properties"
    defaultPropertiesFileName = "local.defaults.properties"
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    //Navigation
    implementation(libs.navigation.compose)

    //Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.database)
    implementation(libs.firebase.auth)

    // Dependency Injection (Hilt)
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)
    implementation(libs.hilt.navigation.compose)


    // Coroutines and Serialization
    implementation(libs.coroutines.android)
    implementation(libs.serialization.json)


    // QR Code Generation (Zxing)
    implementation(libs.zxing.android.embedded)
    implementation(libs.zxing.core)

    // ML Kit Barcode Scanning
    implementation(libs.mlkit.barcode.scanning)
    // Camera with ML Kit
    implementation(libs.camera.mlkit.vision)

    // CameraX
    implementation(libs.camera.camera2)
    implementation(libs.camera.lifecycle)
    implementation(libs.camera.view)

    // Permissions Handling (Accompanist)
    implementation(libs.accompanist.permissions)

    // ViewModel Lifecycle (Compose)
    implementation(libs.lifecycle.viewmodel.compose)

    // Places and Maps SDKs
    implementation(libs.places)

    // Swipeable Cards
    implementation(libs.swipeable.cards)

    // Location Services
    implementation(libs.play.services.location)

    // Image Loading (COIL)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    // Material Design 3 (Compose)
    implementation(libs.material3)

    // Drawable Painter (Accompanist)
    implementation(libs.accompanist.drawablepainter)



}


