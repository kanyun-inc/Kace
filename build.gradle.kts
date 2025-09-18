import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "7.3.1" apply false
    id("com.android.library") version "7.3.1" apply false

    // Use Kotlin 2.0.0 to build gradle plugin and compiler plugin for 2.1.0
    // to avoid incompatible issue with gradle build.
    id("org.jetbrains.kotlin.android") version "2.2.0" apply false
    id("org.jetbrains.kotlin.jvm") version "2.2.0" apply false

    id("com.vanniktech.maven.publish") version "0.18.0" apply false
    id("com.github.gmazzo.buildconfig") version "2.1.0" apply false
}

allprojects {
    repositories {
        mavenCentral()
        google()
        maven { setUrl("https://s01.oss.sonatype.org/content/repositories/snapshots/") }
    }

    apply(plugin = "com.vanniktech.maven.publish")

    pluginManager.withPlugin("java") {
        extensions.getByType<JavaPluginExtension>().sourceCompatibility = JavaVersion.VERSION_1_8
    }
    pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
        extensions.getByType<KotlinJvmProjectExtension>().compilerOptions.jvmTarget.set(JvmTarget.JVM_1_8)
    }
    pluginManager.withPlugin("org.jetbrains.kotlin.android") {
        extensions.getByType<KotlinAndroidProjectExtension>().compilerOptions.jvmTarget.set(JvmTarget.JVM_1_8)
    }
}
