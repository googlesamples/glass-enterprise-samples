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

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.glass.notessample.R
import com.example.glass.notessample.model.Note
import com.example.glass.notessample.model.NoteViewModel
import com.example.glass.notessample.voicecommand.OnVoiceCommandListener
import com.example.glass.ui.GlassGestureDetector
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Base class for the view pager fragments
 */
open class BaseViewPagerFragment : Fragment(), GlassGestureDetector.OnGestureListener,
    OnVoiceCommandListener {

    companion object {
        private val TAG = AddNoteFragment::class.java.simpleName
        const val ADD_NOTE_REQUEST_CODE = 205
        const val EDIT_NOTE_REQUEST_CODE = 210
    }

    private lateinit var noteViewModel: NoteViewModel
    private var noteToEditId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.notes_fragment_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        noteViewModel = ViewModelProvider(this).get(NoteViewModel::class.java)
    }

    override fun onGesture(gesture: GlassGestureDetector.Gesture): Boolean =
        when (gesture) {
            GlassGestureDetector.Gesture.SWIPE_DOWN -> {
                requireActivity().finish()
                true
            }
            else -> false
        }

    override fun onVoiceCommandDetected(menuItem: MenuItem) {
        when (menuItem.itemId) {
            R.id.addNote -> startVoiceRecognition(ADD_NOTE_REQUEST_CODE)
            R.id.edit -> startVoiceRecognition(EDIT_NOTE_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val results: List<String>? =
                data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (results != null && results.isNotEmpty() && results[0].isNotEmpty()) {
                if (requestCode == ADD_NOTE_REQUEST_CODE) {
                    noteViewModel.insert(Note(results[0], results[0], getDateTime()))
                } else if (requestCode == EDIT_NOTE_REQUEST_CODE) {
                    if (noteToEditId != null) {
                        val editedNote = Note(results[0], results[0], getDateTime())
                        editedNote.id = noteToEditId!!
                        noteViewModel.update(editedNote)
                    }
                }
            } else {
                Log.d(TAG, "Voice recognition result is empty")
            }
        } else {
            Log.d(TAG, "Voice recognition activity results with bad request or result code")
        }
    }

    fun startVoiceRecognition(requestCode: Int) {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        startActivityForResult(intent, requestCode)
    }

    fun editNoteWithId(id: Int) {
        noteToEditId = id
        startVoiceRecognition(EDIT_NOTE_REQUEST_CODE)
    }

    private fun getDateTime() =
        SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(Date())
}