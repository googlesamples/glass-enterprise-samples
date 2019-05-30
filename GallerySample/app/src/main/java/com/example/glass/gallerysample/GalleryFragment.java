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
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.glass.ui.GlassGestureDetector;
import com.example.glass.ui.GlassGestureDetector.Gesture;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Shows horizontal scrolling list of the stored photos and videos or information about the empty
 * gallery.
 */
public class GalleryFragment extends Fragment implements GlassGestureDetector.OnGestureListener {


  /**
   * Request code for the gallery permissions. This value doesn't have any special meaning.
   */
  private static final int REQUEST_PERMISSION_CODE = 200;

  /**
   * List of the gallery items (photos and videos).
   */
  private final List<GalleryItem> galleryItems = new ArrayList<>();

  /**
   * String array of necessary gallery permissions.
   */
  private String[] permissions = {permission.READ_EXTERNAL_STORAGE,
      permission.WRITE_EXTERNAL_STORAGE};

  /**
   * Text view informing that the gallery is empty.
   */
  private TextView emptyGalleryTextView;

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.gallery_layout, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    emptyGalleryTextView = view.findViewById(R.id.emptyGalleryTextView);
    emptyGalleryTextView.setVisibility(View.VISIBLE);
    emptyGalleryTextView.setText(getString(R.string.empty_gallery));
  }

  @Override
  public void onStart() {
    super.onStart();
    for (String permission : permissions) {
      if (ContextCompat
          .checkSelfPermission(Objects.requireNonNull(getActivity(), "Activity must not be null"),
              permission) != PackageManager.PERMISSION_GRANTED) {
        requestPermissions(permissions, REQUEST_PERMISSION_CODE);
        return;
      }
    }
  }

  @Override
  public boolean onGesture(Gesture glassGesture) {
    switch (glassGesture) {
      case TAP:
        if (galleryItems.isEmpty()) {
          return false;
        }
      default:
        return false;
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    if (requestCode == REQUEST_PERMISSION_CODE) {
      for (int result : grantResults) {
        if (result != PackageManager.PERMISSION_GRANTED) {
          Objects.requireNonNull(getActivity(), "Activity must not be null").finish();
        }
      }
    } else {
      super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
  }
}
