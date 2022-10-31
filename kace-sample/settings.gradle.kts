pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        google()
    }
}


include(":sample-lib")
include(":app")

rootProject.name = "kace-sample"

includeBuild("../../Kace")