package org.appspot.apprtcstandalone;

import android.widget.SeekBar;

/**
 * Picker for the resolution and framerate values. It sets the minimum and maximum values for
 * {@link SeekBar} and then scales it's position to proper values.
 */
public class CaptureOptionsPicker {
    private Resolution[] resolutions;
    private int[] framerates;

    public CaptureOptionsPicker(Resolution[] resolutions, int[] framerates) {
        this.resolutions = resolutions;
        this.framerates = framerates;
    }

    public CaptureOptions pickCaptureOptions(int currentValue) {
        int resolutionIndex = currentValue / framerates.length;
        int framerateIndex = currentValue % framerates.length;
        return new SimpleCaptureOptions(resolutions[resolutionIndex], framerates[framerateIndex]);
    }

    public void setSeekbarValues(SeekBar seekbar) {
        seekbar.setMin(0);
        seekbar.setMax((resolutions.length * framerates.length) - 1);
    }

    public interface CaptureOptions {
        Resolution getResolution();

        int getFramerate();
    }

}
