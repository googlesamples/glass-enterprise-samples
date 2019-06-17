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

package com.example.glass.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.view.MotionEvent;
import com.example.glass.ui.GlassGestureDetector.Gesture;
import com.example.glass.ui.GlassGestureDetector.OnGestureListener;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

@RunWith(RobolectricTestRunner.class)
public class GlassGestureDetectorTest {

  private static final int DOWN_TIME = 10;
  private static final int EVENT_TIME = 10;
  private static final int ACTION = MotionEvent.ACTION_UP;
  private static final int INITIAL_X = 200;
  private static final int INITIAL_Y = 200;
  private static final int META_STATE = 0;
  private static final int TWICE_SWIPE_DISTANCE_THRESHOLD_PX =
      2 * GlassGestureDetector.SWIPE_DISTANCE_THRESHOLD_PX;
  private static final int TWICE_SWIPE_VELOCITY_THRESHOLD_PX =
      2 * GlassGestureDetector.SWIPE_VELOCITY_THRESHOLD_PX;
  private static final int HALF_SWIPE_DISTANCE_THRESHOLD_PX =
      GlassGestureDetector.SWIPE_DISTANCE_THRESHOLD_PX / 2;
  private static final int HALF_SWIPE_VELOCITY_THRESHOLD_PX =
      GlassGestureDetector.SWIPE_VELOCITY_THRESHOLD_PX / 2;

  private static final float ANGLE_EPSILON_DEGREES = 0.1F;
  private static final float ANGLE_60_DEGREES = 60;
  private static final float ANGLE_120_DEGREES = 120;

  private GlassGestureDetector glassGestureDetector;
  private MotionEvent motionEvent;
  private Gesture detectedGesture;

  @Before
  public void setUp() {
    glassGestureDetector = new GlassGestureDetector(RuntimeEnvironment.systemContext,
        new GestureListener());
    motionEvent = MotionEvent
        .obtain(DOWN_TIME, EVENT_TIME, ACTION, INITIAL_X, INITIAL_Y, META_STATE);
  }

  @Test
  public void onTouchEvent() {
    assertFalse(glassGestureDetector.onTouchEvent(motionEvent));
  }

  @Test
  public void onDown() {
    assertFalse(glassGestureDetector.onDown(motionEvent));
  }

  @Test
  public void onShowPress() {
    glassGestureDetector.onShowPress(motionEvent);
  }

  @Test
  public void onSingleTapUp() {
    assertTrue(glassGestureDetector.onSingleTapUp(motionEvent));
    assertEquals(Gesture.TAP, detectedGesture);
  }

  @Test
  public void onScroll() {
    assertFalse(
        glassGestureDetector.onScroll(motionEvent, motionEvent, TWICE_SWIPE_DISTANCE_THRESHOLD_PX,
            TWICE_SWIPE_DISTANCE_THRESHOLD_PX));
  }

  @Test
  public void onLongPress() {
    glassGestureDetector.onLongPress(motionEvent);
  }

  @Test
  public void onFlingTestSwipeDown() {
    assertFalse(glassGestureDetector
        .onFling(motionEvent, getMotionEventForGesture(Gesture.SWIPE_DOWN),
            HALF_SWIPE_VELOCITY_THRESHOLD_PX,
            HALF_SWIPE_VELOCITY_THRESHOLD_PX));
    assertTrue(glassGestureDetector
        .onFling(motionEvent, getMotionEventForGesture(Gesture.SWIPE_DOWN),
            TWICE_SWIPE_VELOCITY_THRESHOLD_PX,
            TWICE_SWIPE_VELOCITY_THRESHOLD_PX));
    assertEquals(Gesture.SWIPE_DOWN, detectedGesture);
  }

  @Test
  public void onFlingTestSwipeUp() {
    assertFalse(glassGestureDetector
        .onFling(motionEvent, getMotionEventForGesture(Gesture.SWIPE_UP),
            HALF_SWIPE_VELOCITY_THRESHOLD_PX,
            HALF_SWIPE_VELOCITY_THRESHOLD_PX));
    assertTrue(glassGestureDetector
        .onFling(motionEvent, getMotionEventForGesture(Gesture.SWIPE_UP),
            TWICE_SWIPE_VELOCITY_THRESHOLD_PX,
            TWICE_SWIPE_VELOCITY_THRESHOLD_PX));
    assertEquals(Gesture.SWIPE_UP, detectedGesture);
  }

  @Test
  public void onFlingTestSwipeForward() {
    assertFalse(glassGestureDetector
        .onFling(motionEvent, getMotionEventForGesture(Gesture.SWIPE_FORWARD),
            HALF_SWIPE_VELOCITY_THRESHOLD_PX,
            HALF_SWIPE_VELOCITY_THRESHOLD_PX));
    assertTrue(glassGestureDetector
        .onFling(motionEvent, getMotionEventForGesture(Gesture.SWIPE_FORWARD),
            TWICE_SWIPE_VELOCITY_THRESHOLD_PX,
            TWICE_SWIPE_VELOCITY_THRESHOLD_PX));
    assertEquals(Gesture.SWIPE_FORWARD, detectedGesture);
  }

