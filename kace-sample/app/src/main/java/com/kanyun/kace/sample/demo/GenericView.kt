package com.kanyun.kace.sample.demo

import android.content.Context
import android.util.AttributeSet
import android.view.View

/**
 * Created by benny at 2023/8/17 15:37.
 */
class GenericView<T>
@JvmOverloads constructor(
    context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr)