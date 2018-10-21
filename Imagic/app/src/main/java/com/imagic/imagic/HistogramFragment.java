package com.imagic.imagic;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.jjoe64.graphview.GraphView;

public class HistogramFragment extends Fragment {

    /* Properties */

    // Communicator variable
    private FragmentListener activity;

    // UI components
    private ProgressBar progressBar;
    private ImageView imageView;

    private GraphView redGraphView;
    private GraphView greenGraphView;
    private GraphView blueGraphView;
    private GraphView grayscaleGraphView;

    /* Lifecycles */

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (FragmentListener) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) { return inflater.inflate(R.layout.fragment_histogram, container, false); }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        progressBar = view.findViewById(R.id.histogramProgressBar);
        UI.setInvisible(progressBar);

        redGraphView = view.findViewById(R.id.histogramRedGraphView);
        greenGraphView = view.findViewById(R.id.histogramGreenGraphView);
        blueGraphView = view.findViewById(R.id.histogramBlueGraphView);
        grayscaleGraphView = view.findViewById(R.id.histogramGrayscaleGraphView);

        UI.hide(redGraphView);
        UI.hide(greenGraphView);
        UI.hide(blueGraphView);
        UI.hide(grayscaleGraphView);

        UI.setGraphViewXAxisBoundary(redGraphView, ColorHistogram.MIN_VALUE, ColorHistogram.MAX_VALUE);
        UI.setGraphViewXAxisBoundary(greenGraphView, ColorHistogram.MIN_VALUE, ColorHistogram.MAX_VALUE);
        UI.setGraphViewXAxisBoundary(blueGraphView, ColorHistogram.MIN_VALUE, ColorHistogram.MAX_VALUE);
        UI.setGraphViewXAxisBoundary(grayscaleGraphView, ColorHistogram.MIN_VALUE, ColorHistogram.MAX_VALUE);

        imageView = view.findViewById(R.id.histogramImageView);
        imageView.setOnClickListener(getImageViewOnClickListener());

        activity.registerOriginalImageView(getContext(), imageView);
        Uri uri = activity.getImageURI();

        if(uri != null) {
            UI.setImageView(getContext(), imageView, uri);
            UI.clearMemory(getContext());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        UI.clearMemory(getContext());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
    }

    /* Methods */

    // Constructor
    public HistogramFragment() {}

    // Factory method to create new instance of fragment
    public static HistogramFragment newInstance() {
        return new HistogramFragment();
    }

    // Get image view on click listener
    private View.OnClickListener getImageViewOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show image selection dialog
                ImageDialogFragment imageDialog = new ImageDialogFragment();
                imageDialog.show(getFragmentManager(), ImageDialogFragment.TAG);
            }
        };
    }
}
