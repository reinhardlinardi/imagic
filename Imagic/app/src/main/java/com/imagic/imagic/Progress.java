package com.imagic.imagic;

class Progress {

    // Count progress
    static int countProgess(int numTaskDone, int totalNumTask) {
        float taskDoneFraction = (float)numTaskDone / totalNumTask;
        return (int)(taskDoneFraction * 100);
    }

    // Get progess
    static int getProgess(Integer... allProgress) {
        return allProgress[0];
    }
}
