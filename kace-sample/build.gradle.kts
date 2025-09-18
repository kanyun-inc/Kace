// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "7.3.1" apply false
    id("com.android.library") version "7.3.1" apply false
    id("org.jetbrains.kotlin.jvm") version "2.2.0" apply false
    id("org.jetbrains.kotlin.android") version "2.2.0" apply false
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
