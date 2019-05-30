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

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCaptureSession.CaptureCallback;
import android.hardware.camera2.CameraCaptureSession.StateCallback;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.ImageReader;
import android.media.ImageReader.OnImageAvailableListener;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Surface;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Helper class responsible for handling the camera actions. Its main responsibilities are:
 * <ul>
 *   <li>opening and closing camera depends on the activity lifecycle</li>
 *   <li>starting and stopping the background thread for camera and image reader</li>
 *   <li>taking picture using {@link CameraCaptureSessionController}</li>
 * </ul>
 */
public class CameraActionHandler implements OnImageAvailableListener {

  private static final String TAG = CameraActionHandler.class.getSimpleName();

  /**
   * Camera background thread name.
   */
  private static final String BACKGROUND_THREAD_NAME = "CameraBackgroundThread";

  /**
   * Timeout for camera to open.
   */
  private static final int CAMERA_LOCK_TIMEOUT_MS = 2500;

  /**
   * Context this handler is currently associated with.
   */
  private final Context context;

  /**
   * Provides {@link CameraCaptureSession} for the camera preview and to take pictures.
   */
  private final CameraCaptureSessionController cameraCaptureSessionController;

  /**
   * Handles the background {@link android.os.Handler}.
   */
  private final BackgroundThreadHandler backgroundThreadHandler;

  /**
   * Camera system service manager for connecting to the {@link CameraDevice}
   */
  private final CameraManager cameraManager;

  /**
   * Callback for the {@link CameraActionHandler}.
   */
  private final CameraActionHandlerCallback cameraActionHandlerCallback;

  /**
   * ID of the current {@link CameraDevice}.
   */
  private String cameraId;

  /**
   * A reference to the opened {@link CameraDevice}.
   */
  private CameraDevice cameraDevice;

  /**
   * An {@link ImageReaderProvider} that provides the {@link ImageReader}.
   */
  private ImageReaderProvider imageReaderProvider;

  /**
   * A {@link Semaphore} to prevent the app from exiting before closing the camera.
   */
  private Semaphore cameraOpenCloseLock = new Semaphore(1);

  /**
   * Surface for camera preview.
   */
  private Surface previewSurface;

  /**
   * Current mode of the camera application. {@link CameraMode#PICTURE} is a default mode.
   */
  private CameraMode cameraMode = CameraMode.PICTURE;

  /**
   * Provides functionality of video recording.
   */
  private VideoRecorder videoRecorder;

  /**
   * Creates {@link CameraCaptureSession} for a given parameters.
   */
  private CameraCaptureSessionFactory cameraCaptureSessionFactory;

  /**
   * Flag indicating if video capture session is preparing.
   */
  private boolean isVideoCaptureSessionPreparing = false;

  /**
   * {@link CameraDevice.StateCallback} is called when {@link CameraDevice} changes its state.
   */
  private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {

    @Override
    public void onOpened(@NonNull CameraDevice cameraDevice) {
      Log.d(TAG, "Camera device opened");
      CameraActionHandler.this.cameraDevice = cameraDevice;
      cameraCaptureSessionController.setCameraDevice(cameraDevice);
      cameraCaptureSessionFactory = new CameraCaptureSessionFactory(cameraDevice);
      createCameraPreviewSession();
    }

    @Override
    public void onDisconnected(@NonNull CameraDevice cameraDevice) {
      Log.d(TAG, "Camera device disconnected");
      CameraActionHandler.this.cameraDevice = cameraDevice;
      closeCamera();
    }

    @Override
    public void onError(@NonNull CameraDevice cameraDevice, int error) {
      Log.d(TAG, "Camera device error");
      CameraActionHandler.this.cameraDevice = cameraDevice;
      closeCamera();
      Toast.makeText(context, "Camera opening error", Toast.LENGTH_SHORT).show();
    }
  };

  /**
   * Creates {@link CameraActionHandler} object using {@link Context}.
   */
  public CameraActionHandler(Context context,
      CameraActionHandlerCallback cameraActionHandlerCallback) {
    this.context = context;
    this.cameraActionHandlerCallback = cameraActionHandlerCallback;
    backgroundThreadHandler = new BackgroundThreadHandler(BACKGROUND_THREAD_NAME);
    cameraManager = (CameraManager) Objects.requireNonNull(context, "Context must not be null")
        .getSystemService(Context.CAMERA_SERVICE);
    cameraCaptureSessionController = new CameraCaptureSessionController();
  }

