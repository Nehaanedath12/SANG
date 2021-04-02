package com.sangsolutions.sang.Service;

import android.annotation.SuppressLint;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.navigation.NavDirections;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.sangsolutions.sang.Database.DatabaseHelper;
import com.sangsolutions.sang.Database.Sales_purchase_Class;
import com.sangsolutions.sang.Fragment.Sale_Purchase_FragmentDirections;
import com.sangsolutions.sang.Tools;
import com.sangsolutions.sang.URLs;

import org.json.JSONArray;
import org.json.JSONObject;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class PostSalePurchaseService extends JobService {
    DatabaseHelper helper;
    JobParameters params;
    String userIdS;


    @Override
    public boolean onStartJob(JobParameters params) {
        helper=new DatabaseHelper(this);
        this.params=params;
        AndroidNetworking.initialize(this);
        Log.d("Post_SalePurchase","iTransId"+"");
        GetDataSalePurchase();
        return true;
    }

    private void GetDataSalePurchase() {
        @SuppressLint("StaticFieldLeak") AsyncTask<Void,Void,Void> asyncTask=new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                Cursor cursor_userId=helper.getUserId();
                if(cursor_userId!=null &&cursor_userId.moveToFirst()) {
                    userIdS = cursor_userId.getString(cursor_userId.getColumnIndex("user_Id"));

                }

                Cursor cursorHeader=helper.getDataFromS_P_HeaderPost();
                Log.d("responsePost1", cursorHeader.getCount()+"mmmm");
                if(cursorHeader.moveToFirst() && cursorHeader.getCount()>0){
                    for (int i=0;i<cursorHeader.getCount();i++){
                        int iTransId=cursorHeader.getInt(cursorHeader.getColumnIndex(Sales_purchase_Class.I_TRANS_ID));
                        int iDocType= cursorHeader.getInt(cursorHeader.getColumnIndex(Sales_purchase_Class.I_DOC_TYPE));
                        String docNo=cursorHeader.getString(cursorHeader.getColumnIndex(Sales_purchase_Class.S_DOC_NO));
                        Log.d("iTransIdd",iTransId+"");
                            JSONObject jsonObjectMain=new JSONObject();
                            try {

                                jsonObjectMain.put("iTransId", "0");
                                jsonObjectMain.put("sDocNo", cursorHeader.getString(cursorHeader.getColumnIndex(Sales_purchase_Class.S_DOC_NO)));
                                jsonObjectMain.put("sDate", Tools.dateFormat(cursorHeader.getString(cursorHeader.getColumnIndex(Sales_purchase_Class.S_DATE))));
                                jsonObjectMain.put("iDocType",  cursorHeader.getInt(cursorHeader.getColumnIndex(Sales_purchase_Class.I_DOC_TYPE)));
                                jsonObjectMain.put("iAccount1",  cursorHeader.getInt(cursorHeader.getColumnIndex(Sales_purchase_Class.I_ACCOUNT_1)));
                                jsonObjectMain.put("iAccount2", 0);
                                jsonObjectMain.put("sNarration",  cursorHeader.getString(cursorHeader.getColumnIndex(Sales_purchase_Class.S_NARRATION)));
                                Log.d("responsePost1 ", jsonObjectMain.get("sNarration")+"mmmm");

                                jsonObjectMain.put("iUser", Integer.parseInt(userIdS));

                                JSONArray jsonArray=new JSONArray();
                                Cursor cursorBody=helper.getEditValuesBodyS_P(iTransId,iDocType);


                                if(cursorBody.moveToFirst() && cursorBody.getCount()>0){

                                    for (int j=0;j<cursorBody.getCount();j++){
                                        JSONObject jsonObject=new JSONObject();
                                        for (int i1=1;i1<=8;i1++){
                                            jsonObject.put("iTag"+i1, cursorBody.getInt(cursorBody.getColumnIndex("iTag"+i1)));
                                        }
                                        jsonObject.put("iProduct",cursorBody.getInt(cursorBody.getColumnIndex(Sales_purchase_Class.I_PRODUCT)));
                                        jsonObject.put("fQty",cursorBody.getInt(cursorBody.getColumnIndex(Sales_purchase_Class.F_QTY)));
                                        jsonObject.put("fRate",cursorBody.getFloat(cursorBody.getColumnIndex(Sales_purchase_Class.F_RATE)));
                                        jsonObject.put("fDiscount",cursorBody.getFloat(cursorBody.getColumnIndex(Sales_purchase_Class.F_DISCOUNT)));
                                        jsonObject.put("fAddCharges",cursorBody.getFloat(cursorBody.getColumnIndex(Sales_purchase_Class.F_ADD_CHARGES)));
                                        jsonObject.put("fVatPer",cursorBody.getFloat(cursorBody.getColumnIndex(Sales_purchase_Class.F_VAT_PER)));
                                        jsonObject.put("fVAT",cursorBody.getFloat(cursorBody.getColumnIndex(Sales_purchase_Class.F_VAT)));
                                        if(cursorBody.getString(cursorBody.getColumnIndex(Sales_purchase_Class.S_REMARKS)).equals("")){
                                            jsonObject.put("sRemarks","");
                                        }else {
                                            jsonObject.put("sRemarks",cursorBody.getString(cursorBody.getColumnIndex(Sales_purchase_Class.S_REMARKS)));
                                        }
                                        jsonObject.put("sUnits",cursorBody.getString(cursorBody.getColumnIndex(Sales_purchase_Class.S_UNITS)));
                                        jsonObject.put("fNet",cursorBody.getFloat(cursorBody.getColumnIndex(Sales_purchase_Class.F_NET)));

                                        jsonArray.put(jsonObject);
                                        cursorBody.moveToNext();
                                    }
                                }

                                jsonObjectMain.put("Body",jsonArray);
                                Log.d("responsePost", jsonObjectMain.toString());
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
        AndroidNetworking.post("http://"+ new Tools().getIP(this) + URLs.PostProductStock)
                .addJSONObjectBody(jsonObjectMain)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("responsePost ", response);
                        if (response.contains(docNo)) {
                            if(helper.deleteSP_Header(iTransId,iDocType,docNo)){
                                if(helper.delete_S_P_Body(iDocType,iTransId)){
                                    Log.d("responsePost ", "successfully");
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
