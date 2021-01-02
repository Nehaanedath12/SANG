package com.sangsolutions.sang;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.sangsolutions.sang.databinding.ActivityLoginBinding;

public class Login extends AppCompatActivity {


    ActivityLoginBinding binding;
    SchedulerJob schedulerJob;
    DatabaseHelper helper;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        helper=new DatabaseHelper(this);
        schedulerJob = new SchedulerJob();


        binding.settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),SettingsActivity.class));
            }
        });
        
        binding.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!binding.userName.getText().toString().isEmpty()){
                    if(!binding.password.getText().toString().isEmpty()){
                        if(!new Tools().getIP(Login.this).isEmpty()){
                            syncData();

                        }
                        else {
                            Toast.makeText(Login.this, "Please enter IP Address", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        binding.password.setError("enter Password");
                    }
                }
                else {
                    binding.userName.setError("enter Username");
                }
            }
        });


    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void syncData() {
        schedulerJob.SyncMasterSettings(this);
        schedulerJob.SyncAccounts(this);
        schedulerJob.SyncProduct(this);
        schedulerJob.SyncTransSalePurchase(this);

    }
}