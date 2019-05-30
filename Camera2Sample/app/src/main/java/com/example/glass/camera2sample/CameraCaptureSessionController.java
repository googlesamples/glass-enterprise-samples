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
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCaptureSession.CaptureCallback;
import android.hardware.camera2.CameraDevice;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Surface;
import java.util.Collections;
import java.util.List;

/**
 * Helper class providing and managing the {@link CameraCaptureSession}.
 */
public class CameraCaptureSessionController {

  private static final String TAG = CameraCaptureSessionController.class.getSimpleName();

  /**
   * A {@link CameraCaptureSession } for the camera preview and to take pictures.
   */
  @Nullable
  private CameraCaptureSession cameraCaptureSession;

  /**
   * Provides {@link android.hardware.camera2.CaptureRequest} for the {@link CameraCaptureSession}.
   */
  @Nullable
  private CaptureRequestProvider captureRequestProvider;

  /**
   * Creates preview session using template type and {@link List<Surface>} on which preview should
   * be shown.
   */
  public void createPreviewSession(int templateType, List<Surface> surfaces) {
    Log.d(TAG, "Creating session for the template: " + templateType);
    try {
      if (cameraCaptureSession != null && captureRequestProvider != null) {
        cameraCaptureSession
            .setRepeatingRequest(captureRequestProvider.getCaptureRequest(templateType, surfaces),
                null, null);
      }
    } catch (CameraAccessException e) {
      Log.e(TAG, "Creating session failed", e);
    }
  }

  /**
   * Captures picture and calls method on {@link CaptureCallback} to notify about this.
   */
  public void captureStillPicture(Surface surface, CaptureCallback captureCallback) {
    Log.d(TAG, "Capturing picture");
    if (cameraCaptureSession == null) {
      Log.i(TAG, "Session is null, picture won't be taken.");
      return;
    }
    if (captureRequestProvider == null) {
      Log.i(TAG, "RequestProvider is null, picture won't be taken.");
      return;
    }
    try {
      cameraCaptureSession.stopRepeating();
      cameraCaptureSession.abortCaptures();
      cameraCaptureSession
          .capture(captureRequestProvider.getCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE,
              Collections.singletonList(surface)), captureCallback, null);
    } catch (CameraAccessException e) {
      Log.e(TAG, "Capturing picture failed", e);
    }
  }

  /**
   * Sets {@link CameraCaptureSession}.
   */
  public void setSession(CameraCaptureSession cameraCaptureSession) {
    this.cameraCaptureSession = cameraCaptureSession;
  }

  /**
   * Closes {@link CameraCaptureSession}.
   */
  public void closeSession() {
    Log.d(TAG, "Closing session");
    if (cameraCaptureSession != null) {
      cameraCaptureSession.close();
      cameraCaptureSession = null;
      return;
    }
    Log.d(TAG, "Camera capture session is null");
  }

  /**
   * Sets {@link CameraDevice} for the {@link CaptureRequestProvider}.
   */
  public void setCameraDevice(CameraDevice cameraDevice) {
    captureRequestProvider = new CaptureRequestProvider(cameraDevice);
  }
}
