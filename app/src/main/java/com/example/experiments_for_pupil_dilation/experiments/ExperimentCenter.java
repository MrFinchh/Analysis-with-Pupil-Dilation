package com.example.experiments_for_pupil_dilation.experiments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.annotation.Nullable;

import com.example.experiments_for_pupil_dilation.R;

public class ExperimentCenter extends Activity implements View.OnClickListener
{

    private int popUp_height = LinearLayout.LayoutParams.WRAP_CONTENT;
    private int popUp_width =  LinearLayout.LayoutParams.MATCH_PARENT;
    PopupWindow popUp;
    Button emotion;
    Button cognitive;
    Button help;

    Boolean popUpClicked = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.experiment_center);
        popUp = new PopupWindow(this);


        emotion = (Button) findViewById(R.id.exp_emotion);
        cognitive = (Button) findViewById(R.id.exp_cognitive);
        help = (Button) findViewById(R.id.exp_help);

        emotion.setOnClickListener(this);
        cognitive.setOnClickListener(this);
        help.setOnClickListener(this);

    }


    @Override
    public void onClick(View v)
    {
        if(v.equals(emotion))
        {
            Intent emotion_experiments = new Intent(this, ExperimentSelector.class);
            emotion_experiments.putExtra("EXP_TYPE", "EMOTION");
            startActivity(emotion_experiments);
        }
        else if(v.equals(cognitive))
        {
            Intent cognitive_experiments = new Intent(this, ExperimentSelector.class);
            cognitive_experiments.putExtra("EXP_TYPE", "COGNITIVE");
            startActivity(cognitive_experiments);
        }
        else if(v.equals(help))
        {
            System.out.println(popUpClicked);

            LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            PopupWindow pw = new PopupWindow(inflater.inflate(R.layout.experiment_help,
                    null, false),
                    popUp_width,
                    popUp_height,
                    true);

            // The code below assumes that the root container has an id called 'main'
            pw.showAtLocation(this.findViewById(R.id.exp_main), Gravity.CENTER, 0, 0);




        }

    }
}

