package org.fa.core;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.common.io.MoreFiles;
import com.google.common.io.RecursiveDeleteOption;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

/**
 * Created by davidmurillomatallana on 16/01/2018.
 */
public class BrowserTest {

    private static final Logger log = LoggerFactory.getLogger(BrowserTest.class);

    //@Test
    public void findFilesByPattern() throws IOException {
        Path rootPath = Paths.get(Paths.get("").toAbsolutePath().toString() + File.separator + "testdir");
        Path path1 = Paths.get(rootPath.toString() + File.separator + "one");
        Path path2 = Paths.get(rootPath.toString() + File.separator + "two");
        Files.createDirectories(path1);
        Files.createDirectories(path2);
        Files.createFile(Paths.get(path1.toString() + File.separator + "filename1.txt"));
        Files.createFile(Paths.get(path1.toString() + File.separator + "filename1a.txt"));
        Files.createFile(Paths.get(path2.toString() + File.separator + "name2.txt"));

        Browser browser = new Browser();
        Collection<Path> results = browser.findPaths(rootPath.toString(), "file*.txt");

        assertEquals(2, results.size());
        results.stream()
            .forEach(p -> assertTrue(p.getFileName().toString().equals("filename1.txt")
                    || p.getFileName().toString().equals("filename1a.txt")));

        MoreFiles.deleteRecursively(rootPath, RecursiveDeleteOption.ALLOW_INSECURE);

    }

    //@Test
    public void findDirectoriesByPattern() throws IOException {
        Path rootPath = Paths.get(Paths.get("").toAbsolutePath().toString() + File.separator + "testdir");
        //Diseno_y_Desarrollo/Codificacion/Pruebas_Unitarias/Historics/.svn/text-base/
        Path path1 = Paths.get(rootPath.toString() + File.separator + "Diseno_y_Desarrollo"
                + File.separator + "Codificacion" + File.separator + "Pruebas_Unitarias"
                + File.separator + "Historics" + File.separator + "nosvn" + File.separator + "text-base");
        Path path2 = Paths.get(rootPath.toString() + File.separator + "Diseno_y_Desarrollo"
                + File.separator + "Codificacion" + File.separator + "Pruebas_Unitarias"
                + File.separator + "Historics" + File.separator + ".svn" + File.separator + "text-base");
        Path path3 = Paths.get(rootPath.toString() + File.separator + "Diseno_y_Desarrollo"
                + File.separator + "Codificacion" + File.separator + "Componente"
                + File.separator + "Historics" + File.separator + ".svn" + File.separator + "text-base");
        Files.createDirectories(path1);
        Files.createDirectories(path2);
        Files.createDirectories(path3);

        Browser browser = new Browser();
        Collection<Path> results = browser.findPaths(rootPath.toString(), "**/nosvn");

        assertEquals(2, results.size());
        results.stream().forEach(p -> assertTrue(p.equals(path2) || p.equals(path3)));

        MoreFiles.deleteRecursively(rootPath, RecursiveDeleteOption.ALLOW_INSECURE);
    }

    public void findDirectoriesAndFilesByPattern() {

    }

    //@Test
    public void deleteDirectoryInTree() throws IOException {
        String dirToDelete = ".AppleDouble";
        Path rootPath = Paths.get(Paths.get("").toAbsolutePath().toString() + File.separator + "testdir");
        Path path1 = Paths.get(rootPath.toString() + File.separator + "one" + File.separator + dirToDelete);
        Path path2 = Paths.get(rootPath.toString() + File.separator + "two" + File.separator + dirToDelete);
        Files.createDirectories(path1);
        Files.createDirectories(path2);
        Files.createFile(Paths.get(path1.toString() + File.separator + "filename1.txt"));
        Files.createFile(Paths.get(path1.toString() + File.separator + "filename1a.txt"));
        Files.createFile(Paths.get(path2.toString() + File.separator + "filename2.txt"));

        Browser browser = new Browser();
        browser.deleteDirectoryInTree(rootPath.toString(), dirToDelete);

        rootPath.toFile().delete();

        assertFalse(path1.toFile().exists());
        assertFalse(path2.toFile().exists());

        Path pathOne = Paths.get(rootPath.toString() + File.separator + "one");
        assertTrue(pathOne.toFile().exists());
        Path pathTwo = Paths.get(rootPath.toString() + File.separator + "two");
        assertTrue(pathTwo.toFile().exists());

        MoreFiles.deleteRecursively(rootPath, RecursiveDeleteOption.ALLOW_INSECURE);
    }

    //@Test
    public void emptyResultWhenNoFiles() throws IOException {
        Path rootPath = Paths.get(Paths.get("").toAbsolutePath().toString() + File.separator + "testdir");
        Path path1 = Paths.get(rootPath.toString() + File.separator + "one");
        Path path2 = Paths.get(rootPath.toString() + File.separator + "two");
        Files.createDirectories(path1);
        Files.createDirectories(path2);

        Browser browser = new Browser();
        Map<String, List<File>> result = browser.groupFilesByName(rootPath.toString(), "*.txt");

        assertTrue(result.isEmpty());

        MoreFiles.deleteRecursively(rootPath, RecursiveDeleteOption.ALLOW_INSECURE);
    }

    @Test
    public void duplicatedFilesInDifferentDirectories() throws IOException {
        Path rootPath = Paths.get(Paths.get("").toAbsolutePath().toString() + File.separator + "testdir");
        Path path1 = Paths.get(rootPath.toString() + File.separator + "one");
        Path path2 = Paths.get(rootPath.toString() + File.separator + "two");
        Files.createDirectories(path1);
        Files.createDirectories(path2);
        String duplicatedFileName = "fileName.txt";
        Files.createFile(Paths.get(path1 + File.separator + duplicatedFileName));
        Files.createFile(Paths.get(path2 + File.separator + duplicatedFileName));
        String uniqueFileName = "unique.txt";
        Files.createFile(Paths.get(path2 + File.separator + uniqueFileName));

        Browser browser = new Browser();
        Map<String, List<File>> result = browser.groupFilesByName(rootPath.toString(), "*.txt");

        assertTrue(result.size() == 2);
        assertTrue(result.containsKey(duplicatedFileName));
        assertTrue(result.get(duplicatedFileName).size() == 2);
        assertTrue(result.containsKey(uniqueFileName));
        assertTrue(result.get(uniqueFileName).size() == 1);

        MoreFiles.deleteRecursively(rootPath, RecursiveDeleteOption.ALLOW_INSECURE);
    }

    @Test
    public void findDuplicatedFilesInDocuments() throws IOException {
        Browser browser = new Browser();
        browser.printDuplicatedFiles("/Users/davidmurillomatallana/Documents", "*.{doc,docx,xls,xlsx,pdf,ppt,pptx,jpg}");
    }

}
