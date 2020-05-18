package org.appspot.apprtcstandalone;

import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Class that is able to save data from BufferedReader into a file.
 */
public class FileDataSaver implements DataSaver<CharSequence> {

    private static final String TAG = "FileDataSaver";
    private final String separator;
    private OnFileReadyListener fileReadyListener;
    private File file;

    /**
     * Data saver that uses file to save data.
     *
     * @param fileCreator to create a file to save data into.
     */
    public FileDataSaver(FileCreator fileCreator) {
        file = fileCreator.createFile();
        separator = System.getProperty("line.separator");

    }

    /**
     * Sets the OnFileReadyListener
     *
     * @param listener
     */
    public void setFileReadyListener(OnFileReadyListener listener) {
        this.fileReadyListener = listener;
    }

    @Override
    public void saveData(CharSequence charSequence) {

        BufferedWriter bufferedWriter = null;
        FileWriter fileWriter = null;

        try {
            fileWriter = new FileWriter(file, true);
            bufferedWriter = new BufferedWriter(fileWriter);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "File not found.");
            if (fileReadyListener != null) {
                fileReadyListener.onError(file, e);
                return;
            }
        } catch (IOException e) {
            Log.e(TAG, "File not Can't create log file..");
            if (fileReadyListener != null) {
                fileReadyListener.onError(file, e);
                return;
            }
        }

        try {
            bufferedWriter.write(charSequence + separator);
        } catch (IOException e) {
            Log.e(TAG, String.format("Error while writing file: %s", file.getAbsoluteFile()));
            if (fileReadyListener != null) {
                fileReadyListener.onError(file, e);
                return;
            }
        }

        try {
            bufferedWriter.close();
        } catch (IOException e) {
            Log.w(TAG, "Error while closing log file writer.");

            if (fileReadyListener != null) {
                fileReadyListener.onError(file, e);
                return;
            }
        }
        if (fileReadyListener != null) {
            fileReadyListener.onFileSuccessfulWrite(file);
        }
    }

    /**
     * Interface for classes that would listen for file saving events.
     */
    public interface OnFileReadyListener {

        /**
         * Called, when file is successfully written.
         *
         * @param file file, that data has been saved into.
         */
        void onFileSuccessfulWrite(File file);

        /**
         * Called, when there was a problem with saving data to file.
         *
         * @param file file that should have the saved data.
         * @param e    exception thrown by error.
         */
        void onError(File file, Exception e);
    }
}