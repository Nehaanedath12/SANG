package com.sangsolutions.sang.Service;

import android.annotation.SuppressLint;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.database.Cursor;
import android.net.Uri;
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
import com.sangsolutions.sang.Database.Payment_Receipt_class;
import com.sangsolutions.sang.Database.Sales_purchase_Class;
import com.sangsolutions.sang.Fragment.PaymentReceiptFragmentDirections;
import com.sangsolutions.sang.Tools;
import com.sangsolutions.sang.URLs;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class PostPaymentReceiptService extends JobService {
    DatabaseHelper helper;
    JobParameters params;
    String userIdS;
    @Override
    public boolean onStartJob(JobParameters params) {
        helper=new DatabaseHelper(this);
        this.params=params;
        AndroidNetworking.initialize(this);
        Log.d("Payment_Receipt_class","iTransId"+"");
        GetDataPaymentReceipt();
        return true;
    }

    private void GetDataPaymentReceipt() {
        @SuppressLint("StaticFieldLeak") AsyncTask<Void,Void,Void> asyncTask=new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {

                Cursor cursor_userId=helper.getUserId();
                if(cursor_userId!=null &&cursor_userId.moveToFirst()) {
                    userIdS = cursor_userId.getString(cursor_userId.getColumnIndex("user_Id"));
                }

                Cursor cursorHeader=helper.getDataFrom_PayRec_HeaderPost();
                Log.d("responsePostPR", cursorHeader.getCount()+"mmmm");
                if(cursorHeader.moveToFirst() && cursorHeader.getCount()>0){
                    for (int i=0;i<cursorHeader.getCount();i++){
                        int iTransId=cursorHeader.getInt(cursorHeader.getColumnIndex(Payment_Receipt_class.I_TRANS_ID));
                        int iDocType= cursorHeader.getInt(cursorHeader.getColumnIndex(Payment_Receipt_class.I_DOC_TYPE));
                        String docNo=cursorHeader.getString(cursorHeader.getColumnIndex(Sales_purchase_Class.S_DOC_NO));

                        Log.d("iTransIdd",iTransId+"");

                        String Image=cursorHeader.getString(cursorHeader.getColumnIndex(Payment_Receipt_class.S_ATTACHMENT));
                        String imageName=Tools.getFileList(Image);
                        Log.d("iTransIdd",imageName+""+Image);

                        List<String> imgList = new ArrayList<>(Arrays.asList(Image.split(",")));
                        List<File> file = new ArrayList<>();
                        for (int k=0;k<imgList.size();k++) {
                            File file1 = new File(imgList.get(k));
                            if (file1.exists()) {
                                file.add(Tools.CompressImage(file1,PostPaymentReceiptService.this));
                            }
                        }

                        JSONObject jsonObjectMain=new JSONObject();
                        try {
                            if(docNo.contains("L")) {
                                jsonObjectMain.put("iTransId", "0");
                            }
                            else {
                                jsonObjectMain.put("iTransId", iTransId);
                            }
                            jsonObjectMain.put("sDocNo", cursorHeader.getString(cursorHeader.getColumnIndex(Payment_Receipt_class.S_DOC_NO)));
                            jsonObjectMain.put("sDate", Tools.dateFormat( cursorHeader.getString(cursorHeader.getColumnIndex(Payment_Receipt_class.S_DATE))));
                            jsonObjectMain.put("iDocType", iDocType);
                            jsonObjectMain.put("iAccount1",  cursorHeader.getInt(cursorHeader.getColumnIndex(Payment_Receipt_class.I_ACCOUNT_1)));
                            jsonObjectMain.put("iAccount2", 0);
                            jsonObjectMain.put("sNarration",  cursorHeader.getString(cursorHeader.getColumnIndex(Payment_Receipt_class.S_NARRATION)));
                            jsonObjectMain.put("fAmount", cursorHeader.getFloat(cursorHeader.getColumnIndex(Payment_Receipt_class.F_AMOUNT)));
                            jsonObjectMain.put("iPaymentMethod", cursorHeader.getInt(cursorHeader.getColumnIndex(Payment_Receipt_class.I_PAYMENT_METHOD)));
                            int iPaymentMethod= cursorHeader.getInt(cursorHeader.getColumnIndex(Payment_Receipt_class.I_PAYMENT_METHOD));
                            if (iPaymentMethod == 2) {
                                jsonObjectMain.put("iBank",  cursorHeader.getInt(cursorHeader.getColumnIndex(Payment_Receipt_class.I_BANK)));
                                jsonObjectMain.put("iChequeNo",  cursorHeader.getInt(cursorHeader.getColumnIndex(Payment_Receipt_class.I_CHEQUE_NO)));
                                jsonObjectMain.put("sAttachment", imageName);
                            } else if (iPaymentMethod == 1) {
                                jsonObjectMain.put("iBank", 0);
                                jsonObjectMain.put("iChequeNo", 0);
                                jsonObjectMain.put("sAttachment", "");
                            }
                            jsonObjectMain.put("sChequeDate", Tools.dateFormat (cursorHeader.getString(cursorHeader.getColumnIndex(Payment_Receipt_class.S_CHEQUE_DATE))));

                            assert userIdS != null;
                            jsonObjectMain.put("iUser", Integer.parseInt(userIdS));


                            JSONObject jsonObject = new JSONObject();
                            JSONArray jsonArray=new JSONArray();
                            Cursor cursorBody=helper.getEditValuesBodyP_ayRec(iTransId,iDocType);
                            if(cursorBody.moveToFirst() && cursorBody.getCount()>0) {
                                for (int j = 1; j <= 8; j++) {
                                    jsonObject.put("iTag" + j, cursorBody.getInt(cursorBody.getColumnIndex("iTag" + j)));

                                }

                                jsonObject.put("iRefDocId", 0);
                                jsonObject.put("fAmount", 0);
                                jsonArray.put(jsonObject);
                            }

                        jsonObjectMain.put("Body",jsonArray);
                        Log.d("jsonnn",jsonObjectMain.toString());
                            uploadToAPI(jsonObjectMain,file,docNo,iTransId,iDocType);


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

    private void uploadToAPI(JSONObject jsonObjectMain, List<File> files, String docNo, int iTransId, int iDocType) {
        Log.d("jsonObjectMain",jsonObjectMain.toString()+" "+files.toString());
            AndroidNetworking.upload("http://"+ new Tools().getIP(this)+ URLs.Post_Receipt_Payment)
                    .addMultipartParameter("json_content",jsonObjectMain.toString())
                    .setContentType("multipart/form-data")
                    .addMultipartFileList("file",files)
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsString(new StringRequestListener() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("responsePost", response);
                            if (response.contains("-")) {
                                if(helper.deletePayRec_Header(iTransId,iDocType,docNo)){
                                    if(helper.delete_PayRec_Body(iDocType,iTransId)){
                                        Log.d("responsePost ", "successfully");
                                    }
                                }
                                Log.d("responsePost_R_P", "successfully");
                                Toast.makeText(PostPaymentReceiptService.this, "Posted successfully", Toast.LENGTH_SHORT).show();

                            }
                        }
                        @Override
                        public void onError(ANError anError) {
                            Log.d("responsePost_R_P", anError.getErrorDetail() + anError.getErrorBody() + anError.toString());
                        }
                    });

    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
