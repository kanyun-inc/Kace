plugins {
    id("java-gradle-plugin")
    kotlin("jvm")
    id("com.github.gmazzo.buildconfig")
    `maven-publish`
    id("com.vanniktech.maven.publish")
}

dependencies {
    implementation(kotlin("gradle-plugin"))
    implementation(kotlin("stdlib-jdk8"))

    compileOnly("com.android.tools.build:gradle:4.2.0")
    implementation("org.jetbrains.kotlin:kotlin-compiler-embeddable")
}

buildConfig {
    val compilerPluginProject = project(":kace-compiler")
    packageName("${compilerPluginProject.group}")
    buildConfigField("String", "KOTLIN_PLUGIN_ID", "\"${property("KOTLIN_PLUGIN_ID")}\"")
    buildConfigField("String", "KOTLIN_PLUGIN_GROUP", "\"${compilerPluginProject.group}\"")
    buildConfigField("String", "KOTLIN_PLUGIN_NAME", "\"${compilerPluginProject.property("POM_ARTIFACT_ID")}\"")
    buildConfigField("String", "KOTLIN_PLUGIN_VERSION", "\"${compilerPluginProject.version}\"")
}

gradlePlugin {
    plugins {
        create("KaceGradlePlugin") {
            id = project.properties["KOTLIN_PLUGIN_ID"] as String
            displayName = "Kotlin Kace plugin"
            description = "Kotlin Kace plugin"
            implementationClass = "com.kanyun.kace.gradle.KaceGradlePlugin"
        }
    }
}