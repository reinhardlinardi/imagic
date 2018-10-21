package com.imagic.imagic;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

/**
 * An interface to help communication between fragment and MainActivity.
 *
 * MainActivity have to implement this interface as well as all of its methods.
 * Each fragment will hold a reference to MainActivity in a "communicator variable" on attach.
 * That "communicator variable" data type is this interface.
 * We can call a method in MainActivity from fragment by calling the method from our "communicator variable" in that fragment.
 */
interface FragmentListener {

    // Register original image view and fragment context to MainActivity
    void registerOriginalImageView(Context context, ImageView view);

    // Get image URI
    Uri getImageURI();
}
