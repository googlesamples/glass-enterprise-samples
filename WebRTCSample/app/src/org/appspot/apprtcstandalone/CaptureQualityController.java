/*
 *  Copyright 2015 The WebRTC Project Authors. All rights reserved.
 *
 *  Use of this source code is governed by a BSD-style license
 *  that can be found in the LICENSE file in the root of the source
 *  tree. An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */

package org.appspot.apprtcstandalone;

import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Control capture format based on a seekbar listener.
 */
public class CaptureQualityController implements SeekBar.OnSeekBarChangeListener {

    private final Resolution[] resolutions = new Resolution[]{
            new Resolution(480, 360),
            new Resolution(1280, 720),
            new Resolution(1920, 1080)
    };

    private final int[] framerates = new int[]{
            15, 30, 60
    };
    private TextView captureFormatText;
    private GlassCallFragment.OnCallEvents callEvents;
    private CaptureOptionsPicker picker = new CaptureOptionsPicker(resolutions, framerates);
    private CaptureOptionsPicker.CaptureOptions captureOptions;

    public CaptureQualityController(
            TextView captureFormatText, GlassCallFragment.OnCallEvents callEvents, SeekBar seekbar) {
        this.captureFormatText = captureFormatText;
        this.callEvents = callEvents;
        captureOptions = new SimpleCaptureOptions(resolutions[0], framerates[0]);
        picker.setSeekbarValues(seekbar);
        seekbar.setProgress(seekbar.getMin());
        captureFormatText.setText(String.format(captureFormatText.getContext().
                getString(R.string.format_description), 1280, 720, 30));

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        captureOptions = picker.pickCaptureOptions(progress);
        updateResolutionInfo();
    }

    private void updateResolutionInfo() {
        captureFormatText.setText(
                String.format(captureFormatText.getContext().getString(R.string.format_description), captureOptions.getResolution().getWidth(),
                        captureOptions.getResolution().getHeight(), captureOptions.getFramerate()));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        callEvents.onCaptureFormatChange(captureOptions.getResolution().getWidth(), captureOptions.getResolution().getHeight(), captureOptions.getFramerate());
    }
}
