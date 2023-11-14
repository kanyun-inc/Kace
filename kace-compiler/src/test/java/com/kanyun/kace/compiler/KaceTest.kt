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
import com.bennyhuo.kotlin.compiletesting.extensions.source.TextBasedModuleInfoLoader
import com.kanyun.kace.compiler.options.Options
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.Test
import java.io.File

class KaceTest {

    @Test
    fun k1() {
        testBase(false)
    }

    @Test
    fun k2() {
        testBase(true)
    }

    @OptIn(ExperimentalCompilerApi::class)
    private fun testBase(useK2: Boolean) {
        val source = File("testData/source.txt").readText()
        val expect = File("testData/expect.txt").readText()
        val loader = TextBasedModuleInfoLoader("$source\n$expect")
        val sourceModuleInfos = loader.loadSourceModuleInfos()

        Options.isEnabled.set(true)

        val modules = sourceModuleInfos.map {
            KotlinModule(it, compilerPluginRegistrars = listOf(KaceCompilerPluginRegistrar()), useK2 = true)
        }

        modules.checkResult(
            loader.loadExpectModuleInfos(),
            executeEntries = true,
            checkCompilerOutput = true,
            checkGeneratedIr = true,
        )
    }
}
