package com.sangsolutions.sang.Fragment.PurchaseWithBatch;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import com.sangsolutions.sang.Adapter.BatchPurchaseAdapter.BatchPurchase;
import com.sangsolutions.sang.Adapter.BatchPurchaseAdapter.BatchPurchaseAdapter;
import com.sangsolutions.sang.Adapter.BatchPurchaseBodyAdapter.BatchPurchaseBody;
import com.sangsolutions.sang.Adapter.BatchPurchaseBodyAdapter.BatchPurchaseBodyAdapter;
import com.sangsolutions.sang.Adapter.Customer.Customer;
import com.sangsolutions.sang.Adapter.Customer.CustomerAdapter;
import com.sangsolutions.sang.Adapter.MasterSettings.MasterSettings;
import com.sangsolutions.sang.Adapter.Products.Products;
import com.sangsolutions.sang.Adapter.Products.ProductsAdapter;
import com.sangsolutions.sang.Adapter.TagDetailsAdapter.TagDetails;
import com.sangsolutions.sang.Adapter.TagDetailsAdapter.TagDetailsAdapter;
import com.sangsolutions.sang.Adapter.TransSalePurchase.TransSetting;
import com.sangsolutions.sang.Adapter.UnitAdapter;
import com.sangsolutions.sang.Adapter.User;
import com.sangsolutions.sang.Database.BatchPurchaseClass;
import com.sangsolutions.sang.Database.DatabaseHelper;
import com.sangsolutions.sang.Database.Sales_purchase_Class;
import com.sangsolutions.sang.Home;
import com.sangsolutions.sang.R;
import com.sangsolutions.sang.Tools;
import com.sangsolutions.sang.URLs;
import com.sangsolutions.sang.databinding.FragmentBatchPurchaseBinding;
import com.sangsolutions.sang.databinding.MainAlertBatchBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class PurchaseBatchFragment extends Fragment {

    FragmentBatchPurchaseBinding binding;
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
    ArrayList<AutoCompleteTextView>autoText_B_list;
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

    BatchPurchaseBodyAdapter bodyAdapter;
    List<BatchPurchaseBody>bodyList;

    int iCustomer,iProduct,iTagDetail;
    MainAlertBatchBinding batchBinding;

    boolean EditBatch = false;
    int batchEditPosition;
    List<BatchPurchase>batchList;
    List<BatchPurchase>batchList1;
    BatchPurchaseAdapter batchPurchaseAdapter;

    boolean EditModeProduct =false;
    int position_body_Edit;

    Animation slideUp, slideDown;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding=FragmentBatchPurchaseBinding.inflate(getLayoutInflater());
        try {
            ((Home)getActivity()).setDrawerEnabled(false);
        }catch (Exception e){
            e.printStackTrace();
        }
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);



        assert getArguments() != null;
        iDocType = PurchaseBatchFragmentArgs.fromBundle(getArguments()).getIDocType();
        iTransId = PurchaseBatchFragmentArgs.fromBundle(getArguments()).getITransId();
        EditMode = PurchaseBatchFragmentArgs.fromBundle(getArguments()).getEditMode();
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
        bodyAdapter=new BatchPurchaseBodyAdapter(requireActivity(),bodyList,tagTotalNumber,iDocType);
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
        binding.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mainAlert();

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
                Log.d("arroww",bodyList.size()+"");

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
                        jsonObjectBatch.put("fBatchQty", bodyList.get(i).batchList.get(j).getQty());
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
            saveLocally();
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
                                if(helper.delete_Batch_P_Header(iTransId,iDocType,docNo)){
                                    if(helper.delete_Batch_P_Body(iDocType,iTransId)){
                                        if(helper.delete_Batch_P_Body_batch(iDocType,iTransId)) {
                                            Log.d("responsePostPBatch ", "successfully " + iDocType);
                                            Toast.makeText(requireActivity(), "Posted successfully", Toast.LENGTH_SHORT).show();
                                            NavDirections actions = PurchaseBatchFragmentDirections.actionPurchaseBatchFragmentToPurchaseBatchHistoryFragment(18);
                                            navController.navigate(actions);
                                        }
                                    }
                                }
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

    private void saveLocally() {
        BatchPurchaseClass batchP_class=new BatchPurchaseClass();
        Cursor cursor1=helper.getDataFrom_Batch_P_Header();
        if(!EditMode) {
            if (cursor1.moveToFirst() && cursor1.getCount() > 0) {
                iTransId = Tools.getNewDocNoLocally(cursor1);
            }
        }

        batchP_class.setiTransId(iTransId);
        batchP_class.setsDocNo(docNo);
        batchP_class.setsDate(binding.date.getText().toString());
        batchP_class.setiDocType(iDocType);
        batchP_class.setiAccount1(iCustomer);
        batchP_class.setiAccount2(0);
        batchP_class.setsNarration(binding.description.getText().toString());
        batchP_class.setProcessTime(DateFormat.format("yyyy-MM-dd HH:mm:ss", new Date())+"");
        batchP_class.setStatus(0);

        Log.d("responsePost", batchP_class.getStatus()+"");
        if(helper.delete_Batch_P_Header(iTransId,iDocType,docNo)) {
            if (helper.insert_Batch_P_Header(batchP_class)) {
                InsertBodyPart_DB();
            }
        }
    }

    private void InsertBodyPart_DB() {
        if(helper.delete_Batch_P_Body(iDocType,iTransId)) {
            for (int i = 0; i < bodyList.size(); i++) {
                BatchPurchaseClass batchP_classBody=new BatchPurchaseClass();
                for (int j = 1; j <= tagTotalNumber; j++) {
                    if (hashMapHeader.containsKey(j)) {
                        loadDataTags(batchP_classBody, j, hashMapHeader.get(j));
                    } else if (bodyList.get(i).hashMapBody.containsKey(j)) {
                        loadDataTags(batchP_classBody, j, bodyList.get(i).hashMapBody.get(j));
                    } else {
                        loadDataTags(batchP_classBody, j, 0);
                    }
                }
                batchP_classBody.setiProduct(bodyList.get(i).getiProduct());
                batchP_classBody.setTotalQty(bodyList.get(i).getTotalQty());
                batchP_classBody.setfRate(bodyList.get(i).getRate());
                batchP_classBody.setfDiscount(bodyList.get(i).getDiscount());
                batchP_classBody.setfAddCharges(bodyList.get(i).getAddCharges());
                batchP_classBody.setFvatPer(bodyList.get(i).getVatPer());
                batchP_classBody.setfVat(bodyList.get(i).getVat());
                batchP_classBody.setsRemarks(bodyList.get(i).getRemarks());
                batchP_classBody.setUnit(bodyList.get(i).getUnit());
                batchP_classBody.setNet(bodyList.get(i).getNet());
                batchP_classBody.setsDocNo(docNo);
                batchP_classBody.setiDocType(iDocType);
                batchP_classBody.setiTransId(iTransId);
                Log.d("DataBodyInsert", "deleted");
                if (helper.insert_Batch_P_Body(batchP_classBody)) {
                    Log.d("DataBodyInsert", "SUCCESS");
                    InsertBatchBodyPart_DB(bodyList.get(i).getiProduct(),bodyList.get(i).batchList);

                    if (i + 1 == bodyList.size()) {
                        Toast.makeText(requireActivity(), "Data Added Locally", Toast.LENGTH_SHORT).show();
                        NavDirections actions = PurchaseBatchFragmentDirections.actionPurchaseBatchFragmentToPurchaseBatchHistoryFragment(iDocType);
                        navController.navigate(actions);
                    }
                }

            }
        }
    }

    private void InsertBatchBodyPart_DB(int iProduct, List<BatchPurchase> batchList) {

        if(helper.delete_Batch_P_Body_batch(iDocType,iTransId)){
            for (int i = 0; i < batchList.size(); i++) {
                BatchPurchaseClass batchP_classBody=new BatchPurchaseClass();
                batchP_classBody.setBatchName(batchList.get(i).getBatch());
                batchP_classBody.setMfDate(batchList.get(i).getMfDate());
                batchP_classBody.setExpDate(batchList.get(i).getExpDate());
                batchP_classBody.setBatchQty(batchList.get(i).getQty());

                batchP_classBody.setiProduct(iProduct);
                batchP_classBody.setiDocType(iDocType);
                batchP_classBody.setiTransId(iTransId);
                batchP_classBody.setsDocNo(docNo);

                if (helper.insert_Batch_P_Body_batch(batchP_classBody)) {

                    Log.d("batch Added","SuccessFully");
                }
            }
        }
    }

    private void loadDataTags(BatchPurchaseClass b_P_class, int j, Integer iTag) {
        switch (j){
            case 1:{
                b_P_class.setiTag1(iTag);
                break;
            }
            case 2:{
                b_P_class.setiTag2(iTag);
                break;
            }
            case 3:{
                b_P_class.setiTag3(iTag);
                break;
            }
            case 4:{
                b_P_class.setiTag4(iTag);
                break;
            }
            case 5:{
                b_P_class.setiTag5(iTag);
                break;
            }
            case 6:{
                b_P_class.setiTag6(iTag);
                break;
            } case 7:{
                b_P_class.setiTag7(iTag);
                break;
            }
            case 8:{
                b_P_class.setiTag8(iTag);
                break;
            }

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


        batchBinding.qtyProduct.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if(batchList.size()==0 && !EditModeProduct){
                    batchBinding.batchQty.setText(s.toString());
                }
            }
        });




        batchBinding.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainAlertBatch.dismiss();
            }
        });

