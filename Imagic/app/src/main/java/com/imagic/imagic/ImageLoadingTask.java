/*
package com.imagic.imagic;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

public class ImageLoadingTask extends AsyncTask<Bitmap, Bitmap, Void> {
    private ImageView imageView;
    private Bitmap bitmap;

    public ImageLoadingTask(ImageView iv) {
        imageView = iv;
    }

    @Override
    protected Void doInBackground(Bitmap... params) {
        bitmap = params[0];
        return null;
    }

    protected void onPostExecute(Void result) {
        imageView.setImageBitmap(bitmap);
    }
}
*/