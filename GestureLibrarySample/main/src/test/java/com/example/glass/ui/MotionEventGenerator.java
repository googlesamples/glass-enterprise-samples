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

import android.view.MotionEvent;
import android.view.MotionEvent.PointerCoords;
import android.view.MotionEvent.PointerProperties;

import org.robolectric.Shadows;
import org.robolectric.shadows.ShadowMotionEvent;

public class MotionEventGenerator {

  private static final int DOWN_EVENT_TIME = 100;
  private static final int MOVE_EVENT_TIME = 200;
  private static final int UP_EVENT_TIME = 300;
  private static final int TWO_FINGER_POINTER_COUNT = 2;
  private static final int FIRST_FINGER_POINTER_ID = 0;
  private static final int SECOND_FINGER_POINTER_ID = 1;
  private static final int SECOND_FINGER_SHIFT_PX = 100;
  static final int INITIAL_X = 200;
  static final int INITIAL_Y = 200;
  static final int SECOND_FINGER_INITIAL_X = INITIAL_X + SECOND_FINGER_SHIFT_PX;
  static final int SECOND_FINGER_INITIAL_Y = INITIAL_Y;
  static final int DOWN_TIME = 10;
  static final int META_STATE = 0;

  public static MotionEvent getActionDown() {
    return MotionEvent
        .obtain(DOWN_TIME, DOWN_EVENT_TIME, MotionEvent.ACTION_DOWN, INITIAL_X, INITIAL_Y,
            META_STATE);
  }

  public static MotionEvent getActionMove(float xPosition, float yPosition) {
    return MotionEvent
        .obtain(DOWN_TIME, MOVE_EVENT_TIME, MotionEvent.ACTION_MOVE, xPosition, yPosition,
            META_STATE);
  }

  public static MotionEvent getActionUp(float xPosition, float yPosition) {
    return MotionEvent
        .obtain(DOWN_TIME, UP_EVENT_TIME, MotionEvent.ACTION_UP, xPosition, yPosition, META_STATE);
  }

  public static MotionEvent getActionCancel() {
    return MotionEvent
        .obtain(DOWN_TIME, MOVE_EVENT_TIME, MotionEvent.ACTION_CANCEL, INITIAL_X, INITIAL_Y,
            META_STATE);
  }

  public static MotionEvent getSecondFingerActionDown() {
    final MotionEvent motionEvent = MotionEvent
        .obtain(DOWN_TIME, DOWN_EVENT_TIME, MotionEvent.ACTION_POINTER_DOWN,
            TWO_FINGER_POINTER_COUNT, getPointerProperties(),
            getPointerCoords(SECOND_FINGER_INITIAL_X, SECOND_FINGER_INITIAL_Y),
            META_STATE, 0, 0, 0, 0, 0, 0, 0);
    setMotionEventParameters(motionEvent, SECOND_FINGER_INITIAL_X, SECOND_FINGER_INITIAL_Y);
    return motionEvent;
  }

  public static MotionEvent getSecondFingerActionMove(float xPosition, float yPosition) {
    final MotionEvent motionEvent = MotionEvent
        .obtain(DOWN_TIME, DOWN_EVENT_TIME, MotionEvent.ACTION_MOVE, TWO_FINGER_POINTER_COUNT,
            getPointerProperties(), getPointerCoords(xPosition, yPosition), META_STATE, 0, 0, 0, 0,
            0, 0, 0);
    setMotionEventParameters(motionEvent, xPosition, yPosition);
    return motionEvent;
  }

  public static MotionEvent getSecondFingerActionUp(float xPosition, float yPosition) {
    final MotionEvent motionEvent = MotionEvent
        .obtain(DOWN_TIME, DOWN_EVENT_TIME, MotionEvent.ACTION_POINTER_UP, TWO_FINGER_POINTER_COUNT,
            getPointerProperties(), getPointerCoords(xPosition, yPosition), META_STATE, 0, 0, 0, 0,
            0, 0, 0);
    setMotionEventParameters(motionEvent, xPosition, yPosition);
    return motionEvent;
  }

  private static PointerCoords[] getPointerCoords(float xPosition, float yPosition) {
    final PointerCoords[] pointerCoords = new MotionEvent.PointerCoords[TWO_FINGER_POINTER_COUNT];
    pointerCoords[0] = new MotionEvent.PointerCoords();
    pointerCoords[0].x = xPosition - SECOND_FINGER_SHIFT_PX;
    pointerCoords[0].y = yPosition;
    pointerCoords[1] = new MotionEvent.PointerCoords();
    pointerCoords[1].x = xPosition;
    pointerCoords[1].y = yPosition;
    return pointerCoords;
  }

  private static PointerProperties[] getPointerProperties() {
    final PointerProperties[] pointerProperties = new MotionEvent.PointerProperties[TWO_FINGER_POINTER_COUNT];
    pointerProperties[0] = new MotionEvent.PointerProperties();
    pointerProperties[0].id = FIRST_FINGER_POINTER_ID;
    pointerProperties[1] = new MotionEvent.PointerProperties();
    pointerProperties[1].id = SECOND_FINGER_POINTER_ID;
    return pointerProperties;
  }

  private static void setMotionEventParameters(MotionEvent motionEvent, float xPosition,
                                               float yPosition) {
    final ShadowMotionEvent shadowMotionEvent = Shadows.shadowOf(motionEvent);
    shadowMotionEvent.setPointerIndex(SECOND_FINGER_POINTER_ID);
    shadowMotionEvent.setPointerIds(FIRST_FINGER_POINTER_ID, SECOND_FINGER_POINTER_ID);
    shadowMotionEvent.setPointer2(xPosition, yPosition);
  }
}
