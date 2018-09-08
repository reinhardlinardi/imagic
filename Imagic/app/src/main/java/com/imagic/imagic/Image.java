package com.imagic.imagic;

import android.content.Context;
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

    // Properties
    Uri uri;
    Bitmap bitmap;
    RGBHistogram rgb;
    GrayscaleHistogram grayscale;
    
    // Constructors
    Image() { resetHistogram(); }

    Image(Context context, Uri uri) throws IOException {
        this.uri = uri;
        bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
        resetHistogram();
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
        resetHistogram();

        JSONObject imageJSON = new JSONObject(json);
        uri = Uri.parse(imageJSON.getString("uri"));
        bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);

        rgb.jsonDeserialize(context, imageJSON.getJSONObject("rgb").toString());
        grayscale.jsonDeserialize(context, imageJSON.getJSONObject("grayscale").toString());
    }

    // Reset all histogram
    void resetHistogram() {
        rgb = new RGBHistogram();
        grayscale = new GrayscaleHistogram();
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
    /*
    public void updateBitmap() {
        int[] newRed = redHistogram.getNewColorValueMap();
        int[] newGreen = greenHistogram.getNewColorValueMap();
        int[] newBlue = blueHistogram.getNewColorValueMap();
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Bitmap newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        for(int row = 0; row < height; row++) {
            for(int col = 0; col < width; col++) {
                int pixel = bitmap.getPixel(col, row);
                int alpha = Color.alpha(pixel);
                int red = Color.red(pixel);
                int green = Color.green(pixel);
                int blue = Color.blue(pixel);
                newBitmap.setPixel(col, row, Color.argb(alpha, newRed[red], newGreen[green], newBlue[blue]));
            }
        }

        bitmap = newBitmap;
    }
    */
}
