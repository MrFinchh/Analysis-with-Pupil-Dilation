package com.example.experiments_for_pupil_dilation.experiments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.Nullable;

import com.example.experiments_for_pupil_dilation.Database;
import com.example.experiments_for_pupil_dilation.R;
import com.example.experiments_for_pupil_dilation.camera.CameraPreview;
import com.example.experiments_for_pupil_dilation.camera.CameraRecord;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;


public class ExperimentStarter extends Activity implements View.OnClickListener, ExperimentTypes
{
    private ImageView imageView;
    private Button soundView;
    private VideoView videoView;
    private MediaPlayer mp = new MediaPlayer();
    private Button next_button;
    private Button start;

    private String experiment_name;
    private String experiment_type;
    private String username;

    private RelativeLayout main ;
    private FrameLayout preview;

    private boolean lastPageFlag = false ;
    private boolean end = false;

    private int experiment_period = 12 * 1000;

    private int experiment_id = 0;
    private String [] asset_paths;

    private CameraRecord cameraRecord;
    private Database db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.experiment_starter);
        experiment_name = getIntent().getStringExtra("EXP_NAME");
        experiment_type = getIntent().getStringExtra("EXP_TYPE");
        username = getIntent().getStringExtra("username");
        db = new Database(this);
        initLayout();
        setAssets();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        cameraRecord.releaseMediaRecorder();       // if you are using MediaRecorder, release it first
        cameraRecord.releaseCamera();              // release the camera immediately on pause event
    }


    private void initLayout()
    {
        main = findViewById(R.id.exp_starter);
        initNextButton("DEVAM ET");
        initStartButton();
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                1,1
        );
        preview = new FrameLayout(this);
        preview.setVisibility(View.INVISIBLE);
        cameraRecord = new CameraRecord(this, preview, username, experiment_name);
        main.addView(preview, params);
    }

    private void initStartButton()
    {
        start = new Button(this);
        start.setOnClickListener(this);
        start.setText("TESTİ BAŞLAT");
        start.setEnabled(true);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT );

        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        main.addView(start);
    }

    private void initNextButton(String text)
    {
        next_button = new Button(this);
        next_button.setOnClickListener(this);
        next_button.setText(text);
        next_button.setEnabled(false);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT );

        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

        main.addView(next_button, params);
    }

    private void setAssets()
    {
        String assetDir = experiment_type + "/" + experiment_name ;
        try
        {
            String [] asset_names = getAssets().list(assetDir);

            asset_paths = new String [asset_names.length] ;
            for (int i = 0; i < asset_names.length; i++)
            {
                asset_paths[i] = assetDir + "/" + asset_names[i];
            }
        }
        catch (IOException e)
        {
            Toast.makeText(this,"Folder not found.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v)
    {
        if (v.equals(start))
        {
            start.setEnabled(false);
            main.removeView(start);
            next_button.setEnabled(false);
            Timer buttonTimer = new Timer();
            buttonTimer.schedule(new TimerTask()
            {
                @Override
                public void run() {
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run() {

                            next_button.setEnabled(true);
                        }
                    });
                }
            }, experiment_period);
            createExperimentView(experiment_id);
            cameraRecord.record(experiment_id);
        }

        else if(v.equals(next_button))
        {
            if(end)
            {
                finish_experiment();
            }
            else
            {
                next_button.setEnabled(false);
                experiment_id += 1;
                main.removeView(imageView);
                main.removeView(soundView);
                main.removeView(videoView);

                Timer buttonTimer = new Timer();
                buttonTimer.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                next_button.setEnabled(true);
                            }
                        });
                    }
                }, experiment_period);
                createExperimentView(experiment_id);

                if (!lastPageFlag)
                {
                    cameraRecord.record(experiment_id);
                }
                else
                {
                    finish_experiment();
                }
            }

        }
    }

    private  void finish_experiment()
    {
        cameraRecord.releaseMediaRecorder();
        cameraRecord.releaseCamera();
        db.addData(username, experiment_name);
        //Intent to_select = new Intent(this, ExperimentSelector.class);
        //to_select.putExtra("EXP_TYPE", experiment_type);
        //to_select.putExtra("username", username);
        //startActivity(to_select);
        setResult(1);
        finish();
    }


    private void createExperimentView(int experiment_id)
    {
        Uri videoUri = hasVideoAsset();
        String asset_path;

        try
        {
            asset_path = asset_paths[experiment_id];

            if(asset_path.contains(".png") || asset_path.contains("image"))
            {
                createImageView(asset_path);
            }
            else if(asset_path.contains(".mp3") || asset_path.contains("sound"))
            {
                createSoundView(asset_path);
            }
        }
        catch (IndexOutOfBoundsException e)
        {
            if(videoUri != null)
            {
                setVideoAssets(videoUri);
                end = true;
            }
            else
            {
                lastPageFlag = true;
            }
        }

    }

    // Image View Operations

    private Bitmap convertImage2Bitmap(String path)
    {
        try
        {
            InputStream ims = this.getAssets().open(path);
            Bitmap bitmap =  BitmapFactory.decodeStream(ims);
            return bitmap;
        }
        catch (IOException e)
        {
            Toast.makeText(this,"Image not found.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return null;
    }

    private void createImageView(String path)
    {
        // Convert image to bitmap
        Bitmap image = convertImage2Bitmap(path);
        if(image != null)
        {
            // initialize image view
            imageView = new ImageView(this);
            // set drawable image for image view
            imageView.setImageBitmap(image);
            // set layout parameters

            LinearLayout.LayoutParams image_layout_params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            image_layout_params.gravity = Gravity.TOP;
            //image_layout_params.addRule(RelativeLayout.ALIGN_PARENT_TOP);

            //imageView.setLayoutParams(image_layout_params);
            // add image view to layout
            main.addView(imageView, image_layout_params);
        }
    }

    // Sound View Operations

    private void initSoundView()
    {
        soundView = new Button(this);
        soundView.setOnClickListener(this);
        soundView.setText("SES DENEYİNİ BAŞLAT");
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);


        main.addView(soundView, params);
    }

    private void createSoundView(String path)
    {
        initSoundView();

        soundView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try
                {
                    if(mp.isPlaying())
                    {
                        mp.stop();
                        mp.release();
                        mp =  new MediaPlayer();
                    }
                    else
                    {
                        AssetFileDescriptor afd = getAssets().openFd(path);
                        mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                        mp.prepare();
                        mp.start();
                        afd.close();
                        soundView.setEnabled(false);
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    // Video View Operations

    private Uri hasVideoAsset()
    {
        for (int i = 0; i < EMOTION.length ; i++)
        {
            if(EMOTION[i].equals(experiment_name))
            {
                String path = "android.resource://" + getPackageName() + "/raw/" +
                        experiment_name.toLowerCase();
                Uri videoUri = Uri.parse(path);
                return videoUri;
            }
        }
        return null;
    }

    private void setVideoAssets(Uri videoUri)
    {
        videoView = new VideoView(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        //MediaController mc = new MediaController(this);
        //videoView.setMediaController(mc);
        videoView.setVideoURI(videoUri);
        videoView.setVisibility(View.VISIBLE);
        main.addView(videoView, params);
        videoView.start();
    }

}