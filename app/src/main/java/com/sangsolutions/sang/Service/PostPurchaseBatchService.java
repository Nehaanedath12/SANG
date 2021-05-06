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

import androidx.annotation.RequiresApi;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.sangsolutions.sang.Database.BatchPurchaseClass;
import com.sangsolutions.sang.Database.DatabaseHelper;
import com.sangsolutions.sang.Database.Sales_purchase_Class;
import com.sangsolutions.sang.Tools;
import com.sangsolutions.sang.URLs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class PostPurchaseBatchService extends JobService {
    DatabaseHelper helper;
    JobParameters params;
    String userIdS;

    @Override
    public boolean onStartJob(JobParameters params) {
        helper=new DatabaseHelper(this);
        this.params=params;
        AndroidNetworking.initialize(this);
        Log.d("Post_PurchaseBatch","iTransId"+"");
        GetDataPurchaseBatch();
        return true;
    }

    private void GetDataPurchaseBatch() {
        @SuppressLint("StaticFieldLeak") AsyncTask<Void,Void,Void> asyncTask=new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                Cursor cursor_userId=helper.getUserId();
                if(cursor_userId!=null &&cursor_userId.moveToFirst()) {
                    userIdS = cursor_userId.getString(cursor_userId.getColumnIndex("user_Id"));

                }
                Cursor cursorHeader=helper.getDataFrom_Batch_P_Header();
                Log.d("responsePostBatchP", cursorHeader.getCount()+"mmmm");
                if(cursorHeader.moveToFirst() && cursorHeader.getCount()>0) {
                    for (int i = 0; i < cursorHeader.getCount(); i++) {
                        int iTransId=cursorHeader.getInt(cursorHeader.getColumnIndex(BatchPurchaseClass.I_TRANS_ID));
                        int iDocType= cursorHeader.getInt(cursorHeader.getColumnIndex(BatchPurchaseClass.I_DOC_TYPE));
                        String docNo=cursorHeader.getString(cursorHeader.getColumnIndex(BatchPurchaseClass.S_DOC_NO));
                        JSONObject jsonObjectMain = new JSONObject();
                        try {
                            if (docNo.contains("L")) {
                                iTransId = 0;
                            }
                            jsonObjectMain.put("iTransId", iTransId);
                            jsonObjectMain.put("sDocNo", docNo);
                            jsonObjectMain.put("sDate", Tools.dateFormat(cursorHeader.getString(cursorHeader.getColumnIndex(BatchPurchaseClass.S_DATE))));
                            jsonObjectMain.put("iDocType", iDocType);
                            jsonObjectMain.put("iAccount1", cursorHeader.getString(cursorHeader.getColumnIndex(BatchPurchaseClass.I_ACCOUNT_1)));
                            jsonObjectMain.put("iAccount2", 0);
                            jsonObjectMain.put("sNarration", cursorHeader.getString(cursorHeader.getColumnIndex(BatchPurchaseClass.S_NARRATION)));
                            assert userIdS != null;
                            jsonObjectMain.put("iUser", Integer.parseInt(userIdS));


                            JSONArray jsonArray = new JSONArray();

                            Cursor cursorBody = helper.getEditValuesBody_BatchP(iTransId, iDocType);
                            if(cursorBody.moveToFirst() && cursorBody.getCount()>0){
                            for (int j = 0; j < cursorBody.getCount(); j++) {

                                JSONObject jsonObject=new JSONObject();
                                for (int i1=1;i1<=8;i1++){
                                    jsonObject.put("iTag"+i1, cursorBody.getInt(cursorBody.getColumnIndex("iTag"+i1)));
                                }
                                jsonObject.put("iProduct",cursorBody.getInt(cursorBody.getColumnIndex(BatchPurchaseClass.I_PRODUCT)));
                                jsonObject.put("fQty",cursorBody.getInt(cursorBody.getColumnIndex(BatchPurchaseClass.F_QTY)));
                                jsonObject.put("fRate",cursorBody.getFloat(cursorBody.getColumnIndex(BatchPurchaseClass.F_RATE)));
                                jsonObject.put("fDiscount",cursorBody.getFloat(cursorBody.getColumnIndex(BatchPurchaseClass.F_DISCOUNT)));
                                jsonObject.put("fAddCharges",cursorBody.getFloat(cursorBody.getColumnIndex(BatchPurchaseClass.F_ADD_CHARGES)));
                                jsonObject.put("fVatPer",cursorBody.getFloat(cursorBody.getColumnIndex(BatchPurchaseClass.F_VAT_PER)));
                                jsonObject.put("fVAT",cursorBody.getFloat(cursorBody.getColumnIndex(BatchPurchaseClass.F_VAT)));
                                if(cursorBody.getString(cursorBody.getColumnIndex(BatchPurchaseClass.S_REMARKS)).equals("")){
                                    jsonObject.put("sRemarks","");
                                }else {
                                    jsonObject.put("sRemarks",cursorBody.getString(cursorBody.getColumnIndex(BatchPurchaseClass.S_REMARKS)));
                                }
                                jsonObject.put("sUnits",cursorBody.getString(cursorBody.getColumnIndex(BatchPurchaseClass.S_UNITS)));
                                jsonObject.put("fNet",cursorBody.getFloat(cursorBody.getColumnIndex(BatchPurchaseClass.F_NET)));


                                JSONArray jsonArrayBatch = new JSONArray();
                                Cursor cursorBodyBatch = helper.getDataFrom_Batch_P_Batch(iTransId, iDocType);
                                if(cursorBodyBatch.moveToFirst() && cursorBodyBatch.getCount()>0) {
                                    for (int k = 0; k < cursorBodyBatch.getCount(); k++) {
                                        JSONObject jsonObjectBatch = new JSONObject();
                                        jsonObjectBatch.put("sBatch", cursorBodyBatch.getString(cursorBodyBatch.getColumnIndex(BatchPurchaseClass.BATCH_NAME)));
                                        jsonObjectBatch.put("fBatchQty", cursorBodyBatch.getInt(cursorBodyBatch.getColumnIndex(BatchPurchaseClass.BATCH_QTY)));
                                        jsonObjectBatch.put("MfDate", Tools.dateFormat(cursorBodyBatch.getString(cursorBodyBatch.getColumnIndex(BatchPurchaseClass.MF_DATE))));
                                        jsonObjectBatch.put("ExpDate", Tools.dateFormat(cursorBodyBatch.getString(cursorBodyBatch.getColumnIndex(BatchPurchaseClass.EXP_DATE))));

                                        jsonArrayBatch.put(jsonObjectBatch);
                                    }
                                }
                                jsonObject.put("Batch", jsonArrayBatch);

                                jsonArray.put(jsonObject);
                            }
                        }

                            jsonObjectMain.put("Body", jsonArray);
                            Log.d("jsonArray", jsonObjectMain.toString());

                            uploadToAPI(jsonObjectMain,docNo,iTransId,iDocType);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }



                return null;
            }
        };
        asyncTask.execute();

        }

    private void uploadToAPI(JSONObject jsonObjectMain, String docNo, int iTransId, int iDocType) {

        AndroidNetworking.post("http://"+ new Tools().getIP(this) + URLs.PostTransWithBatch)
                .addJSONObjectBody(jsonObjectMain)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("responsePost ", response);
                        Log.d("responsePostSP", response);
                        try {
                            int tId = Integer.parseInt(response);
                            if(helper.delete_Batch_P_Header(iTransId,iDocType,docNo)){
                                if(helper.delete_Batch_P_Body(iDocType,iTransId)){
                                    if(helper.delete_Batch_P_Body_batch(iDocType,iTransId)) {
                                        Log.d("responsePost ", "successfully");
                                    }
                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("responsePost", anError.getErrorDetail() + anError.getErrorBody() + anError.toString());
                    }
                });
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
