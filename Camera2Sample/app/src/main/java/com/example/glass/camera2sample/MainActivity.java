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

package com.example.glass.camera2sample;

import android.os.Bundle;
import android.view.KeyEvent;

import com.example.glass.ui.GlassGestureDetector;

/**
 * Main activity of the application. It creates instance of {@link CameraFragment} and passes
 * gestures to it.
 */
public class MainActivity extends BaseActivity {

  private CameraFragment cameraFragment;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    cameraFragment = (CameraFragment) getSupportFragmentManager()
        .findFragmentById(R.id.camera_fragment);
  }

  @Override
  public boolean onGesture(GlassGestureDetector.Gesture gesture) {
    return cameraFragment.onGesture(gesture) || super.onGesture(gesture);
  }

  @Override
  public boolean onKeyUp(int keyCode, KeyEvent event) {
    return cameraFragment.onKeyUp(keyCode) || super.onKeyUp(keyCode, event);
  }

  @Override
  public boolean onKeyLongPress(int keyCode, KeyEvent event) {
    return cameraFragment.onKeyLongPress(keyCode) || super.onKeyLongPress(keyCode, event);
  }
}
