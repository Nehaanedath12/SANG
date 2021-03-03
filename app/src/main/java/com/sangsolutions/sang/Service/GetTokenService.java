package com.sangsolutions.sang.Service;

import android.annotation.SuppressLint;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.sangsolutions.sang.Adapter.BankAdapter.Bank;
import com.sangsolutions.sang.Commons;
import com.sangsolutions.sang.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class GetTokenService extends JobService {
    int exp_time;
    String token;

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d("Timingg","Tools.tokens");
        GetToken();

        return true;
    }

    private void GetToken() {

        AndroidNetworking.post("http://185.151.4.167/focus/token")
                .addBodyParameter("username", "sa")
                .addBodyParameter("password", "123456")
                .addBodyParameter("grant_type", "password")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loadToken(response);
                        Log.d("responseTokener",response.toString());

                    }
                    @Override
                    public void onError(ANError anError) {
                        Log.d("responseTokener",anError.toString());
                    }
                });

    }

    private void loadToken(JSONObject response) {

        @SuppressLint("StaticFieldLeak") AsyncTask<Void,Void,Void> asyncTask=new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                        JSONObject jsonObject=new JSONObject(response.toString());
                        exp_time =jsonObject.getInt("expires_in");
                        token=jsonObject.getString("access_token");
                        Commons.token=token;
                        Log.d("Timingg",Commons.token+"  " +exp_time);
                        callTimer(exp_time);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                return null;
            }

        };
        asyncTask.execute();
    }

    private void callTimer(int exp_time) {
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                      GetToken();
                    }
                },
                exp_time*1000
        );
    }


    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
