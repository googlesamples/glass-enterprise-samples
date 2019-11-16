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

package com.example.glass.qrcodescannersample;

import android.util.Size;
import androidx.camera.core.ImageAnalysis.ImageReaderMode;
import androidx.camera.core.ImageAnalysisConfig;
import androidx.camera.core.PreviewConfig;

/**
 * Provides appropriate {@link androidx.camera.core.UseCaseConfig} objects.
 */
public class CameraConfigProvider {

  /**
   * Returns {@link PreviewConfig} object to create the {@link androidx.camera.core.Preview}
   * instance. It is important to build {@link PreviewConfig} object with the appropriate display
   * size.
   */
  public static PreviewConfig getPreviewConfig(Size displaySize) {
    return new PreviewConfig.Builder()
        .setTargetResolution(displaySize)
        .build();
  }

  /**
   * Returns {@link ImageAnalysisConfig} object to create the {@link androidx.camera.core.ImageAnalysis}
   * instance.
   */
  public static ImageAnalysisConfig getImageAnalysisConfig() {
    return new ImageAnalysisConfig.Builder()
        .setImageReaderMode(ImageReaderMode.ACQUIRE_LATEST_IMAGE)
        .build();
  }
}
