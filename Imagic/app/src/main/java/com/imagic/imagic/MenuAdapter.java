package com.imagic.imagic;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class MenuAdapter extends ArrayAdapter<MenuOption> {

    // Activity context
    private final Activity context;

    MenuAdapter(Activity context, ArrayList<MenuOption> options) {
        super(context, R.layout.list_menu_option, options);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        @SuppressLint({"ViewHolder", "InflateParams"}) View optionView = inflater.inflate(R.layout.list_menu_option, null, true);

        TextView optionTitleTextView = optionView.findViewById(R.id.menuOptionTitleTextView);
        TextView optionDescriptionTextView = optionView.findViewById(R.id.menuOptionDescriptionTextView);
        MenuOption option = getItem(position);

        if(option != null) {
            optionTitleTextView.setText(option.title);
            optionDescriptionTextView.setText(option.description);

            optionView.setOnClickListener(getOptionOnClickListener(optionTitleTextView));
        }

        return optionView;
    }

    // Option on click listener
    private View.OnClickListener getOptionOnClickListener(final TextView optionTitleTextView) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = optionTitleTextView.getText().toString();

                switch(title) {
                    case "Histogram":
                        break;
                    default:
                        break;
                }
            }
        };
    }
}
