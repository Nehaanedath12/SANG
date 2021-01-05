package com.sangsolutions.sang.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.sangsolutions.sang.databinding.FragmentSalesPurchaseHistoryBinding;

public class SalesPurchaseHistoryFragment extends Fragment {
    FragmentSalesPurchaseHistoryBinding binding;

    int iDocType;
    String title;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding=FragmentSalesPurchaseHistoryBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();

        iDocType = SalesPurchaseHistoryFragmentArgs.fromBundle(getArguments()).getIDocType();
        title=SalesPurchaseHistoryFragmentArgs.fromBundle(getArguments()).getToolTitle();
        if(iDocType==1) {
            binding.text.setText("Purchase");
        }
        else {
            binding.text.setText("Sale");
        }

        Log.d("iDocType",iDocType+"");




        return view;


    }
}
