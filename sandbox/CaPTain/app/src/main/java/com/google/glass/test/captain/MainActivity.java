package com.google.glass.test.captain;

import android.app.Activity;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.Queue;

public class MainActivity extends Activity {

    private Button button;
    private TextView textView;
    private Queue<String> motionEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        motionEvents = new LinkedList<>();
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        ConstraintLayout mainView = (ConstraintLayout) layoutInflater.inflate(R.layout.activity_main, null, false);
        textView = mainView.findViewById(R.id.textView);
        textView.setText(R.string.capture_info);
        button = mainView.findViewById(R.id.button);
        button.setText(R.string.info_text);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (button.hasPointerCapture()) {
                    button.releasePointerCapture();
                } else {
                    button.requestPointerCapture();
                }
            }
        });
        button.setOnCapturedPointerListener(new View.OnCapturedPointerListener() {
            @Override
            public boolean onCapturedPointer(View view, MotionEvent event) {
                logMotionEvent(event);
                return true;
            }
        });
        setContentView(mainView);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        button.setText(String.format("Pointer capture status: %s", hasCapture));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        return super.onGenericMotionEvent(event);
    }

    private void logMotionEvent(MotionEvent motionEvent) {
        String gesture = MotionEvent.actionToString(motionEvent.getAction());
        if (motionEvents.size() > 10) {
            motionEvents.remove();
        }
        motionEvents.add(gesture);

        StringBuilder stringBuilder = new StringBuilder();
        for (String oneGesture : motionEvents) {
            stringBuilder.append(oneGesture).append("\n");
        }
        textView.setText(stringBuilder);
    }
}
