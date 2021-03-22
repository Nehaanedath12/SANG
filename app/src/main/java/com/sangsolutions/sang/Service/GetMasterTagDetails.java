package com.sangsolutions.sang.Service;

import android.annotation.SuppressLint;
import android.app.job.JobParameters;
import android.app.job.JobService;
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
import com.sangsolutions.sang.Adapter.TagDetailsAdapter.TagDetails;
import com.sangsolutions.sang.Database.DatabaseHelper;
import com.sangsolutions.sang.Tools;
import com.sangsolutions.sang.URLs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class GetMasterTagDetails extends JobService {
    DatabaseHelper helper;
    JobParameters params;
    TagDetails details;

    @Override
    public boolean onStartJob(JobParameters params) {
        helper=new DatabaseHelper(this);
        AndroidNetworking.initialize(this);
        this.params=params;
        Log.d("homeFragment","homeFragmentS"+"initial");

        GetAllTag();
        return true;
    }

    private void GetAllTag() {
        for (int i=1;i<=8;i++) {
            GetTag_Details(i);
            Log.d("homeFragment","homeFragmentS"+i);
            if(i==8){
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    public void run() {
                        Toast.makeText(GetMasterTagDetails.this, "TAG Synced", Toast.LENGTH_SHORT).show();

                    }
                });
            }
        }
    }

    private void GetTag_Details(int iType) {
        Log.d("homeFragment","homeFragmentS"+"i");
        AndroidNetworking.get("http://"+ new Tools().getIP(GetMasterTagDetails.this) +URLs.GetMasterTagDetails)
                .addQueryParameter("iType",String.valueOf(iType))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("responseTag",response.toString());
                        loadTagData(response,String.valueOf(iType));
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("response",anError.toString());
                    }
                });
    }

    private void loadTagData(JSONObject response, String iType) {
        @SuppressLint("StaticFieldLeak") AsyncTask<Void,Void,Void> asyncTask=new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    JSONArray jsonArray = new JSONArray(response.getString("Data"));
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        details=new TagDetails(
                                jsonObject.getString(TagDetails.S_CODE),
                                jsonObject.getString(TagDetails.S_NAME),
                                jsonObject.getString(TagDetails.S_ALT_NAME),
                                jsonObject.getInt(TagDetails.I_ID),
                                iType);
                        if(helper.checkTagDetailsById(jsonObject.getString(TagDetails.I_ID),iType)){
                            if(helper.checkAllDataMasterTag(details)){
                                Log.d("success","tag details Updated successfully "+i);
                            }
                        }
                        else if( helper.insertMasterTag(details)){
                            Log.d("success","tag details  added successfully "+i);
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
