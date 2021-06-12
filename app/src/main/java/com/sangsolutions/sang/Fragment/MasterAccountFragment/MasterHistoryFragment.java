package com.sangsolutions.sang.Fragment.MasterAccountFragment;

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
import android.widget.CompoundButton;
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
import com.sangsolutions.sang.Adapter.CustomerMasterAdapter.CustomerMasterAdapter;
import com.sangsolutions.sang.Database.CustomerMasterClass;
import com.sangsolutions.sang.Database.DatabaseHelper;
import com.sangsolutions.sang.Fragment.HomeFragmentDirections;
import com.sangsolutions.sang.R;
import com.sangsolutions.sang.Tools;
import com.sangsolutions.sang.URLs;
import com.sangsolutions.sang.databinding.FragmentMasterHistoryBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MasterHistoryFragment extends Fragment {
    FragmentMasterHistoryBinding binding;
    NavController navController;
    Animation slideUp, slideDown;
    AlertDialog alertDialog;
    String userIdS = null;
    DatabaseHelper helper;
    List<CustomerMasterClass> historyList;
    CustomerMasterAdapter historyAdapter;
    boolean selectionActive = false;
    int  ITEM_TYPE_ONE=0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMasterHistoryBinding.inflate(getLayoutInflater());
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        slideDown = AnimationUtils.loadAnimation(getContext(), R.anim.move_down);
        slideUp = AnimationUtils.loadAnimation(getContext(), R.anim.move_up);
        binding.fabDelete.setVisibility(View.GONE);
        binding.fabClose.setVisibility(View.GONE);
        binding.fabAdd.setVisibility(View.VISIBLE);
        helper = new DatabaseHelper(requireContext());



        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        View view = LayoutInflater.from(requireActivity()).inflate(R.layout.progress_bar, null, false);
        builder.setView(view);
        builder.setCancelable(false);
        alertDialog = builder.create();

        ITEM_TYPE_ONE= MasterHistoryFragmentArgs.fromBundle(getArguments()).getMode();
        if(ITEM_TYPE_ONE==1){
            binding.switchButton.setChecked(true);
        }

        historyList=new ArrayList<>();
        historyAdapter = new CustomerMasterAdapter(requireActivity(), historyList,ITEM_TYPE_ONE);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setAdapter(historyAdapter);

        Cursor cursor_userId = helper.getUserId();
        if (cursor_userId != null && cursor_userId.moveToFirst()) {
            userIdS = cursor_userId.getString(cursor_userId.getColumnIndex("user_Id"));
        }
        binding.fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavDirections action = MasterHistoryFragmentDirections.actionMasterHistoryFragmentToMasterFragment()
                        .setId(0).setEditMode(false);
                navController.navigate(action);
            }
        });

        binding.refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!Tools.isConnected(requireContext())) {
                    Toast.makeText(requireContext(), "No Internet!!", Toast.LENGTH_SHORT).show();
                }
                navController.navigate(R.id.homeFragment);
                NavDirections action = HomeFragmentDirections.actionHomeFragmentToMasterHistoryFragment()
                        .setMode(ITEM_TYPE_ONE);
                navController.navigate(action);
                binding.refresh.setRefreshing(false);

            }
        });
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

        binding.switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){
                    ITEM_TYPE_ONE=1;
                }else {
                    ITEM_TYPE_ONE=0;
                }
                navController.navigate(R.id.homeFragment);
                NavDirections action = HomeFragmentDirections.actionHomeFragmentToMasterHistoryFragment().setMode(ITEM_TYPE_ONE);
                navController.navigate(action);

                Log.d("checkedd",isChecked+" "+ITEM_TYPE_ONE);
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
                        deleteFromAPI(historyList.get(j).getiId());
                        }else {
                            deleteFromDB(historyList.get(j).getiId());
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
                    if (Tools.isConnected(requireContext())) {
                    AndroidNetworking.get("http://" + new Tools().getIP(requireActivity()) + URLs.GetCustomerSummary)
                    .addQueryParameter("iUser", userIdS)
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONArray(new JSONArrayRequestListener() {
                        @Override
                        public void onResponse(JSONArray response) {
                            Log.d("responseHistoryMaster", response.toString());
                            loadDatas(response);
                        }

                        @Override
                        public void onError(ANError anError) {
                            Log.d("responseHistoryMaster", anError.getMessage());

                            alertDialog.dismiss();
                        }
                    });
        } else {
            loadDatasFromDB();
        }
    }

    private void loadDatasFromDB() {
        historyList.clear();
        historyAdapter.notifyDataSetChanged();
        alertDialog.dismiss();

        Cursor cursor=helper.getDataFromCustomerMaster();
        if(cursor.moveToFirst() && cursor!=null) {
            Log.d("doctypee", cursor.getCount() + "");
            for (int i = 0; i < cursor.getCount(); i++) {
                CustomerMasterClass masterClass=new CustomerMasterClass();
                masterClass.setiId(cursor.getInt(cursor.getColumnIndex(CustomerMasterClass.ID)));
                masterClass.setName(cursor.getString(cursor.getColumnIndex(CustomerMasterClass.NAME)));
                masterClass.setCode(cursor.getString(cursor.getColumnIndex(CustomerMasterClass.CODE)));

                historyList.add(masterClass);
                historyAdapter.notifyDataSetChanged();

                historyAdapter.setOnClickListener(new CustomerMasterAdapter.OnClickListener() {
                    @Override
                    public void onItemClick(int iId, int position) {
                        if(!Tools.isConnected(requireContext())) {
                            adapterOnItemClick(iId, position);
                        }else {
                            Toast.makeText(requireContext(), "Please Refresh", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onDeleteClick(int iId, int position) {
                        deleteFromDB(iId);
                        loadDatasFromDB();
                    }

                    @Override
                    public void onItemLongClick(int iId, int position) {
                        enableActionMode(position);
                        selectionActive = true;
                    }
                });
                cursor.moveToNext();
            }
        }

    }

    private void deleteFromDB(int iId) {
        if(helper.deleteMasterCustomer(iId)){
                Toast.makeText(requireContext(), " Deleted from Device", Toast.LENGTH_SHORT).show();
                historyAdapter.notifyDataSetChanged();

        }
    }

    private void loadDatas(JSONArray response) {
        historyList.clear();
        historyAdapter.notifyDataSetChanged();
        try {
            JSONArray jsonArray = new JSONArray(response.toString());
            if (jsonArray.length() == 0) {
                alertDialog.dismiss();
            }
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                CustomerMasterClass masterClass=new CustomerMasterClass();
                masterClass.setiId(jsonObject.getInt("iId"));
                masterClass.setName(jsonObject.getString("sName"));
                masterClass.setCode(jsonObject.getString("sCode"));


                historyList.add(masterClass);
                historyAdapter.notifyDataSetChanged();
                if(i+1==jsonArray.length()) {
                    alertDialog.dismiss();

                    historyAdapter.setOnClickListener(new CustomerMasterAdapter.OnClickListener() {
                                @Override
                                public void onItemClick(int iId, int position) {
                                if(Tools.isConnected(requireContext())) {
                                adapterOnItemClick(iId, position);
                                }else {
                                    Toast.makeText(requireContext(), "Please Refresh", Toast.LENGTH_SHORT).show();
                                }
                                }

                        @Override
                        public void onDeleteClick(int iId, int position) {
                            deleteFromAPI(iId);
                            }

                        @Override
                        public void onItemLongClick(int iId, int position) {
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

    private void deleteFromAPI(int iId) {
                AndroidNetworking.get("http://"+ new Tools().getIP(requireActivity())+  URLs.DeleteCustomer)
                .addQueryParameter("iId", String.valueOf(iId))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        getHistoryDatas();
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("response_delete",anError.toString()+ anError.getErrorDetail()+anError.getErrorBody());

                    }
                });
    }

    private void adapterOnItemClick(int iId, int position) {
        if(!selectionActive) {
            NavDirections action = MasterHistoryFragmentDirections.actionMasterHistoryFragmentToMasterFragment()
                    .setEditMode(true).setId(iId);
                    navController.navigate(action);
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
}