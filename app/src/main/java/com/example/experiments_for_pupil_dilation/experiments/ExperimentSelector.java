package com.example.experiments_for_pupil_dilation.experiments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.annotation.Nullable;

import com.example.experiments_for_pupil_dilation.Database;
import com.example.experiments_for_pupil_dilation.R;

import java.util.ArrayList;

public class ExperimentSelector extends Activity implements View.OnClickListener, ExperimentTypes
{
    private String EXPERIMENT_TYPE = "";
    private String username = "";
    private ArrayList<Button> experiment_buttons = new ArrayList<>();
    private String [] experiments ;
    private ScrollView expLayout ;
    private Button back;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.experiment_selector);
        initContentLayout();
        EXPERIMENT_TYPE = getIntent().getStringExtra("EXP_TYPE");
        username = getIntent().getStringExtra("username");
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

        ArrayList<String> completed_experiments = isCompleted();

        for (int i = 0; i < experiments.length ; i++)
        {
            Button exp_button = new Button(this);

            if(completed_experiments.contains(experiments[i]))
            {
                exp_button.setBackground(this.getResources().getDrawable(R.mipmap.check_mark_background));
            }
            else
            {
                exp_button.setBackground(this.getResources().getDrawable(R.drawable.circleshape_button));
            }
            exp_button.setLayoutParams(button_params);
            exp_button.setGravity(Gravity.CENTER);
            exp_button.setText(experimentBaseName + " " + i);
            exp_button.setOnClickListener(this);
            experiment_buttons.add(exp_button);
            linearLayout.addView(exp_button);
        }
        back = new Button(this);
        back.setOnClickListener(this);
        back.setText("DENEY SEÇİM MERKEZİNE DÖN");
        back.setBackground(getResources().getDrawable(R.drawable.roundshape_enabled_button));
        LinearLayout.LayoutParams back_params = new LinearLayout.LayoutParams(
                500,
                300
        );
        back_params.setMargins(0,20,0,0);
        back_params.gravity = Gravity.CENTER;
        linearLayout.addView(back, back_params);
        expLayout.addView(linearLayout);
    }

    private void change_completed_experiment()
    {
        ArrayList<String> completed_experiments = isCompleted();
        for (int i = 0; i <experiment_buttons.size(); i++)
        {
            Button button = experiment_buttons.get(i);
            if(completed_experiments.contains(experiments[i]))
            {
                button.setBackground(this.getResources().getDrawable(R.mipmap.check_mark_background));
            }
        }
    }

    private ArrayList<String> isCompleted()
    {
        ArrayList<String> completed_experiments = new ArrayList<>();
        Database db = new Database(this);
        String [][] info =  db.getData();
        for (int i = 0; i <info.length ; i++)
        {
            if(username.equals(info[i][0]) && info[i][1] != "null")
            {
                completed_experiments.add(info[i][1]);
            }
        }
        return completed_experiments;
    }


    @Override
    public void onClick(View v)
    {
        for (int i = 0; i < experiment_buttons.size(); i++)
        {
            Button selected = experiment_buttons.get(i);

            if(v.equals(selected))
            {
                Intent experiment;
                String experiment_name = experiments[i];
                experiment = new Intent(this, ExperimentStarter.class);
                experiment.putExtra("EXP_NAME", experiment_name);
                experiment.putExtra("EXP_TYPE", EXPERIMENT_TYPE);
                experiment.putExtra("username", username);
                startActivityForResult(experiment, 1);
            }
        }
        if (v.equals(back))
        {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(resultCode == 1)
        {
            change_completed_experiment();
        }
    }
}
