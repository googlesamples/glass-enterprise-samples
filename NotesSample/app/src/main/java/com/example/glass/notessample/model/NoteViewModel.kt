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

package com.example.glass.notessample.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.glass.notessample.storage.NoteRepository
import com.example.glass.notessample.storage.NotesDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * View model for notes. Uses {@link NoteRepository} to communicate with the persistent storage.
 */
class NoteViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: NoteRepository
    val allNotes: LiveData<List<Note>>

    init {
        val wordsDao = NotesDatabase.getDatabase(
            application,
            viewModelScope
        ).wordDao()
        repository = NoteRepository(wordsDao)
        allNotes = repository.allNotes()
    }

    fun insert(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(note)
    }

    fun update(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(note)
    }

    fun deleteElementAtPosition(position: Int) = viewModelScope.launch(Dispatchers.IO) {
        val note = allNotes.value?.get(position)
        if (note != null) {
            repository.delete(note)
        }
    }


    fun delete(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(note)
    }

    fun find(text: String) = repository.find(text)
}