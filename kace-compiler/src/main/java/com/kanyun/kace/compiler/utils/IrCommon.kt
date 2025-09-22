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

import org.jetbrains.kotlin.descriptors.DescriptorVisibilities
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.builders.declarations.addFunction
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.impl.IrGetValueImpl
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.util.allOverridden
import org.jetbrains.kotlin.ir.util.copyTo
import org.jetbrains.kotlin.ir.util.erasedUpperBound
import org.jetbrains.kotlin.ir.util.fqNameWhenAvailable
import org.jetbrains.kotlin.ir.util.functions
import org.jetbrains.kotlin.ir.util.nonDispatchParameters
import org.jetbrains.kotlin.ir.util.parentAsClass
import org.jetbrains.kotlin.ir.util.parentClassOrNull
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

fun IrClass.isSubclassOfFqName(fqName: String): Boolean =
    fqNameWhenAvailable?.asString() == fqName || superTypes.any {
        it.erasedUpperBound.isSubclassOfFqName(
            fqName,
        )
    }

fun IrClass.addOverride(
    baseFqName: FqName,
    name: String,
    modality: Modality = Modality.FINAL,
): IrSimpleFunction = addFunction {
    this.name = Name.identifier(name)
    this.modality = modality
    this.startOffset = UNDEFINED_OFFSET
    this.endOffset = UNDEFINED_OFFSET
    this.visibility = DescriptorVisibilities.PUBLIC
    this.isFakeOverride = false
    this.isSuspend = false
    this.isInline = false
    this.origin = IrDeclarationOrigin.DEFINED
}.apply {
    val thisReceiver = parentAsClass.thisReceiver!!
    parameters = listOf(thisReceiver.copyTo(this, type = thisReceiver.type)) + nonDispatchParameters

    overriddenSymbols = superTypes.mapNotNull { superType ->
        superType.classOrNull?.owner?.takeIf { superClass ->
            superClass.isSubclassOfFqName(
                baseFqName.asString(),
            )
        }
    }.flatMap { superClass ->
        superClass.functions.filter { function ->
            function.name.asString() == name && function.overridesFunctionIn(baseFqName)
        }.map { it.symbol }.toList()
    }
}

fun IrSimpleFunction.overridesFunctionIn(fqName: FqName): Boolean =
    parentClassOrNull?.fqNameWhenAvailable == fqName || allOverridden().any {
        it.parentClassOrNull?.fqNameWhenAvailable == fqName
    }

fun IrFunction.irThis(): IrExpression {
    val irDispatchReceiverParameter = dispatchReceiverParameter!!
    return IrGetValueImpl(
        startOffset,
        endOffset,
        irDispatchReceiverParameter.type,
        irDispatchReceiverParameter.symbol,
    )
}

fun String.fqn(): FqName = FqName("org.jetbrains.kotlin.fir.plugin.$this")
