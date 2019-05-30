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

import android.Manifest.permission;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import com.example.glass.gallerysample.GalleryFragment.OnGalleryItemSelectedListener;
import com.example.glass.gallerysample.GalleryItemFragment.OnGalleryItemDeletedListener;

/**
 * Main activity of the application. It checks for necessary permissions and adds {@link
 * GalleryFragment} to the container.
 */
public class MainActivity extends BaseActivity implements OnGalleryItemSelectedListener,
    OnGalleryItemDeletedListener {

  /**
   * Request code for the gallery permissions. This value doesn't have any special meaning.
   */
  private static final int REQUEST_PERMISSION_CODE = 200;

  /**
   * String array of necessary gallery permissions.
   */
  private String[] permissions = {permission.READ_EXTERNAL_STORAGE,
      permission.WRITE_EXTERNAL_STORAGE};

  private GalleryItem galleryItem;
  private GalleryFragment galleryFragment;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    for (String permission : permissions) {
      if (ContextCompat
          .checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
        requestPermissions(permissions, REQUEST_PERMISSION_CODE);
        return;
      }
    }
    initializeGalleryFragment();
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    if (requestCode == REQUEST_PERMISSION_CODE) {
      for (int result : grantResults) {
        if (result != PackageManager.PERMISSION_GRANTED) {
          finish();
          return;
        }
      }
      initializeGalleryFragment();
    } else {
      super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
  }

  @Override
  public void onAttachFragment(Fragment fragment) {
    super.onAttachFragment(fragment);
    if (fragment instanceof GalleryFragment) {
      galleryFragment = (GalleryFragment) fragment;
      galleryFragment.setOnGalleryItemSelectedListener(this);
    } else if (fragment instanceof GalleryItemFragment) {
      final GalleryItemFragment galleryItemFragment = (GalleryItemFragment) fragment;
      galleryItemFragment.setOnGalleryItemDeletedListener(this);
    }
  }

  @Override
  public void onGalleryItemSelected(GalleryItem galleryItem) {
    this.galleryItem = galleryItem;
    replaceFragment(GalleryItemFragment.newInstance(galleryItem), true);
  }

  @Override
  public void onGalleryItemDeleted(String filePath) {
    if (galleryItem.getPath().equals(filePath)) {
      galleryFragment.onGalleryItemDeleted(galleryItem);
    }
    popBackStack();
  }

  private void initializeGalleryFragment() {
    replaceFragment(new GalleryFragment(), false);
  }
}
