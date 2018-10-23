package com.imagic.imagic;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class OCRFragment extends Fragment implements MainActivityListener {

    public OCRFragment() {}
    public static OCRFragment newInstance() {
        return new OCRFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) { return inflater.inflate(R.layout.fragment_ocr, container, false); }

    @Override
    public void onAttach(Context context) { super.onAttach(context); }

    @Override
    public void onDetach() { super.onDetach(); }

    @Override
    public void sendSelectImageIntent(Intent intent) {}

    @Override
    public void sendCaptureImageIntent(Intent intent) {}

    @Override
    public void loadImageOnSelected() {}
}
