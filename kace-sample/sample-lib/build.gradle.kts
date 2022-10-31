plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
    id("com.kanyun.kace")
}

val vgoCompileSdkVersion: String by extra
val vgoMinSdkVersion: String by extra
val vgoTargetSdkVersion: String by extra

android {
    compileSdk = vgoCompileSdkVersion.toInt()

    defaultConfig {
        minSdk = vgoMinSdkVersion.toInt()
        targetSdk = vgoTargetSdkVersion.toInt()

        consumerProguardFiles("proguard-rules.pro")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        viewBinding = true
    }

    resourcePrefix = "sample_lib"
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.4.1")
}
