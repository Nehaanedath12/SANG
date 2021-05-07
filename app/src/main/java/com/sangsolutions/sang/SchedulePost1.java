package com.sangsolutions.sang;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.sangsolutions.sang.Service.PostCustomerMasterService;
import com.sangsolutions.sang.Service.PostPurchaseBatchService;
import com.sangsolutions.sang.Service.Post_EnquiryRequestService;
import com.sangsolutions.sang.Service.Post_QuotationService;
import com.sangsolutions.sang.Service.Post_StockCountService;

public class SchedulePost1 {

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void Post_EnquiryRequest(Context context) {
        Log.d("Post_EnquiryRequest","Post_EnquiryRequest");
        JobScheduler js =
                (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        JobInfo job = new JobInfo.Builder(
                5,
                new ComponentName(context, Post_EnquiryRequestService.class))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .build();
        Log.d("Post_EnquiryRequest","Post_EnquiryRequest");
        assert js != null;
        js.schedule(job);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void Post_Quotation(Context context) {
        Log.d("Post_Quotation","Post_Quotation");
        JobScheduler js =
                (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        JobInfo job = new JobInfo.Builder(
                6,
                new ComponentName(context, Post_QuotationService.class))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .build();
        Log.d("Post_Quotation","Post_Quotation");
        assert js != null;
        js.schedule(job);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void Post_StockCount(Context context) {
        Log.d("Post_StockCount","Post_StockCount");
        JobScheduler js =
                (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        JobInfo job = new JobInfo.Builder(
                7,
                new ComponentName(context, Post_StockCountService.class))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .build();
        Log.d("Post_StockCount","Post_StockCount");
        assert js != null;
        js.schedule(job);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void Post_CustomerMaster(Context context) {
        Log.d("Post_CustomerMaster","Post_CustomerMaster");
        JobScheduler js =
                (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        JobInfo job = new JobInfo.Builder(
                8,
                new ComponentName(context, PostCustomerMasterService.class))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .build();
        Log.d("Post_CustomerMaster","Post_CustomerMaster");
        assert js != null;
        js.schedule(job);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void Post_PurchaseBatch(Context context) {
        Log.d("PostPurchaseBatch","PostPurchaseBatch");
        JobScheduler js =
                (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        JobInfo job = new JobInfo.Builder(
                9,
                new ComponentName(context, PostPurchaseBatchService.class))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .build();
        Log.d("PostPurchaseBatch","PostPurchaseBatch");
        assert js != null;
        js.schedule(job);
    }
}
