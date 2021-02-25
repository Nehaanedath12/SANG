package com.sangsolutions.sang.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.sangsolutions.sang.databinding.FragmentHistorySalesPurchaseReturnBinding;

public class SalesPurchaseReturnHistoryFragment extends Fragment {

    FragmentHistorySalesPurchaseReturnBinding binding;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding=FragmentHistorySalesPurchaseReturnBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }
}
