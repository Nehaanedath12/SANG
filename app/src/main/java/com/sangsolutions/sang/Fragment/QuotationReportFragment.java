package com.sangsolutions.sang.Fragment;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.sangsolutions.sang.Adapter.Customer.Customer;
import com.sangsolutions.sang.Adapter.Customer.CustomerAdapter;
import com.sangsolutions.sang.Adapter.Products.Products;
import com.sangsolutions.sang.Adapter.Products.ProductsAdapter;
import com.sangsolutions.sang.Adapter.QuotationAdapter.QuotationClass;
import com.sangsolutions.sang.Adapter.QuotationReportAdapter.QuotationReportAdapter;
import com.sangsolutions.sang.Adapter.QuotationReportAdapter.QuotationReportClass;
import com.sangsolutions.sang.Adapter.SalesPurchaseOrderReportAdapter.SalesPurchaseOrder;
import com.sangsolutions.sang.Adapter.SalesPurchaseOrderReportAdapter.SalesPurchaseOrderReportAdapter;
import com.sangsolutions.sang.Database.DatabaseHelper;
import com.sangsolutions.sang.R;
import com.sangsolutions.sang.Tools;
import com.sangsolutions.sang.URLs;
import com.sangsolutions.sang.databinding.FragmentQuotationReportBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class QuotationReportFragment extends Fragment {
    FragmentQuotationReportBinding binding;
    String StringDate,iType;
    SimpleDateFormat df;
    List<Products> productsList;
    ProductsAdapter productsAdapter;
    DatabaseHelper helper;
    int iProduct,iCustomer;
    List<Customer> customerList;
    CustomerAdapter customerAdapter;
    String userId="0";
    List<QuotationReportClass>reportList;
    QuotationReportAdapter reportAdapter;
    AlertDialog alertDialog;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding=FragmentQuotationReportBinding.inflate(getLayoutInflater());
        helper=new DatabaseHelper(requireContext());

        productsList=new ArrayList<>();
        productsAdapter=new ProductsAdapter(requireActivity(),productsList);

        customerList =new ArrayList<>();
        customerAdapter=new CustomerAdapter(requireActivity(),customerList);

        reportList=new ArrayList<>();
        reportAdapter=new QuotationReportAdapter(requireActivity(),reportList);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));


        assert getArguments() != null;
        iType=QuotationReportFragmentArgs.fromBundle(getArguments()).getIDocType();

        Cursor cursor_userId=helper.getUserId();
        if(cursor_userId!=null && cursor_userId.moveToFirst()){
            userId=cursor_userId.getString(cursor_userId.getColumnIndex("user_Id"));
        }

        initialSetting();
        binding.fromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicking(binding.fromDate);
            }
        });
        binding.toDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicking(binding.toDate);
            }
        });

        binding.clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initialSetting();
                reportList.clear();
                reportAdapter.notifyDataSetChanged();
            }
        });
        binding.product.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                GetProduct(s.toString());
            }
        });
        binding.product.setThreshold(1);
        binding.product.setAdapter(productsAdapter);

        binding.customer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                GetCustomer(s.toString());

            }
        });
        binding.customer.setThreshold(1);
        binding.customer.setAdapter(customerAdapter);
        binding.search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchChecking();
            }
        });


        return binding.getRoot();
    }

    private void searchChecking() {
        if(!binding.product.getText().toString().equals("")) {
            if (!binding.customer.getText().toString().equals("")) {
                loadingFromAPI();

            } else {
                binding.customer.setError("enter Customer");
            }
        }
        else {
            binding.product.setError("enter Product");
        }
    }

    private void loadingFromAPI() {

        AlertDialog.Builder builder=new AlertDialog.Builder(requireActivity());
        View view=LayoutInflater.from(requireActivity()).inflate(R.layout.progress_bar,null,false);
        builder.setView(view);
        builder.setCancelable(false);
        alertDialog = builder.create();
        alertDialog.show();

        String fromDate=Tools.dateFormat(binding.fromDate.getText().toString().trim());
        String toDate=Tools.dateFormat(binding.toDate.getText().toString().trim());

        AndroidNetworking.get("http://"+ URLs.GetSales_PurchaseQuotationReport)
                .addQueryParameter("UserId",userId)
                .addQueryParameter("FromDate",fromDate)
                .addQueryParameter("ToDate",toDate)
                .addQueryParameter("iProduct",String.valueOf(iProduct))
                .addQueryParameter("iCustomer",String.valueOf(iCustomer))
                .addQueryParameter("iType",iType)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("responseReport",response.toString());
                        loadingToClass(response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("responseReport",anError.toString());
                        alertDialog.dismiss();
                    }
                });
    }

    private void loadingToClass(JSONArray response) {
        reportList.clear();
        reportAdapter.notifyDataSetChanged();
        try {
            JSONArray jsonArray=new JSONArray(response.toString());
            if(jsonArray.length()>0){
                binding.frameEmpty.setVisibility(View.GONE);
            }else {
                binding.frameEmpty.setVisibility(View.VISIBLE);
                alertDialog.dismiss();
            }

            for (int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject=jsonArray.getJSONObject(i);

                QuotationReportClass report=new QuotationReportClass(
                        jsonObject.getString("sDocNo"),
                        jsonObject.getString("Date"),
                        jsonObject.getString("Rate"),
                        jsonObject.getString("Account1"),
                        jsonObject.getString("Account2"),
                        jsonObject.getString("Qty"),
                        jsonObject.getString("Product"),
                        jsonObject.getString("Tag1"),
                        jsonObject.getString("Tag2"),
                        jsonObject.getString("Tag3"),
                        jsonObject.getString("Tag4"),
                        jsonObject.getString("Tag5"),
                        jsonObject.getString("Tag6"),
                        jsonObject.getString("Tag7"),
                        jsonObject.getString("Tag8"));

                reportList.add(report);
                reportAdapter.notifyDataSetChanged();
                if(i+1==jsonArray.length()){
                    Log.d("listsizee",reportList.size()+"");
                    alertDialog.dismiss();
                    binding.recyclerView.setAdapter(reportAdapter);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void GetCustomer(String customerKeyword) {

        customerList.clear();
        Cursor cursor=helper.getCustomerbyKeyword(customerKeyword);
        if(cursor!=null && !customerKeyword.equals("")) {
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                for (int i = 0; i < cursor.getCount(); i++) {
                    Customer customer = new Customer();
                    customer.setiId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(Customer.I_ID))));
                    customer.setsCode(cursor.getString(cursor.getColumnIndex(Customer.S_CODE)));
                    customer.setsName(cursor.getString(cursor.getColumnIndex(Customer.S_NAME)));


                    customerList.add(customer);
                    cursor.moveToNext();
                    if (i + 1 == cursor.getCount()) {
                        customerAdapter.notifyDataSetChanged();
                    }
                }

                customerAdapter.setOnClickListener(new CustomerAdapter.OnClickListener() {
                    @Override
                    public void onItemClick(Customer customer, int position) {
                        iCustomer=customer.getiId();
                        binding.customer.setText(customer.getsName());
                        binding.customer.dismissDropDown();
                    }
                });
            }
        }
    }

    private void GetProduct(String productKeyword) {
        productsList.clear();
        Cursor cursor=helper.getProductByKeyword(productKeyword);
        if(cursor!=null && !productKeyword.equals("")) {
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                for (int i = 0; i < cursor.getCount(); i++) {
                    Products products = new Products();
                    products.setiId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(Products.I_ID))));
                    products.setsName(cursor.getString(cursor.getColumnIndex(Products.S_NAME)));
                    products.setsCode(cursor.getString(cursor.getColumnIndex(Products.S_CODE)));
                    productsList.add(products);
                    cursor.moveToNext();
                    if (i + 1 == cursor.getCount()) {
                        productsAdapter.notifyDataSetChanged();


                    }
                    productsAdapter.setOnClickListener(new ProductsAdapter.OnClickListener() {
                        @Override
                        public void onItemClick(Products products, int position) {
                            binding.product.setText(products.getsName());
                            iProduct = products.getiId();
                            binding.product.dismissDropDown();
                        }
                    });
                }
            }
        }
    }

    private void datePicking(EditText date) {

        DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {


                StringDate = Tools.checkDigit(dayOfMonth)+
                        "-" +
                        Tools.checkDigit(month + 1) +
                        "-"+
                        year;
                date.setText(StringDate);
            }
        };
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH);
        int day = now.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(requireActivity(), onDateSetListener, year, month, day);
        datePickerDialog.show();
    }

    private void initialSetting() {

        df = new SimpleDateFormat("dd-MM-yyyy");
        StringDate=df.format(new Date());
        binding.fromDate.setText(StringDate);
        binding.toDate.setText(StringDate);
        binding.customer.setText("");
        binding.product.setText("");
    }
}
