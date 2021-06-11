package com.example.experiments_for_pupil_dilation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.experiments_for_pupil_dilation.experiments.ExperimentCenter;

import java.util.ArrayList;

public class UserOperations extends AppCompatActivity implements View.OnClickListener
{
    private Button submit;
    private EditText text;
    private Spinner registered_username;
    private String username;
    private ArrayList<String> valid_usernames;
    private Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_operations);
        db = new Database(this);
        username = this.getIntent().getStringExtra("username");
        submit = findViewById(R.id.submitButton);
        text = findViewById(R.id.editUsername);
        registered_username = findViewById(R.id.registeredUsername);
        submit.setOnClickListener(this);
        valid_usernames = getUsername();
        set_valid_usernames();

    }

    private void set_valid_usernames()
    {
        if (valid_usernames == null)
        {
            valid_usernames = new ArrayList<>();
        }
        valid_usernames.add(0, "Kullanıcı Adı Seç");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, R.layout.spinner_item, valid_usernames);
        registered_username.setAdapter(adapter);
    }

    private ArrayList<String> getUsername()
    {
        String [][] db_result  = db.getData();
        ArrayList<String> valid_usernames = new ArrayList<>();
        if (db_result.length == 0)
        {
            return null;
        }
        else
        {
            for (int i = 0; i <db_result.length ; i++)
            {
                if(!valid_usernames.contains(db_result[i][0]))
                    valid_usernames.add(db_result[i][0]);
            }
            return valid_usernames;
        }
    }

    private void add_username()
    {
        if(valid_usernames == null)
        {
            db.addData(username, "null");
        }
        else
        {
            if(!valid_usernames.contains(username))
            {
                db.addData(username, "null");
            }
        }
    }

    private void set_username()
    {
        Object selected_item = registered_username.getSelectedItem();

        String selected_old_name = selected_item.toString();

        String new_name = text.getText().toString();

        if(! new_name.isEmpty() && selected_old_name != "Kullanıcı Adı Seç")
        {
            Toast.makeText(this,"Hem yeni kullanıcı ismi hem de " +
                    "eski kullanıcı ismi aynı anda onaylanamaz.", Toast.LENGTH_SHORT).show();
        }
        else if(! new_name.isEmpty() && selected_old_name == "Kullanıcı Adı Seç")
        {
            username = new_name;
        }
        else if(selected_old_name != "Kullanıcı Adı Seç" && new_name.isEmpty())
        {
            username = selected_old_name;
        }
    }


    @Override
    public void onClick(View v)
    {
        set_username();
        if (v.equals(submit) && username != null)
        {
            add_username();
            Intent experiment_center = new Intent(this, ExperimentCenter.class);
            experiment_center.putExtra("username", username);
            setResult(1, experiment_center);
            finish();
        }
        if (v.equals(submit) && username == null)
        {
            Toast.makeText(this,"Kullanıcı adınızı girin ya da seçin. Ardından butona basın.", Toast.LENGTH_SHORT).show();
        }
    }
}