package com.imagic.imagic;

import android.app.ActionBar;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class MenuActivity extends AppCompatActivity {

    // Data source charset
    private static final String DATA_SOURCE_CHARSET = "UTF8";

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

        try(BufferedReader reader = new BufferedReader(new InputStreamReader(getResources().openRawResource(R.raw.menu), MenuActivity.DATA_SOURCE_CHARSET))) {
            ArrayList<MenuOption> options = loadDataSource(reader);
            MenuAdapter menuAdapter = new MenuAdapter(this, options);
            ListView menuListView = findViewById(R.id.menuListView);
            menuListView.setAdapter(menuAdapter);
        }
        catch(Exception e) {
            Log.e("Imagic", "Exception", e);
        }
    }

    // Load data from JSON data source
    private ArrayList<MenuOption> loadDataSource(BufferedReader reader) throws IOException {
        StringBuilder data = new StringBuilder();
        String line;

        while((line = reader.readLine()) != null) data.append(line);
        String json = data.toString();

        Type type = new TypeToken<ArrayList<MenuOption>>(){}.getType();
        return new Gson().fromJson(json, type);
    }
}
