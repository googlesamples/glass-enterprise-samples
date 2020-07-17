/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.glass.notessample

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.glass.notessample.voicecommand.OnVoiceCommandListener
import com.example.glass.ui.GlassGestureDetector
import com.example.glass.ui.GlassGestureDetector.OnGestureListener

abstract class BaseActivity : AppCompatActivity(), OnGestureListener {

    var onGestureListener: OnGestureListener? = null
        set(value) {
            glassGestureDetector = GlassGestureDetector(this, value)
        }
    var onVoiceCommandListener: OnVoiceCommandListener? = null
    private lateinit var glassGestureDetector: GlassGestureDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        glassGestureDetector = GlassGestureDetector(this, this)
    }

    override fun onResume() {
        super.onResume()
        window.hideSystemUI()
    }

    override fun onCreatePanelMenu(featureId: Int, menu: Menu): Boolean {
        menuInflater.inflate(R.menu.notes_menu, menu)
        return true
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        onVoiceCommandListener?.onVoiceCommandDetected(item)
        return super.onContextItemSelected(item)
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        return glassGestureDetector.onTouchEvent(ev) || super.dispatchTouchEvent(ev)
    }

    override fun onGesture(gesture: GlassGestureDetector.Gesture): Boolean = false

    /**
     * Helper method for the [Fragment] replacement in the container. Depends on the given flag,
     * fragment can be added to the back stack.
     */
    fun replaceFragment(fragment: Fragment, addToBackStack: Boolean) {
        supportFragmentManager.commit {
            replace(R.id.container, fragment)
            if (addToBackStack) {
                addToBackStack(fragment.toString())
            }
        }
    }

    /**
     * Pops the back stack on the fragment manager.
     */
    fun popBackStack() {
        supportFragmentManager.popBackStack()
    }

    private fun Window.hideSystemUI() {
        decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }
}