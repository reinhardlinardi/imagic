package com.imagic.imagic;

import android.content.Context;
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
        imageView = view.findViewById(R.id.histogramImageView);

        redGraphView = view.findViewById(R.id.histogramRedGraphView);
        greenGraphView = view.findViewById(R.id.histogramGreenGraphView);
        blueGraphView = view.findViewById(R.id.histogramBlueGraphView);
        grayscaleGraphView = view.findViewById(R.id.histogramGrayscaleGraphView);

        // Show image dialog
        ImageDialogFragment imageDialog = new ImageDialogFragment();
        imageDialog.show(getFragmentManager(), "imageDialog");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
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
}
