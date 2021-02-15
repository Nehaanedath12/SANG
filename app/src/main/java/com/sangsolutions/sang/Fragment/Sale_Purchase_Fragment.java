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
import com.sangsolutions.sang.Adapter.BodyAdapter.BodyPart;
import com.sangsolutions.sang.Adapter.BodyAdapter.BodyPartAdapter;
import com.sangsolutions.sang.Adapter.Customer.Customer;
import com.sangsolutions.sang.Adapter.Customer.CustomerAdapter;
import com.sangsolutions.sang.Adapter.MasterSettings.MasterSettings;
import com.sangsolutions.sang.Adapter.Products.Products;
import com.sangsolutions.sang.Adapter.Products.ProductsAdapter;
import com.sangsolutions.sang.Adapter.TagDetailsAdapter.TagDetails;
import com.sangsolutions.sang.Adapter.TransSalePurchase.TransSetting;
import com.sangsolutions.sang.Adapter.TagDetailsAdapter.TagDetailsAdapter;
import com.sangsolutions.sang.Adapter.UnitAdapter;
import com.sangsolutions.sang.Adapter.User;
import com.sangsolutions.sang.Database.DatabaseHelper;
import com.sangsolutions.sang.R;
import com.sangsolutions.sang.Tools;
import com.sangsolutions.sang.URLs;
import com.sangsolutions.sang.databinding.FragmentSalePurchaseBinding;

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

import static java.nio.file.Paths.get;

public class Sale_Purchase_Fragment extends Fragment {
    FragmentSalePurchaseBinding binding;
    int iDocType,iTransId;
    boolean EditMode;
    String docNo;
    String StringDate;
    SimpleDateFormat df;
    DatabaseHelper helper;
    List<Customer> customerList;
    CustomerAdapter customerAdapter;

    List<TagDetails> tagList;
    TagDetailsAdapter tagDetailsAdapter;

    List<Products>productsList;
    ProductsAdapter productsAdapter;

    CameraSource cameraSource;
    BarcodeDetector barcodeDetector;

    String productBarCode,productId;

    int iCustomer,iProduct,iTagDetail;

    HashMap<Integer, Integer> hashMapBody;
    HashMap<Integer, Integer> hashMapHeader;

    List<BodyPart>bodyPartList;
    BodyPartAdapter bodyPartAdapter;
    NavController navController;

