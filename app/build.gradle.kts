plugins {
    id("com.google.gms.google-services")
    id("kotlin-kapt")  // Đảm bảo rằng bạn đã thêm dòng này để sử dụng KAPT
    id ("dagger.hilt.android.plugin") // Thêm plugin Hilt

    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.superfitness"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.superfitness"
        minSdk = 24
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
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }



}

dependencies {
    implementation(platform("com.google.firebase:firebase-bom:33.10.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation ("com.google.dagger:hilt-android:2.48")  // Phiên bản mới nhất
    kapt("com.google.dagger:hilt-android-compiler:2.48")
    implementation ("androidx.hilt:hilt-navigation-compose:1.2.0")

    implementation("com.google.accompanist:accompanist-permissions:0.37.2")

    // Google Maps SDK for Android
    implementation(libs.places)
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)

    // Google maps Compose
    implementation(libs.maps.compose)
    implementation(libs.timber)


    // Room dependencies

    implementation ("androidx.room:room-runtime:2.6.1")
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.runtime.livedata)
    kapt( "androidx.room:room-compiler:2.6.1")
    implementation( "androidx.room:room-ktx:2.6.1")
    implementation("com.github.PhilJay:MPAndroidChart:3.1.0")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation("androidx.compose.material3:material3:1.1.2") // Hoặc phiên bản mới nhất
    implementation("androidx.compose.material:material-icons-extended:1.7.7")
    implementation("androidx.compose.material3:material3-window-size-class:1.3.1")
    implementation("androidx.compose.material3:material3-adaptive-navigation-suite:1.4.0-alpha11")
    implementation ("com.airbnb.android:lottie-compose:6.1.0")
    implementation ("androidx.datastore:datastore-preferences:1.0.0")
    implementation ("androidx.lifecycle:lifecycle-runtime-compose:2.6.0")

    implementation(libs.androidx.room.common)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.appcompat)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}