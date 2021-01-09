package com.sangsolutions.sang.Fragment;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.sangsolutions.sang.Adapter.Customer.Customer;
import com.sangsolutions.sang.Adapter.Customer.CustomerAdapter;
import com.sangsolutions.sang.Adapter.MasterSettings.MasterSettings;
import com.sangsolutions.sang.Adapter.Products.Products;
import com.sangsolutions.sang.Adapter.Products.ProductsAdapter;
import com.sangsolutions.sang.Adapter.TagDetailsAdapter.TagDetails;
import com.sangsolutions.sang.Adapter.TransSalePurchase.TransSetting;
import com.sangsolutions.sang.Adapter.TagDetailsAdapter.TagDetailsAdapter;
import com.sangsolutions.sang.Database.DatabaseHelper;
import com.sangsolutions.sang.Tools;
import com.sangsolutions.sang.databinding.FragmentSalePurchaseBinding;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding=FragmentSalePurchaseBinding.inflate(getLayoutInflater());
        iDocType = Sale_Purchase_FragmentArgs.fromBundle(getArguments()).getIDocType();
        df = new SimpleDateFormat("dd-MM-yyyy");
        helper=new DatabaseHelper(requireActivity());

        customerList =new ArrayList<>();
        customerAdapter=new CustomerAdapter(requireActivity(),customerList);

        tagList =new ArrayList<>();
        tagDetailsAdapter =new TagDetailsAdapter(requireActivity(),tagList);

        productsList=new ArrayList<>();
        productsAdapter=new ProductsAdapter(requireActivity(),productsList);

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

        binding.tag1Header.addTextChangedListener(getTextWatcher(binding.tag1Header,1));
        binding.tag2Header.addTextChangedListener(getTextWatcher(binding.tag2Header, 2));
        binding.tag3Header.addTextChangedListener(getTextWatcher(binding.tag3Header, 3));
        binding.tag4Header.addTextChangedListener(getTextWatcher(binding.tag4Header,4));
        binding.tag5Header.addTextChangedListener(getTextWatcher(binding.tag5Header, 5));
        binding.Tag6Header.addTextChangedListener(getTextWatcher(binding.Tag6Header, 6));
        binding.Tag7Header.addTextChangedListener(getTextWatcher(binding.Tag7Header,7));
        binding.Tag8Header.addTextChangedListener(getTextWatcher(binding.Tag8Header, 8));

        binding.tag1Body.addTextChangedListener(getTextWatcher(binding.tag1Body,1));
        binding.tag2Body.addTextChangedListener(getTextWatcher(binding.tag2Body, 2));
        binding.tag3Body.addTextChangedListener(getTextWatcher(binding.tag3Body, 3));
        binding.tag4Body.addTextChangedListener(getTextWatcher(binding.tag4Body,4));
        binding.tag5Body.addTextChangedListener(getTextWatcher(binding.tag5Body, 5));
        binding.Tag6Body.addTextChangedListener(getTextWatcher(binding.Tag6Body, 6));
        binding.Tag7Body.addTextChangedListener(getTextWatcher(binding.Tag7Body,7));
        binding.Tag8Body.addTextChangedListener(getTextWatcher(binding.Tag8Body, 8));


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


        return binding.getRoot();
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
                        binding.productName.setAdapter(productsAdapter);
                        if (binding.surfaceView.getVisibility() == View.VISIBLE) {
                            binding.productName.dismissDropDown();
                        }
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


    private TextWatcher getTextWatcher(AutoCompleteTextView autocompleteView, int iTag) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                GetTagDetails(editable.toString(),iTag,autocompleteView);

            }
        };
    }

    private void GetTagDetails(String s, int iTag, AutoCompleteTextView autocompleteView) {
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
                        autocompleteView.setAdapter(tagDetailsAdapter);
                    }

                    tagDetailsAdapter.setOnClickListener(new TagDetailsAdapter.OnClickListener() {
                        @Override
                        public void onItemClick(TagDetails tagDetails, int position) {
                            autocompleteView.setText(tagDetails.getsName());

                            autocompleteView.dismissDropDown();
                            iTagDetail=tagDetails.getiId();
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
                    customerList.add(customer);
                    customerAdapter.notifyDataSetChanged();
                    cursor.moveToNext();
                    if (i + 1 == cursor.getCount()) {
                        binding.customer.setAdapter(customerAdapter);
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