  @Test
  public void onFlingTestSwipeBackward() {
    assertFalse(glassGestureDetector
        .onFling(motionEvent, getMotionEventForGesture(Gesture.SWIPE_BACKWARD),
            HALF_SWIPE_VELOCITY_THRESHOLD_PX,
            HALF_SWIPE_VELOCITY_THRESHOLD_PX));
    assertTrue(glassGestureDetector
        .onFling(motionEvent, getMotionEventForGesture(Gesture.SWIPE_BACKWARD),
            TWICE_SWIPE_VELOCITY_THRESHOLD_PX,
            TWICE_SWIPE_VELOCITY_THRESHOLD_PX));
    assertEquals(Gesture.SWIPE_BACKWARD, detectedGesture);
  }

  @Test
  public void onFlingTestTooSmallDistance() {
    final MotionEvent endMotionEvent = MotionEvent
        .obtain(DOWN_TIME, EVENT_TIME, ACTION,
            motionEvent.getX() + HALF_SWIPE_DISTANCE_THRESHOLD_PX,
            motionEvent.getY() + HALF_SWIPE_DISTANCE_THRESHOLD_PX, META_STATE);
    assertFalse(
        glassGestureDetector.onFling(motionEvent, endMotionEvent, TWICE_SWIPE_VELOCITY_THRESHOLD_PX,
            TWICE_SWIPE_VELOCITY_THRESHOLD_PX));
  }

  @Test
  public void testAngleSwipeForward() {
    final float endX = INITIAL_X - (float) (TWICE_SWIPE_DISTANCE_THRESHOLD_PX / getAbsTanDegrees(
        ANGLE_120_DEGREES));

    // 120 degrees
    final MotionEvent endMotionEvent = MotionEvent
        .obtain(DOWN_TIME, EVENT_TIME, ACTION, endX, INITIAL_Y - TWICE_SWIPE_DISTANCE_THRESHOLD_PX,
            META_STATE);
    glassGestureDetector.onFling(motionEvent, endMotionEvent, TWICE_SWIPE_VELOCITY_THRESHOLD_PX,
        TWICE_SWIPE_VELOCITY_THRESHOLD_PX);
    assertEquals(Gesture.SWIPE_FORWARD, detectedGesture);

    // -120 degrees
    final MotionEvent endMotionEvent2 = MotionEvent
        .obtain(DOWN_TIME, EVENT_TIME, ACTION, endX, INITIAL_Y + TWICE_SWIPE_DISTANCE_THRESHOLD_PX,
            META_STATE);
    glassGestureDetector.onFling(motionEvent, endMotionEvent2, TWICE_SWIPE_VELOCITY_THRESHOLD_PX,
        TWICE_SWIPE_VELOCITY_THRESHOLD_PX);
    assertEquals(Gesture.SWIPE_FORWARD, detectedGesture);
  }

  @Test
  public void testAngleSwipeBackward() {
    final float endX = INITIAL_X + (float) (TWICE_SWIPE_DISTANCE_THRESHOLD_PX / getAbsTanDegrees(
        ANGLE_60_DEGREES));

    // SWIPE_BACKWARD will be detected if movement angle is smaller or equal 60 degrees clockwise from X axis
    final MotionEvent endMotionEvent = MotionEvent
        .obtain(DOWN_TIME, EVENT_TIME, ACTION, endX, INITIAL_Y - TWICE_SWIPE_DISTANCE_THRESHOLD_PX,
            META_STATE);
    glassGestureDetector.onFling(motionEvent, endMotionEvent, TWICE_SWIPE_VELOCITY_THRESHOLD_PX,
        TWICE_SWIPE_VELOCITY_THRESHOLD_PX);
    assertEquals(Gesture.SWIPE_BACKWARD, detectedGesture);

    // SWIPE_BACKWARD will be detected if movement angle is smaller or equal -60 degrees counterclockwise from X axis
    final MotionEvent endMotionEvent2 = MotionEvent
        .obtain(DOWN_TIME, EVENT_TIME, ACTION, endX, INITIAL_Y + TWICE_SWIPE_DISTANCE_THRESHOLD_PX,
            META_STATE);
    glassGestureDetector.onFling(motionEvent, endMotionEvent2, TWICE_SWIPE_VELOCITY_THRESHOLD_PX,
        TWICE_SWIPE_VELOCITY_THRESHOLD_PX);
    assertEquals(Gesture.SWIPE_BACKWARD, detectedGesture);
  }

