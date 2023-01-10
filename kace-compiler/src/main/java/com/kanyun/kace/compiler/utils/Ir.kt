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

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.util.functions

fun IrClass.findViewByIdCached(pluginContext: IrPluginContext): IrSimpleFunction? {
    return functions.find {
        it.isFindViewByIdCached(pluginContext)
    }
}

fun IrFunction.isFindViewByIdCached(pluginContext: IrPluginContext): Boolean {
    return name.identifier == FIND_VIEW_BY_ID_CACHED_NAME &&
        valueParameters.size == 2 &&
        valueParameters[0].type == pluginContext.referenceClass(
        ANDROID_EXTENSIONS_BASE_FQNAME
    )?.defaultType &&
        valueParameters[1].type == pluginContext.symbols.int.defaultType
}

fun IrClass.isAndroidExtensions(): Boolean {
    return superTypes.any { it.classFqName == ANDROID_EXTENSIONS_FQNAME }
}

fun IrPluginContext.typeOfAndroidExtensionsBase() =
    referenceClass(ANDROID_EXTENSIONS_BASE_FQNAME)!!.defaultType

fun IrPluginContext.typeOfView() =
    referenceClass(ANDROID_VIEW_FQNAME)!!.defaultType

fun IrPluginContext.symbolOfAndroidExtensionImpl() =
    referenceClass(ANDROID_EXTENSIONS_IMPL_FQNAME)!!
