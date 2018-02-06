package org.fa.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by davidmurillomatallana on 23/01/2018.
 */
public class FileCollector {


    private static Logger log = LoggerFactory.getLogger(FileCollector.class);
    private List<File> files;

    public FileCollector() {
        files = new ArrayList<>();
    }

    public void consume(Path path) {
        File file = path.toFile();
        if (file.isFile()) {
            files.add(file);
        }
    }

    public Map<String, List<File>> collect() {
        return files.stream()
                .collect(Collectors.groupingBy(File::getName));
    }

    public static void printDuplicatedFiles(String fileName, List<File> fileList) {
        if (fileList.size() >= 2) {
            log.info("Duplicated files");
            fileList.stream().forEach(FileCollector::printFileInfo);
        }
    }

    private static void printFileInfo(File file) {
        try {
            log.info(file.getAbsolutePath()
                    + " size: " + Files.size(Paths.get(file.getAbsolutePath()))
            + " date: " + file.lastModified());
        } catch (IOException e) {
            log.error("Error printing file info ", e);
        }
    }
}
