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

package com.example.glass.camera2sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import com.example.glass.ui.GlassGestureDetector;

/**
 * Base activity for all activities in application for Glass. It hides the UI, making application
 * full screen and sets {@link GlassGestureDetector}.
 */
public abstract class BaseActivity extends AppCompatActivity
        implements GlassGestureDetector.OnGestureListener {

  private View decorView;
  private GlassGestureDetector glassGestureDetector;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getSupportActionBar() != null) {
      getSupportActionBar().hide();
    }
    decorView = getWindow().getDecorView();
    decorView
        .setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
          @Override
          public void onSystemUiVisibilityChange(int visibility) {
            if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
              hideSystemUI();
            }
          }
        });
    glassGestureDetector = new GlassGestureDetector(this, this);
  }

  @Override
  protected void onResume() {
    super.onResume();
    hideSystemUI();
  }

  @Override
  public boolean dispatchTouchEvent(MotionEvent ev) {
    return glassGestureDetector.onTouchEvent(ev) || super.dispatchTouchEvent(ev);
  }

  @Override
  public boolean onGesture(GlassGestureDetector.Gesture gesture) {
    switch (gesture) {
      case SWIPE_DOWN:
        finish();
        return true;
      case SWIPE_UP:
        final Intent homeButtonIntent = new Intent(Intent.ACTION_MAIN);
        homeButtonIntent.addCategory(Intent.CATEGORY_HOME);
        homeButtonIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(homeButtonIntent);
        return true;
      default:
        return false;
    }
  }

  private void hideSystemUI() {
    decorView.setSystemUiVisibility(
        View.SYSTEM_UI_FLAG_IMMERSIVE
            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_FULLSCREEN);
  }
}
