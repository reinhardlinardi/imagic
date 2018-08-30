package com.imagic.imagic;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.io.IOException;

public class HistogramActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 100;
    private ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_histogram);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        iv = (ImageView) findViewById(R.id.iv);
    }

    public void pick(View v) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode){
            case PICK_IMAGE_REQUEST:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = data.getData();

                    // method 1
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                        iv.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // method 2

                    //try {
                    //    InputStream imageStream = getContentResolver().openInputStream(selectedImage);
                    //    Bitmap yourSelectedImage = BitmapFactory.decodeStream(imageStream);
                    //    imageStream.close(;
                    //   iv.setImageBitmap(yourSelectedImage);
                    //} catch (FileNotFoundException e) {
                    //    e.printStackTrace();
                    //}

                    // method 3
                    // iv.setImageURI(selectedImage);
                }
                break;
        }
    }
}
