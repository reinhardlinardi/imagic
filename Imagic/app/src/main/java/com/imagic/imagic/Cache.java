package com.imagic.imagic;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.io.InputStream;

class Cache {

    // Constants
    static final Uri NO_CACHE_URI = Uri.parse("");
    static final String INTENT_BUNDLE_NAME = "cachedImageDataURI";

    // Check if cache available
    private static boolean isCacheAvailable(Uri uri) { return uri != NO_CACHE_URI; }

    // Check if cache exists
    private static boolean isCacheExists(Uri uri) {
        File file = getCacheFile(uri);
        return file.isFile() && file.exists();
    }

    // Get cache file
    private static File getCacheFile(Uri uri) { return new File(uri.getPath()); }

    // Create cache
    static Uri create(Context appContext, String filename, String extension, boolean shareWithExternal) throws Exception {
        if(shareWithExternal) {
            File file = File.createTempFile(filename, "." + extension, appContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES));
            return FileProvider.getUriForFile(appContext, appContext.getPackageName() + ".provider", file);
        }
        else {
            File file = File.createTempFile(filename, "." + extension, appContext.getCacheDir());
            return Uri.fromFile(file);
        }
    }

    // Delete cache
    private static void delete(Uri uri) { getCacheFile(uri).delete(); }

    // Delete old cache
    static void deleteOldCache(Uri uri) { if(isCacheAvailable(uri) && isCacheExists(uri)) delete(uri); }

    // Read cache
    static String read(Uri uri) throws Exception { return Text.readFile(uri); }

    // Write cache
    static void write(Uri uri, String content) throws Exception { Text.writeFile(uri, content); }

    // Open input stream
    static InputStream openInputStream(Context context, Uri uri) throws Exception { return context.getContentResolver().openInputStream(uri); }
}
