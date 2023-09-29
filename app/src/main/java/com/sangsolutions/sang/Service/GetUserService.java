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
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.sangsolutions.sang.Adapter.User;
import com.sangsolutions.sang.Database.DatabaseHelper;
import com.sangsolutions.sang.Tools;
import com.sangsolutions.sang.URLs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class GetUserService extends JobService {

    DatabaseHelper helper;
    JobParameters params;
    User user;

    @Override
    public boolean onStartJob(JobParameters params) {
        helper=new DatabaseHelper(this);
        this.params=params;
        Log.d("ZZZUserr","User1");
        GetUsers();
        AndroidNetworking.initialize(this);
        return true;
    }

    private void GetUsers() {
        AndroidNetworking.get("http://"+ new Tools().getIP(GetUserService.this) + URLs.GetUserLogin)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("responseUser",response.toString());
                        loadUserData(response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("responseUser",anError.toString());

                    }
                });

    }

    private void loadUserData(JSONArray response) {
        @SuppressLint("StaticFieldLeak") AsyncTask<Void,Void,Void> asyncTask=new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
            }

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    JSONArray jsonArray = new JSONArray(response.toString());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        user=new User(
                                jsonObject.getInt(User.I_ID),
                                jsonObject.getString(User.S_LOGIN_NAME),
                                jsonObject.getString(User.S_PASSWORD),
                                jsonObject.getInt(User.I_STATUS),
                                jsonObject.getString(User.S_USERNAME),
                                jsonObject.getString(User.B_WEB),
                                jsonObject.getString(User.B_MOB),
                                jsonObject.getString(User.USER_CODE));
                        if(helper.checkUserById(jsonObject.getString(User.I_ID))){

                            if(helper.checkAllDataUser(user)){
                                Log.d("success","User Updated successfully "+i);
                            }
                        }
                        else if( helper.insertUser(user)){
                            Log.d("success","User added successfully "+i);
                        }

                        if(i+1==jsonArray.length()){
                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.post(new Runnable() {
                                public void run() {
//                                    Toast.makeText(GetProductService.this, "products Synced", Toast.LENGTH_SHORT).show();

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
