pluginManagement {
    val testAgp: String by extra
    val agpVersion: String by extra

    repositories {
        if (testAgp == "true") {
            mavenLocal()
        }
        mavenCentral()
        gradlePluginPortal()
        google()
    }

    resolutionStrategy {
        eachPlugin {
            val requestedId = requested.id.id
            if (requestedId == "com.android.application" || requestedId == "com.android.library") {
                useVersion(agpVersion)
            }
        }
    }
}


include(":sample-lib")
include(":app")

rootProject.name = "kace-sample"
val testAgp: String by extra
if (testAgp != "true") {
    includeBuild("../../Kace")
}