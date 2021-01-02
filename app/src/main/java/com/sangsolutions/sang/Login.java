package com.sangsolutions.sang;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.sangsolutions.sang.databinding.ActivityLoginBinding;

public class Login extends AppCompatActivity {


    ActivityLoginBinding binding;
    SchedulerJob schedulerJob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        schedulerJob = new SchedulerJob();
        schedulerJob.SyncMasterSettings(this);
        binding.settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),SettingsActivity.class));
            }
        });


    }
}