//        batchBinding.addButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(batchBinding.cardViewBatch.getVisibility()==View.GONE){
//                    batchBinding.cardViewBatch.setVisibility(View.VISIBLE);
//                }
//                else {
//                    batchBinding.cardViewBatch.setVisibility(View.GONE);
//                }
//            }
//        });

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

        batchBinding.mfDate.setText(StringDate);
        batchBinding.expDate.setText(StringDate);
        batchBinding.mfDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicking(batchBinding.mfDate);
            }
        });
        batchBinding.expDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicking(batchBinding.expDate);
            }
        });

        batchBinding.saveBatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!batchBinding.batch.getText().toString().equals("")){
                    if(!batchBinding.batchQty.getText().toString().equals("")){
                        try {
                            Log.d("Positionn",  batchList.get(batchEditPosition).getBatch()+" kj ");
                        }catch (Exception e){
                            EditBatch=false;
                        }

                        if(batchList.size()>0){
                            int totalBatchQty=0;
                                for (int i = 0; i< batchList.size(); i++){
                                totalBatchQty=totalBatchQty+ batchList.get(i).getQty();
                                }
                                int remainQty=Integer.parseInt(batchBinding.qtyProduct.getText().toString())-
                                    totalBatchQty;
                                if(EditBatch){
                                    Log.d("remainQty",remainQty+" "+batchList.get(batchEditPosition).getQty());
                                    remainQty=remainQty+ batchList.get(batchEditPosition).getQty();
                                }
                                if(Integer.parseInt(batchBinding.batchQty.getText().toString())<=remainQty  && remainQty>0){
                                addBatchToList();
                                }else {
                                    batchBinding.batchQty.setError("Should not be greater than Total Qty!! max("+remainQty+")");
                                }
                        }
                        else if(!batchBinding.qtyProduct.getText().toString().equals("")){
                            if(Integer.parseInt(batchBinding.qtyProduct.getText().toString())>=
                                Integer.parseInt(batchBinding.batchQty.getText().toString())) {
                                addBatchToList();
                            }else {batchBinding.batchQty.setError("Should not be greater than Total Qty!!");}
                        }else {batchBinding.qtyProduct.setError("Empty!!");
                            Toast.makeText(requireContext(), "Enter Total Qty", Toast.LENGTH_SHORT).show();
                        }
                    }else {batchBinding.batchQty.setError("Empty");}
                }else {batchBinding.batch.setError("Empty");}
            }
        });

        batchBinding.addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(batchList.size()>0) {
                    saveProduct();
                }else {
                    Toast.makeText(requireContext(), "Batch fields are empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void initialBatchSetting() {
        autoText_B_list=new ArrayList<>();
        mandatoryList_B =new ArrayList<>();
        hashMapBody=new HashMap<>();
        bodyListTags=new ArrayList<>();
        batchList =new ArrayList<>();
        batchList1=new ArrayList<>();
        batchPurchaseAdapter=new BatchPurchaseAdapter(requireActivity(), batchList);
        batchBinding=MainAlertBatchBinding.inflate(getLayoutInflater(),null,false);


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

    private void addBatchToList() {
        if(EditBatch){
            batchList.set(batchEditPosition,new BatchPurchase(batchBinding.batch.getText().toString(),
                    batchBinding.mfDate.getText().toString(),
                    batchBinding.expDate.getText().toString(),
                    Integer.parseInt(batchBinding.batchQty.getText().toString())));
        }else {
            batchList.add(new BatchPurchase(batchBinding.batch.getText().toString(),
                    batchBinding.mfDate.getText().toString(),
                    batchBinding.expDate.getText().toString(),
                    Integer.parseInt(batchBinding.batchQty.getText().toString())));
        }
        batchBinding.batchRV.setAdapter(batchPurchaseAdapter);
        Log.d("batchList", batchList.size()+"");
        batchPurchaseAdapter.notifyDataSetChanged();
        clearBatchData();
        batchPurchaseAdapter.setOnClickListener(new BatchPurchaseAdapter.OnClickListener() {
            @Override
            public void onItemClick(BatchPurchase batchPurchase, int position) {

                batchOnItemClick(batchPurchase,position);


            }

        });
    }

    private void batchOnItemClick(BatchPurchase batchPurchase, int position) {
        EditBatch=true;
        batchBinding.batch.setText(batchPurchase.getBatch());
        batchBinding.batchQty.setText(String.valueOf(batchPurchase.getQty()));
        batchBinding.mfDate.setText(batchPurchase.getMfDate());
        batchBinding.expDate.setText(batchPurchase.getExpDate());
        batchEditPosition=position;
        batchBinding.cardViewBatch.setVisibility(View.VISIBLE);

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

        batchList1.clear();
        int batchQty=0;
        for (int i = 0; i< batchList.size(); i++){
           batchQty=batchQty+ batchList.get(i).getQty();
        }
        if(batchQty==Integer.parseInt(batchBinding.qtyProduct.getText().toString())) {
            DecimalFormat df = new DecimalFormat("#.00");
            if (!batchBinding.productName.getText().toString().equals("") && helper.getProductNameValid(batchBinding.productName.getText().toString().trim())) {
                if (!batchBinding.spinnerUnit.getSelectedItem().toString().equals("")) {
                    if (!batchBinding.qtyProduct.getText().toString().equals("")) {
                        if (!batchBinding.rateProduct.getText().toString().equals("")
                                && !batchBinding.rateProduct.getText().toString().equals(".")) {
//                            for (int i = 0; i < batchList.size(); i++) {
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

                            Log.d("batchList1", batchList.size()+"");
                            batchList1.addAll(batchList);
                            try {
                                Log.d("Positionn",  bodyList.get(position_body_Edit).getiProduct()+" kj ");
                            }catch (Exception e){
                                EditModeProduct =false;
                            }
                                BatchPurchaseBody batchPurchaseBody = new BatchPurchaseBody(
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
                                        batchList1);
                                if(!EditModeProduct) {
                                    bodyList.add(batchPurchaseBody);
                                }else {
                                    bodyList.set(position_body_Edit,batchPurchaseBody);
                                }
                                binding.boyPartRV.setAdapter(bodyAdapter);
                                settingBottomBar();
                                bodyAdapter.notifyDataSetChanged();

                            initialValueSettingBody();

                            bodyAdapter.setOnClickListener(new BatchPurchaseBodyAdapter.OnClickListener() {
                                @Override
                                public void onItemClick(BatchPurchaseBody purchaseBody, int position) {

                                    bodyAdapterOnItemClick(purchaseBody,position);

                                }

                                @Override
                                public void onDeleteClick(List<BatchPurchaseBody> list, int position) {
                                    bodyAdapterDelete(list,position);
                                }
                            });



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
        }else {
            Toast.makeText(requireActivity(), "All products are not batched", Toast.LENGTH_SHORT).show();
        }
    }

    private void bodyAdapterDelete(List<BatchPurchaseBody> list, int position) {
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

    private void bodyAdapterOnItemClick(BatchPurchaseBody purchaseBody, int position) {
        EditModeProduct =true;
        position_body_Edit=position;
        Toast.makeText(requireContext(), "click", Toast.LENGTH_SHORT).show();
        mainAlert();
        iProduct=purchaseBody.iProduct;
        batchBinding.productName.setText(purchaseBody.productName);
        setUnit(helper.getProductUnitById(iProduct),position);
        batchBinding.qtyProduct.setText(String.valueOf(purchaseBody.totalQty));
        batchBinding.rateProduct.setText(String.valueOf(purchaseBody.rate));
        batchBinding.vatPerProduct.setText(String.valueOf(purchaseBody.vatPer));
        batchBinding.disProduct.setText(String.valueOf(purchaseBody.discount));
        batchBinding.addChargesProduct.setText(String.valueOf(purchaseBody.addCharges));
        batchBinding.remarksProduct.setText(purchaseBody.remarks);
        batchList.addAll(purchaseBody.batchList);
        batchBinding.batchRV.setAdapter(batchPurchaseAdapter);
        batchPurchaseAdapter.notifyDataSetChanged();

        batchPurchaseAdapter.setOnClickListener(new BatchPurchaseAdapter.OnClickListener() {
            @Override
            public void onItemClick(BatchPurchase batchPurchase, int position) {
                batchOnItemClick(batchPurchase,position);
            }
        });

        try {
            for (int i = 0; i < autoText_B_list.size(); i++) {
                int tagId = (int) purchaseBody.hashMapBody.keySet().toArray()[i];
                int tagDetails = (int) purchaseBody.hashMapBody.values().toArray()[i];
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
        batchList.clear();
//        batchList=new ArrayList<>();
        batchList1=new ArrayList<>();
//        batchList=new ArrayList<>();
        batchPurchaseAdapter.notifyDataSetChanged();
        clearBatchData();
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

    private void clearBatchData() {
        EditBatch=false;
        batchBinding.batchQty.setText("");
        batchBinding.batch.setText("");
        batchBinding.expDate.setText(df.format(new Date()));
        batchBinding.mfDate.setText(df.format(new Date()));
//        batchBinding.cardViewBatch.setVisibility(View.GONE);
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
                                NavDirections actions = PurchaseBatchFragmentDirections.actionPurchaseBatchFragmentToPurchaseBatchHistoryFragment(iDocType);
                                navController.navigate(actions);
                            }
                        });
            }
        }else {
            alertDialog.dismiss();
            Toast.makeText(requireActivity(),"Offline", Toast.LENGTH_SHORT).show();
            if(EditMode){
                editfromlocaldb();
            }else {
                Cursor cursor1=helper.getDataFrom_Batch_P_Header();
                if(cursor1.getCount()>0) {
                    int count= Tools.getNewDocNoLocally(cursor1);
                    Log.d("status",count+"");
                    docNo = "L-"+userCode + "-" + DateFormat.format("MM", new Date()) + "-"  + count;
                }else {
                    docNo ="L-"+ userCode + "-" + DateFormat.format("MM", new Date() )+ "-"  + 1;

                }
            }

            binding.docNo.setText(docNo);
        }

    }

    private void editfromlocaldb() {

        initialBatchSetting();
        Cursor cursorEdit_H = helper.getEditValuesHeaderBatchP(iTransId, iDocType);
        if (cursorEdit_H.moveToFirst() && cursorEdit_H.getCount() > 0) {
            iTransId = cursorEdit_H.getInt(cursorEdit_H.getColumnIndex(BatchPurchaseClass.I_TRANS_ID));
            docNo = cursorEdit_H.getString(cursorEdit_H.getColumnIndex(BatchPurchaseClass.S_DOC_NO));
            binding.docNo.setText(docNo);
            binding.date.setText(cursorEdit_H.getString(cursorEdit_H.getColumnIndex(BatchPurchaseClass.S_DATE)));
            binding.description.setText(cursorEdit_H.getString(cursorEdit_H.getColumnIndex(BatchPurchaseClass.S_NARRATION)));
            iCustomer = cursorEdit_H.getInt(cursorEdit_H.getColumnIndex(BatchPurchaseClass.I_ACCOUNT_1));
            binding.customer.setText(helper.getCustomerUsingId(iCustomer));
            changeStatus(iTransId, docNo, 1);
        }
        Cursor cursorEdit_B = helper.getEditValuesBody_BatchP(iTransId, iDocType);
        Log.d("cursorEdit_B", cursorEdit_B.getCount() + "");

        if (cursorEdit_B.moveToFirst() && cursorEdit_B.getCount() > 0) {
            for (int i = 0; i < cursorEdit_B.getCount(); i++) {
                batchList1.clear();
                for (int k = 0; k < headerListTags.size(); k++) {
                    int tagDetails = cursorEdit_B.getInt(cursorEdit_B.getColumnIndex("iTag" + headerListTags.get(k)));
                    hashMapHeader.put(headerListTags.get(k), tagDetails);
                    Cursor tagNameCursor = helper.getTagName(headerListTags.get(k), tagDetails);
                    if (tagDetails != 0) {
                        autoText_H_list.get(k).setText(tagNameCursor.getString(tagNameCursor.getColumnIndex(TagDetails.S_NAME)));

                    } else {
                        autoText_H_list.get(k).setText("");
                    }
                }
                for (int k = 0; k < bodyListTags.size(); k++) {
                    hashMapBody.put(bodyListTags.get(k), cursorEdit_B.getInt(cursorEdit_B.getColumnIndex("iTag" + bodyListTags.get(k))));
                }

                float gross = cursorEdit_B.getInt(cursorEdit_B.getColumnIndex(BatchPurchaseClass.F_QTY)) * cursorEdit_B.getFloat(cursorEdit_B.getColumnIndex(Sales_purchase_Class.F_RATE));

                BatchPurchaseBody bodyPart = new BatchPurchaseBody();
                bodyPart.setiProduct(cursorEdit_B.getInt(cursorEdit_B.getColumnIndex(BatchPurchaseClass.I_PRODUCT)));

                String productName = helper.getProductNameById(cursorEdit_B.getInt(cursorEdit_B.getColumnIndex(BatchPurchaseClass.I_PRODUCT)));

                bodyPart.setProductName(productName);
                bodyPart.setTotalQty(cursorEdit_B.getInt(cursorEdit_B.getColumnIndex(BatchPurchaseClass.F_QTY)));
                bodyPart.setGross(gross);
                bodyPart.setNet(cursorEdit_B.getFloat(cursorEdit_B.getColumnIndex(BatchPurchaseClass.F_NET)));
                bodyPart.setRate(cursorEdit_B.getFloat(cursorEdit_B.getColumnIndex(BatchPurchaseClass.F_RATE)));
                bodyPart.setDiscount(cursorEdit_B.getFloat(cursorEdit_B.getColumnIndex(BatchPurchaseClass.F_DISCOUNT)));
                bodyPart.setAddCharges(cursorEdit_B.getFloat(cursorEdit_B.getColumnIndex(BatchPurchaseClass.F_ADD_CHARGES)));
                bodyPart.setVatPer(cursorEdit_B.getFloat(cursorEdit_B.getColumnIndex(BatchPurchaseClass.F_VAT_PER)));
                bodyPart.setVat(cursorEdit_B.getFloat(cursorEdit_B.getColumnIndex(BatchPurchaseClass.F_VAT)));
                bodyPart.setRemarks(cursorEdit_B.getString(cursorEdit_B.getColumnIndex(BatchPurchaseClass.S_REMARKS)));
                bodyPart.setUnit(cursorEdit_B.getString(cursorEdit_B.getColumnIndex(BatchPurchaseClass.S_UNITS)));
                bodyPart.setHashMapBody(hashMapBody);

                Cursor cursorBatchBody = helper.getDataFrom_Batch_P_Batch(iTransId, iDocType);
                if (cursorBatchBody.moveToFirst() && cursorBatchBody.getCount() > 0) {
                    for (int k = 0; k < cursorBatchBody.getCount(); k++) {
                        int iProduct = cursorBatchBody.getInt(cursorBatchBody.getColumnIndex(BatchPurchaseClass.I_PRODUCT));
                        if (iProduct == cursorEdit_B.getInt(cursorEdit_B.getColumnIndex(BatchPurchaseClass.I_PRODUCT))) {
                            BatchPurchase batchPurchase = new BatchPurchase(
                                    cursorBatchBody.getString(cursorBatchBody.getColumnIndex(BatchPurchaseClass.BATCH_NAME)),
                                    cursorBatchBody.getString(cursorBatchBody.getColumnIndex(BatchPurchaseClass.MF_DATE)),
                                    cursorBatchBody.getString(cursorBatchBody.getColumnIndex(BatchPurchaseClass.EXP_DATE)),
                                    cursorBatchBody.getInt(cursorBatchBody.getColumnIndex(BatchPurchaseClass.BATCH_QTY))
                            );
                            batchList.add(batchPurchase);
                            batchPurchaseAdapter.notifyDataSetChanged();
                        }
                    }
                }


                batchList1.addAll(batchList);
                bodyPart.setBatchList(batchList1);
                Log.d("headertags", hashMapBody.toString()+ " "+i);
                bodyList.add(bodyPart);
                bodyAdapter.notifyDataSetChanged();
                hashMapBody = new HashMap<>();
                batchList.clear();
                batchList1=new ArrayList<>();


                if (cursorEdit_B.getCount() == i + 1) {
                    binding.boyPartRV.setAdapter(bodyAdapter);
                    settingBottomBar();
                    alertDialog.dismiss();
                    bodyAdapter.setOnClickListener(new BatchPurchaseBodyAdapter.OnClickListener() {
                        @Override
                        public void onItemClick(BatchPurchaseBody purchaseBody, int position) {
                            bodyAdapterOnItemClick(purchaseBody,position);
                        }

                        @Override
                        public void onDeleteClick(List<BatchPurchaseBody> list, int position) {
                            bodyAdapterDelete(list,position);
                        }
                    });
                }
                cursorEdit_B.moveToNext();
            }
        }
    }



    private void changeStatus(int transId, String docNo, int iStatus) {
        if(helper.changeStatus_Batch_P(transId,docNo,iStatus)){
            Log.d("statusChange","successfully");
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
        }    }

    private void loadAPIValue_for_Edit(JSONObject response) {
//        batchList=new ArrayList<>();
//        batchList1=new ArrayList<>();
//        mainAlert();
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
                batchList1.clear();
                JSONObject jsonObjectInner = productBodyArray.getJSONObject(j);

                for (int k = 0; k < headerListTags.size(); k++) {
                    hashMapHeader.put(headerListTags.get(k), jsonObjectInner.getInt("iTag" + headerListTags.get(k)));
                    autoText_H_list.get(k).setText(jsonObjectInner.getString("sTag" + headerListTags.get(k)));
                }
                for (int k = 0; k < bodyListTags.size(); k++) {
                    hashMapBody.put(bodyListTags.get(k), jsonObjectInner.getInt("iTag" + bodyListTags.get(k)));
                    Log.d("headertags", hashMapBody.toString()+ " "+j);

                }
                float gross = jsonObjectInner.getInt("fQty") * Float.parseFloat(jsonObjectInner.getString("fRate"));

            BatchPurchaseBody bodyPart = new BatchPurchaseBody();
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

            for (int k=0;k<batchBodyArray.length();k++){
                JSONObject jsonObjectBatch = batchBodyArray.getJSONObject(k);
                int iProduct= jsonObjectBatch.getInt("iProduct");
                if(iProduct==jsonObjectInner.getInt("iProduct")){
                    BatchPurchase batchPurchase=new BatchPurchase(
                            jsonObjectBatch.getString("sBatch"),
                            jsonObjectBatch.getString("iMFDate"),
                            jsonObjectBatch.getString("iExpDate"),
                            jsonObjectBatch.getInt("fQty")
                    );
                    batchList.add(batchPurchase);
                    batchPurchaseAdapter.notifyDataSetChanged();
                }
            }
            batchList1.addAll(batchList);
            bodyPart.setBatchList(batchList1);
            Log.d("headertags", hashMapBody.toString()+ " "+j);
            bodyList.add(bodyPart);
            bodyAdapter.notifyDataSetChanged();
            hashMapBody = new HashMap<>();
            batchList.clear();
            batchList1=new ArrayList<>();

                if(productBodyArray.length()==j+1){
                    Log.d("bodyListsize",bodyList.size()+"");
                    settingBottomBar();
                    binding.boyPartRV.setAdapter(bodyAdapter);
                    alertDialog.dismiss();
                    bodyAdapter.setOnClickListener(new BatchPurchaseBodyAdapter.OnClickListener() {
                        @Override
                        public void onItemClick(BatchPurchaseBody purchaseBody, int position) {
                            bodyAdapterOnItemClick(purchaseBody,position);
                        }

                        @Override
                        public void onDeleteClick(List<BatchPurchaseBody> list, int position) {
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
