package com.sangsolutions.sang.Fragment;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.sangsolutions.sang.Adapter.SalesPurchaseHistoryAdapter.SalesPurchaseHistory;
import com.sangsolutions.sang.Database.DatabaseHelper;
import com.sangsolutions.sang.Database.Sales_purchase_Class;
import com.sangsolutions.sang.Home;
import com.sangsolutions.sang.R;
import com.sangsolutions.sang.Adapter.SalesPurchaseHistoryAdapter.SalesPurchaseHistoryAdapter;
import com.sangsolutions.sang.Tools;
import com.sangsolutions.sang.URLs;
import com.sangsolutions.sang.databinding.FragmentSalesPurchaseHistoryBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SalesPurchaseHistoryFragment extends Fragment {
    FragmentSalesPurchaseHistoryBinding binding;

    int iDocType;
    String title;
    NavController navController;
    List<SalesPurchaseHistory>historyList;
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
        binding=FragmentSalesPurchaseHistoryBinding.inflate(getLayoutInflater());
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
        assert getArguments() != null;
        iDocType = SalesPurchaseHistoryFragmentArgs.fromBundle(getArguments()).getIDocType();
        if (iDocType == 10) {
            toolTitle = "Purchase";
        } else {
            toolTitle = "Sales";
        }

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
                NavDirections action=HomeFragmentDirections.actionHomeFragmentToSalesPurchaseHistoryFragment(toolTitle+" History").setIDocType(iDocType);
                navController.navigate(action);

            }
        });

        binding.fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavDirections action=SalesPurchaseHistoryFragmentDirections
                        .actionSalesPurchaseHistoryFragmentToSalePurchaseFragment2(toolTitle).setIDocType(iDocType).setEditMode(false).setITransId(0);
                navController.navigate(action);
            }
        });

        AlertDialog.Builder builder=new AlertDialog.Builder(requireActivity());
        View view=LayoutInflater.from(requireActivity()).inflate(R.layout.progress_bar,null,false);
        builder.setView(view);
        builder.setCancelable(false);
        alertDialog = builder.create();

        getHistoryDatas();

        binding.fabClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeSelection();
            }
        });
        binding.fabDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteAlert();
            }
        });

        return binding.getRoot();

    }

    private void deleteAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Delete?")
                .setMessage("Do you want to Delete " + historyAdapter.getSelectedItemCount() + " items?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DeleteItems();

                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    private void DeleteItems() {
        List<Integer> listSelectedItem = historyAdapter.getSelectedItems();
        for (int i =0;i<listSelectedItem.size();i++) {
            for (int j =0;j<historyList.size();j++) {
                if (listSelectedItem.get(i) == j) {
                    if(Tools.isConnected(requireContext())) {
                        deleteFromAPI(historyList.get(j).getiTransId());
                    }else {
                        deleteFromDB(historyList.get(j).getiTransId(),historyList.get(j).getsDocNo());
                    }
                }
            }

            if (i + 1 == listSelectedItem.size()) {
                getHistoryDatas();
                historyAdapter.notifyDataSetChanged();
                closeSelection();
            }
        }
    }

    private void getHistoryDatas() {
        alertDialog.show();
        Log.d("doctypee",iDocType+"");
        if(Tools.isConnected(requireContext())) {
            AndroidNetworking.get("http://" + new Tools().getIP(requireActivity()) + URLs.GetTransSummary)
                    .addQueryParameter("iDocType", String.valueOf(iDocType))
                    .addQueryParameter("iUser", userIdS)
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONArray(new JSONArrayRequestListener() {
                        @Override
                        public void onResponse(JSONArray response) {
                            Log.d("responseHistory", response.toString());

                            loadDatas(response);

                        }

                        @Override
                        public void onError(ANError anError) {
                            Log.d("responseHistory", anError.getMessage());

                            alertDialog.dismiss();
                        }
                    });
        }else {
            loadDatasFromDB();
        }
    }

    private void loadDatasFromDB() {
        historyList.clear();
        historyAdapter.notifyDataSetChanged();
        alertDialog.dismiss();

        Cursor cursor=helper.getDataFromS_P_by_Itype(iDocType);
        if(cursor.moveToFirst() && cursor!=null){

            Log.d("doctypee",cursor.getCount()+"");

            for (int i=0;i<cursor.getCount();i++){
                SalesPurchaseHistory history=new SalesPurchaseHistory();
                Log.d("historyList",cursor.getString(cursor.getColumnIndex(Sales_purchase_Class.S_DATE))+"");

                history.setsDate(cursor.getString(cursor.getColumnIndex(Sales_purchase_Class.S_DATE)));
                history.setsDocNo(cursor.getString(cursor.getColumnIndex(Sales_purchase_Class.S_DOC_NO)));
                history.setiTransId(cursor.getInt(cursor.getColumnIndex(Sales_purchase_Class.I_TRANS_ID)));

                int iCustomer=cursor.getInt(cursor.getColumnIndex(Sales_purchase_Class.I_ACCOUNT_1));
                String customerName=helper.getCustomerUsingId(iCustomer);
                history.setsAccount1(customerName);

                historyList.add(history);
                historyAdapter.notifyDataSetChanged();

                Log.d("historyList",history.getsDate()+"");
                historyAdapter.setOnClickListener(new SalesPurchaseHistoryAdapter.OnClickListener() {
                    @Override
                    public void onItemClick(int iTransId, int position) {
                        if(!Tools.isConnected(requireContext())) {
                            adapterOnItemClick(iTransId,position);
                        }else {
                            Toast.makeText(requireContext(), "Please Refresh", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onItemLongClick(int position) {
                        enableActionMode(position);
                        selectionActive = true;
                    }

                    @Override
                    public void onDeleteClick(int iTransId, String sDocNo) {

                        deleteFromDB(iTransId,sDocNo);
                        loadDatasFromDB();
                    }
                });
                cursor.moveToNext();
            }
        }
    }

    private void deleteFromDB(int iTransId, String sDocNo) {
        if(helper.deleteSP_Header(iTransId,iDocType,sDocNo)){
            if(helper.delete_S_P_Body(iDocType,iTransId)){
                Toast.makeText(requireContext(), " Deleted from Device", Toast.LENGTH_SHORT).show();
                historyAdapter.notifyDataSetChanged();

            }
        }

    }

    private void loadDatas(JSONArray response) {
                        historyList.clear();
                        historyAdapter.notifyDataSetChanged();
                        try {
                        JSONArray jsonArray = new JSONArray(response.toString());
                        if(jsonArray.length()==0){
                            alertDialog.dismiss();
                        }
                        for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        Log.d("I_TRANS_ID",jsonObject.getInt(SalesPurchaseHistory.I_ACCOUNT1)+"");
                        SalesPurchaseHistory history=new SalesPurchaseHistory(
                        jsonObject.getInt(SalesPurchaseHistory.I_TRANS_ID),
                        jsonObject.getInt(SalesPurchaseHistory.I_ACCOUNT1),
                        jsonObject.getInt(SalesPurchaseHistory.N_AMOUNT),
                        jsonObject.getString(SalesPurchaseHistory.S_DOC_NO),
                        jsonObject.getString(SalesPurchaseHistory.S_DATE),
                        jsonObject.getString(SalesPurchaseHistory.S_ACCOUNT1),
                        jsonObject.getString(SalesPurchaseHistory.S_ACCOUNT2)
                        );

                    historyList.add(history);
                    historyAdapter.notifyDataSetChanged();
                    if(i+1==jsonArray.length()){
                    alertDialog.dismiss();


                    historyAdapter.setOnClickListener(new SalesPurchaseHistoryAdapter.OnClickListener() {
                        @Override
                        public void onItemClick(int iTransId, int position) {

                            if(Tools.isConnected(requireContext())) {
                                adapterOnItemClick(iTransId, position);
                            }else {
                                Toast.makeText(requireContext(), "Check Your Internet Connection", Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onDeleteClick(int iTransId, String sDocNo) {
                            deleteFromAPI(iTransId);
                        }

                        @Override
                        public void onItemLongClick(int position) {
                            enableActionMode(position);
                            selectionActive = true;
                        }
                    });

                    }
                    }
                    } catch (JSONException e) {
                    e.printStackTrace();
                    }
    }

    private void adapterOnItemClick(int iTransId, int position) {
        if(!selectionActive) {
//            if (Tools.isConnected(requireContext())) {
                NavDirections action = SalesPurchaseHistoryFragmentDirections
                        .actionSalesPurchaseHistoryFragmentToSalePurchaseFragment2(toolTitle).setIDocType(iDocType).setEditMode(true).setITransId(iTransId);
                navController.navigate(action);
//            } else {
//                Toast.makeText(requireContext(), "no Internet", Toast.LENGTH_SHORT).show();
//            }
        }
        else {
            enableActionMode(position);
        }
    }

    private void enableActionMode(int position) {
        toggleSelection(position);
    }

    private void toggleSelection(int position) {
        historyAdapter.toggleSelection(position);
        int count = historyAdapter.getSelectedItemCount();

        if (count == 1 && binding.fabDelete.getVisibility() != View.VISIBLE) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    binding.fabAdd.startAnimation(slideDown);
                    binding.fabAdd.setVisibility(View.GONE);

                    binding.fabDelete.startAnimation(slideUp);
                    binding.fabClose.startAnimation(slideUp);
                    binding.fabDelete.setVisibility(View.VISIBLE);
                    binding.fabClose.setVisibility(View.VISIBLE);
                }
            }, 300);
        }

        if (count == 0) {
            closeSelection();
        }
    }

    private void closeSelection() {
        historyAdapter.clearSelections();
        binding.fabAdd.setVisibility(View.VISIBLE);
        binding.fabAdd.startAnimation(slideUp);
        binding.fabDelete.startAnimation(slideDown);
        binding.fabClose.startAnimation(slideDown);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.fabDelete.setVisibility(View.GONE);
                binding.fabClose.setVisibility(View.GONE);
            }
        }, 300);
        selectionActive = false;
    }

    private void deleteFromAPI(int iTransId) {
        AndroidNetworking.get("http://"+ new Tools().getIP(requireActivity())+  URLs.DeleteTrans)
                .addQueryParameter("iTransId", String.valueOf(iTransId))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("response_delete",response);
                        if(response.equals("-1")){
                            Toast.makeText(requireContext(), "Can't delete.Its invoice has used.", Toast.LENGTH_SHORT).show();
                        }
                        getHistoryDatas();
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("response_delete",anError.toString()+ anError.getErrorDetail()+anError.getErrorBody());

                    }
                });
    }
}
