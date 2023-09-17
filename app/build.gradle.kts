
plugins {

    id("com.android.application")
    id ("com.chaquo.python")
    id("org.jetbrains.kotlin.android")
    id ("kotlin-kapt")
    id ("androidx.navigation.safeargs.kotlin")
    id ("kotlin-parcelize")
    id ("com.google.gms.google-services")
    id ("kotlin-android")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.example.stack"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.example.stack"
        minSdk = 28
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        ndk {
            abiFilters += listOf("x86", "x86_64", "armeabi-v7a",
                "arm64-v8a")
        }


    }
    buildFeatures {
        dataBinding = true
        viewBinding = true
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation ("androidx.core:core-ktx:1.8.0")
    implementation ("androidx.appcompat:appcompat:1.6.1")
    implementation ("com.google.android.material:material:1.5.0")
    implementation ("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation ("junit:junit:4.13.2")
    androidTestImplementation ("androidx.test.ext:junit:1.1.5")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.5.1")

    implementation ("androidx.core:core-ktx:1.8.0")
    implementation ("androidx.appcompat:appcompat:1.6.1")
    implementation ("com.google.android.material:material:1.5.0")
    implementation ("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation ("com.google.android.gms:play-services-maps:18.1.0")
    testImplementation ("junit:junit:4.13.2")
    androidTestImplementation ("androidx.test.ext:junit:1.1.5")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.5.1")

    implementation ("androidx.legacy:legacy-support-v4:1.0.0")
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation ("androidx.viewpager2:viewpager2:1.0.0")
    implementation ("com.google.android.material:material:1.2.0-alpha02")
    implementation ("androidx.gridlayout:gridlayout:1.0.0")

    //toolbar
    implementation ("androidx.appcompat:appcompat:1.2.0")
    implementation ("com.google.android.material:material:1.2.1")

    //
    implementation ("com.google.android.material:material:1.2.0")


    //bottom navigation
    val nav_version = "2.5.3"

    // Java language implementation
    implementation("androidx.navigation:navigation-fragment:$nav_version")
    implementation("androidx.navigation:navigation-ui:$nav_version")

    // Kotlin
    implementation("androidx.navigation:navigation-fragment-ktx:$nav_version")
    implementation("androidx.navigation:navigation-ui-ktx:$nav_version")

    // Feature module Support
    implementation("androidx.navigation:navigation-dynamic-features-fragment:$nav_version")

    // Testing Navigation
    androidTestImplementation("androidx.navigation:navigation-testing:$nav_version")

    // Jetpack Compose Integration
    implementation("androidx.navigation:navigation-compose:$nav_version")
    //recycler view
    implementation ("androidx.recyclerview:recyclerview:1.2.0")


    // Moshi
    implementation ("com.squareup.moshi:moshi:1.14.0")
    implementation ("com.squareup.moshi:moshi-kotlin:1.14.0")
    kapt ("com.squareup.moshi:moshi-kotlin-codegen:1.14.0")

    //Gson
    implementation ("com.google.code.gson:gson:2.10.1")

    // Retrofit - Deprecated plugins - No more required
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    // implementation "com.squareup.retrofit2:converter-scalars:$version_retrofit"

    // Retrofit with Moshi Converter
    implementation ("com.squareup.retrofit2:converter-moshi:2.9.0")


    // Coroutines - Deprecated - No more required
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.7")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.7")

    // Retrofit Coroutines Support - Deprecated - No more required
    implementation ("com.jakewharton.retrofit:retrofit2-kotlin-coroutines-adapter:0.9.2")


    //Glide
    //implementation "com.github.bumptech.glide:glide:4.8.0"
    implementation ("com.github.bumptech.glide:glide:4.14.2")
    // Skip this if you don't want to use integration libraries or configure Glide.
    annotationProcessor ("com.github.bumptech.glide:compiler:4.14.2")

    //view pager and tab layout
    implementation ("com.google.android.material:material:1.1.0")
    implementation ("androidx.viewpager2:viewpager2:1.0.0")
    //paging
    implementation ("androidx.paging:paging-runtime-ktx:2.1.1")
    //lottie animation
    implementation ("com.airbnb.android:lottie:6.1.0")
    //room
    implementation ("androidx.room:room-runtime:2.5.0")
    kapt ("androidx.room:room-compiler:2.5.0")
    implementation ("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation ("androidx.room:room-ktx:2.5.0")

    //google maps
    implementation ("com.google.android.gms:play-services-maps:18.1.0")
    implementation ("com.google.android.gms:play-services-location:21.0.1")

    //dagger hilt
    implementation("com.google.dagger:hilt-android:2.44")
    kapt("com.google.dagger:hilt-android-compiler:2.44")

    //firestore
    implementation(platform("com.google.firebase:firebase-bom:32.2.3"))
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")

}
kapt {
    correctErrorTypes = true
}