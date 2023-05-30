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
import com.android.build.gradle.api.AndroidSourceSet
import org.gradle.api.Project
import org.w3c.dom.Document
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

internal fun getApplicationPackage(
    androidExtension: BaseExtension,
    project: Project,
    mainSourceSet: AndroidSourceSet,
): String {
    val manifestFile = mainSourceSet.manifest.srcFile
    val applicationPackage = getApplicationPackage(androidExtension, manifestFile)

    if (applicationPackage == null) {
        project.logger.warn(
            "Application package name is not present in the manifest file " +
                "(${manifestFile.absolutePath})",
        )

        return ""
    } else {
        return applicationPackage
    }
}

private fun getApplicationPackage(androidExtension: BaseExtension, manifestFile: File): String? {
    // Starting AGP 7 the package can be set via the DSL namespace value:
    //
    // android {
    //   namespace "com.example"
    // }
    //
    // instead of via the "package" attribute in the manifest file.
    //
    // Starting AGP 8, the package *must* be set via the DSL and the manifest file
    // attribute cannot be used.
    //
    // See https://issuetracker.google.com/issues/172361895
    //
    // Therefore, we try to get the package from there first. Since we support AGP versions
    // prior to AGP 7, we need to reflectively find and call it.
    try {
        val method = androidExtension.javaClass.getDeclaredMethod("getNamespace")
        val result = method.invoke(androidExtension)
        if (result is String && result.isNotEmpty()) {
            return result
        }
    } catch (e: ReflectiveOperationException) {
        // Ignore and try parsing manifest.
    }

    // Didn't find the namespace getter, or it was not set. Try parsing the
    // manifest to find the "package" attribute from there.
    try {
        return manifestFile.parseXml().documentElement.getAttribute("package")
    } catch (e: Exception) {
        return null
    }
}

private fun File.parseXml(): Document {
    val factory = DocumentBuilderFactory.newInstance()
    val builder = factory.newDocumentBuilder()
    return builder.parse(this)
}
