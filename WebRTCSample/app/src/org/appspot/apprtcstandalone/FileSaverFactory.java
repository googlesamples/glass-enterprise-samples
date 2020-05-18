package org.appspot.apprtcstandalone;

public class FileSaverFactory {

    private static final String WEBRTC_DIRECTORY = "webrtc_testapp";

    public static DataSaver<CharSequence> createSdCardDataSaver(CharSequence fileName) {
        return new FileDataSaver(new SdCardFileCreator(WEBRTC_DIRECTORY, fileName));
    }
}
