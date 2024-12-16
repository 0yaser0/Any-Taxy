plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.0"
}

android {
    namespace = "com.cmc.mytaxi"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.cmc.mytaxi"
        minSdk = 27
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
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
        viewBinding = true
        dataBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
}

dependencies {
    implementation(libs.easypermissions)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)
    implementation(libs.androidx.material3.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //Circular Indicator
    implementation ("me.relex:circleindicator:2.1.6")

    //Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    //noinspection KaptUsageInsteadOfKsp
    kapt(libs.androidx.room.compiler)

    //Kotlin Coroutines
    implementation(libs.kotlinx.coroutines.android)

    //ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    // Bar Code
    implementation(libs.zxing.android.embedded)

    //google maps
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)
    implementation(libs.easypermissions)

    //Glide
    implementation(libs.glide)
    annotationProcessor(libs.compiler)

    //lottie
    implementation (libs.lottie)

    //animated ButtomBar
    implementation (libs.androidx.databinding.runtime)
    implementation(libs.animated.navigation.bar)
    implementation (libs.androidx.navigation.compose)


}
