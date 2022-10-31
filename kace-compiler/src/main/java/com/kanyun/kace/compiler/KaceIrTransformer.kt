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

package com.kanyun.kace.compiler

import com.kanyun.kace.compiler.utils.ANDROID_EXTENSIONS_FQNAME
import com.kanyun.kace.compiler.utils.DELEGATE_FIELD_NAME
import com.kanyun.kace.compiler.utils.FIND_VIEW_BY_ID_CACHED_NAME
import com.kanyun.kace.compiler.utils.addOverride
import com.kanyun.kace.compiler.utils.findViewByIdCached
import com.kanyun.kace.compiler.utils.irThis
import com.kanyun.kace.compiler.utils.isAndroidExtensions
import com.kanyun.kace.compiler.utils.symbolOfAndroidExtensionImpl
import com.kanyun.kace.compiler.utils.typeOfAndroidExtensionsBase
import com.kanyun.kace.compiler.utils.typeOfView
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.IrBlockBodyBuilder
import org.jetbrains.kotlin.ir.builders.Scope
import org.jetbrains.kotlin.ir.builders.declarations.addField
import org.jetbrains.kotlin.ir.builders.declarations.addTypeParameter
import org.jetbrains.kotlin.ir.builders.declarations.addValueParameter
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irExprBody
import org.jetbrains.kotlin.ir.builders.irGet
import org.jetbrains.kotlin.ir.builders.irGetField
import org.jetbrains.kotlin.ir.builders.irReturn
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.types.impl.IrUninitializedType
import org.jetbrains.kotlin.ir.types.makeNullable
import org.jetbrains.kotlin.ir.util.SYNTHETIC_OFFSET
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid

class KaceIrTransformer(private val context: IrPluginContext) : IrElementTransformerVoid() {

    override fun visitClass(declaration: IrClass): IrStatement {
        if (declaration.isAndroidExtensions()) {
            val originalFunction = declaration.findViewByIdCached(context)
            if (originalFunction == null || originalFunction.isFakeOverride) {
                if (originalFunction != null) {
                    declaration.declarations.remove(originalFunction)
                }

                val androidExtensionImpl = context.symbolOfAndroidExtensionImpl()

                // private val $$androidExtensionsImpl = AndroidExtensionsImpl()
                val androidExtensionsField = declaration.addField(
                    DELEGATE_FIELD_NAME,
                    androidExtensionImpl.defaultType
                ).apply {
                    initializer = DeclarationIrBuilder(
                        context,
                        symbol,
                        symbol.owner.startOffset,
                        symbol.owner.endOffset
                    ).run {
                        irExprBody(irCall(androidExtensionImpl.constructors.first()))
                    }
                }

                // override fun <T> findViewByIdCached(owner, id) = ...
                declaration.addOverride(
                    ANDROID_EXTENSIONS_FQNAME,
                    FIND_VIEW_BY_ID_CACHED_NAME,
                    IrUninitializedType
                ).apply {
                    val parameterT = addTypeParameter("T", context.typeOfView())
                    returnType = parameterT.defaultType.makeNullable()

                    addValueParameter("owner", context.typeOfAndroidExtensionsBase())
                    addValueParameter("id", context.symbols.int.defaultType)

                    body = IrBlockBodyBuilder(
                        context,
                        Scope(this.symbol),
                        SYNTHETIC_OFFSET,
                        SYNTHETIC_OFFSET
                    ).apply {
                        val androidExtensionsValue = irGetField(irThis(), androidExtensionsField)
                        +irReturn(
                            irCall(
                                androidExtensionImpl.owner.findViewByIdCached(this@KaceIrTransformer.context)!!.symbol
                            ).apply {
                                dispatchReceiver = androidExtensionsValue

                                valueParameters.forEachIndexed { index, irValueParameter ->
                                    putValueArgument(
                                        index,
                                        irGet(irValueParameter.type, irValueParameter.symbol)
                                    )
                                }
                            })
                    }.doBuild()
                }
            }
        }
        return super.visitClass(declaration)
    }
}