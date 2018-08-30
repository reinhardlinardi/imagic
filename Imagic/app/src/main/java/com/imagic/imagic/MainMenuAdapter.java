package com.imagic.imagic;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class MainMenuAdapter extends ArrayAdapter {

    private final Activity context;

    public MainMenuAdapter(Activity context, ArrayList<MainMenuOption> options) {
        super(context, R.layout.listview_mainmenu_option, options);
        this.context = context;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View optionView = inflater.inflate(R.layout.listview_mainmenu_option, null, true);

        LinearLayout optionButton = (LinearLayout) optionView.findViewById(R.id.menu);
        final TextView optionTitleTextView = (TextView) optionView.findViewById(R.id.mainMenuOptionTitleTextView);
        TextView optionDescriptionTextView = (TextView) optionView.findViewById(R.id.mainMenuOptionDescriptionTextView);

        MainMenuOption option = (MainMenuOption) getItem(position);
        optionTitleTextView.setText(option.title);
        optionDescriptionTextView.setText(option.description);

        optionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (optionTitleTextView.getText().equals("Histogram")) {
                    Intent intent = new Intent(context, HistogramActivity.class);
                    context.startActivity(intent);
                }
            }
        });
        return optionView;
    }
}
