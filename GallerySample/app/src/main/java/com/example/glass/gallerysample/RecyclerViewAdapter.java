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
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.example.glass.gallerysample.databinding.GalleryListItemBinding;
import java.util.List;

/**
 * Adapter for the keyboard horizontal {@link RecyclerView}
 */
public class RecyclerViewAdapter extends
    RecyclerView.Adapter<RecyclerViewAdapter.GalleryViewHolder> {

  private final List<GalleryItem> galleryItems;

  RecyclerViewAdapter(List<GalleryItem> galleryItems) {
    this.galleryItems = galleryItems;
  }

  @Override
  public int getItemViewType(int position) {
    return galleryItems.get(position).getType().ordinal();
  }

  @NonNull
  @Override
  public GalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    return new GalleryViewHolder(GalleryListItemBinding.inflate(
        LayoutInflater.from(parent.getContext()), parent, false));
  }

  @Override
  public void onBindViewHolder(@NonNull GalleryViewHolder holder, int position) {
    holder.bind(galleryItems.get(position));
  }

  @Override
  public int getItemCount() {
    return galleryItems.size();
  }

  /**
   * {@link RecyclerView.ViewHolder} class for keyboard, using generic binding.
   */
  static class GalleryViewHolder extends RecyclerView.ViewHolder {

    private final GalleryListItemBinding binding;

    GalleryViewHolder(@NonNull GalleryListItemBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
    }

    void bind(GalleryItem galleryItem) {
      binding.setItem(galleryItem);
      binding.executePendingBindings();
    }
  }
}
