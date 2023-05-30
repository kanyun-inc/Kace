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

import com.kanyun.kace.compiler.utils.ANDROID_EXTENSIONS_CLASS_ID
import com.kanyun.kace.compiler.utils.IMPLICIT_ANDROID_EXTENSIONS_CLASS_IDS
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.FirClass
import org.jetbrains.kotlin.fir.declarations.FirClassLikeDeclaration
import org.jetbrains.kotlin.fir.declarations.FirDeclarationOrigin
import org.jetbrains.kotlin.fir.declarations.FirRegularClass
import org.jetbrains.kotlin.fir.declarations.FirTypeAlias
import org.jetbrains.kotlin.fir.declarations.utils.classId
import org.jetbrains.kotlin.fir.extensions.FirSupertypeGenerationExtension
import org.jetbrains.kotlin.fir.resolve.toSymbol
import org.jetbrains.kotlin.fir.symbols.SymbolInternals
import org.jetbrains.kotlin.fir.types.ConeClassLikeType
import org.jetbrains.kotlin.fir.types.ConeKotlinType
import org.jetbrains.kotlin.fir.types.FirResolvedTypeRef
import org.jetbrains.kotlin.fir.types.builder.buildResolvedTypeRef
import org.jetbrains.kotlin.fir.types.classId
import org.jetbrains.kotlin.fir.types.coneTypeSafe
import org.jetbrains.kotlin.fir.types.constructClassLikeType
import org.jetbrains.kotlin.fir.types.toSymbol
import org.jetbrains.kotlin.name.ClassId

/**
 * Created by benny at 2023/5/29 14:47.
 */
@OptIn(SymbolInternals::class)
class KaceFirSupertypeGenerationExtension(
    session: FirSession,
) : FirSupertypeGenerationExtension(session) {

    context(TypeResolveServiceContainer) override fun computeAdditionalSupertypes(
        classLikeDeclaration: FirClassLikeDeclaration,
        resolvedSupertypes: List<FirResolvedTypeRef>,
    ): List<FirResolvedTypeRef> {
        var shouldAddSuperType = false
        OUTER@ for (superTypeRef in resolvedSupertypes) {
            val superType = superTypeRef.type
            val classIds = listOf(superType.classId) + superType.allSuperTypeClassIds()
            for (classId in classIds) {
                if (classId == ANDROID_EXTENSIONS_CLASS_ID) {
                    shouldAddSuperType = false
                    break@OUTER
                }
                if (!shouldAddSuperType && classId in IMPLICIT_ANDROID_EXTENSIONS_CLASS_IDS) {
                    shouldAddSuperType = true
                }
            }
        }

        if (!shouldAddSuperType) return emptyList()

        return listOf(
            buildResolvedTypeRef {
                type = ANDROID_EXTENSIONS_CLASS_ID.constructClassLikeType(
                    emptyArray(),
                    isNullable = false,
                )
            },
        )
    }

    private fun FirClassLikeDeclaration.supertypeRefs() = when (this) {
        is FirRegularClass -> superTypeRefs
        is FirTypeAlias -> listOf(expandedTypeRef)
        else -> emptyList()
    }

    private fun ConeKotlinType.allSuperTypeClassIds(): List<ClassId> {
        if (this !is ConeClassLikeType) return emptyList()
        val superTypeModuleSession = toSymbol(session)?.moduleData?.session ?: return emptyList()
        val superTypeFir = lookupTag.toSymbol(superTypeModuleSession)?.fir ?: return emptyList()
        return listOf(superTypeFir.classId) + superTypeFir.supertypeRefs().flatMap {
            it.coneTypeSafe<ConeKotlinType>()?.allSuperTypeClassIds() ?: emptyList()
        }
    }

    override fun needTransformSupertypes(declaration: FirClassLikeDeclaration): Boolean {
        return declaration is FirClass &&
            declaration.origin == FirDeclarationOrigin.Source &&
            declaration.superTypeRefs.isNotEmpty()
    }
}
