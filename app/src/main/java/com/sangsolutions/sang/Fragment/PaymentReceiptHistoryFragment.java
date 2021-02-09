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
import com.sangsolutions.sang.databinding.FragmentPaymentReceiptHistoryBinding;
import com.sangsolutions.sang.databinding.FragmentSalesPurchaseHistoryBinding;

public class PaymentReceiptHistoryFragment extends Fragment {

    FragmentPaymentReceiptHistoryBinding binding;
    NavController navController;
    int iDocType;
    String toolTitle;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding=FragmentPaymentReceiptHistoryBinding.inflate(getLayoutInflater());
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);


        assert getArguments() != null;
        iDocType = SalesPurchaseHistoryFragmentArgs.fromBundle(getArguments()).getIDocType();

        if(iDocType==1){
            toolTitle="Payment";
        }
        else {
            toolTitle="Receipt";
        }

        binding.fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavDirections action=PaymentReceiptHistoryFragmentDirections.actionPaymentReceiptHistoryFragmentToPaymentReceiptFragment(toolTitle,iDocType);
                navController.navigate(action);
            }
        });

        return binding.getRoot();
    }
}
