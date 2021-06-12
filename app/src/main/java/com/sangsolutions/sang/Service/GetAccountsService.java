package com.sangsolutions.sang.Service;

import android.annotation.SuppressLint;
import android.app.job.JobParameters;
import android.app.job.JobService;
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
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.sangsolutions.sang.Adapter.Customer.Customer;
import com.sangsolutions.sang.Commons;
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
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    public boolean onStartJob(JobParameters params) {
        helper=new DatabaseHelper(this);
        this.params=params;
        Log.d("responseAccounts","response.toString()");
        AndroidNetworking.initialize(this);

        preferences = getSharedPreferences(Commons.PREFERENCE_SYNC,MODE_PRIVATE);
        editor = preferences.edit();

        if(helper.getAccounts()==0) {
            GetAccounts();
        }else {
            UpdateAccounts();
        }
        return true;
    }

    private void UpdateAccounts() {
        int maxId=helper.getAccountsMaxId();
        AndroidNetworking.get("http://"+  new Tools().getIP(GetAccountsService.this) +URLs.GetUpdateCustomer)
                .addQueryParameter("MaxId", String.valueOf(maxId))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("responseAccountsUp",response.toString());
                        updateAccountsValues(response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("responseAccounts",anError.toString());
                    }
                });
    }

    private void updateAccountsValues(JSONObject response) {
        @SuppressLint("StaticFieldLeak") AsyncTask<Void,Void,Void> asyncTask=new AsyncTask<Void, Void, Void>() {


            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                editor.putString(Commons.ACCOUNTS,"true").apply();
            }

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    JSONArray jsonArray = new JSONArray(response.getString("Table"));
                    for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    int iStatus=jsonObject.getInt("iStatus");
                    Log.d("iStatus",iStatus+"");
                    customer = new Customer(
                            jsonObject.getString(Customer.S_CODE),
                            jsonObject.getString(Customer.S_NAME),
                            jsonObject.getString(Customer.S_ALT_NAME),
                            jsonObject.getInt(Customer.I_ID));

                    if(iStatus==0){
                        if(helper.insertAccounts(customer)){
                            Log.d("successUpdate", "Accounts UpdateInsert successfully"+i);
                        }
                    }
                    else if(iStatus==1) {
                        if (helper.editAccounts(customer)) {
                            Log.d("successUpdate", "Accounts UpdateEdit successfully " + i);

                        }
                    }
                    else if(iStatus==2){
                        if(helper.deleteAccounts(customer)){
                            Log.d("successUpdate", "Accounts Update Delete successfully " + i);

                        }
                    }
                }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("exceptionn",e.toString());
                }


                return null;
            }
        };asyncTask.execute();

    }

    private void GetAccounts() {
        AndroidNetworking.get("http://"+  new Tools().getIP(GetAccountsService.this) +URLs.GetAccounts)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("responseAccounts",response.toString());
                        loadAccountsData(response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("responseAccounts",anError.toString());

                    }
                });

    }

    private void loadAccountsData(JSONArray response) {
        @SuppressLint("StaticFieldLeak") AsyncTask<Void,Void,Void> asyncTask=new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                editor.putString(Commons.ACCOUNTS,"true").apply();

            }

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    JSONArray jsonArray = new JSONArray(response.toString());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        customer =new Customer(
                                jsonObject.getString(Customer.S_CODE),
                                jsonObject.getString(Customer.S_NAME),
                                jsonObject.getString(Customer.S_ALT_NAME),
                                jsonObject.getInt(Customer.I_ID));

                         if( helper.insertAccounts(customer)){
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
