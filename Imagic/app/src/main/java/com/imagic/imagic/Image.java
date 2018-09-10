package com.imagic.imagic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

class Image implements JSONSerializable {

    // Color types
    enum ColorType {
        RED(0), GREEN(1), BLUE(2), GRAYSCALE(3);

        int value;
        ColorType(int value) { this.value = value; }
    }

    // Constants
    static final String MIME_TYPE = "image/*";

    // Properties
    Uri uri;
    Bitmap bitmap;
    RGBHistogram rgb;
    GrayscaleHistogram grayscale;
    
    // Constructors
    Image() { resetData(); }

    Image(Context context, Uri uri) throws Exception {
        resetData();
        this.uri = uri;
    }

    Image(Context context, Image image, boolean reloadBitmap) throws Exception {
        uri = image.uri;
        recycleBitmap();

        if(reloadBitmap) bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
        else bitmap = image.bitmap;

        rgb = new RGBHistogram(image.rgb);
        grayscale = new GrayscaleHistogram(image.grayscale);
    }

    @Override
    public String jsonSerialize() throws Exception {
        JSONObject imageJSON = new JSONObject();

        imageJSON.put("uri", uri.toString());
        imageJSON.put("rgb", new JSONObject(rgb.jsonSerialize()));
        imageJSON.put("grayscale", new JSONObject(grayscale.jsonSerialize()));

        return imageJSON.toString();
    }

    @Override
    public void jsonDeserialize(Context context, String json) throws Exception {
        resetData();

        JSONObject imageJSON = new JSONObject(json);
        uri = Uri.parse(imageJSON.getString("uri"));

        recycleBitmap();
        bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);

        rgb.jsonDeserialize(context, imageJSON.getJSONObject("rgb").toString());
        grayscale.jsonDeserialize(context, imageJSON.getJSONObject("grayscale").toString());
    }

    // Reset all histogram
    private void resetData() {
        uri = Cache.NO_CACHE_URI;
        recycleBitmap();

        rgb = new RGBHistogram();
        grayscale = new GrayscaleHistogram();
    }

    // Recycle bitmap
    private void recycleBitmap() {
        if(bitmap != null) {
            if(!bitmap.isRecycled()) bitmap.recycle();
        }
    }

    // Get rotation matrix
    private Matrix getRotationMatrix(Context context) throws Exception {
        Matrix matrix = new Matrix();
        ExifInterface exif = new ExifInterface(Cache.openAsInputStream(context, uri));

        int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        int degree;

        switch (rotation) {
            case ExifInterface.ORIENTATION_ROTATE_90: degree = 90; break;
            case ExifInterface.ORIENTATION_ROTATE_180: degree = 180; break;
            case ExifInterface.ORIENTATION_ROTATE_270: degree = 270 ; break;
            default: degree = 0; break;
        }

        if(rotation != 0f) matrix.preRotate(degree);
        return matrix;
    }

    // Update bitmap
    public void updateBitmap(Context context, int[] newRedValue, int[] newGreenValue, int[] newBlueValue) throws Exception {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        for(int row = 0; row < height; row++) {
            for(int col = 0; col < width; col++) {
                int pixel = pixels[row * width + col];
                pixels[row * width + col] = Color.rgb(newRedValue[Color.red(pixel)], newGreenValue[Color.green(pixel)], newBlueValue[Color.blue(pixel)]);
            }
        }

        Bitmap newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        newBitmap.setPixels(pixels, 0, width, 0, 0, width, height);

        if(Build.VERSION.SDK_INT >= 24) {
            Bitmap rotatedBitmap = Bitmap.createBitmap(newBitmap, 0, 0, width, height, getRotationMatrix(context), true);
            bitmap = rotatedBitmap;
        }
        else bitmap = newBitmap;
    }

    // Generate histogram
    void generateHistogramByColorType(Image.ColorType colorType) {
        int[] valueCount = new int[256];
        Arrays.fill(valueCount, 0);

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        for(int row = 0; row < height; row++) {
            for(int col = 0; col < width; col++) {
                int pixel = pixels[row * width + col];

                switch(colorType) {
                    case RED: valueCount[Color.red(pixel)]++; break;
                    case GREEN: valueCount[Color.green(pixel)]++; break;
                    case BLUE: valueCount[Color.blue(pixel)]++; break;
                    case GRAYSCALE: valueCount[(Color.red(pixel) + Color.green(pixel) + Color.blue(pixel)) / 3]++; break;
                    default: break;
                }
            }
        }

        for(int val = 0; val < 256; val++) {
            switch(colorType) {
                case RED: rgb.red.addDataPoint(val, valueCount[val]); break;
                case GREEN: rgb.green.addDataPoint(val, valueCount[val]); break;
                case BLUE: rgb.blue.addDataPoint(val, valueCount[val]); break;
                case GRAYSCALE: grayscale.addDataPoint(val, valueCount[val]); break;
                default: break;
            }
        }

        switch(colorType) {
            case RED: rgb.red.updateSeries(); break;
            case GREEN: rgb.green.updateSeries(); break;
            case BLUE: rgb.blue.updateSeries(); break;
            case GRAYSCALE: grayscale.updateSeries(); break;
            default: break;
        }
    }
}
