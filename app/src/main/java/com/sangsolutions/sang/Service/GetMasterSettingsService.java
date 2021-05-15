package com.sangsolutions.sang.Service;

import android.annotation.SuppressLint;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.sangsolutions.sang.Adapter.MasterSettings.MasterSettings;
import com.sangsolutions.sang.Commons;
import com.sangsolutions.sang.Database.DatabaseHelper;
import com.sangsolutions.sang.SchedulerJob;
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
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    public boolean onStartJob(JobParameters params) {
        helper=new DatabaseHelper(this);
        this.params=params;

        preferences = getSharedPreferences(Commons.PREFERENCE_SYNC,MODE_PRIVATE);
        editor = preferences.edit();

        Log.d("SyncMasterSettings1","SyncMasterSettings");
        AndroidNetworking.initialize(this);
        GetMaster();

        return true;
    }

    private void UpdateMaster() {
        int maxId=helper.getTagDetailsMaxId();
        AndroidNetworking.get("http://"+  new Tools().getIP(GetMasterSettingsService.this) +URLs.GetUpdateMasterTagDetails)
                .addQueryParameter("MaxId", String.valueOf(maxId))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("responseMasterUp",response.toString());
                        updateMasterValues(response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("responseMaster",anError.toString());
                    }
                });

    }

    private void updateMasterValues(JSONObject response) {
        @SuppressLint("StaticFieldLeak") AsyncTask<Void,Void,Void> asyncTask=new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {

                try {
                    JSONArray jsonArray = new JSONArray(response.getString("Table"));
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        int iStatus = jsonObject.getInt("iStatus");
                        Log.d("iStatus", iStatus + "");
                        settings = new MasterSettings(
                                jsonObject.getString(MasterSettings.S_NAME),
                                jsonObject.getString(MasterSettings.S_ALT_NAME),
                                jsonObject.getInt(MasterSettings.I_ID));

                        if (iStatus == 0) {
                            if (helper.insertMasterSettings(settings)) {
                                Log.d("successUpdate", "Master UpdateInsert successfully" + i);
                            }
                        } else if (iStatus == 1) {
                            if (helper.editMasterSettings(settings)) {
                                Log.d("successUpdate", "Master UpdateEdit successfully " + i);

                            }
                        }
                        else if(iStatus==2){
                            if(helper.deleteMaster(settings)){
                                Log.d("successUpdate", "Master Update Delete successfully " + i);
                            }
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                return null;
            }
        };asyncTask.execute();

        }

    private void GetMaster() {

        AndroidNetworking.get("http://"+ new Tools().getIP(GetMasterSettingsService.this) +URLs.GetMasterSettings)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("responseMaster",response.toString());
                        if(helper.deleteMasterAll()) {
                            loadMasterData(response);
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Toast.makeText(GetMasterSettingsService.this, "check Ip or Internet", Toast.LENGTH_SHORT).show();
                        Log.d("responseMaster",anError.toString());
                    }
                });
    }

    private void loadMasterData(JSONObject response) {
        @SuppressLint("StaticFieldLeak") AsyncTask<Void,Void,Void>asyncTask=new AsyncTask<Void, Void, Void>() {


            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                editor.putString(Commons.MASTER_SETTINGS,"true").apply();

            }

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
                    if( helper.insertMasterSettings(settings)){
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
        Toast.makeText(this, "MasterSetting Synced", Toast.LENGTH_SHORT).show();

        JobScheduler js =
                (JobScheduler) getApplicationContext().getSystemService(Context.JOB_SCHEDULER_SERVICE);
        JobInfo job = new JobInfo.Builder(
                0,
                new ComponentName(getApplicationContext(), GetMasterSettingsService.class))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .build();
        assert js != null;
        js.schedule(job);
        return true;
    }
}
