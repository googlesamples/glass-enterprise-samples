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

package com.example.glass.camera2sample;

import android.Manifest.permission;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.example.glass.camera2sample.GlassGestureDetector.Gesture;
import com.example.glass.camera2sample.GlassGestureDetector.OnGestureListener;
import com.example.glass.camera2sample.CameraActionHandler.CameraActionHandlerCallback;
import com.example.glass.camera2sample.CameraActionHandler.CameraMode;
import java.util.Objects;

/**
 * Fragment responsible for displaying the camera preview and handling camera actions.
 */
public class CameraFragment extends Fragment
    implements OnRequestPermissionsResultCallback, OnGestureListener, CameraActionHandlerCallback {

  private static final String TAG = CameraFragment.class.getSimpleName();

  /**
   * Request code for the camera permission. This value doesn't have any special meaning.
   */
  private static final int CAMERA_PERMISSIONS_REQUEST_CODE = 105;

  /**
   * Permissions required for the camera usage.
   */
  private static final String[] REQUIRED_PERMISSIONS = new String[]{permission.CAMERA,
      permission.WRITE_EXTERNAL_STORAGE, permission.RECORD_AUDIO};

  /**
   * Default margin for the shutter indicator.
   */
  private static final int DEFAULT_MARGIN_PX = 8;

  /**
   * An {@link TextureView} for camera preview.
   */
  private TextureView textureView;

  /**
   * An {@link ImageView} for camera shutter image.
   */
  private ImageView shutterImageView;

  /**
   * {@link CameraActionHandler} for the camera.
   */
  private CameraActionHandler cameraActionHandler;

  /**
   * {@link TextureView.SurfaceTextureListener} handles several lifecycle events on a {@link
   * TextureView}.
   */
  private final TextureView.SurfaceTextureListener surfaceTextureListener
      = new TextureView.SurfaceTextureListener() {

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture texture, int width, int height) {
      Log.d(TAG, "Surface texture available");
      cameraActionHandler.setPreviewSurface(getSurface(texture));
      cameraActionHandler.openCamera();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture texture, int width, int height) {
      Log.d(TAG, "Surface texture size changed");
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture texture) {
      Log.d(TAG, "Surface texture destroyed");
      return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture texture) {
    }
  };

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    final FrameLayout frameLayout = new FrameLayout(
        Objects.requireNonNull(getContext(), "Context must not be null"));
    textureView = new TextureView(getContext());
    frameLayout.addView(textureView);

    shutterImageView = new ImageView(getContext());
    shutterImageView.setImageResource(R.drawable.ic_camera_white_96dp);

    final FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
        LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    layoutParams.gravity = Gravity.END | Gravity.CENTER_VERTICAL;
    layoutParams.setMarginEnd(DEFAULT_MARGIN_PX);
    shutterImageView.setLayoutParams(layoutParams);
    frameLayout.addView(shutterImageView);
    return frameLayout;
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    cameraActionHandler = new CameraActionHandler(getContext(), this);
  }

  @Override
  public void onResume() {
    super.onResume();
    cameraActionHandler.startBackgroundThread();

    for (String permission : REQUIRED_PERMISSIONS) {
      if (ContextCompat
          .checkSelfPermission(Objects.requireNonNull(getActivity(), "Activity must not be null"),
              permission)
          != PackageManager.PERMISSION_GRANTED) {
        Log.d(TAG, "Requesting for the permissions");
        requestPermissions(new String[]{permission}, CAMERA_PERMISSIONS_REQUEST_CODE);
        return;
      }
    }

    if (textureView.isAvailable()) {
      cameraActionHandler.setPreviewSurface(getSurface(textureView.getSurfaceTexture()));
      cameraActionHandler.openCamera();
    } else {
      textureView.setSurfaceTextureListener(surfaceTextureListener);
    }
  }

  @Override
  public void onPause() {
    cameraActionHandler.closeCamera();
    cameraActionHandler.stopBackgroundThread();
    super.onPause();
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    if (requestCode == CAMERA_PERMISSIONS_REQUEST_CODE) {
      for (int result : grantResults) {
        if (result != PackageManager.PERMISSION_GRANTED) {
          Log.d(TAG, "Permission denied");
          Objects.requireNonNull(getActivity(), "Activity must not be null").finish();
        }
      }
    } else {
      super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    Log.d(TAG, "Permission granted");
  }

  @Override
  public boolean onGesture(Gesture gesture) {
    switch (gesture) {
      case TAP:
        cameraActionHandler.performTapAction();
        return true;
      case SWIPE_FORWARD:
        cameraActionHandler.performSwipeForwardAction();
        return true;
      case SWIPE_BACKWARD:
        cameraActionHandler.performSwipeBackwardAction();
        return true;
      case SWIPE_DOWN:
        Objects.requireNonNull(getActivity()).finish();
        return true;
      default:
        return false;
    }
  }

  @Override
  public void onTakingPictureStarted() {
    Log.d(TAG, "Taking picture started");
    AnimationManager.animateShutter(getContext(), shutterImageView);
  }

  @Override
  public void onVideoRecordingStarted() {
    Log.d(TAG, "Video recording started");
    AnimationManager.changeImageByAlpha(shutterImageView, R.drawable.ic_videocam_red_96dp);
  }

  @Override
  public void onVideoRecordingStopped() {
    Log.d(TAG, "Video recording stopped");
    AnimationManager.changeImageByAlpha(shutterImageView, R.drawable.ic_videocam_white_96dp);
  }

  @Override
  public void onCameraModeChanged(CameraMode newCameraMode) {
    Log.d(TAG, "Camera mode changed to " + newCameraMode.name());
    switch (newCameraMode) {
      case VIDEO:
        AnimationManager.changeImageByAlpha(shutterImageView, R.drawable.ic_videocam_white_96dp);
        break;
      case PICTURE:
        AnimationManager.changeImageByAlpha(shutterImageView, R.drawable.ic_camera_white_96dp);
        break;
    }
  }

  private Surface getSurface(SurfaceTexture surfaceTexture) {
    final DisplayMetrics displayMetrics = new DisplayMetrics();
    Objects.requireNonNull(getActivity(), "Activity must not be null").getWindowManager()
        .getDefaultDisplay().getRealMetrics(displayMetrics);
    surfaceTexture.setDefaultBufferSize(displayMetrics.widthPixels, displayMetrics.heightPixels);
    return new Surface(surfaceTexture);
  }
}