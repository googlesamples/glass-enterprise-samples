/*
 * Copyright 2019 The Android Open Source Project
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

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.example.android.glass.cardsample.BaseActivity;
import com.example.android.glass.cardsample.R;
import com.example.android.glass.cardsample.menu.GlassMenuItemViewHolder.OnItemChosenListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Activity which provides the menu functionality. It creates the horizontal recycler view to move
 * between menu items.
 */
public class MenuActivity extends BaseActivity implements OnItemChosenListener {

  private static final String MENU_KEY = "menu_key";
  private static final String EXTRA_NAME = "title";
  private static final int DEFAULT_MENU_VALUE = -1;
  private MenuListAdapter adapter;
  private List<GlassMenuItem> menuItems = new ArrayList<>();

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    final RecyclerView recyclerView = (RecyclerView) getLayoutInflater()
        .inflate(R.layout.recycler_view_layout, null, false);
    adapter = new MenuListAdapter(this, new GlassMenuItem.ItemDiffComparator(), menuItems,
        this);
    recyclerView
        .setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    recyclerView.setAdapter(adapter);
    recyclerView.setFocusable(true);

    final SnapHelper snapHelper = new PagerSnapHelper();
    snapHelper.attachToRecyclerView(recyclerView);
    setContentView(recyclerView);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    final int menuResource = getIntent().getIntExtra(MENU_KEY, DEFAULT_MENU_VALUE);
    if (menuResource != DEFAULT_MENU_VALUE) {
      final MenuInflater inflater = getMenuInflater();
      inflater.inflate(menuResource, menu);

      for (int i = 0; i < menu.size(); i++) {
        final MenuItem menuItem = menu.getItem(i);
        menuItems.add(
            new GlassMenuItem(menuItem.getIcon(), menuItem.getTitle().toString()));
        adapter.notifyDataSetChanged();
      }
    }
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public void onItemChosen(GlassMenuItem glassMenuItem) {
    final Intent intent = new Intent();
    intent.putExtra(EXTRA_NAME, glassMenuItem.getText());
    setResult(RESULT_OK, intent);
    finish();
  }
}
