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

package com.kanyun.kace.gradle

import com.android.build.gradle.AppExtension
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.api.BaseVariant
import com.kanyun.kace.BuildConfig
import com.kanyun.kace.gradle.utils.addCustomVariantLayoutDir
import com.kanyun.kace.gradle.utils.addSourceSetLayoutDir
import com.kanyun.kace.gradle.utils.configSourceSetDir
import com.kanyun.kace.gradle.utils.getApplicationPackage
import com.kanyun.kace.gradle.utils.withAllPlugins
import java.io.File
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

class KaceGradlePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val kaceExtension = target.extensions.create("kace", KaceExtension::class.java)
        addKaceRuntime(target)
        target.plugins.apply(KaceSubPlugin::class.java)
        configTask(target, kaceExtension)
    }

    private fun addKaceRuntime(project: Project) {
        project.configurations.all { configuration ->
            val name = configuration.name
            if (name != "api") return@all

            configuration.dependencies.add(
                project.dependencies.create(
                    "${BuildConfig.KOTLIN_PLUGIN_GROUP}:kace-runtime:${BuildConfig.KOTLIN_PLUGIN_VERSION}"
                )
            )
        }
    }

    private fun configTask(target: Project, kaceExtension: KaceExtension) {
        configVariants(target) { extension, variant ->
            val variantCapitalizeName = variant.name.capitalize()
            val compileKotlin = target.tasks.getByName("compile${variantCapitalizeName}Kotlin")
            val sourceOutputDir =
                File(target.buildDir, "generated/source/kace/${variant.dirName}")
            val sourceSet = extension.sourceSets.getByName(variant.name)
            configSourceSetDir(sourceSet, sourceOutputDir, target.logger)

            val relativePath =
                target.projectDir.toPath().relativize(sourceOutputDir.toPath()).toString()
            (compileKotlin as KotlinCompile).source(relativePath)
            val task = target.tasks.register(
                "generate${variantCapitalizeName}KaceCode", KaceGenerateTask::class.java
            ) { task ->
                val mainSourceSet = extension.sourceSets.getByName("main")
                val layoutDirList = getLayoutDirList(extension, variant, kaceExtension)

                task.layoutDirs.from(layoutDirList.map { it.layoutDir })
                task.layoutVariantMap.set(layoutDirList.associate { it.layoutDir.absolutePath to it.layoutVariantName })
                task.namespace.set(getApplicationPackage(extension, target, mainSourceSet))
                task.whiteList.set(kaceExtension.whiteList)
                task.blackList.set(kaceExtension.blackList)
                task.sourceOutputDir.set(sourceOutputDir)
            }

            compileKotlin.dependsOn(task)
            target.plugins.withId("com.google.devtools.ksp") {
                target.afterEvaluate {
                    val kspKotlin =
                        target.tasks.findByName("ksp${variantCapitalizeName}Kotlin")
                    kspKotlin?.dependsOn(task)
                }
            }
        }
    }

    private fun getLayoutDirList(
        extension: BaseExtension,
        variant: BaseVariant,
        kaceExtension: KaceExtension
    ): List<LayoutDir> {
        val layoutDirList = mutableListOf<LayoutDir>()
        val buildTypeName = variant.buildType.name
        val flavorName = variant.flavorName
        val variantName = variant.name

        addSourceSetLayoutDir(extension, "main", layoutDirList)
        addSourceSetLayoutDir(extension, buildTypeName, layoutDirList)
        addSourceSetLayoutDir(extension, flavorName, layoutDirList)
        extension.productFlavors.configureEach {
            if (it.name.isNotEmpty() && it.name != flavorName && it.name != buildTypeName) {
                addSourceSetLayoutDir(extension, it.name, layoutDirList)
            }
        }
        if (buildTypeName != variantName && buildTypeName != flavorName) {
            addSourceSetLayoutDir(extension, variantName, layoutDirList)
        }

        val customVariant = kaceExtension.customVariantCallbacks.map { it.invoke(variant) }
            .fold(HashMap<String, List<String>>(kaceExtension.customVariant)) { acc, customVariant ->
                customVariant.forEach {
                    acc.merge(it.key, it.value, List<String>::plus)
                }
                acc
            }

        addCustomVariantLayoutDir(customVariant, layoutDirList)
        return layoutDirList
    }

    private fun configVariants(
        target: Project,
        action: (BaseExtension, BaseVariant) -> Unit
    ) {
        target.withAllPlugins("com.android.library") {
            val extension = target.extensions.getByType(LibraryExtension::class.java)
            extension.libraryVariants.all {
                action(extension, it)
            }
        }
        target.withAllPlugins("com.android.application") {
            val extension = target.extensions.getByType(AppExtension::class.java)
            extension.applicationVariants.all {
                action(extension, it)
            }
        }
    }
}
