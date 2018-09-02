package com.imagic.imagic;

import android.app.ActionBar;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.InputStream;
import java.util.ArrayList;

public class MenuActivity extends AppCompatActivity {

    // Selected or captured image URI
    private static Uri imageURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        ActionBar actionBar = getActionBar();
        if(actionBar != null) actionBar.setHomeButtonEnabled(true);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) imageURI = Uri.parse(bundle.getString("image"));

        try {
            InputStream stream = getResources().openRawResource(R.raw.menu);
            String json = Text.readAllLines(stream);
            ArrayList<MenuOption> options = Text.deserializeJson(json);

            Log.d("Options", options.toString());

            stream.close();
        }
        catch(Exception e) {
            Log.e("Imagic", "Exception", e);
        }
    }
}
