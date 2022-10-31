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

package com.kanyun.kace.compiler.options

import com.kanyun.kace.compiler.logger
import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.CompilerConfigurationKey
import java.lang.ref.WeakReference

object Options {

    class Option<T : Any?>(
        name: String,
        private val defaultValue: T,
        description: String,
        valueDescription: String,
        required: Boolean = false,
        allowMultipleOccurrences: Boolean = false
    ) {

        private val key = CompilerConfigurationKey.create<T>(name)
        private var value: T? = null

        val option = CliOption(
            name, valueDescription, description,
            required, allowMultipleOccurrences
        )

        fun config(value: String?, configuration: CompilerConfiguration) {
            val typedValue = when (defaultValue) {
                is Boolean -> value?.toBooleanStrictOrNull() ?: defaultValue
                is Int -> value?.toIntOrNull() ?: defaultValue
                else -> value
            } as T

            if (typedValue != null) {
                configuration.put(key, typedValue)
            }
        }

        operator fun invoke(): T {
            return value ?: (compilerConfiguration?.get()?.get(key) ?: defaultValue).also {
                value = it
            }
        }

        fun set(value: T) {
            this.value = value
        }
    }

    private var compilerConfiguration: WeakReference<CompilerConfiguration>? = null

    @JvmField
    val isEnabled = Option(
        "isEnabled",
        false,
        "Enable this plugin.",
        "<true/false>"
    )

    val allOptions = Options::class.java.declaredFields.filter {
        it.type == Option::class.java
    }.map {
        it.get(null) as Option<*>
    }

    fun processOption(
        option: AbstractCliOption,
        value: String,
        configuration: CompilerConfiguration
    ) {
        allOptions.firstOrNull {
            it.option == option
        }?.config(value, configuration)
    }

    fun setup(compilerConfiguration: CompilerConfiguration) {
        this.compilerConfiguration = WeakReference(compilerConfiguration)
    }

    fun dump() {
        logger.warn(
            allOptions.joinToString {
                "${it.option.optionName}: ${it()}"
            }
        )
    }
}
