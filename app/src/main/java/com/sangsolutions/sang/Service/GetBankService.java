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
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.sangsolutions.sang.Adapter.BankAdapter.Bank;
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
    @Override
    public boolean onStartJob(JobParameters params) {
        helper=new DatabaseHelper(this);
        Log.d("bankk","bankk");
        this.params=params;
        GetBanks();
        AndroidNetworking.initialize(this);
        return true;
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
            protected Void doInBackground(Void... voids) {
                try {
                    JSONArray jsonArray = new JSONArray(response.toString());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        bank =new Bank(
                                jsonObject.getString(Bank.S_NAME),
                                jsonObject.getString(Bank.S_CODE),
                                jsonObject.getInt(Bank.I_ID));
                        if(helper.checkBankById(jsonObject.getString(Bank.I_ID))){
                            if(helper.checkAllDataBank(bank)){
                                Log.d("success", "Bank Updated successfully "+i);
                            }
                        }
                        else if( helper.insertBanks(bank)){
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
