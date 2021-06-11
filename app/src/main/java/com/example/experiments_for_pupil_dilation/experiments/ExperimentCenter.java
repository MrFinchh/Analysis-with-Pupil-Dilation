package com.example.experiments_for_pupil_dilation.experiments;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.experiments_for_pupil_dilation.Database;
import com.example.experiments_for_pupil_dilation.LightTest;
import com.example.experiments_for_pupil_dilation.NormalTest;
import com.example.experiments_for_pupil_dilation.R;
import com.example.experiments_for_pupil_dilation.UserOperations;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ExperimentCenter extends Activity implements View.OnClickListener
{
    private String username;
    private int popUp_height = LinearLayout.LayoutParams.WRAP_CONTENT;
    private int popUp_width =  LinearLayout.LayoutParams.MATCH_PARENT;
    TextView show_username;
    PopupWindow popUp;
    Button emotion;
    Button cognitive;
    Button help;
    Button user_operations;
    Button normal_test;
    Button light_test;

    Intent user_op;
    Boolean popUpClicked = true;

    private String[] permissions = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.experiment_center);
        checkPermissions();
        popUp = new PopupWindow(this);
        show_username = findViewById(R.id.username_text);
        emotion = (Button) findViewById(R.id.exp_emotion);
        cognitive = (Button) findViewById(R.id.exp_cognitive);
        help = (Button) findViewById(R.id.exp_help);
        user_operations = findViewById(R.id.user_operations);
        normal_test = findViewById(R.id.normal_test);
        light_test = findViewById(R.id.light_test);

        show_username.setText("Aktif Kullanıcı İsmi: Yok");

        emotion.setOnClickListener(this);
        cognitive.setOnClickListener(this);
        help.setOnClickListener(this);
        light_test.setOnClickListener(this);
        user_operations.setOnClickListener(this);
        normal_test.setOnClickListener(this);
        light_test.setEnabled(false);
        emotion.setEnabled(false);
        cognitive.setEnabled(false);
        normal_test.setEnabled(false);
        create_internal();
    }

    private void create_internal()
    {
        File file  = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "DENEYLER");
        if(!file.exists())
        {
            boolean success = file.mkdirs();
        }
    }

    private void create_user_folder()
    {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "DENEYLER");
        File user_file = new File(file.getPath(), username);

        if(!user_file.exists())
        {
            user_file.mkdirs();
        }
    }


    private boolean checkPermissions()
    {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(this, p);
            if (result != PackageManager.PERMISSION_GRANTED)
            {
                listPermissionsNeeded.add(p);
            }

        }
        if (!listPermissionsNeeded.isEmpty())
        {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 100);
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v)
    {
        if(v.equals(help))
        {
            LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            PopupWindow pw = new PopupWindow(inflater.inflate(R.layout.experiment_help,
                    null, false),
                    popUp_width,
                    popUp_height,
                    true);

            // The code below assumes that the root container has an id called 'main'
            pw.showAtLocation(this.findViewById(R.id.exp_main), Gravity.CENTER, 0, 0);

        }

        if(v.equals(emotion) && username != null)
        {
            Intent emotion_experiments = new Intent(this, ExperimentSelector.class);
            emotion_experiments.putExtra("EXP_TYPE", "EMOTION");
            emotion_experiments.putExtra("username", username);
            startActivity(emotion_experiments);
        }
        else if(v.equals(cognitive) && username != null)
        {
            Intent cognitive_experiments = new Intent(this, ExperimentSelector.class);
            cognitive_experiments.putExtra("EXP_TYPE", "COGNITIVE");
            cognitive_experiments.putExtra("username", username);
            startActivity(cognitive_experiments);
        }
        else if (v.equals(normal_test) && username != null)
        {
            Intent normal = new Intent(this, NormalTest.class);
            normal.putExtra("username", username);
            startActivityForResult(normal, 2);
        }
        else if(v.equals(light_test) && username != null)
        {
            Intent light = new Intent(this, LightTest.class);
            light.putExtra("username", username);
            startActivityForResult(light, 200);
        }

        else if(v.equals(user_operations))
        {
            user_op = new Intent(this, UserOperations.class);
            user_op.putExtra("username", username);
            startActivityForResult(user_op, 1);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1)
        {
            username = data.getStringExtra("username");
            show_username.setText("Kullanıcı İsmi: "+username);
            enableButton(light_test);
            create_user_folder();
            if(isLightTestCompleted(username))
            {
                enableButton(normal_test);
                if(isNormalTestCompleted(username))
                {
                    enableButton(emotion);
                    enableButton(cognitive);
                }
                else
                {
                    disableButton(emotion);
                    disableButton(cognitive);
                }
            }
            else
            {
                disableButton(normal_test);
                disableButton(emotion);
                disableButton(cognitive);
            }
        }
        if(resultCode == 200)
        {
            enableButton(normal_test);
            if(isNormalTestCompleted(username))
            {
                enableButton(emotion);
                enableButton(cognitive);
            }
            else
            {
                disableButton(emotion);
                disableButton(cognitive);
            }
        }
        if (resultCode == 2)
        {
            enableButton(emotion);
            enableButton(cognitive);
        }
    }

    private boolean isNormalTestCompleted(String username)
    {
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), "DENEYLER");

        File user_file = new File(file.getPath(), username);
        File recorded_test = new File(user_file.getPath() + File.separator +
                username + "_" + "normal_test0" + ".mp4");

        if(recorded_test.exists())
            return true;

        return false;
    }

    private boolean isLightTestCompleted(String username)
    {
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), "DENEYLER");

        File user_file = new File(file.getPath(), username);
        File recorded_test = new File(user_file.getPath() + File.separator +
                username + "_" + "light_test0" + ".mp4");

        if(recorded_test.exists())
            return true;

        return false;
    }

    private void enableButton(Button button)
    {
        button.setEnabled(true);
        button.setBackground(getResources().getDrawable(R.drawable.roundshape_enabled_button));
    }

    private void disableButton(Button button)
    {
        button.setEnabled(false);
        button.setBackground(getResources().getDrawable(R.drawable.roundshape_disabled_button));
    }

}

