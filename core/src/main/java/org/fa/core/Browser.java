package org.fa.core;

import static org.apache.commons.io.filefilter.FileFilterUtils.*;

import com.google.common.io.MoreFiles;
import org.apache.tools.ant.DirectoryScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static  java.util.stream.Collectors.*;

/**
 * Created by davidmurillomatallana on 16/01/2018.
 */
public class Browser {

    private static final Logger log = LoggerFactory.getLogger(Browser.class);

    public List<Path> getPaths(String rootDir) {
        List<Path> result = new ArrayList<>();
        Path rootPath = Paths.get(rootDir);
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(rootPath)) {
            for (Path path: stream) {
                result.add(path);
                File file = path.toFile();
                StringBuilder sb = new StringBuilder();
                if (file.isDirectory()) {
                    sb.append("D ");
                    result.addAll(getPaths(path.toString()));
                } else {
                    sb.append("F ");
                }
                sb.append(file.getName());
                log.debug(sb.toString());
            }
        } catch (IOException ioe) {
            log.error(ioe.getMessage());
        }
        return result;
    }

    public Collection<Path> findPaths(String dirToBrowse, String pattern) {
        WildcardFinder wildcardFinder = new WildcardFinder(pattern);
        try {
            Files.walkFileTree(Paths.get(dirToBrowse), wildcardFinder);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return wildcardFinder.getResults();
    }

    public void deleteDirectoryInTree(String dirToBrowse, String dirToDelete) {
        Path pathToBrowse = Paths.get(dirToBrowse);
        MoreFiles.fileTraverser().breadthFirst(pathToBrowse)
                .forEach(p -> DirectoryEraser.run(p, dirToDelete));
    }

    public Map<String, List<File>> findDuplicatedFiles(String path) {
        Path pathToBrowse = Paths.get(path);
        FileCollector fileCollector = new FileCollector();
        MoreFiles.fileTraverser().breadthFirst(pathToBrowse)
                .forEach(fileCollector::consume);
        return fileCollector.collect();
    }

    public static Map<String, List<File>> groupFilesByName(String path, String fileNamePattern) {
        DuplicatedFileFinder duplicatedFileFinder = new DuplicatedFileFinder(fileNamePattern);
        try {
            Files.walkFileTree(Paths.get(path), duplicatedFileFinder);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return duplicatedFileFinder.collect();
    }

    public static void printDuplicatedFiles(String path, String fileNamePattern) {
        groupFilesByName(path, fileNamePattern)
                .forEach(DuplicatedFileFinder::printDuplicatedFiles);
    }

    public static void main(String[] args) {
        if (args == null || args.length == 0) {
            return;
        }
        if (args[0].equals("-df") && args.length == 3) {
            printDuplicatedFiles(args[1], args[2]);
        }
    }
}
