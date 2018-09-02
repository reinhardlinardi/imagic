package com.imagic.imagic;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;

public class Text {

    // Default character encoding
    private static final String DEFAULT_ENCODING = "UTF8";

    // Read all lines
    public static String readAllLines(InputStream stream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream, Text.DEFAULT_ENCODING));

        StringBuilder data = new StringBuilder();
        String line;

        while((line = reader.readLine()) != null) {
            data.append(line);
        }

        return data.toString();
    }

    // Deserialize JSON data
    public static <T> T deserializeJson(String data) {
        Type type = new TypeToken<T>(){}.getType();
        return new Gson().fromJson(data, type);
    }
}
