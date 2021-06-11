package com.example.experiments_for_pupil_dilation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.experiments_for_pupil_dilation.camera.CameraRecord;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class NormalTest extends AppCompatActivity implements View.OnClickListener {

    Button recorder;
    String username;
    private int experiment_id = 0;
    private CameraRecord cameraRecord;
    private LinearLayout main;
    private FrameLayout preview;
    private int experiment_period = 12 * 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal_test);
        username = this.getIntent().getStringExtra("username");
        recorder = findViewById(R.id.recorder);
        recorder.setOnClickListener(this);
        initLayout();
    }

    private void initLayout()
    {
        main = findViewById(R.id.normal_layout);
        preview = new FrameLayout(this);
        preview.setVisibility(View.VISIBLE);
        cameraRecord = new CameraRecord(this, preview, username, "normal_test");
        main.addView(preview);
    }

    private void delete_file(File file)
    {
        if(file != null)
        {
            boolean deleted = file.delete();
        }
    }

    @Override
    public void onClick(View v)
    {

        if(v.equals(recorder))
        {
            File file = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "DENEYLER");

            File user_file = new File(file.getPath(), username);
            File recorded_test = new File(user_file.getPath() + File.separator +
                    username + "_" + "normal_test0" + ".mp4");

            delete_file(recorded_test);

            cameraRecord.record(experiment_id);
            recorder.setEnabled(false);
            Context context = this.getApplication();
            Timer buttonTimer = new Timer();
            buttonTimer.schedule(new TimerTask()
            {

                @Override
                public void run()
                {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run()
                        {
                            cameraRecord.releaseMediaRecorder();
                            cameraRecord.releaseCamera();
                            recorder.setEnabled(true);
                            Toast.makeText(context,"Normal durum testi tamamlandı, deneylere geçebilirsiniz.", Toast.LENGTH_LONG).show();
                            setResult(2);
                            finish();
                        }
                    });
                }
            }, experiment_period);

        }
    }
}