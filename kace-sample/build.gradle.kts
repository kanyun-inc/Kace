// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") apply false
    id("com.android.library") apply false
    id("org.jetbrains.kotlin.android") version "1.9.10" apply false
    id("com.kanyun.kace") version "0.0.0-SNAPSHOT" apply false
}

tasks.register<Delete>(name = "clean") {
    group = "build"
    delete(rootProject.buildDir)
}

allprojects {
    repositories {
        if (extra["testAgp"] == "true") {
            mavenLocal()
        }
        google()
        mavenCentral()
    }
}