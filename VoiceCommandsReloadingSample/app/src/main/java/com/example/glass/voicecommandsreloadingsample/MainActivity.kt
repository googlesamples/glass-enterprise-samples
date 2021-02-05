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

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.example.glass.ui.GlassGestureDetector
import com.example.glass.voicecommandsreloadingsample.MainActivity.Companion.FEATURE_DEBUG_VOICE_COMMANDS

/**
 * Activity extending [FragmentActivity] which requires calling [invalidateOptionsMenu] method in
 * order to reload voice commands list after it's changed.
 *
 * By default voice commands feature is enabled. Swipe forward or backward toggles it.
 *
 * [FEATURE_DEBUG_VOICE_COMMANDS] shows detected command in logcat and provides visual feedback
 * when unlisted voice command is detected.
 */
class MainActivity : FragmentActivity(), GlassGestureDetector.OnGestureListener {

    /**
     * Make sure you call [invalidateOptionsMenu] method to apply changes in voice commands.
     */
    private var enableVoiceCommands: Boolean = true
        set(value) {
            field = value
            invalidateOptionsMenu()
        }

    private val glassGestureDetector: GlassGestureDetector by lazy {
        GlassGestureDetector(this, this)
    }

    private lateinit var textViewVoiceCommands: TextView
    private lateinit var textViewAction: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(FEATURE_VOICE_COMMANDS)
        requestWindowFeature(FEATURE_DEBUG_VOICE_COMMANDS)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textViewVoiceCommands = findViewById(R.id.voiceCommands)
        textViewAction = findViewById(R.id.action)

        // Requesting permissions to enable voice commands menu
        ActivityCompat.requestPermissions(
            this,
            PERMISSIONS,
            REQUEST_PERMISSION_CODE
        )
    }

    override fun onCreatePanelMenu(featureId: Int, menu: Menu): Boolean {
        return if (enableVoiceCommands) {
            menuInflater.inflate(R.menu.main_menu, menu)
            textViewVoiceCommands.text = getString(R.string.main_voice_commands)
            textViewAction.text = getString(R.string.swipe_forward_to_disable_voice_commands)
            true
        } else {
            textViewVoiceCommands.text = getText(R.string.disabled)
            textViewAction.text = getString(R.string.swipe_forward_to_enable_voice_commands)
            false
        }
    }

    override fun onGesture(gesture: GlassGestureDetector.Gesture): Boolean {
        return when (gesture) {
            GlassGestureDetector.Gesture.TAP -> {
                startActivity(Intent(this, AlternativeActivity::class.java))
                true
            }
            GlassGestureDetector.Gesture.SWIPE_FORWARD,
            GlassGestureDetector.Gesture.SWIPE_BACKWARD -> {
                toggleVoiceCommands()
                true
            }
            GlassGestureDetector.Gesture.SWIPE_DOWN -> {
                finish()
                true
            }
            else -> false
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        Log.d(TAG, "Menu item selected: ${item.title}")
        return false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_PERMISSION_CODE) {
            for (result in grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Permission denied. Voice commands menu is disabled.")
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        return if (glassGestureDetector.onTouchEvent(ev)) true else super.dispatchTouchEvent(ev)
    }


    private fun toggleVoiceCommands() {
        enableVoiceCommands = !enableVoiceCommands

    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
        private const val FEATURE_VOICE_COMMANDS = 14
        private const val FEATURE_DEBUG_VOICE_COMMANDS = 15
        private const val REQUEST_PERMISSION_CODE = 200
        private val PERMISSIONS = arrayOf(Manifest.permission.RECORD_AUDIO)
    }
}