  @Test
  public void testAngleSwipeUp() {
    // SWIPE_UP gesture will be detected if movement angle is greater than 60 degrees clockwise from X axis
    final float endX = INITIAL_X - (float) (TWICE_SWIPE_DISTANCE_THRESHOLD_PX / getAbsTanDegrees(
        ANGLE_60_DEGREES + ANGLE_EPSILON_DEGREES));
    final MotionEvent endMotionEvent = MotionEvent
        .obtain(DOWN_TIME, EVENT_TIME, ACTION, endX, INITIAL_Y - TWICE_SWIPE_DISTANCE_THRESHOLD_PX,
            META_STATE);
    glassGestureDetector.onFling(motionEvent, endMotionEvent, TWICE_SWIPE_VELOCITY_THRESHOLD_PX,
        TWICE_SWIPE_VELOCITY_THRESHOLD_PX);
    assertEquals(Gesture.SWIPE_UP, detectedGesture);

    // SWIPE_UP gesture will be detected if movement angle is smaller than 120 degrees clockwise from X axis
    final float endX2 = INITIAL_X + (float) (TWICE_SWIPE_DISTANCE_THRESHOLD_PX / getAbsTanDegrees(
        ANGLE_120_DEGREES - ANGLE_EPSILON_DEGREES));
    final MotionEvent endMotionEvent2 = MotionEvent
        .obtain(DOWN_TIME, EVENT_TIME, ACTION, endX2, INITIAL_Y - TWICE_SWIPE_DISTANCE_THRESHOLD_PX,
            META_STATE);
    glassGestureDetector.onFling(motionEvent, endMotionEvent2, TWICE_SWIPE_VELOCITY_THRESHOLD_PX,
        TWICE_SWIPE_VELOCITY_THRESHOLD_PX);
    assertEquals(Gesture.SWIPE_UP, detectedGesture);
  }

  @Test
  public void testAngleSwipeDown() {
    // SWIPE_DOWN will be detected if movement angle is greater than 60 degrees counterclockwise from X axis
    final float endX = INITIAL_X - (float) (TWICE_SWIPE_DISTANCE_THRESHOLD_PX / getAbsTanDegrees(
        ANGLE_60_DEGREES + ANGLE_EPSILON_DEGREES));
    final MotionEvent endMotionEvent = MotionEvent
        .obtain(DOWN_TIME, EVENT_TIME, ACTION, endX, INITIAL_Y + TWICE_SWIPE_DISTANCE_THRESHOLD_PX,
            META_STATE);
    glassGestureDetector.onFling(motionEvent, endMotionEvent, TWICE_SWIPE_VELOCITY_THRESHOLD_PX,
        TWICE_SWIPE_VELOCITY_THRESHOLD_PX);
    assertEquals(Gesture.SWIPE_DOWN, detectedGesture);

    // SWIPE_DOWN will be detected if movement angle is smaller than 120 degrees counterclockwise from X axis
    final float endX2 = INITIAL_X + (float) (TWICE_SWIPE_DISTANCE_THRESHOLD_PX / getAbsTanDegrees(
        ANGLE_120_DEGREES - ANGLE_EPSILON_DEGREES));
    final MotionEvent endMotionEvent2 = MotionEvent
        .obtain(DOWN_TIME, EVENT_TIME, ACTION, endX2, INITIAL_Y + TWICE_SWIPE_DISTANCE_THRESHOLD_PX,
            META_STATE);
    glassGestureDetector.onFling(motionEvent, endMotionEvent2, TWICE_SWIPE_VELOCITY_THRESHOLD_PX,
        TWICE_SWIPE_VELOCITY_THRESHOLD_PX);
    assertEquals(Gesture.SWIPE_DOWN, detectedGesture);
  }

  private MotionEvent getMotionEventForGesture(Gesture gesture) {
    int X_CHANGE = 0;
    int Y_CHANGE = 0;

    switch (gesture) {
      case SWIPE_UP:
        Y_CHANGE -= TWICE_SWIPE_DISTANCE_THRESHOLD_PX;
        break;
      case SWIPE_DOWN:
        Y_CHANGE += TWICE_SWIPE_DISTANCE_THRESHOLD_PX;
        break;
      case SWIPE_FORWARD:
        X_CHANGE -= TWICE_SWIPE_DISTANCE_THRESHOLD_PX;
        break;
      case SWIPE_BACKWARD:
        X_CHANGE += TWICE_SWIPE_DISTANCE_THRESHOLD_PX;
        break;
      default:
    }

    return MotionEvent
        .obtain(DOWN_TIME, EVENT_TIME, ACTION, INITIAL_X + X_CHANGE, INITIAL_Y + Y_CHANGE,
            META_STATE);
  }

  private double getAbsTanDegrees(float degrees) {
    return Math.abs(Math.tan(Math.toRadians(degrees)));
  }

  class GestureListener implements OnGestureListener {

    @Override
    public boolean onGesture(Gesture gesture) {
      detectedGesture = gesture;
      return true;
    }
  }
}