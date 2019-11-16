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

package com.example.glass.qrcodescannersample;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import com.example.glass.qrcodescannersample.GlassGestureDetector.OnGestureListener;

/**
 * Base Activity class used to hide the system UI and capture gestures.
 */
public abstract class BaseActivity extends FragmentActivity implements OnGestureListener {

  private GlassGestureDetector glassGestureDetector;
  private View decorView;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    decorView = getWindow().getDecorView();
    decorView.setOnSystemUiVisibilityChangeListener(visibility -> {
      if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
        hideSystemUI();
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
