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

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import com.example.glass.ui.GlassGestureDetector;
import com.example.glass.ui.GlassGestureDetector.Gesture;
import com.example.glass.ui.GlassGestureDetector.OnGestureListener;
import org.webrtc.StatsReport;

/** In call activity */
public class GlassCallActivity extends BaseCallActivity implements OnGestureListener {

  private GlassCallFragment callFragment;
  private GlassHudFragment hudFragment;
  private GlassGestureDetector gestureDetector;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    DisplayMetrics displayMetrics = new DisplayMetrics();
    getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

    callFragment = new GlassCallFragment();
    callFragment.setArguments(getIntent().getExtras());
    hudFragment = new GlassHudFragment();
    hudFragment.setArguments(getIntent().getExtras());

    gestureDetector = new GlassGestureDetector(this, this);

    callFragment.setArguments(getIntent().getExtras());
    FragmentTransaction ft = getFragmentManager().beginTransaction();
    ft.add(R.id.hud_fragment_container, hudFragment);
    ft.add(R.id.call_fragment_container, callFragment);
    ft.commit();
  }

  @Override
  public boolean dispatchTouchEvent(MotionEvent ev) {
    if (gestureDetector.onTouchEvent(ev)) {
      return true;
    }
    return super.dispatchTouchEvent(ev);
  }

  @Override
  public boolean onGesture(Gesture gesture) {
    switch (gesture) {
      case SWIPE_DOWN:
        disconnect();
        finish();
        return true;
      default:
        return false;
    }
  }

  @Override
  public void onPeerConnectionStatsReady(StatsReport[] reports) {
  }
}
