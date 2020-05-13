package org.appspot.apprtcstandalone;

/**
 * Interface to be implemented by classes that could save DataTypeT.
 *
 * @param <DataTypeT>
 */
public interface DataSaver<DataTypeT> {

    /**
     * Saves the data
     *
     * @param data to be saved.
     */
    void saveData(DataTypeT data);
}
