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

import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.glass.notessample.NotesViewHelper
import com.example.glass.notessample.model.Note
import kotlinx.android.synthetic.main.notes_view_pager.view.*

/**
 * View helper implementation for the view pager
 */
class NotesViewPagerViewHelper(
    view: View,
    fragmentManager: FragmentManager
) : NotesViewHelper {

    companion object {
        private const val FIRST_NOTE_POSITION = 2
    }

    val viewPagerAdapter: NotesPagerAdapter
    private val addNoteFragment = AddNoteFragment()
    private val optionsList = listOf(addNoteFragment)
    private var fragmentList = mutableListOf<BaseViewPagerFragment>()
    private val viewPager = view.notesViewPager

    init {
        fragmentList.addAll(optionsList)
        viewPagerAdapter = NotesPagerAdapter(
            fragmentList,
            fragmentManager,
            FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
        )
        viewPager.adapter = viewPagerAdapter
        viewPager.currentItem = FIRST_NOTE_POSITION

        val tabLayout = view.page_indicator
        tabLayout.setupWithViewPager(viewPager)
    }

    override fun getCurrentElementIndex(): Int = viewPager.currentItem


    override fun notifyDataSetChanged(position: Int) {
        fragmentList.removeAt(position)
        viewPagerAdapter.notifyDataSetChanged()
    }

    fun scrollToNextElement() {
        if (getCurrentElementIndex() != fragmentList.size - 1) {
            viewPager.setCurrentItem(getCurrentElementIndex() + 1, true)
        }
    }

    fun scrollToPreviousElement() {
        if (getCurrentElementIndex() != 0) {
            viewPager.setCurrentItem(getCurrentElementIndex() - 1, true)
        }
    }

    fun optionsNumber() = optionsList.size

    fun updateFragmentList(list: List<Note>) {
        fragmentList.clear()
        fragmentList.addAll(optionsList)
        list.forEach {
            fragmentList.add(NoteFragment.newInstance(it.content, it.daytime))
        }
        viewPagerAdapter.notifyDataSetChanged()
    }
}