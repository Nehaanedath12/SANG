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
                NavDirections actions=Report_selection_fragmentDirections.actionReportSelectionFragmentToReportFragment("20","Sales Report");
                navController.navigate(actions);
            }
        });
        binding.purchaseReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavDirections actions=Report_selection_fragmentDirections.actionReportSelectionFragmentToReportFragment("10","Purchase Report");
                navController.navigate(actions);
            }
        });
        binding.PaymentReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavDirections action=Report_selection_fragmentDirections.actionReportSelectionFragmentToPRReportFragment("15","Payment Report");
                navController.navigate(action);
            }
        });
        binding.receiptReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavDirections action=Report_selection_fragmentDirections.actionReportSelectionFragmentToPRReportFragment("25","Receipt Report");
                navController.navigate(action);
            }
        });
        binding.purchaseReturnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavDirections action=Report_selection_fragmentDirections
                        .actionReportSelectionFragmentToSPReturnReportFragment2("11","Purchase Return Report");
                navController.navigate(action);
            }
        });
        binding.salesReturnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavDirections action=Report_selection_fragmentDirections
                        .actionReportSelectionFragmentToSPReturnReportFragment2("21","Sales Return Report");
                navController.navigate(action);
            }
        });
        binding.purchaseOrderReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavDirections action=Report_selection_fragmentDirections
                        .actionReportSelectionFragmentToSPOrderReportFragment("12","Purchase Order Report");

                navController.navigate(action);
            }
        });
        binding.salesOrderReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavDirections action=Report_selection_fragmentDirections
                        .actionReportSelectionFragmentToSPOrderReportFragment("22","Sales Order Report");

                navController.navigate(action);
            }
        });

        binding.enquiryReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavDirections action=Report_selection_fragmentDirections
                        .actionReportSelectionFragmentToRequestEnquiryReportFragment("23","Enquiry Report");
                navController.navigate(action);
            }
        });

        binding.requestReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavDirections action=Report_selection_fragmentDirections
                        .actionReportSelectionFragmentToRequestEnquiryReportFragment("13","Request Report");
                navController.navigate(action);
            }
        });

        binding.purchaseQuotationReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavDirections action=Report_selection_fragmentDirections
                        .actionReportSelectionFragmentToQuotationReportFragment("14","Purchase Quotation");
                navController.navigate(action);
            }
        });
        binding.salesQuotationReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavDirections action=Report_selection_fragmentDirections
                        .actionReportSelectionFragmentToQuotationReportFragment("21","Sales Quotation");
                navController.navigate(action);
            }
        });

        binding.stockReportReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavDirections action=Report_selection_fragmentDirections
                        .actionReportSelectionFragmentToStockCountReport("40","StockCount Report");
                navController.navigate(action);
            }
        });
        return binding.getRoot();
    }
}
