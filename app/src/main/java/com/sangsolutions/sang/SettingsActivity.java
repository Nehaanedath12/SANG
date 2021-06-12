package com.sangsolutions.sang;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.sangsolutions.sang.databinding.ActivitySettingsBinding;

public class SettingsActivity extends AppCompatActivity {

    ActivitySettingsBinding binding;
    Tools tools;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivitySettingsBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);
        tools=new Tools();

        if(!tools.getIP(SettingsActivity.this).isEmpty()){
            binding.ipAddress.setText(tools.getIP(this));
        }

        binding.save.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                if(binding.ipAddress.getText().toString().equals("")){
                    Toast.makeText(SettingsActivity.this, "Enter Ip Address", Toast.LENGTH_SHORT).show();
                }
                else if(tools.setIP(getApplicationContext(),binding.ipAddress.getText().toString())){
                    startActivity(new Intent(getApplicationContext(),Login.class));
                    Log.d("IpAddress",tools.getIP(getApplicationContext()));
                    finishAffinity();
                }
            }
        });
    }

}