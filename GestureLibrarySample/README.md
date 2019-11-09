# Glass Gesture Detector Library Sample

This project is an example of a shared library for recognizing simple gestures 
on the touchpad, such as tap and swipe. It's used by many of the other samples in
this repository and it can also be used as a starting point for your own UI
libraries on Glass.

## Important Classes in this Sample

### GlassGestureDetector

GlassGestureDetector recognizes common one finger gestures like:

* tap
* swipe down
* swipe up
* swipe forward
* swipe backward

Example usage:

```java
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import com.example.glass.ui.GlassGestureDetector;
import com.example.glass.ui.GlassGestureDetector.Gesture;
import com.example.glass.ui.GlassGestureDetector.OnGestureListener;

public class MainActivity extends Activity implements OnGestureListener {

  private GlassGestureDetector glassGestureDetector;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    glassGestureDetector = new GlassGestureDetector(this, this);
  }

  @Override
  public boolean dispatchTouchEvent(MotionEvent ev) {
    return glassGestureDetector.onTouchEvent(event) || super.dispatchTouchEvent(event);
  }

  @Override
  public boolean onGesture(Gesture gesture) {
    switch (gesture) {
      case SWIPE_DOWN:
        finish();
        return true;
      default:
        return false;
    }
  }
}
```

## Building

This sample does not require any additional setup. Open the project in Android Studio and build as usual!
