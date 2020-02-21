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
import android.support.annotation.Nullable;
import java.util.Objects;

/**
 * Represents the single menu item object.
 */
public class GlassMenuItem {

  private int id;
  private Drawable icon;
  private String text;

  /**
   * {@link GlassMenuItem} object is constructed by usage of this method.
   *
   * @param id is an id of the the current menu item.
   * @param icon is a menu icon {@link Drawable} object.
   * @param text is a String with the menu option label.
   */
  GlassMenuItem(int id, Drawable icon, String text) {
    this.id = id;
    this.icon = icon;
    this.text = text;
  }

  /**
   * Returns menu item id.
   */
  public int getId() {
    return id;
  }

  /**
   * Returns menu item icon.
   */
  public Drawable getIcon() {
    return icon;
  }

  /**
   * Returns menu item label.
   */
  public String getText() {
    return text;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, icon, text);
  }

  @Override
  public boolean equals(@Nullable Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof GlassMenuItem)) {
      return false;
    }
    final GlassMenuItem that = (GlassMenuItem) other;
    return Objects.equals(this.id, that.id) &&
        Objects.equals(this.icon, that.icon) &&
        Objects.equals(this.text, that.text);
  }

  @NonNull
  @Override
  public String toString() {
    return id + " " + icon + " " + text;
  }
}
