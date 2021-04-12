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
import com.sangsolutions.sang.Database.RequestEnquiryClass;
import com.sangsolutions.sang.Database.Sales_purchase_order_class;
import com.sangsolutions.sang.Fragment.RequestFragmentDirections;
import com.sangsolutions.sang.Tools;
import com.sangsolutions.sang.URLs;

import org.json.JSONArray;
import org.json.JSONObject;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class Post_EnquiryRequestService extends JobService {
    DatabaseHelper helper;
    JobParameters params;
    String userIdS;
    @Override
    public boolean onStartJob(JobParameters params) {
        helper=new DatabaseHelper(this);
        this.params=params;
        AndroidNetworking.initialize(this);
        Log.d("Post_EnquiryRequest","iTransId"+"");
        GetDataEnquiryRequestService();
        return true;
    }

    private void GetDataEnquiryRequestService() {
        @SuppressLint("StaticFieldLeak") AsyncTask<Void,Void,Void> asyncTask=new AsyncTask<Void, Void, Void>() {


            @Override
            protected Void doInBackground(Void... voids) {
                Cursor cursor_userId=helper.getUserId();
                if(cursor_userId!=null &&cursor_userId.moveToFirst()) {
                    userIdS = cursor_userId.getString(cursor_userId.getColumnIndex("user_Id"));
                }

                Cursor cursorHeader=helper.getDataFromEnquiryRequestHeaderPost();
                Log.d("responsePostER", cursorHeader.getCount()+"");
                if(cursorHeader.moveToFirst() && cursorHeader.getCount()>0) {
                    for (int i = 0; i < cursorHeader.getCount(); i++) {
                        int iTransId=cursorHeader.getInt(cursorHeader.getColumnIndex(RequestEnquiryClass.I_TRANS_ID));
                        int iDocType= cursorHeader.getInt(cursorHeader.getColumnIndex(RequestEnquiryClass.I_DOC_TYPE));
                        String docNo=cursorHeader.getString(cursorHeader.getColumnIndex(RequestEnquiryClass.S_DOC_NO));
                        Log.d("iTransIdd",iTransId+"");

                        JSONObject jsonObjectMain = new JSONObject();
                        try {
                            if(docNo.contains("L")) {
                                jsonObjectMain.put("iTransId", "0");
                            }
                            else {
                                jsonObjectMain.put("iTransId", iTransId);
                            }
                            jsonObjectMain.put("sDocNo", cursorHeader.getString(cursorHeader.getColumnIndex(RequestEnquiryClass.S_DOC_NO)));
                            jsonObjectMain.put("sDate", Tools.dateFormat(cursorHeader.getString(cursorHeader.getColumnIndex(RequestEnquiryClass.S_DATE))));
                            jsonObjectMain.put("iDocType", cursorHeader.getInt(cursorHeader.getColumnIndex(RequestEnquiryClass.I_DOC_TYPE)));
                            jsonObjectMain.put("sNarration", cursorHeader.getString(cursorHeader.getColumnIndex(RequestEnquiryClass.S_NARRATION)));
                            assert userIdS != null;
                            jsonObjectMain.put("iUser", Integer.parseInt(userIdS));

                            JSONArray jsonArray=new JSONArray();
                            Cursor cursorBody=helper.getEditValuesBodyRequestEnquiry(iTransId,iDocType);

                            if(cursorBody.moveToFirst() && cursorBody.getCount()>0) {

                                for (int j = 0; j < cursorBody.getCount(); j++) {
                                    JSONObject jsonObject = new JSONObject();
                                    for (int i1 = 1; i1 <= 8; i1++) {
                                        jsonObject.put("iTag" + i1, cursorBody.getInt(cursorBody.getColumnIndex("iTag" + i1)));
                                    }

                                    jsonObject.put("iProduct",cursorBody.getInt(cursorBody.getColumnIndex(RequestEnquiryClass.I_PRODUCT)));
                                    jsonObject.put("fQty",cursorBody.getInt(cursorBody.getColumnIndex(RequestEnquiryClass.F_QTY)));
                                    if(cursorBody.getString(cursorBody.getColumnIndex(RequestEnquiryClass.S_REMARKS)).equals("")){
                                        jsonObject.put("sRemarks","");
                                    }else {
                                        jsonObject.put("sRemarks",cursorBody.getString(cursorBody.getColumnIndex(RequestEnquiryClass.S_REMARKS)));
                                    }
                                    jsonObject.put("sUnits",cursorBody.getString(cursorBody.getColumnIndex(RequestEnquiryClass.S_UNITS)));

                                    jsonArray.put(jsonObject);
                                    cursorBody.moveToNext();
                                }
                            }


                            jsonObjectMain.put("Body",jsonArray);
                            Log.d("responsePostOrder", jsonObjectMain.toString());
                            uploadToAPI(jsonObjectMain,docNo,iTransId,iDocType);


                        }catch (Exception e){
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
        AndroidNetworking.post("http://"+ new Tools().getIP(this)+ URLs.PostTransRequest)
                .addJSONObjectBody(jsonObjectMain)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        if (response.contains("-")) {
                            if(helper.deleteRequestEnquiry_Header(iTransId,iDocType,docNo)) {
                                if (helper.deleteRequestEnquiry_Body(iDocType, iTransId)) {
                                    Log.d("responsePostER ", "successfully");

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
