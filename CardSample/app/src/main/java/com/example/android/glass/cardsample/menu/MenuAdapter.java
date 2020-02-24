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

package com.example.android.glass.cardsample.menu;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.android.glass.cardsample.databinding.MenuItemBinding;
import java.util.List;

/**
 * Adapter for the menu horizontal recycler view.
 */
public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {

  private final List<GlassMenuItem> menuItems;

  MenuAdapter(List<GlassMenuItem> menuItems) {
    this.menuItems = menuItems;
  }

  @NonNull
  @Override
  public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    return new MenuViewHolder(MenuItemBinding.inflate(
        LayoutInflater.from(parent.getContext()), parent, false));
  }

  @Override
  public void onBindViewHolder(@NonNull MenuViewHolder menuViewHolder, int position) {
    menuViewHolder.bind(menuItems.get(position));
  }

  @Override
  public int getItemCount() {
    return menuItems.size();
  }

  static class MenuViewHolder extends RecyclerView.ViewHolder {

    private MenuItemBinding binding;

    public MenuViewHolder(@NonNull MenuItemBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
    }

    public void bind(GlassMenuItem glassMenuItem) {
      binding.setItem(glassMenuItem);
      binding.executePendingBindings();
    }
  }
}
