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

package com.kanyun.kace.gradle.utils

import com.android.build.gradle.BaseExtension
import com.kanyun.kace.gradle.LayoutDir
import java.io.File
import java.lang.reflect.Field
import org.gradle.api.Plugin
import org.gradle.api.Project

internal fun Project.withAllPlugins(vararg pluginIds: String, action: (List<Plugin<*>>) -> Unit) {
    return withAllPlugins(pluginIds.toList(), action)
}

internal fun Project.withAllPlugins(pluginIds: List<String>, action: (List<Plugin<*>>) -> Unit) {
    when {
        pluginIds.size > 1 -> {
            val left = pluginIds.subList(0, pluginIds.size - 1)
            val right = pluginIds.last()
            plugins.withId(right) { plugin ->
                withAllPlugins(left) { plugins ->
                    action(plugins + plugin)
                }
            }
        }
        pluginIds.size == 1 -> {
            plugins.withId(pluginIds[0]) {
                action(listOf(it))
            }
        }
        else -> {
            // empty pluginIds, do noting.
        }
    }
}

internal fun String.toCamelCase(): String {
    return split('-', '_').joinToString("") {
        it.replaceFirstChar { item -> item.uppercaseChar() }
    }
}

private inline fun String.replaceFirstChar(transform: (Char) -> Char): String {
    return if (isNotEmpty()) transform(this[0]) + substring(1) else this
}

private fun Char.uppercaseChar(): Char = Character.toUpperCase(this)

internal fun addSourceSetLayoutDir(
    extension: BaseExtension,
    layoutVariantName: String,
    layoutDirList: MutableList<LayoutDir>,
) {
    val sourceSet = extension.sourceSets.findByName(layoutVariantName) ?: return
    val srcDirs = sourceSet.res.getSourceDirectoryTrees()
    srcDirs.forEach { fileTree ->
        fileTree.dir.listFiles()?.forEach { dir ->
            if (dir.name.startsWith("layout") && dir.isDirectory) {
                layoutDirList.add(LayoutDir(dir, layoutVariantName))
            }
        }
    }
}

internal fun addCustomVariantLayoutDir(
    customVariant: Map<String, List<String>>,
    layoutDirList: MutableList<LayoutDir>
) {
    customVariant.forEach { item ->
        if (layoutDirList.none { it.layoutVariantName == item.key }) {
            item.value.map { File(it) }.filter { it.exists() }.forEach { resDir ->
                resDir.listFiles()?.forEach { dir ->
                    if (dir.name.startsWith("layout") && dir.isDirectory) {
                        layoutDirList.add(LayoutDir(dir, item.key))
                    }
                }
            }
        }
    }
}

@Throws(NoSuchFieldException::class, IllegalAccessException::class)
fun getFieldValue(instance: Any, name: String): Any? {
    val clazz: Class<*> = instance.javaClass
    val field = getField(clazz, name)
    field.isAccessible = true
    return field[instance]
}

@Throws(NoSuchFieldException::class)
private fun getField(clazz: Class<*>, name: String): Field {
    return try {
        clazz.getDeclaredField(name)
    } catch (e: NoSuchFieldException) {
        if (clazz.superclass != null) {
            getField(clazz.superclass, name)
        } else {
            throw e
        }
    }
}
