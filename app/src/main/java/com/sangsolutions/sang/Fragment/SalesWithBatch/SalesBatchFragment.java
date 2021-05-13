package com.sangsolutions.sang.Fragment.SalesWithBatch;

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
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
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
import com.sangsolutions.sang.Adapter.BatchPurchaseAdapter.BatchPurchase;
import com.sangsolutions.sang.Adapter.BatchPurchaseBodyAdapter.BatchPurchaseBody;
import com.sangsolutions.sang.Adapter.BatchPurchaseBodyAdapter.BatchPurchaseBodyAdapter;
import com.sangsolutions.sang.Adapter.BatchSalesAdapter.BatchSales;
import com.sangsolutions.sang.Adapter.BatchSalesAdapter.BatchSalesAdapter;
import com.sangsolutions.sang.Adapter.BatchSalesBodyAdapter.BatchSalesBody;
import com.sangsolutions.sang.Adapter.BatchSalesBodyAdapter.BatchSalesBodyAdapter;
import com.sangsolutions.sang.Adapter.BatchSalesSelectedAdapter.BatchSalesSelectedAdapter;
import com.sangsolutions.sang.Adapter.Customer.Customer;
import com.sangsolutions.sang.Adapter.Customer.CustomerAdapter;
import com.sangsolutions.sang.Adapter.InvoiceAdapter.InvoiceAdapter;
import com.sangsolutions.sang.Adapter.MasterSettings.MasterSettings;
import com.sangsolutions.sang.Adapter.Products.Products;
import com.sangsolutions.sang.Adapter.Products.ProductsAdapter;
import com.sangsolutions.sang.Adapter.TagDetailsAdapter.TagDetails;
import com.sangsolutions.sang.Adapter.TagDetailsAdapter.TagDetailsAdapter;
import com.sangsolutions.sang.Adapter.TransSalePurchase.TransSetting;
import com.sangsolutions.sang.Adapter.UnitAdapter;
import com.sangsolutions.sang.Adapter.User;
import com.sangsolutions.sang.Database.DatabaseHelper;
import com.sangsolutions.sang.Fragment.PurchaseWithBatch.PurchaseBatchFragmentDirections;
import com.sangsolutions.sang.Home;
import com.sangsolutions.sang.R;
import com.sangsolutions.sang.Tools;
import com.sangsolutions.sang.URLs;
import com.sangsolutions.sang.databinding.BatchSalesDialogueBinding;
import com.sangsolutions.sang.databinding.FragmentSalesBatchBinding;
import com.sangsolutions.sang.databinding.MainAlertSalesBatchBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class SalesBatchFragment extends Fragment {

    FragmentSalesBatchBinding binding;
    NavController navController;
    int iDocType,iTransId;
    boolean EditMode;
    SimpleDateFormat df;
    DatabaseHelper helper;
    AlertDialog alertDialog;
    String docNo;
    String StringDate;
    String userIdS = null;
    String userCode;

    Cursor cursorTagNumber;
    int tagTotalNumber;
    int numberOfLinesH;
    int numberOfLinesB;
    ArrayList<AutoCompleteTextView> autoText_B_list;
    ArrayList<AutoCompleteTextView>autoText_H_list;

    ArrayList<AutoCompleteTextView> mandatoryList_H;
    ArrayList<AutoCompleteTextView> mandatoryList_B;
    List<Customer> customerList;
    CustomerAdapter customerAdapter;

    List<TagDetails> tagList;
    TagDetailsAdapter tagDetailsAdapter;

    List<Products>productsList;
    ProductsAdapter productsAdapter;

    List<Integer> headerListTags;
    List<Integer>bodyListTags;

    HashMap<Integer, Integer> hashMapBody;
    HashMap<Integer, Integer> hashMapHeader;

    BatchSalesBodyAdapter bodyAdapter;
    List<BatchSalesBody>bodyList;
    List<BatchSalesBody>bodyListCopy;


    int iCustomer,iProduct,iTagDetail;
    MainAlertSalesBatchBinding batchBinding;

    boolean EditBatch = false;
    int batchEditPosition;


    boolean EditModeProduct =false;
    int position_body_Edit;

    Animation slideUp, slideDown;

    CameraSource cameraSource;
    BarcodeDetector barcodeDetector;

    BatchSalesDialogueBinding batchDialogueBinding;
    AlertDialog alertDialog_batch;

    List<BatchSales>batchSalesList;

    BatchSalesAdapter batchSalesAdapter;

    List<BatchSales>batchSalesSelectedList;

    List<BatchSales>batchSalesSelectedListEdit;
    List<BatchSales>batchSalesSelectedList1;
    BatchSalesSelectedAdapter batchSalesSelectedAdapter;

    boolean selectionActive = false;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding=FragmentSalesBatchBinding.inflate(getLayoutInflater());
        try {
            ((Home)getActivity()).setDrawerEnabled(false);
        }catch (Exception e){
            e.printStackTrace();
        }
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);

        assert getArguments() != null;
        iDocType = SalesBatchFragmentArgs.fromBundle(getArguments()).getIDocType();
        iTransId = SalesBatchFragmentArgs.fromBundle(getArguments()).getITransId();
        EditMode = SalesBatchFragmentArgs.fromBundle(getArguments()).getEditMode();
        Log.d("llllll","iDocType "+iDocType+" "+"iTransId "+iTransId+" "+"editMode "+ EditMode +"");
        df = new SimpleDateFormat("dd-MM-yyyy");
        helper=new DatabaseHelper(requireActivity());

        autoText_H_list=new ArrayList<>();
        mandatoryList_H =new ArrayList<>();
        autoText_B_list=new ArrayList<>();
        mandatoryList_B =new ArrayList<>();

        hashMapBody=new HashMap<>();
        hashMapHeader=new HashMap<>();

        headerListTags =new ArrayList<>();
        bodyListTags =new ArrayList<>();

        customerList =new ArrayList<>();
        customerAdapter=new CustomerAdapter(requireActivity(),customerList);

        tagList =new ArrayList<>();
        tagDetailsAdapter =new TagDetailsAdapter(requireActivity(),tagList);

        productsList=new ArrayList<>();
        productsAdapter=new ProductsAdapter(requireActivity(),productsList);

        slideDown = AnimationUtils.loadAnimation(getContext(), R.anim.move_down);
        slideUp = AnimationUtils.loadAnimation(getContext(), R.anim.move_up);
        binding.bottomBar.setVisibility(View.GONE);
        binding.bottomMoreDetails.setVisibility(View.GONE);



        AlertDialog.Builder builder=new AlertDialog.Builder(requireActivity());
        View view=LayoutInflater.from(requireActivity()).inflate(R.layout.progress_bar,null,false);
        builder.setView(view);
        builder.setCancelable(false);
        alertDialog = builder.create();

        Cursor cursor_userId=helper.getUserId();
        if(cursor_userId!=null &&cursor_userId.moveToFirst()) {
            userIdS = cursor_userId.getString(cursor_userId.getColumnIndex("user_Id"));
        }

        cursorTagNumber =helper.getTotalTagNumber();
        if(cursorTagNumber!=null) {
            tagTotalNumber = cursorTagNumber.getCount();
        }

        bodyList=new ArrayList<>();
        bodyListCopy=new ArrayList<>();
        bodyAdapter=new BatchSalesBodyAdapter(requireActivity(),bodyList,tagTotalNumber,iDocType);
        binding.boyPartRV.setLayoutManager(new LinearLayoutManager(requireActivity()));


        for (int tagId=1;tagId<=tagTotalNumber;tagId++) {
            Cursor cursor = helper.getTransSettings(iDocType, tagId);
            if (cursor != null) {
                cursor.moveToFirst();
                String iTagPosition = cursor.getString(cursor.getColumnIndex(TransSetting.I_TAG_POSITION));
                String mandatory = cursor.getString(cursor.getColumnIndex(TransSetting.B_MANDATORY));
                String visibility = cursor.getString(cursor.getColumnIndex(TransSetting.B_VISIBLE));

                Cursor cursor1 = helper.getTagNamebyId(tagId);
                cursor1.moveToFirst();
                if (iTagPosition.equals("1")) {

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


                }else if(iTagPosition.equals("2")){

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

        initialValueSettingHeader();

        binding.date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {


                        StringDate = Tools.checkDigit(dayOfMonth)+
                                "-" +
                                Tools.checkDigit(month + 1) +
                                "-"+
                                year;
                        binding.date.setText(StringDate);
                    }
                };
                Calendar now = Calendar.getInstance();
                int year = now.get(Calendar.YEAR);
                int month = now.get(Calendar.MONTH);
                int day = now.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(requireActivity(), onDateSetListener, year, month, day);
                datePickerDialog.show();
            }
        });

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

        binding.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mainAlert();

            }
        });


        binding.saveMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(requireContext());
                builder.setTitle("Save!")
                        .setMessage("Do you want to Save?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SaveMainTest();
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

        binding.bottomArrowUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bodyList.size()>0){
                    binding.bottomMoreDetails.startAnimation(slideUp);
                    binding.bottomMoreDetails.setVisibility(View.VISIBLE);
                    binding.bottomArrowUp.setVisibility(View.GONE);
                    binding.bottomArrowDown.setVisibility(View.VISIBLE);
                }
            }
        });

        binding.bottomArrowDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.bottomMoreDetails.startAnimation(slideDown);
                binding.bottomMoreDetails.setVisibility(View.GONE);
                binding.bottomArrowUp.setVisibility(View.VISIBLE);
                binding.bottomArrowDown.setVisibility(View.GONE);
            }
        });

        return binding.getRoot();
    }

    private void SaveMainTest() {

        boolean flag = true;
        if(bodyList.size()>0) {
            if (!binding.customer.getText().toString().equals("") &&
                    helper.getCustomerNameValid(binding.customer.getText().toString().trim())) {
                if (mandatoryList_H.size() <= 0) {
                    saveMain();
                }

                for (int i = 0; i < mandatoryList_H.size(); i++) {
                    if (!mandatoryList_H.get(i).getText().toString().equals("")
                            && helper.isTagValid(mandatoryList_H.get(i).getText().toString().trim())) {
                        if (i + 1 == mandatoryList_H.size() && flag) {
                            saveMain();
                        }
                    } else {
                        Toast.makeText(requireContext(), "Mandatory fields are not filled", Toast.LENGTH_SHORT).show();
                        mandatoryList_H.get(i).setError("Mandatory");
                        flag = false;
                    }
                }
            }else {binding.customer.setError("Enter Customer");}
        }else {
            Toast.makeText(requireActivity(), "No Products Added", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveMain() {

        if (Tools.isConnected(requireContext())) {

            JSONObject jsonObjectMain = new JSONObject();
            try {
                if(docNo.contains("L")){
                    iTransId=0;
                }
                jsonObjectMain.put("iTransId", iTransId);
                jsonObjectMain.put("sDocNo", docNo);
                jsonObjectMain.put("sDate", Tools.dateFormat(binding.date.getText().toString()));
                jsonObjectMain.put("iDocType", iDocType);
                jsonObjectMain.put("iAccount1", iCustomer);
                jsonObjectMain.put("iAccount2", 0);
                jsonObjectMain.put("sNarration", binding.description.getText().toString());
                assert userIdS != null;
                jsonObjectMain.put("iUser", Integer.parseInt(userIdS));


                JSONArray jsonArray = new JSONArray();
                for (int i = 0; i < bodyList.size(); i++) {
                    JSONObject jsonObject = new JSONObject();

                    Log.d("bodyPartList size", bodyList.size() + "" + i);

                    for (int j = 1; j <= tagTotalNumber; j++) {
                        if (hashMapHeader.containsKey(j)) {
                            jsonObject.put("iTag" + j, hashMapHeader.get(j));
                        } else if (bodyList.get(i).hashMapBody.containsKey(j)) {
                            jsonObject.put("iTag" + j, bodyList.get(i).hashMapBody.get(j));
                        } else {
                            jsonObject.put("iTag" + j, 0);
                        }
                    }

                    jsonObject.put("iProduct", bodyList.get(i).getiProduct());
                    jsonObject.put("fQty", bodyList.get(i).getTotalQty());
                    jsonObject.put("fRate", bodyList.get(i).getRate());
                    jsonObject.put("fDiscount", bodyList.get(i).getDiscount());
                    jsonObject.put("fAddCharges", bodyList.get(i).getAddCharges());
                    jsonObject.put("fVatPer", bodyList.get(i).getVatPer());
                    jsonObject.put("fVAT", bodyList.get(i).getVat());
                    if (bodyList.get(i).getRemarks().equals("")) {
                        jsonObject.put("sRemarks", "");
                    } else {
                        jsonObject.put("sRemarks", bodyList.get(i).getRemarks());
                    }
                    jsonObject.put("sUnits", bodyList.get(i).getUnit());
                    jsonObject.put("fNet", bodyList.get(i).getNet());


                    JSONArray jsonArrayBatch = new JSONArray();
                    for (int j = 0; j < bodyList.get(i).batchList.size(); j++) {
                        JSONObject jsonObjectBatch = new JSONObject();
                        jsonObjectBatch.put("sBatch", bodyList.get(i).batchList.get(j).getBatch());
                        jsonObjectBatch.put("fBatchQty", bodyList.get(i).batchList.get(j).getEnterQty());
                        jsonObjectBatch.put("MfDate", Tools.dateFormat(bodyList.get(i).batchList.get(j).getMfDate()));
                        jsonObjectBatch.put("ExpDate", Tools.dateFormat(bodyList.get(i).batchList.get(j).getExpDate()));

                        jsonArrayBatch.put(jsonObjectBatch);
                    }
                    jsonObject.put("Batch", jsonArrayBatch);

                    jsonArray.put(jsonObject);
                }

                jsonObjectMain.put("Body", jsonArray);
                Log.d("jsonArray", jsonObjectMain.toString());

                uploadToAPI(jsonObjectMain);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else {
//            saveLocally();
        }
    }

    private void uploadToAPI(JSONObject jsonObjectMain) {

        Log.d("uploadJSONoBJECT",jsonObjectMain.toString());
        if(Tools.isConnected(requireActivity())) {
            alertDialog.show();
            AndroidNetworking.post("http://"+ new Tools().getIP(requireActivity()) + URLs.PostTransWithBatch)
                    .addJSONObjectBody(jsonObjectMain)
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsString(new StringRequestListener() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("responsePostPBatch", response);
                            try {
                                iTransId=Integer.parseInt(response);
                                alertDialog.dismiss();
//                                if(helper.delete_Batch_P_Header(iTransId,iDocType,docNo)){
//                                    if(helper.delete_Batch_P_Body(iDocType,iTransId)){
//                                        if(helper.delete_Batch_P_Body_batch(iDocType,iTransId)) {
//                                            Log.d("responsePostPBatch ", "successfully " + iDocType);
                                            Toast.makeText(requireActivity(), "Posted successfully", Toast.LENGTH_SHORT).show();
                                            NavDirections actions = SalesBatchFragmentDirections.actionSalesBatchFragmentToSalesBatchHistoryFragment(28);
                                            navController.navigate(actions);
//                                        }
//                                    }
//                                }
                            }catch (Exception e){
                                alertDialog.dismiss();
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            alertDialog.dismiss();
                            Log.d("responsePost", anError.getErrorDetail() + anError.getErrorBody() + anError.toString());
                        }
                    });
        }

    }


    private void mainAlert() {
        initialBatchSetting();

        AlertDialog.Builder builder=new AlertDialog.Builder(requireContext(),android.R.style.Theme_Light_NoTitleBar_Fullscreen);
        builder.setView(batchBinding.getRoot());
        AlertDialog mainAlertBatch=builder.create();
        mainAlertBatch.show();
        mainAlertBatch.setCancelable(false);
        batchBinding.batchRV.setLayoutManager(new LinearLayoutManager(requireActivity()));

//        batchSalesSelectedList=new ArrayList<>();



        batchBinding.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainAlertBatch.dismiss();
            }
        });


        batchBinding.barcodeI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                barcodeScanningChecking();
            }
        });
        batchBinding.productName.addTextChangedListener(new TextWatcher() {
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

        batchBinding.productName.setThreshold(1);
        batchBinding.productName.setAdapter(productsAdapter);
        if (batchBinding.surfaceView.getVisibility() == View.VISIBLE) {
            batchBinding.productName.dismissDropDown();
        }


        batchBinding.selectBatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Tools.isConnected(requireActivity())){
                    if(!batchBinding.productName.getText().toString().equals("") &&
                            helper.getProductNameValid(batchBinding.productName.getText().toString().trim()))
                    {
                    if(!batchBinding.qtyProduct.getText().toString().equals("") &&
                    Integer.parseInt(batchBinding.qtyProduct.getText().toString())!=0) {
                        loadBatchFromAPI();
                    }else {batchBinding.qtyProduct.setError("please select qty");}
                }else { batchBinding.productName.setError("select Valid product"); }
                } else { Toast.makeText(requireActivity(), "You are offline!!", Toast.LENGTH_SHORT).show(); }

            }
        });

        batchBinding.addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(batchSalesSelectedList.size()>0) {
                    saveProduct();
                }else {
                    Toast.makeText(requireContext(), "Batch fields are empty", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }

    private void saveProduct() {
        boolean flag=true;
        if(mandatoryList_B.size()<=0){
            saveBodyPartProduct();
        }
        for (int i = 0; i < mandatoryList_B.size(); i++) {
            if (!mandatoryList_B.get(i).getText().toString().equals("") &&
                    helper.isTagValid(mandatoryList_B.get(i).getText().toString().trim())) {
                if (i + 1 == mandatoryList_B.size() && flag ) {

                    for(int j=0;j<bodyListTags.size();j++){
                        if(autoText_B_list.get(j).getText().toString().equals("")){
                            bodyListTags.remove(bodyListTags.get(j));
                        }
                    }
                    saveBodyPartProduct();
                }

            } else {
                Toast.makeText(requireContext(), "Mandatory fields are not filled properly", Toast.LENGTH_SHORT).show();
                mandatoryList_B.get(i).setError("Mandatory");
                flag = false;
            }
        }
    }

    private void saveBodyPartProduct() {
        int batchQty=0;
        for (int i = 0; i< batchSalesSelectedList.size(); i++){
            batchQty=batchQty+ batchSalesSelectedList.get(i).enterQty;
        }

            if (!batchBinding.productName.getText().toString().equals("") && helper.getProductNameValid(batchBinding.productName.getText().toString().trim())) {
                if (!batchBinding.spinnerUnit.getSelectedItem().toString().equals("")) {
                    if (!batchBinding.qtyProduct.getText().toString().equals("")) {
                        if (!batchBinding.rateProduct.getText().toString().equals("")
                                && !batchBinding.rateProduct.getText().toString().equals(".")
                                && Float.parseFloat(batchBinding.rateProduct.getText().toString())!=0.0f) {
                            if(batchQty==Integer.parseInt(batchBinding.qtyProduct.getText().toString())) {
                                float rate, gross, net, vatPer = 0, vat = 0, discount = 0, addCharges = 0;
                                int qty;
                                qty = Integer.parseInt(batchBinding.qtyProduct.getText().toString());
                                rate = Float.parseFloat(batchBinding.rateProduct.getText().toString());
                                gross = qty * rate;

                                if (!batchBinding.disProduct.getText().toString().equals("") && !batchBinding.disProduct.getText().toString().equals(".")) {
                                    discount = Float.parseFloat(batchBinding.disProduct.getText().toString());
                                }
                                if (!batchBinding.addChargesProduct.getText().toString().equals("") && !batchBinding.addChargesProduct.getText().toString().equals(".")) {
                                    addCharges = Float.parseFloat(batchBinding.addChargesProduct.getText().toString());
                                }
                                if (!batchBinding.vatPerProduct.getText().toString().equals("") && !batchBinding.vatPerProduct.getText().toString().equals(".")) {
                                    vatPer = Float.parseFloat(batchBinding.vatPerProduct.getText().toString());
                                }
                                net = gross - discount + addCharges + vat;
                                vat = ((vatPer / 100) * (gross - discount + addCharges));


                                Log.d("batchList1", batchSalesSelectedList.size()+"");
                                batchSalesSelectedList1.addAll(batchSalesSelectedList);
                                try {
                                    Log.d("Positionn",  bodyList.get(position_body_Edit).getiProduct()+" kj ");
                                }catch (Exception e){
                                    EditModeProduct =false;
                                }
                                 BatchSalesBody batchSalesBody = new BatchSalesBody(
                                        batchBinding.productName.getText().toString(),
                                        batchBinding.spinnerUnit.getSelectedItem().toString(),
                                        batchBinding.remarksProduct.getText().toString(),
                                        gross,
                                        net,
                                        rate,
                                        vat,
                                        vatPer,
                                        discount,
                                        addCharges,
                                        qty,
                                        iProduct,
                                        hashMapBody,
                                        batchSalesSelectedList1);
                                if(!EditModeProduct) {
                                    bodyList.add(batchSalesBody);
                                }else {
                                    bodyList.set(position_body_Edit,batchSalesBody);
                                }
                                binding.boyPartRV.setAdapter(bodyAdapter);
                                settingBottomBar();
                                bodyAdapter.notifyDataSetChanged();
                                initialValueSettingBody();

                                bodyAdapter.setOnClickListener(new BatchSalesBodyAdapter.OnClickListener() {
                                    @Override
                                    public void onItemClick(BatchSalesBody salesBody, int position) {
                                        bodyAdapterOnItemClick(salesBody,position);
                                    }

                                    @Override
                                    public void onDeleteClick(List<BatchSalesBody> list, int position) {
                                        bodyAdapterDelete(list,position);

                                    }


                                });


                            }else {
                                    Toast.makeText(requireActivity(), "All products are not batched", Toast.LENGTH_SHORT).show();
                                }
                        } else {
                            batchBinding.rateProduct.setError("Empty!!");
                        }
                    } else {
                        batchBinding.qtyProduct.setError("Empty!!");
                    }
                } else {
                    Toast.makeText(requireContext(), "select Unit", Toast.LENGTH_SHORT).show();
                }
            } else {
                batchBinding.productName.setError("Empty!!");
            }
        }

    private void settingBottomBar() {
        if(bodyList.size()>0){
            binding.bottomBar.setVisibility(View.VISIBLE);
            float totalNet=0.0f;

            int tQty=0;
            float gross=0.0f;
            float vat=0.0f;
            float discount=0.0f;
            float addCharges=0.0f;
            float rate=0.0f;
            for (int i=0;i<bodyList.size();i++) {
                totalNet=totalNet+bodyList.get(i).getNet();

                tQty=tQty+bodyList.get(i).getTotalQty();
                gross=gross+bodyList.get(i).getGross();
                vat=vat+bodyList.get(i).getVat();
                discount=discount+bodyList.get(i).getDiscount();
                addCharges=addCharges+bodyList.get(i).getAddCharges();
                rate=rate+bodyList.get(i).getRate();
            }
            binding.totalNet.setText("Total Net: "+totalNet);

            binding.TotalQtyBar.setText("Total Qty: "+tQty);
            binding.TotalGrossBar.setText("Total Gross: "+gross);
            binding.TotalVatBar.setText("Total Vat: "+vat);
            binding.TotalDisBar.setText("Total Discount: "+discount);
            binding.TotalAddBar.setText("Total AddCharges: "+addCharges);
            binding.TotalRateBar.setText("Total Rate: "+rate);
            Log.d("totalNet",totalNet+"");
        }else {
            binding.bottomBar.setVisibility(View.GONE);
        }
    }

    private void bodyAdapterDelete(List<BatchSalesBody> list, int position) {
        Log.d("clickedd","deleted") ;
        AlertDialog.Builder builder=new AlertDialog.Builder(requireContext());
        builder.setTitle("delete!")
                .setMessage("Do you want to delete this product ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        list.remove(position);
                        bodyAdapter.notifyDataSetChanged();
                        binding.boyPartRV.setAdapter(bodyAdapter);
                        settingBottomBar();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
    }

    private void bodyAdapterOnItemClick(BatchSalesBody salesBody, int position) {
        EditModeProduct =true;
        position_body_Edit=position;
        Toast.makeText(requireContext(), "click", Toast.LENGTH_SHORT).show();
        mainAlert();

        iProduct=salesBody.iProduct;
        batchBinding.productName.setText(salesBody.productName);
        setUnit(helper.getProductUnitById(iProduct),position);
        batchBinding.qtyProduct.setText(String.valueOf(salesBody.totalQty));
        batchBinding.rateProduct.setText(String.valueOf(salesBody.rate));
        batchBinding.vatPerProduct.setText(String.valueOf(salesBody.vatPer));
        batchBinding.disProduct.setText(String.valueOf(salesBody.discount));
        batchBinding.addChargesProduct.setText(String.valueOf(salesBody.addCharges));
        batchBinding.remarksProduct.setText(salesBody.remarks);
        batchSalesSelectedList.addAll(salesBody.batchList);
        batchBinding.batchRV.setAdapter(batchSalesSelectedAdapter);
        batchSalesSelectedAdapter.notifyDataSetChanged();

//        batchSalesSelectedListEdit.addAll(salesBody.batchList);

        batchSalesSelectedAdapter.setOnClickListener(new BatchSalesSelectedAdapter.OnClickListener() {
            @Override
            public void onItemClick(BatchSales batchSales, int position) {
                Log.d("onItemClick",batchSales.batch+"  "+batchSales.enterQty+"  "+batchSales.iId);
                batchEditPosition=position;
                loadBatchFromAPI();
            }
        });

        try {
            for (int i = 0; i < autoText_B_list.size(); i++) {
                int tagId = (int) salesBody.hashMapBody.keySet().toArray()[i];
                int tagDetails = (int) salesBody.hashMapBody.values().toArray()[i];
                Cursor cursor = helper.getTagName(tagId, tagDetails);
                if(tagDetails!=0){
                    autoText_B_list.get(i).setText(cursor.getString(cursor.getColumnIndex(TagDetails.S_NAME)));
                }else {
                    autoText_B_list.get(i).setText("");
                }
                hashMapBody.put(tagId, tagDetails);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    private void initialValueSettingBody() {
        EditModeProduct =false;
        batchSalesSelectedList.clear();
//        batchList=new ArrayList<>();
        batchSalesSelectedList1=new ArrayList<>();
//        batchList=new ArrayList<>();
        batchSalesSelectedAdapter.notifyDataSetChanged();
//        clearBatchData();
        hashMapBody=new HashMap<>();
        batchBinding.productName.setText("");
        batchBinding.qtyProduct.setText("");
        batchBinding.rateProduct.setText("");
        batchBinding.vatPerProduct.setText("");
        batchBinding.disProduct.setText("");
        batchBinding.addChargesProduct.setText("");
        batchBinding.remarksProduct.setText("");
        iProduct=0;
        for (int i=0;i<autoText_B_list.size();i++){
            autoText_B_list.get(i).setText("");
        }
        setUnit("",-1);

    }




    private void loadBatchFromAPI() {

            AlertDialog.Builder builder=new AlertDialog.Builder(requireContext());
            batchDialogueBinding = BatchSalesDialogueBinding.inflate(getLayoutInflater());
            batchDialogueBinding.recyclerViewBatch.setLayoutManager(new LinearLayoutManager(requireContext()));

            builder.setView(batchDialogueBinding.getRoot());
            alertDialog_batch=builder.create();
//            batchDialogueBinding.recyclerViewBatch.setAdapter(invoiceAdapter);

            batchSalesList=new ArrayList<>();
            batchSalesAdapter =new BatchSalesAdapter(requireContext(),batchSalesList);
            batchDialogueBinding.recyclerViewBatch.setAdapter(batchSalesAdapter);

            alertDialog_batch.show();
            API_Invoice();

//            batchSalesAdapter.setOnClickListener(new BatchSalesAdapter.OnClickListener() {
//                @Override
//                public void OnItemClick(BatchSales invoice, int position) {
//                    enableActionMode(position);
//                    selectionActive = true;
//                }
//            });
            batchDialogueBinding.apply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int enteredQty=0;
                    for (int i=0;i<batchSalesList.size();i++){
                        Log.d("batChSIze",batchSalesList.size()+"");
                        enteredQty+=batchSalesList.get(i).getEnterQty();
                    }
                    Log.d("enteredQty",enteredQty+"");
                    if(enteredQty==Integer.parseInt(batchBinding.qtyProduct.getText().toString())){
                        alertDialog_batch.dismiss();
                        loadSelectedInvoice();
                    }else if(enteredQty>Integer.parseInt(batchBinding.qtyProduct.getText().toString())){
                        Toast.makeText(requireContext(), "Exceeds Qty", Toast.LENGTH_SHORT).show();
                    }
                    else if(enteredQty<Integer.parseInt(batchBinding.qtyProduct.getText().toString())){
                        Toast.makeText(requireContext(), "All products are not batched", Toast.LENGTH_SHORT).show();
                    }

                }
            });

            batchDialogueBinding.cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog_batch.dismiss();
                }
            });

    }

    private void loadSelectedInvoice() {
//        List<Integer> listSelectedItem = batchSalesAdapter.getSelectedItems();
//        for (int i=0;i<listSelectedItem.size();i++) {
//            for (int j = 0; j < batchSalesList.size(); j++) {
//                if (listSelectedItem.get(i) == j) {
//                    BatchSales batchSales=new BatchSales(
//                            batchSalesList.get(j).getBatch(),
//                            batchSalesList.get(j).getMfDate(),
//                            batchSalesList.get(j).getExpDate(),
//                            batchSalesList.get(j).getQty(),0
//                    );
//                    Log.d("batchData", batchSalesList.get(j).getBatch());
//                    Log.d("batchData", batchSalesList.get(j).getMfDate());
//                    batchSalesSelectedList.add(batchSales);
//                    batchSalesSelectedAdapter.notifyDataSetChanged();
//
//                }
//            }
//        }

        batchSalesSelectedList.clear();
        for (int i=0;i<batchSalesList.size();i++){
            Log.d("batChSIze",batchSalesList.size()+"  "+  batchSalesList.get(i).enterQty);
            if(batchSalesList.get(i).enterQty!=0){
                batchSalesSelectedList.add(new BatchSales(
                        batchSalesList.get(i).batch,
                        batchSalesList.get(i).mfDate,
                        batchSalesList.get(i).expDate,
                        batchSalesList.get(i).enterQty,
                        batchSalesList.get(i).iId,
                        batchSalesList.get(i).iProduct)
                );

            }
        }
        batchSalesSelectedAdapter.notifyDataSetChanged();
        batchSalesSelectedAdapter.setOnClickListener(new BatchSalesSelectedAdapter.OnClickListener() {
            @Override
            public void onItemClick(BatchSales batchSales, int position) {
                Log.d("onItemClick",batchSales.batch+"  "+batchSales.enterQty+"  "+batchSales.iId);
                loadBatchFromAPI();
            }
        });

    }

