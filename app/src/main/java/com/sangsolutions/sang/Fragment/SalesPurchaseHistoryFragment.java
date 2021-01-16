package com.sangsolutions.sang.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.sangsolutions.sang.Adapter.SalesPurchaseHistory;
import com.sangsolutions.sang.R;
import com.sangsolutions.sang.SalesPurchaseHistoryAdapter;
import com.sangsolutions.sang.Service.GetAccountsService;
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding=FragmentSalesPurchaseHistoryBinding.inflate(getLayoutInflater());
        historyList=new ArrayList<>();
        historyAdapter=new SalesPurchaseHistoryAdapter(requireActivity(),historyList);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        iDocType = SalesPurchaseHistoryFragmentArgs.fromBundle(getArguments()).getIDocType();


        binding.fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavDirections action=SalesPurchaseHistoryFragmentDirections
                        .actionSalesPurchaseHistoryFragmentToSalePurchaseFragment2().setIDocType(iDocType);

                navController.navigate(action);
            }
        });


        AndroidNetworking.get("http://"+ new Tools().getIP(requireActivity()) + URLs.GetTransSummary)
                .addQueryParameter("iDocType",String.valueOf(iDocType))
                .addQueryParameter("iUser","0")
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


                    binding.recyclerView.setAdapter(historyAdapter);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
