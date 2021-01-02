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
import com.sangsolutions.sang.Adapter.TransSalePurchase.TransSetting;
import com.sangsolutions.sang.DatabaseHelper;
import com.sangsolutions.sang.Login;
import com.sangsolutions.sang.Tools;
import com.sangsolutions.sang.URLs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class GetTransactionSetting extends JobService {
    DatabaseHelper helper;
    JobParameters params;
    TransSetting transSalePurchase;

    @Override
    public boolean onStartJob(JobParameters params) {
        helper=new DatabaseHelper(this);
        this.params=params;
        AndroidNetworking.initialize(this);
        for (int i=1;i<=2;i++){
            GetTransSetting(String.valueOf(i));
        }
        return true;
    }

    private void GetTransSetting(String iDocType) {
        Log.d("iiiii",iDocType);

        AndroidNetworking.get("http://"+new Tools().getIP(GetTransactionSetting.this) + URLs.GetTransSettings)
                .addQueryParameter("iDocType",iDocType)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("response",response.toString());
                        loadPurchaseSalesData(response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("response",anError.toString());
                    }
                });

    }

    private void loadPurchaseSalesData(JSONObject response) {
        @SuppressLint("StaticFieldLeak") AsyncTask<Void,Void,Void> asyncTask=new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    JSONArray jsonArray = new JSONArray(response.getString("Data"));
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        transSalePurchase=new TransSetting(
                                jsonObject.getInt(TransSetting.I_DOC_TYPE),
                                jsonObject.getInt(TransSetting.I_TAG_ID),
                                jsonObject.getBoolean(TransSetting.B_VISIBLE),
                                jsonObject.getBoolean(TransSetting.B_MANDATORY));

                        if(helper.checkTransSettingById(jsonObject.getString(TransSetting.I_DOC_TYPE),jsonObject.getString(TransSetting.I_TAG_ID))){
                            if(helper.checkAllDataTransSetting(transSalePurchase)){
                                Log.d("success","transSalePurchase Updated successfully "+i);
                            }
                        }
                        else if( helper.insertTransSetting(transSalePurchase)){
                            Log.d("success","transSalePurchase added successfully "+i);
                        }

                        if(i+1==jsonArray.length()){
                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.post(new Runnable() {
                                public void run() {
                                    Toast.makeText(GetTransactionSetting.this, "sale_purchase Synced", Toast.LENGTH_SHORT).show();

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
