package com.sangsolutions.sang.Fragment;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.material.snackbar.Snackbar;
import com.sangsolutions.sang.Adapter.TagDetailsAdapter.TagDetails;
import com.sangsolutions.sang.Database.DatabaseHelper;
import com.sangsolutions.sang.R;
import com.sangsolutions.sang.SchedulerJob;
import com.sangsolutions.sang.Tools;
import com.sangsolutions.sang.URLs;
import com.sangsolutions.sang.databinding.FragmentHomeBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HomeFragment extends Fragment {
    FragmentHomeBinding binding;
    NavController navController;
    NavDirections action;
    SchedulerJob schedulerJob;
    TagDetails details;
    DatabaseHelper helper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding=FragmentHomeBinding.inflate(getLayoutInflater());

        navController = Navigation.findNavController(requireActivity(),R.id.nav_host_fragment);
        schedulerJob=new SchedulerJob();
        helper=new DatabaseHelper(requireContext());


        binding.purchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                action=HomeFragmentDirections.actionHomeFragmentToSalesPurchaseHistoryFragment().setIDocType(1);
                navController.navigate(action);

            }
        });
        binding.sale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                action=HomeFragmentDirections.actionHomeFragmentToSalesPurchaseHistoryFragment().setIDocType(2);
                navController.navigate(action);

            }
        });
        binding.sync.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                if(Tools.isConnected(requireActivity())) {
                    Log.d("homeFragment","homeFragment");
//                    schedulerJob.syncMasterTagDetails(requireActivity());
                    syncTag();
                }
                else {
                    Snackbar snackbar=Snackbar.make(v,"Offline",Snackbar.LENGTH_LONG);
                    snackbar.setTextColor(Color.WHITE);
                    snackbar.setBackgroundTint(Color.RED);
                    snackbar.show();
                }
            }
        });



        return binding.getRoot();


    }

    private void syncTag() {
        AndroidNetworking.initialize(requireContext());
        GetAllTag();
    }

    private void GetAllTag() {
        for (int i=1;i<=8;i++) {
            GetTag_Details(i);
            if(i==8){
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    public void run() {
                        Toast.makeText(requireContext(), "TAG Synced", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    private void GetTag_Details(int iType) {
        AndroidNetworking.get("http://"+new Tools().getIP(requireContext()) + URLs.GetMasterTagDetails)
                .addQueryParameter("iType",String.valueOf(iType))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("responseTag",response.toString());
                        loadTagData(response,String.valueOf(iType));
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("response",anError.toString());
                    }
                });
    }

    private void loadTagData(JSONObject response, String iType) {
        try {
            JSONArray jsonArray = new JSONArray(response.getString("Data"));
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                details = new TagDetails(
                        jsonObject.getString(TagDetails.S_CODE),
                        jsonObject.getString(TagDetails.S_NAME),
                        jsonObject.getString(TagDetails.S_ALT_NAME),
                        jsonObject.getInt(TagDetails.I_ID),
                        iType);

                if (helper.checkTagDetailsById(jsonObject.getString(TagDetails.I_ID), iType)) {
                    if (helper.checkAllDataMasterTag(details)) {
                    }
                } else if (helper.insertMasterTag(details)) {
                    Log.d("successTag", "tag details  added successfully " + i);
                }
            }
        }
            catch (JSONException e) {
            e.printStackTrace();
    }



        }
    }

