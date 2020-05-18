package org.appspot.apprtcstandalone;

import java.io.File;

/**
 * Interface for classes that can create files.
 */
public interface FileCreator {

    /**
     * Creates file.
     *
     * @return created file.
     */
    File createFile();
}
