package com.sangsolutions.sang.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.sangsolutions.sang.databinding.FragmentSalesPurchaseBinding;

public class SalesPurchaseFragment extends Fragment {
    FragmentSalesPurchaseBinding binding;
//    SalesPurchaseFragmentArgs args;

    int iDocType;
    String title;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding=FragmentSalesPurchaseBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();

        iDocType = SalesPurchaseFragmentArgs.fromBundle(getArguments()).getIDocType();
        title=SalesPurchaseFragmentArgs.fromBundle(getArguments()).getToolTitle();
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
