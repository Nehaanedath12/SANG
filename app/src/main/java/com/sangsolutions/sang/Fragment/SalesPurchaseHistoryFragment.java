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

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.sangsolutions.sang.R;
import com.sangsolutions.sang.databinding.FragmentSalesPurchaseHistoryBinding;

import org.json.JSONArray;
import org.json.JSONObject;

public class SalesPurchaseHistoryFragment extends Fragment {
    FragmentSalesPurchaseHistoryBinding binding;

    int iDocType;
    String title;
    NavController navController;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding=FragmentSalesPurchaseHistoryBinding.inflate(getLayoutInflater());

        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        iDocType = SalesPurchaseHistoryFragmentArgs.fromBundle(getArguments()).getIDocType();
//        title=SalesPurchaseHistoryFragmentArgs.fromBundle(getArguments()).getToolTitle();
        Log.d("iDocType",iDocType+"");
        if(iDocType==1){
            binding.name.setText("Purchase");
        }
        else if(iDocType==2){
            binding.name.setText("Sales");
        }

        binding.fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavDirections action=SalesPurchaseHistoryFragmentDirections
                        .actionSalesPurchaseHistoryFragmentToSalePurchaseFragment2().setIDocType(iDocType);

                navController.navigate(action);
            }
        });


        AndroidNetworking.get(" http://185.151.4.167/Focus/api/Data/GetTransSummary")
                .addQueryParameter("iDocType",String.valueOf(iDocType))
                .addQueryParameter("iUser","0")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("responseHistory",response.toString());
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("responseHistory",anError.toString());
                    }
                });





        return binding.getRoot();


    }
}
