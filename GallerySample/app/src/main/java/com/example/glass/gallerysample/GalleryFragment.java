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

import android.content.Context;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.glass.gallerysample.databinding.GalleryLayoutBinding;
import com.example.glass.ui.GlassGestureDetector.Gesture;
import com.example.glass.ui.GlassGestureDetector.OnGestureListener;

/**
 * Shows horizontal scrolling list of the stored photos and videos or information about the empty
 * gallery.
 */
public class GalleryFragment extends Fragment implements OnGestureListener, GalleryItemsListener {

  /**
   * Background handler thread name.
   */
  private static final String BACKGROUND_HANDLER_THREAD_NAME = "gallery_background_thread";

  /**
   * Gallery model containing all available {@link GalleryItem}s.
   */
  private final GalleryModel galleryModel = new GalleryModel();

  /**
   * Background handler thread for the {@link ContentObserver}.
   */
  private HandlerThread handlerThread = new HandlerThread(BACKGROUND_HANDLER_THREAD_NAME);

  private GalleryViewHelper galleryViewHelper;
  private GalleryItemsProvider galleryItemsProvider;
  private OnGalleryItemSelectedListener onGalleryItemSelectedListener;

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    final GalleryLayoutBinding galleryLayoutBinding = GalleryLayoutBinding
        .inflate(inflater, container, false);
    galleryLayoutBinding.setGalleryModel(galleryModel);
    return galleryLayoutBinding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    galleryViewHelper = new GalleryViewHelper(view, galleryModel.getItems());
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    handlerThread.start();
    galleryModel.clearItems();
    galleryItemsProvider = new GalleryItemsProvider(context, handlerThread, this);
    galleryItemsProvider.loadGalleryItems();
  }

  @Override
  public void onResume() {
    super.onResume();
    ((BaseActivity) requireActivity()).setOnGestureListener(this);
  }

  @Override
  public void onDetach() {
    super.onDetach();
    handlerThread.quit();
  }

  @Override
  public boolean onGesture(Gesture glassGesture) {
    switch (glassGesture) {
      case TAP:
        if (galleryModel.isGalleryEmpty()) {
          return false;
        }
        final GalleryItem currentGalleryItem = galleryModel.getItems()
            .get(galleryViewHelper.getCurrentGalleryItemIndex());
        onGalleryItemSelectedListener.onGalleryItemSelected(currentGalleryItem);
        return true;
      case SWIPE_DOWN:
        requireActivity().finish();
        return true;
      default:
        return false;
    }
  }

  @Override
  public void onItemFound(GalleryItem galleryItem) {
    galleryModel.addItem(galleryItem);
    notifyDataSetChanged();
  }

  @Override
  public void onEmptyList() {
    galleryModel.clearItems();
    if (galleryViewHelper != null) {
      notifyDataSetChanged();
    }
  }

  /**
   * Sets the {@link OnGalleryItemSelectedListener} listener for this fragment.
   */
  public void setOnGalleryItemSelectedListener(OnGalleryItemSelectedListener listener) {
    this.onGalleryItemSelectedListener = listener;
  }

  /**
   * Removes deleted GalleryItem object from the model and notifies view helper about this.
   */
  public void onGalleryItemDeleted(GalleryItem galleryItem) {
    galleryModel.getItems().remove(galleryItem);
    notifyDataSetChanged();
  }

  private void notifyDataSetChanged() {
    if (isAdded()) {
      requireActivity().runOnUiThread((new Runnable() {
        @Override
        public void run() {
          galleryViewHelper.notifyDataSetChanged();
        }
      }));
    }
  }

  /**
   * Interface to notify parent that {@link GalleryItem} has been selected.
   */
  interface OnGalleryItemSelectedListener {

    void onGalleryItemSelected(GalleryItem galleryItem);
  }
}
