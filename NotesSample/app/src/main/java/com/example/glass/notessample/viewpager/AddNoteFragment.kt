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

package com.example.glass.notessample.viewpager

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.glass.notessample.R
import com.example.glass.ui.GlassGestureDetector
import kotlinx.android.synthetic.main.notes_option_layout.*

/**
 * Fragment for adding new notes
 */
class AddNoteFragment : BaseViewPagerFragment() {

    companion object {
        private val TAG = AddNoteFragment::class.java.simpleName
        private const val VOICE_REQUEST_CODE = 205

        fun newInstance(
            listener: (text: String) -> Unit
        ): AddNoteFragment {
            val addNoteFragment = AddNoteFragment()
            addNoteFragment.listener = listener
            return addNoteFragment
        }
    }

    lateinit var listener: (String) -> Unit

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.notes_option_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        optionIcon.setImageResource(R.drawable.ic_add)
        optionTextView.text = resources.getString(R.string.add_note)
    }

    override fun onGesture(gesture: GlassGestureDetector.Gesture): Boolean =
        when (gesture) {
            GlassGestureDetector.Gesture.TAP -> {
                startVoiceRecognition()
                true
            }
            else -> super.onGesture(gesture)
        }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == VOICE_REQUEST_CODE && resultCode == RESULT_OK) {
            val results: List<String>? =
                data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (results != null && results.isNotEmpty() && results[0].isNotEmpty()) {
                listener(results[0])
            } else {
                Log.d(TAG, "Voice recognition result is empty")
            }
        } else {
            Log.d(TAG, "Voice recognition activity results with bad request or result code")
        }
    }

    private fun startVoiceRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        startActivityForResult(intent, VOICE_REQUEST_CODE)
    }
}