package org.fa.core;

import com.google.common.io.MoreFiles;
import com.google.common.io.RecursiveDeleteOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by davidmurillomatallana on 23/01/2018.
 */
public class DirectoryEraser {

    private static final Logger log = LoggerFactory.getLogger(DirectoryEraser.class);

    public static void run(Path source, String dirToDelete) {
        log.debug("run:: path:" + source.toString());
        if (isDirNameEquals(source, dirToDelete)) {
            try {
                MoreFiles.deleteRecursively(source, RecursiveDeleteOption.ALLOW_INSECURE);
                log.debug("Dir deleted: " + source.toString());
            } catch (IOException e) {
                log.error("Error deleting dir: " + source.toString(), e);
            }

        }
    }

    public static boolean isDirNameEquals(Path path, String dirName) {
        return path.getName(path.getNameCount() - 1).toString().equals(dirName);
    }

}
