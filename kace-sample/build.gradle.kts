// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "4.2.0" apply false
    id("com.android.library") version "4.2.0" apply false
    id("org.jetbrains.kotlin.android") version "1.7.10" apply false
    id("com.kanyun.kace") apply false
}

tasks.register<Delete>(name = "clean") {
    group = "build"
    delete(rootProject.buildDir)
}

allprojects {
    repositories {
        google()
        mavenCentral()
        mavenLocal()
    }
}