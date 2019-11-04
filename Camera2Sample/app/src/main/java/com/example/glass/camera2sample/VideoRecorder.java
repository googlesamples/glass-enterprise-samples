/*
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.glass.camera2sample;

import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.MediaRecorder.AudioSource;
import android.media.MediaRecorder.VideoSource;
import android.util.Log;
import android.view.Surface;
import java.io.File;
import java.io.IOException;

/**
 * Provides functionality of recording video using {@link MediaRecorder}.
 */
public class VideoRecorder {

  private static final String TAG = VideoRecorder.class.getSimpleName();

  private MediaRecorder recorder;
  private boolean isRecording = false;
  private File outputFile;
  private Surface surface;

  /**
   * Sets output file for the video recording and initializes {@link MediaRecorder}.
   */
  public void initRecorder() {
    Log.d(TAG, "Initializing video recorder");
    outputFile = FileManager.getOutputVideoFile();
    recorder = new MediaRecorder();
    recorder.setAudioSource(AudioSource.CAMCORDER);
    recorder.setVideoSource(VideoSource.SURFACE);
    recorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_720P));
    recorder.setOutputFile(outputFile.getPath());
  }

  /**
   * Prepares {@link MediaRecorder} and sets the surface to recording.
   */
  public void prepareRecorder() {
    Log.d(TAG, "Preparing video recorder");
    try {
      recorder.prepare();
      surface = recorder.getSurface();
    } catch (IllegalStateException | IOException e) {
      Log.e(TAG, "Preparing video recorder failed", e);
    }
  }

  /**
   * Returns TRUE if {@link MediaRecorder} is in the middle of recording. FALSE otherwise.
   */
  public boolean isRecording() {
    return isRecording;
  }

  /**
   * Starts {@link MediaRecorder} and sets {@link VideoRecorder#isRecording} to true.
   */
  public void startRecording() {
    if (!isRecording) {
      Log.d(TAG, "Start recording");
      recorder.start();
      isRecording = true;
      return;
    }
    Log.d(TAG, "Recording is already started");
  }

  /**
   * Stops {@link MediaRecorder} and sets {@link VideoRecorder#isRecording} to false. Resets {@link
   * MediaRecorder} to prepare it for the next recording. Deletes output file if {@link Exception}
   * occurs.
   */
  public void stopRecording() {
    try {
      if (isRecording) {
        Log.d(TAG, "Stop recording");
        recorder.stop();
      } else {
        Log.d(TAG, "Recording is already stopped");
      }
      recorder.reset();
    } catch (Exception e) {
      if (outputFile.delete()) {
        Log.d(TAG, "File has been deleted");
      }
      Log.e(TAG, "Recording stop failed", e);
    }
    isRecording = false;
  }

  /**
   * Returns last recorded {@link File} object.
   */
  public File getLastRecordedFile() {
    return outputFile;
  }

  /**
   * Releases {@link MediaRecorder}.
   */
  public void releaseMediaRecorder() {
    if (recorder != null) {
      Log.d(TAG, "Releasing media recorder");
      recorder.release();
      return;
    }
    Log.d(TAG, "Media recorder is null");
  }

  /**
   * Returns recording surface.
   */
  public Surface getSurface() {
    return surface;
  }
}
