package com.example.experiments_for_pupil_dilation.camera;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.CamcorderProfile;
import android.media.MediaMetadataRetriever;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.experiments_for_pupil_dilation.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.content.Context.AUDIO_SERVICE;

public class CameraRecord
{
    private Context context;
    private Camera mCamera;
    private CameraPreview mPreview;
    public MediaRecorder mediaRecorder;
    private FrameLayout parent_layout;
    private boolean isRecording = false;
    private int visibility = View.INVISIBLE;

    private String username;
    private String experiment_name;

    public CameraRecord(Context context, FrameLayout parent_layout, String username, String experiment_name)
    {
        this.username = username;
        this.experiment_name = experiment_name;
        this.context = context;
        this.parent_layout = parent_layout;
        initialize();
    }


    /** Create a File for saving an image or video */
    private File getOutputMediaFile(int experiment_id)
    {
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), "DENEYLER");

        File user_file = new File(file.getPath(), username);

        String filename = experiment_name + String.valueOf(experiment_id) ;
        // Create a media file name
        File mediaFile = new File(user_file.getPath() + File.separator +
                username + "_" + filename + ".mp4");

        if(mediaFile != null)
        {
            boolean deleted = mediaFile.delete();
            mediaFile = new File(user_file.getPath() + File.separator +
                    username + "_" + filename + ".mp4");
        }

        return mediaFile;
    }

    private static int findFrontCamera()
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

    private void initialize()
    {
        // Create an instance of Camera
        mCamera = getCameraInstance();
        /*
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(front_camera_id, info);
        if (info.canDisableShutterSound) {
            mCamera.enableShutterSound(false);
        }
        */
        //mCamera.setDisplayOrientation(90);
        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(context, mCamera);
        parent_layout.setVisibility(visibility);
        parent_layout.addView(mPreview);
    }

    public void start_recording(int experiment_id)
    {
        // initialize video camera
        if (prepareVideoRecorder(experiment_id))
        {
            Log.d("CAM", "Camera Started.");
            mediaRecorder.start();
            // inform the user that recording has started
            isRecording = true;
        }
        else
        {
            System.out.println("STOPPED SOUND HERE?");
            // prepare didn't work, release the camera
            releaseMediaRecorder();
            // inform user
        }
    }

    public void record(int experiment_id)
    {
        if (isRecording)
        {
            Log.d("CAM", "Camera Stopped.");
            // stop recording and release camera
            // stop the recording
            System.out.println("STOPPED SOUND HERE?");
            mediaRecorder.stop();
            // release the MediaRecorder object
            releaseMediaRecorder();
            // take camera access back from MediaRecorder
            //mCamera.lock();
            // inform the user that recording has stopped
            isRecording = false;
            start_recording(experiment_id);
        }
        else
        {
            start_recording(experiment_id);
        }
    }

    private boolean prepareVideoRecorder(int experiment_id)
    {
        mCamera = getCameraInstance();
        //mCamera.setDisplayOrientation(90);
        mediaRecorder = new MediaRecorder();
        // Step 1: Unlock and set camera to MediaRecorder
        mCamera.unlock();
        mediaRecorder.setCamera(mCamera);
        CamcorderProfile profile = CamcorderProfile.get(front_camera_id, CamcorderProfile.QUALITY_HIGH);

        // Step 2: Set sources
        //mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
        //mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
        mediaRecorder.setOutputFormat(profile.fileFormat);
        mediaRecorder.setVideoEncoder(profile.videoCodec);
        mediaRecorder.setVideoEncodingBitRate(profile.videoBitRate);
        mediaRecorder.setVideoFrameRate(profile.videoFrameRate);
        mediaRecorder.setVideoSize(profile.videoFrameWidth, profile.videoFrameHeight);

        // Step 4: Set output file
        mediaRecorder.setOutputFile(getOutputMediaFile(experiment_id).toString());

        // Step 5: Set the preview output
        mediaRecorder.setPreviewDisplay(mPreview.getHolder().getSurface());
        Log.d("MEDIA RECORDER ERROR", mPreview.getHolder().getSurface().toString());
        // Step 6: Prepare configured MediaRecorder
        try
        {
            mediaRecorder.prepare();
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


    public void releaseMediaRecorder()
    {
        System.out.println("STOPPED SOUND HERE?");
        if (mediaRecorder != null) {
            mediaRecorder.reset();   // clear recorder configuration
            mediaRecorder.release(); // release the recorder object
            mediaRecorder = null;
            mCamera.lock();           // lock camera for later use
        }
    }

    public void releaseCamera()
    {
        if (mCamera != null){
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }

    static int front_camera_id = findFrontCamera();

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

