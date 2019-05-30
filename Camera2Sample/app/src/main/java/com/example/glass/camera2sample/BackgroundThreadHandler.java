/*
 * Copyright 2019 The Android Open Source Project
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

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

/**
 * Helper class responsible for handling the background thread.
 */
public class BackgroundThreadHandler {

  private static final String TAG = BackgroundThreadHandler.class.getSimpleName();

  private final String threadName;
  private HandlerThread handlerThread;
  private Handler handler;

  /**
   * Constructor of this class needs a background thread name to be passed by an argument.
   *
   * @param threadName of the background thread to handle.
   */
  public BackgroundThreadHandler(String threadName) {
    this.threadName = threadName;
  }

  /**
   * Starts a background thread and its {@link Handler}.
   */
  public void startBackgroundThread() {
    Log.d(TAG, "Starting thread " + threadName);
    handlerThread = new HandlerThread(threadName);
    handlerThread.start();
    handler = new Handler(handlerThread.getLooper());
  }

  /**
   * Stops the background thread and its {@link Handler}.
   */
  public void stopBackgroundThread() {
    Log.d(TAG, "Stopping thread " + threadName);
    handlerThread.quitSafely();
    try {
      handlerThread.join();
      handlerThread = null;
      handler = null;
    } catch (InterruptedException e) {
      Log.e(TAG, "Stopping thread " + threadName + " failed", e);
    }
  }

  /**
   * Returns {@link Handler} using the background thread.
   */
  public Handler getHandler() {
    return handler;
  }
}
