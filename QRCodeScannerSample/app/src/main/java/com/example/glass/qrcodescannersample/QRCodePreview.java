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

import android.view.TextureView;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.camera.core.Preview;
import androidx.camera.core.Preview.OnPreviewOutputUpdateListener;
import androidx.camera.core.Preview.PreviewOutput;
import androidx.camera.core.PreviewConfig;
import androidx.camera.core.UseCase;

/**
 * Builds and provides {@link Preview} object to bind the camera with.
 */
public class QRCodePreview implements OnPreviewOutputUpdateListener {

  private final PreviewConfig previewConfig;
  private final TextureView textureView;

  /**
   * Creates {@link QRCodeImageAnalysis} object with {@link PreviewConfig} and {@link TextureView}.
   */
  public QRCodePreview(PreviewConfig previewConfig, TextureView textureView) {
    this.previewConfig = previewConfig;
    this.textureView = textureView;
  }

  /**
   * Sets {@link OnPreviewOutputUpdateListener} and returns created {@link Preview} object.
   */
  public UseCase getUseCase() {
    final Preview preview = new Preview(previewConfig);
    preview.setOnPreviewOutputUpdateListener(this);
    return preview;
  }

  /**
   * Updates given {@link TextureView} with the {@link android.graphics.SurfaceTexture} from {@link
   * PreviewOutput} object. Removing and adding {@link TextureView} to the {@link ViewGroup} is
   * necessary, when camera permission has not been granted yet.
   */
  @Override
  public void onUpdated(@NonNull PreviewOutput output) {
    final ViewGroup viewGroup = (ViewGroup) textureView.getParent();
    viewGroup.removeView(textureView);
    viewGroup.addView(textureView, 0);
    textureView.setSurfaceTexture(output.getSurfaceTexture());
  }
}
