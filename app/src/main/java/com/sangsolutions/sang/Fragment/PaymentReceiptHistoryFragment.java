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
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.sangsolutions.sang.Adapter.PaymentReceiptHistoryAdapter.PaymentReceiptHistory;
import com.sangsolutions.sang.Adapter.PaymentReceiptHistoryAdapter.PaymentReceiptHistoryAdapter;
import com.sangsolutions.sang.Adapter.SalesPurchaseHistoryAdapter.SalesPurchaseHistory;
import com.sangsolutions.sang.Database.DatabaseHelper;
import com.sangsolutions.sang.R;
import com.sangsolutions.sang.Adapter.SalesPurchaseHistoryAdapter.SalesPurchaseHistoryAdapter;
import com.sangsolutions.sang.Tools;
import com.sangsolutions.sang.URLs;
import com.sangsolutions.sang.databinding.FragmentPaymentReceiptHistoryBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

public class PaymentReceiptHistoryFragment extends Fragment {

    FragmentPaymentReceiptHistoryBinding binding;
    NavController navController;
    int iDocType;
    String toolTitle;
    AlertDialog alertDialog;
    String userIdS=null;
    DatabaseHelper helper;
    boolean selectionActive = false;
    Animation slideUp, slideDown;

    List<PaymentReceiptHistory> historyList;
    PaymentReceiptHistoryAdapter historyAdapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding=FragmentPaymentReceiptHistoryBinding.inflate(getLayoutInflater());
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        helper=new DatabaseHelper(requireContext());

        slideDown = AnimationUtils.loadAnimation(getContext(), R.anim.move_down);
        slideUp = AnimationUtils.loadAnimation(getContext(), R.anim.move_up);
        binding.fabDelete.setVisibility(View.GONE);
        binding.fabClose.setVisibility(View.GONE);
        binding.fabAdd.setVisibility(View.VISIBLE);

        assert getArguments() != null;
        iDocType = SalesPurchaseHistoryFragmentArgs.fromBundle(getArguments()).getIDocType();
        historyList=new ArrayList<>();
        historyAdapter=new PaymentReceiptHistoryAdapter(requireActivity(),historyList);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        if(iDocType==1){
            toolTitle="Payment";
        }
        else {
            toolTitle="Receipt";
        }
        Cursor cursor_userId=helper.getUserId();
        if(cursor_userId!=null &&cursor_userId.moveToFirst()) {
            userIdS = cursor_userId.getString(cursor_userId.getColumnIndex("user_Id"));
        }


        binding.fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavDirections action=PaymentReceiptHistoryFragmentDirections.actionPaymentReceiptHistoryFragmentToPaymentReceiptFragment(toolTitle,iDocType).setITransId(0).setEditMode(false);
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
        AndroidNetworking.get("http://"+ URLs.GetTransReceipt_PaymentSummary)
                .addQueryParameter("iDocType",String.valueOf(iDocType))
                .addQueryParameter("iUser",userIdS)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("responseP_RHistory",response.toString());
                        loadDatas(response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("responseP_RHistory",anError.toString());
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

                Log.d("I_TRANS_ID",jsonObject.getInt(PaymentReceiptHistory.I_ACCOUNT1)+"");
                PaymentReceiptHistory history=new PaymentReceiptHistory(
                        jsonObject.getInt(PaymentReceiptHistory.I_TRANS_ID),
                        jsonObject.getInt(PaymentReceiptHistory.I_ACCOUNT1),
                        jsonObject.getInt(PaymentReceiptHistory.I_ACCOUNT2),
                        jsonObject.getInt(PaymentReceiptHistory.F_AMOUNT),
                        jsonObject.getInt(PaymentReceiptHistory.I_PAYMENT_METHOD),
                        jsonObject.getInt(PaymentReceiptHistory.I_BANK),
                        jsonObject.getInt(PaymentReceiptHistory.I_CHEQUE_NO),
                        jsonObject.getString(PaymentReceiptHistory.S_DOC_NO),
                        jsonObject.getString(PaymentReceiptHistory.S_DATE),
                        jsonObject.getString(PaymentReceiptHistory.S_ACCOUNT1),
                        jsonObject.getString(PaymentReceiptHistory.S_ACCOUNT2),
                        jsonObject.getString(PaymentReceiptHistory.S_CHEQUE_DATE));

                historyList.add(history);
                historyAdapter.notifyDataSetChanged();
                if(i+1==jsonArray.length()){
                    alertDialog.dismiss();
                    binding.recyclerView.setAdapter(historyAdapter);
                    historyAdapter.setOnClickListener(new PaymentReceiptHistoryAdapter.OnClickListener() {
                        @Override
                        public void onItemClick(int iTransId, int position) {
                            if (!selectionActive) {
                                if (Tools.isConnected(requireContext())) {
                                    NavDirections action = PaymentReceiptHistoryFragmentDirections.actionPaymentReceiptHistoryFragmentToPaymentReceiptFragment(toolTitle, iDocType).setITransId(iTransId).setEditMode(true);
                                    navController.navigate(action);
                                } else {
                                    Toast.makeText(requireContext(), "no Internet", Toast.LENGTH_SHORT).show();
                                }
                            }else {
                                enableActionMode(position);
                            }
                        }

                        @Override
                        public void onDeleteClick(List<PaymentReceiptHistory> list, int position) {
                            deleteFromAPI(list.get(position).getiTransId());

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
        AndroidNetworking.get("http://"+  URLs.DeleteTransReceipt_Payment)
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
}
