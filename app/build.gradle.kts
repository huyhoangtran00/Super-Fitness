plugins {
    id("com.google.gms.google-services")
    id("kotlin-kapt")  // Đảm bảo rằng bạn đã thêm dòng này để sử dụng KAPT

    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.superfitness"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.superfitness"
        minSdk = 26
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
val hiltVersion by extra("2.54")
val hiltCompilerVersion by extra("1.2.0")
val activityVersion by extra("1.9.3")
val composeNavigationVersion by extra("2.8.5")
val composeHiltNavigationVersion by extra("1.2.0")
dependencies {
    implementation(platform("com.google.firebase:firebase-bom:33.10.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.accompanist:accompanist-permissions:0.37.2")

    // Google Maps SDK for Android
    implementation(libs.places)
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)

    // Google maps Compose
    implementation(libs.maps.compose)
    implementation(libs.timber)


    // Room dependencies

    implementation ("androidx.room:room-runtime:2.7.0")
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.runtime.livedata)
    kapt( "androidx.room:room-compiler:2.7.0")
    implementation( "androidx.room:room-ktx:2.7.0")
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
    implementation(libs.charts)
    implementation(libs.okhttp)


    implementation("androidx.activity:activity-compose:$activityVersion")
    implementation("androidx.navigation:navigation-compose:$composeNavigationVersion")
    implementation("androidx.hilt:hilt-navigation-compose:$composeHiltNavigationVersion")
    implementation("io.coil-kt.coil3:coil-compose:3.1.0")
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.1.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")

    // Retrofit
    implementation( "com.squareup.retrofit2:retrofit:2.9.0")
    implementation( "com.squareup.retrofit2:converter-moshi:2.9.0")
    implementation( "com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.3")
}