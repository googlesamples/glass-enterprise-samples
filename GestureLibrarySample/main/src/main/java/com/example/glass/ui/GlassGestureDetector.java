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

package com.example.glass.ui;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;

/**
 * Gesture detector for Google Glass usage purposes.
 *
 * It detects one and two finger gestures like:
 * <ul>
 *   <li>TAP</li>
 *   <li>TWO_FINGER_TAP</li>
 *   <li>SWIPE_FORWARD</li>
 *   <li>TWO_FINGER_SWIPE_FORWARD</li>
 *   <li>SWIPE_BACKWARD</li>
 *   <li>TWO_FINGER_SWIPE_BACKWARD</li>
 *   <li>SWIPE_UP</li>
 *   <li>TWO_FINGER_SWIPE_UP</li>
 *   <li>SWIPE_DOWN</li>
 *   <li>TWO_FINGER_SWIPE_DOWN</li>
 * </ul>
 *
 * Swipe detection depends on the:
 * <ul>
 *   <li>movement tan value</li>
 *   <li>movement distance</li>
 *   <li>movement velocity</li>
 * </ul>
 *
 * To prevent unintentional SWIPE_DOWN, TWO_FINGER_SWIPE_DOWN, SWIPE_UP and TWO_FINGER_SWIPE_UP
 * gestures, they are detected if movement angle is only between 60 and 120 degrees to the
 * Glass touchpad horizontal axis.
 * Any other detected swipes, will be considered as SWIPE_FORWARD and SWIPE_BACKWARD gestures,
 * depends on the sign of the axis x movement value.
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
public class GlassGestureDetector {

  /**
   * Currently handled gestures.
   */
  public enum Gesture {
    TAP,
    TWO_FINGER_TAP,
    SWIPE_FORWARD,
    TWO_FINGER_SWIPE_FORWARD,
    SWIPE_BACKWARD,
    TWO_FINGER_SWIPE_BACKWARD,
    SWIPE_UP,
    TWO_FINGER_SWIPE_UP,
    SWIPE_DOWN,
    TWO_FINGER_SWIPE_DOWN
  }

  /**
   * Listens for the gestures.
   */
  public interface OnGestureListener {

    /**
     * Should notify about detected gesture.
     *
     * @param gesture is a detected gesture.
     * @return TRUE if gesture is handled by the method. FALSE otherwise.
     */
    boolean onGesture(Gesture gesture);

    /**
     * Notifies when a scroll occurs with the initial on down {@link MotionEvent} and the current
     * move {@link MotionEvent}. The distance in x and y is also supplied for convenience.
     *
     * @param e1 The first down motion event that started the scrolling.
     * @param e2 The move motion event that triggered the current onScroll.
     * @param distanceX The distance along the X axis that has been scrolled since the last call to
     * onScroll. This is NOT the distance between {@code e1} and {@code e2}.
     * @param distanceY The distance along the Y axis that has been scrolled since the last call to
     * onScroll. This is NOT the distance between {@code e1} and {@code e2}.
     * @return true if the event is consumed, else false
     */
    default boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
      return false;
    }

    /**
     * Notifies when touch is ended.
     */
    default void onTouchEnded() {
    }
  }

  private static final int VELOCITY_UNIT = 1000;
  private static final int FIRST_FINGER_POINTER_INDEX = 0;
  private static final int SECOND_FINGER_POINTER_INDEX = 1;
  private static final double TAN_ANGLE_DEGREES = Math.tan(Math.toRadians(60));
  static final int SWIPE_DISTANCE_THRESHOLD_PX = 100;
  static final int SWIPE_VELOCITY_THRESHOLD_PX = 100;

  private final int touchSlopSquare;
  /**
   * This flag is set to true each time the {@link MotionEvent#ACTION_DOWN} action appears
   * and it remains true until the finger moves out of the tap region.
   * Checking of the finger movement takes place each time the {@link MotionEvent#ACTION_MOVE}
   * action appears, until finger moves out of the tap region.
   * If this flag is set to false, {@link Gesture#TAP} and {@link Gesture#TWO_FINGER_TAP}
   * gestures won't be notified as detected.
   *
   * Tap region is calculated from the {@link ViewConfiguration#getScaledTouchSlop()} value.
   * It prevents from detecting {@link Gesture#TAP} and {@link Gesture#TWO_FINGER_TAP} gestures
   * during the scrolling on the touchpad.
   */
  private boolean isInTapRegion;
  private boolean isTwoFingerGesture = false;
  private boolean isActionDownPerformed = false;
  private float firstFingerDownX;
  private float firstFingerDownY;
  private float firstFingerLastFocusX;
  private float firstFingerLastFocusY;
  private float firstFingerVelocityX;
  private float firstFingerVelocityY;
  private float firstFingerDistanceX;
  private float firstFingerDistanceY;
  private float secondFingerDownX;
  private float secondFingerDownY;
  private float secondFingerDistanceX;
  private float secondFingerDistanceY;
  private VelocityTracker velocityTracker;
  private MotionEvent currentDownEvent;
  private OnGestureListener onGestureListener;

  /**
   * {@link GlassGestureDetector} object is constructed by usage of this method.
   *
   * @param context is a context of the application.
   * @param onGestureListener is a listener for the gestures.
   */
  public GlassGestureDetector(Context context, OnGestureListener onGestureListener) {
    final int touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    touchSlopSquare = touchSlop * touchSlop;
    this.onGestureListener = onGestureListener;
  }

  /**
   * Passes the {@link MotionEvent} object from the activity to the Android {@link
   * GestureDetector}.
   *
   * @param motionEvent is a detected {@link MotionEvent} object.
   * @return TRUE if event is handled by the Android {@link GestureDetector}. FALSE otherwise.
   */
  public boolean onTouchEvent(MotionEvent motionEvent) {
    if (velocityTracker == null) {
      velocityTracker = VelocityTracker.obtain();
    }
    velocityTracker.addMovement(motionEvent);
    boolean handled = false;

    switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
      case MotionEvent.ACTION_DOWN:
        firstFingerDownX = firstFingerLastFocusX = motionEvent.getX();
        firstFingerDownY = firstFingerLastFocusY = motionEvent.getY();
        isActionDownPerformed = true;
        isInTapRegion = true;
        if (currentDownEvent != null) {
          currentDownEvent.recycle();
        }
        currentDownEvent = MotionEvent.obtain(motionEvent);
        break;
      case MotionEvent.ACTION_POINTER_DOWN:
        isTwoFingerGesture = true;
        secondFingerDownX = motionEvent.getX(motionEvent.getActionIndex());
        secondFingerDownY = motionEvent.getY(motionEvent.getActionIndex());
        break;
      case MotionEvent.ACTION_MOVE:
        final float firstFingerFocusX = motionEvent.getX(FIRST_FINGER_POINTER_INDEX);
        final float firstFingerFocusY = motionEvent.getY(FIRST_FINGER_POINTER_INDEX);
        final float scrollX = firstFingerLastFocusX - firstFingerFocusX;
        final float scrollY = firstFingerLastFocusY - firstFingerFocusY;
        firstFingerDistanceX = firstFingerFocusX - firstFingerDownX;
        firstFingerDistanceY = firstFingerFocusY - firstFingerDownY;
        if (motionEvent.getPointerCount() > 1) {
          secondFingerDistanceX =
              motionEvent.getX(SECOND_FINGER_POINTER_INDEX) - secondFingerDownX;
          secondFingerDistanceY =
              motionEvent.getY(SECOND_FINGER_POINTER_INDEX) - secondFingerDownY;
        }
        if (isInTapRegion) {
          final float distance = (firstFingerDistanceX * firstFingerDistanceX)
              + (firstFingerDistanceY * firstFingerDistanceY);
          float distanceSecondFinger = 0;
          if (motionEvent.getPointerCount() > 1) {
            distanceSecondFinger = (secondFingerDistanceX * secondFingerDistanceX)
                + (secondFingerDistanceY * secondFingerDistanceY);
          }
          if (distance > touchSlopSquare || distanceSecondFinger > touchSlopSquare) {
            isInTapRegion = false;
          }
        }
        if ((Math.abs(scrollX) >= 1) || (Math.abs(scrollY) >= 1)) {
          handled = onGestureListener
              .onScroll(currentDownEvent, motionEvent, scrollX, scrollY);
          firstFingerLastFocusX = firstFingerFocusX;
          firstFingerLastFocusY = firstFingerFocusY;
        }
        break;
      case MotionEvent.ACTION_UP:
        velocityTracker.computeCurrentVelocity(VELOCITY_UNIT);
        firstFingerVelocityX = velocityTracker
            .getXVelocity(motionEvent.getPointerId(motionEvent.getActionIndex()));
        firstFingerVelocityY = velocityTracker
            .getYVelocity(motionEvent.getPointerId(motionEvent.getActionIndex()));
        handled = detectGesture();
        onTouchEnded();
        break;
      case MotionEvent.ACTION_CANCEL:
        velocityTracker.recycle();
        velocityTracker = null;
        isInTapRegion = false;
        break;
    }
    return handled;
  }

  private boolean detectGesture() {
    if (!isActionDownPerformed) {
      return false;
    }
    final double tan =
        firstFingerDistanceX != 0 ? Math.abs(firstFingerDistanceY / firstFingerDistanceX)
            : Double.MAX_VALUE;

    if (isTwoFingerGesture) {
      final double tanSecondFinger =
          secondFingerDistanceX != 0 ? Math.abs(secondFingerDistanceY / secondFingerDistanceX)
              : Double.MAX_VALUE;
      return detectTwoFingerGesture(tan, tanSecondFinger);
    } else {
      return detectOneFingerGesture(tan);
    }
  }

  private boolean detectOneFingerGesture(double tan) {
    if (tan > TAN_ANGLE_DEGREES) {
      if (Math.abs(firstFingerDistanceY) < SWIPE_DISTANCE_THRESHOLD_PX
          || Math.abs(firstFingerVelocityY) < SWIPE_VELOCITY_THRESHOLD_PX) {
        if (isInTapRegion) {
          return onGestureListener.onGesture(Gesture.TAP);
        }
      } else if (firstFingerDistanceY < 0) {
        return onGestureListener.onGesture(Gesture.SWIPE_UP);
      } else if (firstFingerDistanceY > 0) {
        return onGestureListener.onGesture(Gesture.SWIPE_DOWN);
      }
    } else {
      if (Math.abs(firstFingerDistanceX) < SWIPE_DISTANCE_THRESHOLD_PX
          || Math.abs(firstFingerVelocityX) < SWIPE_VELOCITY_THRESHOLD_PX) {
        if (isInTapRegion) {
          return onGestureListener.onGesture(Gesture.TAP);
        }
      } else if (firstFingerDistanceX < 0) {
        return onGestureListener.onGesture(Gesture.SWIPE_FORWARD);
      } else if (firstFingerDistanceX > 0) {
        return onGestureListener.onGesture(Gesture.SWIPE_BACKWARD);
      }
    }
    return false;
  }

  private boolean detectTwoFingerGesture(double tan, double tanSecondFinger) {
    if (tan > TAN_ANGLE_DEGREES && tanSecondFinger > TAN_ANGLE_DEGREES) {
      if (Math.abs(firstFingerDistanceY) < SWIPE_DISTANCE_THRESHOLD_PX
          || Math.abs(firstFingerVelocityY) < SWIPE_VELOCITY_THRESHOLD_PX) {
        if (isInTapRegion) {
          return onGestureListener.onGesture(Gesture.TWO_FINGER_TAP);
        }
      } else if (firstFingerDistanceY < 0 && secondFingerDistanceY < 0) {
        return onGestureListener.onGesture(Gesture.TWO_FINGER_SWIPE_UP);
      } else if (firstFingerDistanceY > 0 && secondFingerDistanceY > 0) {
        return onGestureListener.onGesture(Gesture.TWO_FINGER_SWIPE_DOWN);
      }
    } else {
      if (Math.abs(firstFingerDistanceX) < SWIPE_DISTANCE_THRESHOLD_PX
          || Math.abs(firstFingerVelocityX) < SWIPE_VELOCITY_THRESHOLD_PX) {
        if (isInTapRegion) {
          return onGestureListener.onGesture(Gesture.TWO_FINGER_TAP);
        }
      } else if (firstFingerDistanceX < 0 && secondFingerDistanceX < 0) {
        return onGestureListener.onGesture(Gesture.TWO_FINGER_SWIPE_FORWARD);
      } else if (firstFingerDistanceX > 0 && secondFingerDistanceX > 0) {
        return onGestureListener.onGesture(Gesture.TWO_FINGER_SWIPE_BACKWARD);
      }
    }
    return false;
  }

  private void onTouchEnded() {
    isTwoFingerGesture = false;
    if (velocityTracker != null) {
      velocityTracker.recycle();
      velocityTracker = null;
    }
    isActionDownPerformed = false;
    onGestureListener.onTouchEnded();
  }
}
