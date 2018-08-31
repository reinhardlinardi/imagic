package com.imagic.imagic;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    // Main menu options
    private static final ArrayList<MainMenuOption> options = new ArrayList<>(Arrays.asList(
        new MainMenuOption("Histogram", "Show RGB and Grayscale histogram from an image")
    ));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MainMenuAdapter adapter = new MainMenuAdapter(this, MainActivity.options);
        ListView mainMenu = findViewById(R.id.mainMenuListView);
        mainMenu.setAdapter(adapter);
    }
}
