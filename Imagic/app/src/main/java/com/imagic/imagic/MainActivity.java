package com.imagic.imagic;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView mainMenu;
    private final ArrayList<MainMenuOption> options = new ArrayList<MainMenuOption>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getOptions();
        MainMenuAdapter adapter = new MainMenuAdapter(this, options);

        mainMenu = (ListView) findViewById(R.id.mainMenuListView);
        mainMenu.setAdapter(adapter);
    }

    private void getOptions() {
        options.add(new MainMenuOption("Histogram", "Show RGB and Grayscale histogram from an image"));
    }
}
