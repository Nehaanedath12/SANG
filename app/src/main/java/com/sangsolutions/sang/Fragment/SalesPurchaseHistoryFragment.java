package com.sangsolutions.sang.Fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.sangsolutions.sang.Adapter.SalesPurchaseHistory;
import com.sangsolutions.sang.Database.DatabaseHelper;
import com.sangsolutions.sang.R;
import com.sangsolutions.sang.SalesPurchaseHistoryAdapter;
import com.sangsolutions.sang.Tools;
import com.sangsolutions.sang.URLs;
import com.sangsolutions.sang.databinding.FragmentSalesPurchaseHistoryBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SalesPurchaseHistoryFragment extends Fragment {
    FragmentSalesPurchaseHistoryBinding binding;

    int iDocType;
    String title;
    NavController navController;
    List<SalesPurchaseHistory>historyList;
    SalesPurchaseHistoryAdapter historyAdapter;
    AlertDialog alertDialog;
    DatabaseHelper helper;
    String userIdS=null;
    String toolTitle;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding=FragmentSalesPurchaseHistoryBinding.inflate(getLayoutInflater());
        helper=new DatabaseHelper(requireContext());
        historyList=new ArrayList<>();
        historyAdapter=new SalesPurchaseHistoryAdapter(requireActivity(),historyList);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        assert getArguments() != null;
        iDocType = SalesPurchaseHistoryFragmentArgs.fromBundle(getArguments()).getIDocType();
        if (iDocType == 1) {
            toolTitle = "Purchase";
        } else {
            toolTitle = "Sales";
        }

        Cursor cursor_userId=helper.getUserId();
        if(cursor_userId!=null &&cursor_userId.moveToFirst()) {
            userIdS = cursor_userId.getString(cursor_userId.getColumnIndex("user_Id"));
        }

        binding.fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavDirections action=SalesPurchaseHistoryFragmentDirections
                        .actionSalesPurchaseHistoryFragmentToSalePurchaseFragment2(toolTitle).setIDocType(iDocType);

                navController.navigate(action);
            }
        });

        AlertDialog.Builder builder=new AlertDialog.Builder(requireActivity());
        View view=LayoutInflater.from(requireActivity()).inflate(R.layout.progress_bar,null,false);
        builder.setView(view);
        builder.setCancelable(false);
        alertDialog = builder.create();
        alertDialog.show();


        AndroidNetworking.get("http://"+ new Tools().getIP(requireActivity()) + URLs.GetTransSummary)
                        .addQueryParameter("iDocType",String.valueOf(iDocType))
                        .addQueryParameter("iUser",userIdS)
                        .setPriority(Priority.MEDIUM)
                        .build()
                        .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                        Log.d("responseHistory",response.toString());
                        loadDatas(response);
                        }

                        @Override
                        public void onError(ANError anError) {
                        Log.d("responseHistory",anError.toString());
                        alertDialog.dismiss();
                    }
                    });

        return binding.getRoot();

    }

    private void loadDatas(JSONObject response) {

                        try {
                        JSONArray jsonArray = new JSONArray(response.getString("Data"));

                        for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        Log.d("I_TRANS_ID",jsonObject.getInt(SalesPurchaseHistory.I_ACCOUNT1)+"");
                        SalesPurchaseHistory history=new SalesPurchaseHistory(
                        jsonObject.getInt(SalesPurchaseHistory.I_TRANS_ID),
                        jsonObject.getInt(SalesPurchaseHistory.I_ACCOUNT1),
                        jsonObject.getInt(SalesPurchaseHistory.N_AMOUNT),
                        jsonObject.getString(SalesPurchaseHistory.S_DOC_NO),
                        jsonObject.getString(SalesPurchaseHistory.S_DATE),
                        jsonObject.getString(SalesPurchaseHistory.S_ACCOUNT1),
                        jsonObject.getString(SalesPurchaseHistory.S_ACCOUNT2)

                        );

                    historyList.add(history);
                    if(i+1==jsonArray.length()){
                    alertDialog.dismiss();
                    binding.recyclerView.setAdapter(historyAdapter);
                    }
                    }
                    } catch (JSONException e) {
                    e.printStackTrace();
                    }
    }
}
