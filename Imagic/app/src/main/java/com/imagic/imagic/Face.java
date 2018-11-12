package com.imagic.imagic;


import android.graphics.Point;

public class Face {
    public static final int UPPER = 0;
    public static final int LOWER = 1;
    public static final int LEFT = 2;
    public static final int RIGHT = 3;

    private Point[] faceBorder;

    public Face(){
        faceBorder = new Point[4];
    }

    public void setBorder(Point upper, Point lower, Point left, Point right){
        faceBorder[UPPER] = upper;
        faceBorder[LOWER] = lower;
        faceBorder[LEFT] = left;
        faceBorder[RIGHT] = right;
    }
}
