package org.fa.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.stream.Collectors;

import static java.nio.file.FileVisitResult.CONTINUE;

/**
 * Created by davidmurillomatallana on 04/02/2018.
 */
public class DuplicatedFileFinder extends SimpleFileVisitor<Path> {

    private static final Logger log = LoggerFactory.getLogger(DuplicatedFileFinder.class);

    private PathMatcher pathMatcher;
    private List<File> files;

    public DuplicatedFileFinder(String fileNamePattern) {
        pathMatcher = FileSystems.getDefault().getPathMatcher("glob:" + fileNamePattern);
        files = new ArrayList<>();
    }

    @Override
    public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
        if (pathMatcher.matches(path.getFileName())) {
            files.add(path.toFile());
        }
        return CONTINUE;
    }

    public Map<String, List<File>> collect() {
        return files.stream()
                .collect(Collectors.groupingBy(File::getName));
    }

    public static void printDuplicatedFiles(String fileName, List<File> fileList) {
        if (fileList.size() >= 2) {
            log.info("Duplicated files");
            fileList.stream().forEach(DuplicatedFileFinder::printFileInfo);
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
