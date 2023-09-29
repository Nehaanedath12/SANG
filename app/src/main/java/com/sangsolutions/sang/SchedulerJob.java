package com.sangsolutions.sang;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.sangsolutions.sang.Service.GetAccountsService;
import com.sangsolutions.sang.Service.GetBankService;
import com.sangsolutions.sang.Service.GetMasterSettingsService;
import com.sangsolutions.sang.Service.GetMasterTagDetails;
import com.sangsolutions.sang.Service.GetProductService;
import com.sangsolutions.sang.Service.GetTokenService;
import com.sangsolutions.sang.Service.GetTransactionSettingService;
import com.sangsolutions.sang.Service.GetUserService;

public class SchedulerJob {


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void SyncUser(Context context) {
        Log.d("user2","user");
        JobScheduler js =
                (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        JobInfo job = new JobInfo.Builder(
                0,
                new ComponentName(context, GetUserService.class))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .build();
        assert js != null;
        js.schedule(job);
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void SyncMasterSettings(Context context) {
        Log.d("SyncMasterSettings","SyncMasterSettings");

        JobScheduler js =
                (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        JobInfo job = new JobInfo.Builder(
                1,
                new ComponentName(context, GetMasterSettingsService.class))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .build();
        assert js != null;
        js.schedule(job);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void SyncAccounts(Context context) {
        Log.d("SyncAccounts","SyncAccounts");
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
        Log.d("syncProduct","syncProduct");
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
        Log.d("TransSalePurchase","TransSalePurchase");
            JobScheduler js =
                    (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
            JobInfo job = new JobInfo.Builder(
                    4,
                    new ComponentName(context, GetTransactionSettingService.class))
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .build();
            assert js != null;
            js.schedule(job);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void SyncBank(Context context) {
        Log.d("bankk","bankk");
        JobScheduler js =
                (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        JobInfo job = new JobInfo.Builder(
                5,
                new ComponentName(context, GetBankService.class))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .build();
        assert js != null;
        js.schedule(job);
    }



//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    public void SyncToken(Home context) {
//        Log.d("Timingg","Tools.token");
//        JobScheduler js =
//                (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
//        JobInfo job = new JobInfo.Builder(
//                6,
//                new ComponentName(context, GetTokenService.class))
//                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
//                .build();
//        assert js != null;
//        js.schedule(job);
//    }






    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)

    public void syncMasterTagDetails(Context context) {
        Log.d("syncMasterTag","syncMasterTag");

        JobScheduler js =
                    (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
            JobInfo job = new JobInfo.Builder(
                    6,
                    new ComponentName(context, GetMasterTagDetails.class))
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .build();
            assert js != null;
            js.schedule(job);
    }


}
