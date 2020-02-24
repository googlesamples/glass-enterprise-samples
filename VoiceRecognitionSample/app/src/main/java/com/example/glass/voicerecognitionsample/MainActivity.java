
/*
 * Copyright 2020 Google LLC
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

package com.example.glass.voicerecognitionsample;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.glass.ui.GlassGestureDetector;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
    GlassGestureDetector.OnGestureListener {

  private static final int REQUEST_CODE = 999;
  private static final String TAG = MainActivity.class.getSimpleName();
  private static final String DELIMITER = "\n";

  private TextView resultTextView;
  private GlassGestureDetector glassGestureDetector;
  private List<String> mVoiceResults = new ArrayList<>(4);

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    resultTextView = findViewById(R.id.results);
    glassGestureDetector = new GlassGestureDetector(this, this);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (resultCode == RESULT_OK) {
      List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
      Log.d(TAG, "results: " + results.toString());
      if (results != null && results.size() > 0 && !results.get(0).isEmpty()) {
        updateUI(results.get(0));
      }
    } else {
      Log.d(TAG, "Result not OK");
    }
  }

  @Override
  public boolean dispatchTouchEvent(MotionEvent ev) {
    return glassGestureDetector.onTouchEvent(ev) || super.dispatchTouchEvent(ev);
  }

  @Override
  public boolean onGesture(GlassGestureDetector.Gesture gesture) {
    switch (gesture) {
      case TAP:
        requestVoiceRecognition();
        break;
      case SWIPE_DOWN:
        finish();
        break;
      default:
        return false;
    }
    return true;
  }

  private void requestVoiceRecognition() {
    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
    startActivityForResult(intent, REQUEST_CODE);
  }

  private void updateUI(String result) {
    if (mVoiceResults.size() >= 4) {
      mVoiceResults.remove(mVoiceResults.size() - 1);
    }
    mVoiceResults.add(0, result);
    String recognizedText = String.join(DELIMITER, mVoiceResults);
    resultTextView.setText(recognizedText);
  }
}
