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

import android.content.Context;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

/**
 * Helper class responsible for performing animations on the camera shutter during taking pictures
 * and changing shutter icon to the camera icon and vice versa, depends on the current camera mode.
 */
public class AnimationManager {

  /**
   * Minimum alpha for animations.
   */
  private static final float ALPHA_MIN = 0F;

  /**
   * Maximum alpha for animations.
   */
  private static final float ALPHA_MAX = 0.8F;

  /**
   * Animates shutter action on the given {@link ImageView}.
   */
  public static void animateShutter(Context context, ImageView imageView) {
    imageView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.alpha));
  }

  /**
   * Changes given {@link ImageView} image resource by the animation using alpha.
   */
  public static void changeImageByAlpha(final ImageView imageView, final int resource) {
    imageView.animate().alpha(ALPHA_MIN).withEndAction(new Runnable() {
      @Override
      public void run() {
        imageView.setImageResource(resource);
        imageView.animate().alpha(ALPHA_MAX);
      }
    });
  }
}
