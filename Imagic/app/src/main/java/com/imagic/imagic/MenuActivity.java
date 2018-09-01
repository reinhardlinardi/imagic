package com.imagic.imagic;

import android.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        ActionBar actionBar = getActionBar();
        if(actionBar != null) actionBar.setHomeButtonEnabled(true);

        Bundle bundle = getIntent().getExtras();

        if(bundle != null) {
            Log.d("Extra", bundle.getString("image"));
        }
    }
}
