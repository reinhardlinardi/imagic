package com.imagic.imagic;

import android.content.Intent;

/**
 * An interface to help communication from MainActivity to current fragment.
 *
 * Each fragment have to implement this interface as well as all of its methods.
 * MainActivity will hold a reference to current fragment in a "communicator variable".
 * That "communicator variable" data type is this interface. It will be updated on page change or tab selected.
 * We can call a method in current fragment from MainActivity by calling the method from our "communicator variable" in MainActivity.
 */
interface MainActivityListener {

    // Send given intent to select image
    void sendSelectImageIntent(Intent intent);

    // Send given intent to capture image
    void sendCaptureImageIntent(Intent intent);

    // Load image when fragment is selected
    void loadImageOnSelected();
}
