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

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;
import androidx.recyclerview.widget.RecyclerView.OnScrollListener;
import androidx.recyclerview.widget.SnapHelper;
import com.example.android.glass.cardsample.BaseActivity;
import com.example.android.glass.cardsample.R;
import com.example.glass.ui.GlassGestureDetector.Gesture;
import com.example.glass.ui.GlassGestureDetector.OnGestureListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Activity which provides the menu functionality. It creates the horizontal recycler view to move
 * between menu items.
 */
public class MenuActivity extends BaseActivity implements OnGestureListener {

  /**
   * Key for the menu item id.
   */
  public static final String EXTRA_MENU_ITEM_ID_KEY = "id";

  /**
   * Default value for the menu item.
   */
  public static final int EXTRA_MENU_ITEM_DEFAULT_VALUE = -1;

  /**
   * Key for the menu.
   */
  public static final String EXTRA_MENU_KEY = "menu_key";

  private MenuAdapter adapter;
  private List<GlassMenuItem> menuItems = new ArrayList<>();
  private int currentMenuItemIndex;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.menu_layout);
    final RecyclerView recyclerView = findViewById(R.id.menuRecyclerView);
    adapter = new MenuAdapter(menuItems);
    final LayoutManager layoutManager = new LinearLayoutManager(this,
        LinearLayoutManager.HORIZONTAL, false);
    recyclerView.setLayoutManager(layoutManager);
    recyclerView.setAdapter(adapter);
    recyclerView.setFocusable(true);

    final SnapHelper snapHelper = new PagerSnapHelper();
    snapHelper.attachToRecyclerView(recyclerView);

    recyclerView.addOnScrollListener(new OnScrollListener() {
      @Override
      public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        final View foundView = snapHelper.findSnapView(layoutManager);
        if (foundView == null) {
          return;
        }
        currentMenuItemIndex = layoutManager.getPosition(foundView);
      }
    });
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    final int menuResource = getIntent()
        .getIntExtra(EXTRA_MENU_KEY, EXTRA_MENU_ITEM_DEFAULT_VALUE);
    if (menuResource != EXTRA_MENU_ITEM_DEFAULT_VALUE) {
      final MenuInflater inflater = getMenuInflater();
      inflater.inflate(menuResource, menu);

      for (int i = 0; i < menu.size(); i++) {
        final MenuItem menuItem = menu.getItem(i);
        menuItems.add(
            new GlassMenuItem(menuItem.getItemId(), menuItem.getIcon(),
                menuItem.getTitle().toString()));
        adapter.notifyDataSetChanged();
      }
    }
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onGesture(Gesture gesture) {
    switch (gesture) {
      case TAP:
        final Intent intent = new Intent();
        intent.putExtra(EXTRA_MENU_ITEM_ID_KEY, menuItems.get(currentMenuItemIndex).getId());
        setResult(RESULT_OK, intent);
        finish();
        return true;
      default:
        return super.onGesture(gesture);
    }
  }
}
