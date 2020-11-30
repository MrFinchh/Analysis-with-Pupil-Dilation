package com.example.experiments_for_pupil_dilation.experiments;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.example.experiments_for_pupil_dilation.R;


public class ExperimentStarter extends Activity
{
    private String experiment_name;
    private String experiment_type;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.experiment_starter);
        experiment_name = getIntent().getStringExtra("EXP_NAME");
        experiment_type = getIntent().getStringExtra("EXP_TYPE");
    }

    private void setAssets()
    {

    }



}