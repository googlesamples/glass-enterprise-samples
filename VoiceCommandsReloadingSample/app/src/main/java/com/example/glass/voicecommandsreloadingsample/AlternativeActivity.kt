/*
 * Copyright 2021 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.glass.voicecommandsreloadingsample

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MotionEvent
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.glass.ui.GlassGestureDetector

/**
 * Activity extending [AppCompatActivity] which requires calling [sendBroadcast] method in order
 * to reload voice commands list after it's changed.
 *
 * By default it uses voice commands from [R.menu.main_menu] file. Swipe forward or backward causes
 * voice command list to toggle.
 */
class AlternativeActivity : AppCompatActivity(), GlassGestureDetector.OnGestureListener {

    /**
     * Make sure you call [sendBroadcast] method with the following intent to apply changes in
     * voice commands.
     */
    private var useMainVoiceCommands: Boolean = true
        set(value) {
            field = value
            sendBroadcast(Intent("reload-voice-commands"))
        }

    private val glassGestureDetector: GlassGestureDetector by lazy {
        GlassGestureDetector(this, this)
    }

    private lateinit var textViewVoiceCommands: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(FEATURE_VOICE_COMMANDS)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alternative)
        textViewVoiceCommands = findViewById(R.id.voiceCommands)
    }

    override fun onCreatePanelMenu(featureId: Int, menu: Menu): Boolean {
        return if (useMainVoiceCommands) {
            menuInflater.inflate(R.menu.main_menu, menu)
            textViewVoiceCommands.text = getString(R.string.main_voice_commands)
            true
        } else {
            menuInflater.inflate(R.menu.alternative_menu, menu)
            textViewVoiceCommands.text = getString(R.string.alternative_voice_commands)
            true
        }
    }

    override fun onGesture(gesture: GlassGestureDetector.Gesture): Boolean {
        return when (gesture) {
            GlassGestureDetector.Gesture.SWIPE_FORWARD,
            GlassGestureDetector.Gesture.SWIPE_BACKWARD -> {
                useMainVoiceCommands = !useMainVoiceCommands
                true
            }
            GlassGestureDetector.Gesture.SWIPE_DOWN -> {
                finish()
                true
            }
            else -> false
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        return if (glassGestureDetector.onTouchEvent(ev)) true else super.dispatchTouchEvent(ev)
    }

    companion object {
        private const val FEATURE_VOICE_COMMANDS = 14
    }
}
