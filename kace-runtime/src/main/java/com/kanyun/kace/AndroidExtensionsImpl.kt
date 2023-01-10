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

package com.kanyun.kace

import android.view.View

class AndroidExtensionsImpl : AndroidExtensions {

    private var cached: HashMap<Int, View?>? = null

    private var component: AndroidExtensionsComponent? = null

    private fun initComponent(owner: AndroidExtensionsBase) {
        if (component == null) {
            component = AndroidExtensionsComponent(owner) { destroy() }
        }
    }

    private fun initCachedHashMap() {
        if (cached == null) {
            cached = HashMap()
        }
    }

    override fun <T : View?> findViewByIdCached(owner: AndroidExtensionsBase, id: Int): T {
        initComponent(owner)
        initCachedHashMap()
        return cached?.getOrPut(id) { component!!.findViewById(id) } as T
    }

    private fun destroy() {
        cached?.clear()
    }
}
