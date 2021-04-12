package com.sangsolutions.sang.Service;

import android.annotation.SuppressLint;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.PersistableBundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.navigation.NavDirections;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.sangsolutions.sang.Database.CustomerMasterClass;
import com.sangsolutions.sang.Database.DatabaseHelper;
import com.sangsolutions.sang.Database.StockCountDBClass;
import com.sangsolutions.sang.Fragment.MasterFragmentDirections;
import com.sangsolutions.sang.Tools;
import com.sangsolutions.sang.URLs;

import org.json.JSONObject;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class PostCustomerMasterService extends JobService {
    DatabaseHelper helper;
    JobParameters params;
    String userIdS;
    @Override
    public boolean onStartJob(JobParameters params) {
        helper=new DatabaseHelper(this);
        this.params=params;
        AndroidNetworking.initialize(this);
        Log.d("PostCustomerMaster","iTransId"+"");
        GetDataPostCustomerMasterService();
        return true;
    }

    private void GetDataPostCustomerMasterService() {

        @SuppressLint("StaticFieldLeak") AsyncTask<Void,Void,Void> asyncTask=new AsyncTask<Void, Void, Void>() {


            @Override
            protected Void doInBackground(Void... voids) {

                Cursor cursor_userId=helper.getUserId();
                if(cursor_userId!=null &&cursor_userId.moveToFirst()) {
                    userIdS = cursor_userId.getString(cursor_userId.getColumnIndex("user_Id"));
                }

                Cursor cursorHeader=helper.getDataFromCustomerMasterPost();
                Log.d("responsePostMastr", cursorHeader.getCount()+"mmmm");
                if(cursorHeader.moveToFirst() && cursorHeader.getCount()>0) {
                    for (int i = 0; i < cursorHeader.getCount(); i++) {

                        int iId=cursorHeader.getInt(cursorHeader.getColumnIndex(CustomerMasterClass.ID));

                        JSONObject jsonObjectMain = new JSONObject();
                        try {
                            jsonObjectMain.put("iId", iId);
                            jsonObjectMain.put("sName",cursorHeader.getString(cursorHeader.getColumnIndex(CustomerMasterClass.NAME)));
                            jsonObjectMain.put("sCode",   cursorHeader.getString(cursorHeader.getColumnIndex(CustomerMasterClass.CODE)));
                            jsonObjectMain.put("sAltName",    cursorHeader.getString(cursorHeader.getColumnIndex(CustomerMasterClass.ALT_NAME)));
                            jsonObjectMain.put("iCreditDays",   cursorHeader.getString(cursorHeader.getColumnIndex(CustomerMasterClass.CREDIT_DAYS)));

                            jsonObjectMain.put("fCreditAmount", cursorHeader.getString(cursorHeader.getColumnIndex(CustomerMasterClass.CREDIT_AMOUNT)));
                            jsonObjectMain.put("sAddress",cursorHeader.getString(cursorHeader.getColumnIndex(CustomerMasterClass.ADDRESS)));
                            jsonObjectMain.put("sCity",cursorHeader.getString(cursorHeader.getColumnIndex(CustomerMasterClass.CITY)));
                            jsonObjectMain.put("sCountry",cursorHeader.getString(cursorHeader.getColumnIndex(CustomerMasterClass.COUNTRY)));
                            jsonObjectMain.put("sPincode", cursorHeader.getString(cursorHeader.getColumnIndex(CustomerMasterClass.PIN_NO)));

                            jsonObjectMain.put("iMobile",cursorHeader.getString(cursorHeader.getColumnIndex(CustomerMasterClass.MOBILE_NO)));
                            jsonObjectMain.put("iPhone",cursorHeader.getString(cursorHeader.getColumnIndex(CustomerMasterClass.PHONE_NO)));
                            jsonObjectMain.put("sFax",cursorHeader.getString(cursorHeader.getColumnIndex(CustomerMasterClass.FAX)));
                            jsonObjectMain.put("sEmail",cursorHeader.getString(cursorHeader.getColumnIndex(CustomerMasterClass.EMAIL)));
                            jsonObjectMain.put("sWebsite", cursorHeader.getString(cursorHeader.getColumnIndex(CustomerMasterClass.WEBSITE)));

                            jsonObjectMain.put("sContactPersonNo",cursorHeader.getString(cursorHeader.getColumnIndex(CustomerMasterClass.CONTACT_PERSON_NO)));
                            jsonObjectMain.put("iType",cursorHeader.getString(cursorHeader.getColumnIndex(CustomerMasterClass.I_TYPE)));
                            jsonObjectMain.put("iUser", userIdS);
                            Log.d("MasterCustomer","success"+jsonObjectMain.toString());
                            uploadToAPI(jsonObjectMain,iId);
                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }



                return null;
            }
        };
        asyncTask.execute();
        }

    private void uploadToAPI(JSONObject jsonObjectMain, int iId) {
        AndroidNetworking.post("http://"+ new Tools().getIP(this) + URLs.PostCustomer)
                .addJSONObjectBody(jsonObjectMain)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("responseMaster",response);
                        try {
                            int i=Integer.parseInt(response);
                            if(helper.deleteMasterCustomer(iId)){
                                Log.d("responseMaster ", "successfully");
                            }

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("responseMaster",anError.getMessage());
                    }
                });
    }


    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
