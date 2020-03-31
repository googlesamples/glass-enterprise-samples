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
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import com.example.glass.ui.GlassGestureDetector.Gesture;
import com.example.glass.ui.GlassGestureDetector.OnGestureListener;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static com.example.glass.ui.MotionEventGenerator.DOWN_TIME;
import static com.example.glass.ui.MotionEventGenerator.INITIAL_X;
import static com.example.glass.ui.MotionEventGenerator.INITIAL_Y;
import static com.example.glass.ui.MotionEventGenerator.META_STATE;
import static com.example.glass.ui.MotionEventGenerator.SECOND_FINGER_INITIAL_X;
import static com.example.glass.ui.MotionEventGenerator.SECOND_FINGER_INITIAL_Y;
import static com.example.glass.ui.MotionEventGenerator.getActionCancel;
import static com.example.glass.ui.MotionEventGenerator.getActionDown;
import static com.example.glass.ui.MotionEventGenerator.getActionMove;
import static com.example.glass.ui.MotionEventGenerator.getActionUp;
import static com.example.glass.ui.MotionEventGenerator.getSecondFingerActionDown;
import static com.example.glass.ui.MotionEventGenerator.getSecondFingerActionMove;
import static com.example.glass.ui.MotionEventGenerator.getSecondFingerActionUp;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class GlassGestureDetectorTest {

  private static final int EVENT_TIME = 10;
  private static final int ACTION = MotionEvent.ACTION_UP;
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
  private static final double DELTA = 1e-15;

  private GlassGestureDetector glassGestureDetector;
  private MotionEvent motionEvent;
  private Gesture detectedGesture;
  private boolean isTouchEnded;
  private boolean isScrolling;
  private float scrollingDistanceX;
  private float scrollingDistanceY;
  private int touchSlop;

  @Before
  public void setUp() {
    final Context context = RuntimeEnvironment.application;
    glassGestureDetector = new GlassGestureDetector(context, new GestureListener());
    motionEvent = MotionEvent
        .obtain(DOWN_TIME, EVENT_TIME, ACTION, INITIAL_X, INITIAL_Y, META_STATE);
    isTouchEnded = false;
    isScrolling = false;
    scrollingDistanceX = 0;
    scrollingDistanceY = 0;
    touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
  }

  @Test
  public void testOnTouchEvent() {
    assertFalse(glassGestureDetector.onTouchEvent(motionEvent));
  }

  @Test
  public void testOnDown() {
    assertFalse(glassGestureDetector.onDown(motionEvent));
  }

  @Test
  public void testOnShowPress() {
    glassGestureDetector.onShowPress(motionEvent);
  }

  @Test
  public void testOnSingleTapUp() {
    assertTrue(glassGestureDetector.onSingleTapUp(motionEvent));
    assertEquals(Gesture.TAP, detectedGesture);
  }

  @Test
  public void testOnScroll() {
    assertFalse(isScrolling);
    assertFalse(
        glassGestureDetector.onScroll(motionEvent, motionEvent, TWICE_SWIPE_DISTANCE_THRESHOLD_PX,
            TWICE_SWIPE_DISTANCE_THRESHOLD_PX));
    assertTrue(isScrolling);
    assertEquals(TWICE_SWIPE_DISTANCE_THRESHOLD_PX, scrollingDistanceX, DELTA);
    assertEquals(TWICE_SWIPE_DISTANCE_THRESHOLD_PX, scrollingDistanceY, DELTA);
  }

  @Test
  public void testScrolling() {
    assertFalse(isScrolling);
    glassGestureDetector.onTouchEvent(getActionDown());
    assertFalse(isScrolling);
    glassGestureDetector.onTouchEvent(
        getActionMove(INITIAL_X, INITIAL_Y - TWICE_SWIPE_DISTANCE_THRESHOLD_PX));
    assertTrue(isScrolling);
    assertEquals(0, scrollingDistanceX, DELTA);
    assertEquals(TWICE_SWIPE_DISTANCE_THRESHOLD_PX, scrollingDistanceY, DELTA);
    glassGestureDetector.onTouchEvent(
        getActionUp(INITIAL_X, INITIAL_Y - TWICE_SWIPE_DISTANCE_THRESHOLD_PX));
    assertTrue(isScrolling);
    assertEquals(0, scrollingDistanceX, DELTA);
    assertEquals(TWICE_SWIPE_DISTANCE_THRESHOLD_PX, scrollingDistanceY, DELTA);
    assertEquals(Gesture.SWIPE_UP, detectedGesture);
  }

  @Test
  public void testScrollingSmallDistance() {
    assertFalse(isScrolling);
    glassGestureDetector.onTouchEvent(getActionDown());
    assertFalse(isScrolling);
    glassGestureDetector.onTouchEvent(
        getActionMove(INITIAL_X, INITIAL_Y - HALF_SWIPE_DISTANCE_THRESHOLD_PX));
    assertTrue(isScrolling);
    assertEquals(0, scrollingDistanceX, DELTA);
    assertEquals(HALF_SWIPE_DISTANCE_THRESHOLD_PX, scrollingDistanceY, DELTA);
    glassGestureDetector.onTouchEvent(
        getActionUp(INITIAL_X, INITIAL_Y - HALF_SWIPE_DISTANCE_THRESHOLD_PX));
    assertTrue(isScrolling);
    assertEquals(0, scrollingDistanceX, DELTA);
    assertEquals(HALF_SWIPE_DISTANCE_THRESHOLD_PX, scrollingDistanceY, DELTA);
    assertNull(detectedGesture);
  }

  @Test
  public void testOnLongPress() {
    glassGestureDetector.onLongPress(motionEvent);
  }

  @Test
  public void testOnFlingTestSwipeDown() {
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
  public void testOnFlingTestSwipeUp() {
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
  public void testOnFlingTestSwipeForward() {
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
  public void testOnFlingTestSwipeBackward() {
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
  public void testOnFlingTestTooSmallDistance() {
    final MotionEvent endMotionEvent = MotionEvent
        .obtain(DOWN_TIME, EVENT_TIME, ACTION,
            motionEvent.getX() + HALF_SWIPE_DISTANCE_THRESHOLD_PX,
            motionEvent.getY() + HALF_SWIPE_DISTANCE_THRESHOLD_PX, META_STATE);
    assertFalse(
        glassGestureDetector.onFling(motionEvent, endMotionEvent, TWICE_SWIPE_VELOCITY_THRESHOLD_PX,
            TWICE_SWIPE_VELOCITY_THRESHOLD_PX));
  }

  @Test
  public void testTapGesture() {
    assertNull(detectedGesture);
    assertFalse(glassGestureDetector.onTouchEvent(getActionDown()));
    assertNull(detectedGesture);
    assertFalse(glassGestureDetector.onTouchEvent(getActionMove(INITIAL_X, INITIAL_Y)));
    assertNull(detectedGesture);
    assertTrue(glassGestureDetector.onTouchEvent(getActionUp(INITIAL_X, INITIAL_Y)));
    assertEquals(Gesture.TAP, detectedGesture);
  }

  @Test
  public void testTwoFingerTapGesture() {
    assertNull(detectedGesture);
    assertFalse(glassGestureDetector.onTouchEvent(getActionDown()));
    assertFalse(glassGestureDetector.onTouchEvent(getSecondFingerActionDown()));
    assertNull(detectedGesture);
    assertFalse(glassGestureDetector.onTouchEvent(getActionMove(INITIAL_X, INITIAL_Y)));
    assertFalse(glassGestureDetector
        .onTouchEvent(getSecondFingerActionMove(SECOND_FINGER_INITIAL_X, SECOND_FINGER_INITIAL_Y)));
    assertNull(detectedGesture);
    assertFalse(glassGestureDetector
        .onTouchEvent(getSecondFingerActionUp(SECOND_FINGER_INITIAL_X, SECOND_FINGER_INITIAL_Y)));
    assertTrue(glassGestureDetector.onTouchEvent(getActionUp(INITIAL_X, INITIAL_Y)));
    assertEquals(Gesture.TWO_FINGER_TAP, detectedGesture);
  }

  @Test
  public void testOnTouchEnded() {
    assertFalse(isTouchEnded);
    glassGestureDetector.onTouchEvent(getActionDown());
    assertFalse(isTouchEnded);
    glassGestureDetector.onTouchEvent(getActionMove(INITIAL_X, INITIAL_Y));
    assertFalse(isTouchEnded);
    glassGestureDetector.onTouchEvent(getActionUp(INITIAL_X, INITIAL_Y));
    assertTrue(isTouchEnded);
  }

  @Test
  public void testInTapRegion() {
    final int inTapRegionDistance = touchSlop - 1;
    assertNull(detectedGesture);
    assertFalse(glassGestureDetector.onTouchEvent(getActionDown()));
    assertNull(detectedGesture);
    assertFalse(glassGestureDetector
        .onTouchEvent(getActionMove(INITIAL_X, INITIAL_Y - inTapRegionDistance)));
    assertNull(detectedGesture);
    assertTrue(
        glassGestureDetector.onTouchEvent(getActionUp(INITIAL_X, INITIAL_Y - inTapRegionDistance)));
    assertEquals(Gesture.TAP, detectedGesture);
  }

  @Test
  public void testNotInTapRegion() {
    final int notInTapRegionDistance = touchSlop + 1;
    assertNull(detectedGesture);
    assertFalse(glassGestureDetector.onTouchEvent(getActionDown()));
    assertNull(detectedGesture);
    assertFalse(glassGestureDetector
        .onTouchEvent(getActionMove(INITIAL_X, INITIAL_Y - notInTapRegionDistance)));
    assertNull(detectedGesture);
    assertFalse(glassGestureDetector
        .onTouchEvent(getActionUp(INITIAL_X, INITIAL_Y - notInTapRegionDistance)));
    assertNull(detectedGesture);
  }

  @Test
  public void testCancelledTapGesture() {
    assertFalse(glassGestureDetector.onTouchEvent(getActionDown()));
    assertFalse(glassGestureDetector.onTouchEvent(getActionMove(INITIAL_X, INITIAL_Y)));
    assertFalse(glassGestureDetector.onTouchEvent(getActionCancel()));
    assertFalse(glassGestureDetector.onTouchEvent(getActionUp(INITIAL_X, INITIAL_Y)));
    assertNull(detectedGesture);
  }

  @Test
  public void testCancelledSwipeGesture() {
    assertFalse(glassGestureDetector.onTouchEvent(getActionDown()));
    assertFalse(glassGestureDetector.onTouchEvent(getActionCancel()));
    assertFalse(glassGestureDetector.onTouchEvent(
        getActionMove(INITIAL_X, INITIAL_Y + TWICE_SWIPE_DISTANCE_THRESHOLD_PX)));
    assertFalse(glassGestureDetector.onTouchEvent(
        getActionUp(INITIAL_X, INITIAL_Y + TWICE_SWIPE_DISTANCE_THRESHOLD_PX)));
    assertNull(detectedGesture);
  }

  @Test
  public void testAngleSwipeForward() {
    final float endX = INITIAL_X - (float) (TWICE_SWIPE_DISTANCE_THRESHOLD_PX / getAbsTanDegrees(
        ANGLE_120_DEGREES));

    // 120 degrees
    assertFalse(glassGestureDetector.onTouchEvent(getActionDown()));
    assertFalse(glassGestureDetector.onTouchEvent(
        getActionMove(endX, INITIAL_Y - TWICE_SWIPE_DISTANCE_THRESHOLD_PX)));
    assertTrue(glassGestureDetector.onTouchEvent(
        getActionUp(endX, INITIAL_Y - TWICE_SWIPE_DISTANCE_THRESHOLD_PX)));
    assertEquals(Gesture.SWIPE_FORWARD, detectedGesture);

    // -120 degrees
    assertFalse(glassGestureDetector.onTouchEvent(getActionDown()));
    assertFalse(glassGestureDetector.onTouchEvent(
        getActionMove(endX, INITIAL_Y + TWICE_SWIPE_DISTANCE_THRESHOLD_PX)));
    assertTrue(glassGestureDetector.onTouchEvent(
        getActionUp(endX, INITIAL_Y + TWICE_SWIPE_DISTANCE_THRESHOLD_PX)));
    assertEquals(Gesture.SWIPE_FORWARD, detectedGesture);
  }

  @Test
  public void testAngleTwoFingerSwipeForward() {
    final float endX = INITIAL_X - (float) (TWICE_SWIPE_DISTANCE_THRESHOLD_PX / getAbsTanDegrees(
        ANGLE_120_DEGREES));
    final float secondFingerEndX =
        SECOND_FINGER_INITIAL_X - (float) (TWICE_SWIPE_DISTANCE_THRESHOLD_PX / getAbsTanDegrees(
            ANGLE_120_DEGREES));

    // 120 degrees
    assertFalse(glassGestureDetector.onTouchEvent(getActionDown()));
    assertFalse(glassGestureDetector.onTouchEvent(getSecondFingerActionDown()));
    assertFalse(glassGestureDetector.onTouchEvent(
        getActionMove(endX, INITIAL_Y - TWICE_SWIPE_DISTANCE_THRESHOLD_PX)));
    assertFalse(glassGestureDetector.onTouchEvent(getSecondFingerActionMove(secondFingerEndX,
        SECOND_FINGER_INITIAL_Y - TWICE_SWIPE_DISTANCE_THRESHOLD_PX)));
    assertFalse(glassGestureDetector.onTouchEvent(getSecondFingerActionUp(secondFingerEndX,
        SECOND_FINGER_INITIAL_Y - TWICE_SWIPE_DISTANCE_THRESHOLD_PX)));
    assertTrue(glassGestureDetector.onTouchEvent(
        getActionUp(endX, INITIAL_Y - TWICE_SWIPE_DISTANCE_THRESHOLD_PX)));
    assertEquals(Gesture.TWO_FINGER_SWIPE_FORWARD, detectedGesture);

    // -120 degrees
    assertFalse(glassGestureDetector.onTouchEvent(getActionDown()));
    assertFalse(glassGestureDetector.onTouchEvent(getSecondFingerActionDown()));
    assertFalse(glassGestureDetector.onTouchEvent(
        getActionMove(endX, INITIAL_Y + TWICE_SWIPE_DISTANCE_THRESHOLD_PX)));
    assertFalse(glassGestureDetector.onTouchEvent(getSecondFingerActionMove(secondFingerEndX,
        SECOND_FINGER_INITIAL_Y + TWICE_SWIPE_DISTANCE_THRESHOLD_PX)));
    assertFalse(glassGestureDetector.onTouchEvent(getSecondFingerActionUp(secondFingerEndX,
        SECOND_FINGER_INITIAL_Y + TWICE_SWIPE_DISTANCE_THRESHOLD_PX)));
    assertTrue(glassGestureDetector.onTouchEvent(
        getActionUp(endX, INITIAL_Y + TWICE_SWIPE_DISTANCE_THRESHOLD_PX)));
    assertEquals(Gesture.TWO_FINGER_SWIPE_FORWARD, detectedGesture);
  }

  @Test
  public void testAngleSwipeBackward() {
    final float endX = INITIAL_X + (float) (TWICE_SWIPE_DISTANCE_THRESHOLD_PX / getAbsTanDegrees(
        ANGLE_60_DEGREES));

    // SWIPE_BACKWARD will be detected if movement angle is smaller or equal 60 degrees clockwise from X axis
    assertFalse(glassGestureDetector.onTouchEvent(getActionDown()));
    assertFalse(glassGestureDetector.onTouchEvent(
        getActionMove(endX, INITIAL_Y - TWICE_SWIPE_DISTANCE_THRESHOLD_PX)));
    assertTrue(glassGestureDetector.onTouchEvent(
        getActionUp(endX, INITIAL_Y - TWICE_SWIPE_DISTANCE_THRESHOLD_PX)));
    assertEquals(Gesture.SWIPE_BACKWARD, detectedGesture);

    // SWIPE_BACKWARD will be detected if movement angle is smaller or equal -60 degrees counterclockwise from X axis
    assertFalse(glassGestureDetector.onTouchEvent(getActionDown()));
    assertFalse(glassGestureDetector.onTouchEvent(
        getActionMove(endX, INITIAL_Y + TWICE_SWIPE_DISTANCE_THRESHOLD_PX)));
    glassGestureDetector.onTouchEvent(
        getActionUp(endX, INITIAL_Y + TWICE_SWIPE_DISTANCE_THRESHOLD_PX));
    assertEquals(Gesture.SWIPE_BACKWARD, detectedGesture);
  }

  @Test
  public void testAngleTwoFingerSwipeBackward() {
    final float endX = INITIAL_X + (float) (TWICE_SWIPE_DISTANCE_THRESHOLD_PX / getAbsTanDegrees(
        ANGLE_60_DEGREES));
    final float secondFingerEndX =
        SECOND_FINGER_INITIAL_X + (float) (TWICE_SWIPE_DISTANCE_THRESHOLD_PX / getAbsTanDegrees(
            ANGLE_60_DEGREES));

    // SWIPE_BACKWARD will be detected if movement angle is smaller or equal 60 degrees clockwise from X axis
    assertFalse(glassGestureDetector.onTouchEvent(getActionDown()));
    assertFalse(glassGestureDetector.onTouchEvent(getSecondFingerActionDown()));
    assertFalse(glassGestureDetector.onTouchEvent(
        getActionMove(endX, INITIAL_Y - TWICE_SWIPE_DISTANCE_THRESHOLD_PX)));
    assertFalse(glassGestureDetector.onTouchEvent(getSecondFingerActionMove(secondFingerEndX,
        SECOND_FINGER_INITIAL_Y - TWICE_SWIPE_DISTANCE_THRESHOLD_PX)));
    assertFalse(glassGestureDetector.onTouchEvent(getSecondFingerActionUp(secondFingerEndX,
        SECOND_FINGER_INITIAL_Y - TWICE_SWIPE_DISTANCE_THRESHOLD_PX)));
    assertTrue(glassGestureDetector.onTouchEvent(
        getActionUp(endX, INITIAL_Y - TWICE_SWIPE_DISTANCE_THRESHOLD_PX)));
    assertEquals(Gesture.TWO_FINGER_SWIPE_BACKWARD, detectedGesture);

    // SWIPE_BACKWARD will be detected if movement angle is smaller or equal -60 degrees counterclockwise from X axis
    assertFalse(glassGestureDetector.onTouchEvent(getActionDown()));
    assertFalse(glassGestureDetector.onTouchEvent(getSecondFingerActionDown()));
    assertFalse(glassGestureDetector.onTouchEvent(
        getActionMove(endX, INITIAL_Y + TWICE_SWIPE_DISTANCE_THRESHOLD_PX)));
    assertFalse(glassGestureDetector.onTouchEvent(getSecondFingerActionMove(secondFingerEndX,
        SECOND_FINGER_INITIAL_Y + TWICE_SWIPE_DISTANCE_THRESHOLD_PX)));
    assertFalse(glassGestureDetector.onTouchEvent(getSecondFingerActionUp(secondFingerEndX,
        SECOND_FINGER_INITIAL_Y + TWICE_SWIPE_DISTANCE_THRESHOLD_PX)));
    assertTrue(glassGestureDetector.onTouchEvent(
        getActionUp(endX, INITIAL_Y + TWICE_SWIPE_DISTANCE_THRESHOLD_PX)));
    assertEquals(Gesture.TWO_FINGER_SWIPE_BACKWARD, detectedGesture);
  }

  @Test
  public void testAngleSwipeUp() {
    // SWIPE_UP gesture will be detected if movement angle is greater than 60 degrees clockwise from X axis
    final float endX = INITIAL_X - (float) (TWICE_SWIPE_DISTANCE_THRESHOLD_PX / getAbsTanDegrees(
        ANGLE_60_DEGREES + ANGLE_EPSILON_DEGREES));
    assertFalse(glassGestureDetector.onTouchEvent(getActionDown()));
    assertFalse(glassGestureDetector.onTouchEvent(
        getActionMove(endX, INITIAL_Y - TWICE_SWIPE_DISTANCE_THRESHOLD_PX)));
    assertTrue(glassGestureDetector.onTouchEvent(
        getActionUp(endX, INITIAL_Y - TWICE_SWIPE_DISTANCE_THRESHOLD_PX)));
    assertEquals(Gesture.SWIPE_UP, detectedGesture);

    // SWIPE_UP gesture will be detected if movement angle is smaller than 120 degrees clockwise from X axis
    final float endX2 = INITIAL_X + (float) (TWICE_SWIPE_DISTANCE_THRESHOLD_PX / getAbsTanDegrees(
        ANGLE_120_DEGREES - ANGLE_EPSILON_DEGREES));
    assertFalse(glassGestureDetector.onTouchEvent(getActionDown()));
    assertFalse(glassGestureDetector.onTouchEvent(
        getActionMove(endX2, INITIAL_Y - TWICE_SWIPE_DISTANCE_THRESHOLD_PX)));
    assertTrue(glassGestureDetector.onTouchEvent(
        getActionUp(endX2, INITIAL_Y - TWICE_SWIPE_DISTANCE_THRESHOLD_PX)));
    assertEquals(Gesture.SWIPE_UP, detectedGesture);
  }

  @Test
  public void testAngleTwoFingerSwipeUp() {
    // SWIPE_UP gesture will be detected if movement angle is greater than 60 degrees clockwise from X axis
    final float endX = INITIAL_X - (float) (TWICE_SWIPE_DISTANCE_THRESHOLD_PX / getAbsTanDegrees(
        ANGLE_60_DEGREES + ANGLE_EPSILON_DEGREES));
    final float secondFingerEndX =
        SECOND_FINGER_INITIAL_X - (float) (TWICE_SWIPE_DISTANCE_THRESHOLD_PX / getAbsTanDegrees(
            ANGLE_60_DEGREES + ANGLE_EPSILON_DEGREES));
    assertFalse(glassGestureDetector.onTouchEvent(getActionDown()));
    assertFalse(glassGestureDetector.onTouchEvent(getSecondFingerActionDown()));
    assertFalse(glassGestureDetector.onTouchEvent(
        getActionMove(endX, INITIAL_Y - TWICE_SWIPE_DISTANCE_THRESHOLD_PX)));
    assertFalse(glassGestureDetector.onTouchEvent(getSecondFingerActionMove(secondFingerEndX,
        SECOND_FINGER_INITIAL_Y - TWICE_SWIPE_DISTANCE_THRESHOLD_PX)));
    assertFalse(glassGestureDetector.onTouchEvent(getSecondFingerActionUp(secondFingerEndX,
        SECOND_FINGER_INITIAL_Y - TWICE_SWIPE_DISTANCE_THRESHOLD_PX)));
    assertTrue(glassGestureDetector.onTouchEvent(
        getActionUp(endX, INITIAL_Y - TWICE_SWIPE_DISTANCE_THRESHOLD_PX)));
    assertEquals(Gesture.TWO_FINGER_SWIPE_UP, detectedGesture);

    // SWIPE_UP gesture will be detected if movement angle is smaller than 120 degrees clockwise from X axis
    final float endX2 = INITIAL_X + (float) (TWICE_SWIPE_DISTANCE_THRESHOLD_PX / getAbsTanDegrees(
        ANGLE_120_DEGREES - ANGLE_EPSILON_DEGREES));
    final float secondFingerEndX2 =
        SECOND_FINGER_INITIAL_X + (float) (TWICE_SWIPE_DISTANCE_THRESHOLD_PX / getAbsTanDegrees(
            ANGLE_120_DEGREES - ANGLE_EPSILON_DEGREES));
    assertFalse(glassGestureDetector.onTouchEvent(getActionDown()));
    assertFalse(glassGestureDetector.onTouchEvent(getSecondFingerActionDown()));
    assertFalse(glassGestureDetector.onTouchEvent(
        getActionMove(endX2, INITIAL_Y - TWICE_SWIPE_DISTANCE_THRESHOLD_PX)));
    assertFalse(glassGestureDetector.onTouchEvent(getSecondFingerActionMove(secondFingerEndX2,
        SECOND_FINGER_INITIAL_Y - TWICE_SWIPE_DISTANCE_THRESHOLD_PX)));
    assertFalse(glassGestureDetector.onTouchEvent(getSecondFingerActionUp(secondFingerEndX2,
        SECOND_FINGER_INITIAL_Y - TWICE_SWIPE_DISTANCE_THRESHOLD_PX)));
    assertTrue(glassGestureDetector.onTouchEvent(
        getActionUp(endX2, INITIAL_Y - TWICE_SWIPE_DISTANCE_THRESHOLD_PX)));
    assertEquals(Gesture.TWO_FINGER_SWIPE_UP, detectedGesture);
  }

  @Test
  public void testAngleSwipeDown() {
    // SWIPE_DOWN will be detected if movement angle is greater than 60 degrees counterclockwise from X axis
    final float endX = INITIAL_X - (float) (TWICE_SWIPE_DISTANCE_THRESHOLD_PX / getAbsTanDegrees(
        ANGLE_60_DEGREES + ANGLE_EPSILON_DEGREES));
    assertFalse(glassGestureDetector.onTouchEvent(getActionDown()));
    assertFalse(glassGestureDetector.onTouchEvent(
        getActionMove(endX, INITIAL_Y + TWICE_SWIPE_DISTANCE_THRESHOLD_PX)));
    assertTrue(glassGestureDetector.onTouchEvent(
        getActionUp(endX, INITIAL_Y + TWICE_SWIPE_DISTANCE_THRESHOLD_PX)));
    assertEquals(Gesture.SWIPE_DOWN, detectedGesture);

    // SWIPE_DOWN will be detected if movement angle is smaller than 120 degrees counterclockwise from X axis
    final float endX2 = INITIAL_X + (float) (TWICE_SWIPE_DISTANCE_THRESHOLD_PX / getAbsTanDegrees(
        ANGLE_120_DEGREES - ANGLE_EPSILON_DEGREES));
    assertFalse(glassGestureDetector.onTouchEvent(getActionDown()));
    assertFalse(glassGestureDetector.onTouchEvent(
        getActionMove(endX2, INITIAL_Y + TWICE_SWIPE_DISTANCE_THRESHOLD_PX)));
    assertTrue(glassGestureDetector.onTouchEvent(
        getActionUp(endX2, INITIAL_Y + TWICE_SWIPE_DISTANCE_THRESHOLD_PX)));
    assertEquals(Gesture.SWIPE_DOWN, detectedGesture);
  }

  @Test
  public void testAngleTwoFingerSwipeDown() {
    // SWIPE_DOWN will be detected if movement angle is greater than 60 degrees counterclockwise from X axis
    final float endX = INITIAL_X - (float) (TWICE_SWIPE_DISTANCE_THRESHOLD_PX / getAbsTanDegrees(
        ANGLE_60_DEGREES + ANGLE_EPSILON_DEGREES));
    final float secondFingerEndX =
        SECOND_FINGER_INITIAL_X - (float) (TWICE_SWIPE_DISTANCE_THRESHOLD_PX / getAbsTanDegrees(
            ANGLE_60_DEGREES + ANGLE_EPSILON_DEGREES));
    assertFalse(glassGestureDetector.onTouchEvent(getActionDown()));
    assertFalse(glassGestureDetector.onTouchEvent(getSecondFingerActionDown()));
    assertFalse(glassGestureDetector.onTouchEvent(
        getActionMove(endX, INITIAL_Y + TWICE_SWIPE_DISTANCE_THRESHOLD_PX)));
    assertFalse(glassGestureDetector.onTouchEvent(getSecondFingerActionMove(secondFingerEndX,
        SECOND_FINGER_INITIAL_Y + TWICE_SWIPE_DISTANCE_THRESHOLD_PX)));
    assertFalse(glassGestureDetector.onTouchEvent(getSecondFingerActionUp(secondFingerEndX,
        SECOND_FINGER_INITIAL_Y + TWICE_SWIPE_DISTANCE_THRESHOLD_PX)));
    assertTrue(glassGestureDetector.onTouchEvent(
        getActionUp(endX, INITIAL_Y + TWICE_SWIPE_DISTANCE_THRESHOLD_PX)));
    assertEquals(Gesture.TWO_FINGER_SWIPE_DOWN, detectedGesture);

    // SWIPE_DOWN will be detected if movement angle is smaller than 120 degrees counterclockwise from X axis
    final float endX2 = INITIAL_X + (float) (TWICE_SWIPE_DISTANCE_THRESHOLD_PX / getAbsTanDegrees(
        ANGLE_120_DEGREES - ANGLE_EPSILON_DEGREES));
    final float secondFingerEndX2 =
        SECOND_FINGER_INITIAL_X + (float) (TWICE_SWIPE_DISTANCE_THRESHOLD_PX / getAbsTanDegrees(
            ANGLE_120_DEGREES - ANGLE_EPSILON_DEGREES));
    assertFalse(glassGestureDetector.onTouchEvent(getActionDown()));
    assertFalse(glassGestureDetector.onTouchEvent(getSecondFingerActionDown()));
    assertFalse(glassGestureDetector.onTouchEvent(
        getActionMove(endX2, INITIAL_Y + TWICE_SWIPE_DISTANCE_THRESHOLD_PX)));
    assertFalse(glassGestureDetector.onTouchEvent(
        getSecondFingerActionMove(secondFingerEndX2,
            SECOND_FINGER_INITIAL_Y + TWICE_SWIPE_DISTANCE_THRESHOLD_PX)));
    assertFalse(glassGestureDetector.onTouchEvent(getSecondFingerActionUp(secondFingerEndX2,
        SECOND_FINGER_INITIAL_Y + TWICE_SWIPE_DISTANCE_THRESHOLD_PX)));
    assertTrue(glassGestureDetector.onTouchEvent(
        getActionUp(endX2, INITIAL_Y + TWICE_SWIPE_DISTANCE_THRESHOLD_PX)));
    assertEquals(Gesture.TWO_FINGER_SWIPE_DOWN, detectedGesture);
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

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
      isScrolling = true;
      scrollingDistanceX = distanceX;
      scrollingDistanceY = distanceY;
      return false;
    }

    @Override
    public void onTouchEnded() {
      isTouchEnded = true;
    }
  }
}