package com.imagic.imagic;

import android.content.Context;
import android.net.Uri;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;

class Text {

    // Constants
    private static final String DEFAULT_CHARSET = "UTF8";

    // Read all lines
    private static String readAllLines(BufferedReader reader) throws Exception {
        StringBuilder allLines = new StringBuilder();
        String line;

        while((line = reader.readLine()) != null) allLines.append(line);
        return allLines.toString();
    }

    // Read file
    static String readFile(Uri uri) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(new File(uri.getPath())));
        String result = readAllLines(reader);

        reader.close();
        return result;
    }

    // Read raw resource file
    static String readRawResource(Context context, int rawResourceID) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(rawResourceID), Text.DEFAULT_CHARSET));
        String result = readAllLines(reader);

        reader.close();
        return result;
    }

    // Write file
    static void writeFile(Uri uri, String content) throws Exception {
        BufferedWriter writer = new BufferedWriter(new FileWriter(new File(uri.getPath())));
        writer.write(content);
        writer.close();
    }
}
