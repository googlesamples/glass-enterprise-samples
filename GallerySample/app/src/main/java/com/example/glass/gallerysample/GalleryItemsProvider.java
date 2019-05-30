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

package com.example.glass.gallerysample;

import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.MediaStore;
import android.provider.MediaStore.Files.FileColumns;
import android.provider.MediaStore.Images.Media;
import android.support.v4.content.CursorLoader;
import android.util.Log;

/**
 * Provides {@link GalleryItem}s and calls methods from {@link GalleryItemsListener} depends on the
 * actual gallery items list state.
 */
public class GalleryItemsProvider {

  private static final String TAG = GalleryItemsProvider.class.getSimpleName();
  private static final String VOLUME_NAME = "external";
  private static final String SORT_ORDER = " DESC";
  private static final int THUMBNAIL_WIDTH_PX = 400;
  private static final int THUMBNAIL_HEIGHT_PX = 400;

  private final Context context;
  private final GalleryItemsListener galleryItemsListener;
  private final Handler handler;

  GalleryItemsProvider(Context context, HandlerThread backgroundHandlerThread,
      GalleryItemsListener galleryItemsListener) {
    this.context = context;
    this.galleryItemsListener = galleryItemsListener;
    this.handler = new Handler(backgroundHandlerThread.getLooper());
  }

  /**
   * Loads gallery items and calls {@link GalleryItemsListener#onItemFound(GalleryItem)} if specific
   * gallery item has been found or {@link GalleryItemsListener#onEmptyList()} if none of the items
   * have been found.
   */
  public void loadGalleryItems() {
    handler.post(new Runnable() {
      @Override
      public void run() {
        String[] projection = {
            FileColumns._ID,
            FileColumns.DATA,
            FileColumns.MEDIA_TYPE,
            FileColumns.MIME_TYPE,
            FileColumns.TITLE
        };

        // Obtains content URI for the external drive.
        Uri queryUri = MediaStore.Files.getContentUri(VOLUME_NAME);

        // Creates selection for the images only.
        String selection = FileColumns.MEDIA_TYPE + "="
            + FileColumns.MEDIA_TYPE_IMAGE;

        CursorLoader cursorLoader = new CursorLoader(
            context,
            queryUri,
            projection,
            selection,
            null,
            FileColumns.DATE_ADDED + SORT_ORDER);

        Cursor cursor = cursorLoader.loadInBackground();

        if (cursor != null) {
          if (cursor.getCount() == 0) {
            galleryItemsListener.onEmptyList();
            cursor.close();
            return;
          }

          // Iterate over the cursor to notify listener about the found gallery item.
          while (cursor.moveToNext()) {
            final int columnIndexName = cursor.getColumnIndex(FileColumns.TITLE);
            final int columnIndexPath = cursor.getColumnIndex(Media.DATA);
            final String name = cursor.getString(columnIndexName);
            final String path = cursor.getString(columnIndexPath);

            // Creates thumbnail from the bitmap.
            final Drawable drawable = new BitmapDrawable(context.getResources(),
                ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(path), THUMBNAIL_WIDTH_PX,
                    THUMBNAIL_HEIGHT_PX));
            galleryItemsListener
                .onItemFound(new GalleryItem(name, path, GalleryItem.Type.IMAGE, drawable));
          }
          cursor.close();
        }
      }
    });
  }

  /**
   * Removes gallery item using given path.
   */
  public static void deleteGalleryItem(final Context context, String path) {
    try {
      MediaScannerConnection.scanFile(context, new String[]{path},
          null, new MediaScannerConnection.OnScanCompletedListener() {
            public void onScanCompleted(String path, Uri uri) {
              context.getContentResolver()
                  .delete(uri, null, null);
            }
          });
    } catch (Exception e) {
      Log.e(TAG, "Deleting gallery item failed", e);
    }
  }
}
