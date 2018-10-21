package com.imagic.imagic;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A class representing custom alert dialog for showing options to change or reset image.
 */
public class ImageDialogFragment extends DialogFragment {

    /* Constants */

    // Dialog tag
    static final String TAG = "imageDialog";

    /* Properties */

    // Communicator variable
    private ImageDialogListener activity;

    /* Lifecycles */

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (ImageDialogListener) context;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view =  inflater.inflate(R.layout.dialog_image, null);
        Button selectImageButton = view.findViewById(R.id.selectImageButton);
        Button captureImageButton = view.findViewById(R.id.captureImageButton);

        selectImageButton.setOnClickListener(getSelectImageButtonOnClickListener());
        captureImageButton.setOnClickListener(getCaptureImageButtonOnClickListener());

        builder.setTitle("Change or Reset Image")
               .setView(view)
               .setPositiveButton(R.string.image_dialog_positive_button, new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialogInterface, int id) {
                       // Reset image (right button)
                       activity.resetImage(getContext());
                   }
               })
               .setNegativeButton(R.string.image_dialog_negative_button, new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialogInterface, int id) {
                       // Cancel (left button)
                       ImageDialogFragment.this.getDialog().cancel();
                   }
               });

        return builder.create();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
    }

    /* Event listeners */

    // Get select image button on click listener
    private View.OnClickListener getSelectImageButtonOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Dismiss dialog
                ImageDialogFragment.this.getDialog().dismiss();

                // Send intent to select image
                activity.sendSelectImageIntent();
            }
        };
    }

    // Get capture image button on click listener
    private View.OnClickListener getCaptureImageButtonOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Dismiss dialog
                ImageDialogFragment.this.getDialog().dismiss();

                // Send intent to capture image
                activity.sendCaptureImageIntent();
            }
        };
    }
}
