package org.appspot.apprtcstandalone;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import javax.annotation.Nullable;
import org.webrtc.RendererCommon.ScalingType;

public class GlassCallFragment extends Fragment {

  private TextView captureFormatText;
  private SeekBar captureFormatSlider;
  private OnCallEvents callEvents;
  private CaptureQualityController seekBarChangeListener;
  private int startingProgress = 0;
  private boolean firstFling = true;
  private float singleStep;

  /**
   * Call control interface for container activity.
   */
  public interface OnCallEvents {
    void onCallHangUp();
    void onCameraSwitch();
    void onVideoScalingSwitch(ScalingType scalingType);
    void onCaptureFormatChange(int width, int height, int framerate);
    boolean onToggleMic();
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      Bundle savedInstanceState) {
    View controlView = inflater.inflate(R.layout.glass_fragment_call, container, false);
    captureFormatText = controlView.findViewById(R.id.capture_format_text_call);
    captureFormatSlider = controlView.findViewById(R.id.capture_format_slider_call);
    return controlView;
  }

  @Override
  public void onStart() {
    super.onStart();
    DisplayMetrics displayMetrics = new DisplayMetrics();
    getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

    seekBarChangeListener = new CaptureQualityController(captureFormatText, callEvents,
        captureFormatSlider);
    captureFormatSlider.setOnSeekBarChangeListener(
        seekBarChangeListener);
    singleStep = displayMetrics.widthPixels / captureFormatSlider.getMax() + 1;
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    callEvents = (OnCallEvents) activity;
  }
}
