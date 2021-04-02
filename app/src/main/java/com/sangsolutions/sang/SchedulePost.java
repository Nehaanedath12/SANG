package com.sangsolutions.sang;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.sangsolutions.sang.Service.PostPaymentReceiptService;
import com.sangsolutions.sang.Service.PostSalePurchaseService;

public class SchedulePost {

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void Post_SalePurchase(Context context) {
        Log.d("Post_SalePurchase","Post_SalePurchase");
        JobScheduler js =
                (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        JobInfo job = new JobInfo.Builder(
                1,
                new ComponentName(context, PostSalePurchaseService.class))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .build();
        Log.d("Post_SalePurchase","Post_SalePurchase");
        assert js != null;
        js.schedule(job);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void Post_PaymentReceipt(Home context) {
        Log.d("Post_PaymentReceipt","Post_PaymentReceipt");
        JobScheduler js =
                (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        JobInfo job = new JobInfo.Builder(
                2,
                new ComponentName(context, PostPaymentReceiptService.class))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .build();
        Log.d("Post_PaymentReceipt","Post_PaymentReceipt");
        assert js != null;
        js.schedule(job);
    }
}
