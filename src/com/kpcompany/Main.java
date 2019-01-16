package com.kpcompany;

public class Main {

    public static void main(String[] args) {

        FileSearchApp app = new FileSearchApp();

        switch (Math.min(args.length, 3)) {
            case 0:
                System.out.println("USAGE: FileSearchApp path [regex] [zipfile]");
                return;
            case 3: app.setZipFileName(args[2]);
            case 2: app.setRegex(args[1]);
            case 1: app.setPath(args[0]);
        }

        app.walkDirectory(app.getPath());
        System.out.println(app.getPath());
        System.out.println(app.getRegex());
        System.out.println(app.getZipFileName());
    }
}
