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

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat

class MainActivity : BaseActivity() {

    companion object {
        const val FEATURE_VOICE_COMMANDS = 14
        const val REQUEST_PERMISSION_CODE = 200
        val PERMISSIONS = arrayOf(Manifest.permission.RECORD_AUDIO)
        val TAG = MainActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.requestFeature(FEATURE_VOICE_COMMANDS)
        setContentView(R.layout.activity_main)

        // Requesting permissions to enable voice commands menu
        ActivityCompat.requestPermissions(
            this,
            PERMISSIONS,
            REQUEST_PERMISSION_CODE
        )

        replaceFragment(NotesFragment(), false)
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
}
