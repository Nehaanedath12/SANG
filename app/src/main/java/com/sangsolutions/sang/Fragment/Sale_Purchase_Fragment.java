package com.sangsolutions.sang.Fragment;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.icu.number.Precision;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
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
import com.sangsolutions.sang.Database.DatabaseHelper;
import com.sangsolutions.sang.Home;
import com.sangsolutions.sang.Login;
import com.sangsolutions.sang.R;
import com.sangsolutions.sang.Tools;
import com.sangsolutions.sang.databinding.FragmentSalePurchaseBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Sale_Purchase_Fragment extends Fragment {
    FragmentSalePurchaseBinding binding;
    int iDocType;
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
    int iTag1=-1,iTag2=-1,iTag3=-1,iTag4=-1,iTag5=-1,iTag6=-1,iTag7=-1,iTag8=-1;

    ArrayList<Map> mapListBody =new ArrayList<>();
    ArrayList<Map>mapListHeader=new ArrayList<>();
    HashMap<String, Integer> hashMapBody =new HashMap<>();
    HashMap<String, Integer> hashMapHeader =new HashMap<>();

    List<BodyPart>bodyPartList;
    BodyPartAdapter bodyPartAdapter;
    NavController navController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding=FragmentSalePurchaseBinding.inflate(getLayoutInflater());

        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);

        assert getArguments() != null;
        iDocType = Sale_Purchase_FragmentArgs.fromBundle(getArguments()).getIDocType();
        df = new SimpleDateFormat("dd-MM-yyyy");
        helper=new DatabaseHelper(requireActivity());


        customerList =new ArrayList<>();
        customerAdapter=new CustomerAdapter(requireActivity(),customerList);

        tagList =new ArrayList<>();
        tagDetailsAdapter =new TagDetailsAdapter(requireActivity(),tagList);

        productsList=new ArrayList<>();
        productsAdapter=new ProductsAdapter(requireActivity(),productsList);

        bodyPartList=new ArrayList<>();
        bodyPartAdapter=new BodyPartAdapter(requireActivity(),bodyPartList);
        binding.boyPartRV.setLayoutManager(new LinearLayoutManager(requireActivity()));

        StringDate=df.format(new Date());
        binding.date.setText(StringDate);

        if(iDocType==1) {
            docNo = "P-" + DateFormat.format("ddMMyy-HHmmss", new Date());
        }else {
            docNo = "S-" + DateFormat.format("ddMMyy-HHmmss", new Date());
        }
        binding.docNo.setText(docNo);

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
                datePickerDialog.setTitle("Select Date");
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
                if(binding.cardViewBody.getVisibility()==View.GONE){
                    binding.cardViewBody.setVisibility(View.VISIBLE);
                }
                else {
                    binding.cardViewBody.setVisibility(View.GONE);
                }

            }
        });


        for (int tagId=1;tagId<=8;tagId++){
            Cursor cursor=helper.getTransSettings(iDocType,tagId);
            if(cursor!=null ){
                cursor.moveToFirst();
                String iTagPosition=cursor.getString(cursor.getColumnIndex(TransSetting.I_TAG_POSITION));
                String mandatory=cursor.getString(cursor.getColumnIndex(TransSetting.B_MANDATORY));
                String visibility=cursor.getString(cursor.getColumnIndex(TransSetting.B_VISIBLE));
                Log.d(" iTagPositionN " ,iTagPosition+" pos "+mandatory+" visible "+visibility+" iTagId "+tagId);

                Cursor cursor1=helper.getTagNamebyId(tagId);
                cursor1.moveToFirst();
                if(iTagPosition.equals("1")){

                    switch (tagId){
                        case 1:

                            if(visibility.equals("false")){
                            binding.tag1Header.setEnabled(false);
                            }
                            binding.tag1Header.setVisibility(View.VISIBLE);
                            binding.tag1Header.setHint(cursor1.getString(cursor1.getColumnIndex(MasterSettings.S_NAME)));
                            break;
                        case 2:
                            if(visibility.equals("false")){
                                binding.tag2Header.setEnabled(false);
                            }
                            binding.tag2Header.setVisibility(View.VISIBLE);
                            binding.tag2Header.setHint(cursor1.getString(cursor1.getColumnIndex(MasterSettings.S_NAME)));
                            break;
                        case 3:
                            if(visibility.equals("false")){
                                binding.tag3Header.setEnabled(false);
                            }
                            binding.tag3Header.setVisibility(View.VISIBLE);
                            binding.tag3Header.setHint(cursor1.getString(cursor1.getColumnIndex(MasterSettings.S_NAME)));
                            break;
                        case 4:
                            if(visibility.equals("false")){
                                binding.tag4Header.setEnabled(false);
                            }
                            binding.tag4Header.setVisibility(View.VISIBLE);
                            binding.tag4Header.setHint(cursor1.getString(cursor1.getColumnIndex(MasterSettings.S_NAME)));
                            break;
                        case 5:
                            if(visibility.equals("false")){
                                binding.tag5Header.setEnabled(false);
                            }
                            binding.tag5Header.setVisibility(View.VISIBLE);
                            binding.tag5Header.setHint(cursor1.getString(cursor1.getColumnIndex(MasterSettings.S_NAME)));
                            break;
                        case 6:
                            if(visibility.equals("false")){
                                binding.Tag6Header.setEnabled(false);
                            }
                            binding.Tag6Header.setVisibility(View.VISIBLE);
                            binding.Tag6Header.setHint(cursor1.getString(cursor1.getColumnIndex(MasterSettings.S_NAME)));
                            break;
                        case 7:
                            if(visibility.equals("false")){
                                binding.Tag7Header.setEnabled(false);
                            }
                            binding.Tag7Header.setVisibility(View.VISIBLE);
                            binding.Tag7Header.setHint(cursor1.getString(cursor1.getColumnIndex(MasterSettings.S_NAME)));
                            break;
                        case 8:
                            if(visibility.equals("false")){
                                binding.Tag8Header.setEnabled(false);
                            }
                            binding.Tag8Header.setVisibility(View.VISIBLE);
                            binding.Tag8Header.setHint(cursor1.getString(cursor1.getColumnIndex(MasterSettings.S_NAME)));
                            break;

                    }

                }else if(iTagPosition.equals("2")){
                    switch (tagId){
                        case 1:
                            if(visibility.equals("false")){
                                binding.tag1Body.setEnabled(false);
                            }
                            binding.tag1Body.setVisibility(View.VISIBLE);
                            binding.tag1Body.setHint(cursor1.getString(cursor1.getColumnIndex(MasterSettings.S_NAME)));
                            break;
                        case 2:
                            if(visibility.equals("false")){
                                binding.tag2Body.setEnabled(false);
                            }
                            binding.tag2Body.setVisibility(View.VISIBLE);
                            binding.tag2Body.setHint(cursor1.getString(cursor1.getColumnIndex(MasterSettings.S_NAME)));
                            break;
                        case 3:
                            if(visibility.equals("false")){
                                binding.tag3Body.setEnabled(false);
                            }
                            binding.tag3Body.setVisibility(View.VISIBLE);
                            binding.tag3Body.setHint(cursor1.getString(cursor1.getColumnIndex(MasterSettings.S_NAME)));
                            break;
                        case 4:
                            if(visibility.equals("false")){
                                binding.tag4Body.setEnabled(false);
                            }
                            binding.tag4Body.setVisibility(View.VISIBLE);
                            binding.tag4Body.setHint(cursor1.getString(cursor1.getColumnIndex(MasterSettings.S_NAME)));
                            break;
                        case 5:
                            if(visibility.equals("false")){
                                binding.tag5Body.setEnabled(false);
                            }
                            binding.tag5Body.setVisibility(View.VISIBLE);
                            binding.tag5Body.setHint(cursor1.getString(cursor1.getColumnIndex(MasterSettings.S_NAME)));
                            break;
                        case 6:
                            if(visibility.equals("false")){
                                binding.Tag6Body.setEnabled(false);
                            }
                            binding.Tag6Body.setVisibility(View.VISIBLE);
                            binding.Tag6Body.setHint(cursor1.getString(cursor1.getColumnIndex(MasterSettings.S_NAME)));
                            break;
                        case 7:
                            if(visibility.equals("false")){
                                binding.Tag7Body.setEnabled(false);
                            }
                            binding.Tag7Body.setVisibility(View.VISIBLE);
                            binding.Tag7Body.setHint(cursor1.getString(cursor1.getColumnIndex(MasterSettings.S_NAME)));
                            break;
                        case 8:
                            if(visibility.equals("false")){
                                binding.Tag8Body.setEnabled(false);
                            }
                            binding.Tag8Body.setVisibility(View.VISIBLE);
                            binding.Tag8Body.setHint(cursor1.getString(cursor1.getColumnIndex(MasterSettings.S_NAME)));
                            break;

                    }

                }

            }

        }

        binding.tag1Header.addTextChangedListener(getTextWatcher(binding.tag1Header,1,"Header"));
        binding.tag2Header.addTextChangedListener(getTextWatcher(binding.tag2Header, 2,"Header"));
        binding.tag3Header.addTextChangedListener(getTextWatcher(binding.tag3Header, 3,"Header"));
        binding.tag4Header.addTextChangedListener(getTextWatcher(binding.tag4Header,4, "Header"));
        binding.tag5Header.addTextChangedListener(getTextWatcher(binding.tag5Header, 5, "Header"));
        binding.Tag6Header.addTextChangedListener(getTextWatcher(binding.Tag6Header, 6, "Header"));
        binding.Tag7Header.addTextChangedListener(getTextWatcher(binding.Tag7Header,7, "Header"));
        binding.Tag8Header.addTextChangedListener(getTextWatcher(binding.Tag8Header, 8, "Header"));

        binding.tag1Body.addTextChangedListener(getTextWatcher(binding.tag1Body,1, "Body"));
        binding.tag2Body.addTextChangedListener(getTextWatcher(binding.tag2Body, 2, "Body"));
        binding.tag3Body.addTextChangedListener(getTextWatcher(binding.tag3Body, 3, "Body"));
        binding.tag4Body.addTextChangedListener(getTextWatcher(binding.tag4Body,4, "Body"));
        binding.tag5Body.addTextChangedListener(getTextWatcher(binding.tag5Body, 5, "Body"));
        binding.Tag6Body.addTextChangedListener(getTextWatcher(binding.Tag6Body, 6, "Body"));
        binding.Tag7Body.addTextChangedListener(getTextWatcher(binding.Tag7Body,7, "Body"));
        binding.Tag8Body.addTextChangedListener(getTextWatcher(binding.Tag8Body, 8, "Body"));


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
                saveBodyPartProduct();
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
                                    if(!binding.customer.getText().toString().equals("")) {
                                        if(bodyPartList.size()>0) {
                                            saveMain();

                                        }
                                        else {
                                            Toast.makeText(requireContext(), "No products to add", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    else {
                                        binding.customer.setError("empty customer!");
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
                                bodyPartList.clear();
                                mapListBody.clear();
                                NavDirections actions = Sale_Purchase_FragmentDirections.actionSalePurchaseFragmentToSalesPurchaseHistoryFragment().setIDocType(iDocType);
                                navController.navigate(actions);
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

    private void saveMain() {
        Cursor cursor=helper.getUserId();
        String userIdS=cursor.getString(cursor.getColumnIndex("user_Id"));
        JSONObject jsonObjectMain=new JSONObject();
        try{
            jsonObjectMain.put("iTransId",0);
            jsonObjectMain.put("sDocNo",docNo);
            jsonObjectMain.put("sDate",Tools.dateFormat(StringDate));
            jsonObjectMain.put("iDocType",iDocType);
            jsonObjectMain.put("iAccount1",iCustomer);
            jsonObjectMain.put("iAccount2",0);
            jsonObjectMain.put("sNarration",binding.description.getText().toString());
            jsonObjectMain.put("iUser",Integer.parseInt(userIdS));
            JSONArray jsonArray=new JSONArray();
            for (int i=0;i<bodyPartList.size();i++){
                JSONObject jsonObject=new JSONObject();
                Log.d("iTag11",String.valueOf(hashMapHeader.get("iTag1")));

                if(hashMapHeader.containsKey("iTag1")){
                    jsonObject.put("iTag1",hashMapHeader.get("iTag1"));
                }
                else if(mapListBody.get(i).containsKey("iTag1")){
                         jsonObject.put("iTag1", mapListBody.get(i).get("iTag1"));
                }
                else {
                    jsonObject.put("iTag1",-1);
                }


                if(hashMapHeader.containsKey("iTag2")){
                    jsonObject.put("iTag2",hashMapHeader.get("iTag2"));
                }
                else if(mapListBody.get(i).containsKey("iTag2")) {
                    jsonObject.put("iTag2", mapListBody.get(i).get("iTag2"));
                }
                else {
                    jsonObject.put("iTag2",-1);
                }


                if(hashMapHeader.containsKey("iTag3")){
                    jsonObject.put("iTag3",hashMapHeader.get("iTag3"));
                }
                else if(mapListBody.get(i).containsKey("iTag3")){
                    jsonObject.put("iTag3", mapListBody.get(i).get("iTag3"));
                }
                else {
                    jsonObject.put("iTag3",-1);
                }

                if(hashMapHeader.containsKey("iTag4")){
                    jsonObject.put("iTag4",hashMapHeader.get("iTag4"));
                }
                else if(mapListBody.get(i).containsKey("iTag4")){
                    jsonObject.put("iTag4", mapListBody.get(i).get("iTag4"));
                }
                else {
                    jsonObject.put("iTag4",-1);
                }






                if(hashMapHeader.containsKey("iTag5")){
                    jsonObject.put("iTag5",hashMapHeader.get("iTag5"));
                }
                else if(mapListBody.get(i).containsKey("iTag5")) {
                    jsonObject.put("iTag5", mapListBody.get(i).get("iTag5"));
                }
                else {
                    jsonObject.put("iTag5",-1);
                }


                if(hashMapHeader.containsKey("iTag6")){
                    jsonObject.put("iTag6",hashMapHeader.get("iTag6"));
                }
                else if(mapListBody.get(i).containsKey("iTag6"))  {
                    jsonObject.put("iTag6", mapListBody.get(i).get("iTag6"));
                }else {
                    jsonObject.put("iTag6",-1);
                }


                if(hashMapHeader.containsKey("iTag7")){
                    jsonObject.put("iTag7",hashMapHeader.get("iTag7"));
                }
                else if(mapListBody.get(i).containsKey("iTag7")) {
                    jsonObject.put("iTag7", mapListBody.get(i).get("iTag7"));
                }else {
                    jsonObject.put("iTag7",-1);
                }

                if(hashMapHeader.containsKey("iTag8")){
                    jsonObject.put("iTag8",hashMapHeader.get("iTag8"));
                }
                else if(mapListBody.get(i).containsKey("iTag8")){
                    jsonObject.put("iTag8", mapListBody.get(i).get("iTag8"));
                }
                else {
                    jsonObject.put("iTag8",-1);
                }

                jsonObject.put("iProduct",bodyPartList.get(i).getiProduct());
                jsonObject.put("fQty",bodyPartList.get(i).getQty());
                jsonObject.put("fRate",bodyPartList.get(i).getRate());
                jsonObject.put("fDiscount",bodyPartList.get(i).getDiscount());
                jsonObject.put("fAddCharges",bodyPartList.get(i).getAddCharges());
                jsonObject.put("fVatPer",bodyPartList.get(i).getVatPer());
                jsonObject.put("fVAT",bodyPartList.get(i).getVat());
                jsonObject.put("sRemarks",binding.remarksProduct.getText().toString());

                Log.d("producti",bodyPartList.get(i).getiProduct()+"");
                jsonArray.put(jsonObject);
            }
            jsonObjectMain.put("Body",jsonArray);

            uploadToAPI(jsonObjectMain);




        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void uploadToAPI(JSONObject jsonObjectMain) {
        if(Tools.isConnected(requireActivity())) {
            Log.d("Uploadd", "response");
            AndroidNetworking.post("http://185.151.4.167/Focus/api/Data/PostProductStock")
                    .addJSONObjectBody(jsonObjectMain)
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsString(new StringRequestListener() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("Uploadd", response);
                            if (response.contains(docNo)) {
                                Log.d("Uploading ", "successfully");
                                Toast.makeText(requireActivity(), "Posted successfully", Toast.LENGTH_SHORT).show();
                                bodyPartList.clear();
                                NavDirections actions = Sale_Purchase_FragmentDirections.actionSalePurchaseFragmentToSalesPurchaseHistoryFragment().setIDocType(iDocType);
                                navController.navigate(actions);
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            Log.d("Uploadd", anError.getErrorDetail() + anError.getErrorBody() + anError.toString());
                        }
                    });
        }
        else {
            Toast.makeText(requireActivity(), "connect to network", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveBodyPartProduct() {

        int qty;
        float rate,gross,net,vatPer,vat,discount,addCharges;
        DecimalFormat df = new DecimalFormat("#.00");
        if(!binding.productName.getText().toString().equals("")){
            if(!binding.qtyProduct.getText().toString().equals("")){
                if(!binding.rateProduct.getText().toString().equals("")){
                    if(!binding.vatProduct.getText().toString().equals("")){
                        mapListBody.add(hashMapBody);

                        Log.d("maplist", mapListBody.size()+"");
                        BodyPart bodyPart=new BodyPart();
                        qty=Integer.parseInt(binding.qtyProduct.getText().toString());
                        rate=Integer.parseInt(binding.rateProduct.getText().toString());
                        vatPer=Float.parseFloat(binding.vatProduct.getText().toString());
                        discount=Float.parseFloat(binding.disProduct.getText().toString());
                        addCharges=Float.parseFloat(binding.addChargesProduct.getText().toString());

                        gross=qty*rate;
                        vat=((vatPer/100)*(gross-discount+addCharges));
                        net=gross-discount+addCharges+vat;

                        bodyPart.setGross(gross);
                        bodyPart.setNet(net);
                        bodyPart.setVat(Float.parseFloat(df.format(vat)));
                        bodyPart.setVatPer(vatPer);
                        bodyPart.setDiscount(discount);
                        bodyPart.setAddCharges(addCharges);
                        bodyPart.setProductName(binding.productName.getText().toString());
                        bodyPart.setQty(qty);
                        bodyPart.setRate(rate);
                        bodyPart.setiProduct(iProduct);

                        bodyPartList.add(bodyPart);
                        if(bodyPartList.size()>0){
                            binding.frameEmpty.setVisibility(View.GONE);
                        }
                        else {
                            binding.frameEmpty.setVisibility(View.VISIBLE);
                        }
                        binding.boyPartRV.setAdapter(bodyPartAdapter);
                        Log.d("listSize",bodyPartList.size()+"");

                        initialValueSettingBody();
                        binding.cardViewBody.setVisibility(View.GONE);
                    }
                }
            }else {binding.qtyProduct.setError("no qty");}
        }else {binding.productName.setError("no Product");}


    }

    private void initialValueSettingBody() {
        binding.productName.setText("");
        binding.qtyProduct.setText("");
        binding.rateProduct.setText("");
        binding.vatProduct.setText("");
        binding.disProduct.setText("");
        binding.addChargesProduct.setText("");
        binding.tag1Body.setText("");
        binding.tag2Body.setText("");
        binding.tag3Body.setText("");
        binding.tag4Body.setText("");
        binding.tag5Body.setText("");
        binding.Tag6Body.setText("");
        binding.Tag7Body.setText("");
        binding.Tag8Body.setText("");
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
                    productsAdapter.notifyDataSetChanged();
                    cursor.moveToNext();
                    if (i + 1 == cursor.getCount()) {
//                        binding.productName.setThreshold(1);
//                        binding.productName.setAdapter(productsAdapter);
//                        if (binding.surfaceView.getVisibility() == View.VISIBLE) {
//                            binding.productName.dismissDropDown();
//                        }
                    }
                    productsAdapter.setOnClickListener(new ProductsAdapter.OnClickListener() {
                        @Override
                        public void onItemClick(Products products, int position) {
                            binding.productName.setText(products.getsName());
                            iProduct = products.getiId();
                            binding.productName.dismissDropDown();
                            Log.d("iProduct",iProduct+"");
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
                calllbeforeTag(autocompleteView);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                GetTagDetails(editable.toString(),iTag,autocompleteView,iTagPosition);

            }
        };
    }

    private void calllbeforeTag(AutoCompleteTextView autocompleteView) {
        autocompleteView.setThreshold(1);
        autocompleteView.setAdapter(tagDetailsAdapter);
    }

    private void GetTagDetails(String s, int iTag, AutoCompleteTextView autocompleteView, String iTagPosition) {
        tagList.clear();
        Cursor cursor=helper.getTagbyKeyword(s,iTag);
        if(cursor!=null && !s.equals("")) {
            cursor.moveToFirst();
            Log.d("iTag",cursor.getCount()+"count"+"");
            if (cursor.getCount() > 0) {
                for (int i = 0; i < cursor.getCount(); i++) {
                    TagDetails details=new TagDetails();
                    details.setsName(cursor.getString(cursor.getColumnIndex(TagDetails.S_NAME)));
                    details.setiId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(TagDetails.I_ID))));
                    tagList.add(details);
                    tagDetailsAdapter.notifyDataSetChanged();
                    cursor.moveToNext();
                    if (i + 1 == cursor.getCount()) {
//                        autocompleteView.setThreshold(1);
//                        autocompleteView.setAdapter(tagDetailsAdapter);
                    }

                    tagDetailsAdapter.setOnClickListener(new TagDetailsAdapter.OnClickListener() {
                        @Override
                        public void onItemClick(TagDetails tagDetails, int position) {

                            autocompleteView.setText(tagDetails.getsName());
                            iTagDetail=tagDetails.getiId();
                            Log.d("itagId",iTagDetail+"");
                            if(iTagPosition.equals("Body")){
                                hashMapBody.put("iTag"+iTag,iTagDetail);
                                Log.d("itagIdBody",iTagDetail+"");
                            }
                            else if(iTagPosition.equals("Header"))
                            {
                                hashMapHeader.put("iTag"+iTag,iTagDetail);
                            }

                            mapListHeader.add(hashMapHeader);
                            autocompleteView.dismissDropDown();

                            Log.d("iTagDetail",iTagDetail+"");
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

                    Log.d("dddd",cursor.getString(cursor.getColumnIndex(Customer.S_NAME))+"");

//                    binding.customer.setThreshold(1);

                    customerList.add(customer);
                    customerAdapter.notifyDataSetChanged();
                    cursor.moveToNext();
                    if (i + 1 == cursor.getCount()) {
                        Log.d("dddd",customerList.size()+"");
//                        binding.customer.setThreshold(1);
//                        binding.customer.setAdapter(customerAdapter);

                    }
                }
                customerAdapter.setOnClickListener(new CustomerAdapter.OnClickListener() {
                    @Override
                    public void onItemClick(Customer customer, int position) {
                        iCustomer=customer.getiId();
                        binding.customer.setText(customer.getsName());
                        binding.customer.dismissDropDown();
                        Log.d("iCustomer",iCustomer+"");
                    }
                });
            }
        }

        else {
            Customer customer=new Customer();
            customerList.clear();
            customer.setsName("No customer available!!");
            customerList.add(customer);

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
                                Log.d("iTag",productId+"count"+"");
                            }
                        }
                    });
                }
            }
        });

    }

}
