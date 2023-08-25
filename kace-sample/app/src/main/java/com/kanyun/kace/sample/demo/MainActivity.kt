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

package com.kanyun.kace.sample.demo

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*

class MainActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        button1.setOnClickListener { view ->
            Toast.makeText(this@MainActivity, "Button1 clicked!", Toast.LENGTH_SHORT).show()
        }

        button2.setOnClickListener { view ->
            startActivity(Intent(this@MainActivity, SecondActivity::class.java))
        }

        button1.text2?.text = "test text"

        genericView.setBackgroundColor(Color.YELLOW)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("11111", "onDestroy() called")
    }
}