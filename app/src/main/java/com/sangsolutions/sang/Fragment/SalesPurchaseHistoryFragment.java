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

import com.sangsolutions.sang.R;
import com.sangsolutions.sang.databinding.FragmentSalesPurchaseHistoryBinding;

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

        binding.fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavDirections action=SalesPurchaseHistoryFragmentDirections
                        .actionSalesPurchaseHistoryFragmentToSalePurchaseFragment2().setIDocType(iDocType);

                navController.navigate(action);
            }
        });






        return binding.getRoot();


    }
}
