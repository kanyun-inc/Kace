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

import com.kanyun.kace.gradle.utils.ResourceIdentifier
import com.kanyun.kace.gradle.utils.toCamelCase
import java.io.File

internal data class LayoutNodeItem constructor(
    val identifier: ResourceIdentifier,
    val xmlType: String
) {
    val viewNameWithPackage: String
        get() {
            return if (xmlType.contains(".")) {
                xmlType
            } else {
                when (xmlType) {
                    "WebView" -> {
                        "android.webkit.WebView"
                    }
                    "View", "ViewStub" -> {
                        "android.view.$xmlType"
                    }
                    else -> {
                        "android.widget.$xmlType"
                    }
                }
            }
        }
    val viewId: String
        get() = identifier.name
}

data class LayoutItem(
    val destDir: File,
    val layoutFile: File,
    val variantName: String,
    private val kaePackagePrefix: String = "kotlinx.android.synthetic"
) {
    private val kaePackage: String
        get() = "$kaePackagePrefix.$variantName"
    private val kaePackageDir: String
        get() = kaePackage.replace(".", "/")
    private val layoutName: String
        get() = layoutFile.name.split(".")[0]
    val targetDir: File
        get() = File(destDir, "$kaePackageDir/$layoutName")
    val targetViewExtensionDir: File
        get() = File(targetDir, "view")
    val targetFilePackageName: String
        get() = "$kaePackage.$layoutName"
    val targetFileName: String
        get() = "${layoutName.toCamelCase()}.kt"
}

internal data class LayoutDir(
    val layoutDir: File,
    val layoutVariantName: String
)