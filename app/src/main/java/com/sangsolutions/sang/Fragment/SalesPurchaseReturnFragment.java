package com.sangsolutions.sang.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.sangsolutions.sang.databinding.FragmentSalesPurchaseReturnBinding;

public class SalesPurchaseReturnFragment extends Fragment {

    FragmentSalesPurchaseReturnBinding binding;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding=FragmentSalesPurchaseReturnBinding.inflate(getLayoutInflater());

        return binding.getRoot();
    }
}
