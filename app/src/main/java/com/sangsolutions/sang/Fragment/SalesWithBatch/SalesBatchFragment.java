package com.sangsolutions.sang.Fragment.SalesWithBatch;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.sangsolutions.sang.databinding.FragmentSalesBatchBinding;

public class SalesBatchFragment extends Fragment {

    FragmentSalesBatchBinding binding;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding=FragmentSalesBatchBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }
}
