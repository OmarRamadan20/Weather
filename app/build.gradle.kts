import org.gradle.kotlin.dsl.androidTestImplementation
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id ("kotlin-parcelize")
    id ("com.google.devtools.ksp") version "2.1.0-1.0.29"
}

android {
    namespace = "com.example.weather"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.weather"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        val properties = Properties()
        val localPropertiesFile = project.rootProject.file("local.properties")

        if (localPropertiesFile.exists()) {
            properties.load(localPropertiesFile.inputStream())
        }

        val apiKey = properties.getProperty("MAP_API_KEY") ?: ""
        buildConfigField("String", "MAP_API_KEY", "\"$apiKey\"")

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
        buildConfig =true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.animation)
    implementation(libs.androidx.compose.remote.creation.core)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.compose.ui.text)
    implementation(libs.ads.mobile.sdk)
    implementation(libs.firebase.components)
    implementation(libs.androidx.navigation.compose)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)


    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // Lifecycle
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // Network
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    val room_version = "2.6.1"

    implementation("androidx.room:room-runtime:$room_version")

    implementation("androidx.room:room-ktx:$room_version")



    implementation("io.coil-kt:coil-compose:2.6.0")

    implementation(libs.glide.compose)

    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.2")


    implementation ("com.github.bumptech.glide:compose:1.0.0-beta01")

    implementation ("androidx.compose.material:material-icons-extended")
    // أو لو بتستخدم الـ BOM (وده الأفضل)
    implementation ("androidx.compose.material:material-icons-extended")


    implementation("com.google.maps.android:maps-compose:4.3.3")
    implementation("com.google.accompanist:accompanist-permissions:0.34.0")


    implementation("com.google.android.libraries.places:places:3.3.0")

    ksp("androidx.room:room-compiler:$room_version")


    val work_version = "2.9.0"
    implementation("androidx.work:work-runtime-ktx:$work_version")


    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // Coroutines (ضرورية للعمل مع DataStore و Room)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")


    // AndroidX Test - Instrumental
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test:core-ktx:1.5.0")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.room:room-testing:2.6.1")
    androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    androidTestImplementation("androidx.arch.core:core-testing:2.2.0")

    // Unit Tests
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    testImplementation("io.mockk:mockk:1.13.8")
    testImplementation("androidx.test:core-ktx:1.5.0")
    testImplementation("org.robolectric:robolectric:4.11.1")
    testImplementation("androidx.test.ext:junit-ktx:1.1.5")


    implementation("androidx.core:core-splashscreen:1.0.1")
    // الـ Lottie لـ Compose
    implementation("com.airbnb.android:lottie-compose:6.1.0")



}


