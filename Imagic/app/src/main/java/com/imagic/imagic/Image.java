package com.imagic.imagic;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;

import org.json.JSONObject;

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
    static final int NUM_COLOR_VALUES = 256;

    // Properties
    Uri uri;
    Bitmap bitmap;
    RedHistogram redHistogram;
    GreenHistogram greenHistogram;
    BlueHistogram blueHistogram;
    GrayscaleHistogram grayscaleHistogram;
    
    // Constructors
    Image() {}

    Image(Activity activity, Uri uri) throws IOException {
        this.uri = uri;
        bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), uri);
        redHistogram = null;
        greenHistogram = null;
        blueHistogram = null;
        grayscaleHistogram = null;
    }

    @Override
    public String jsonSerialize() throws Exception {
        JSONObject imageJSON = new JSONObject();

        imageJSON.put("uri", uri.toString());
        imageJSON.put("redHistogram", (redHistogram == null)? null : redHistogram.jsonSerialize());
        imageJSON.put("greenHistogram", (greenHistogram == null)? null : greenHistogram.jsonSerialize());
        imageJSON.put("blueHistogram", (blueHistogram == null)? null : blueHistogram.jsonSerialize());
        imageJSON.put("grayscaleHistogram", (grayscaleHistogram == null)? null : grayscaleHistogram.jsonSerialize());

        return imageJSON.toString();
    }

    @Override
    public void jsonDeserialize(Activity activity, String json) throws Exception {
        JSONObject imageJSON = new JSONObject(json);

        uri = Uri.parse(imageJSON.getString("uri"));
        bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), uri);
        redHistogram = null;
        greenHistogram = null;
        blueHistogram = null;
        grayscaleHistogram = null;

        if(!imageJSON.isNull("redHistogram")) redHistogram.jsonDeserialize(activity, imageJSON.get("redHistogram").toString());
        if(!imageJSON.isNull("greenHistogram")) greenHistogram.jsonDeserialize(activity, imageJSON.get("greenHistogram").toString());
        if(!imageJSON.isNull("blueHistogram")) blueHistogram.jsonDeserialize(activity, imageJSON.get("blueHistogram").toString());
        if(!imageJSON.isNull("grayscaleHistogram")) grayscaleHistogram.jsonDeserialize(activity, imageJSON.get("grayscaleHistogram").toString());
    }

    // Generate histogram
    void generateHistogramByColorType(Image.ColorType colorType) {
        int[] valueCount = new int[Image.NUM_COLOR_VALUES];
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

        for(int val = 0; val < Image.NUM_COLOR_VALUES; val++) {
            switch(colorType) {
                case RED: redHistogram.addDataPoint(val, valueCount[val]); break;
                case GREEN: greenHistogram.addDataPoint(val, valueCount[val]); break;
                case BLUE: blueHistogram.addDataPoint(val, valueCount[val]); break;
                case GRAYSCALE: grayscaleHistogram.addDataPoint(val, valueCount[val]); break;
                default: break;
            }
        }

        switch(colorType) {
            case RED: redHistogram.setSeriesDataPoints(); break;
            case GREEN: greenHistogram.setSeriesDataPoints(); break;
            case BLUE: blueHistogram.setSeriesDataPoints(); break;
            case GRAYSCALE: grayscaleHistogram.setSeriesDataPoints(); break;
            default: break;
        }
    }
}
