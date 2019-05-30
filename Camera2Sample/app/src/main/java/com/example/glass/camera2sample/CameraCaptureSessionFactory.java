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

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession.StateCallback;
import android.hardware.camera2.CameraDevice;
import android.util.Log;
import android.view.Surface;
import java.util.List;

/**
 * Provides functionality of creating camera capture session.
 */
public class CameraCaptureSessionFactory {

  private static final String TAG = CameraCaptureSessionFactory.class.getSimpleName();

  /**
   * {@link CameraDevice} used for creating the capture session.
   */
  private final CameraDevice cameraDevice;

  /**
   * Creates {@link CameraCaptureSessionFactory} using {@link CameraDevice} object.
   */
  public CameraCaptureSessionFactory(CameraDevice cameraDevice) {
    this.cameraDevice = cameraDevice;
  }

  /**
   * Creates camera capture session on the given {@link List<Surface>} of surfaces. Returns created
   * {@link android.hardware.camera2.CameraCaptureSession} in stateCallback.
   */
  public void createCaptureSession(List<Surface> surfaceList, final StateCallback stateCallback) {
    Log.d(TAG, "Creating capture session");
    try {
      cameraDevice.createCaptureSession(surfaceList, stateCallback, null);
    } catch (CameraAccessException e) {
      Log.e(TAG, "Creating capture session failed", e);
    }
  }
}
