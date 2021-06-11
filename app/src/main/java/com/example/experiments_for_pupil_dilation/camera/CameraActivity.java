package com.example.experiments_for_pupil_dilation.camera;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaMetadataRetriever;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.experiments_for_pupil_dilation.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class CameraActivity extends Activity {

    private Camera mCamera;
    private CameraPreview mPreview;
    private MediaRecorder mediaRecorder;

    private Button captureButton;

    private boolean isRecording = false;
    private LinearLayout main;

    private String[] permissions = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
    };

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    /** Create a file Uri for saving an image or video */
    private static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

//        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
//                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        File filepath = Environment.getExternalStorageDirectory();

        File mediaStorageDir = new File(filepath.getAbsolutePath() + "/Pictures/MyCameraApp/" );
        System.out.println(mediaStorageDir);
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            mediaStorageDir.mkdirs();
//            if (! mediaStorageDir.mkdirs()){
//                Log.d("MyCameraApp", "failed to create directory");
//                return null;
//            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE)
        {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        }
        else if(type == MEDIA_TYPE_VIDEO)
        {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        }
        else
        {
            return null;
        }

        return mediaFile;
    }

    private static int find_frontCamera()
    {
        boolean found = false;
        int i;
        for (i=0; i< Camera.getNumberOfCameras(); i++) {
            Camera.CameraInfo newInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(i, newInfo);
            if (newInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                found = true;
                break;
            }
        }
        return i;
    }

    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }

        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 100);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == 100) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // do something
            }
            return;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        main = findViewById(R.id.camera_layout);
        checkPermissions();

        mCamera = getCameraInstance();
        mCamera.setDisplayOrientation(90);
        mPreview = new CameraPreview(this, mCamera);

        FrameLayout preview = new FrameLayout(this);
        preview.setVisibility(View.INVISIBLE);
        preview.addView(mPreview);
        main.addView(preview);

        Button test = (Button) findViewById(R.id.button_test);
        captureButton = (Button) findViewById(R.id.button_capture);
        // Add a listener to the Capture button
        captureButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        record();
                    }
                }
        );
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File filepath = Environment.getExternalStorageDirectory();
                String videoName = "Testing.mp4";
                File mediapath = new File(filepath.getAbsoluteFile() + "/Pictures/MyCameraApp/"+videoName );

                MediaMetadataRetriever media = new MediaMetadataRetriever();
                media.setDataSource(mediapath.toString());
                Bitmap extractedImage = media.getFrameAtTime(100000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);

                int redColors = 0;
                int greenColors = 0;
                int blueColors = 0;
                int pixelCount = 0;

                for (int y = 0; y < extractedImage.getHeight(); y++)
                {
                    for (int x = 0; x < extractedImage.getWidth(); x++)
                    {
                        int c = extractedImage.getPixel(x, y);
                        pixelCount++;
                        redColors += Color.red(c);
                        greenColors += Color.green(c);
                        blueColors += Color.blue(c);
                    }
                }
                // calculate average of bitmap r,g,b values
                int red = (redColors/pixelCount);
                int green = (greenColors/pixelCount);
                int blue = (blueColors/pixelCount);

                String colorValues = "RGB :" + red+ " "+ green + " "+ blue ;

                double brightness = 0.2126 * red + 0.7152 * green + 0.0722 * blue ;
                String brText = "Brightness is : " + brightness;
                Toast.makeText(CameraActivity.this, colorValues, Toast.LENGTH_LONG  ).show();
                Toast.makeText(CameraActivity.this, brText, Toast.LENGTH_LONG  ).show();
            }
        });
    }

    private void record()
    {
        if (isRecording)
        {
            mediaRecorder.stop();
            releaseMediaRecorder();
            mCamera.lock();
            captureButton.setText("Capture");
            isRecording = false;
        }
        else
        {
            if (prepareVideoRecorder())
            {
                mediaRecorder.start();
                captureButton.setText("Stop");
                isRecording = true;
            }
            else
            {
                releaseMediaRecorder();
            }
        }
    }

    private boolean prepareVideoRecorder(){

        mCamera = getCameraInstance();
        mCamera.setDisplayOrientation(90);
        mediaRecorder = new MediaRecorder();

        // Step 1: Unlock and set camera to MediaRecorder
        mCamera.unlock();
        mediaRecorder.setCamera(mCamera);

        // Step 2: Set sources
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
        mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_LOW));

        // Step 4: Set output file
        mediaRecorder.setOutputFile(getOutputMediaFile(MEDIA_TYPE_VIDEO).toString());
        System.out.println(getOutputMediaFile(MEDIA_TYPE_VIDEO).toString());
        // Step 5: Set the preview output
        //mediaRecorder.setPreviewDisplay(mPreview.getHolder().getSurface());
        mediaRecorder.setPreviewDisplay(mPreview.mHolder.getSurface());
        // Step 6: Prepare configured MediaRecorder
        try
        {
            System.out.println("step6");
            mediaRecorder.prepare();
            System.out.println("step7");
        }
        catch (IllegalStateException e)
        {
            Log.d("MEDIA RECORDER ERROR", "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        }
        catch (IOException e)
        {
            Log.d("MEDIA RECORDER ERROR", "IOException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        }
        return true;
    }

    private void releaseMediaRecorder(){
        if (mediaRecorder != null) {
            mediaRecorder.reset();   // clear recorder configuration
            mediaRecorder.release(); // release the recorder object
            mediaRecorder = null;
            mCamera.lock();           // lock camera for later use
        }
    }

    /** Check if this device has a camera */

    static int front_camera_id = find_frontCamera();

    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(front_camera_id); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }


}
