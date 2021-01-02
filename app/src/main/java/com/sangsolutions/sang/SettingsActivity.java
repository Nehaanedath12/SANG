package com.sangsolutions.sang;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.sangsolutions.sang.databinding.ActivitySettingsBinding;

public class SettingsActivity extends AppCompatActivity {

    ActivitySettingsBinding binding;
    Tools tools;
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
            @Override
            public void onClick(View v) {
                if(!Tools.isValidIP(binding.ipAddress.getText().toString())){
                    Toast.makeText(SettingsActivity.this, "Enter Valid IP", Toast.LENGTH_SHORT).show();
                }
                else if(tools.setIP(getApplicationContext(),binding.ipAddress.getText().toString())){
                    startActivity(new Intent(getApplicationContext(),Login.class));
                    finishAffinity();
                }
            }
        });
    }
}