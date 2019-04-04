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

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.example.android.glass.cardsample.R;

/**
 * Sets and holds the {@link View} for the {@link GlassMenuItem}.
 */
class GlassMenuItemViewHolder extends RecyclerView.ViewHolder {

  private static final int TEXT_SIZE_SP = 35;
  private static final int PADDING_16_DP = 16;
  private static final int PADDING_0_DP = 0;

  private ImageView icon;
  private TextView text;
  private GlassMenuItem glassMenuItem;
  private OnItemChosenListener listener;

  /**
   * {@link GlassMenuItemViewHolder} object is constructed using this method.
   *
   * @param itemView is a {@link GlassMenuItem} inflated view.
   * @param onItemChosenListener is a listener for notifying about the chosen menu item.
   */
  GlassMenuItemViewHolder(@NonNull View itemView, OnItemChosenListener onItemChosenListener) {
    super(itemView);
    listener = onItemChosenListener;
    itemView.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        listener.onItemChosen(glassMenuItem);
      }
    });

    final Context context = itemView.getContext();
    icon = new ImageView(context);
    icon.setId(ViewCompat.generateViewId());

    text = new TextView(context);
    text.setTextSize(TEXT_SIZE_SP);
    text.setTypeface(Typeface.create(context.getString(R.string.light_font), Typeface.NORMAL));
    text.setPadding(PADDING_16_DP, PADDING_0_DP, PADDING_0_DP, PADDING_0_DP);

    final LinearLayout linearLayout = new LinearLayout(context);
    linearLayout.setGravity(Gravity.CENTER);
    linearLayout.addView(icon);
    linearLayout.addView(text);

    final RelativeLayout relativeLayout = itemView.findViewById(R.id.body_layout);
    relativeLayout.setGravity(Gravity.CENTER);
    relativeLayout.addView(linearLayout);
  }

  /**
   * Sets glass menu item.
   *
   * @param glassMenuItem to pass the value from the object to the view.
   */
  void setGlassMenuItem(GlassMenuItem glassMenuItem) {
    this.glassMenuItem = glassMenuItem;
    icon.setImageDrawable(glassMenuItem.getIcon());
    text.setText(glassMenuItem.getText());
  }

  /**
   * Listener for the item chosen event.
   */
  public interface OnItemChosenListener {

    /**
     * Should be used to notify about chosen item.
     *
     * @param glassMenuItem is a item chosen by the user.
     */
    void onItemChosen(GlassMenuItem glassMenuItem);
  }
}
