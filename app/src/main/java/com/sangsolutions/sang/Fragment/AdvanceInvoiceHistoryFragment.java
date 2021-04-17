package com.sangsolutions.sang.Fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.sangsolutions.sang.Adapter.SalesPurchaseHistoryAdapter.SalesPurchaseHistory;
import com.sangsolutions.sang.Adapter.SalesPurchaseHistoryAdapter.SalesPurchaseHistoryAdapter;
import com.sangsolutions.sang.Database.DatabaseHelper;
import com.sangsolutions.sang.Home;
import com.sangsolutions.sang.R;
import com.sangsolutions.sang.Tools;
import com.sangsolutions.sang.databinding.FragmentAdvanceInvoiceHistoryBinding;

import java.util.ArrayList;
import java.util.List;

public class AdvanceInvoiceHistoryFragment extends Fragment {

    FragmentAdvanceInvoiceHistoryBinding binding;
    int iDocType=16;
    String title;
    NavController navController;
    List<SalesPurchaseHistory> historyList;
    SalesPurchaseHistoryAdapter historyAdapter;
    AlertDialog alertDialog;
    DatabaseHelper helper;
    String userIdS=null;
    String toolTitle;
    boolean selectionActive = false;
    Animation slideUp, slideDown;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding=FragmentAdvanceInvoiceHistoryBinding.inflate(getLayoutInflater());
        try {
            ((Home)getActivity()).setDrawerEnabled(true);
        }catch (Exception e){
            e.printStackTrace();
        }
        helper=new DatabaseHelper(requireContext());
        historyList=new ArrayList<>();
        historyAdapter=new SalesPurchaseHistoryAdapter(requireActivity(),historyList);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setAdapter(historyAdapter);

        slideDown = AnimationUtils.loadAnimation(getContext(), R.anim.move_down);
        slideUp = AnimationUtils.loadAnimation(getContext(), R.anim.move_up);
        binding.fabDelete.setVisibility(View.GONE);
        binding.fabClose.setVisibility(View.GONE);
        binding.fabAdd.setVisibility(View.VISIBLE);

        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);

        Cursor cursor_userId=helper.getUserId();
        if(cursor_userId!=null &&cursor_userId.moveToFirst()) {
            userIdS = cursor_userId.getString(cursor_userId.getColumnIndex("user_Id"));
        }
        binding.refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                binding.refresh.setRefreshing(false);
                if(!Tools.isConnected(requireContext())){
                    Toast.makeText(requireContext(), "No Internet!!", Toast.LENGTH_SHORT).show();
                }
                navController.navigate(R.id.homeFragment);
                NavDirections action=HomeFragmentDirections.actionHomeFragmentToAdvanceInvoiceHistoryFragment();
                navController.navigate(action);

            }
        });

        binding.fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavDirections action=AdvanceInvoiceHistoryFragmentDirections.actionAdvanceInvoiceHistoryFragmentToAdvanceInvoiceFragment()
                        .setEditMode(false).setITransId(0);
                navController.navigate(action);
            }
        });

        AlertDialog.Builder builder=new AlertDialog.Builder(requireActivity());
        View view=LayoutInflater.from(requireActivity()).inflate(R.layout.progress_bar,null,false);
        builder.setView(view);
        builder.setCancelable(false);
        alertDialog = builder.create();

//        getHistoryDatas();

        return binding.getRoot();
    }
}
