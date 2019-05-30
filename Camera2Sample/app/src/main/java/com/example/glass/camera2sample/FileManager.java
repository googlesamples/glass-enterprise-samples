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
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import java.io.File;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Provides functionality necessary to store data captured by the camera.
 */
public class FileManager {

  private static final String TAG = FileManager.class.getSimpleName();
  private static final String DATE_FORMAT_PATTERN = "yyyyMMddHHmmss";
  private static final String STORAGE_DIRECTORY_CHILD = "";
  private static final String VIDEO_FILE_NAME_BEGINNING = "Video";
  private static final String VIDEO_FILE_NAME_EXTENSION = ".mp4";

  /**
   * Creates new file in the Movies directory on the device.
   */
  public static File getOutputVideoFile() {
    Log.d(TAG, "Creating output video file");
    final File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
        Environment.DIRECTORY_MOVIES), STORAGE_DIRECTORY_CHILD);
    final String timeStamp = new SimpleDateFormat(DATE_FORMAT_PATTERN, Locale.US)
        .format(new Date());
    return new File(mediaStorageDir.getPath() + File.separator +
        VIDEO_FILE_NAME_BEGINNING + timeStamp + VIDEO_FILE_NAME_EXTENSION);
  }

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

  /**
   * Refreshes file indexing for the {@link MediaStore}. It is necessary to see the recorded video
   * in the gallery, without rebooting the device.
   */
  public static void refreshFileIndexing(Context context, File file) {
    Log.d(TAG, "Refreshing file indexing");
    MediaScannerConnection.scanFile(context, new String[]{file.getAbsolutePath()}, null, null);
  }
}
