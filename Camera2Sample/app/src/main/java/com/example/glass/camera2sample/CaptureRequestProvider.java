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

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CaptureRequest;
import android.util.Log;
import android.view.Surface;
import java.util.List;

/**
 * Builds and provides {@link CaptureRequest} object.
 */
public class CaptureRequestProvider {

  private static final String TAG = CaptureRequestProvider.class.getSimpleName();

  /**
   * {@link CameraDevice} used for creating the capture request.
   */
  private final CameraDevice cameraDevice;

  /**
   * Creates object using {@link CameraDevice}.
   */
  public CaptureRequestProvider(CameraDevice cameraDevice) {
    this.cameraDevice = cameraDevice;
  }

  /**
   * Returns {@link CaptureRequest} built with the templateType. Adds targets as {@link
   * List<Surface>}.
   */
  public CaptureRequest getCaptureRequest(int templateType, List<Surface> surfaces) {
    Log.d(TAG, "Creating capture request for the template type: " + templateType);
    try {
      final CaptureRequest.Builder cameraRequestBuilder = cameraDevice
          .createCaptureRequest(templateType);
      for (Surface surface : surfaces) {
        cameraRequestBuilder.addTarget(surface);
      }
      return cameraRequestBuilder.build();
    } catch (CameraAccessException e) {
      Log.e(TAG, "Creating capture request failed", e);
      return null;
    }
  }
}
