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

import com.kanyun.kace.BuildConfig
import com.kanyun.kace.gradle.utils.appendLine
import com.kanyun.kace.gradle.utils.initSAX
import com.kanyun.kace.gradle.utils.parseXml
import java.io.File
import javax.xml.parsers.SAXParser
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.workers.WorkAction
import org.gradle.workers.WorkParameters
import org.slf4j.LoggerFactory

abstract class KaceGenerateAction : WorkAction<KaceGenerateAction.Parameters> {
    interface Parameters : WorkParameters {
        val destDir: DirectoryProperty
        val layoutFile: RegularFileProperty
        val variantName: Property<String>
        val namespace: Property<String>
    }

    private val logger = LoggerFactory.getLogger(KaceGenerateAction::class.java)
    private val saxParser: SAXParser = initSAX()

    override fun execute() {
        val item = LayoutItem(
            parameters.destDir.get().asFile,
            parameters.layoutFile.get().asFile,
            parameters.variantName.get()
        )
        val namespace = parameters.namespace.get()
        val file = item.layoutFile
        val layoutNodeItems = parseXml(saxParser, file, logger)
        writeActivityFragmentExtension(layoutNodeItems, item, namespace)
        writeViewExtension(layoutNodeItems, item, namespace)
    }

    private fun writeActivityFragmentExtension(
        layoutNodeItems: List<LayoutNodeItem>,
        item: LayoutItem,
        namespace: String
    ) {
        item.targetDir.mkdirs()

        File(item.targetDir, item.targetFileName).bufferedWriter().use { writer ->
            writer.appendLine("package ${item.targetFilePackageName}")
            writer.newLine()
            writer.appendLine("import ${BuildConfig.KOTLIN_PLUGIN_GROUP}.AndroidExtensionsBase")
            writer.appendLine("import android.app.Activity")
            writer.appendLine("import androidx.fragment.app.Fragment")
            writer.appendLine("import $namespace.R")
            writer.newLine()

            layoutNodeItems.forEach { item ->
                writer.appendLine("private inline val AndroidExtensionsBase.${item.viewId}")
                writer.appendLine("    get() = findViewByIdCached<${item.viewNameWithPackage}>(this, R.id.${item.viewId})")
                writer.appendLine("internal inline val Activity.${item.viewId}")
                writer.appendLine("    get() = (this as AndroidExtensionsBase).${item.viewId}")
                writer.appendLine("internal inline val Fragment.${item.viewId}")
                writer.appendLine("    get() = (this as AndroidExtensionsBase).${item.viewId}")
                writer.newLine()
            }
        }
    }

    private fun writeViewExtension(
        layoutNodeItems: List<LayoutNodeItem>,
        item: LayoutItem,
        namespace: String
    ) {
        item.targetViewExtensionDir.mkdirs()

        File(item.targetViewExtensionDir, item.targetFileName).bufferedWriter()
            .use { writer ->
                writer.appendLine("package ${item.targetFilePackageName}.view")
                writer.newLine()
                writer.appendLine("import android.view.View")
                writer.appendLine("import $namespace.R")
                writer.newLine()

                layoutNodeItems.forEach { item ->
                    writer.appendLine("internal inline val View.${item.viewId}")
                    writer.appendLine("    get() = findViewById<${item.viewNameWithPackage}>(R.id.${item.viewId})")
                    writer.newLine()
                }
            }
    }
}
