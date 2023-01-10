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

import com.bennyhuo.kotlin.compiletesting.extensions.module.KotlinModule
import com.bennyhuo.kotlin.compiletesting.extensions.module.checkResult
import com.bennyhuo.kotlin.compiletesting.extensions.source.FileBasedModuleInfoLoader
import com.kanyun.kace.compiler.options.Options
import org.junit.Test

class KaceTest {

    @Test
    fun basic() {
        testBase("basic.txt")
    }

    private fun testBase(fileName: String) {
        val loader = FileBasedModuleInfoLoader("testData/$fileName")
        val sourceModuleInfos = loader.loadSourceModuleInfos()

        Options.isEnabled.set(true)

        val modules = sourceModuleInfos.map {
            KotlinModule(it, componentRegistrars = listOf(KaceComponentRegistrar()))
        }

        modules.checkResult(
            loader.loadExpectModuleInfos(),
            executeEntries = true,
            checkCompilerOutput = true
        )
    }
}
