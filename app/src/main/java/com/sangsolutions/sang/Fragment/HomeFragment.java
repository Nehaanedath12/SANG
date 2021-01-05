package com.sangsolutions.sang.Fragment;

import android.os.Bundle;
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
import com.sangsolutions.sang.databinding.ActivityMainBinding;
import com.sangsolutions.sang.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {
    FragmentHomeBinding binding;
    NavController navController;
    NavDirections action;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding=FragmentHomeBinding.inflate(getLayoutInflater());

        navController = Navigation.findNavController(requireActivity(),R.id.nav_host_fragment);


        binding.purchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    action=HomeFragmentDirections.actionHomeFragmentToSalesPurchaseFragment("Purchase History").setIDocType(1);
                    navController.navigate(action);
            }
        });

        return binding.getRoot();


    }
}
