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
import com.sangsolutions.sang.Adapter.MasterSettings.MasterSettings;
import com.sangsolutions.sang.Database.DatabaseHelper;
import com.sangsolutions.sang.Tools;
import com.sangsolutions.sang.URLs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class GetMasterSettingsService extends JobService {

    DatabaseHelper helper;
    JobParameters params;
    MasterSettings settings;
    @Override
    public boolean onStartJob(JobParameters params) {
        helper=new DatabaseHelper(this);
        this.params=params;
        GetMaster();
        AndroidNetworking.initialize(this);
        return true;
    }

    private void GetMaster() {

        AndroidNetworking.get("http://"+new Tools().getIP(GetMasterSettingsService.this) + URLs.GetMasterSettings)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("response",response.toString());
                        loadMasterData(response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("response",anError.toString());
                    }
                });
    }

    private void loadMasterData(JSONObject response) {
        @SuppressLint("StaticFieldLeak") AsyncTask<Void,Void,Void>asyncTask=new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    JSONArray jsonArray = new JSONArray(response.getString("Data"));
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                         settings=new MasterSettings(
                                 jsonObject.getString(MasterSettings.S_NAME),
                                 jsonObject.getString(MasterSettings.S_ALT_NAME),
                                 jsonObject.getInt(MasterSettings.I_ID));
                         if(helper.checkMasterById(jsonObject.getString(MasterSettings.I_ID))){
                             if(helper.checkAllDataMaster(settings)){
                                 Log.d("success","Master Updated successfully "+i);
                             }
                         }
                        else if( helper.insertMasterSettings(settings)){
                            Log.d("success","Master added successfully "+i);
                        }

                        if(i+1==jsonArray.length()){
                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.post(new Runnable() {
                                public void run() {
//                                        Toast.makeText(GetMasterSettingsService.this, "Master Synced", Toast.LENGTH_SHORT).show();

                                }
                            });
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }

        };
        asyncTask.execute();
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
