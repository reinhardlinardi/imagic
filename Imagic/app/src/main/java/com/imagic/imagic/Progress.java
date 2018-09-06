package com.imagic.imagic;

import android.app.Activity;
import android.view.View;
import android.widget.ProgressBar;

class Progress {

    // Properties
    public int viewID;
    public ProgressBar view;
    public int progress;

    // Constructor
    Progress(Activity activity, int viewID) {
        this.viewID = viewID;
        view = activity.findViewById(viewID);
    }

    // Show progress bar
    void show() { view.setVisibility(View.VISIBLE); }

    // Hide progress bar
    void hide() { view.setVisibility(View.GONE); }

    // Count progress
    static int countProgess(int numTaskDone, int totalNumTask) {
        float taskDoneFraction = (float)numTaskDone / totalNumTask;
        return (int)(taskDoneFraction * 100);
    }

    // Set progess
    void setProgess(Integer... allProgress) {
        progress = allProgress[0];
        view.setProgress(progress);
    }
}
