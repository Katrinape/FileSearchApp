package com.kpcompany;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileSearchApp {

    private String path;
    private String regex;
    private String zipFileName;
    private Pattern pattern;
    private List<File> zipFiles = new ArrayList<>();

    public void walkDirectory(String path) throws IOException {
        walkDirectoryJava8(path);
        zipFilesJava7();
    }

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
        if (getZipFileName() != null) zipFiles.add(file);
    }

    public void zipFilesJava6() throws IOException {
        ZipOutputStream out = null;
        try {
            out = new ZipOutputStream(new FileOutputStream(getZipFileName()));
            File baseDir = new File(getPath());

            for (File file: zipFiles) {
                // fileName must be a relative path
                String fileName = getRelativeFilename(file, baseDir);

                ZipEntry zipEntry = new ZipEntry(fileName);
                zipEntry.setTime(file.lastModified());
                out.putNextEntry(zipEntry);

                int bufferSize = 2048;
                byte[] buffer = new byte[bufferSize];
                int len = 0;
                BufferedInputStream in = new BufferedInputStream(new FileInputStream(file), bufferSize);
                while ((len = in.read(buffer, 0, bufferSize)) != -1) {
                    out.write(buffer,0, len);
                }
                in.close();
                out.closeEntry();
            }
        } finally {
            out.close();
        }
    }

    public void zipFilesJava7() throws IOException {
        try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(getZipFileName()))) {
            File baseDir = new File(getPath());
            for (File file: zipFiles) {
                // fileName must be a reletive path, not an absolute one.
                String fileName = getRelativeFilename(file, baseDir);

                ZipEntry zipEntry = new ZipEntry(fileName);
                zipEntry.setTime(file.lastModified());
                out.putNextEntry(zipEntry);

                Files.copy(file.toPath(), out);
                out.closeEntry();
            }
        }
    }

    public String getRelativeFilename(File file, File baseDir) {
        String fileName = file.getAbsolutePath().substring(baseDir.getAbsolutePath().length());

        // IMPOTRANT: the ZipEntry file name must be use "/",  not "\".
        fileName = fileName.replace('\\', '/');
        while (fileName.startsWith("/")) {
            fileName = fileName.substring(1);
        }
        return fileName;
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