    int numberOfLinesH;
    int numberOfLinesB;
    ArrayList<AutoCompleteTextView>autoText_B_list;
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


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding=FragmentSalePurchaseBinding.inflate(getLayoutInflater());

        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);



        hashMapBody=new HashMap<>();
        hashMapHeader=new HashMap<>();

        headerListTags =new ArrayList<>();
        bodyListTags =new ArrayList<>();

        assert getArguments() != null;
        iDocType = Sale_Purchase_FragmentArgs.fromBundle(getArguments()).getIDocType();
        iTransId = Sale_Purchase_FragmentArgs.fromBundle(getArguments()).getITransId();
        EditMode = Sale_Purchase_FragmentArgs.fromBundle(getArguments()).getEditMode();

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


        customerList =new ArrayList<>();
        customerAdapter=new CustomerAdapter(requireActivity(),customerList);

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
        bodyPartAdapter=new BodyPartAdapter(requireActivity(),bodyPartList,tagTotalNumber,iDocType);
        binding.boyPartRV.setLayoutManager(new LinearLayoutManager(requireActivity()));

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



        binding.saveProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean flag=true;
                    if(mandatoryList_B.size()<=0){
                        saveBodyPartProduct();
                    }

                        for (int i = 0; i < mandatoryList_B.size(); i++) {
                            if (!mandatoryList_B.get(i).getText().toString().equals("")) {
                                if (i + 1 == mandatoryList_B.size() && flag ) {
                                   saveBodyPartProduct();
                                }

                            } else {
                                Toast.makeText(requireContext(), "Mandatory fields are not filled", Toast.LENGTH_SHORT).show();
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
                                                if (!binding.customer.getText().toString().equals("") && helper.getCustomerNameValid(binding.customer.getText().toString().trim())) {

                                                    if (mandatoryList_H.size() <= 0) {
                                                        saveMain();
                                                    }

                                                    for (int i = 0; i < mandatoryList_H.size(); i++) {
                                                        if (!mandatoryList_H.get(i).getText().toString().equals("")) {
                                                            if (i + 1 == mandatoryList_H.size() && flag) {
//                                                                if(mandatoryList_H.size()==hashMapHeader.size()){
                                                                    saveMain();
//                                                                }
//                                                                else {
//                                                                    Toast.makeText(requireContext(), "Enter valid Header values", Toast.LENGTH_SHORT).show();
//                                                                }

                                                            }
                                                        } else {
                                                            Toast.makeText(requireContext(), "Mandatory fields are not filled", Toast.LENGTH_SHORT).show();
                                                            mandatoryList_H.get(i).setError("Mandatory");
                                                            flag = false;
                                                        }
                                                    }
                                                }


                                        else {
                                            binding.customer.setError("Enter valid customer!");
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

    private void initialValueSettingHeader() {
        alertDialog.show();
        numberOfLinesH =0;
        numberOfLinesB =0;
        StringDate=df.format(new Date());
        binding.date.setText(StringDate);
        if (iDocType == 1) {
            toolTitle = "Purchase Summary";
        } else {
            toolTitle = "Sale Summary";
        }

        if(Tools.isConnected(requireActivity())) {
            if (EditMode) {
                EditValueFromAPI();
            } else {

                String userCode = null;
                Cursor cursor = helper.getUserCode(userIdS);
                if (cursor.moveToFirst() && cursor.getCount() > 0) {
                    userCode = cursor.getString(cursor.getColumnIndex(User.USER_CODE));
                }
                final int[] arrayLength = new int[1];
                ///////

                String finalUserCode = userCode;
                    AndroidNetworking.get("http://" + new Tools().getIP(requireActivity()) + URLs.GetTransSummary)
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

        ///////


    }

    private void EditValueFromAPI() {
        if(Tools.isConnected(requireContext())){
            AndroidNetworking.get("http://" + new Tools().getIP(requireActivity()) + URLs.GetTransDetails)
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

        JSONArray jsonArray= null;
        try {
            jsonArray = new JSONArray(response.getString("Table"));
            Log.d("HeadArray",jsonArray.length()+"");
            JSONArray jsonArray1=new JSONArray(response.getString("Table1"));
            Log.d("BodyArray1",jsonArray1.length()+"");

            for (int i=0;i<jsonArray.length();i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                binding.docNo.setText(jsonObject.getString("sDocNo"));
                docNo=jsonObject.getString("sDocNo");
                binding.date.setText(jsonObject.getString("sDate"));
                iCustomer = jsonObject.getInt("iAccount1");
                binding.customer.setText(jsonObject.getString("sAccount1"));
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
                float gross=jsonObjectInner.getInt("fQty")*Float.parseFloat(jsonObjectInner.getString("fRate"));

                BodyPart bodyPart=new BodyPart();
                bodyPart.setiProduct(jsonObjectInner.getInt("iProduct"));
                bodyPart.setProductName(jsonObjectInner.getString("sProduct"));
                bodyPart.setQty(jsonObjectInner.getInt("fQty"));
                bodyPart.setGross(gross);
                bodyPart.setNet(Float.parseFloat(jsonObjectInner.getString("fNet")));
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
                    Log.d("Bodypartlistsize",bodyPartList.size()+" ");
                    binding.boyPartRV.setAdapter(bodyPartAdapter);
                    alertDialog.dismiss();
                    Log.d("Bodypartlistsize",bodyPartList.size()+" "+jsonObjectInner.getString("sProduct"));
                    bodyPartAdapter.setOnClickListener(new BodyPartAdapter.OnClickListener() {
                        @Override
                        public void onItemClick(BodyPart bodyPart, int position) {
//                            productDialogue();

                            editingProductField(bodyPart,position);
                        }
                    });
                }
            }




        } catch (JSONException e) {
            Log.d("exception",e.getMessage());
            e.printStackTrace();
        }
    }

    private void deleteAll() {
        EditMode=false;
        editModeProduct=false;
        initialValueSettingHeader();
        initialValueSettingBody();
        binding.customer.setText("");
        binding.description.setText("");
        for (int i=0;i<autoText_H_list.size();i++){
            autoText_H_list.get(i).setText("");
        }
        bodyPartList.clear();
        bodyPartAdapter.notifyDataSetChanged();
    }

    private void saveMain() {

        JSONObject jsonObjectMain=new JSONObject();
        try{
            jsonObjectMain.put("iTransId",iTransId);
            jsonObjectMain.put("sDocNo",docNo);
            jsonObjectMain.put("sDate",Tools.dateFormat(StringDate));
            jsonObjectMain.put("iDocType",iDocType);
            jsonObjectMain.put("iAccount1",iCustomer);
            jsonObjectMain.put("iAccount2",0);
            jsonObjectMain.put("sNarration",binding.description.getText().toString());
            assert userIdS != null;
            jsonObjectMain.put("iUser",Integer.parseInt(userIdS));

            Log.d("jsonObjecMain",jsonObjectMain.get("iTransId")+"");
            Log.d("jsonObjecMain",jsonObjectMain.get("sDocNo")+"");
            Log.d("jsonObjecMain",jsonObjectMain.get("iDocType")+"");
            Log.d("jsonObjecMain",jsonObjectMain.get("iAccount1")+"");
            Log.d("jsonObjecMain",jsonObjectMain.get("iAccount2")+"");
            Log.d("jsonObjecMain",jsonObjectMain.get("sNarration")+"");
            Log.d("jsonObjecMain",jsonObjectMain.get("iUser")+"");


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

                jsonObject.put("iProduct",bodyPartList.get(i).getiProduct());
                jsonObject.put("fQty",bodyPartList.get(i).getQty());
                jsonObject.put("fRate",bodyPartList.get(i).getRate());
                jsonObject.put("fDiscount",bodyPartList.get(i).getDiscount());
                jsonObject.put("fAddCharges",bodyPartList.get(i).getAddCharges());
                jsonObject.put("fVatPer",bodyPartList.get(i).getVatPer());
                jsonObject.put("fVAT",bodyPartList.get(i).getVat());
                if(bodyPartList.get(i).getRemarks().equals("")){
                    jsonObject.put("sRemarks","");
                }else {
                    jsonObject.put("sRemarks",bodyPartList.get(i).getRemarks());
                }
                jsonObject.put("sUnits",bodyPartList.get(i).getUnit());
                jsonObject.put("fNet",bodyPartList.get(i).getNet());



                Log.d("jsonObjecttIproduct",jsonObject.get("iProduct")+"");
                Log.d("jsonObjecttQty",jsonObject.get("fQty")+"");
                Log.d("jsonObjecttrate",jsonObject.get("fRate")+"");
                Log.d("jsonObjecttdis",jsonObject.get("fDiscount")+"");
                Log.d("jsonObjecttaddcha",jsonObject.get("fAddCharges")+"");
                Log.d("jsonObjecttvarper",jsonObject.get("fVatPer")+"");
                Log.d("jsonObjecttvat",jsonObject.get("fVAT")+"");
                Log.d("jsonObjecttrema",jsonObject.get("sRemarks")+"");



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
            AndroidNetworking.post("http://"+ new Tools().getIP(requireActivity()) + URLs.PostProductStock)
                    .addJSONObjectBody(jsonObjectMain)
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsString(new StringRequestListener() {
                            @Override
                            public void onResponse(String response) {
                            if (response.contains(docNo)) {
                                    alertDialog.dismiss();
                                Log.d("responsePost ", "successfully");
                                Toast.makeText(requireActivity(), "Posted successfully", Toast.LENGTH_SHORT).show();
                                bodyPartList.clear();
                                NavDirections actions = Sale_Purchase_FragmentDirections.actionSalePurchaseFragmentToSalesPurchaseHistoryFragment(toolTitle).setIDocType(iDocType);
                                navController.navigate(actions);
                                }
                                }

                        @Override
                        public void onError(ANError anError) {
                                    alertDialog.dismiss();
                            Log.d("responsePost", anError.getErrorDetail() + anError.getErrorBody() + anError.toString());
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

    private void saveBodyPartProduct() {

        float rate,gross,net,vatPer,vat,discount,addCharges;
        int qty;
        DecimalFormat df = new DecimalFormat("#.00");
        if(!binding.productName.getText().toString().equals("")  && helper.getProductNameValid(binding.productName.getText().toString().trim())) {
            if(!binding.qtyProduct.getText().toString().equals("")){
                if(!binding.rateProduct.getText().toString().equals("")){
                    if(!binding.vatProduct.getText().toString().equals("")){
                        if(!binding.disProduct.getText().toString().equals("")){
                            if(!binding.addChargesProduct.getText().toString().equals("")){

                        BodyPart bodyPart=new BodyPart();

                        qty=Integer.parseInt(binding.qtyProduct.getText().toString());
                        rate=Float.parseFloat(binding.rateProduct.getText().toString());
                        vatPer=Float.parseFloat(binding.vatProduct.getText().toString());
                        discount=Float.parseFloat(binding.disProduct.getText().toString());
                        addCharges=Float.parseFloat(binding.addChargesProduct.getText().toString());

                        gross=qty*rate;
                        vat=((vatPer/100)*(gross-discount+addCharges));
                        net=gross-discount+addCharges+vat;

                        bodyPart.setGross(gross);
                        bodyPart.setNet(Float.parseFloat(df.format(net)));
                        bodyPart.setVat(Float.parseFloat(df.format(vat)));
                        bodyPart.setVatPer(vatPer);
                        bodyPart.setDiscount(discount);
                        bodyPart.setAddCharges(addCharges);
                        bodyPart.setProductName(binding.productName.getText().toString());
                        bodyPart.setQty(qty);
                        bodyPart.setRate(rate);
                        bodyPart.setiProduct(iProduct);
                        bodyPart.setHashMapBody(hashMapBody);
                        bodyPart.setUnit(binding.spinnerUnit.getSelectedItem().toString());

                        bodyPart.setRemarks(binding.remarksProduct.getText().toString());

                        if(editModeProduct) {
                            bodyPartList.set(position_body_Edit,bodyPart);

                        }else {
                            bodyPartList.add(bodyPart);

                        }
                        bodyPartAdapter.notifyDataSetChanged();

//                        if(bodyPartList.size()>0){
//                            binding.frameEmpty.setVisibility(View.GONE);
//                        }
//                        else {
//                            binding.frameEmpty.setVisibility(View.VISIBLE);
//                        }
                        binding.boyPartRV.setAdapter(bodyPartAdapter);

                        initialValueSettingBody();
                        binding.cardViewBody.setVisibility(View.GONE);

                        /////////////////////////////////////////editProduct

                        bodyPartAdapter.setOnClickListener(new BodyPartAdapter.OnClickListener() {
                            @Override
                            public void onItemClick(BodyPart bodyPart, int position) {

                                editingProductField(bodyPart,position);


                            }
                        });

                        ////////////////////////////////////////editProduct

                            }else {binding.addChargesProduct.setError("no addChargesProduct");}
                        }else {binding.disProduct.setError("no disProduct");}
                    }else {binding.vatProduct.setError("no Vat");}
                }else {binding.rateProduct.setError("no Rate");}
            }else {binding.qtyProduct.setError("no qty");}
        }else {binding.productName.setError("enter valid product");}


    }

    private void editingProductField(BodyPart bodyPart, int position) {
        editModeProduct =true;
        position_body_Edit=position;
        binding.cardViewBody.setVisibility(View.VISIBLE);
        binding.productName.setText(bodyPart.getProductName());
        iProduct=bodyPart.getiProduct();
        binding.qtyProduct.setText(String.valueOf(bodyPart.getQty()));
        binding.rateProduct.setText(String.valueOf(bodyPart.getRate()));
        binding.vatProduct.setText(String.valueOf(bodyPart.getVatPer()));
        binding.disProduct.setText(String.valueOf(bodyPart.getDiscount()));
        binding.addChargesProduct.setText(String.valueOf(bodyPart.getAddCharges()));
        binding.remarksProduct.setText(bodyPart.getRemarks());
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
        binding.productName.setText("");
        binding.qtyProduct.setText("");
        binding.rateProduct.setText("");
        binding.vatProduct.setText("");
        binding.disProduct.setText("");
        binding.addChargesProduct.setText("");
        binding.cardViewBody.setVisibility(View.GONE);
        setUnit("",-1);

        for (int i=0;i<autoText_B_list.size();i++){
            autoText_B_list.get(i).setText("");
        }
        binding.remarksProduct.setText("");
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
                                productId=cursor.getString(cursor.getColumnIndex(Products.I_ID));
                                }
                            }
                            });
                            }
                            }
                            });

    }
    }
