package com.imagic.imagic;

import android.app.ActionBar;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class HistogramActivity extends AppCompatActivity {

    // Selected or captured image URI
    private static Uri imageURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_histogram);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) HistogramActivity.imageURI = Uri.parse(bundle.getString("image"));
    }
}
