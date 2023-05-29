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

import com.kanyun.kace.compiler.utils.ANDROID_EXTENSIONS_CLASS_NAME
import com.kanyun.kace.compiler.utils.ANDROID_EXTENSIONS_FQNAME
import com.kanyun.kace.compiler.utils.ANDROID_EXTENSIONS_FULL_NAME
import com.kanyun.kace.compiler.utils.ANDROID_EXTENSIONS_PACKAGE_NAME
import com.kanyun.kace.compiler.utils.IMPLICIT_ANDROID_EXTENSIONS_TYPES
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.descriptors.findClassAcrossModuleDependencies
import org.jetbrains.kotlin.js.descriptorUtils.getJetTypeFqName
import org.jetbrains.kotlin.js.descriptorUtils.getKotlinTypeFqName
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.descriptorUtil.module
import org.jetbrains.kotlin.resolve.extensions.SyntheticResolveExtension
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.KotlinTypeFactory
import org.jetbrains.kotlin.types.TypeAttributes
import org.jetbrains.kotlin.types.typeUtil.supertypes

class KaceSyntheticResolveExtension : SyntheticResolveExtension {

    override fun addSyntheticSupertypes(thisDescriptor: ClassDescriptor, supertypes: MutableList<KotlinType>) {
        if (thisDescriptor.kind != ClassKind.CLASS) return

        val superTypeNames = supertypes.asSequence().flatMap {
            listOf(it) + it.supertypes()
        }.map {
            it.getKotlinTypeFqName(false)
        }

        var shouldAddSuperType = false
        for (superTypeName in superTypeNames) {
            if (superTypeName == ANDROID_EXTENSIONS_FULL_NAME) return
            if (!shouldAddSuperType && superTypeName in IMPLICIT_ANDROID_EXTENSIONS_TYPES) {
                shouldAddSuperType = true
            }
        }
        if (!shouldAddSuperType) return

        val androidExtensionsType = thisDescriptor.module.findClassAcrossModuleDependencies(
            ClassId(
                FqName(ANDROID_EXTENSIONS_PACKAGE_NAME),
                Name.identifier(ANDROID_EXTENSIONS_CLASS_NAME)
            )
        )

        checkNotNull(androidExtensionsType) {
            "Cannot locate $ANDROID_EXTENSIONS_FQNAME. Check your module classpath for the kace-runtime library."
        }

        supertypes.add(
            KotlinTypeFactory.simpleNotNullType(
                TypeAttributes.Empty,
                androidExtensionsType, emptyList()
            )
        )
    }
}
