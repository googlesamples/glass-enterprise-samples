package org.appspot.apprtcstandalone;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

/**
 * An implementation of FileCreator that would create files on sdcard in systemlogs folder.
 */
public class SdCardFileCreator implements FileCreator {

    private static final String SEPARATOR = "_";
    private static final String EXTENSION = ".txt";
    private static final String TAG = "FileCreator";
    private static final String SD_CARD_DIR = Environment.getExternalStorageDirectory().getAbsolutePath();
    private static final String SLASH = "/";
    private final CharSequence directoryName;
    private final CharSequence fileName;

    /**
     * Default constructor
     *
     * @param directoryName directory name to be created in systemlogs directory.
     * @param fileName      the name of the file to be appended with date of file creation.
     */
    public SdCardFileCreator(CharSequence directoryName, CharSequence fileName) {

        this.directoryName = directoryName;
        this.fileName = fileName;
    }

    @Override
    public File createFile() {
        File directory =
                new File(getFilePath(directoryName));
        if (!directory.exists()) {
            boolean mkdir = directory.mkdirs();
            if (!mkdir) {
                return null;
            }
        }

        File file =
                new File(
                        directory.getAbsoluteFile(),
                        String.valueOf(fileName) + SEPARATOR + getCreationDate() + EXTENSION);
        if (file.exists()) {
            return null;
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            Log.e(TAG, "Cannot create log file.");
            return null;
        }
        return file;
    }

    private String getCreationDate() {
        StringBuilder outputDate = new StringBuilder();
        Calendar cal = Calendar.getInstance();
        outputDate.append(cal.get(Calendar.YEAR)).append(SEPARATOR);
        outputDate.append(cal.get(Calendar.MONTH)).append(SEPARATOR);
        outputDate.append(cal.get(Calendar.DAY_OF_MONTH)).append(SEPARATOR);
        outputDate.append(cal.get(Calendar.HOUR_OF_DAY)).append(SEPARATOR);
        outputDate.append(cal.get(Calendar.MINUTE)).append(SEPARATOR);
        outputDate.append(cal.get(Calendar.SECOND));
        return outputDate.toString();
    }

    private String getFilePath(CharSequence directory) {
        return SD_CARD_DIR + SLASH + directory;

    }
}

