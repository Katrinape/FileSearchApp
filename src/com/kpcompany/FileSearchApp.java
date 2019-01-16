package com.kpcompany;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class FileSearchApp {

    private String path;
    private String regex;
    private String zipFileName;
    private Pattern pattern;

    public void walkDirectoryJava6(String path) throws IOException {
        File dir = new File(path);
        File[] files = dir.listFiles();

        for (File file: files) {
            if (file.isDirectory()) {
                walkDirectoryJava6(file.getAbsolutePath());
            } else {
                processFile(file);
            }
        }
    }

    public void walkDirectoryJava7(String path) throws IOException {
        Files.walkFileTree(Paths.get(path), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                processFile(file.toFile());
                return FileVisitResult.CONTINUE;
            }
        });
    }

    public void walkDirectoryJava8(String path) throws IOException {
        Files.walk(Paths.get(path)).forEach(f -> processFile(f.toFile()));
    }

    private void processFile(File file) {
        try {
            if (searchFile(file)) {
                addFileToZip(file);
            }
        } catch (IOException|UncheckedIOException e) {
            System.out.println("Error processing file: " + file + ": " + e);
        }
    }

    public boolean searchFile(File file) throws IOException {
        return searchFileJava8(file);
    }

    public boolean searchFileJava6(File file) throws FileNotFoundException {
        boolean found = false;
        Scanner scanner = new Scanner(file, "UTF-8");
        while (scanner.hasNextLine()) {
            found = searchText(scanner.nextLine());
            if (found) break;
        }
        scanner.close();
        return found;
    }

    public boolean searchFileJava7(File file) throws IOException {
        List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
        for (String line: lines) {
            if (searchText(line)) return true;
        }
        return false;
    }

    public boolean searchFileJava8(File file) throws IOException {
        return Files.lines(file.toPath(), StandardCharsets.UTF_8).anyMatch(t -> searchText(t));
    }

    public boolean searchText(String text) {
        return (this.getRegex() == null) ? true : this.pattern.matcher(text).matches();
    }

    private void addFileToZip(File file) {
        System.out.println("addFileToZip: " + file);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
        this.pattern = Pattern.compile(regex);
    }

    public String getZipFileName() {
        return zipFileName;
    }

    public void setZipFileName(String zipFileName) {
        this.zipFileName = zipFileName;
    }
}
