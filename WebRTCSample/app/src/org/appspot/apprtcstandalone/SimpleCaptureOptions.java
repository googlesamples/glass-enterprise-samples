package org.appspot.apprtcstandalone;

public class SimpleCaptureOptions implements CaptureOptionsPicker.CaptureOptions {

    private Resolution resolution;
    private int framerate;

    public SimpleCaptureOptions(Resolution resolution, int framerate) {
        this.resolution = resolution;
        this.framerate = framerate;
    }

    @Override
    public Resolution getResolution() {
        return resolution;
    }

    @Override
    public int getFramerate() {
        return framerate;
    }
}
