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

internal object AndroidConst {
    const val VIEW_FQNAME = "android.view.View"

    const val ID_ATTRIBUTE_NO_NAMESPACE: String = "id"
    const val CLASS_ATTRIBUTE_NO_NAMESPACE: String = "class"

    private const val IDENTIFIER_WORD_REGEX = "[(?:\\p{L}\\p{M}*)0-9_\\.\\:\\-]+"
    val IDENTIFIER_REGEX = "^@(\\+)?(($IDENTIFIER_WORD_REGEX)\\:)?id\\/($IDENTIFIER_WORD_REGEX)$".toRegex()

    val IGNORED_XML_WIDGET_TYPES = setOf("requestFocus", "merge", "tag", "check", "blink")
    val FQNAME_RESOLVE_PACKAGES = listOf("android.widget", "android.webkit", "android.view")
}

internal fun androidIdToName(id: String): ResourceIdentifier? {
    val values = AndroidConst.IDENTIFIER_REGEX.matchEntire(id)?.groupValues ?: return null
    val packageName = values[3]

    return ResourceIdentifier(
        getJavaIdentifierNameForResourceName(values[4]),
        if (packageName.isEmpty()) null else packageName
    )
}

internal fun getJavaIdentifierNameForResourceName(styleName: String) = buildString {
    for (char in styleName) {
        when (char) {
            '.', '-', ':' -> append('_')
            else -> append(char)
        }
    }
}

internal fun isWidgetTypeIgnored(xmlType: String): Boolean {
    return (xmlType.isEmpty() || xmlType in AndroidConst.IGNORED_XML_WIDGET_TYPES)
}