// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "4.2.2" apply false
    id("com.android.library") version "4.2.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0-RC" apply false
    id("com.vanniktech.maven.publish") version "0.18.0" apply false
    id("com.github.gmazzo.buildconfig") version "2.1.0" apply false
}

allprojects {
    repositories {
        mavenCentral()
        maven { setUrl("https://oss.sonatype.org/content/repositories/snapshots/") }
    }

    apply(plugin = "com.vanniktech.maven.publish")

    pluginManager.withPlugin("java") {
        extensions.getByType<JavaPluginExtension>().sourceCompatibility = JavaVersion.VERSION_1_8
    }
}
