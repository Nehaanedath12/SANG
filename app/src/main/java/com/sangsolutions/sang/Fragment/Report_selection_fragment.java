package com.sangsolutions.sang.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.sangsolutions.sang.R;
import com.sangsolutions.sang.databinding.FragmentPaymentReceiptHistoryBinding;
import com.sangsolutions.sang.databinding.FragmentReportSelectionBinding;

public class Report_selection_fragment extends Fragment {

    FragmentReportSelectionBinding binding;
    NavController navController;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding=FragmentReportSelectionBinding.inflate(getLayoutInflater());
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);

        binding.saleReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavDirections actions=Report_selection_fragmentDirections.actionReportSelectionFragmentToReportFragment("2","Sales Report");
                navController.navigate(actions);
            }
        });
        binding.purchaseReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavDirections actions=Report_selection_fragmentDirections.actionReportSelectionFragmentToReportFragment("1","Purchase Report");
                navController.navigate(actions);
            }
        });
        binding.PaymentReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavDirections action=Report_selection_fragmentDirections.actionReportSelectionFragmentToPRReportFragment("1","Payment Report");
                navController.navigate(action);
            }
        });
        binding.receiptReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavDirections action=Report_selection_fragmentDirections.actionReportSelectionFragmentToPRReportFragment("2","Receipt Report");
                navController.navigate(action);
            }
        });
        return binding.getRoot();
    }
}
