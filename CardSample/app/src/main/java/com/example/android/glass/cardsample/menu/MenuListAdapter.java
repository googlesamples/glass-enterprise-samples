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

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil.ItemCallback;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.android.glass.cardsample.R;
import com.example.android.glass.cardsample.menu.GlassMenuItemViewHolder.OnItemChosenListener;
import java.util.List;

/**
 * Adapter for the menu horizontal recycler view.
 */
public class MenuListAdapter extends ListAdapter<GlassMenuItem, GlassMenuItemViewHolder> {

  private final Context context;
  private final OnItemChosenListener onItemChosenListener;
  private final List<GlassMenuItem> glassMenuItemList;

  MenuListAdapter(Context context, @NonNull ItemCallback<GlassMenuItem> diffCallback,
      List<GlassMenuItem> glassMenuItemList, OnItemChosenListener onItemChosenListener) {
    super(diffCallback);
    this.context = context;
    this.glassMenuItemList = glassMenuItemList;
    this.onItemChosenListener = onItemChosenListener;
  }

  @Override
  public int getItemCount() {
    return glassMenuItemList.size();
  }

  @NonNull
  @Override
  public GlassMenuItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
    final View view = LayoutInflater.from(context)
        .inflate(R.layout.main_layout, viewGroup, false);
    return new GlassMenuItemViewHolder(view, onItemChosenListener);
  }

  @Override
  public void onBindViewHolder(@NonNull GlassMenuItemViewHolder glassMenuItemViewHolder,
      int position) {
    glassMenuItemViewHolder.setGlassMenuItem(glassMenuItemList.get(position));
  }
}
