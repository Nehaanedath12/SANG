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

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.sangsolutions.sang.Adapter.RequestHistoryAdapter.RequestClass;
import com.sangsolutions.sang.Adapter.RequestHistoryAdapter.RequestHistoryAdapter;
import com.sangsolutions.sang.Adapter.SalesPurchaseHistoryAdapter.SalesPurchaseHistory;
import com.sangsolutions.sang.Database.DatabaseHelper;
import com.sangsolutions.sang.R;
import com.sangsolutions.sang.Tools;
import com.sangsolutions.sang.URLs;
import com.sangsolutions.sang.databinding.FragmentHistoryStockCountBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StockCountHistoryFragment extends Fragment {
    FragmentHistoryStockCountBinding binding;
    int iDocType;
    String title;
    NavController navController;
    List<RequestClass> historyList;
    RequestHistoryAdapter historyAdapter;
    AlertDialog alertDialog;
    DatabaseHelper helper;
    String userIdS=null;
    String toolTitle;
    boolean selectionActive = false;
    Animation slideUp, slideDown;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding=FragmentHistoryStockCountBinding.inflate(getLayoutInflater());
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);

        helper=new DatabaseHelper(requireContext());
        historyList=new ArrayList<>();
        historyAdapter=new RequestHistoryAdapter(requireActivity(),historyList);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        slideDown = AnimationUtils.loadAnimation(getContext(), R.anim.move_down);
        slideUp = AnimationUtils.loadAnimation(getContext(), R.anim.move_up);
        binding.fabDelete.setVisibility(View.GONE);
        binding.fabClose.setVisibility(View.GONE);
        binding.fabAdd.setVisibility(View.VISIBLE);

        Cursor cursor_userId=helper.getUserId();
        if(cursor_userId!=null &&cursor_userId.moveToFirst()) {
            userIdS = cursor_userId.getString(cursor_userId.getColumnIndex("user_Id"));
        }

        assert getArguments() != null;
        iDocType = StockCountHistoryFragmentArgs.fromBundle(getArguments()).getIDocType();


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

        binding.fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavDirections action=StockCountHistoryFragmentDirections
                        .actionStockCountHistoryFragmentToStockCountFragment()
                        .setEditMode(false).setIDocType(iDocType).setITransId(0);

                navController.navigate(action);
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
                    deleteFromAPI(historyList.get(j).getiTransId());
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
        AndroidNetworking.get("http://"+  URLs.GetTransStockSummary)
                .addQueryParameter("iDocType",String.valueOf(iDocType))
                .addQueryParameter("iUser",userIdS)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("responseHistory",response.toString());

                        loadDatas(response);

                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("responseHistory",anError.toString());

                        alertDialog.dismiss();
                    }
                });
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

                RequestClass history=new RequestClass();
                history.setiTransId(jsonObject.getInt(SalesPurchaseHistory.I_TRANS_ID));
                history.setsDocNo(jsonObject.getString(SalesPurchaseHistory.S_DOC_NO));
                history.setsDate(jsonObject.getString(SalesPurchaseHistory.S_DATE));

                historyList.add(history);
                historyAdapter.notifyDataSetChanged();
                if(i+1==jsonArray.length()){
                    alertDialog.dismiss();
                    binding.recyclerView.setAdapter(historyAdapter);

                    historyAdapter.setOnClickListener(new RequestHistoryAdapter.OnClickListener() {
                        @Override
                        public void onItemClick(int iTransId, int position) {
                            if(!selectionActive) {
                                if (Tools.isConnected(requireContext())) {
                                    NavDirections action=StockCountHistoryFragmentDirections
                                            .actionStockCountHistoryFragmentToStockCountFragment()
                                            .setIDocType(iDocType).setITransId(iTransId).setEditMode(true);
                                    navController.navigate(action);
                                } else {
                                    Toast.makeText(requireContext(), "No Internet", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else {
                                enableActionMode(position);
                            }
                        }

                        @Override
                        public void onItemLongClick(int position) {
                            enableActionMode(position);
                            selectionActive = true;
                        }

                        @Override
                        public void onDeleteClick(int iTransId) {
                            deleteFromAPI(iTransId);
                        }
                    });

                }
            }
        } catch (JSONException e) {
            Log.d("exceptionn",e.getMessage());
            e.printStackTrace();
        }
    }

    private void deleteFromAPI(int iTransId) {
        AndroidNetworking.get("http://"+  URLs.DeleteTransStock)
                .addQueryParameter("iTransId", String.valueOf(iTransId))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("response_delete",response);

                        getHistoryDatas();
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("response_delete",anError.toString()+ anError.getErrorDetail()+anError.getErrorBody());

                    }
                });
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
}
