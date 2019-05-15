# Google Glass UI library

## Helper classes included in this library

### GlassGestureDetector
GlassGestureDetector helps to recognize common one finger gestures like:
* tap
* swipe down
* swipe up
* swipe forward
* swipe backward

Example usage:

```
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
