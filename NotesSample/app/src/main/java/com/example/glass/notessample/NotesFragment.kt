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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.glass.notessample.viewpager.BaseViewPagerFragment
import com.example.glass.notessample.viewpager.NotesViewPagerViewHelper
import com.example.glass.ui.GlassGestureDetector

/**
 * Main fragment for displaying notes
 */
class NotesFragment : Fragment(), GlassGestureDetector.OnGestureListener {

    private lateinit var notesViewHelper: NotesViewPagerViewHelper
    private val currentFragment: BaseViewPagerFragment by lazy {
        notesViewHelper.viewPagerAdapter
            .getCurrentFragment(notesViewHelper.getCurrentElementIndex()) as BaseViewPagerFragment
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.notes_view_pager, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        notesViewHelper = NotesViewPagerViewHelper(view, childFragmentManager)
    }

    override fun onGesture(glassGesture: GlassGestureDetector.Gesture): Boolean {
        return currentFragment.onGesture(glassGesture)
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as BaseActivity).setOnGestureListener(this)
    }
}