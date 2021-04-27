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
import com.sangsolutions.sang.Adapter.MasterSettings.MasterSettings;
import com.sangsolutions.sang.Adapter.Products.Products;
import com.sangsolutions.sang.Adapter.Products.ProductsAdapter;
import com.sangsolutions.sang.Adapter.RequestBodyAdapter.RequestBodyAdapter;
import com.sangsolutions.sang.Adapter.RequestHistoryAdapter.RequestClass;
import com.sangsolutions.sang.Adapter.TagDetailsAdapter.TagDetails;
import com.sangsolutions.sang.Adapter.TagDetailsAdapter.TagDetailsAdapter;
import com.sangsolutions.sang.Adapter.TransSalePurchase.TransSetting;
import com.sangsolutions.sang.Adapter.UnitAdapter;
import com.sangsolutions.sang.Adapter.User;
import com.sangsolutions.sang.Database.DatabaseHelper;
import com.sangsolutions.sang.Database.RequestEnquiryClass;
import com.sangsolutions.sang.Home;
import com.sangsolutions.sang.R;
import com.sangsolutions.sang.Tools;
import com.sangsolutions.sang.URLs;
import com.sangsolutions.sang.databinding.FragmentRequestBinding;

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

public class RequestFragment extends Fragment {
    FragmentRequestBinding binding;

    int iTransId,iDocType;
    boolean EditMode;
    String docNo;
    String StringDate;
    SimpleDateFormat df;
    DatabaseHelper helper;


    List<TagDetails> tagList;
    TagDetailsAdapter tagDetailsAdapter;

    List<Products>productsList;
    ProductsAdapter productsAdapter;

    CameraSource cameraSource;
    BarcodeDetector barcodeDetector;

    String productBarCode;

    int iProduct,iTagDetail;

    HashMap<Integer, Integer> hashMapBody;
    HashMap<Integer, Integer> hashMapHeader;

    List<RequestClass>bodyPartList;
    RequestBodyAdapter bodyPartAdapter;
    NavController navController;

    int numberOfLinesH;
    int numberOfLinesB;
    ArrayList<AutoCompleteTextView> autoText_B_list;
    ArrayList<AutoCompleteTextView>autoText_H_list;

    ArrayList<AutoCompleteTextView> mandatoryList_H;
    ArrayList<AutoCompleteTextView> mandatoryList_B;

    AlertDialog alertDialog;


    int position_body_Edit;
    boolean editModeProduct;

    Cursor cursorTagNumber;
    int tagTotalNumber;

    String toolTitle;
    String userIdS = null;

