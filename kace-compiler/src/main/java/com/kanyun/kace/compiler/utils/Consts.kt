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

package com.kanyun.kace.compiler.utils

import org.jetbrains.kotlin.name.FqName

val ANDROID_EXTENSIONS_PACKAGE_NAME = "com.kanyun.kace"
val ANDROID_EXTENSIONS_CLASS_NAME = "AndroidExtensions"
val ANDROID_EXTENSIONS_FULL_NAME = "com.kanyun.kace.AndroidExtensions"

val ANDROID_EXTENSIONS_FQNAME = FqName(ANDROID_EXTENSIONS_FULL_NAME)
val ANDROID_EXTENSIONS_BASE_FQNAME = FqName("com.kanyun.kace.AndroidExtensionsBase")
val ANDROID_EXTENSIONS_IMPL_FQNAME = FqName("com.kanyun.kace.AndroidExtensionsImpl")

val ANDROID_VIEW_FQNAME = FqName("android.view.View")

val IMPLICIT_ANDROID_EXTENSIONS_TYPES = setOf(
    "android.app.Activity",
    "androidx.fragment.app.Fragment"
)

const val FIND_VIEW_BY_ID_CACHED_NAME = "findViewByIdCached"

const val DELEGATE_FIELD_NAME = "\$\$androidExtensionsImpl"

