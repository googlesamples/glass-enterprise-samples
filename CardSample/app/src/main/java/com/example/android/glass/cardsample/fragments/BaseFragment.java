/*
 * Copyright 2019 Google LLC
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

package com.example.android.glass.cardsample.fragments;

import android.content.Intent;
import android.support.v4.app.Fragment;
import com.example.android.glass.cardsample.menu.MenuActivity;

/**
 * Base class for each fragment. Provides functionality to start new activity with a menu.
 */
public abstract class BaseFragment extends Fragment implements OnSingleTapUpListener {

  /**
   * Key for obtaining menu value from fragment arguments.
   */
  protected static final String MENU_KEY = "menu_key";

  /**
   * Default value for menu obtained from fragment arguments.
   */
  protected static final int MENU_DEFAULT_VALUE = 0;

  /**
   * Request code for starting activity for result. Used to compare actual request code from
   * onActivityResult method in {@link MainLayoutFragment}
   * This value doesn't have any special meaning.
   */
  protected static final int REQUEST_CODE = 205;

  @Override
  public void onSingleTapUp() {
    if (getArguments() != null) {
      int menu = getArguments().getInt(MENU_KEY, MENU_DEFAULT_VALUE);
      if (menu != MENU_DEFAULT_VALUE) {
        Intent intent = new Intent(getActivity(), MenuActivity.class);
        intent.putExtra(MENU_KEY, menu);
        startActivityForResult(intent, REQUEST_CODE);
      }
    }
  }
}
