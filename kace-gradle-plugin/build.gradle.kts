/*
 * Copyright (C) 2022 KanYun
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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