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

package com.example.android.glass.cardsample.menu;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;

/**
 * Represents the single menu item object.
 */
class GlassMenuItem {

  private Drawable icon;
  private String text;

  /**
   * Compares two {@link GlassMenuItem} objects and determines if there is a difference between
   * them.
   */
  public static class ItemDiffComparator extends DiffUtil.ItemCallback<GlassMenuItem> {

    @Override
    public boolean areItemsTheSame(@NonNull GlassMenuItem oldItem,
        @NonNull GlassMenuItem newItem) {
      return oldItem.equals(newItem);
    }

    @Override
    public boolean areContentsTheSame(@NonNull GlassMenuItem oldItem,
        @NonNull GlassMenuItem newItem) {
      return oldItem.equals(newItem);
    }
  }

  /**
   * {@link GlassMenuItem} object is constructed by usage of this method.
   *
   * @param icon is a menu icon {@link Drawable} object.
   * @param text is a String with the menu option label.
   */
  GlassMenuItem(Drawable icon, String text) {
    this.icon = icon;
    this.text = text;
  }

  /**
   * Returns menu item icon.
   */
  Drawable getIcon() {
    return icon;
  }

  /**
   * Returns menu item label.
   */
  String getText() {
    return text;
  }
}
