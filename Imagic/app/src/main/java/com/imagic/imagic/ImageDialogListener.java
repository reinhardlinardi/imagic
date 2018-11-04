package com.imagic.imagic;

/**
 * An interface to help communication from ImageDialogFragment to MainActivity.
 *
 * MainActivity have to implement this interface as well as all of its methods.
 * ImageDialogFragment will hold a reference to MainActivity in a "communicator variable" on attach.
 * That "communicator variable" data type is this interface.
 * We can pass events back to MainActivity from ImageDialogFragment by calling method from our "communicator variable" in ImageDialogFragment.
 */
interface ImageDialogListener {

    // Send intent to select image
    void sendSelectImageIntent();

    // Send intent to capture image
    void sendCaptureImageIntent();
}
