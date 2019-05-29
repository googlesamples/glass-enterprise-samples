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

package com.example.glass.camera2sample;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * Gesture detector for Google Glass usage purposes.
 */
public class GlassGestureDetector implements GestureDetector.OnGestureListener {

  /**
   * Currently handled gestures.
   */
  public enum Gesture {
    TAP,
    SWIPE_FORWARD,
    SWIPE_BACKWARD,
    SWIPE_UP,
    SWIPE_DOWN
  }

  /**
   * Listens for the gestures.
   */
  public interface OnGestureListener {

    /**
     * Should notify about detected gesture.
     *
     * @param gesture is a detected gesture.
     * @return TRUE if gesture is handled by the medhod. FALSE otherwise.
     */
    boolean onGesture(Gesture gesture);
  }

  static final int SWIPE_DISTANCE_THRESHOLD_PX = 100;
  static final int SWIPE_VELOCITY_THRESHOLD_PX = 100;
  private static final double TAN_ANGLE_DEGREES = Math.tan(Math.toRadians(60));

  private GestureDetector gestureDetector;
  private OnGestureListener onGestureListener;

  /**
   * {@link GlassGestureDetector} object is constructed by usage of this method.
   *
   * @param context is a context of the application.
   * @param onGestureListener is a listener for the gestures.
   */
  public GlassGestureDetector(Context context, OnGestureListener onGestureListener) {
    gestureDetector = new GestureDetector(context, this);
    this.onGestureListener = onGestureListener;
  }

  /**
   * Passes the {@link MotionEvent} object from the activity to the Android {@link GestureDetector}.
   *
   * @param motionEvent is a detected {@link MotionEvent} object.
   * @return TRUE if event is handled by the Android {@link GestureDetector}. FALSE otherwise.
   */
  public boolean onTouchEvent(MotionEvent motionEvent) {
    return gestureDetector.onTouchEvent(motionEvent);
  }

  @Override
  public boolean onDown(MotionEvent e) {
    return false;
  }

  @Override
  public void onShowPress(MotionEvent e) {
  }

  @Override
  public boolean onSingleTapUp(MotionEvent e) {
    return onGestureListener.onGesture(Gesture.TAP);
  }

  @Override
  public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
    return false;
  }

  @Override
  public void onLongPress(MotionEvent e) {
  }

  /**
   * Swipe detection depends on the:
   * - movement tan value,
   * - movement distance,
   * - movement velocity.
   *
   * To prevent unintentional SWIPE_DOWN and SWIPE_UP gestures, they are detected if movement
   * angle is only between 60 and 120 degrees.
   * Any other detected swipes, will be considered as SWIPE_FORWARD and SWIPE_BACKWARD, depends
   * on deltaX value sign.
   *
   *           ______________________________________________________________
   *          |                     \        UP         /                    |
   *          |                       \               /                      |
   *          |                         60         120                       |
   *          |                           \       /                          |
   *          |                             \   /                            |
   *          |  BACKWARD  <-------  0  ------------  180  ------>  FORWARD  |
   *          |                             /   \                            |
   *          |                           /       \                          |
   *          |                         60         120                       |
   *          |                       /               \                      |
   *          |                     /       DOWN        \                    |
   *           --------------------------------------------------------------
   */
  @Override
  public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
    final float deltaX = e2.getX() - e1.getX();
    final float deltaY = e2.getY() - e1.getY();
    final double tan = deltaX != 0 ? Math.abs(deltaY/deltaX) : Double.MAX_VALUE;

    if (tan > TAN_ANGLE_DEGREES) {
      if (Math.abs(deltaY) < SWIPE_DISTANCE_THRESHOLD_PX || Math.abs(velocityY) < SWIPE_VELOCITY_THRESHOLD_PX) {
        return false;
      } else if (deltaY < 0) {
        return onGestureListener.onGesture(Gesture.SWIPE_UP);
      } else {
        return onGestureListener.onGesture(Gesture.SWIPE_DOWN);
      }
    } else {
      if (Math.abs(deltaX) < SWIPE_DISTANCE_THRESHOLD_PX || Math.abs(velocityX) < SWIPE_VELOCITY_THRESHOLD_PX) {
        return false;
      } else if (deltaX < 0) {
        return onGestureListener.onGesture(Gesture.SWIPE_FORWARD);
      } else {
        return onGestureListener.onGesture(Gesture.SWIPE_BACKWARD);
      }
    }
  }
}
