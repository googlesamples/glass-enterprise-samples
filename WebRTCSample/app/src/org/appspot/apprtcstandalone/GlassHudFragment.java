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

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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
