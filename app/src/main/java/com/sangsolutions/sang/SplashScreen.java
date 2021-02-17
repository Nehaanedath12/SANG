package com.sangsolutions.sang;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        Thread thread=new Thread(){
            @Override
            public void run() {
                try{
                    sleep(2000);

//                    if(!new Tools().getIP(SplashScreen.this).isEmpty()){
                        startActivity(new Intent(getApplicationContext(),Login.class));
//                    }
//                    else {
//                        startActivity(new Intent(getApplicationContext(),SettingsActivity.class));
//                    }
                    finish();

                }catch (Exception e){
                    e.printStackTrace();
                }
                super.run();
            }
        };
        thread.start();
    }
}