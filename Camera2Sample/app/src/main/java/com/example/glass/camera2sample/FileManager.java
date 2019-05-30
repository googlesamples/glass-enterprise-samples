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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.media.ImageReader;
import android.provider.MediaStore;
import android.util.Log;
import java.nio.ByteBuffer;

/**
 * Provides functionality necessary to store data captured by the camera.
 */
public class FileManager {

  private static final String TAG = FileManager.class.getSimpleName();

  /**
   * Stores given image from the {@link ImageReader} object, using {@link MediaStore}.
   */
  public static void saveImage(final Context context, final ImageReader imageReader) {
    Log.d(TAG, "Saving image");
    final Image image = imageReader.acquireNextImage();
    final ByteBuffer buffer = image.getPlanes()[0].getBuffer();
    final byte[] bytes = new byte[buffer.remaining()];
    buffer.get(bytes);
    final Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    if (bitmap != null) {
      MediaStore.Images.Media
          .insertImage(context.getContentResolver(), bitmap, null, null);
    } else {
      Log.d(TAG, "Bitmap is null");
    }
    image.close();
  }
}
