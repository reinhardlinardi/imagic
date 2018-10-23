package com.imagic.imagic;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

/**
 * A helper class for manipulating UI components.
 */
class UI {

    /* Methods */

    // Show
    static void show(View view) { view.setVisibility(View.VISIBLE); }

    // Hide (component does not affect layout calculation), usually for graph view when no data available
    static void hide(View view) { view.setVisibility(View.GONE); }

    // Set invisible (component affects layout calculation), usually for progress bar
    static void setInvisible(View view) { view.setVisibility(View.INVISIBLE); }

    // Test visibility
    static boolean isVisible(View view) { return view.getVisibility() == View.VISIBLE; }

    // Enable
    static void enable(View view) { view.setEnabled(true); }

    // Disable
    static void disable(View view) { view.setEnabled(false); }

    // Set clickable, usually for ImageView when no async tasks are running
    static void setClickable(View view) { view.setClickable(true); }

    // Set unclickable, usually for ImageView when async tasks are running
    static void setUnclickable(View view) { view.setClickable(false); }

    // Set image view with image from given URI using Glide
    static void setImageView(Context context, ImageView view, Uri uri) { Glide.with(context).load(uri).into(view); }

    // Set image view with bitmap using Glide
    static void setImageView(Context context, ImageView view, Bitmap bitmap) { Glide.with(context).load(bitmap).into(view); }

    // Clear Glide memory
    static void clearMemory(Context context) { Glide.get(context).clearMemory(); }

    // Set graph view with given BarGraphSeries
    static void setGraphView(GraphView view, BarGraphSeries<DataPoint> series) {
        view.removeAllSeries();
        view.addSeries(series);
    }

    // Set graph view X axis boundary
    static void setGraphViewXAxisBoundary(GraphView view, double min, double max) {
        Viewport viewport = view.getViewport();

        viewport.setMinX(min);
        viewport.setMaxX(max);
        viewport.setXAxisBoundsManual(true);
    }

    // Set graph view Y axis boundary
    static void setGraphViewYAxisBoundary(GraphView view, double min, double max) {
        Viewport viewport = view.getViewport();

        viewport.setMinY(min);
        viewport.setMaxY(max);
        viewport.setYAxisBoundsManual(true);
    }
}
