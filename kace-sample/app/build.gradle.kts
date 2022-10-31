plugins {
    id("com.android.application")
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
        applicationId = "com.kanyun.kace.sample"
        minSdk = vgoMinSdkVersion.toInt()
        targetSdk = vgoTargetSdkVersion.toInt()
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        getByName("release")  {
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

    flavorDimensions += listOf("api", "mode")

    productFlavors {
        create("demo") {
            // Assigns this product flavor to the "mode" flavor dimension.
            dimension = "mode"
        }

        create("full") {
            dimension = "mode"
        }

        create("minApi23") {
            dimension = "api"
            minSdk = 23
            versionCode = 20000 + (android.defaultConfig.versionCode ?: 0)
            versionNameSuffix = "-minApi23"
        }

        create("minApi21") {
            dimension = "api"
            minSdk = 21
            versionCode = 10000 + (android.defaultConfig.versionCode ?: 0)
            versionNameSuffix = "-minApi21"
        }

    }

    dependencies {
        implementation("androidx.core:core-ktx:1.7.0")
        implementation("androidx.appcompat:appcompat:1.4.1")
        implementation("com.google.android.material:material:1.5.0")
        implementation("androidx.constraintlayout:constraintlayout:2.1.3")
        implementation("androidx.navigation:navigation-fragment-ktx:2.4.1")
        implementation("androidx.navigation:navigation-ui-ktx:2.4.1")

        // custom deps below
        implementation(project(":sample-lib"))
    }
}
