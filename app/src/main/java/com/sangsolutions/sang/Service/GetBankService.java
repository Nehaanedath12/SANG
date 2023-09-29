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
import com.sangsolutions.sang.Adapter.BankAdapter.Bank;
import com.sangsolutions.sang.Commons;
import com.sangsolutions.sang.Database.DatabaseHelper;
import com.sangsolutions.sang.Tools;
import com.sangsolutions.sang.URLs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class GetBankService extends JobService {

    DatabaseHelper helper;
    JobParameters params;
    Bank bank;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    @Override
    public boolean onStartJob(JobParameters params) {
        helper=new DatabaseHelper(this);
        Log.d("ZZZbankk","banks");
        this.params=params;

        preferences = getSharedPreferences(Commons.PREFERENCE_SYNC,MODE_PRIVATE);
        editor = preferences.edit();

        AndroidNetworking.initialize(this);
        if(helper.getBank()==0) {
            GetBanks();
        }else {
            UpdateBank();
        }


        return true;
    }

    private void UpdateBank() {
        int maxId=helper.getBankMaxId();
        AndroidNetworking.get("http://"+  new Tools().getIP(GetBankService.this) +URLs.GetUpdateBank)
                .addQueryParameter("MaxId", String.valueOf(maxId))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("responseBankUp",response.toString());
                        updateBankValues(response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("responseBankUp",anError.toString());
                    }
                });
    }

    private void updateBankValues(JSONObject response) {
        @SuppressLint("StaticFieldLeak") AsyncTask<Void,Void,Void> asyncTask=new AsyncTask<Void, Void, Void>() {


            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                editor.putString(Commons.BANK,"true").apply();
            }

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    JSONArray jsonArray = new JSONArray(response.getString("Table"));
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        int iStatus=jsonObject.getInt("iStatus");
                        Log.d("iStatus",iStatus+"");
                        bank = new Bank(
                                jsonObject.getString(Bank.S_NAME),
                                jsonObject.getString(Bank.S_CODE),
                                jsonObject.getInt(Bank.I_ID));


                        if(iStatus==0){
                            if(helper.insertBanks(bank)){
                                Log.d("successUpdate", "Banks UpdateInsert successfully"+i);
                            }
                        }
                        else if(iStatus==1) {
                            if (helper.editBanks(bank)) {
                                Log.d("successUpdate", "Banks UpdateEdit successfully " + i);

                            }
                        }
                        else if(iStatus==2){
                            if(helper.deleteBanks(bank)){
                                Log.d("successUpdate", "Banks Update Delete successfully " + i);

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

    private void GetBanks() {

        AndroidNetworking.get("http://"+ new Tools().getIP(GetBankService.this)+ URLs.GetBanks)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        loadBankData(response);
                        Log.d("responseBank",response.toString());

                    }

                    @Override
                    public void onError(ANError anError) {

                        Toast.makeText(GetBankService.this, "check Ip or Internet", Toast.LENGTH_SHORT).show();
                        Log.d("responseBank",anError.toString());
                    }
                });
    }

    private void loadBankData(JSONArray response) {
        @SuppressLint("StaticFieldLeak") AsyncTask<Void,Void,Void> asyncTask=new AsyncTask<Void, Void, Void>() {


            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                editor.putString(Commons.BANK,"true").apply();

            }

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    JSONArray jsonArray = new JSONArray(response.toString());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        bank =new Bank(
                                jsonObject.getString(Bank.S_NAME),
                                jsonObject.getString(Bank.S_CODE),
                                jsonObject.getInt(Bank.I_ID));
                     if( helper.insertBanks(bank)){
                            Log.d("success","bank added successfully "+i);
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
