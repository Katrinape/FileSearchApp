package com.kpcompany;

import java.io.File;

public class FileSearchApp {

    private String path;
    private String regex;
    private String zipFileName;

    public void walkDirectory(String path) {
        System.out.println("walkDirectory: " + path);
        searchFile(null);
        addFileToZip(null);
    }

    private void searchFile(File file) {
        System.out.println("searchFile: " + file);
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
    }

    public String getZipFileName() {
        return zipFileName;
    }

    public void setZipFileName(String zipFileName) {
        this.zipFileName = zipFileName;
    }
}
