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

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.VideoView;
import com.example.glass.gallerysample.menu.MenuActivity;
import com.example.glass.ui.GlassGestureDetector.Gesture;
import com.example.glass.ui.GlassGestureDetector.OnGestureListener;
import java.io.File;

/**
 * Displays selected gallery item on the full screen.
 */
public class GalleryItemFragment extends Fragment implements OnGestureListener {

  /**
   * Request code for starting the {@link MenuActivity}.
   */
  public static final int MENU_REQUEST_CODE = 205;

  private static final String NAME_KEY = "name";
  private static final String PATH_KEY = "path";
  private static final String TYPE_KEY = "type";

  private ImageView imageView;
  private ImageView playButtonVideoImageView;
  private VideoView videoView;
  private String filePath;
  private GalleryItem.Type galleryItemType;
  private OnGalleryItemDeletedListener onGalleryItemDeletedListener;

  /**
   * Creates new instance of the {@link GalleryItemFragment}, passing selected {@link GalleryItem}
   * object to it.
   */
  public static GalleryItemFragment newInstance(GalleryItem galleryItem) {
    final GalleryItemFragment galleryItemFragment = new GalleryItemFragment();
    galleryItemFragment.setArguments(GalleryItemFragment
        .createArguments(galleryItem.getName(), galleryItem.getPath(),
            galleryItem.getType().ordinal()));
    return galleryItemFragment;
  }

  /**
   * Returns {@link Bundle} with passed item name, path and type.
   */
  private static Bundle createArguments(String name, String path, int type) {
    final Bundle bundle = new Bundle();
    bundle.putString(NAME_KEY, name);
    bundle.putString(PATH_KEY, path);
    bundle.putInt(TYPE_KEY, type);
    return bundle;
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    galleryItemType = GalleryItem.Type.fromId(getArguments().getInt(TYPE_KEY));
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    switch (galleryItemType) {
      case VIDEO:
        return inflater.inflate(R.layout.gallery_item_video, container, false);
      default:
        return inflater.inflate(R.layout.gallery_item_picture, container, false);
    }
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    switch (galleryItemType) {
      case VIDEO:
        playButtonVideoImageView = view.findViewById(R.id.playButtonVideoImageView);
        filePath = getArguments().getString(PATH_KEY);
        videoView = view.findViewById(R.id.videoView);
        videoView.setVideoURI(Uri.fromFile(new File(filePath)));
        videoView.start();
        videoView.setOnCompletionListener(new OnCompletionListener() {
          @Override
          public void onCompletion(MediaPlayer mp) {
            playButtonVideoImageView.setVisibility(View.VISIBLE);
          }
        });
        break;
      default:
        imageView = view.findViewById(R.id.galleryItemImageView);
        filePath = getArguments().getString(PATH_KEY);
        final Drawable drawable = Drawable.createFromPath(filePath);
        imageView.setImageDrawable(drawable);
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    ((BaseActivity) requireActivity()).setOnGestureListener(this);
  }

  @Override
  public boolean onGesture(Gesture glassGesture) {
    switch (glassGesture) {
      case TAP:
        switch (GalleryItem.Type.fromId(getArguments().getInt(TYPE_KEY))) {
          case IMAGE:
            startActivityForResult(getMenuIntent(R.menu.photo_menu), MENU_REQUEST_CODE);
            break;
          case VIDEO:
            startActivityForResult(getMenuIntent(R.menu.video_menu), MENU_REQUEST_CODE);
        }
        return true;
      case SWIPE_DOWN:
        ((BaseActivity) requireActivity()).popBackStack();
        return true;
      default:
        return false;
    }
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == MENU_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
      final int id = data.getIntExtra(MenuActivity.EXTRA_MENU_ITEM_ID_KEY,
          MenuActivity.EXTRA_MENU_ITEM_DEFAULT_VALUE);
      switch (id) {
        case R.id.play_video:
          playButtonVideoImageView.setVisibility(View.GONE);
          videoView.start();
          break;
        case R.id.delete:
          GalleryItemsProvider.deleteGalleryItem(getContext(), filePath);
          onGalleryItemDeletedListener.onGalleryItemDeleted(filePath);
          break;
      }
    }
  }

  /**
   * Sets the {@link OnGalleryItemDeletedListener} listener for this fragment.
   */
  public void setOnGalleryItemDeletedListener(OnGalleryItemDeletedListener listener) {
    this.onGalleryItemDeletedListener = listener;
  }

  private Intent getMenuIntent(int menu) {
    final Intent intent = new Intent(getContext(), MenuActivity.class);
    intent.putExtra(MenuActivity.EXTRA_MENU_KEY, menu);
    return intent;
  }

  /**
   * Interface to notify parent that {@link GalleryItem} has been deleted.
   */
  interface OnGalleryItemDeletedListener {

    void onGalleryItemDeleted(String filePath);
  }
}
