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
import com.sangsolutions.sang.Adapter.Customer.Customer;
import com.sangsolutions.sang.Database.DatabaseHelper;
import com.sangsolutions.sang.Tools;
import com.sangsolutions.sang.URLs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class GetAccountsService extends JobService {

    DatabaseHelper helper;
    JobParameters params;
    Customer customer;

    @Override
    public boolean onStartJob(JobParameters params) {
        helper=new DatabaseHelper(this);
        this.params=params;
        GetAccounts();
        AndroidNetworking.initialize(this);
        return true;
    }

    private void GetAccounts() {
        AndroidNetworking.get("http://"+ URLs.GetAccounts)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("responseAccounts",response.toString());
                        loadAccountsData(response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("response",anError.toString());
                    }
                });
    }

    private void loadAccountsData(JSONObject response) {
        @SuppressLint("StaticFieldLeak") AsyncTask<Void,Void,Void> asyncTask=new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    JSONArray jsonArray = new JSONArray(response.getString("Data"));
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        customer =new Customer(
                                jsonObject.getString(Customer.S_CODE),
                                jsonObject.getString(Customer.S_NAME),
                                jsonObject.getString(Customer.S_ALT_NAME),
                                jsonObject.getInt(Customer.I_ID));
                        if(helper.checkAccountsById(jsonObject.getString(Customer.I_ID))){
                            if(helper.checkAllDataAccounts(customer)){
                                Log.d("success","accounts Updated successfully "+i);
                            }
                        }
                        else if( helper.insertAccounts(customer)){
                            Log.d("success","accounts added successfully "+i);
                        }

                        if(i+1==jsonArray.length()){
                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.post(new Runnable() {
                                public void run() {
//                                    Toast.makeText(GetAccountsService.this, "accounts Synced", Toast.LENGTH_SHORT).show();

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
