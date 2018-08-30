package com.imagic.imagic;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class MainMenuAdapter extends ArrayAdapter<MainMenuOption> {

    // Activity context
    private final Activity context;

    MainMenuAdapter(Activity context, ArrayList<MainMenuOption> options) {
        super(context, R.layout.listview_mainmenu_option, options);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        @SuppressLint({"ViewHolder", "InflateParams"}) View optionView = inflater.inflate(R.layout.listview_mainmenu_option, null, true);

        final TextView optionTitleTextView = optionView.findViewById(R.id.mainMenuOptionTitleTextView);
        TextView optionDescriptionTextView = optionView.findViewById(R.id.mainMenuOptionDescriptionTextView);

        MainMenuOption option = getItem(position);

        if(option != null) {
            optionTitleTextView.setText(option.title);
            optionDescriptionTextView.setText(option.description);

            optionView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String title = optionTitleTextView.getText().toString();

                    switch(title) {
                        case "Histogram":
                            Intent intent = new Intent(context, HistogramActivity.class);
                            context.startActivity(intent);
                            break;
                        default:
                            break;
                    }
                }
            });
        }

        return optionView;
    }
}
