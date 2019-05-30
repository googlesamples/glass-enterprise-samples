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

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.example.glass.ui.GlassGestureDetector.Gesture;
import com.example.glass.ui.GlassGestureDetector.OnGestureListener;

/**
 * Displays selected gallery item on the full screen.
 */
public class GalleryItemFragment extends Fragment implements OnGestureListener {

  private static final String NAME_KEY = "name";
  private static final String PATH_KEY = "path";
  private static final String TYPE_KEY = "type";

  private ImageView imageView;
  private String filePath;

  /**
   * Returns {@link Bundle} with passed item name, path and type.
   */
  public static Bundle createArguments(String name, String path, int type) {
    final Bundle bundle = new Bundle();
    bundle.putString(NAME_KEY, name);
    bundle.putString(PATH_KEY, path);
    bundle.putInt(TYPE_KEY, type);
    return bundle;
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.gallery_item_picture, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    imageView = view.findViewById(R.id.galleryItemImageView);
    filePath = getArguments().getString(PATH_KEY);
    final Drawable drawable = Drawable.createFromPath(filePath);
    imageView.setImageDrawable(drawable);
  }

  @Override
  public void onResume() {
    super.onResume();
    ((BaseActivity) requireActivity()).setOnGestureListener(this);
  }

  @Override
  public boolean onGesture(Gesture glassGesture) {
    switch (glassGesture) {
      case SWIPE_DOWN:
        getFragmentManager().popBackStack();
        return true;
      default:
        return false;
    }
  }
}
