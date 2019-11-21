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

package com.example.glass.qrcodescannersample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.Nullable;

import com.example.glass.ui.GlassGestureDetector.Gesture;

/**
 * This activity scans a QR code and shows the result.
 */
public class MainActivity extends BaseActivity {

  private static final int REQUEST_CODE = 105;
  private TextView resultLabel;
  private TextView scanResult;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    resultLabel = findViewById(R.id.resultLabel);
    scanResult = findViewById(R.id.scanResult);
  }

  /**
   * Shows the detected {@link String} if the QR code was successfully read.
   */
  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
      if (data != null) {
        final String qrData = data.getStringExtra(CameraActivity.QR_SCAN_RESULT);
        resultLabel.setVisibility(View.VISIBLE);
        scanResult.setVisibility(View.VISIBLE);
        scanResult.setText(qrData);
      }
    }
  }

  /**
   * Hides previously shown QR code string and starts scanning QR Code on {@link Gesture#TAP}
   * gesture. Finishes application on {@link Gesture#SWIPE_DOWN} gesture.
   */
  @Override
  public boolean onGesture(Gesture gesture) {
    switch (gesture) {
      case TAP:
        startActivityForResult(new Intent(this, CameraActivity.class), REQUEST_CODE);
        resultLabel.setVisibility(View.GONE);
        scanResult.setVisibility(View.GONE);
        return true;
      case SWIPE_DOWN:
        finish();
        return true;
      default:
        return false;
    }
  }
}
