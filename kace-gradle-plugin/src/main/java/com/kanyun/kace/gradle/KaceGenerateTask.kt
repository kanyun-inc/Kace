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

import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileCollection
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.work.ChangeType
import org.gradle.work.Incremental
import org.gradle.work.InputChanges
import org.gradle.workers.WorkerExecutor
import org.jetbrains.kotlin.incremental.ChangedFiles
import java.io.File
import javax.inject.Inject

abstract class KaceGenerateTask : DefaultTask() {

    @get:Internal
    val layoutDirs: ConfigurableFileCollection = project.files()

    @get:Incremental
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    internal open val androidLayoutResources: FileCollection = layoutDirs
        .asFileTree
        .matching { patternFilterable ->
            patternFilterable.include("**/*.xml")
        }

    @get:Input
    abstract val layoutVariantMap: MapProperty<String, String>

    @get:Input
    abstract val namespace: Property<String>

    @get:Input
    abstract val whiteList: ListProperty<String>

    @get:Input
    abstract val blackList: ListProperty<String>

    @get:OutputDirectory
    abstract val sourceOutputDir: DirectoryProperty

    @get:Internal
    val projectName: String = project.name

    @get:Inject
    abstract val workerExecutor: WorkerExecutor

    @TaskAction
    fun action(inputChanges: InputChanges) {
        val startTime = System.currentTimeMillis()
        val workQueue = workerExecutor.noIsolation()
        val destDir = sourceOutputDir.asFile.get()
        val changeFiles = getChangedFiles(inputChanges, androidLayoutResources)
        cleanTargetDir(changeFiles, destDir)
        val changedLayoutList = getChangedLayoutList(changeFiles)
        val changedLayoutItemList = changedLayoutList.map { getLayoutItem(it) }
        changedLayoutItemList.forEach { item ->
            workQueue.submit(KaceGenerateAction::class.java) { parameters ->
                parameters.destDir.set(destDir)
                parameters.layoutFile.set(item.layoutFile)
                parameters.variantName.set(item.variantName)
                parameters.namespace.set(namespace)
            }
        }
        workQueue.await()
        val duration = System.currentTimeMillis() - startTime
        logger.info("${changedLayoutItemList.size} layouts are processed at $projectName Project, which takes ${duration}ms")
    }

    private fun cleanTargetDir(changeFiles: ChangedFiles, destDir: File) {
        if (changeFiles is ChangedFiles.Known) {
            deleteGeneratedFile(changeFiles.removed)
        } else {
            destDir.listFiles()?.forEach { it.deleteRecursively() }
        }
    }

    private fun getChangedLayoutList(changeFiles: ChangedFiles): List<File> {
        val layoutFileList = mutableListOf<File>()
        if (changeFiles is ChangedFiles.Known) {
            layoutFileList.addAll(changeFiles.modified)
        } else {
            layoutFileList.addAll(androidLayoutResources)
        }
        if (whiteList.get().isNotEmpty()) {
            return layoutFileList.filter { whiteList.get().contains(it.name) }
        }
        if (blackList.get().isNotEmpty()) {
            return layoutFileList.filter { blackList.get().contains(it.name).not() }
        }
        return layoutFileList
    }

    private fun deleteGeneratedFile(removeFiles: List<File>) {
        removeFiles.forEach { file ->
            val item = getLayoutItem(file)
            if (item.targetDir.exists()) {
                item.targetDir.deleteRecursively()
            }
        }
    }

    private fun getLayoutItem(file: File): LayoutItem {
        val destDir = sourceOutputDir.asFile.get()
        val parentDir = file.parentFile.absolutePath
        val layoutVariantName = layoutVariantMap.get()[parentDir] ?: "main"
        return LayoutItem(destDir, file, layoutVariantName)
    }

    private fun getChangedFiles(
        inputChanges: InputChanges,
        layoutResources: FileCollection,
    ) = if (!inputChanges.isIncremental) {
        ChangedFiles.Unknown()
    } else {
        inputChanges.getFileChanges(layoutResources)
            .fold(mutableListOf<File>() to mutableListOf<File>()) { (modified, removed), item ->
                when (item.changeType) {
                    ChangeType.ADDED, ChangeType.MODIFIED -> modified.add(item.file)
                    ChangeType.REMOVED -> removed.add(item.file)
                    else -> Unit
                }
                modified to removed
            }.run {
                ChangedFiles.Known(first, second)
            }
    }
}
