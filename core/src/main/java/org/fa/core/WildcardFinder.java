package org.fa.core;

import static java.nio.file.FileVisitResult.*;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by davidmurillomatallana on 01/02/2018.
 */
public class WildcardFinder extends SimpleFileVisitor<Path> {

    private PathMatcher pathMatcher;
    private Set<Path> results;

    public WildcardFinder(String wildcard) {
        pathMatcher = FileSystems.getDefault().getPathMatcher("glob:" + wildcard);
        results = new HashSet<>();
    }

    private void find(Path path) {
        if (pathMatcher.matches(path.getFileName())) {
            results.add(path);
        }
    }

    public Set<Path> getResults() {
        return results;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        find(dir);
        return CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        find(file);
        return CONTINUE;
    }
}