  /**
   * Opens camera for a given parameters. Waits {@link CameraActionHandler#CAMERA_LOCK_TIMEOUT_MS}
   * milliseconds for the camera to open. Throws {@link RuntimeException} if this time is exceeded.
   */
  @SuppressLint("MissingPermission")
  public void openCamera() {
    Log.d(TAG, "Opening camera");
    setUpImageReader();
    try {
      if (!cameraOpenCloseLock.tryAcquire(CAMERA_LOCK_TIMEOUT_MS, TimeUnit.MILLISECONDS)) {
        throw new RuntimeException("Time out waiting to lock camera opening.");
      }
      cameraManager
          .openCamera(cameraId, stateCallback, backgroundThreadHandler.getHandler());
    } catch (CameraAccessException e) {
      Log.e(TAG, "Opening camera failed", e);
    } catch (InterruptedException e) {
      throw new RuntimeException("Interrupted while trying to lock camera opening.", e);
    }
  }

  /**
   * Closes {@link CameraDevice}, {@link CameraCaptureSession} and {@link ImageReader}.
   */
  public void closeCamera() {
    Log.d(TAG, "Start closing camera");
    try {
      cameraOpenCloseLock.acquire();
      cameraCaptureSessionController.closeSession();
      if (videoRecorder != null && videoRecorder.isRecording()) {
        stopRecording();
        videoRecorder.releaseMediaRecorder();
      }
      if (cameraDevice != null) {
        Log.d(TAG, "Closing camera device");
        cameraDevice.close();
        cameraDevice = null;
      }
      if (imageReaderProvider != null) {
        Log.d(TAG, "Closing image reader");
        imageReaderProvider.closeImageReader();
      }
    } catch (InterruptedException e) {
      throw new RuntimeException("Interrupted while trying to lock camera closing.", e);
    } finally {
      cameraOpenCloseLock.release();
    }
  }

  /**
   * Starts background {@link android.os.HandlerThread}.
   */
  public void startBackgroundThread() {
    backgroundThreadHandler.startBackgroundThread();
  }

  /**
   * Stops background {@link android.os.HandlerThread}.
   */
  public void stopBackgroundThread() {
    backgroundThreadHandler.stopBackgroundThread();
  }

  /**
   * Performs action on {@link com.example.glass.ui.GlassGestureDetector.Gesture#TAP} gesture.
   */
  public void performTapAction() {
    switch (cameraMode) {
      case PICTURE:
        takePicture();
        break;
      case VIDEO:
        if (isVideoCaptureSessionPreparing) {
          return;
        }
        isVideoCaptureSessionPreparing = true;
        if (videoRecorder.isRecording()) {
          stopRecording();
          createCameraPreviewSession();
        } else {
          startRecording();
        }
        break;
    }
  }

  /**
   * Performs action on {@link com.example.glass.ui.GlassGestureDetector.Gesture#SWIPE_FORWARD}
   * gesture.
   */
  public void performSwipeForwardAction() {
    Log.d(TAG, "Performing swipe forward action");
    if (cameraMode == CameraMode.PICTURE) {
      cameraMode = CameraMode.VIDEO;
      videoRecorder = new VideoRecorder();
      cameraActionHandlerCallback.onCameraModeChanged(cameraMode);
    }
  }

  /**
   * Performs action on {@link com.example.glass.ui.GlassGestureDetector.Gesture#SWIPE_BACKWARD}
   * gesture.
   */
  public void performSwipeBackwardAction() {
    Log.d(TAG, "Performing swipe backward action");
    if (cameraMode == CameraMode.VIDEO && !videoRecorder.isRecording()) {
      cameraMode = CameraMode.PICTURE;
      if (videoRecorder != null) {
        videoRecorder.releaseMediaRecorder();
      }
      cameraActionHandlerCallback.onCameraModeChanged(cameraMode);
    }
  }

  /**
   * Sets preview surface.
   */
  public void setPreviewSurface(Surface previewSurface) {
    this.previewSurface = previewSurface;
  }

  /**
   * Takes picture and gets back to the preview after this.
   */
  private void takePicture() {
    Log.d(TAG, "Taking picture");
    cameraActionHandlerCallback.onTakingPictureStarted();
    cameraCaptureSessionController.captureStillPicture(
        Objects.requireNonNull(imageReaderProvider.getImageReader(), "ImageReader must not be null")
            .getSurface(), new CaptureCallback() {
          @Override
          public void onCaptureCompleted(@NonNull CameraCaptureSession session,
              @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
            startPreview();
          }
        });
  }

