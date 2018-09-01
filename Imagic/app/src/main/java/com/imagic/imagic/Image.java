package com.imagic.imagic;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Image {

    // Image MIME type
    public static final String MIME_TYPE = "image/*";

    // Image extension
    public static final String EXTENSION = "jpg";

    // Error messages
    public static final String CREATE_IMAGE_ERROR_MSG = "Cannot save captured image.\nPlease try again later";

    // Get file provider URI
    public static Uri getFileProviderURI(Activity context, File file) {
        return FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);
    }

    // Create new image
    public static File createImage(Activity context) throws IOException {
        @SuppressLint("SimpleDateFormat") String filename = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File storagePath = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(filename, Image.EXTENSION, storagePath);
    }
}
