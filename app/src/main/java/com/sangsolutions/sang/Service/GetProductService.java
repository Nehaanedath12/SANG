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
import com.sangsolutions.sang.Adapter.Products.Products;
import com.sangsolutions.sang.Database.DatabaseHelper;
import com.sangsolutions.sang.Tools;
import com.sangsolutions.sang.URLs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class GetProductService extends JobService {

    DatabaseHelper helper;
    JobParameters params;
    Products products;
    @Override
    public boolean onStartJob(JobParameters params) {
        helper=new DatabaseHelper(this);
        this.params=params;
        GetProducts();
        AndroidNetworking.initialize(this);
        return true;
    }

    private void GetProducts() {

        AndroidNetworking.get("http://"+new Tools().getIP(GetProductService.this) + URLs.GetProducts)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("responseProduct",response.toString());
                        loadProductData(response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("response",anError.toString());
                    }
                });
    }

    private void loadProductData(JSONObject response) {
        @SuppressLint("StaticFieldLeak") AsyncTask<Void,Void,Void> asyncTask=new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    JSONArray jsonArray = new JSONArray(response.getString("Data"));
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        products=new Products(
                                jsonObject.getString(Products.S_CODE),
                                jsonObject.getString(Products.S_NAME),
                                jsonObject.getString(Products.S_ALT_NAME),
                                jsonObject.getInt(Products.I_ID));
                        if(helper.checkProductsById(jsonObject.getString(Products.I_ID))){
                            if(helper.checkAllDataProducts(products)){
                                Log.d("success","products Updated successfully "+i+" "+jsonArray.length());
                            }
                        }
                        else if( helper.insertProducts(products)){
                            Log.d("success","products added successfully "+i+"  "+jsonArray.length());
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
