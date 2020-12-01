package com.example.experiments_for_pupil_dilation.experiments;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.Nullable;

import com.example.experiments_for_pupil_dilation.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;


public class ExperimentStarter extends Activity
{
    private String experiment_name;
    private String experiment_type;

    private ArrayList<ImageView> imageViews = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.experiment_starter);
        experiment_name = getIntent().getStringExtra("EXP_NAME");
        experiment_type = getIntent().getStringExtra("EXP_TYPE");
        setImageAssets();
    }

    private void setImageAssets()
    {
        String assetDir = experiment_type + "/" + experiment_name ;

        try
        {
            String[] images =getAssets().list(assetDir);

            for (int i = 0; i <images.length ; i++)
            {
                InputStream ips = getAssets().open(assetDir + "/" + images[i]);
                Drawable d = Drawable.createFromStream(ips, null);
                imageViews.get(i).setImageDrawable(d);
            }
        }
        catch (IOException e)
        {
            Toast.makeText(this,"Folder not found.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void setVideoAssets()
    {
        String path = "android.resource://" + getPackageName() + "/" + R.raw.sample;
        VideoView videoHolder = new VideoView(this);
        Uri videoUri = Uri.parse(path);
        videoHolder.setVideoURI(videoUri);
        setContentView(videoHolder);
        videoHolder.start();
    }


}