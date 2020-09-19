package org.fa.core;


import com.google.common.io.MoreFiles;
import com.google.common.io.RecursiveDeleteOption;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by davidmurillomatallana on 16/01/2018.
 */
class BrowserTest {

    private static final Logger log = LoggerFactory.getLogger(BrowserTest.class);

    private Path rootPath, path1, path2;

    @BeforeEach
    void beforeEach() throws IOException {
        rootPath = Paths.get("").resolve("testdir");
        path1 = rootPath.resolve("one");
        path2 = rootPath.resolve("two");
        Files.createDirectories(path1);
        Files.createDirectories(path2);
    }

    @AfterEach
    void afterEach() throws IOException {
        MoreFiles.deleteRecursively(rootPath, RecursiveDeleteOption.ALLOW_INSECURE);
    }

    @Test
    void findFilesByPattern() throws IOException {
        Files.createFile(path1.resolve("filename1.txt"));
        Files.createFile(path1.resolve("filename1a.txt"));
        Files.createFile(path2.resolve("name2.txt"));

        Browser browser = new Browser();
        Collection<Path> results = browser.findPaths(rootPath.toString(), "**/*.txt");

        assertEquals(3, results.size());
        results.stream().forEach(path -> {
            String fileName = path.getFileName().toString();
            assertTrue(fileName.equals("filename1.txt")
                    || fileName.equals("filename1a.txt")
                    || fileName.equals("name2.txt"));
        });
    }

    @Test
    public void findDirectoriesByPattern() throws IOException {
        Path noSvnPath = Paths.get(rootPath.toString(),
                "Diseno_y_Desarrollo", "Codificacion", "Pruebas_Unitarias", "Historics", "nosvn", "text-base");
        Path svnPath1 = Paths.get(rootPath.toString(),
                "Diseno_y_Desarrollo", "Codificacion", "Pruebas_Unitarias", "Historics", ".svn", "text-base");
        Path svnPath2 = Paths.get(rootPath.toString(),
                "Diseno_y_Desarrollo", "Codificacion", "Componente", "Historics", ".svn", "text-base");
        Files.createDirectories(noSvnPath);
        Files.createDirectories(svnPath1);
        Files.createDirectories(svnPath2);

        Browser browser = new Browser();
        Collection<Path> results = browser.findPaths(rootPath.toString(), "**/.svn");

        assertEquals(2, results.size());
        results.stream().forEach(p -> assertTrue(p.toString().contains(".svn")));
    }

    @Test
    public void findDirectoriesAndFilesByPattern() throws IOException {
        Path project1Path = Paths.get(path1.toString(),
                "Project1", "Code");
        Path project2Path = Paths.get(path2.toString(),
                "Project2", "Code");
        Path project3Path = Paths.get(path2.toString(),
                "Project3", "Test");
        Files.createDirectories(project1Path);
        Files.createDirectories(project2Path);
        Files.createDirectories(project3Path);
        Files.createFile(project1Path.resolve("MainProject1.java"));
        Files.createFile(project2Path.resolve("MainProject2.java"));
        Files.createFile(project3Path.resolve("TestProject3.java"));

        Browser browser = new Browser();
        Collection<Path> results = browser.findPaths(rootPath.toString(), "**/Code/*.java");

        assertEquals(2, results.size());
        results.stream().forEach(path -> {
            String fileName = path.getFileName().toString();
            assertTrue(fileName.equals("MainProject1.java")
                    || fileName.equals("MainProject2.java"));
        });
    }

    @Test
    void deleteDirectoryInTree() throws IOException {
        String dirToDelete = ".AppleDouble";
        Path dirToDelete1 = path1.resolve(dirToDelete);
        Path dirToDelete2 = path2.resolve(dirToDelete);
        Files.createDirectories(dirToDelete1);
        Files.createDirectories(dirToDelete2);
        Files.createFile(dirToDelete1.resolve("filename1.txt"));
        Files.createFile(dirToDelete1.resolve("filename1a.txt"));
        Files.createFile(dirToDelete2.resolve("filename2.txt"));

        Browser browser = new Browser();
        browser.deleteDirectoryInTree(rootPath.toString(), dirToDelete);

        assertFalse(dirToDelete1.toFile().exists());
        assertFalse(dirToDelete2.toFile().exists());
        assertTrue(path1.toFile().exists());
        assertTrue(path2.toFile().exists());
    }

    @Test
    void emptyResultWhenNoFiles() throws IOException {
        Browser browser = new Browser();
        Map<String, List<File>> result = browser.groupFilesByName(rootPath.toString(), "*.txt");

        assertTrue(result.isEmpty());
    }

    @Test
    void duplicatedFilesInDifferentDirectories() throws IOException {
        String duplicatedFileName = "fileName.txt";
        Files.createFile(path1.resolve(duplicatedFileName));
        Files.createFile(path2.resolve(duplicatedFileName));
        String uniqueFileName = "unique.txt";
        Files.createFile(path2.resolve(uniqueFileName));

        Browser browser = new Browser();
        Map<String, List<File>> result = browser.groupFilesByName(rootPath.toString(), "*.txt");

        assertTrue(result.size() == 2);
        assertTrue(result.containsKey(duplicatedFileName));
        assertTrue(result.get(duplicatedFileName).size() == 2);
        assertTrue(result.containsKey(uniqueFileName));
        assertTrue(result.get(uniqueFileName).size() == 1);
    }

    @Test
    void findDuplicatedFilesInDocuments() throws IOException {
        Browser browser = new Browser();
        browser.printDuplicatedFiles("/Users/davidmurillomatallana/Documents", "*.{doc,docx,xls,xlsx,pdf,ppt,pptx,jpg}");
    }
}
