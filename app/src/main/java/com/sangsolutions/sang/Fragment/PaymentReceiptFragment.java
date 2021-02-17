package com.sangsolutions.sang.Fragment;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.google.android.material.snackbar.Snackbar;
import com.sangsolutions.sang.Adapter.BankAdapter.Bank;
import com.sangsolutions.sang.Adapter.BankAdapter.BankAdapter;
import com.sangsolutions.sang.Adapter.Customer.Customer;
import com.sangsolutions.sang.Adapter.Customer.CustomerAdapter;
import com.sangsolutions.sang.Adapter.InvoiceAdapter.Invoice;
import com.sangsolutions.sang.Adapter.InvoiceAdapter.InvoiceAdapter;
import com.sangsolutions.sang.Adapter.InvoiceAdapter.InvoiceSelectedAdapter;
import com.sangsolutions.sang.Adapter.MasterSettings.MasterSettings;
import com.sangsolutions.sang.Adapter.TagDetailsAdapter.TagDetails;
import com.sangsolutions.sang.Adapter.TagDetailsAdapter.TagDetailsAdapter;
import com.sangsolutions.sang.Adapter.TransSalePurchase.TransSetting;
import com.sangsolutions.sang.Adapter.UnitAdapter;
import com.sangsolutions.sang.Adapter.User;
import com.sangsolutions.sang.Database.DatabaseHelper;
import com.sangsolutions.sang.R;
import com.sangsolutions.sang.Tools;
import com.sangsolutions.sang.URLs;
import com.sangsolutions.sang.databinding.FragmentPaymentReceiptBinding;
import com.sangsolutions.sang.databinding.InvoiceDialogeLayoutBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class PaymentReceiptFragment extends Fragment {
    FragmentPaymentReceiptBinding binding;
    NavController navController;
    int iDocType,iTransId;
    boolean EditMode;
    String StringDate;
    SimpleDateFormat df;
    DatabaseHelper helper;
    String userIdS=null;
    String toolTitle;
    String docNo;
    AlertDialog alertDialog;

    Cursor cursorTagNumber;
    int tagTotalNumber;

    int numberOfLinesH;
    ArrayList<AutoCompleteTextView>autoText_H_list;

    ArrayList<AutoCompleteTextView> mandatoryList_H;

    List<Bank>bankList;
    BankAdapter bankAdapter;

    List<Customer> customerList;
    CustomerAdapter customerAdapter;

    List<Invoice>invoiceList;
    InvoiceAdapter invoiceAdapter;

    List<Invoice>invoiceSecondList;

    List<Invoice>invoiceSelectedList;
    InvoiceSelectedAdapter invoiceSelectedAdapter;

    int iCustomer,iTagDetail,iBank,iPaymentMethod;
    AlertDialog alertDialog_invoice;

    boolean selectionActive = false;

    InvoiceDialogeLayoutBinding bindingInvoice;


    List<TagDetails> tagList;
    TagDetailsAdapter tagDetailsAdapter;
    HashMap<Integer, Integer> hashMapHeader;
    List<Integer> headerListTags;
    String userCode;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       binding=FragmentPaymentReceiptBinding.inflate(getLayoutInflater());
       navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        df = new SimpleDateFormat("dd-MM-yyyy");
        helper=new DatabaseHelper(requireContext());

        assert getArguments() != null;
        iDocType = PaymentReceiptFragmentArgs.fromBundle(getArguments()).getIDocType();
        iTransId = PaymentReceiptFragmentArgs.fromBundle(getArguments()).getITransId();
        EditMode = PaymentReceiptFragmentArgs.fromBundle(getArguments()).getEditMode();
        Log.d("PaymentReceipt","iDocType "+iDocType+" "+"iTransId "+iTransId+" "+"editMode "+ EditMode +"");


        AlertDialog.Builder builder=new AlertDialog.Builder(requireActivity());
        View view=LayoutInflater.from(requireActivity()).inflate(R.layout.progress_bar,null,false);
        builder.setView(view);
        builder.setCancelable(false);
        alertDialog = builder.create();

        headerListTags =new ArrayList<>();

        autoText_H_list=new ArrayList<>();
        mandatoryList_H =new ArrayList<>();

        bankList =new ArrayList<>();
        bankAdapter=new BankAdapter(requireActivity(),bankList);


        customerList =new ArrayList<>();
        customerAdapter=new CustomerAdapter(requireActivity(),customerList);

        invoiceList=new ArrayList<>();
        invoiceAdapter =new InvoiceAdapter(requireContext(),invoiceList);

        invoiceSecondList=new ArrayList<>();

        invoiceSelectedList=new ArrayList<>();
        invoiceSelectedAdapter=new InvoiceSelectedAdapter(requireContext(),invoiceSelectedList);

        tagList =new ArrayList<>();
        tagDetailsAdapter =new TagDetailsAdapter(requireActivity(),tagList);

        hashMapHeader=new HashMap<>();


        Cursor cursor_userId=helper.getUserId();
        if(cursor_userId!=null &&cursor_userId.moveToFirst()) {
            userIdS = cursor_userId.getString(cursor_userId.getColumnIndex("user_Id"));
        }
        cursorTagNumber =helper.getTotalTagNumber();
        if(cursorTagNumber!=null) {
            tagTotalNumber = cursorTagNumber.getCount();
        }

       initialValueSettingHeader();

        binding.paymentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(parent.getItemAtPosition(position).toString().equals("Cheque")){
                    binding.checkDetails.setVisibility(View.VISIBLE);
                }

                else if(parent.getItemAtPosition(position).toString().equals("Cash")){
                    binding.checkDetails.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        for (int tagId=1;tagId<=tagTotalNumber;tagId++){
            Cursor cursor=helper.getTransSettings(iDocType,tagId);
            if(cursor!=null ){
                cursor.moveToFirst();
                String iTagPosition=cursor.getString(cursor.getColumnIndex(TransSetting.I_TAG_POSITION));
                String mandatory=cursor.getString(cursor.getColumnIndex(TransSetting.B_MANDATORY));
                String visibility=cursor.getString(cursor.getColumnIndex(TransSetting.B_VISIBLE));

                Cursor cursor1=helper.getTagNamebyId(tagId);
                cursor1.moveToFirst();
                if(iTagPosition.equals("1")) {

                    headerListTags.add(tagId);
                    LinearLayout ll = binding.linearHeader;
                    AutoCompleteTextView autoTextHeader = new AutoCompleteTextView(requireActivity());
                    LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    autoTextHeader.setLayoutParams(p);
                    if (visibility.equals("false")) {
                        autoTextHeader.setEnabled(false);
                    }
                    if (mandatory.equals("true")) {
                        mandatoryList_H.add(autoTextHeader);
                    }
                    autoTextHeader.setLongClickable(false);
                    autoTextHeader.cancelLongPress();
                    autoTextHeader.setHint(cursor1.getString(cursor1.getColumnIndex(MasterSettings.S_NAME)));
                    autoTextHeader.setId(numberOfLinesH + 1);
                    autoTextHeader.setTag("tag" + autoTextHeader.getId() + "Header");

                    ll.addView(autoTextHeader);
                    numberOfLinesH++;
                    autoText_H_list.add(autoTextHeader);
                    autoTextHeader.addTextChangedListener(getTextWatcher(autoTextHeader, tagId, "Header"));

                }
                }
        }

       binding.date.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               settingDate(binding.date);
           }
       });


        binding.checkDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingDate(binding.checkDate);
            }
        });

        binding.bankName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                GetBank(s.toString());

            }
        });
        binding.bankName.setThreshold(1);
        binding.bankName.setAdapter(bankAdapter);


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

        binding.invoiceB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                invoiceDialogue();
            }
        });


        binding.saveMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMain();
            }
        });

        binding.deleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(requireActivity());
                builder.setTitle("delete!")
                        .setMessage("Do you want to delete all ?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteAll();

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create().show();
            }
        });

        /////

       return binding.getRoot();
    }

    private void deleteAll() {
        EditMode=false;
        initialValueSettingHeader();
        binding.customer.setText("");
        binding.description.setText("");
        binding.amount.setText("");
        binding.bankName.setText("");
        binding.checkNo.setText("");
        binding.linearInvoice.setVisibility(View.GONE);
        for (int i=0;i<autoText_H_list.size();i++){
            autoText_H_list.get(i).setText("");
        }


    }

    private void saveMain() {
        AlertDialog.Builder builder=new AlertDialog.Builder(requireActivity());
        builder.setTitle("save!")
                .setMessage("Do you want to save ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(binding.paymentSpinner.getSelectedItem().toString().equals("Cheque")){
                            iPaymentMethod=2;
                        }
                        else {
                            iPaymentMethod=1;
                        }
                        if (!binding.customer.getText().toString().equals("") && helper.getCustomerNameValid(binding.customer.getText().toString().trim())) {
//                            Log.d("invoiceli",invoiceSelectedList.size()+"hh");

                            float amount=0;
                            if(binding.linearInvoice.getVisibility()==View.VISIBLE){
                                Log.d("invoiceli",invoiceSelectedList.size()+"hh");
                                for(int i=0;i<invoiceSelectedList.size();i++){
                                    amount+=Float.parseFloat(invoiceSelectedList.get(i).getAmount());
                                }
                                Log.d("invoiceli",amount+"hh");

                                if(Float.parseFloat(binding.amount.getText().toString().trim())>amount){
                                    binding.amount.setError("Not greater than selected invoice");
                                }
                                else {
                                    fieldsChecking(iPaymentMethod);
                                }
                            }
                            else {
                                fieldsChecking(iPaymentMethod);
                            }

                        }
                        else { binding.customer.setError("Enter valid customer!");}

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();


    }

    private void fieldsChecking(int iPaymentMethod) {
        if(iPaymentMethod==2){
            if(!binding.bankName.getText().toString().equals("")){
                if(!binding.checkNo.getText().toString().equals("")){

                    mandatoryChecking();

                }else {binding.checkNo.setError("empty!");}
            }else {binding.bankName.setError("empty!");}
        }else if(iPaymentMethod==1){
            binding.bankName.setText("");
            binding.checkNo.setText("");
            binding.checkDate.setText(df.format(new Date()));
            mandatoryChecking();
        }
    }

    private void mandatoryChecking() {
        boolean flag = true;
            if (!binding.amount.getText().toString().equals("")) {
                if (mandatoryList_H.size() <= 0) {
                    uploadToJson();
                }
                for (int i = 0; i < mandatoryList_H.size(); i++) {
                    if (!mandatoryList_H.get(i).getText().toString().equals("")) {
                        if (i + 1 == mandatoryList_H.size() && flag) {
                            uploadToJson();
                        }
                    } else {
                        Toast.makeText(requireContext(), "Mandatory fields are not filled", Toast.LENGTH_SHORT).show();
                        mandatoryList_H.get(i).setError("Mandatory");
                        flag = false;
                    }
                }
            }
            else {
                binding.amount.setError("Empty!");
            }
    }

    private void uploadToJson() {

        JSONObject jsonObjectMain=new JSONObject();
        try{
            jsonObjectMain.put("iTransId",iTransId);
            jsonObjectMain.put("sDocNo",docNo);
            jsonObjectMain.put("sDate",Tools.dateFormat(binding.date.getText().toString()));
            jsonObjectMain.put("iDocType",iDocType);
            jsonObjectMain.put("iAccount1",iCustomer);
            jsonObjectMain.put("iAccount2",0);
            jsonObjectMain.put("sNarration",binding.description.getText().toString());
            jsonObjectMain.put("fAmount",binding.amount.getText().toString());
            jsonObjectMain.put("iPaymentMethod",iPaymentMethod);
            if(iPaymentMethod==2) {
                jsonObjectMain.put("iBank", iBank);
                jsonObjectMain.put("iChequeNo", Float.parseFloat(binding.checkNo.getText().toString()));
            }else if(iPaymentMethod==1){
                jsonObjectMain.put("iBank", 0);
                jsonObjectMain.put("iChequeNo", 0);
            }
            jsonObjectMain.put("sChequeDate", Tools.dateFormat(binding.checkDate.getText().toString()));

            assert userIdS != null;
            jsonObjectMain.put("iUser",Integer.parseInt(userIdS));

            Log.d("jsonObjecMain",jsonObjectMain.get("iTransId")+"");
            Log.d("jsonObjecMain",jsonObjectMain.get("sDocNo")+"");
            Log.d("jsonObjecMain",jsonObjectMain.get("iDocType")+"");
            Log.d("jsonObjecMain",jsonObjectMain.get("iAccount1")+"");
            Log.d("jsonObjecMain",jsonObjectMain.get("iAccount2")+"");
            Log.d("jsonObjecMain",jsonObjectMain.get("sNarration")+"");
            Log.d("jsonObjecMain",jsonObjectMain.get("iUser")+"");
            Log.d("jsonObjecMainamount",jsonObjectMain.get("fAmount")+"");
            Log.d("jsonObjecMainp",jsonObjectMain.get("iPaymentMethod")+"");
            Log.d("jsonObjecMainbank",jsonObjectMain.get("iBank")+"");
            Log.d("jsonObjecMainche",jsonObjectMain.get("iChequeNo")+"");
            Log.d("jsonObjecMainda",jsonObjectMain.get("sChequeDate")+"");

            JSONArray jsonArray=new JSONArray();

            if(invoiceSelectedList.size()>0) {
                for (int i = 0; i < invoiceSelectedList.size(); i++) {
                    JSONObject jsonObject = new JSONObject();
                    Log.d("bodyPartListsize", invoiceSelectedList.size() + "" + invoiceSelectedList.get(i).getiTransId());

                    Log.d("bodyPartListsize", invoiceSelectedList.size() + "");

                    for (int j = 1; j <= tagTotalNumber; j++) {
                        if (hashMapHeader.containsKey(j)) {
                            jsonObject.put("iTag" + j, hashMapHeader.get(j));
                        } else {
                            jsonObject.put("iTag" + j, 0);
                        }
                        Log.d("Tags" + j, jsonObject.get("iTag" + j) + "");
                    }
                    jsonObject.put("iRefDocId", invoiceSelectedList.get(i).getiTransId());
                    jsonObject.put("fAmount", invoiceSelectedList.get(i).getAmount());

                    Log.d("jsonObjecttiRefDocId", jsonObject.get("iRefDocId") + "");
                    Log.d("jsonObjectfAmount", jsonObject.get("fAmount") + "");

                    jsonArray.put(jsonObject);
                }
            }
            else {
                invoiceSelectedList.size();
                JSONObject jsonObject = new JSONObject();
                for (int j = 1; j <= tagTotalNumber; j++) {
                    if (hashMapHeader.containsKey(j)) {
                        jsonObject.put("iTag" + j, hashMapHeader.get(j));
                    } else {
                        jsonObject.put("iTag" + j, 0);
                    }
                    Log.d("Tags" + j, jsonObject.get("iTag" + j) + "");
                }

                jsonObject.put("iRefDocId",0);
                jsonObject.put("fAmount", 0);
                jsonArray.put(jsonObject);
            }
            jsonObjectMain.put("Body",jsonArray);

            Log.d("jsonnn",jsonObjectMain.toString());
            uploadToAPI(jsonObjectMain);

        } catch (JSONException e) {

            Log.d("exceptionn",e.getMessage());
            e.printStackTrace();
        }

    }

    private void uploadToAPI(JSONObject jsonObjectMain) {
        if(Tools.isConnected(requireActivity())) {
            alertDialog.show();
            AndroidNetworking.post("http://"+URLs.Post_Receipt_Payment)
                    .addJSONObjectBody(jsonObjectMain)
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsString(new StringRequestListener() {
                        @Override
                        public void onResponse(String response) {
                            if (response.contains(docNo)) {
                                alertDialog.dismiss();
                                Log.d("responsePost_R_P", "successfully");
                                Toast.makeText(requireActivity(), "Posted successfully", Toast.LENGTH_SHORT).show();
//                                bodyPartList.clear();
                                NavDirections actions = PaymentReceiptFragmentDirections.actionPaymentReceiptFragmentToPaymentReceiptHistoryFragment(toolTitle,iDocType);
                                navController.navigate(actions);
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            alertDialog.dismiss();
                            Log.d("responsePost_R_P", anError.getErrorDetail() + anError.getErrorBody() + anError.toString());
                        }
                    });
        }
        else {
            Snackbar snackbar=Snackbar.make(binding.getRoot(),"No Internet",Snackbar.LENGTH_LONG);
            snackbar.setBackgroundTint(Color.RED);
            snackbar.setTextColor(Color.WHITE);
            snackbar.show();
        }
    }

    private void GetBank(String bankKeyword) {
        customerList.clear();
        Cursor cursor=helper.getBankyKeyword(bankKeyword);
        if(cursor!=null && !bankKeyword.equals("")) {
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                for (int i = 0; i < cursor.getCount(); i++) {
                    Bank bank = new Bank();
                    bank.setiId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(Bank.I_ID))));
                    bank.setsCode(cursor.getString(cursor.getColumnIndex(Bank.S_CODE)));
                    bank.setsName(cursor.getString(cursor.getColumnIndex(Bank.S_NAME)));

                    bankList.add(bank);
                    cursor.moveToNext();
                    if (i + 1 == cursor.getCount()) {
                        bankAdapter.notifyDataSetChanged();
                    }
                }

                bankAdapter.setOnClickListener(new BankAdapter.OnClickListener() {
                    @Override
                    public void onItemClick(Bank bank, int position) {
                        iBank=bank.getiId();
                        binding.bankName.setText(bank.getsName());
                        binding.bankName.dismissDropDown();
                    }
                });
            }
        }
    }

    private void settingDate(EditText date) {
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

    private void invoiceDialogue() {
        if(!binding.customer.getText().toString().equals(""))
        {
            AlertDialog.Builder builder=new AlertDialog.Builder(requireContext());
            bindingInvoice=InvoiceDialogeLayoutBinding.inflate(getLayoutInflater());
            bindingInvoice.recyclerViewInvoice.setLayoutManager(new LinearLayoutManager(requireContext()));

            builder.setView(bindingInvoice.getRoot());
            alertDialog_invoice=builder.create();
            alertDialog_invoice.setCancelable(false);
            bindingInvoice.recyclerViewInvoice.setAdapter(invoiceAdapter);
            API_Invoice();
            invoiceAdapter.setOnClickListener(new InvoiceAdapter.OnClickListener() {
                @Override
                public void OnItemClick(Invoice invoice, int position) {
                    enableActionMode(position);
                    selectionActive = true;
                }
            });

            bindingInvoice.apply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadSelectedInvoice();
                    alertDialog_invoice.dismiss();
                }
            });

            bindingInvoice.cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog_invoice.dismiss();
                }
            });
        }
        else {
            binding.customer.setError("select customer");
        }
    }

    private void loadSelectedInvoice() {
        invoiceSelectedList.clear();
        float totalAmount = 0;
        List<Integer> listSelectedItem = invoiceAdapter.getSelectedItems();
        for (int i=0;i<listSelectedItem.size();i++) {
            for (int j=0;j<invoiceList.size();j++) {
                if (listSelectedItem.get(i) == j) {
                   Invoice invoice=new Invoice(invoiceList.get(j).getiTransId(),
                           invoiceList.get(j).getInvDate(),
                           invoiceList.get(j).getInvNo(),
                           invoiceList.get(j).getAmount(),
                           invoiceList.get(j).getCustomer(),
                           invoiceList.get(j).getCustomerCode()
                           );
                   invoiceSelectedList.add(invoice);
                   invoiceSelectedAdapter.notifyDataSetChanged();
                   totalAmount+=  Float.parseFloat(invoiceList.get(j).getAmount());
                }
                if(invoiceSelectedList.size()>0) {
                    binding.linearInvoice.setVisibility(View.VISIBLE);
                }
            }

            if (i + 1 == listSelectedItem.size()) {
                binding.recycleInvoiceHome.setLayoutManager(new LinearLayoutManager(requireContext()));
                binding.recycleInvoiceHome.setAdapter(invoiceSelectedAdapter);
                binding.amount.setText(totalAmount+"");

               invoiceSelectedAdapter.setOnClickListener(new InvoiceSelectedAdapter.OnClickListener() {
                   @Override
                   public void onItemClick(List<Invoice> list, int position) {

                       onItemClickInvoice(list,position);

                   }
               });
            }
        }
    }

    private void onItemClickInvoice(List<Invoice> list, int position) {
        float total_Amount=0;
        for (int i=0;i<list.size();i++) {
            total_Amount += Float.parseFloat(list.get(i).getAmount());
            Log.d("total_Amount",total_Amount+"  "+list.get(i).getAmount());
            if (i + 1 == list.size()) {
                binding.amount.setText(total_Amount + "");
            }
        }
    }

    private void enableActionMode(int position) {
        toggleSelection(position);
    }

    private void toggleSelection(int position) {
        invoiceAdapter.toggleSelection(position);
        int count = invoiceAdapter.getSelectedItemCount();
        if (count == 0) {
            closeSelection();
        }
    }

    private void closeSelection() {
        invoiceAdapter.clearSelection();
        selectionActive = false;
    }

    private void API_Invoice() {
        AndroidNetworking.get("http://"+ URLs.GetInvoiceList)
                .addQueryParameter("iCustomer",String.valueOf(iCustomer))
                .addQueryParameter("iType",String.valueOf(iDocType))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("responseInvoice",response.toString());
                        load_API_Invoice(response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("responseInvoice",anError.toString());

                    }
                });
    }

    private void load_API_Invoice(JSONArray response) {
        invoiceList.clear();
        alertDialog_invoice.show();
        try {
            JSONArray jsonArray=new JSONArray(response.toString());
            for (int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                Invoice invoice=new Invoice(jsonObject.getInt(Invoice.I_TRANS_ID),
                        jsonObject.getString(Invoice.INV_DATE),
                        jsonObject.getString(Invoice.INV_NO),
                        jsonObject.getString(Invoice.AMOUNT),
                        jsonObject.getString(Invoice.CUSTOMER),
                        jsonObject.getString(Invoice.CUSTOMER_CODE));

                invoiceList.add(invoice);
                invoiceAdapter.notifyDataSetChanged();

                Log.d("invoiceList",invoiceList.size()+"");

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

    private void initialValueSettingHeader() {
        alertDialog.show();
        List<String> list= Arrays.asList("Cash","Cheque");
        UnitAdapter unitAdapter = new UnitAdapter(list, requireActivity());
        binding.paymentSpinner.setAdapter(unitAdapter);
        StringDate = df.format(new Date());
        binding.date.setText(StringDate);
        binding.checkDate.setText(StringDate);

        if (iDocType == 1) {
            toolTitle = "Payment Summary";
        } else {
            toolTitle = "Receipt Summary";
        }

        if(Tools.isConnected(requireActivity())) {
            if (EditMode) {
                EditValueFromAPI();
            }
            else {

                Cursor cursor = helper.getUserCode(userIdS);
                if (cursor.moveToFirst() && cursor.getCount() > 0) {
                    userCode = cursor.getString(cursor.getColumnIndex(User.USER_CODE));
                }
                final int[] arrayLength = new int[1];
                AndroidNetworking.get("http://" +URLs.GetTransReceipt_PaymentSummary)
                        .addQueryParameter("iDocType", String.valueOf(iDocType))
                        .addQueryParameter("iUser", userIdS)
                        .setPriority(Priority.MEDIUM)
                        .build()
                        .getAsJSONArray(new JSONArrayRequestListener() {
                            @Override
                            public void onResponse(JSONArray response) {
                                Log.d("responseHistory", response.toString());
                                try {
                                    JSONArray jsonArray = new JSONArray(response.toString());
                                    arrayLength[0] = jsonArray.length() + 1;
                                    docNo = userCode + "-" + DateFormat.format("MM", new Date()) + "-" + "000" + arrayLength[0];
                                    binding.docNo.setText(docNo);
                                    alertDialog.dismiss();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(ANError anError) {
                                Log.d("responseTotalNumber", anError.toString());
                                alertDialog.dismiss();
                            }
                        });
            }
        }
        else {
            Toast.makeText(requireActivity(), "NO Internet", Toast.LENGTH_SHORT).show();
            alertDialog.dismiss();
            NavDirections actions = PaymentReceiptFragmentDirections.actionPaymentReceiptFragmentToPaymentReceiptHistoryFragment(toolTitle,iDocType);
            navController.navigate(actions);
        }
    }

    private void EditValueFromAPI() {
        if(Tools.isConnected(requireContext())){
            AndroidNetworking.get("http://" +URLs.GetTransReceipt_PaymentDetails)
                    .addQueryParameter("iTransId",String.valueOf(iTransId))
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("Response_loadEditValue",response.toString());
                            loadAPIValue_for_Edit(response);

                        }

                        @Override
                        public void onError(ANError anError) {
                            Log.d("Response_loadEditValue",anError.toString());
                            alertDialog.dismiss();

                        }
                    });
        }else {
            Toast.makeText(requireActivity(), "NO Internet", Toast.LENGTH_SHORT).show();
            alertDialog.dismiss();
        }
    }

    private void loadAPIValue_for_Edit(JSONObject response) {
        try {
            JSONArray jsonArray=new JSONArray(response.getString("Table"));
            Log.d("resultArray",jsonArray.length()+"");

            JSONArray jsonArray1=new JSONArray(response.getString("Table1"));
            Log.d("resultArray1",jsonArray1.length()+"");

            for (int i=0;i<jsonArray.length();i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                binding.docNo.setText(jsonObject.getString("sDocNo"));
                docNo=jsonObject.getString("sDocNo");
                binding.date.setText(jsonObject.getString("sDate"));
                iCustomer = jsonObject.getInt("iAccount1");
                binding.customer.setText(jsonObject.getString("sAccount1"));
                binding.description.setText(jsonObject.getString("sNarration"));
                binding.amount.setText(jsonObject.getString("fAmount"));
                iPaymentMethod = jsonObject.getInt("iPaymentMethod");
                if(iPaymentMethod==2){
                    binding.paymentSpinner.setSelection(1);
                    iBank = jsonObject.getInt("iBank");
                    binding.bankName.setText(jsonObject.getString("sBank"));
                    binding.checkNo.setText(jsonObject.getString("iChequeNo"));
                    binding.checkDate.setText(jsonObject.getString("sChequeDate"));
                }
            }
            for (int j=0;j<jsonArray1.length();j++) {
                JSONObject jsonObjectInner = jsonArray1.getJSONObject(j);
                for (int k = 0; k < headerListTags.size(); k++) {
                    hashMapHeader.put(headerListTags.get(k), jsonObjectInner.getInt("iTag" + headerListTags.get(k)));
                    autoText_H_list.get(k).setText(jsonObjectInner.getString("sTag" + headerListTags.get(k)));
                }


                Invoice invoice=new Invoice();
                invoice.setiTransId(jsonObjectInner.getInt("iRefDocId"));
                invoice.setAmount(jsonObjectInner.getString("fAmount"));
                invoice.setInvDate(jsonObjectInner.getString("InvDate"));
                invoice.setInvNo(jsonObjectInner.getString("InvNo"));

                Log.d("invoice",jsonObjectInner.getString("iRefDocId"));
                if(jsonObjectInner.getInt("iRefDocId")!=0){
                    binding.linearInvoice.setVisibility(View.VISIBLE);
                }
                invoiceSelectedList.add(invoice);
                invoiceSelectedAdapter.notifyDataSetChanged();
                if(j+1==jsonArray1.length()) {
                    binding.recycleInvoiceHome.setLayoutManager(new LinearLayoutManager(requireContext()));
                    binding.recycleInvoiceHome.setAdapter(invoiceSelectedAdapter);
                    invoiceSelectedAdapter.setOnClickListener(new InvoiceSelectedAdapter.OnClickListener() {
                        @Override
                        public void onItemClick(List<Invoice> list, int position) {
                            onItemClickInvoice(list,position);
                        }
                    });
                }

            }

                alertDialog.dismiss();

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private TextWatcher getTextWatcher(AutoCompleteTextView autocompleteView, int iTag, String iTagPosition) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                autocompleteView.setThreshold(1);
                autocompleteView.setAdapter(tagDetailsAdapter);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                GetTagDetails(editable.toString(),iTag,autocompleteView,iTagPosition);

            }
        };
    }

    private void GetTagDetails(String s, int iTag, AutoCompleteTextView autocompleteView, String iTagPosition) {

        tagList.clear();
        Cursor cursor=helper.getTagbyKeyword(s,iTag);
        if(cursor!=null && !s.equals("")) {
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                for (int i = 0; i < cursor.getCount(); i++) {
                    TagDetails details=new TagDetails();
                    details.setsName(cursor.getString(cursor.getColumnIndex(TagDetails.S_NAME)));
                    details.setiId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(TagDetails.I_ID))));
                    tagList.add(details);
                    tagDetailsAdapter.notifyDataSetChanged();
                    cursor.moveToNext();

                    tagDetailsAdapter.setOnClickListener(new TagDetailsAdapter.OnClickListener() {
                        @Override
                        public void onItemClick(TagDetails tagDetails, int position) {

                            autocompleteView.setText(tagDetails.getsName());
                            iTagDetail=tagDetails.getiId();
                            if(iTagPosition.equals("Body")){
//                                hashMapBody.put(iTag,iTagDetail);

                            }
                            else if(iTagPosition.equals("Header"))
                            {
                                hashMapHeader.put(iTag,iTagDetail);
                            }
                            autocompleteView.dismissDropDown();
                        }
                    });

                }

            }
        }
        else {

            tagList.clear();
        }
    }
}
