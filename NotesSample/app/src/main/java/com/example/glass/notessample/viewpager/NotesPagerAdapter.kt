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

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter

/**
 * Adapter for the view pager
 */
class NotesPagerAdapter(
    private val fragments: MutableList<BaseViewPagerFragment>,
    fm: FragmentManager,
    behavior: Int
) : FragmentStatePagerAdapter(fm, behavior) {

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getItemPosition(item: Any): Int {
        val position = fragments.indexOf(item)
        return if (position == -1) {
            PagerAdapter.POSITION_NONE
        } else {
            position
        }
    }

    fun getCurrentFragment(position: Int): Fragment? {
        return fragments[position]
    }
}