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
import android.util.Log
import android.view.Menu
import android.view.MenuItem

class MainActivity : BaseActivity() {

    companion object {
        const val FEATURE_VOICE_COMMANDS = 14
        val TAG = MainActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.requestFeature(FEATURE_VOICE_COMMANDS)
        setContentView(R.layout.activity_main)

        replaceFragment(NotesFragment(), false)
    }

    override fun onCreatePanelMenu(featureId: Int, menu: Menu): Boolean {
        menuInflater.inflate(R.menu.notes_menu, menu)
        return true
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        Log.d(TAG, "Voice menu item selected: ${item.title}")
        return super.onContextItemSelected(item)
    }
}
