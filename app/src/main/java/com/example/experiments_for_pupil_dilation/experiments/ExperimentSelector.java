package com.example.experiments_for_pupil_dilation.experiments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.annotation.Nullable;

import com.example.experiments_for_pupil_dilation.R;

import java.util.ArrayList;

public class ExperimentSelector extends Activity implements View.OnClickListener, ExperimentTypes
{
    private String EXPERIMENT_TYPE = "";
    private ArrayList<Button> experiment_buttons = new ArrayList<>();
    private String [] experiments ;
    private ScrollView expLayout ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.experiment_selector);
        initContentLayout();
        EXPERIMENT_TYPE = getIntent().getStringExtra("EXP_TYPE");
        findExperimentType();
        createLayout();
    }

    private void initContentLayout()
    {
        expLayout = findViewById(R.id.exp_selector);
    }

    private void findExperimentType()
    {
        if(EXPERIMENT_TYPE.equals("EMOTION"))
        {
            experiments = EMOTION;
        }
        else if (EXPERIMENT_TYPE.equals("COGNITIVE"))
        {
            experiments = COGNITIVE;
        }
    }

    private void createLayout()
    {
        String experimentBaseName = "Deney";

        LinearLayout linearLayout = new LinearLayout(this);
        LinearLayout.LayoutParams button_params  = new LinearLayout.LayoutParams(
                300,
                300);

        linearLayout.setOrientation(LinearLayout.VERTICAL);

        button_params.gravity = Gravity.CENTER;
        button_params.setMargins(0, 20, 0, 0);

        for (int i = 0; i < experiments.length ; i++)
        {
            Button exp_button = new Button(this);
            exp_button.setBackground(this.getResources().getDrawable(R.drawable.circleshape_button));
            exp_button.setLayoutParams(button_params);
            exp_button.setGravity(Gravity.CENTER);
            exp_button.setText(experimentBaseName + " " + i);
            exp_button.setOnClickListener(this);
            experiment_buttons.add(exp_button);
            linearLayout.addView(exp_button);
        }
        expLayout.addView(linearLayout);
    }


    @Override
    public void onClick(View v)
    {
        for (int i = 0; i < experiment_buttons.size(); i++)
        {
            Button selected = experiment_buttons.get(i);

            if(v.equals(selected))
            {
                String experiment_name = experiments[i];
                Intent experiment = new Intent(this, ExperimentStarter.class);
                experiment.putExtra("EXP_NAME", experiment_name);
                experiment.putExtra("EXP_TYPE", EXPERIMENT_TYPE);
                startActivity(experiment);
            }

        }
    }
}
