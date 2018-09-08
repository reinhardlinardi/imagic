package com.imagic.imagic;

import android.app.Activity;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

class UI {

    // Show
    static void show(View view) { view.setVisibility(View.VISIBLE); }

    // Hide
    static void hide(View view) { view.setVisibility(View.INVISIBLE); }

    // Vanish
    static void vanish(View view) { view.setVisibility(View.GONE); }

    // Update image view using Glide
    static void updateImageView(Activity activity, Uri uri, ImageView view) { Glide.with(activity).load(uri).into(view); }

    // Clear Glide memory
    static void clearImageViewMemory(Activity activity) { Glide.get(activity).clearMemory(); }

    // Show all X values in graph view
    static void showAllXGraphView(GraphView view) {
        view.getViewport().setMinX(0);
        view.getViewport().setMaxX(255);
        view.getViewport().setXAxisBoundsManual(true);
    }

    // Render graph view
    static void renderGraphView(GraphView view, BarGraphSeries<DataPoint> series) {
        view.removeAllSeries();
        view.addSeries(series);
    }
}
