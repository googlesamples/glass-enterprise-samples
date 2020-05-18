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
package org.appspot.apprtcstandalone;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.glass.ui.GlassGestureDetector;
import com.example.glass.ui.GlassGestureDetector.Gesture;
import com.example.glass.ui.GlassGestureDetector.OnGestureListener;
import org.appspot.apprtcstandalone.util.RandomRoomNameCreator;
import org.appspot.apprtcstandalone.util.RoomNameCreator;

/** Connect to a video chat room */
public class GlassConnectActivity extends BaseConnectActivity implements OnGestureListener {

  private static final String TAG = "GlassConnectActivity";
  private static final int PERMISSIONS_REQUEST = 999;
  private static final String[] PERMISSIONS = new String[]{Manifest.permission.RECORD_AUDIO,
      Manifest.permission.CAMERA};

  private TextView roomNameTextView;
  private RoomNameCreator roomNameCreator;
  private View decorView;
  private GlassGestureDetector gestureDetector;
  private boolean isConnecting = false;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    decorView = getWindow().getDecorView();
    decorView
        .setOnSystemUiVisibilityChangeListener(visibility -> {
          if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
            hideSystemUI();
          }
        });
    resetViews();

    for (String permission : PERMISSIONS) {
      if (ContextCompat.checkSelfPermission(this, permission)
          != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSIONS_REQUEST);
      }
      break;
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, String[] permissions,
      int[] grantResults) {
    if (requestCode == PERMISSIONS_REQUEST) {
      if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        Log.d(TAG, "permission granted");
      } else {
        Log.e(TAG, "no permission to record audio/video");
        finish();
      }
    }
  }

  private void resetViews() {
    View mainView = getLayoutInflater().inflate(R.layout.glass_activity_connect, null, false);
    setContentView(mainView);

    gestureDetector = new GlassGestureDetector(this, this);
    roomNameCreator = new RandomRoomNameCreator();
    roomNameTextView = findViewById(R.id.room_name_textview);
    roomNameTextView.setText(roomNameCreator.getRoomName());
  }

  private void refreshUi() {
    isConnecting = true;
    ViewGroup mainView = findViewById(android.R.id.content);
    mainView.removeAllViews();
    ProgressBar pb = new ProgressBar(this);
    pb.setIndeterminate(true);
    mainView.addView(pb);
  }

  @Override
  protected void onResume() {
    super.onResume();
    hideSystemUI();
    isConnecting = false;
    resetViews();
    roomNameTextView.setText(roomNameCreator.getRoomName());
  }

  @Override
  protected String getRoomString() {
    return roomNameTextView.getText().toString();
  }

  @Override
  protected Intent createCallActivityIntent() {
    return new Intent(this, GlassCallActivity.class);
  }

  @Override
  public boolean dispatchTouchEvent(MotionEvent ev) {
    if (isConnecting) {
      return super.dispatchTouchEvent(ev);
    }
    if (gestureDetector.onTouchEvent(ev)) {
      return true;
    }
    return super.dispatchTouchEvent(ev);
  }

  @Override
  public boolean onGesture(Gesture gesture) {
    switch (gesture) {
      case TAP:
        refreshUi();
        connectToRoom(getRoomString(), false, false, false, 0);
        return true;
      case SWIPE_DOWN:
        onBackPressed();
        return true;
      default:
        return false;
    }
  }

  private void hideSystemUI() {
    decorView.setSystemUiVisibility(
        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
  }
}
