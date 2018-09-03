package com.imagic.imagic;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class MenuActivity extends AppCompatActivity {

    // Menu option
    private class Option {

        // Properties
        public String title;
        public String description;
        public String activityOnClick;

        Option(String title, String description, String activityOnClick) {
            this.title = title;
            this.description = description;
            this.activityOnClick = activityOnClick;
        }
    }

    // Menu Adapter
    private class MenuAdapter extends ArrayAdapter<MenuActivity.Option> {

        MenuAdapter(ArrayList<MenuActivity.Option> options) {
            super(MenuActivity.this, R.layout.list_menu_option, options);
        }

        @NonNull
        @Override
        public View getView(int position, View view, @NonNull ViewGroup parent) {
            LayoutInflater inflater = MenuActivity.this.getLayoutInflater();
            @SuppressLint({"ViewHolder", "InflateParams"}) View optionView = inflater.inflate(R.layout.list_menu_option, null, true);

            TextView optionTitleTextView = optionView.findViewById(R.id.menuOptionTitleTextView);
            TextView optionDescriptionTextView = optionView.findViewById(R.id.menuOptionDescriptionTextView);
            MenuActivity.Option option = getItem(position);

            if(option != null) {
                optionTitleTextView.setText(option.title);
                optionDescriptionTextView.setText(option.description);
                optionView.setOnClickListener(getOptionOnClickListener(option.activityOnClick));
            }

            return optionView;
        }

        // Option on click listener
        private View.OnClickListener getOptionOnClickListener(final String activityOnClick) {
            return new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Intent intent = new Intent(MenuActivity.this, Class.forName(MenuActivity.this.getApplicationContext().getPackageName() + "." + activityOnClick + "Activity"));
                        intent.putExtra("image", MenuActivity.imageURI.toString());
                        MenuActivity.this.startActivity(intent);
                    }
                    catch(Exception e) {
                        Log.e("Imagic", "Exception", e);
                    }
                }
            };
        }
    }

    // Data source charset
    private static final String DATA_SOURCE_CHARSET = "UTF8";

    // Selected or captured image URI
    private static Uri imageURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) MenuActivity.imageURI = Uri.parse(bundle.getString("image"));

        try(BufferedReader reader = new BufferedReader(new InputStreamReader(getResources().openRawResource(R.raw.menu), MenuActivity.DATA_SOURCE_CHARSET))) {
            ArrayList<MenuActivity.Option> options = loadDataSource(reader);
            MenuAdapter menuAdapter = new MenuAdapter(options);
            ListView menuListView = findViewById(R.id.menuListView);
            menuListView.setAdapter(menuAdapter);
        }
        catch(Exception e) {
            Log.e("Imagic", "Exception", e);
        }
    }

    // Load data from JSON data source
    private ArrayList<MenuActivity.Option> loadDataSource(BufferedReader reader) throws IOException {
        StringBuilder data = new StringBuilder();
        String line;

        while((line = reader.readLine()) != null) data.append(line);
        String json = data.toString();

        Type type = new TypeToken<ArrayList<MenuActivity.Option>>(){}.getType();
        return new Gson().fromJson(json, type);
    }
}
