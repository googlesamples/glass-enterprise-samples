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

import android.content.res.Resources;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ItemDecoration;
import android.view.View;

/**
 * Decoration fot the {@link GalleryItem} in {@link RecyclerView}.
 */
public class GalleryItemDecoration extends ItemDecoration {

  private final Resources resources;

  GalleryItemDecoration(Resources resources) {
    this.resources = resources;
  }

  /**
   * Changes initial position of the first element to be in the center of the screen. Changes
   * position of the last element, to be in the center of the screen, when list is scrolled
   * horizontally to its end.
   */
  @Override
  public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
      @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
    int position = parent.getChildViewHolder(view).getAdapterPosition();
    if (position == 0 || position == state.getItemCount() - 1) {
      int elementWidth = (int) resources.getDimension(R.dimen.gallery_drawer_item_size);
      int elementMargin = (int) resources.getDimension(R.dimen.gallery_drawer_item_margin);
      int padding = Resources.getSystem().getDisplayMetrics().widthPixels / 2 - elementWidth / 2
          - elementMargin;
      if (position == 0) {
        outRect.left = padding;
      } else {
        outRect.right = padding;
      }
    }
  }
}
