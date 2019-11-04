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

import android.graphics.ImageFormat;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.ImageReader;
import android.media.ImageReader.OnImageAvailableListener;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Size;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

/**
 * Helper class for the {@link ImageReader}.
 */
public class ImageReaderProvider {

  private static final String TAG = ImageReaderProvider.class.getSimpleName();

  /**
   * Format in which images will be saved.
   */
  private static final int IMAGE_FORMAT = ImageFormat.JPEG;

  /**
   * Maximum amount of images for simultaneous access from ImageReader.
   */
  private static final int MAX_IMAGES = 1;

  /**
   * An {@link ImageReader} that handles still image capture.
   */
  @Nullable
  private ImageReader imageReader;

  /**
   * Creates {@link ImageReader} instance.
   */
  public ImageReaderProvider(StreamConfigurationMap streamConfigurationMap) {
    // For still image captures, we use the largest available size.
    final Size pictureSize = Collections.max(
        Arrays.asList(streamConfigurationMap.getOutputSizes(ImageFormat.JPEG)),
        new CompareSizesByArea());
    imageReader = ImageReader.newInstance(pictureSize.getWidth(), pictureSize.getHeight(),
        IMAGE_FORMAT, MAX_IMAGES);
  }

  /**
   * Returns {@link ImageReader} object.
   */
  @Nullable
  public ImageReader getImageReader() {
    return imageReader;
  }

  /**
   * Closes {@link ImageReader}.
   */
  public void closeImageReader() {
    Log.d(TAG, "Closing image reader");
    if (imageReader != null) {
      imageReader.close();
      imageReader = null;
      return;
    }
    Log.d(TAG, "Image reader is null");
  }

  /**
   * Sets {@link OnImageAvailableListener} on the {@link ImageReader} object.
   */
  public void setOnImageAvailableListener(OnImageAvailableListener onImageAvailableListener,
      Handler handler) {
    if (imageReader != null) {
      imageReader.setOnImageAvailableListener(onImageAvailableListener, handler);
    }
  }

  /**
   * Compares two {@code Size} objects based on their areas.
   */
  static class CompareSizesByArea implements Comparator<Size> {

    @Override
    public int compare(Size lhs, Size rhs) {
      // We cast here to ensure the multiplications won't overflow
      return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
          (long) rhs.getWidth() * rhs.getHeight());
    }
  }
}
