package com.example.experiments_for_pupil_dilation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.experiments_for_pupil_dilation.camera.CameraRecord;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class LightTest extends AppCompatActivity implements View.OnClickListener
{

    Button start;
    String username;

    private int experiment_id = 0;
    private CameraRecord cameraRecord;
    private RelativeLayout main;
    private FrameLayout preview;
    private int experiment_period = 6 * 1000;

    static String DEFAULT_EXPERIMENT_NAME = "light_test0.mp4";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light_test);
        username = getIntent().getStringExtra("username");
        init_layout();
        init_button();
    }

    private void init_layout()
    {
        main = findViewById(R.id.light_test);
        preview = new FrameLayout(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
                );
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        cameraRecord = new CameraRecord(this, preview, username, "light_test");
        main.addView(preview, params);
    }

    private void init_button()
    {
        start = new Button(this);
        //start = findViewById(R.id.light_test_start);
        start.setOnClickListener(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        start.setText("Işık Kontrolünü Başlat");
        //params.gravity = Gravity.BOTTOM;
        start.setLayoutParams(params);
        main.addView(start);
    }

    private File access_file()
    {
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), "DENEYLER");

        File user_file = new File(file.getPath(), username);
        File recorded_test = new File(user_file.getPath() + File.separator +
                username + "_" + DEFAULT_EXPERIMENT_NAME);
        return recorded_test;
    }

    private double brightness_value()
    {
        File recorded = access_file();

        MediaMetadataRetriever media = new MediaMetadataRetriever();
        media.setDataSource(recorded.toString());
        Bitmap extractedImage = media.getFrameAtTime(100000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);

        int redColors = 0;
        int greenColors = 0;
        int blueColors = 0;
        int pixelCount = 0;

        for (int y = 0; y < extractedImage.getHeight(); y++)
        {
            for (int x = 0; x < extractedImage.getWidth(); x++)
            {
                int c = extractedImage.getPixel(x, y);
                pixelCount++;
                redColors += Color.red(c);
                greenColors += Color.green(c);
                blueColors += Color.blue(c);
            }
        }
        // calculate average of bitmap r,g,b values
        int red = (redColors/pixelCount);
        int green = (greenColors/pixelCount);
        int blue = (blueColors/pixelCount);

        String colorValues = "RGB :" + red+ " "+ green + " "+ blue ;

        double brightness = 0.2126 * red + 0.7152 * green + 0.0722 * blue ;
        return brightness;
    }

    @Override
    public void onClick(View v)
    {

        if(v.equals(start))
        {
            File recorded = access_file();
            recorded.delete();
            start.setEnabled(false);
            Context context = this.getApplication();
            Timer buttonTimer = new Timer();
            cameraRecord.record(experiment_id);
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
                            start.setEnabled(true);
                            double brightness = brightness_value();
                            String result = String.format("%.2f", brightness);
                            if(brightness > 100)
                            {
                                Toast.makeText(context,"Işık testi tamamlandı, deneylere geçebilirsiniz." +
                                       "Işık değeri: " + result, Toast.LENGTH_LONG).show();
                                setResult(200);
                                finish();
                            }
                            else
                            {
                                cameraRecord = new CameraRecord(context, preview, username, "light_test");
                                Toast.makeText(context,"Ortamınızı değiştirip deneyi tekrarlayın." +
                                        "Işık değeri: " + result, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }, experiment_period);

        }
    }
}