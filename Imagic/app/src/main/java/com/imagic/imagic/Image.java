package com.imagic.imagic;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class Image {

    // Image MIME type
    public static final String MIME_TYPE = "image/*";

    // Image extension
    private static final String EXTENSION = "jpg";

    // Max possible colors
    public static final int MAX_POSSIBLE_COLORS = 256;

    // Color
    public static final int COLOR_RED = 0;
    public static final int COLOR_GREEN = 1;
    public static final int COLOR_BLUE = 2;
    public static final int COLOR_GRAYSCALE = 3;

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

    // Get color count
    public static int[] getColorCount(Bitmap bitmap, int color) {
        int[] count = new int[Image.MAX_POSSIBLE_COLORS];
        Arrays.fill(count, 0);

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int[] pixels = new int[height * width];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        for(int row = 0; row < height; row++) {
            for(int col = 0; col < width; col++) {
                int pixel = pixels[row * width + col];

                switch(color) {
                    case Image.COLOR_RED:
                        count[Color.red(pixel)]++;
                        break;
                    case Image.COLOR_GREEN:
                        count[Color.green(pixel)]++;
                        break;
                    case Image.COLOR_BLUE:
                        count[Color.blue(pixel)]++;
                        break;
                    case Image.COLOR_GRAYSCALE:
                        count[(Color.red(pixel) + Color.green(pixel) + Color.blue(pixel)) / 3]++;
                        break;
                    default:
                        break;
                }
            }
        }

        return count;
    }
}
