**[English](README.md)** | 简体中文

# Kace
[![Hex.pm](https://img.shields.io/hexpm/l/plug.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![Language](https://img.shields.io/badge/Language-Kotlin-green)](https://kotlinlang.org/)

> Kace 即 kotlin-android-compatible-extensions，一个用于帮助从 kotlin-android-extensions 无缝迁移的框架

kotlin-android-extensions 框架已经过时了很久，并且将在 Kotlin 1.8 中被正式移除

对于新代码，我们可以使用 ViewBinding 等方式，但是大量存量代码的迁移，对于开发者来说不是一个轻松的工作

Kace 通过解析 layout 生成源码的方式实现对 kotlin-android-extensions 的无缝迁移，帮助开发者轻松的升级到 Kotlin 1.8

## 快速迁移
### 1. 添加插件到 classpath
```kotlin
// 方式 1
// 传统方式，在根目录的 build.gradle.kts 中添加以下代码
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("com.kanyun.kace:kace-gradle-plugin:$latest_version")
    }
}

// 方式 2
// 引用插件新方式，在 settings.gradle.kts 中添加以下代码
pluginManagement {
    repositories {
        mavenCentral()
    }
    plugins {
        id("com.kanyun.kace") version "$latest_version" apply false
    }
}
```

### 2. 应用插件
移除`kotlin-android-extensions`插件，并添加以下代码

```kotlin
plugins {
    id("com.kanyun.kace")
    id("kotlin-parcelize") // 可选，当使用了`@Parcelize`注解时需要添加
}
```

### 3. 配置插件（可选）
默认情况下 Kace 会解析模块内的每个 layout 并生成代码，用户也可以自定义需要解析的 layout

```kotlin
kace {
    whiteList = listOf() // 当 whiteList 不为空时，只有 whiteList 中的 layout 才会被解析
    blackList = listOf("activity_main.xml") // 当 blackList 不为空时，blackList 中的 layout 不会被解析
}
```

## 支持的类型
- android.app.Activity
- androidx.fragment.app.Fragment
- androidx.fragment.app.DialogFragment
- android.view.View(目前 View 类型不支持 viewId 缓存)

Kace 目前支持了以上四种最常用的类型，其他 kotlin-android-extensions 支持的类型如 android.app.Fragment, android.app.Dialog, kotlinx.android.extensions.LayoutContainer 等，由于被废弃或者使用较少，Kace 目前没有做支持

## 版本兼容
| Kace                 | Kotlin | AGP   | Gradle |
|----------------------|--------|-------|--------|
| 1.0.2                | 1.7.0  | 4.2.0 | 6.7.1  |
| 1.8.0-1.0.2-SNAPSHOT | 1.8.0  | 4.2.0 | 6.8.3  |

由于 Kace 的目标是帮助开发者更方便地迁移到 Kotlin 1.8，因此 Kotlin 最低支持版本比较高

## Benchmark
### 编译速度
默认情况下 Kace 会解析模块内的每个 layout 并生成代码，这可能会给编译速度带来一定的影响

Kace 通过并行任务的方式加速代码生成，经过测试，在一个包括 500 个 layout 的模块中引入 Kace 插件，全量编译时解析 layout 并生成代码的总耗时约 1.5 秒

Kace 也支持增量编译，当修改或添加一个 layout 时，解析 layout 并生成代码的总耗时约 8 毫秒

### 包体积
Kace 生成的代码都是内联的扩展属性，同时在代码缩减之后会自动移除没有被使用的代码，经过测试，引入 Kace 前后包体积几乎没有变化。

## License
```
Copyright (C) 2022 KanYun

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```