//    private void enableActionMode(int position) {
//        toggleSelection(position);
//    }
//
//    private void toggleSelection(int position) {
//        batchSalesAdapter.toggleSelection(position);
//        int count = batchSalesAdapter.getSelectedItemCount();
//        if (count == 0) {
//            closeSelection();
//
//        }
//        }
//
//    private void closeSelection() {
//        batchSalesAdapter.clearSelection();
//        selectionActive = false;
//    }

    private void API_Invoice() {
        AndroidNetworking.get("http://"+ new Tools().getIP(requireActivity())+ URLs.GetBatchDetails)
                .addQueryParameter("iProduct",String.valueOf(iProduct))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("responseBatch",response.toString());
                        load_API_Batch(response);
                    }
                    @Override
                    public void onError(ANError anError) {
                        Log.d("responseBatch",anError.toString());
                    }
                });
    }

    private void load_API_Batch(JSONArray response) {
//        batchSalesList.clear();
        try {


            JSONArray jsonArray = new JSONArray(response.toString());
            Log.d("batchhhselcetededit", batchSalesSelectedList.size() + "");

            for (int i=0;i<bodyList.size();i++){
                for (int j=0;j<bodyList.get(i).batchList.size();j++){
                    Log.d("batchhhelecetedit", bodyList.get(i).batchList.get(j).batch + " "+
                            bodyList.get(i).batchList.get(j).getEnterQty());

                }
            }

            for (int j=0;j<batchSalesSelectedList.size();j++){
                Log.d("batchhhleetedit", batchSalesSelectedList.get(j).batch + " "+
                        batchSalesSelectedList.get(j).getEnterQty());

            }

            if(EditMode) {
                for (int k = 0; k < bodyListCopy.size(); k++) {
                    if (bodyListCopy.get(k).iProduct== iProduct) {
                        for (int j = 0; j < bodyListCopy.get(k).batchList.size(); j++) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                                if (bodyListCopy.get(k).batchList.get(j).batch.equals(jsonObject.getString(BatchSales.S_BATCH))) {
                                    int qty=jsonObject.getInt(BatchSales.F_QTY) +
                                            bodyListCopy.get(k).batchList.get(j).getEnterQty();
                                    jsonObject.put(BatchSales.F_QTY, qty);
                                }
                                Log.d("jsonObjectt", jsonObject.getInt(BatchSales.F_QTY) + ""+jsonObject.getString(BatchSales.S_BATCH));
                        }
                        }
                    }
                }
            }

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    boolean flag = false;
                    BatchSales batchSales = new BatchSales();
                    batchSales = new BatchSales(
                            jsonObject.getString(BatchSales.S_BATCH),
                            jsonObject.getString(BatchSales.MF_DATE),
                            jsonObject.getString(BatchSales.EXP_DATE),
                            jsonObject.getInt(BatchSales.F_QTY),
                            0,
                            jsonObject.getInt(BatchSales.I_ID),
                            jsonObject.getInt(BatchSales.I_PRODUCT)
                    );
                    if (batchSalesSelectedList.size() > 0) {
                        for (int j = 0; j < batchSalesSelectedList.size(); j++) {
                            if (jsonObject.getInt(BatchSales.F_QTY) != 0)
                                if (batchSalesSelectedList.get(j).iProduct == (jsonObject.getInt(BatchSales.I_PRODUCT))) {
                                    if (batchSalesSelectedList.get(j).batch.equals(jsonObject.getString(BatchSales.S_BATCH))) {
                                        batchSales.setEnterQty(batchSalesSelectedList.get(j).enterQty);
                                        flag = true;
                                    }
                                }
                        }
                    }
                    if (!flag) {
                        batchSales = new BatchSales(
                                jsonObject.getString(BatchSales.S_BATCH),
                                jsonObject.getString(BatchSales.MF_DATE),
                                jsonObject.getString(BatchSales.EXP_DATE),
                                jsonObject.getInt(BatchSales.F_QTY),
                                0,
                                jsonObject.getInt(BatchSales.I_ID),
                                jsonObject.getInt(BatchSales.I_PRODUCT)
                        );
                    }

                    if (bodyList.size() > 0) {
                        int balQty = jsonObject.getInt(BatchSales.F_QTY);
                        for (int k = 0; k < bodyList.size(); k++) {
                                for (int k1 = 0; k1 < bodyList.get(k).batchList.size(); k1++) {
                                    Log.d("position_body_Edit",position_body_Edit+" "+batchEditPosition);
                                            Log.d("batchhhedit"+k+"_"+k1, bodyList.get(k).batchList.get(k1).batch + " "+
                                            bodyList.get(k).batchList.get(k1).getEnterQty());
                                    if (jsonObject.getInt(BatchSales.I_PRODUCT) == (bodyList.get(k).batchList.get(k1).iProduct)) {
                                    if (jsonObject.getString(BatchSales.S_BATCH).equals(bodyList.get(k).batchList.get(k1).batch)) {
//                                        if (jsonObject.getInt(BatchSales.F_QTY) != 0) {
                                                balQty -= bodyList.get(k).batchList.get(k1).enterQty;
                                                batchSales.setQty(balQty);

//                                        }
                                    }
                                }
                            }
                        }
                        if (EditModeProduct) {
                            batchSales.setQty(batchSales.getEnterQty() + batchSales.getQty());
                        }
                    }
                    Log.d("llllll",jsonObject.getInt(BatchSales.F_QTY)+"");
                    Log.d("llllll",batchSales.getQty()+"c");



                    if (batchSales.qty != 0) {
                        batchSalesList.add(batchSales);

                    }
                    batchSalesAdapter.notifyDataSetChanged();

                }
        } catch (JSONException e) {
            e.printStackTrace();
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
                            batchBinding.productName.setText(products.getsName());
                            iProduct = products.getiId();

                            setUnit(helper.getProductUnitById(iProduct),-1);
                            batchBinding.productName.dismissDropDown();
                        }
                    });
                }
            }
        }
    }

    private void barcodeScanningChecking() {

        if(ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(requireActivity(),new String[]{Manifest.permission.CAMERA},101);
        }
        else {
            if (batchBinding.surfaceView.getVisibility() == View.VISIBLE) {
                if(cameraSource!=null){
                    cameraSource.stop();
                }
                batchBinding.surfaceView.setVisibility(View.GONE);
            }
            else if(batchBinding.surfaceView.getVisibility()==View.GONE){
                batchBinding.surfaceView.setVisibility(View.VISIBLE);
                barcodeScanning();
            }
        }
    }

    private void barcodeScanning() {
        barcodeDetector=new BarcodeDetector.Builder(requireActivity()).setBarcodeFormats(Barcode.ALL_FORMATS).build();
        cameraSource=new CameraSource.Builder(requireActivity(),barcodeDetector).setAutoFocusEnabled(true)
                .setRequestedPreviewSize(1080,1920).build();

        batchBinding.surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {

                if(ActivityCompat.checkSelfPermission(requireContext(),Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(requireActivity(),new String[]{Manifest.permission.CAMERA},101);
                }
                else {
                    try {
                        cameraSource.start(batchBinding.surfaceView.getHolder());
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
                            batchBinding.productName.dismissDropDown();

                            String productBarCode=array.valueAt(0).displayValue;
                            Cursor cursor=helper.getProductDetailsByBarcode(productBarCode);

                            if(cursor!=null && cursor.moveToFirst()){
                                batchBinding.productName.setText(cursor.getString(cursor.getColumnIndex(Products.S_NAME)));
                                iProduct=cursor.getInt(cursor.getColumnIndex(Products.I_ID));
                                setUnit(helper.getProductUnitById(iProduct),-1);
                            }
                        }
                    });
                }
            }
        });
    }

    private void setUnit(String units, int position) {
        List<String> list;
        list = Arrays.asList(units.split("\\s*,\\s*"));
        UnitAdapter unitAdapter = new UnitAdapter(list, requireActivity());
        batchBinding.spinnerUnit.setAdapter(unitAdapter);
        if (bodyList.size() > 0) {
            if (position != -1)
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).equals(bodyList.get(position).getUnit())) {
                        batchBinding.spinnerUnit.setSelection(i);
                    }
                }
        }
    }


    private void initialBatchSetting() {
        autoText_B_list=new ArrayList<>();
        mandatoryList_B =new ArrayList<>();
        hashMapBody=new HashMap<>();
        bodyListTags=new ArrayList<>();
        batchSalesSelectedList =new ArrayList<>();
        batchSalesSelectedList1=new ArrayList<>();
        batchSalesSelectedListEdit=new ArrayList<>();

        batchBinding=MainAlertSalesBatchBinding.inflate(getLayoutInflater(),null,false);
        batchSalesSelectedAdapter =new BatchSalesSelectedAdapter(requireContext(),batchSalesSelectedList);
        batchBinding.batchRV.setAdapter(batchSalesSelectedAdapter);


        for (int tagId=1;tagId<=tagTotalNumber;tagId++) {
            Cursor cursor = helper.getTransSettings(iDocType, tagId);
            if (cursor != null) {
                cursor.moveToFirst();
                String iTagPosition = cursor.getString(cursor.getColumnIndex(TransSetting.I_TAG_POSITION));
                String mandatory = cursor.getString(cursor.getColumnIndex(TransSetting.B_MANDATORY));
                String visibility = cursor.getString(cursor.getColumnIndex(TransSetting.B_VISIBLE));

                Cursor cursor1 = helper.getTagNamebyId(tagId);
                cursor1.moveToFirst();

                if(iTagPosition.equals("2")){

                    bodyListTags.add(tagId);

                    LinearLayout ll = batchBinding.linearBody;
                    // add autocompleteTextView
                    AutoCompleteTextView autoTextBody = new AutoCompleteTextView(requireActivity());
                    LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    autoTextBody.setLayoutParams(p);
                    if(visibility.equals("false")){
                        autoTextBody.setEnabled(false);
                    }
                    if(mandatory.equals("true")){
                        mandatoryList_B.add(autoTextBody);
                    }
                    autoTextBody.setLongClickable(false);
                    autoTextBody.cancelLongPress();
                    autoTextBody.setHint(cursor1.getString(cursor1.getColumnIndex(MasterSettings.S_NAME)));
//                    Log.d("hinttt",cursor1.getColumnIndex(MasterSettings.S_NAME)));
                    autoTextBody.setId(numberOfLinesB +1);
                    autoTextBody.setTag("tag"+autoTextBody.getId()+"Body");
                    ll.addView(autoTextBody);
                    numberOfLinesB++;
                    autoText_B_list.add(autoTextBody);
                    autoTextBody.addTextChangedListener(getTextWatcher(autoTextBody,tagId,"Body"));

                }

            }
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
        numberOfLinesH =0;
        numberOfLinesB =0;
        StringDate=df.format(new Date());
        binding.date.setText(StringDate);
        Cursor cursor = helper.getUserCode(userIdS);
        if (cursor!=null) {
            userCode = cursor.getString(cursor.getColumnIndex(User.USER_CODE));
        }
        Log.d("doctypeee",iDocType+"");

        if(Tools.isConnected(requireActivity())) {
            if (EditMode) {
                EditValueFromAPI();
            } else {
                AndroidNetworking.get("http://"+ new Tools().getIP(requireActivity()) + URLs.GetNextDocNo)
                        .addQueryParameter("iDocType", String.valueOf(iDocType))
                        .addQueryParameter("iUser", userIdS)
                        .setPriority(Priority.MEDIUM)
                        .build()
                        .getAsJSONArray(new JSONArrayRequestListener() {
                            @Override
                            public void onResponse(JSONArray response) {

                                Log.d("responseDocNo", response.toString());
                                try {
                                    JSONArray jsonArray = new JSONArray(response.toString());
                                    if(jsonArray.length()>0) {
                                        JSONObject jsonObject=jsonArray.getJSONObject(0);
                                        docNo=jsonObject.getString("Column1");
                                    }
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
                                NavDirections actions = SalesBatchFragmentDirections.actionSalesBatchFragmentToSalesBatchHistoryFragment(iDocType);
                                navController.navigate(actions);
                            }
                        });
            }
        }else {
//            alertDialog.dismiss();
//            Toast.makeText(requireActivity(),"Offline", Toast.LENGTH_SHORT).show();
//            if(EditMode){
//                editfromlocaldb();
//            }else {
//                Cursor cursor1=helper.getDataFrom_Batch_P_Header();
//                if(cursor1.getCount()>0) {
//                    int count= Tools.getNewDocNoLocally(cursor1);
//                    Log.d("status",count+"");
//                    docNo = "L-"+userCode + "-" + DateFormat.format("MM", new Date()) + "-"  + count;
//                }else {
//                    docNo ="L-"+ userCode + "-" + DateFormat.format("MM", new Date() )+ "-"  + 1;
//
//                }
//            }
//
//            binding.docNo.setText(docNo);
        }
    }

    private void EditValueFromAPI() {
        if(Tools.isConnected(requireContext())){
            AndroidNetworking.get("http://" + new Tools().getIP(requireActivity())+ URLs.GetTransWithBatchDetails)
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
                            Toast.makeText(requireActivity(), anError.getMessage(), Toast.LENGTH_SHORT).show();
                            NavDirections actions = PurchaseBatchFragmentDirections.actionPurchaseBatchFragmentToPurchaseBatchHistoryFragment(18);
                            navController.navigate(actions);
                        }
                    });
        }else {
            Toast.makeText(requireActivity(), "NO Internet", Toast.LENGTH_SHORT).show();
            alertDialog.dismiss();
        }
    }

    private void loadAPIValue_for_Edit(JSONObject response) {
        initialBatchSetting();
        try {
            JSONArray headArray = new JSONArray(response.getString("Table"));
            Log.d("HeadArray", headArray.length() + "");
            JSONArray productBodyArray = new JSONArray(response.getString("Table1"));
            Log.d("ProductBodyArray", productBodyArray.length() + "");
            JSONArray batchBodyArray = new JSONArray(response.getString("Table2"));
            Log.d("batchBodyArray", batchBodyArray.length() + "");

            for (int i = 0; i < headArray.length(); i++) {
                JSONObject jsonObject = headArray.getJSONObject(i);

                binding.docNo.setText(jsonObject.getString("sDocNo"));
                docNo = jsonObject.getString("sDocNo");
                binding.date.setText(jsonObject.getString("sDate"));
                iCustomer = jsonObject.getInt("iAccount1");
                binding.customer.setText(jsonObject.getString("sAccount1"));
                binding.description.setText(jsonObject.getString("sNarration"));
            }

            for (int j = 0; j < productBodyArray.length(); j++) {
                batchSalesSelectedList1.clear();
                JSONObject jsonObjectInner = productBodyArray.getJSONObject(j);

                for (int k = 0; k < headerListTags.size(); k++) {
                    hashMapHeader.put(headerListTags.get(k), jsonObjectInner.getInt("iTag" + headerListTags.get(k)));
                    autoText_H_list.get(k).setText(jsonObjectInner.getString("sTag" + headerListTags.get(k)));
                }
                for (int k = 0; k < bodyListTags.size(); k++) {
                    hashMapBody.put(bodyListTags.get(k), jsonObjectInner.getInt("iTag" + bodyListTags.get(k)));
                    Log.d("headertags", hashMapBody.toString() + " " + j);

                }
                float gross = jsonObjectInner.getInt("fQty") * Float.parseFloat(jsonObjectInner.getString("fRate"));

                BatchSalesBody bodyPart = new BatchSalesBody();
                bodyPart.setiProduct(jsonObjectInner.getInt("iProduct"));
                bodyPart.setProductName(jsonObjectInner.getString("sProduct"));
                bodyPart.setTotalQty(jsonObjectInner.getInt("fQty"));
                bodyPart.setGross(gross);
                bodyPart.setNet(Float.parseFloat(jsonObjectInner.getString("fNet")));
                bodyPart.setRate(Float.parseFloat(jsonObjectInner.getString("fRate")));
                bodyPart.setDiscount(Float.parseFloat(jsonObjectInner.getString("fDiscount")));
                bodyPart.setAddCharges(Float.parseFloat(jsonObjectInner.getString("fAddCharges")));
                bodyPart.setVatPer(Float.parseFloat(jsonObjectInner.getString("fVatPer")));
                bodyPart.setVat((float) jsonObjectInner.getInt("fVAT"));
                bodyPart.setRemarks(jsonObjectInner.getString("sRemarks"));
                bodyPart.setUnit(jsonObjectInner.getString("sUnits"));
                bodyPart.setHashMapBody(hashMapBody);
                int iTransDtIdBody=jsonObjectInner.getInt("iTransDtId");
                for (int k=0;k<batchBodyArray.length();k++){
                    JSONObject jsonObjectBatch = batchBodyArray.getJSONObject(k);
                    int iTransDtId= jsonObjectBatch.getInt("iTransDtId");
                    if(iTransDtId==iTransDtIdBody){
                        BatchSales batchPurchase=new BatchSales(
                                jsonObjectBatch.getString("sBatch"),
                                jsonObjectBatch.getString("iMFDate"),
                                jsonObjectBatch.getString("iExpDate"),
                                jsonObjectBatch.getInt("fQty"),
                                jsonObjectBatch.getInt("iId"),
                                jsonObjectBatch.getInt("iProduct")
                        );
                        batchSalesSelectedList.add(batchPurchase);
                        batchSalesSelectedAdapter.notifyDataSetChanged();
                    }
                }

                batchSalesSelectedList1.addAll(batchSalesSelectedList);
//                batchSalesSelectedListEdit.addAll(batchSalesSelectedList);
                bodyPart.setBatchList(batchSalesSelectedList1);
                Log.d("headertags", hashMapBody.toString()+ " "+j);
                bodyList.add(bodyPart);

                bodyAdapter.notifyDataSetChanged();
                hashMapBody = new HashMap<>();
                batchSalesSelectedList.clear();
                batchSalesSelectedList1=new ArrayList<>();

                if(productBodyArray.length()==j+1){
                    Log.d("bodyListsize",bodyList.size()+"");
                    bodyListCopy.addAll(bodyList);
                    settingBottomBar();
                    binding.boyPartRV.setAdapter(bodyAdapter);
                    alertDialog.dismiss();

                    bodyAdapter.setOnClickListener(new BatchSalesBodyAdapter.OnClickListener() {
                        @Override
                        public void onItemClick(BatchSalesBody batchSalesBody, int position) {
                            bodyAdapterOnItemClick(batchSalesBody,position);

                        }

                        @Override
                        public void onDeleteClick(List<BatchSalesBody> list, int position) {
                            bodyAdapterDelete(list,position);
                        }
                    });
                }


            }



        } catch (JSONException e) {
            Log.d("exception",e.getMessage());
            e.printStackTrace();
        }
    }

    private TextWatcher getTextWatcher(AutoCompleteTextView autocompleteView, int tagId, String iTagPosition) {
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
                GetTagDetails(editable.toString(),tagId,autocompleteView,iTagPosition);

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
