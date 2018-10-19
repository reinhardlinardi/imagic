package com.imagic.imagic;

import android.content.Context;
import android.net.Uri;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;

/**
 * A helper class for reading and writing UTF-8 text files.
 */
class TextFile {

    /* Constants */

    // Default charset (UTF-8) for text files
    private static final String DEFAULT_CHARSET = "UTF8";

    // Read each line using BufferedReader's readLine and return all lines as a string
    private static String readAllLines(BufferedReader reader) throws Exception {
        StringBuilder allLines = new StringBuilder();
        String line;

        while((line = reader.readLine()) != null) allLines.append(line);
        return allLines.toString();
    }

    // Read content of a file given by its URI
    static String readFile(Uri uri) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(new File(uri.getPath())));
        String result = readAllLines(reader);

        reader.close();
        return result;
    }

    // Read content of a raw resource file given by its resource ID (any file inside res/raw folder)
    static String readRawResourceFile(Context context, int rawResourceID) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(rawResourceID), TextFile.DEFAULT_CHARSET));
        String result = readAllLines(reader);

        reader.close();
        return result;
    }

    // Write content to a file given by its URI
    static void writeFile(Uri uri, String content) throws Exception {
        BufferedWriter writer = new BufferedWriter(new FileWriter(new File(uri.getPath())));
        writer.write(content);
        writer.close();
    }
}