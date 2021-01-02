package com.sangsolutions.sang;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.sangsolutions.sang.Service.GetAccountsService;
import com.sangsolutions.sang.Service.GetMasterSettingsService;
import com.sangsolutions.sang.Service.GetProductService;
import com.sangsolutions.sang.Service.GetTransactionSetting;

public class SchedulerJob {

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void SyncMasterSettings(Context context) {

        JobScheduler js =
                (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        JobInfo job = new JobInfo.Builder(
                0,
                new ComponentName(context, GetMasterSettingsService.class))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .build();
        assert js != null;
        js.schedule(job);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void SyncAccounts(Context context) {
        JobScheduler js =
                (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        JobInfo job = new JobInfo.Builder(
                2,
                new ComponentName(context, GetAccountsService.class))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .build();
        assert js != null;
        js.schedule(job);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void SyncProduct(Context context) {
            JobScheduler js =
                    (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
            JobInfo job = new JobInfo.Builder(
                    3,
                    new ComponentName(context, GetProductService.class))
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .build();
            assert js != null;
            js.schedule(job);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void SyncTransSalePurchase(Context context) {
            JobScheduler js =
                    (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
            JobInfo job = new JobInfo.Builder(
                    4,
                    new ComponentName(context, GetTransactionSetting.class))
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .build();
            assert js != null;
            js.schedule(job);
    }
}
