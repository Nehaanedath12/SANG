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
import com.sangsolutions.sang.Database.Sales_purchase_order_class;
import com.sangsolutions.sang.Database.StockCountDBClass;
import com.sangsolutions.sang.Tools;
import com.sangsolutions.sang.URLs;

import org.json.JSONArray;
import org.json.JSONObject;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class Post_StockCountService extends JobService {
    DatabaseHelper helper;
    JobParameters params;
    String userIdS;

    @Override
    public boolean onStartJob(JobParameters params) {
        helper=new DatabaseHelper(this);
        this.params=params;
        AndroidNetworking.initialize(this);
        Log.d("Post_StockCountService","iTransId"+"");
        GetDataStockCountService();
        return true;
    }

    private void GetDataStockCountService() {
        @SuppressLint("StaticFieldLeak") AsyncTask<Void,Void,Void> asyncTask=new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {

                Cursor cursor_userId=helper.getUserId();
                if(cursor_userId!=null &&cursor_userId.moveToFirst()) {
                    userIdS = cursor_userId.getString(cursor_userId.getColumnIndex("user_Id"));
                }

                Cursor cursorHeader=helper.getDataFromStockCountHeaderPost();
                Log.d("responsePost1", cursorHeader.getCount()+"mmmm");
                if(cursorHeader.moveToFirst() && cursorHeader.getCount()>0) {
                    for (int i = 0; i < cursorHeader.getCount(); i++) {

                        int iTransId=cursorHeader.getInt(cursorHeader.getColumnIndex(StockCountDBClass.I_TRANS_ID));
                        int iDocType= cursorHeader.getInt(cursorHeader.getColumnIndex(StockCountDBClass.I_DOC_TYPE));
                        String docNo=cursorHeader.getString(cursorHeader.getColumnIndex(StockCountDBClass.S_DOC_NO));
                        Log.d("iTransIdd",iTransId+"");

                        JSONObject jsonObjectMain = new JSONObject();
                        try {
                            if(docNo.contains("L")) {
                                jsonObjectMain.put("iTransId", "0");
                            }
                            else {
                                jsonObjectMain.put("iTransId", iTransId);
                            }
                            jsonObjectMain.put("sDocNo", cursorHeader.getString(cursorHeader.getColumnIndex(StockCountDBClass.S_DOC_NO)));
                            jsonObjectMain.put("sDate", Tools.dateFormat(cursorHeader.getString(cursorHeader.getColumnIndex(StockCountDBClass.S_DATE))));
                            jsonObjectMain.put("sStockDate", Tools.dateFormat(cursorHeader.getString(cursorHeader.getColumnIndex(StockCountDBClass.STOCK_DATE))));
                            jsonObjectMain.put("iDocType", cursorHeader.getInt(cursorHeader.getColumnIndex(StockCountDBClass.I_DOC_TYPE)));
                            jsonObjectMain.put("sNarration", cursorHeader.getString(cursorHeader.getColumnIndex(StockCountDBClass.S_NARRATION)));
                            assert userIdS != null;
                            jsonObjectMain.put("iUser", Integer.parseInt(userIdS));

                            JSONArray jsonArray=new JSONArray();
                            Cursor cursorBody=helper.getEditValuesBodyStockCount(iTransId,iDocType);
                            if(cursorBody.moveToFirst() && cursorBody.getCount()>0) {

                                for (int j = 0; j < cursorBody.getCount(); j++) {
                                    JSONObject jsonObject = new JSONObject();
                                    for (int i1 = 1; i1 <= 8; i1++) {
                                        jsonObject.put("iTag" + i1, cursorBody.getInt(cursorBody.getColumnIndex("iTag" + i1)));
                                    }

                                    jsonObject.put("iProduct",cursorBody.getInt(cursorBody.getColumnIndex(StockCountDBClass.I_PRODUCT)));
                                    jsonObject.put("fQty",cursorBody.getInt(cursorBody.getColumnIndex(StockCountDBClass.F_QTY)));
                                    if(cursorBody.getString(cursorBody.getColumnIndex(StockCountDBClass.S_REMARKS)).equals("")){
                                        jsonObject.put("sRemarks","");
                                    }else {
                                        jsonObject.put("sRemarks",cursorBody.getString(cursorBody.getColumnIndex(StockCountDBClass.S_REMARKS)));
                                    }
                                    jsonObject.put("sUnits",cursorBody.getString(cursorBody.getColumnIndex(StockCountDBClass.S_UNITS)));

                                    jsonArray.put(jsonObject);
                                    cursorBody.moveToNext();
                                }
                            }
                            jsonObjectMain.put("Body",jsonArray);
                            Log.d("responsePostStockCont", jsonObjectMain.toString());
                            uploadToAPI(jsonObjectMain,docNo,iTransId,iDocType);



                        }catch (Exception e){
                            Log.d("exceptionPost",e.getMessage());
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
        AndroidNetworking.post("http://"+ new Tools().getIP(this)+ URLs.PostTransStock)
                .addJSONObjectBody(jsonObjectMain)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        if (response.contains("-")) {
                            if(helper.deleteStockCountHeader(iTransId,iDocType,docNo)) {
                                if (helper.delete_StockCount_Body(iDocType, iTransId)) {
                                    Log.d("responsePostStockCont ", "successfully");
                                }
                            }
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
