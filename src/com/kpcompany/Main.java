package com.kpcompany;

import java.io.IOException;

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

        try {
            app.walkDirectoryJava8(app.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
