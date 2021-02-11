package com.sangsolutions.sang.Fragment;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
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
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.material.snackbar.Snackbar;
import com.sangsolutions.sang.Adapter.BankAdapter.Bank;
import com.sangsolutions.sang.Adapter.BankAdapter.BankAdapter;
import com.sangsolutions.sang.Adapter.BodyAdapter.BodyPart;
import com.sangsolutions.sang.Adapter.BodyAdapter.BodyPartAdapter;
import com.sangsolutions.sang.Adapter.Customer.Customer;
import com.sangsolutions.sang.Adapter.Customer.CustomerAdapter;
import com.sangsolutions.sang.Adapter.InvoiceAdapter.Invoice;
import com.sangsolutions.sang.Adapter.InvoiceAdapter.InvoiceAdapter;
import com.sangsolutions.sang.Adapter.InvoiceAdapter.InvoiceSelectedAdapter;
import com.sangsolutions.sang.Adapter.MasterSettings.MasterSettings;
import com.sangsolutions.sang.Adapter.Products.Products;
import com.sangsolutions.sang.Adapter.Products.ProductsAdapter;
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
import com.sangsolutions.sang.databinding.ProductDialogueBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
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
    int numberOfLinesB;
    ArrayList<AutoCompleteTextView> autoText_B_list;
    ArrayList<AutoCompleteTextView>autoText_H_list;

    ArrayList<AutoCompleteTextView> mandatoryList_H;
    ArrayList<AutoCompleteTextView> mandatoryList_B;

    List<Bank>bankList;
    BankAdapter bankAdapter;

    List<Customer> customerList;
    CustomerAdapter customerAdapter;

    List<Invoice>invoiceList;
    InvoiceAdapter invoiceAdapter;

    List<Invoice>invoiceSelectedList;
    InvoiceSelectedAdapter invoiceSelectedAdapter;

    int iCustomer,iProduct,iTagDetail,iBank,iPaymentMethod;
    AlertDialog alertDialog_invoice;

    boolean selectionActive = false;

    InvoiceDialogeLayoutBinding bindingInvoice;
    ProductDialogueBinding bindingProduct;


    List<TagDetails> tagList;
    TagDetailsAdapter tagDetailsAdapter;

    HashMap<Integer, Integer> hashMapBody;
    HashMap<Integer, Integer> hashMapHeader;

    CameraSource cameraSource;
    BarcodeDetector barcodeDetector;

    String productBarCode,productId;

    List<Products>productsList;
    ProductsAdapter productsAdapter;

    boolean editModeProduct;
    int position_body_Edit;


    List<BodyPart>bodyPartList;
    BodyPartAdapter bodyPartAdapter;

    AlertDialog alertDialog_product;

    List<Integer> headerListTags;
    List<Integer>bodyListTags;

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
        bodyListTags =new ArrayList<>();

        autoText_H_list=new ArrayList<>();
        mandatoryList_H =new ArrayList<>();

        bankList =new ArrayList<>();
        bankAdapter=new BankAdapter(requireActivity(),bankList);


        customerList =new ArrayList<>();
        customerAdapter=new CustomerAdapter(requireActivity(),customerList);


        invoiceList=new ArrayList<>();
        invoiceAdapter =new InvoiceAdapter(requireContext(),invoiceList);

        invoiceSelectedList=new ArrayList<>();
        invoiceSelectedAdapter=new InvoiceSelectedAdapter(requireContext(),invoiceSelectedList);

        tagList =new ArrayList<>();
        tagDetailsAdapter =new TagDetailsAdapter(requireActivity(),tagList);

        productsList=new ArrayList<>();
        productsAdapter=new ProductsAdapter(requireActivity(),productsList);

        hashMapBody=new HashMap<>();
        hashMapHeader=new HashMap<>();


       Toast.makeText(requireContext(), iDocType+"", Toast.LENGTH_SHORT).show();
        Cursor cursor_userId=helper.getUserId();
        if(cursor_userId!=null &&cursor_userId.moveToFirst()) {
            userIdS = cursor_userId.getString(cursor_userId.getColumnIndex("user_Id"));
        }
        cursorTagNumber =helper.getTotalTagNumber();
        if(cursorTagNumber!=null) {
            tagTotalNumber = cursorTagNumber.getCount();
        }

        bodyPartList=new ArrayList<>();
        bodyPartAdapter=new BodyPartAdapter(requireActivity(),bodyPartList,tagTotalNumber,iDocType);
        binding.boyPartRV.setLayoutManager(new LinearLayoutManager(requireActivity()));

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
                    // add autocompleteTextView
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
                else if(iTagPosition.equals("2")) {

                    bodyListTags.add(tagId);
                    LinearLayout l_tags = binding.linearTags;
                    // add TextView
                    TextView textView=new TextView(requireActivity());
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.MATCH_PARENT);

                    textView.setLayoutParams(params);
                    params.setMargins(5,0,5,0);
                    l_tags.addView(textView);
                    textView.setWidth(150);
                    textView.setGravity(Gravity.CENTER);
                    textView.setText(cursor1.getString(cursor1.getColumnIndex(MasterSettings.S_NAME)));
                    textView.setTextColor(Color.WHITE);
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

        binding.addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productDialogue();
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
       return binding.getRoot();
    }

    private void deleteAll() {
        initialValueSettingHeader();
//        initialValueSettingBody();
        binding.customer.setText("");
        binding.description.setText("");
        binding.amount.setText("");
        binding.bankName.setText("");
        binding.checkNo.setText("");
        binding.linearInvoice.setVisibility(View.GONE);
        for (int i=0;i<autoText_H_list.size();i++){
            autoText_H_list.get(i).setText("");
            Log.d("autoText_H_list",autoText_B_list.size()+""+autoText_H_list.get(i).getTag(i).toString());
        }
        bodyPartList.clear();
        bodyPartAdapter.notifyDataSetChanged();

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

    private void mandatoryChecking() {
        boolean flag = true;
        if (bodyPartList.size() > 0) {
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
        else {
            Toast.makeText(requireContext(), "No products to add", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadToJson() {
        Log.d("bodypartListSizee",bodyPartList.size()+"");


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

            Log.d("jsonObjecMainp",jsonObjectMain.get("iPaymentMethod")+"");
            Log.d("jsonObjecMainbank",jsonObjectMain.get("iBank")+"");
            Log.d("jsonObjecMainche",jsonObjectMain.get("iChequeNo")+"");
            Log.d("jsonObjecMainda",jsonObjectMain.get("sChequeDate")+"");

            JSONArray jsonArray=new JSONArray();
            for (int i=0;i<bodyPartList.size();i++){
                JSONObject jsonObject=new JSONObject();

                Log.d("bodyPartList size",bodyPartList.size()+""+i);

                for (int j=1;j<=tagTotalNumber;j++){
                    if(hashMapHeader.containsKey(j)){
                        jsonObject.put("iTag"+j,hashMapHeader.get(j));
                    }
                    else if(bodyPartList.get(i).hashMapBody.containsKey(j)){
                        jsonObject.put("iTag"+j, bodyPartList.get(i).hashMapBody.get(j));
                    }
                    else {
                        jsonObject.put("iTag"+j,0);
                    }
                }



//                Log.d("Tagss",jsonObject.get("iTag1")+"");
//                Log.d("Tagss",jsonObject.get("iTag2")+"");
//                Log.d("Tagss",jsonObject.get("iTag3")+"");
//                Log.d("Tagss",jsonObject.get("iTag4")+"");
//                Log.d("Tagss",jsonObject.get("iTag5")+"");
//                Log.d("Tagss",jsonObject.get("iTag6")+"");
//                Log.d("Tagss",jsonObject.get("iTag7")+"");
//                Log.d("Tagss",jsonObject.get("iTag8")+"");
//                Log.d("Tagss",jsonObject.get("iTag9")+"");

                jsonObject.put("iProduct",bodyPartList.get(i).getiProduct());
                jsonObject.put("fQty",bodyPartList.get(i).getQty());
                jsonObject.put("fRate",bodyPartList.get(i).getRate());
                jsonObject.put("fDiscount",bodyPartList.get(i).getDiscount());
                jsonObject.put("fAddCharges",bodyPartList.get(i).getAddCharges());
                jsonObject.put("fVatPer",bodyPartList.get(i).getVatPer());
                jsonObject.put("fVAT",bodyPartList.get(i).getVat());
                jsonObject.put("sRemarks",bodyPartList.get(i).getRemarks());
                jsonObject.put("sUnits",bodyPartList.get(i).getUnit());



                Log.d("jsonObjecttIproduct",jsonObject.get("iProduct")+"");
                Log.d("jsonObjecttQty",jsonObject.get("fQty")+"");
                Log.d("jsonObjecttrate",jsonObject.get("fRate")+"");
                Log.d("jsonObjecttdis",jsonObject.get("fDiscount")+"");
                Log.d("jsonObjecttaddcha",jsonObject.get("fAddCharges")+"");
                Log.d("jsonObjecttvarper",jsonObject.get("fVatPer")+"");
                Log.d("jsonObjecttvat",jsonObject.get("fVAT")+"");
                Log.d("jsonObjecttrema",jsonObject.get("sRemarks")+"");
                Log.d("jsonObjecttrema",jsonObject.get("sUnits")+"");

                jsonArray.put(jsonObject);
            }
            jsonObjectMain.put("Body",jsonArray);

            uploadToAPI(jsonObjectMain);

        } catch (JSONException e) {

            Log.d("exception",e.getMessage());
            e.printStackTrace();
        }

    }

    private void uploadToAPI(JSONObject jsonObjectMain) {
        if(Tools.isConnected(requireActivity())) {
            alertDialog.show();
            AndroidNetworking.post("http://"+ new Tools().getIP(requireActivity()) + URLs.Post_Receipt_Payment)
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
                                bodyPartList.clear();
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

    private void productDialogue() {

        autoText_B_list=new ArrayList<>();
        mandatoryList_B =new ArrayList<>();

        bindingProduct=ProductDialogueBinding.inflate(getLayoutInflater());
        AlertDialog.Builder builderMain = new AlertDialog.Builder(requireActivity(), android.R.style.Theme_Light_NoTitleBar_Fullscreen);
        builderMain.setView(bindingProduct.getRoot());
        builderMain.setCancelable(false);
        alertDialog_product = builderMain.create();
        alertDialog_product.show();
        bindingProduct.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog_product.dismiss();
            }
        });
        bindingProduct.barcodeI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                barcodeScanningChecking();
            }
        });
        bindingProduct.productName.addTextChangedListener(new TextWatcher() {
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

        bindingProduct.productName.setThreshold(1);
        bindingProduct.productName.setAdapter(productsAdapter);
        if (bindingProduct.surfaceView.getVisibility() == View.VISIBLE) {
            bindingProduct.productName.dismissDropDown();
        }

            for (int tagId=1;tagId<=tagTotalNumber;tagId++) {
            Cursor cursor = helper.getTransSettings(iDocType, tagId);
            if (cursor != null) {
                cursor.moveToFirst();
                String iTagPosition = cursor.getString(cursor.getColumnIndex(TransSetting.I_TAG_POSITION));
                String mandatory = cursor.getString(cursor.getColumnIndex(TransSetting.B_MANDATORY));
                String visibility = cursor.getString(cursor.getColumnIndex(TransSetting.B_VISIBLE));

                Cursor cursor1 = helper.getTagNamebyId(tagId);
                cursor1.moveToFirst();
                if (iTagPosition.equals("2")) {

                    LinearLayout ll = bindingProduct.linearBody;
                    // add autocompleteTextView
                    AutoCompleteTextView autoTextBody = new AutoCompleteTextView(requireActivity());
                    LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    autoTextBody.setLayoutParams(p);
                    if (visibility.equals("false")) {
                        autoTextBody.setEnabled(false);
                    }
                    if (mandatory.equals("true")) {
                        mandatoryList_B.add(autoTextBody);
                    }
                    autoTextBody.setLongClickable(false);
                    autoTextBody.cancelLongPress();
                    autoTextBody.setHint(cursor1.getString(cursor1.getColumnIndex(MasterSettings.S_NAME)));
                    autoTextBody.setId(numberOfLinesB + 1);
                    autoTextBody.setTag("tag" + autoTextBody.getId() + "Body");
                    ll.addView(autoTextBody);
                    numberOfLinesB++;
                    autoText_B_list.add(autoTextBody);
                    autoTextBody.addTextChangedListener(getTextWatcher(autoTextBody, tagId, "Body"));
                }
            }
        }




        bindingProduct.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("mandatorytListSize",mandatoryList_B.size()+"");
                boolean flag=true;
                if(mandatoryList_B.size()<=0){
                    saveBodyPartProduct();
                }

                for (int i = 0; i < mandatoryList_B.size(); i++) {
                    Log.d("mandatoryList_B",mandatoryList_B.get(i).getText().toString());

                    if (!mandatoryList_B.get(i).getText().toString().equals("")) {
                        if (i + 1 == mandatoryList_B.size() && flag ) {
                            saveBodyPartProduct();
                        }

                    } else {
                        Log.d("mandatoryList_Bj",mandatoryList_B.get(i).getId()+"");
                        Toast.makeText(requireContext(), "Mandatory fields are not filled", Toast.LENGTH_SHORT).show();
                        mandatoryList_B.get(i).setError("Mandatory");
                        flag = false;
                    }
                }
            }
        });


    }

    private void saveBodyPartProduct() {
        float rate,gross,net,vatPer,vat,discount,addCharges;
        int qty;
        DecimalFormat df = new DecimalFormat("#.00");
        if(!bindingProduct.productName.getText().toString().equals("")  && helper.getProductNameValid(bindingProduct.productName.getText().toString().trim())) {
            if(!bindingProduct.qtyProduct.getText().toString().equals("")){
                if(!bindingProduct.rateProduct.getText().toString().equals("")){
                    if(!bindingProduct.vatProduct.getText().toString().equals("")){
                        if(!bindingProduct.disProduct.getText().toString().equals("")){
                            if(!bindingProduct.addChargesProduct.getText().toString().equals("")){

                                BodyPart bodyPart=new BodyPart();

                                qty=Integer.parseInt(bindingProduct.qtyProduct.getText().toString());
                                rate=Float.parseFloat(bindingProduct.rateProduct.getText().toString());
                                vatPer=Float.parseFloat(bindingProduct.vatProduct.getText().toString());
                                discount=Float.parseFloat(bindingProduct.disProduct.getText().toString());
                                addCharges=Float.parseFloat(bindingProduct.addChargesProduct.getText().toString());

                                gross=qty*rate;
                                vat=((vatPer/100)*(gross-discount+addCharges));
                                net=gross-discount+addCharges+vat;

                                bodyPart.setGross(gross);
                                bodyPart.setNet(Float.parseFloat(df.format(net)));
                                bodyPart.setVat(Float.parseFloat(df.format(vat)));
                                bodyPart.setVatPer(vatPer);
                                bodyPart.setDiscount(discount);
                                bodyPart.setAddCharges(addCharges);
                                bodyPart.setProductName(bindingProduct.productName.getText().toString());
                                bodyPart.setQty(qty);
                                bodyPart.setRate(rate);
                                bodyPart.setiProduct(iProduct);
                                bodyPart.setHashMapBody(hashMapBody);
                                bodyPart.setUnit(bindingProduct.spinnerUnit.getSelectedItem().toString());

                                bodyPart.setRemarks(bindingProduct.remarksProduct.getText().toString());

                                if(editModeProduct) {
                                    bodyPartList.set(position_body_Edit,bodyPart);

                                }else {
                                    bodyPartList.add(bodyPart);

                                }
                                bodyPartAdapter.notifyDataSetChanged();


//                                if(bodyPartList.size()>0){
//                                    binding.frameEmpty.setVisibility(View.GONE);
//                                }
//                                else {
//                                    binding.frameEmpty.setVisibility(View.VISIBLE);
//                                }
                                binding.boyPartRV.setAdapter(bodyPartAdapter);


                                initialValueSettingBody();

                                /////////////////////////////////////////editProduct

                                bodyPartAdapter.setOnClickListener(new BodyPartAdapter.OnClickListener() {
                                    @Override
                                    public void onItemClick(BodyPart bodyPart, int position) {

                                        editingProductField(bodyPart,position);

                                    }
                                });

                                ////////////////////////////////////////editProduct

                            }else {bindingProduct.addChargesProduct.setError("no addChargesProduct");}
                        }else {bindingProduct.disProduct.setError("no disProduct");}
                    }else {bindingProduct.vatProduct.setError("no Vat");}
                }else {bindingProduct.rateProduct.setError("no Rate");}
            }else {bindingProduct.qtyProduct.setError("no qty");}
        }else {bindingProduct.productName.setError("enter valid product");}

    }

    private void editingProductField(BodyPart bodyPart, int position) {
        alertDialog_product.show();
        editModeProduct =true;
        position_body_Edit=position;
//                                        bindingProduct.cardViewBody.setVisibility(View.VISIBLE);
        bindingProduct.productName.setText(bodyPart.getProductName());
        iProduct=bodyPart.getiProduct();
        bindingProduct.qtyProduct.setText(String.valueOf(bodyPart.getQty()));
        bindingProduct.rateProduct.setText(String.valueOf(bodyPart.getRate()));
        bindingProduct.vatProduct.setText(String.valueOf(bodyPart.getVatPer()));
        bindingProduct.disProduct.setText(String.valueOf(bodyPart.getDiscount()));
        bindingProduct.addChargesProduct.setText(String.valueOf(bodyPart.getAddCharges()));
        bindingProduct.remarksProduct.setText(bodyPart.getRemarks());
        setUnit(helper.getProductUnitById(iProduct),position);

        try {
            for (int i = 0; i < autoText_B_list.size(); i++) {
                int tagId = (int) bodyPartList.get(position).hashMapBody.keySet().toArray()[i];
                int tagDetails = (int) bodyPartList.get(position).hashMapBody.values().toArray()[i];
                Cursor cursor = helper.getTagName(tagId, tagDetails);
                autoText_B_list.get(i).setText(cursor.getString(cursor.getColumnIndex(TagDetails.S_NAME)));
                hashMapBody.put(tagId, tagDetails);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void initialValueSettingBody() {
        editModeProduct =false;
        hashMapBody=new HashMap<>();
        bindingProduct.productName.setText("");
        bindingProduct.qtyProduct.setText("");
        bindingProduct.rateProduct.setText("");
        bindingProduct.vatProduct.setText("");
        bindingProduct.disProduct.setText("");
        bindingProduct.addChargesProduct.setText("");
        bindingProduct.remarksProduct.setText("");
        setUnit("",-1);
        for (int i=0;i<autoText_B_list.size();i++){
            autoText_B_list.get(i).setText("");
        }

//        if(bodyPartList.size()==0) {
//            binding.frameEmpty.setVisibility(View.VISIBLE);
//        }

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
                            bindingProduct.productName.setText(products.getsName());
                            iProduct = products.getiId();

                            setUnit(helper.getProductUnitById(iProduct),-1);
                            bindingProduct.productName.dismissDropDown();
                        }
                    });
                }
            }
        }
    }

    private void setUnit(String units, int position) {
        List<String> list;
        list = Arrays.asList(units.split("\\s*,\\s*"));
        UnitAdapter unitAdapter = new UnitAdapter(list, requireActivity());
        bindingProduct.spinnerUnit.setAdapter(unitAdapter);
        if (bodyPartList.size() > 0) {
            if (position != -1)
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).equals(bodyPartList.get(position).getUnit())) {
                        bindingProduct.spinnerUnit.setSelection(i);
                    }
                }
        }
    }

    private void barcodeScanningChecking() {

        if(ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(requireActivity(),new String[]{Manifest.permission.CAMERA},101);
        }
        else {
            if (bindingProduct.surfaceView.getVisibility() == View.VISIBLE) {
                if(cameraSource!=null){
                    cameraSource.stop();
                }
                bindingProduct.surfaceView.setVisibility(View.GONE);
            }
            else if(bindingProduct.surfaceView.getVisibility()==View.GONE){
                bindingProduct.surfaceView.setVisibility(View.VISIBLE);
                barcodeScanning();
            }
        }
    }

    private void barcodeScanning() {

        barcodeDetector=new BarcodeDetector.Builder(requireActivity()).setBarcodeFormats(Barcode.ALL_FORMATS).build();
        cameraSource=new CameraSource.Builder(requireActivity(),barcodeDetector).setAutoFocusEnabled(true)
                .setRequestedPreviewSize(1080,1920).build();

        bindingProduct.surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {

                if(ActivityCompat.checkSelfPermission(requireContext(),Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(requireActivity(),new String[]{Manifest.permission.CAMERA},101);
                }
                else {
                    try {
                        cameraSource.start(bindingProduct.surfaceView.getHolder());
                    } catch (IOException e) {
                        Log.d("exception", e.toString()) ;
                    }
                }

            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

            }
        });


        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
                Toast.makeText(requireContext(), "barcode released", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> array = detections.getDetectedItems();
                if (array.size() > 0) {

                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        public void run() {
                            bindingProduct.productName.dismissDropDown();

                            productBarCode=array.valueAt(0).displayValue;
                            Cursor cursor=helper.getProductDetailsByBarcode(productBarCode);

                            if(cursor!=null && cursor.moveToFirst()){
                                bindingProduct.productName.setText(cursor.getString(cursor.getColumnIndex(Products.S_NAME)));
                                productId=cursor.getString(cursor.getColumnIndex(Products.I_ID));
                            }
                        }
                    });
                }
            }
        });
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
                           invoiceList.get(j).getDocDate(),
                           invoiceList.get(j).getDocNo(),
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
                       float total_Amount=0;
                       for (int i=0;i<list.size();i++) {
                           total_Amount += Float.parseFloat(list.get(i).getAmount());
                           Log.d("total_Amount",total_Amount+"  "+list.get(i).getAmount());
                           if (i + 1 == list.size()) {
                               binding.amount.setText(total_Amount + "");
                           }
                       }
                   }
               });
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
        AndroidNetworking.get("http://"+new Tools().getIP(requireContext())+ URLs.GetInvoiceList)
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
                        jsonObject.getString(Invoice.DOC_DATE),
                        jsonObject.getString(Invoice.DOC_NO),
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
                String userCode = null;
                Cursor cursor = helper.getUserCode(userIdS);
                if (cursor.moveToFirst() && cursor.getCount() > 0) {
                    userCode = cursor.getString(cursor.getColumnIndex(User.USER_CODE));
                }
                final int[] arrayLength = new int[1];
                String finalUserCode = userCode;
                AndroidNetworking.get("http://" + new Tools().getIP(requireActivity()) + URLs.GetTransReceipt_PaymentSummary)
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
                                    docNo = finalUserCode + "-" + DateFormat.format("MM", new Date()) + "-" + "000" + arrayLength[0];
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
        }
    }

    private void EditValueFromAPI() {
        if(Tools.isConnected(requireContext())){
            AndroidNetworking.get("http://" + new Tools().getIP(requireActivity()) + URLs.GetTransReceipt_PaymentDetails)
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
            for (int j=0;j<jsonArray1.length();j++){
                JSONObject jsonObjectInner = jsonArray1.getJSONObject(j);
                for (int k = 0; k< headerListTags.size(); k++){
//                    Log.d("iTagHeader", headerListTags.get(k)+"");
                    hashMapHeader.put(headerListTags.get(k),jsonObjectInner.getInt("iTag"+ headerListTags.get(k)));
                    autoText_H_list.get(k).setText(jsonObjectInner.getString("sTag"+ headerListTags.get(k)));
//                        Log.d("iTag"+ headerListTags.get(k),jsonObjectInner.getString("iTag"+ headerListTags.get(k)));
                }
                Log.d("iTagHeader", bodyListTags.size() +"");

                for(int k=0;k<bodyListTags.size();k++){
                Log.d("iTagHeader", bodyListTags.get(k)+"");
                    hashMapBody.put(bodyListTags.get(k),jsonObjectInner.getInt("iTag"+ bodyListTags.get(k)));
                    Log.d("iTaggg"+ bodyListTags.get(k),jsonObjectInner.getString("iTag"+ bodyListTags.get(k)));

                }


                float gross=jsonObjectInner.getInt("fQty")*Float.parseFloat(jsonObjectInner.getString("fRate"));

                BodyPart bodyPart=new BodyPart();
                bodyPart.setiProduct(jsonObjectInner.getInt("iProduct"));
                bodyPart.setProductName(jsonObjectInner.getString("sProduct"));
                bodyPart.setQty(jsonObjectInner.getInt("fQty"));
                bodyPart.setGross(gross);
                bodyPart.setNet(0.0f);
                bodyPart.setRate(Float.parseFloat(jsonObjectInner.getString("fRate")));
                bodyPart.setDiscount(Float.parseFloat(jsonObjectInner.getString("fDiscount")));
                bodyPart.setAddCharges(Float.parseFloat(jsonObjectInner.getString("fAddCharges")));
                bodyPart.setVatPer(Float.parseFloat(jsonObjectInner.getString("fVatPer")));
                bodyPart.setVat(jsonObjectInner.getInt("fVAT"));
                bodyPart.setRemarks(jsonObjectInner.getString("sRemarks"));
                bodyPart.setUnit(jsonObjectInner.getString("sUnits"));
                bodyPart.setHashMapBody(hashMapBody);

                bodyPartList.add(bodyPart);
                bodyPartAdapter.notifyDataSetChanged();
                hashMapBody=new HashMap<>();

                if(jsonArray1.length()==j+1){
                    binding.boyPartRV.setAdapter(bodyPartAdapter);
                    alertDialog.dismiss();

                    bodyPartAdapter.setOnClickListener(new BodyPartAdapter.OnClickListener() {
                        @Override
                        public void onItemClick(BodyPart bodyPart, int position) {
                            productDialogue();

                            editingProductField(bodyPart,position);
                        }
                    });
                }
            }
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
                                hashMapBody.put(iTag,iTagDetail);

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
