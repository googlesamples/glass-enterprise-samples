package org.appspot.apprtcstandalone;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.Calendar;
import javax.annotation.Nullable;

/** Displays the meeting room name */
public class GlassHudFragment extends Fragment {

  public GlassHudFragment() {
    super();
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      Bundle savedInstanceState) {
    View controlView = inflater.inflate(R.layout.glass_fragment_hud, container, false);
    TextView roomName = controlView.findViewById(R.id.room_name_textview);
    roomName.setText(getArguments().getString(GlassCallActivity.EXTRA_ROOMID));
    return controlView;
  }
}
