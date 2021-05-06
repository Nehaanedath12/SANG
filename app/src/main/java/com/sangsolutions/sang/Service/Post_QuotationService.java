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
import com.sangsolutions.sang.Database.DatabaseHelper;
import com.sangsolutions.sang.Database.SP_QuotationClass;

import com.sangsolutions.sang.Tools;
import com.sangsolutions.sang.URLs;

import org.json.JSONArray;
import org.json.JSONObject;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class Post_QuotationService extends JobService {
    DatabaseHelper helper;
    JobParameters params;
    String userIdS;

    @Override
    public boolean onStartJob(JobParameters params) {
        helper=new DatabaseHelper(this);
        this.params=params;
        AndroidNetworking.initialize(this);
        Log.d("Post_Quotation","iTransId"+"");
        GetDataQuotation();
        return true;
    }

    private void GetDataQuotation() {
        @SuppressLint("StaticFieldLeak") AsyncTask<Void,Void,Void> asyncTask=new AsyncTask<Void, Void, Void>() {


            @Override
            protected Void doInBackground(Void... voids) {
                Cursor cursor_userId=helper.getUserId();
                if(cursor_userId!=null &&cursor_userId.moveToFirst()) {
                    userIdS = cursor_userId.getString(cursor_userId.getColumnIndex("user_Id"));
                }
                Cursor cursorHeader=helper.getDataFromQuotation_HeaderPost();

                Log.d("responsePost1", cursorHeader.getCount()+"mmmm");
                if(cursorHeader.moveToFirst() && cursorHeader.getCount()>0) {
                    for (int i = 0; i < cursorHeader.getCount(); i++) {
                        int iTransId=cursorHeader.getInt(cursorHeader.getColumnIndex(SP_QuotationClass.I_TRANS_ID));
                        int iDocType= cursorHeader.getInt(cursorHeader.getColumnIndex(SP_QuotationClass.I_DOC_TYPE));
                        String docNo=cursorHeader.getString(cursorHeader.getColumnIndex(SP_QuotationClass.S_DOC_NO));
                        Log.d("iTransIdd",iTransId+"");
                        JSONObject jsonObjectMain=new JSONObject();
                        try {

                            if(docNo.contains("L")) {
                                jsonObjectMain.put("iTransId", "0");
                            }
                            else {
                                jsonObjectMain.put("iTransId", iTransId);
                            }
                            jsonObjectMain.put("sDocNo", cursorHeader.getString(cursorHeader.getColumnIndex(SP_QuotationClass.S_DOC_NO)));
                            jsonObjectMain.put("sDate", Tools.dateFormat(cursorHeader.getString(cursorHeader.getColumnIndex(SP_QuotationClass.S_DATE))));
                            jsonObjectMain.put("iDocType", cursorHeader.getInt(cursorHeader.getColumnIndex(SP_QuotationClass.I_DOC_TYPE)));
                            jsonObjectMain.put("iAccount1", cursorHeader.getInt(cursorHeader.getColumnIndex(SP_QuotationClass.I_ACCOUNT_1)));
                            jsonObjectMain.put("iAccount2", 0);
                            jsonObjectMain.put("sNarration", cursorHeader.getString(cursorHeader.getColumnIndex(SP_QuotationClass.S_NARRATION)));
                            Log.d("responsePost1 ", jsonObjectMain.get("sNarration") + "mmmm");

                            jsonObjectMain.put("iUser", Integer.parseInt(userIdS));

                            JSONArray jsonArray=new JSONArray();
                            Cursor cursorBody=helper.getEditValuesBodyQuotation(iTransId,iDocType);

                            if(cursorBody.moveToFirst() && cursorBody.getCount()>0) {

                                for (int j = 0; j < cursorBody.getCount(); j++) {
                                    JSONObject jsonObject = new JSONObject();

                                    for (int i1=1;i1<=8;i1++){
                                        jsonObject.put("iTag"+i1, cursorBody.getInt(cursorBody.getColumnIndex("iTag"+i1)));
                                    }
                                    jsonObject.put("iProduct",cursorBody.getInt(cursorBody.getColumnIndex(SP_QuotationClass.I_PRODUCT)));
                                    jsonObject.put("fQty",cursorBody.getInt(cursorBody.getColumnIndex(SP_QuotationClass.F_QTY)));
                                    jsonObject.put("fRate",cursorBody.getFloat(cursorBody.getColumnIndex(SP_QuotationClass.F_RATE)));
                                    if(cursorBody.getString(cursorBody.getColumnIndex(SP_QuotationClass.S_REMARKS)).equals("")){
                                        jsonObject.put("sRemarks","");
                                    }else {
                                        jsonObject.put("sRemarks",cursorBody.getString(cursorBody.getColumnIndex(SP_QuotationClass.S_REMARKS)));
                                    }
                                    jsonObject.put("sUnits",cursorBody.getString(cursorBody.getColumnIndex(SP_QuotationClass.S_UNITS)));

                                    jsonArray.put(jsonObject);
                                    cursorBody.moveToNext();


                                }
                            }


                            jsonObjectMain.put("Body",jsonArray);
                            Log.d("responsePost", jsonObjectMain.toString());
                            uploadToAPI(jsonObjectMain,docNo,iTransId,iDocType);

                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                        cursorHeader.moveToNext();
                    }
                }

                return null;
            }
        };
        asyncTask.execute();

        }

    private void uploadToAPI(JSONObject jsonObjectMain, String docNo, int iTransId, int iDocType) {
        AndroidNetworking.post("http://"+ new Tools().getIP(this)+ URLs.PostTransQuotation)
                .addJSONObjectBody(jsonObjectMain)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("responsePostQ ", response);

                        if (response.contains("-")) {

                            if(helper.deleteQuotationHeader(iTransId,iDocType,docNo)) {
                                if (helper.delete_Quotation_Body(iDocType, iTransId)) {
                                    Log.d("responsePostQ ", "successfully");

                                }
                            }
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("responsePostQ", anError.getErrorDetail() + anError.getErrorBody() + anError.toString());
                    }
                });
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
