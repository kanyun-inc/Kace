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

import com.kanyun.kace.gradle.LayoutNodeItem
import java.io.File
import java.io.FileInputStream
import javax.xml.parsers.SAXParser
import javax.xml.parsers.SAXParserFactory
import org.slf4j.Logger

internal fun parseXml(saxParser: SAXParser, file: File, logger: Logger): List<LayoutNodeItem> {
    val inputStream = FileInputStream(file)
    val list = mutableListOf<LayoutNodeItem>()
    try {
        saxParser.parse(
            inputStream,
            AndroidXmlHandler { id, tag ->
                val safeTag = tag.replace("$", ".")
                val resource = parseAndroidResource(id, safeTag)
                if (resource is AndroidResource.Widget) {
                    list.add(LayoutNodeItem(resource.id, resource.xmlType))
                }
            }
        )
    } catch (e: Exception) {
        logger.error("Layout Parse error: ${e.message} at ${file.absolutePath}}.")
    }
    return list
}

private fun parseAndroidResource(id: ResourceIdentifier, tag: String): AndroidResource {
    return when (tag) {
        "fragment" -> AndroidResource.Fragment(id)
        "include" -> AndroidResource.Widget(id, AndroidConst.VIEW_FQNAME)
        else -> AndroidResource.Widget(id, tag)
    }
}

internal fun initSAX(): SAXParser {
    val saxFactory = SAXParserFactory.newInstance()
    saxFactory.isNamespaceAware = true
    return saxFactory.newSAXParser()
}
