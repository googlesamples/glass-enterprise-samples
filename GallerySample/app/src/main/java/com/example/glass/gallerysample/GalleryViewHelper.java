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

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.support.v7.widget.SnapHelper;
import android.view.View;
import java.util.List;

/**
 * Handles view of the gallery items list.
 */
public class GalleryViewHelper {

  private final SnapHelper snapHelper = new LinearSnapHelper();
  private final LinearLayoutManager layoutManager;
  private final RecyclerViewAdapter recyclerViewAdapter;
  private int currentGalleryItemIndex;

  /**
   * Creates {@link GalleryViewHelper} object using {@link List<GalleryItem>}. Sets horizontal
   * {@link RecyclerView} with {@link LinearSnapHelper} to show the gallery items list on the
   * screen.
   */
  public GalleryViewHelper(View view, List<GalleryItem> galleryItems) {
    final RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
    recyclerViewAdapter = new RecyclerViewAdapter(galleryItems);
    recyclerView.setAdapter(recyclerViewAdapter);

    layoutManager = new LinearLayoutManager(view.getContext(),
        RecyclerView.HORIZONTAL, false);
    recyclerView.setLayoutManager(layoutManager);

    snapHelper.attachToRecyclerView(recyclerView);
    recyclerView.setOnFlingListener(snapHelper);
    recyclerView.setHasFixedSize(true);

    recyclerView.addOnScrollListener(new OnScrollListener() {
      @Override
      public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        final View foundView = snapHelper.findSnapView(layoutManager);
        if (foundView == null) {
          return;
        }
        currentGalleryItemIndex = layoutManager.getPosition(foundView);
      }
    });
    recyclerView.addItemDecoration(new GalleryItemDecoration(view.getResources()));
  }

  /**
   * Returns current gallery item index.
   */
  public int getCurrentGalleryItemIndex() {
    return currentGalleryItemIndex;
  }

  /**
   * Calls the {@link RecyclerViewAdapter#notifyDataSetChanged()} on the {@link
   * RecyclerViewAdapter}.
   */
  public void notifyDataSetChanged() {
    recyclerViewAdapter.notifyDataSetChanged();
  }
}
