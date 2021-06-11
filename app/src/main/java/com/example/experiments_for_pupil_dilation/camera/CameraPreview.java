package com.example.experiments_for_pupil_dilation.camera;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import java.io.IOException;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback
{
    public SurfaceHolder mHolder;
    public Camera mCamera;

    public CameraPreview(Context context, Camera camera)
    {
        super(context);
        mCamera = camera;
        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder)
    {
        // The Surface has been created, now tell the camera where to draw the preview.
        try
       {
            if(mCamera != null)
            {
                //mCamera.setDisplayOrientation(90);
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
            }
        }
        catch (IOException e)
        {
            Log.d("CAMERA PREVIEW1 ERROR", "Error setting camera preview: " + e.getMessage());
        }

    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height)
    {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (mHolder.getSurface() == null){
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try
        {
            mCamera.stopPreview();
        }
        catch (Exception e)
        {
            // ignore: tried to stop a non-existent preview
        }
        // set preview size and make any resize, rotate or
        // reformatting changes here
        // start preview with new settings
        try
        {
            //mCamera.setPreviewDisplay(mHolder);
           // mCamera.startPreview();
        }
        catch (Exception e)
        {
            Log.d("CAMERA PREVIEW2 ERROR", "Error starting camera preview: " + e.getMessage());
        }
    }

}
