/*
 * Copyright 2019 Google LLC
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

package com.example.glass.gallerysample;

import android.graphics.drawable.Drawable;

/**
 * Model for the Gallery item.
 */
public class GalleryItem {

  /**
   * Gallery item type.
   */
  enum Type {
    IMAGE, VIDEO;

    /**
     * Returns {@link GalleryItem} from its ordinal value.
     */
    public static Type fromId(int itemType) {
      return GalleryItem.Type.values()[itemType];
    }
  }

  private String name;
  private String path;
  private Type type;
  private Drawable bitmap;

  /**
   * Creates {@link GalleryItem} object from the {@link String} name, {@link String} path, {@link
   * Type} type and {@link Drawable} bitmap.
   */
  public GalleryItem(String name, String path, Type type, Drawable bitmap) {
    this.name = name;
    this.path = path;
    this.type = type;
    this.bitmap = bitmap;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public Type getType() {
    return type;
  }

  public void setType(Type type) {
    this.type = type;
  }

  public Drawable getBitmap() {
    return bitmap;
  }

  public void setBitmap(Drawable bitmap) {
    this.bitmap = bitmap;
  }
}
