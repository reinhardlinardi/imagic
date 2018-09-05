package com.imagic.imagic;

import android.app.Activity;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.io.IOException;

public class Cache {

    // Create new file
    public static File create(Activity context, String filename, String extension, boolean sharedWithExternal) throws IOException {
        File filePath;

        if(sharedWithExternal) filePath = context.getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        else filePath = context.getApplicationContext().getCacheDir();

        return File.createTempFile(filename, extension, filePath);
    }

    // Delete file
    public static boolean delete(File file) {
        return file.delete();
    }

    // Get file provider URI
    public static Uri getFileProviderURI(Activity context, File file) {
        return FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);
    }
}
