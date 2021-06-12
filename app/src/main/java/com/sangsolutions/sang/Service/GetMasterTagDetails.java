package com.sangsolutions.sang.Service;

import android.annotation.SuppressLint;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.sangsolutions.sang.Adapter.TagDetailsAdapter.TagDetails;
import com.sangsolutions.sang.Commons;
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
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    public boolean onStartJob(JobParameters params) {
        helper=new DatabaseHelper(this);
        AndroidNetworking.initialize(this);
        this.params=params;
        Log.d("homeFragment","homeFragmentS"+"initial");
        preferences = getSharedPreferences(Commons.PREFERENCE_SYNC,MODE_PRIVATE);
        editor = preferences.edit();

        for (int i=1;i<=8;i++) {
            if(helper.getTagDetailsCount(i)==0) {
                GetAllTag(i);
            }else {
                UpdateTagAllDetails(i);
            }
        }


//        if(helper.getTagDetailsCount(i)==0) {
//            GetAllTag(i);
//        }else {
//            UpdateTagAllDetails();
//        }
        return true;
    }

    private void UpdateTagAllDetails(int iType) {
            UpdateTag_Details(iType);
            Log.d("homeFragment","homeFragmentUp"+iType);

    }

    private void UpdateTag_Details(int iType) {
        int maxId=helper.getTagDetailsMaxId();
        AndroidNetworking.get("http://"+  new Tools().getIP(GetMasterTagDetails.this) +URLs.GetUpdateMasterTagDetails)
                .addQueryParameter("MaxId", String.valueOf(maxId))
                .addQueryParameter("iType", String.valueOf(iType))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("responseTag_Details",response.toString());
                        updateTagDetailsValues(response,String.valueOf(iType));
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("responseMaster",anError.toString());
                    }
                });
    }

    private void updateTagDetailsValues(JSONObject response, String iType) {
        @SuppressLint("StaticFieldLeak") AsyncTask<Void,Void,Void> asyncTask=new AsyncTask<Void, Void, Void>() {


            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                editor.putString(Commons.TAG_DETAILS,"true").apply();

            }

            @Override
            protected Void doInBackground(Void... voids) {

                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(response.getString("Table"));

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    int iStatus = jsonObject.getInt("iStatus");
                    Log.d("iStatus", iStatus + "");
                    details = new TagDetails(
                            jsonObject.getString(TagDetails.S_CODE),
                            jsonObject.getString(TagDetails.S_NAME),
                            jsonObject.getString(TagDetails.S_ALT_NAME),
                            jsonObject.getInt(TagDetails.I_ID),
                            iType);

                    if (iStatus == 0) {
                        if (helper.insertMasterTag(details)) {
                            Log.d("successUpdate", "Tag UpdateInsert successfully" + i);
                        }
                    } else if (iStatus == 1) {
                        if (helper.editTagDetails(details,iType)) {
                            Log.d("successUpdate", "Tag UpdateEdit successfully " + i);

                        }
                    }
                    else if(iStatus==2){
                        if(helper.deleteTagDetails(details,iType)){
                            Log.d("successUpdate", "Tag Update Delete successfully " + i);
                        }
                    }

                }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };asyncTask.execute();

        }

    private void GetAllTag(int iType) {
            GetTag_Details(iType);
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
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                editor.putString(Commons.TAG_DETAILS,"true").apply();

            }

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
                      if( helper.insertMasterTag(details)){
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
