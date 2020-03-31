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

public class MotionEventGenerator {

  private static final int INITIAL_X = 200;
  private static final int INITIAL_Y = 200;
  private static final int DOWN_TIME = 10;
  private static final int DOWN_EVENT_TIME = 100;
  private static final int MOVE_EVENT_TIME = 200;
  private static final int UP_EVENT_TIME = 300;
  private static final int META_STATE = 0;

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
}
