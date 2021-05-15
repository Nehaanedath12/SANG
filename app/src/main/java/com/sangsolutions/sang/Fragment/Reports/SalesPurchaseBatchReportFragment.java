package com.sangsolutions.sang.Fragment.Reports;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.sangsolutions.sang.databinding.FragmentSalesPurchaseBatchReportBinding;

public class SalesPurchaseBatchReportFragment extends Fragment {
    FragmentSalesPurchaseBatchReportBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding=FragmentSalesPurchaseBatchReportBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }
}
