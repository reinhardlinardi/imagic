package com.imagic.imagic;

import android.annotation.SuppressLint;
import android.app.Activity;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MenuActivity extends AppCompatActivity {

    // Menu option
    private class Option implements JSONSerializable {

        // Properties
        public String title;
        public String description;
        public String activityOnClick;

        // Constructors
        Option() {}

        @Override
        public String jsonSerialize() throws Exception {
            JSONObject optionJSON = new JSONObject();

            optionJSON.put("title", title);
            optionJSON.put("description", description);
            optionJSON.put("activityOnClick", activityOnClick);

            return optionJSON.toString();
        }

        @Override
        public void jsonDeserialize(Activity activity, String json) throws Exception {
            JSONObject optionJSON = new JSONObject(json);

            title = optionJSON.getString("title");
            description = optionJSON.getString("description");
            activityOnClick = optionJSON.getString("activityOnClick");
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
                        intent.putExtra("imageData", MenuActivity.imageDataURI.toString());
                        MenuActivity.this.startActivity(intent);
                    }
                    catch(Exception e) {
                        Log.e("Imagic", "Exception", e);
                    }
                }
            };
        }
    }

    // Internal shared cached image data URI
    private static Uri imageDataURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) MenuActivity.imageDataURI = Uri.parse(bundle.getString("imageData"));

        try(BufferedReader reader = new BufferedReader(new InputStreamReader(getResources().openRawResource(R.raw.menu), "UTF8"))) {
            StringBuilder data = new StringBuilder();
            String line;

            while((line = reader.readLine()) != null) data.append(line);
            String json = data.toString();

            ArrayList<MenuActivity.Option> options = new ArrayList<>();
            JSONArray optionList = new JSONArray(json);

            for(int idx = 0; idx < optionList.length(); idx++) {
                MenuActivity.Option option = new Option();
                option.jsonDeserialize(this, optionList.get(idx).toString());
                options.add(option);
            }

            MenuAdapter menuAdapter = new MenuAdapter(options);
            ListView menuListView = findViewById(R.id.menuListView);
            menuListView.setAdapter(menuAdapter);
        }
        catch(Exception e) {
            Log.e("Imagic", "Exception", e);
        }
    }
}