  /**
   * Gets back to the preview.
   */
  private void startPreview() {
    Log.d(TAG, "Getting back to the preview");
    cameraCaptureSessionController
        .createPreviewSession(CameraDevice.TEMPLATE_PREVIEW,
            Collections.singletonList(previewSurface));
  }

  /**
   * Closes preview session and using {@link VideoRecorder} prepares to the record and starts
   * recording video.
   */
  private void startRecording() {
    Log.d(TAG, "Starting recording");
    cameraActionHandlerCallback.onVideoRecordingStarted();
    closePreviewSession();
    // Set up Surface for the MediaRecorder
    videoRecorder.initRecorder();
    videoRecorder.prepareRecorder();
    Surface recorderSurface = videoRecorder.getSurface();

    final List<Surface> surfaces = new ArrayList<>();
    surfaces.add(previewSurface);
    surfaces.add(recorderSurface);

    cameraCaptureSessionFactory
        .createCaptureSession(surfaces, new StateCallback() {
          @Override
          public void onConfigured(@NonNull CameraCaptureSession session) {
            Log.d(TAG, "TEMPLATE_RECORD capture session configured");
            cameraCaptureSessionController.setSession(session);
            cameraCaptureSessionController
                .createPreviewSession(CameraDevice.TEMPLATE_RECORD, surfaces);
            videoRecorder.startRecording();
            isVideoCaptureSessionPreparing = false;
          }

          @Override
          public void onConfigureFailed(@NonNull CameraCaptureSession session) {
            Log.e(TAG, "TEMPLATE_RECORD capture session configuration failed");
          }
        });
  }

  /**
   * Using {@link VideoRecorder} stops recording video, refreshes file indexing after saving
   * recorded video.
   */
  private void stopRecording() {
    Log.d(TAG, "Stopping recording");
    videoRecorder.stopRecording();
    cameraActionHandlerCallback.onVideoRecordingStopped();
    FileManager.refreshFileIndexing(context, videoRecorder.getLastRecordedFile());
  }

  /**
   * Closes preview session.
   */
  private void closePreviewSession() {
    Log.d(TAG, "Closing preview session");
    cameraCaptureSessionController.closeSession();
  }

  /**
   * Added functionality of recording and storing videos to the Camera2Sample application Creates a
   * new {@link CameraCaptureSession} for camera preview.
   */
  private void createCameraPreviewSession() {
    Log.d(TAG, "Creating camera preview session");
    final List<Surface> surfaces = Arrays.asList(previewSurface,
        Objects.requireNonNull(imageReaderProvider.getImageReader(), "ImageReader must not be null")
            .getSurface());
    cameraCaptureSessionFactory.createCaptureSession(surfaces, new StateCallback() {
      @Override
      public void onConfigured(@NonNull CameraCaptureSession session) {
        Log.d(TAG, "Preview session configured");
        cameraCaptureSessionController.setSession(session);
        startPreview();
        cameraOpenCloseLock.release();
        isVideoCaptureSessionPreparing = false;
      }

      @Override
      public void onConfigureFailed(@NonNull CameraCaptureSession session) {
        Log.e(TAG, "Preview session configuration failed");
      }
    });
  }

  /**
   * Sets up member variables related to camera.
   */
  private void setUpImageReader() {
    Log.d(TAG, "Setting up image reader");
    try {
      for (String cameraId : cameraManager.getCameraIdList()) {
        final StreamConfigurationMap map = cameraManager.getCameraCharacteristics(cameraId)
            .get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        if (map == null) {
          Log.d(TAG, "Stream configuration map is null");
          continue;
        }

        imageReaderProvider = new ImageReaderProvider(map);
        imageReaderProvider.setOnImageAvailableListener(this, backgroundThreadHandler.getHandler());
        this.cameraId = cameraId;
        return;
      }
    } catch (CameraAccessException | NullPointerException e) {
      Log.e(TAG, "Setting up image reader failed", e);
    }
  }

  @Override
  public void onImageAvailable(ImageReader reader) {
    Log.d(TAG, "Image is available");
    FileManager.saveImage(context, reader);
  }

  /**
   * Available camera modes.
   */
  public enum CameraMode {
    PICTURE, VIDEO
  }

  /**
   * Callback for the camera action.
   */
  interface CameraActionHandlerCallback {

    void onTakingPictureStarted();

    void onVideoRecordingStarted();

    void onVideoRecordingStopped();

    void onCameraModeChanged(CameraMode newCameraMode);
  }
}