    List<Integer> headerListTags;
    List<Integer>bodyListTags;
    String userCode;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       binding=FragmentRequestBinding.inflate(getLayoutInflater());
        try {
            ((Home)getActivity()).setDrawerEnabled(false);
        }catch (Exception e){
            e.printStackTrace();
        }

        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);

        hashMapBody=new HashMap<>();
        hashMapHeader=new HashMap<>();

        headerListTags =new ArrayList<>();
        bodyListTags =new ArrayList<>();

        assert getArguments() != null;
        iDocType = Sale_Purchase_FragmentArgs.fromBundle(getArguments()).getIDocType();
        iTransId = RequestFragmentArgs.fromBundle(getArguments()).getITransId();
        EditMode = RequestFragmentArgs.fromBundle(getArguments()).getEditMode();

        Log.d("llllll","iDocType "+iDocType+" "+"iTransId "+iTransId+" "+"editMode "+ EditMode +"");

        df = new SimpleDateFormat("dd-MM-yyyy");
        helper=new DatabaseHelper(requireActivity());
        AlertDialog.Builder builder=new AlertDialog.Builder(requireActivity());
        View view=LayoutInflater.from(requireActivity()).inflate(R.layout.progress_bar,null,false);
        builder.setView(view);
        builder.setCancelable(false);
        alertDialog = builder.create();

        autoText_B_list=new ArrayList<>();
        autoText_H_list=new ArrayList<>();
        mandatoryList_H =new ArrayList<>();
        mandatoryList_B =new ArrayList<>();

        tagList =new ArrayList<>();
        tagDetailsAdapter =new TagDetailsAdapter(requireActivity(),tagList);

        productsList=new ArrayList<>();
        productsAdapter=new ProductsAdapter(requireActivity(),productsList);

        Cursor cursor_userId=helper.getUserId();
        if(cursor_userId!=null &&cursor_userId.moveToFirst()) {
            userIdS = cursor_userId.getString(cursor_userId.getColumnIndex("user_Id"));
        }

        cursorTagNumber =helper.getTotalTagNumber();
        if(cursorTagNumber!=null) {
            tagTotalNumber = cursorTagNumber.getCount();
        }

        bodyPartList=new ArrayList<>();
        bodyPartAdapter=new RequestBodyAdapter(requireActivity(),bodyPartList,tagTotalNumber,iDocType);
        binding.boyPartRV.setLayoutManager(new LinearLayoutManager(requireActivity()));



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
                if(binding.cardViewBody.getVisibility()==View.GONE){
                    binding.cardViewBody.setVisibility(View.VISIBLE);
                }
                else {
                    binding.cardViewBody.setVisibility(View.GONE);
                }

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
                if(iTagPosition.equals("1")){

                    headerListTags.add(tagId);
                    LinearLayout ll = binding.linearHeader;
                    // add autocompleteTextView
                    AutoCompleteTextView autoTextHeader = new AutoCompleteTextView(requireActivity());
                    LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    autoTextHeader.setLayoutParams(p);
                    if(visibility.equals("false")){
                        autoTextHeader.setEnabled(false);
                    }
                    if(mandatory.equals("true")){
                        mandatoryList_H.add(autoTextHeader);
                    }
                    autoTextHeader.setLongClickable(false);
                    autoTextHeader.cancelLongPress();
                    autoTextHeader.setHint(cursor1.getString(cursor1.getColumnIndex(MasterSettings.S_NAME)));
                    autoTextHeader.setId(numberOfLinesH +1);
                    autoTextHeader.setTag("tag"+autoTextHeader.getId()+"Header");
                    ll.addView(autoTextHeader);
                    numberOfLinesH++;
                    autoText_H_list.add(autoTextHeader);
                    autoTextHeader.addTextChangedListener(getTextWatcher(autoTextHeader,tagId,"Header"));


                }else if(iTagPosition.equals("2")){

                    bodyListTags.add(tagId);

                    LinearLayout ll = binding.linearBody;
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
//                    textView.setAllCaps(true);
//                    textView.setPadding(5,0,5,0);
                }
            }
        }

        initialValueSettingHeader();

        binding.barcodeI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                barcodeScanningChecking();
            }
        });

        binding.productName.addTextChangedListener(new TextWatcher() {
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

        binding.productName.setThreshold(1);
        binding.productName.setAdapter(productsAdapter);
        if (binding.surfaceView.getVisibility() == View.VISIBLE) {
            binding.productName.dismissDropDown();
        }

        binding.saveProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        });

        binding.saveMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(requireActivity());
                builder.setTitle("save!")
                        .setMessage("Do you want to save ?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                boolean flag = true;

                                if (bodyPartList.size() > 0) {
                                        if (mandatoryList_H.size() <= 0) {
                                            saveMain();
                                        }

                                        for (int i = 0; i < mandatoryList_H.size(); i++) {
                                            if (!mandatoryList_H.get(i).getText().toString().equals("")
                                                    && helper.isTagValid(mandatoryList_H.get(i).getText().toString().trim())) {
                                                if (i + 1 == mandatoryList_H.size() && flag) {
                                                    for(int j=0;j<headerListTags.size();j++){
                                                        if(autoText_H_list.get(j).getText().toString().equals("")){
                                                            hashMapHeader.remove(headerListTags.get(j));
                                                        }
                                                    }
                                                    saveMain();
                                                }
                                            } else {
                                                Toast.makeText(requireContext(), "Mandatory fields are not filled", Toast.LENGTH_SHORT).show();
                                                mandatoryList_H.get(i).setError("Mandatory");
                                                flag = false;
                                            }
                                        }

                                }
                                else {
                                    Toast.makeText(requireContext(), "No products to add", Toast.LENGTH_SHORT).show();
                                }

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

        binding.deleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(EditMode) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                    builder.setTitle("delete!")
                            .setMessage("Do you want to delete ?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if(Tools.isConnected(requireContext())) {
                                        deleteAllFromAPI();
                                    }else {
                                        deleteAllFromDB();
                                    }

                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).create().show();
                }else {
                    Toast.makeText(requireActivity(), "Only existing document can delete", Toast.LENGTH_SHORT).show();
                }


            }
        });
        return binding.getRoot();
    }

    private void deleteAllFromDB() {
        if(helper.deleteRequestEnquiry_Header(iTransId,iDocType,docNo)){
            if(helper.deleteRequestEnquiry_Body(iDocType,iTransId)){
                Log.d("responsePost ", "successfully");
                NavDirections actions = RequestFragmentDirections
                        .actionRequestFragmentToRequestHistoryFragment(iDocType,toolTitle);
                navController.navigate(actions);
                Toast.makeText(requireContext(), "Deleted", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void deleteAllFromAPI() {

        AndroidNetworking.get("http://"+ new Tools().getIP(requireActivity())+  URLs.DeleteTransRequest)
                .addQueryParameter("iTransId", String.valueOf(iTransId))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("response_delete",response);
                        NavDirections actions = RequestFragmentDirections
                                .actionRequestFragmentToRequestHistoryFragment(iDocType,toolTitle);
                        navController.navigate(actions);
                        Toast.makeText(requireContext(), "Deleted", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("response_delete",anError.toString()+ anError.getErrorDetail()+anError.getErrorBody());

                    }
                });
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
                jsonObjectMain.put("sNarration", binding.description.getText().toString());
                assert userIdS != null;
                jsonObjectMain.put("iUser", Integer.parseInt(userIdS));

                Log.d("jsonObjecMain", jsonObjectMain.get("iTransId") + "");
                Log.d("jsonObjecMain", jsonObjectMain.get("sDocNo") + "");
                Log.d("jsonObjecMain", jsonObjectMain.get("sNarration") + "");
                Log.d("jsonObjecMain", jsonObjectMain.get("iUser") + "");


                JSONArray jsonArray = new JSONArray();
                for (int i = 0; i < bodyPartList.size(); i++) {
                    JSONObject jsonObject = new JSONObject();


                    Log.d("bodyPartList size", bodyPartList.size() + "" + i);

                    for (int j = 1; j <= tagTotalNumber; j++) {
                        if (hashMapHeader.containsKey(j)) {
                            jsonObject.put("iTag" + j, hashMapHeader.get(j));
                        } else if (bodyPartList.get(i).hashMapBody.containsKey(j)) {
                            jsonObject.put("iTag" + j, bodyPartList.get(i).hashMapBody.get(j));
                        } else {
                            jsonObject.put("iTag" + j, 0);
                        }


                    }

                    jsonObject.put("iProduct", bodyPartList.get(i).getiProduct());
                    jsonObject.put("fQty", bodyPartList.get(i).getQty());
                    if (bodyPartList.get(i).getRemarks().equals("")) {
                        jsonObject.put("sRemarks", "");
                    } else {
                        jsonObject.put("sRemarks", bodyPartList.get(i).getRemarks());
                    }
                    jsonObject.put("sUnits", bodyPartList.get(i).getUnit());


                    Log.d("jsonObjecttIproduct", jsonObject.get("iProduct") + "");
                    Log.d("jsonObjecttQty", jsonObject.get("fQty") + "");
                    Log.d("jsonObjecttrema", jsonObject.get("sRemarks") + "");


                    jsonArray.put(jsonObject);
                }
                jsonObjectMain.put("Body", jsonArray);

                uploadToAPI(jsonObjectMain);


            } catch (JSONException e) {
                Log.d("exception", e.getMessage());
                e.printStackTrace();
            }
        }else {
            saveLocally();
        }

    }

    private void saveLocally() {
        RequestEnquiryClass re_class=new RequestEnquiryClass();
        Cursor cursor1=helper.getDataFromRequestEnquiry_Header();
        if(!EditMode) {
            if (cursor1.moveToFirst() && cursor1.getCount() > 0) {
                iTransId = Tools.getNewDocNoLocally(cursor1);
            }
        }
        re_class.setiTransId(iTransId);
        re_class.setsDocNo(docNo);
        re_class.setsDate(binding.date.getText().toString());
        re_class.setiDocType(iDocType);
        re_class.setsNarration(binding.description.getText().toString());
        re_class.setProcessTime(DateFormat.format("yyyy-MM-dd HH:mm:ss", new Date())+"");
        re_class.setStatus(0);
        Log.d("responsePost", re_class.getStatus()+"");
        if(helper.deleteRequestEnquiry_Header(iTransId,iDocType,docNo)) {
            if (helper.insertRequestEnquiry_Header(re_class)) {
                InsertBodyPart_DB();
            }

        }


    }

    private void InsertBodyPart_DB() {
        if(helper.deleteRequestEnquiry_Body(iDocType,iTransId)) {

            for (int i = 0; i < bodyPartList.size(); i++) {
                RequestEnquiryClass reClass = new RequestEnquiryClass();
                for (int j = 1; j <= tagTotalNumber; j++) {
                    if (hashMapHeader.containsKey(j)) {
                        loadDataTags(reClass, j, hashMapHeader.get(j));
                    } else if (bodyPartList.get(i).hashMapBody.containsKey(j)) {
                        loadDataTags(reClass, j, bodyPartList.get(i).hashMapBody.get(j));
                    } else {
                        loadDataTags(reClass, j, 0);
                    }
                }

                reClass.setiProduct(bodyPartList.get(i).getiProduct());
                reClass.setFqty(bodyPartList.get(i).getQty());
                reClass.setsRemarks(bodyPartList.get(i).getRemarks());
                reClass.setUnit(bodyPartList.get(i).getUnit());
                reClass.setsDocNo(docNo);
                reClass.setiDocType(iDocType);
                reClass.setiTransId(iTransId);
                Log.d("DataBodyInsert", "deleted");
                if (helper.insertRequestEnquiry_Body(reClass)) {
                    Log.d("DataBodyInsert", "SUCCESS");

                    if (i + 1 == bodyPartList.size()) {
                        Toast.makeText(requireActivity(), "Data Added Locally", Toast.LENGTH_SHORT).show();
                        NavDirections actions = RequestFragmentDirections
                                .actionRequestFragmentToRequestHistoryFragment(iDocType,toolTitle);
                        navController.navigate(actions);
                    }
                }
            }
        }
    }

    private void loadDataTags(RequestEnquiryClass re_class, int j, Integer iTag) {
        switch (j){
            case 1:{
                re_class.setiTag1(iTag);
                break;
            }
            case 2:{
                re_class.setiTag2(iTag);
                break;
            }
            case 3:{
                re_class.setiTag3(iTag);
                break;
            }
            case 4:{
                re_class.setiTag4(iTag);
                break;
            }
            case 5:{
                re_class.setiTag5(iTag);
                break;
            }
            case 6:{
                re_class.setiTag6(iTag);
                break;
            } case 7:{
                re_class.setiTag7(iTag);
                break;
            }
            case 8:{
                re_class.setiTag8(iTag);
                break;
            }

        }
    }

    private void uploadToAPI(JSONObject jsonObjectMain) {

        Log.d("uploadjSONoBJECT",jsonObjectMain.toString());
        if(Tools.isConnected(requireActivity())) {
            alertDialog.show();
            AndroidNetworking.post("http://"+ new Tools().getIP(requireActivity())+ URLs.PostTransRequest)
                    .addJSONObjectBody(jsonObjectMain)
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsString(new StringRequestListener() {
                        @Override
                        public void onResponse(String response) {
                            if (response.contains(docNo)) {
                                alertDialog.dismiss();
                                if(helper.deleteRequestEnquiry_Header(iTransId,iDocType,docNo)) {
                                    if (helper.deleteRequestEnquiry_Body(iDocType, iTransId)) {

                                        if(helper.changeStatusForAll()) {
                                            Log.d("status ", " changed successfully");
                                        }
                                        Toast.makeText(requireActivity(), "Posted successfully", Toast.LENGTH_SHORT).show();
                                        bodyPartList.clear();
                                        NavDirections actions = RequestFragmentDirections
                                                .actionRequestFragmentToRequestHistoryFragment(iDocType, toolTitle);
                                        navController.navigate(actions);
                                    }
                                }
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

    private void saveBodyPartProduct() {

        int qty;
        if(!binding.productName.getText().toString().equals("")  && helper.getProductNameValid(binding.productName.getText().toString().trim())) {
            if(!binding.qtyProduct.getText().toString().equals("")){

                    RequestClass bodyPart=new RequestClass();
                    qty=Integer.parseInt(binding.qtyProduct.getText().toString());
                    bodyPart.setProductName(binding.productName.getText().toString());
                    bodyPart.setQty(qty);
                    bodyPart.setiProduct(iProduct);
                    bodyPart.setHashMapBody(hashMapBody);
                    bodyPart.setUnit(binding.spinnerUnit.getSelectedItem().toString());
                    bodyPart.setRemarks(binding.remarksProduct.getText().toString());
                try {
                    Log.d("Positionn",  bodyPartList.get(position_body_Edit).getiProduct()+" kj ");
                }catch (Exception e){
                    editModeProduct=false;
                }
                    if(editModeProduct) {
                        bodyPartList.set(position_body_Edit,bodyPart);

                    }else {
                        bodyPartList.add(bodyPart);

                    }
                    bodyPartAdapter.notifyDataSetChanged();

                    binding.boyPartRV.setAdapter(bodyPartAdapter);

                    initialValueSettingBody();
                    binding.cardViewBody.setVisibility(View.GONE);

                    /////////////////////////////////////////editProduct

                bodyPartAdapter.setOnClickListener(new RequestBodyAdapter.OnClickListener() {
                    @Override
                    public void onItemClick(RequestClass requestClass, int position) {

                        editingProductField(requestClass,position);
                    }
                });


                    ////////////////////////////////////////editProduct

            }else {binding.qtyProduct.setError("no qty");}
        }else {binding.productName.setError("enter valid product");}
    }

    private void editingProductField(RequestClass bodyPart, int position) {
        for (int i=0;i<autoText_B_list.size();i++){
            autoText_B_list.get(i).setText("");
        }
        editModeProduct =true;
        position_body_Edit=position;
        binding.cardViewBody.setVisibility(View.VISIBLE);
        binding.productName.setText(bodyPart.getProductName());
        iProduct=bodyPart.getiProduct();
        binding.qtyProduct.setText(String.valueOf(bodyPart.getQty()));
        binding.remarksProduct.setText(bodyPart.getRemarks());
        setUnit(helper.getProductUnitById(iProduct),position);

        try {
            for (int i = 0; i < autoText_B_list.size(); i++) {
                int tagId = (int) bodyPartList.get(position).hashMapBody.keySet().toArray()[i];
                int tagDetails = (int) bodyPartList.get(position).hashMapBody.values().toArray()[i];
                Cursor cursor = helper.getTagName(tagId, tagDetails);
                if(tagDetails!=0){
                    autoText_B_list.get(i).setText(cursor.getString(cursor.getColumnIndex(TagDetails.S_NAME)));
                }else {
                    autoText_B_list.get(i).setText("");
                }
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
        binding.productName.setText("");
        binding.qtyProduct.setText("");
        binding.cardViewBody.setVisibility(View.GONE);
        setUnit("",-1);

        for (int i=0;i<autoText_B_list.size();i++){
            autoText_B_list.get(i).setText("");
        }
        binding.remarksProduct.setText("");

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
                            binding.productName.setText(products.getsName());
                            iProduct = products.getiId();

                            setUnit(helper.getProductUnitById(iProduct),-1);
                            binding.productName.dismissDropDown();
                        }
                    });
                }
            }
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

    private void barcodeScanningChecking() {

        if(ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(requireActivity(),new String[]{Manifest.permission.CAMERA},101);
        }
        else {
            if (binding.surfaceView.getVisibility() == View.VISIBLE) {
                if(cameraSource!=null){
                    cameraSource.stop();
                }
                binding.surfaceView.setVisibility(View.GONE);
            }
            else if(binding.surfaceView.getVisibility()==View.GONE){
                binding.surfaceView.setVisibility(View.VISIBLE);
                barcodeScanning();
            }
        }
    }

    private void barcodeScanning() {

        barcodeDetector=new BarcodeDetector.Builder(requireActivity()).setBarcodeFormats(Barcode.ALL_FORMATS).build();
        cameraSource=new CameraSource.Builder(requireActivity(),barcodeDetector).setAutoFocusEnabled(true)
                .setRequestedPreviewSize(1080,1920).build();

        binding.surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {

                if(ActivityCompat.checkSelfPermission(requireContext(),Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(requireActivity(),new String[]{Manifest.permission.CAMERA},101);
                }
                else {
                    try {
                        cameraSource.start(binding.surfaceView.getHolder());
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
                            binding.productName.dismissDropDown();

                            productBarCode=array.valueAt(0).displayValue;
                            Cursor cursor=helper.getProductDetailsByBarcode(productBarCode);

                            if(cursor!=null && cursor.moveToFirst()){
                                binding.productName.setText(cursor.getString(cursor.getColumnIndex(Products.S_NAME)));
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
        binding.spinnerUnit.setAdapter(unitAdapter);
        if (bodyPartList.size() > 0) {
            if (position != -1)
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).equals(bodyPartList.get(position).getUnit())) {
                        binding.spinnerUnit.setSelection(i);
                    }
                }
        }
    }

    private void initialValueSettingHeader() {

        alertDialog.show();
        numberOfLinesH =0;
        numberOfLinesB =0;
        StringDate=df.format(new Date());
        binding.date.setText(StringDate);
        if (iDocType == 13) {
            toolTitle = "Request Summary";
        } else {
            toolTitle = "Enquiry Summary";
        }

        Cursor cursor = helper.getUserCode(userIdS);
        if (cursor!=null) {
            userCode = cursor.getString(cursor.getColumnIndex(User.USER_CODE));
        }

        if(Tools.isConnected(requireActivity())) {
            if (EditMode) {
                EditValueFromAPI();
            } else {
                ///////

                AndroidNetworking.get("http://" + new Tools().getIP(requireActivity())+ URLs.GetNextDocNo)
                        .addQueryParameter("iUser", userIdS)
                        .addQueryParameter("iDocType", String.valueOf(iDocType))
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
                                Toast.makeText(requireContext(), anError.getErrorBody(), Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss();
                                NavDirections actions = RequestFragmentDirections
                                        .actionRequestFragmentToRequestHistoryFragment(iDocType,toolTitle);
                                navController.navigate(actions);
                            }
                        });
            }
        }else {
            alertDialog.dismiss();
            Toast.makeText(requireActivity(),"No Internet", Toast.LENGTH_SHORT).show();
            if (EditMode) {
                editfromlocaldb();
            } else {
                Cursor cursor1 = helper.getDataFromRequestEnquiry_Header();
                if (cursor1.getCount() > 0) {
                    int count = Tools.getNewDocNoLocally(cursor1);
                    Log.d("status", count + "");
                    docNo = "L-"+userCode + "-" + DateFormat.format("MM", new Date()) + "-"  + count;
                }else {
                    docNo ="L-"+ userCode + "-" + DateFormat.format("MM", new Date() )+ "-"  + 1;

                }
            }

            binding.docNo.setText(docNo);
        }

    }

    private void editfromlocaldb() {
        Cursor cursorEdit_H=helper.getEditValuesHeadeReqEnq(iTransId,iDocType);

        if(cursorEdit_H.moveToFirst()&& cursorEdit_H.getCount()>0){
            iTransId=cursorEdit_H.getInt(cursorEdit_H.getColumnIndex(RequestEnquiryClass.I_TRANS_ID));
            docNo=cursorEdit_H.getString(cursorEdit_H.getColumnIndex(RequestEnquiryClass.S_DOC_NO));
            binding.docNo.setText(docNo);
            binding.date.setText( cursorEdit_H.getString(cursorEdit_H.getColumnIndex(RequestEnquiryClass.S_DATE)));
            binding.description.setText(cursorEdit_H.getString(cursorEdit_H.getColumnIndex(RequestEnquiryClass.S_NARRATION)));
            changeStatus(iTransId,docNo,1);
        }


        Cursor cursorEdit_B=helper.getEditValuesBodyReqEnq(iTransId,iDocType);


        if(cursorEdit_B.moveToFirst() && cursorEdit_B.getCount()>0) {
            for (int i = 0; i < cursorEdit_B.getCount(); i++) {
                for (int k = 0; k < headerListTags.size(); k++) {
                    int tagDetails = cursorEdit_B.getInt(cursorEdit_B.getColumnIndex("iTag" + headerListTags.get(k)));
                    hashMapHeader.put(headerListTags.get(k), tagDetails);
                    Cursor tagNameCursor = helper.getTagName(headerListTags.get(k), tagDetails);
                    if (tagDetails!=0) {
                        autoText_H_list.get(k).setText(tagNameCursor.getString(tagNameCursor.getColumnIndex(TagDetails.S_NAME)));

                    } else {
                        autoText_H_list.get(k).setText("");
                    }
                }
                for (int k = 0; k < bodyListTags.size(); k++) {
                    hashMapBody.put(bodyListTags.get(k), cursorEdit_B.getInt(cursorEdit_B.getColumnIndex("iTag" + bodyListTags.get(k))));
                }
                RequestClass bodyPart=new RequestClass();
                bodyPart.setiProduct( cursorEdit_B.getInt(cursorEdit_B.getColumnIndex(RequestEnquiryClass.I_PRODUCT)));

                String productName=helper.getProductNameById(cursorEdit_B.getInt(cursorEdit_B.getColumnIndex(RequestEnquiryClass.I_PRODUCT)));

                bodyPart.setProductName(productName);
                bodyPart.setQty( cursorEdit_B.getInt(cursorEdit_B.getColumnIndex(RequestEnquiryClass.F_QTY)));
                bodyPart.setRemarks(cursorEdit_B.getString(cursorEdit_B.getColumnIndex(RequestEnquiryClass.S_REMARKS)));
                bodyPart.setUnit(cursorEdit_B.getString(cursorEdit_B.getColumnIndex(RequestEnquiryClass.S_UNITS)));
                bodyPart.setHashMapBody(hashMapBody);

                bodyPartList.add(bodyPart);
                bodyPartAdapter.notifyDataSetChanged();
                hashMapBody=new HashMap<>();

                if(cursorEdit_B.getCount()==i+1){
                    binding.boyPartRV.setAdapter(bodyPartAdapter);
                    alertDialog.dismiss();

                    bodyPartAdapter.setOnClickListener(new RequestBodyAdapter.OnClickListener() {
                        @Override
                        public void onItemClick(RequestClass requestClass, int position) {
                            editingProductField(bodyPart,position);
                        }
                    });
                }
                cursorEdit_B.moveToNext();
            }
        }
    }

    private void changeStatus(int transId, String docNo, int iStatus) {
        if(helper.changeStatusReqEnq(transId,docNo,iStatus)){
            Log.d("statusChange","successfully");
        }
    }

    private void EditValueFromAPI() {
        if(Tools.isConnected(requireContext())){
            AndroidNetworking.get("http://"+ new Tools().getIP(requireActivity()) + URLs.GetTransRequestDetails)
                    .addQueryParameter("iTransId",String.valueOf(iTransId))
                    .addQueryParameter("iDocType",String.valueOf(iDocType))
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
                            NavDirections actions = RequestFragmentDirections
                                    .actionRequestFragmentToRequestHistoryFragment(iDocType,toolTitle);
                            navController.navigate(actions);
                        }
                    });
        }else {
            Toast.makeText(requireActivity(), "NO Internet", Toast.LENGTH_SHORT).show();
            alertDialog.dismiss();
        }


    }

    private void loadAPIValue_for_Edit(JSONObject response) {
        try {
            JSONArray jsonArray = new JSONArray(response.getString("Table"));
            Log.d("HeadArray",jsonArray.length()+"");
            JSONArray jsonArray1=new JSONArray(response.getString("Table1"));
            Log.d("BodyArray1",jsonArray1.length()+"");

            for (int i=0;i<jsonArray.length();i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                binding.docNo.setText(jsonObject.getString("sDocNo"));
                docNo=jsonObject.getString("sDocNo");
                binding.date.setText(jsonObject.getString("sDate"));
                binding.description.setText(jsonObject.getString("sNarration"));
            }


            for (int j=0;j<jsonArray1.length();j++){
                JSONObject jsonObjectInner = jsonArray1.getJSONObject(j);
                for (int k = 0; k< headerListTags.size(); k++){
                    hashMapHeader.put(headerListTags.get(k),jsonObjectInner.getInt("iTag"+ headerListTags.get(k)));
                    autoText_H_list.get(k).setText(jsonObjectInner.getString("sTag"+ headerListTags.get(k)));
                }
                for(int k=0;k<bodyListTags.size();k++){
                    hashMapBody.put(bodyListTags.get(k),jsonObjectInner.getInt("iTag"+ bodyListTags.get(k)));

                }

                RequestClass requestClass=new RequestClass();
                requestClass.setiProduct(jsonObjectInner.getInt("iProduct"));
                requestClass.setProductName(jsonObjectInner.getString("sProduct"));
                requestClass.setQty(jsonObjectInner.getInt("fQty"));
                requestClass.setRemarks(jsonObjectInner.getString("sRemarks"));
                requestClass.setUnit(jsonObjectInner.getString("sUnits"));
                requestClass.setHashMapBody(hashMapBody);

                bodyPartList.add(requestClass);
                bodyPartAdapter.notifyDataSetChanged();
                hashMapBody=new HashMap<>();


                if(jsonArray1.length()==j+1){
                    Log.d("Bodypartlistsize",bodyPartList.size()+" ");
                    binding.boyPartRV.setAdapter(bodyPartAdapter);
                    alertDialog.dismiss();
                    Log.d("Bodypartlistsize",bodyPartList.size()+" "+jsonObjectInner.getString("sProduct"));

                    bodyPartAdapter.setOnClickListener(new RequestBodyAdapter.OnClickListener() {
                        @Override
                        public void onItemClick(RequestClass requestClass, int position) {
                            editingProductField(requestClass,position);
                        }
                    });
                }
            }


        } catch (JSONException e) {
            Log.d("exception",e.getMessage());
            e.printStackTrace();
        }
    }
}
