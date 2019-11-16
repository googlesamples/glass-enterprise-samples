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

import android.util.Log;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageAnalysis.Analyzer;
import androidx.camera.core.ImageAnalysisConfig;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.UseCase;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import java.nio.ByteBuffer;
import java.util.concurrent.Executor;

/**
 * Builds and provides {@link ImageAnalysis} object to bind the camera with.
 */
public class QRCodeImageAnalysis implements Analyzer {

  private static final String TAG = QRCodeImageAnalysis.class.getSimpleName();
  private final ImageAnalysisConfig imageAnalysisConfig;
  private final Executor executor;
  private final QrCodeAnalysisCallback qrCodeAnalysisCallback;

  /**
   * Creates {@link QRCodeImageAnalysis} object with {@link ImageAnalysisConfig} and {@link
   * QrCodeAnalysisCallback} callback.
   */
  public QRCodeImageAnalysis(ImageAnalysisConfig imageAnalysisConfig, Executor executor,
      QrCodeAnalysisCallback qrCodeAnalysisCallback) {
    this.imageAnalysisConfig = imageAnalysisConfig;
    this.executor = executor;
    this.qrCodeAnalysisCallback = qrCodeAnalysisCallback;
  }

  /**
   * Sets {@link Analyzer} with the given thread and returns created {@link ImageAnalysis} object.
   */
  public UseCase getUseCase() {
    final ImageAnalysis imageAnalysis = new ImageAnalysis(imageAnalysisConfig);
    imageAnalysis.setAnalyzer(executor, this);
    return imageAnalysis;
  }

  /**
   * Performs an analysis of the image, searching for the QR code, using the ZXing library.
   */
  @Override
  public void analyze(ImageProxy image, int rotationDegrees) {
    final ByteBuffer buffer = image.getPlanes()[0].getBuffer();
    final byte[] imageBytes = new byte[buffer.remaining()];
    buffer.get(imageBytes);
    final int width = image.getWidth();
    final int height = image.getHeight();
    final PlanarYUVLuminanceSource source =
        new PlanarYUVLuminanceSource(imageBytes, width, height, 0, 0, width, height, false);
    final BinaryBitmap zxingBinaryBitmap = new BinaryBitmap(new HybridBinarizer(source));
    try {
      final Result decodedBarcode = new QRCodeReader().decode(zxingBinaryBitmap);
      qrCodeAnalysisCallback.onQrCodeDetected(decodedBarcode.getText());
    } catch (NotFoundException | ChecksumException | FormatException e) {
      Log.e(TAG, "QR Code decoding error", e);
    }
  }

  /**
   * Callback interface for the communication with the {@link CameraActivity}.
   */
  interface QrCodeAnalysisCallback {

    void onQrCodeDetected(String result);
  }
}
