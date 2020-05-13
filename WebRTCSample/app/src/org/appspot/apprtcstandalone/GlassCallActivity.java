package org.appspot.apprtcstandalone;


import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import com.example.glass.ui.GlassGestureDetector;
import com.example.glass.ui.GlassGestureDetector.Gesture;
import com.example.glass.ui.GlassGestureDetector.OnGestureListener;
import org.webrtc.StatsReport;

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
