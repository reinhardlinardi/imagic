package com.imagic.imagic;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class FaceRecognitionFragment extends Fragment implements MainActivityListener {

    /* Properties */

    /* Communicator Variables */
    private FragmentListener activity;

    /* UI Components */
    private LinearLayout container;
    private ProgressBar progressBar;

    private ImageView imageView;
    private ImageView transformedImageView;

    private TextView helpTextView;

    private Button detectButton;

    Image image;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (FragmentListener) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_face_recognition,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if(isAttachedToMainActivity()){
            container = view.findViewById(R.id.faceRecognitionContainer);

            progressBar = view.findViewById(R.id.faceRecognitionProgressBar);
            helpTextView = view.findViewById(R.id.faceRecognitionHelpTextView);

            transformedImageView = view.findViewById(R.id.faceRecognitionTransformedImageView);
            imageView = view.findViewById(R.id.faceRecognitionImageView);
            imageView.setOnClickListener(getImageViewOnClickListener());

            detectButton = view.findViewById(R.id.faceRecognitionButton);
            detectButton.setOnClickListener(getDetectButtonOnClickListener());
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

    /* Implemented interface methods */

    // Send given intent to select image
    public void sendSelectImageIntent(Intent intent) { startActivityForResult(intent, IntentRequestCode.SELECT_IMAGE.code); }

    // Send given intent to capture image
    public void sendCaptureImageIntent(Intent intent) { startActivityForResult(intent, IntentRequestCode.CAPTURE_IMAGE.code); }

    public void loadImageOnSelected(){
        if(isAttachedToMainActivity()){
            if(activity.isImageHasBitmap()){
                /*WOY IMPLEMENT */
                ImageLoadAsynctask imageLoadAsynctask = new ImageLoadAsynctask();
                imageLoadAsynctask.execute(false);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(isAttachedToMainActivity()){
            if(activity.onImageIntentResult(requestCode,resultCode,data) && activity.hasImage()){
                /*WOY IMPLEMENT */
                ImageLoadAsynctask imageLoadAsynctask = new ImageLoadAsynctask();
                imageLoadAsynctask.execute(true);
            }
        }
    }

    /* Event Listeners */
    private View.OnClickListener getImageViewOnClickListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isAttachedToMainActivity()){
                    ImageDialogFragment imageDialog = new ImageDialogFragment();
                    imageDialog.show(getFragmentManager(),ImageDialogFragment.TAG);
                }
            }
        };
    }

    private View.OnClickListener getDetectButtonOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isAttachedToMainActivity()){
                    if(activity.hasImage()){
                        image = new Image(getContext(),activity.getImage());
                        FaceDetectionAsynctask faceDetectionAsynctask = new FaceDetectionAsynctask();
                        faceDetectionAsynctask.execute();
                    }
                }
            }
        };
    }

    /* Methods */
    //Constructor
    public FaceRecognitionFragment(){}

    public static FaceRecognitionFragment newInstance(){return new FaceRecognitionFragment();}

    // Check if fragment is attached to MainActivity or not
    private boolean isAttachedToMainActivity() { return activity != null; }

    // Count progress
    private int countProgress(int numTaskDone, int totalNumTask) {
        float taskDoneFraction = (float) numTaskDone / totalNumTask;
        return (int)(taskDoneFraction * 100);
    }

    /* Async tasks */
    //Image load Asynctask
    private class ImageLoadAsynctask extends AsyncTask<Boolean, Integer, Boolean> {
        @Override
        protected Boolean doInBackground(Boolean... bools) {
            boolean executedFromIntentResult = bools[0];
            publishProgress(countProgress(1,2));
            try {
                if(isAttachedToMainActivity() && executedFromIntentResult) activity.loadImageBitmap(imageView.getWidth(),imageView.getHeight());
                publishProgress(countProgress(2,2));
            } catch (Exception e) {
                Debug.ex(e);
            }

            return executedFromIntentResult;
        }

        @Override
        protected void onPreExecute() {
            if(isAttachedToMainActivity()){
                UI.setUnclickable(imageView);
                if(UI.isVisible(helpTextView)) UI.hide(helpTextView);
                progressBar.setProgress(0);
                UI.show(progressBar);
            }
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            if(isAttachedToMainActivity()){
                progressBar.setProgress(progress[0]);
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if(isAttachedToMainActivity()){
                image = new Image(getContext(),activity.getImage());

                UI.setImageView(getContext(),imageView,image.bitmap);
                UI.setImageView(getContext(),transformedImageView,image.bitmap);

                UI.clearMemory(getContext());
                UI.setInvisible(progressBar);

                UI.setClickable(imageView);

                if(!UI.isVisible(transformedImageView)) UI.show(transformedImageView);
                if(!UI.isVisible(container)) UI.show(container);

            }
        }
    }

    private class FaceDetectionAsynctask extends AsyncTask<Void, Integer,Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            if(isAttachedToMainActivity()){
                progressBar.setProgress(countProgress(1,2));
                /*WOY IMPLEMENT */
                image.findFace();
                System.out.println("WOY IMPLEMENT");
                progressBar.setProgress(countProgress(2,2));
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            if(isAttachedToMainActivity()){
                UI.setUnclickable(imageView);
                UI.disable(detectButton);
                progressBar.setProgress(0);
                UI.show(progressBar);
            }
        }

        @Override
        protected void onPostExecute(Void result) {
            if(isAttachedToMainActivity()){
                UI.setImageView(getContext(),transformedImageView,image.bitmap);
                UI.clearMemory(getContext());

                UI.setInvisible(progressBar);
                UI.setClickable(imageView);
                UI.enable(detectButton);
            }
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            if(isAttachedToMainActivity()){
                progressBar.setProgress(progress[0]);
            }
        }
    }
}
