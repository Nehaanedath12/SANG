package com.sangsolutions.sang;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.sangsolutions.sang.Adapter.User;
import com.sangsolutions.sang.Database.DatabaseHelper;
import com.sangsolutions.sang.databinding.ActivityLoginBinding;

public class Login extends AppCompatActivity {


    ActivityLoginBinding binding;
    SchedulerJob schedulerJob;
    DatabaseHelper helper;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onResume() {
        super.onResume();
        syncData();
    }

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

        if (helper.GetLoginStatus()) {
            startActivity(new Intent(Login.this, Home.class));
            finish();
            syncData();
        }
        
        binding.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!binding.userName.getText().toString().isEmpty()){
                    if(!binding.password.getText().toString().isEmpty()){

                        if(!new Tools().getIP(Login.this).isEmpty()) {
                            User u = new User();
                            u.setsLoginName(binding.userName.getText().toString().trim());
                            u.setsPassword(binding.password.getText().toString().trim());

                                schedulerJob.SyncUser(Login.this);
                                if (helper.GetUser()) {
                                    syncData();
                                    if (helper.loginUser(u)) {
                                        if (helper.InsertCurrentLoginUser(u)) {
                                            startActivity(new Intent(Login.this, Home.class));
                                            finish();

                                        } else {
                                            Toast.makeText(Login.this, "An unexpected error occurred!", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(Login.this, "enter correct username and password", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(Login.this, "Check your network or IP", Toast.LENGTH_SHORT).show();
                                }
                            } else {
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
        schedulerJob.SyncUser(this);
        schedulerJob.SyncMasterSettings(this);
        schedulerJob.SyncAccounts(this);
        schedulerJob.SyncProduct(this);
        schedulerJob.SyncTransSalePurchase(this);

    }
}