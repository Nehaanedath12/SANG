package com.sangsolutions.sang.Fragment.PaymentReceiptFragment;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
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
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.sangsolutions.sang.Adapter.BankAdapter.Bank;
import com.sangsolutions.sang.Adapter.BankAdapter.BankAdapter;
import com.sangsolutions.sang.Adapter.CheckImageAdapter.CheckImageAdapter;
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
import com.sangsolutions.sang.Database.Payment_Receipt_class;
import com.sangsolutions.sang.Fragment.SalesPurchaseFragment.Sale_Purchase_FragmentDirections;
import com.sangsolutions.sang.Home;
import com.sangsolutions.sang.R;
import com.sangsolutions.sang.Tools;
import com.sangsolutions.sang.URLs;
import com.sangsolutions.sang.databinding.CameraLayoutBinding;
import com.sangsolutions.sang.databinding.FragmentPaymentReceiptBinding;
import com.sangsolutions.sang.databinding.InvoiceDialogeLayoutBinding;
import com.sangsolutions.sang.databinding.ViewImageBinding;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import io.fotoapparat.Fotoapparat;
import io.fotoapparat.result.BitmapPhoto;
import io.fotoapparat.result.PhotoResult;
import io.fotoapparat.result.WhenDoneListener;

public class PaymentReceiptFragment extends Fragment {
    FragmentPaymentReceiptBinding binding;
    NavController navController;
    int iDocType,iTransId,iTransID;
    boolean EditMode,FromInvoice;
    String StringDate;
    SimpleDateFormat df;
    DatabaseHelper helper;
    String userIdS=null;
    String toolTitle;
    String docNo;
    AlertDialog alertDialog;
    AlertDialog alertDialogProgress;

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
    List<Invoice>invoiceEditList;

    List<Invoice>invoiceSelectedList;
    InvoiceSelectedAdapter invoiceSelectedAdapter;

    int iCustomer=0,iTagDetail,iBank,iPaymentMethod;
    AlertDialog alertDialog_invoice;

    boolean selectionActive = false;

    InvoiceDialogeLayoutBinding bindingInvoice;


    List<TagDetails> tagList;
    TagDetailsAdapter tagDetailsAdapter;
    HashMap<Integer, Integer> hashMapHeader;
    List<Integer> headerListTags;
    String userCode;

    DecimalFormat decimalFormat ;

    List<String> imageList;
    CheckImageAdapter imageAdapter;

    String Image,image;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       binding=FragmentPaymentReceiptBinding.inflate(getLayoutInflater());
       navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        try {
            ((Home)getActivity()).setDrawerEnabled(false);
        }catch (Exception e){
            e.printStackTrace();
        }
        df = new SimpleDateFormat("dd-MM-yyyy");
        helper=new DatabaseHelper(requireContext());
        decimalFormat= new DecimalFormat("0.00");

        assert getArguments() != null;
        iDocType = PaymentReceiptFragmentArgs.fromBundle(getArguments()).getIDocType();
        iTransId = PaymentReceiptFragmentArgs.fromBundle(getArguments()).getITransId();
        EditMode = PaymentReceiptFragmentArgs.fromBundle(getArguments()).getEditMode();
        FromInvoice = PaymentReceiptFragmentArgs.fromBundle(getArguments()).getFromInvoice();
        Log.d("lllllp","iDocType "+iDocType+" "+"iTransId "+iTransId+" "+"editMode "+ EditMode +""+FromInvoice);




        AlertDialog.Builder builderAlert=new AlertDialog.Builder(requireActivity());
        View viewAlert=LayoutInflater.from(requireActivity()).inflate(R.layout.progress_bar,null,false);
        builderAlert.setView(viewAlert);
        builderAlert.setCancelable(false);
        alertDialogProgress = builderAlert.create();

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
        invoiceEditList=new ArrayList<>();

        invoiceSelectedList=new ArrayList<>();
        invoiceSelectedAdapter=new InvoiceSelectedAdapter(requireContext(),invoiceSelectedList,invoiceSecondList);

        tagList =new ArrayList<>();
        tagDetailsAdapter =new TagDetailsAdapter(requireActivity(),tagList);

        imageList =new ArrayList<>();
        imageAdapter=new CheckImageAdapter(requireContext(), imageList);
        binding.rvImage.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL,false));
        binding.rvImage.setAdapter(imageAdapter);

        hashMapHeader=new HashMap<>();


        Cursor cursor_userId=helper.getUserId();
        if(cursor_userId!=null &&cursor_userId.moveToFirst()) {
            userIdS = cursor_userId.getString(cursor_userId.getColumnIndex("user_Id"));
        }
        cursorTagNumber =helper.getTotalTagNumber();
        if(cursorTagNumber!=null) {
            tagTotalNumber = cursorTagNumber.getCount();
        }

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
        binding.imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA}, 100);
                } else {
                    addCaptureImage();

                }
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

                if(Tools.isConnected(requireActivity())){
                    if(!FromInvoice) {
                        invoiceDialogue();
                    }
                }else {
                    Toast.makeText(requireActivity(), "You are offline!!", Toast.LENGTH_SHORT).show();
                }
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
                if(EditMode) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                    builder.setTitle("delete!")
                            .setMessage("Do you want to delete all ?")
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

        /////


        imageAdapter.setOnClickListener(new CheckImageAdapter.OnClickListener() {
            @Override
            public void onImageClickListener(String photo, int position) {
                ViewImageBinding viewImageBinding=ViewImageBinding.inflate(getLayoutInflater());
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(),android.R.style.Theme_Light_NoTitleBar_Fullscreen).setView(viewImageBinding.getRoot());
                AlertDialog dialogue = builder.create();
                dialogue.show();
                Picasso.get().load(photo).into(viewImageBinding.viewImage);
//                PhotoViewAttacher photoViewAttacher = new PhotoViewAttacher(viewImageBinding.viewImage);
//                photoViewAttacher.canZoom();
            }
        });



       return binding.getRoot();
    }

    private void deleteAllFromDB() {
        if(helper.deletePayRec_Header(iTransId,iDocType,docNo)){
            if(helper.delete_PayRec_Body(iDocType,iTransId)){
                Log.d("responsePost ", "successfully");
                NavDirections actions = PaymentReceiptFragmentDirections.actionPaymentReceiptFragmentToPaymentReceiptHistoryFragment(toolTitle,iDocType);
                navController.navigate(actions);
                Toast.makeText(requireContext(), "Deleted", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void addCaptureImage() {

        CameraLayoutBinding camBinding=CameraLayoutBinding.inflate(getLayoutInflater());
        Fotoapparat fotoapparat = new Fotoapparat(requireContext(), camBinding.cameraView);
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext()).setView(camBinding.getRoot());
        AlertDialog dialogue = builder.create();
        dialogue.show();
        fotoapparat.start();
        camBinding.cancelImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogue.dismiss();
                fotoapparat.stop();
            }
        });

        camBinding.captureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhotoResult photoResult = fotoapparat.takePicture();
                photoResult.toBitmap().whenDone(new WhenDoneListener<BitmapPhoto>() {
                    @Override
                    public void whenDone(BitmapPhoto bitmapPhoto) {
                        if (bitmapPhoto != null) {
                            File f=new File(Tools.savePhoto(requireContext(),photoResult));
                            imageList.add(Uri.fromFile(f)+"");
                            Log.d("ImageList",Tools.savePhoto(requireContext(),photoResult));
                            imageAdapter.notifyDataSetChanged();
                            fotoapparat.stop();
                            dialogue.dismiss();
                        }
                    }
                });
            }
        });
    }


    private void deleteAllFromAPI() {
        AndroidNetworking.get("http://"+ new Tools().getIP(requireActivity())+  URLs.DeleteTransReceipt_Payment)
                .addQueryParameter("iTransId", String.valueOf(iTransId))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("response_delete",response);
                        NavDirections actions = PaymentReceiptFragmentDirections.actionPaymentReceiptFragmentToPaymentReceiptHistoryFragment(toolTitle,iDocType);
                        navController.navigate(actions);
                        Toast.makeText(requireContext(), "Deleted!!", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("response_delete",anError.toString()+ anError.getErrorDetail()+anError.getErrorBody());

                    }
                });

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
                        SAVE();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();


    }

    private void SAVE() {
        if (!binding.customer.getText().toString().equals("") && helper.getCustomerNameValid(binding.customer.getText().toString().trim())) {
            Double amount = 0.0;
            if(binding.linearInvoice.getVisibility()==View.VISIBLE){
                for(int i=0;i<invoiceSelectedList.size();i++){
                    amount+=invoiceSelectedList.get(i).getAmount();
                }
                if(binding.amount.getText().equals("") || Double.parseDouble(binding.amount.getText().toString().trim())!=amount){
                    binding.amount.setError("amount incorrect");
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

    private void fieldsChecking(int iPaymentMethod) {
        if(iPaymentMethod==2){
            if(!binding.bankName.getText().toString().equals("") && iBank!=0){
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
                            for(int j=0;j<headerListTags.size();j++){
                                if(autoText_H_list.get(j).getText().toString().equals("")){
                                    hashMapHeader.remove(headerListTags.get(j));
                                }
                            }
                        uploadToJson();
                        }
                        for (int i = 0; i < mandatoryList_H.size(); i++) {
                        if (!mandatoryList_H.get(i).getText().toString().equals("")) {
                        if (i + 1 == mandatoryList_H.size() && flag) {
                            for(int j=0;j<headerListTags.size();j++){
                                if(autoText_H_list.get(j).getText().toString().equals("")){
                                    hashMapHeader.remove(headerListTags.get(j));
                                }
                            }
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

        List<File> file = new ArrayList<>();
        Image = TextUtils.join(",", imageList);

        for (int i = 0; i < imageList.size(); i++) {
                if(imageList.get(i).contains("file://")){
                imageList.set(i,imageList.get(i).substring(7));
                }
                else if(imageList.get(i).contains("http")){
                Bitmap myBitmap = BitmapFactory.decodeFile(imageList.get(i));
                String image=Tools.savePhotoURL(requireContext(),myBitmap);
                imageList.set(i, image);
                }

                File file1 = new File(imageList.get(i));
                if (file1.exists()) {
                file.add(file1);
                }

            image = TextUtils.join(",", imageList);
                }

        if(Tools.isConnected(requireContext())){


        String imageName=Tools.getFileList(Image);
            Log.d("jsonObjecMain",Image+"");
        JSONObject jsonObjectMain=new JSONObject();
        try{
            if(docNo.contains("L")){
                iTransId=0;
            }
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
                jsonObjectMain.put("sAttachment",imageName);
            }else if(iPaymentMethod==1){
                jsonObjectMain.put("iBank", 0);
                jsonObjectMain.put("iChequeNo", 0);
                jsonObjectMain.put("sAttachment","");
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

                    if(invoiceSelectedList.size()>0 ) {
                    for (int i = 0; i < invoiceSelectedList.size(); i++) {
                    JSONObject jsonObject = new JSONObject();

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
                    uploadToAPI(jsonObjectMain,file);

        } catch (JSONException e) {

            Log.d("exceptionn",e.getMessage());
            e.printStackTrace();
        }

        }else {
            saveLocally(image);
        }


    }

    private void uploadToAPI(JSONObject jsonObjectMain, List<File> files) {
        Log.d("jsonObjectMain",jsonObjectMain.toString()+" "+files.toString());
                    if(Tools.isConnected(requireActivity())) {
                    alertDialog.show();
                    AndroidNetworking.upload("http://"+ new Tools().getIP(requireActivity())+URLs.Post_Receipt_Payment)
                    .addMultipartParameter("json_content",jsonObjectMain.toString())
                    .setContentType("multipart/form-data")
                    .addMultipartFileList("file",files)
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsString(new StringRequestListener() {
                                @Override
                                public void onResponse(String response) {
                                    Log.d("responsePostt", response);
                                if (response.contains(docNo)) {
                                alertDialog.dismiss();
                                    if(helper.deletePayRec_Header(iTransId,iDocType,docNo)){
                                        if(helper.delete_PayRec_Body(iDocType,iTransId)){
                                            Log.d("responsePost ", "successfully");
                                        }
                                    }
                                Log.d("responsePost_R_P", "successfully");
                                    alertDialog.dismiss();
                                Toast.makeText(requireActivity(), "Posted successfully", Toast.LENGTH_SHORT).show();
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

    }

    private void saveLocally(String file) {

        Log.d("fileee",file+"  "+iPaymentMethod);
        Cursor cursor1=helper.getDataFromPaymentReceipt();
        if(!EditMode) {
            if (cursor1.moveToFirst() && cursor1.getCount() > 0) {
                iTransId = Tools.getNewDocNoLocally(cursor1);
            }
        }

        Payment_Receipt_class PR_class=new Payment_Receipt_class();
        PR_class.setiTransId(iTransId);
        PR_class.setsDocNo(docNo);
        PR_class.setsDate(binding.date.getText().toString());
        PR_class.setiDocType(iDocType);
        PR_class.setiAccount1(iCustomer);
        PR_class.setiAccount2(0);
        PR_class.setsNarration(binding.description.getText().toString());
        PR_class.setProcessTime(DateFormat.format("yyyy-MM-dd HH:mm:ss", new Date())+"");
        PR_class.setStatus(0);
        PR_class.setfAmount(Double.parseDouble(binding.amount.getText().toString()));
        PR_class.setiPaymentMethod(iPaymentMethod);

        if(iPaymentMethod==2) {
            PR_class.setiBank( iBank);
            PR_class.setiChequeNo( Integer.parseInt(binding.checkNo.getText().toString()));
            PR_class.setsAttachment(file);
        }else if(iPaymentMethod==1){
            PR_class.setiBank( 0);
            PR_class.setiChequeNo( 0);
            PR_class.setsAttachment("");
        }
        PR_class.setsChequeDate( binding.checkDate.getText().toString());
        if(helper.deletePayRec_Header(iTransId,iDocType,docNo)) {
            Log.d("deletePayRec_Header",iTransId+" "+iDocType+" "+docNo);
            if (helper.insert_PayRec_Header(PR_class)) {
                Log.d("deletePayRec_r",iTransId+" "+iDocType+" "+docNo);
                InsertBodyPart_DB();
            }
        }


    }

    private void InsertBodyPart_DB() {
        if(helper.delete_PayRec_Body(iDocType,iTransId)) {

                Payment_Receipt_class payRec_classBody = new Payment_Receipt_class();
                for (int j = 1; j <= tagTotalNumber; j++) {
                    if (hashMapHeader.containsKey(j)) {
                        loadDataTags(payRec_classBody, j, hashMapHeader.get(j));
                    } else {
                        loadDataTags(payRec_classBody, j, 0);
                    }
                }
                payRec_classBody.setiRefDocId(0);
                payRec_classBody.setfAmount(0.0);
                payRec_classBody.setiDocType(iDocType);
                payRec_classBody.setsDocNo(docNo);
                payRec_classBody.setiTransId(iTransId);
                if (helper.insert_PayRec_Body(payRec_classBody)) {
                    Log.d("DataBodyInsert", "SUCCESS");
                    Toast.makeText(requireActivity(), "Posted successfully in DB", Toast.LENGTH_SHORT).show();
                    NavDirections actions = PaymentReceiptFragmentDirections.actionPaymentReceiptFragmentToPaymentReceiptHistoryFragment(toolTitle, iDocType);
                    navController.navigate(actions);
                }
        }
    }

    private void loadDataTags(Payment_Receipt_class payRec_classBody, int j, Integer iTag) {
        switch (j){
            case 1:{
                payRec_classBody.setiTag1(iTag);
                break;
            }
            case 2:{
                payRec_classBody.setiTag2(iTag);
                break;
            }
            case 3:{
                payRec_classBody.setiTag3(iTag);
                break;
            }
            case 4:{
                payRec_classBody.setiTag4(iTag);
                break;
            }
            case 5:{
                payRec_classBody.setiTag5(iTag);
                break;
            }
            case 6:{
                payRec_classBody.setiTag6(iTag);
                break;
            } case 7:{
                payRec_classBody.setiTag7(iTag);
                break;
            }
            case 8:{
                payRec_classBody.setiTag8(iTag);
                break;
            }

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
        if(!binding.customer.getText().toString().equals("") && iCustomer!=0)
        {
            AlertDialog.Builder builder=new AlertDialog.Builder(requireContext());
            bindingInvoice=InvoiceDialogeLayoutBinding.inflate(getLayoutInflater());
            bindingInvoice.recyclerViewInvoice.setLayoutManager(new LinearLayoutManager(requireContext()));

            builder.setView(bindingInvoice.getRoot());
            alertDialog_invoice=builder.create();
//            alertDialog_invoice.setCancelable(false);
            bindingInvoice.recyclerViewInvoice.setAdapter(invoiceAdapter);

            alertDialog_invoice.show();
            alertDialogProgress.show();

            API_Invoice();


            invoiceAdapter.setOnClickListener(new InvoiceAdapter.OnClickListener() {
                        @Override
                        public void OnItemClick(Invoice invoice, int position) {
                        if(invoice.getAmount()==0.0){
                        Toast.makeText(requireContext(), "No Amount", Toast.LENGTH_SHORT).show();
                        }
                        else {
                        enableActionMode(position);
                        selectionActive = true;
                        }
                        }
                });

            bindingInvoice.apply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    alertDialog_invoice.dismiss();
                    loadSelectedInvoice();
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
            binding.customer.setError("select Valid customer");
            }
            }

    private void loadSelectedInvoice() {
                        Double totalAmount = 0.0;
                        List<Integer> listSelectedItem = invoiceAdapter.getSelectedItems();
                        for (int i=0;i<listSelectedItem.size();i++) {
                        for (int j = 0; j< invoiceList.size(); j++) {
                        if (listSelectedItem.get(i) == j) {
                        try {
                        if (invoiceSelectedList.size() ==listSelectedItem.size() &&
                                invoiceSelectedList.get(i).getiTransId() == invoiceList.get(j).getiTransId()) {

                                    Double re_amount = invoiceSelectedList.get(i).getAmount() +
                                    invoiceList.get(j).getAmount();
                                    Invoice invoice1 = new Invoice(invoiceList.get(j).getiTransId(),
                                    invoiceList.get(j).getInvDate(),
                                    invoiceList.get(j).getInvNo(),
                                    re_amount,
                                    invoiceList.get(j).getCustomer(),
                                    invoiceList.get(j).getCustomerCode()
                                    );
                                    invoiceSelectedList.set(i, invoice1);
                                    } else {
                                    Invoice invoice = new Invoice(invoiceList.get(j).getiTransId(),
                                    invoiceList.get(j).getInvDate(),
                                    invoiceList.get(j).getInvNo(),
                                    invoiceList.get(j).getAmount(),
                                    invoiceList.get(j).getCustomer(),
                                    invoiceList.get(j).getCustomerCode()
                                    );
                                    invoiceSelectedList.add(invoice);
                            }
                            }catch (Exception e){
                            Log.d("exeception",e.getMessage()+e.getLocalizedMessage()+"size");
                            }
                            invoiceSelectedAdapter.notifyDataSetChanged();
                            }
                            }
            if (i + 1 == listSelectedItem.size()) {
                binding.linearInvoice.setVisibility(View.VISIBLE);
                binding.recycleInvoiceHome.setLayoutManager(new LinearLayoutManager(requireContext()));
                binding.recycleInvoiceHome.setAdapter(invoiceSelectedAdapter);

                        for(int a=0;a<invoiceSelectedList.size();a++) {
                        for (int b=a+1;b<invoiceSelectedList.size();b++){
                        if(invoiceSelectedList.get(a).getiTransId()==invoiceSelectedList.get(b).getiTransId()){
                            invoiceSelectedList.get(a).setAmount(invoiceSelectedList.get(a).getAmount()+
                                    invoiceSelectedList.get(b).getAmount());
                            invoiceSelectedList.remove(b);
                            invoiceSelectedAdapter.notifyDataSetChanged();
                            }
                            }
                            totalAmount += invoiceSelectedList.get(a).getAmount();
                            }
                            binding.amount.setText(decimalFormat.format(totalAmount));

                            invoiceSelectedAdapter.setOnClickListener(new InvoiceSelectedAdapter.OnClickListener() {
                            @Override
                            public void onItemClick(List<Invoice> list, int position) {
                            if(list.size()==0){
                            binding.linearInvoice.setVisibility(View.GONE);
                            binding.amount.setText("");
                            }else {
                            onItemClickInvoice(list,position);
                        }
                        }
                        });
            }
            }
            }

    private void onItemClickInvoice(List<Invoice> list, int position) {
            Double total_Amount = 0.0;
            for (int i=0;i<list.size();i++) {
            total_Amount += list.get(i).getAmount();
            Log.d("total_Amount",total_Amount+"  "+list.get(i).getAmount());
            }
            binding.amount.setText(decimalFormat.format(total_Amount));
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
        int itype=0;
        if(iDocType==15){
            itype=10;
        }else if(iDocType==25) {
            itype=20;
        }else if(iDocType==17) {
            itype=16;
        }
        Log.d("icustomer",iCustomer+" "+iDocType);
                AndroidNetworking.get("http://"+ new Tools().getIP(requireActivity())+ URLs.GetInvoiceList)
                .addQueryParameter("iCustomer",String.valueOf(iCustomer))
                .addQueryParameter("iType",String.valueOf(itype))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                        @Override
                        public void onResponse(JSONArray response) {
                        Log.d("responseInvoice",response.toString());
                        load_API_Invoice(response);
                            alertDialogProgress.dismiss();
                        }
                        @Override
                        public void onError(ANError anError) {
                        Log.d("responseInvoice",anError.toString());
                            alertDialogProgress.dismiss();
                        }
                        });
                        }

    private void load_API_Invoice(JSONArray response) {
        invoiceList.clear();
        List<Integer>transId=new ArrayList<>();
        Log.d("invoiceSecondList4",invoiceEditList.size()+" j ");
            try {
                            JSONArray jsonArray = new JSONArray(response.toString());
                            for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Invoice invoice = new Invoice(
                            jsonObject.getInt(Invoice.I_TRANS_ID),
                            jsonObject.getString(Invoice.INV_DATE),
                            jsonObject.getString(Invoice.INV_NO),
                            jsonObject.getDouble(Invoice.AMOUNT),
                            jsonObject.getString(Invoice.CUSTOMER),
                            jsonObject.getString(Invoice.CUSTOMER_CODE));
                    invoiceList.add(invoice);
                    invoiceSecondList.add(invoice);
                    invoiceAdapter.notifyDataSetChanged();
                    Log.d("invoiceSecondList",invoiceSecondList.get(i).getAmount()+"");
                    if (EditMode) {
                        for (int sel = 0; sel < invoiceEditList.size(); sel++) {
                                Log.d("invoiceSecondList3",invoiceEditList.size()+" j "+invoiceEditList.get(sel).getAmount());
                                if (invoiceEditList.get(sel).getiTransId() == jsonObject.getInt(Invoice.I_TRANS_ID)) {
                                Double amount = jsonObject.getDouble(Invoice.AMOUNT) +
                                        invoiceEditList.get(sel).getAmount();
                                        Invoice invoice1 = new Invoice(
                                        jsonObject.getInt(Invoice.I_TRANS_ID),
                                        jsonObject.getString(Invoice.INV_DATE),
                                        jsonObject.getString(Invoice.INV_NO),
                                        amount,
                                        jsonObject.getString(Invoice.CUSTOMER),
                                        jsonObject.getString(Invoice.CUSTOMER_CODE));
                                invoiceList.set(i, invoice1);
                                invoiceSecondList.set(i, invoice1);
                                Log.d("invoiceSecondList11",invoiceSecondList.get(i).getAmount()+"");
                                invoiceAdapter.notifyDataSetChanged();
                            }
                            }

                        if(i+1==jsonArray.length()){
                            for (int edit=0;edit<invoiceEditList.size();edit++){
                                boolean flag=false;
                                for (int jArry=0;jArry<jsonArray.length();jArry++){
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(jArry);
                                    Log.d("invoiceEditList2",jsonObject1.getInt(Invoice.I_TRANS_ID)+"  "+
                                            invoiceEditList.get(edit).getiTransId());
                                    if(jsonObject1.getInt(Invoice.I_TRANS_ID)==invoiceEditList.get(edit).getiTransId()){
                                        flag=true;
                                    }
                                    }
                                    if(!flag){
                                    transId.add(invoiceEditList.get(edit).getiTransId());
                                    }
                                    }
                                    }
                        for (int trans_id=0;trans_id<transId.size();trans_id++){
                            for (int inVedit=0;inVedit<invoiceEditList.size();inVedit++){
                                if(invoiceEditList.get(inVedit).getiTransId()==transId.get(trans_id)){
                                    invoiceList.add(invoiceEditList.get(inVedit));
                                    invoiceSecondList.add(invoiceEditList.get(inVedit));
                                    Log.d("invoiceSecondList3",invoiceSecondList.get(i).getAmount()+"");
                                    invoiceAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    }


                    if(i+1==jsonArray.length()){
                        for (int i1=0;i1<invoiceList.size();i1++){
                            for (int j = 0; j < invoiceSelectedList.size(); j++) {
                                if (invoiceSelectedList.get(j).getiTransId() == invoiceList.get(i1).getiTransId()) {
                                    double remain_amount = invoiceList.get(i1).getAmount() -
                                            invoiceSelectedList.get(j).getAmount();
                                            Invoice invoice1 = new Invoice(
                                            invoiceList.get(i1).getiTransId(),
                                            invoiceList.get(i1).getInvDate(),
                                            invoiceList.get(i1).getInvNo(),
                                            remain_amount,
                                            invoiceList.get(i1).getCustomer(),
                                            invoiceList.get(i1).getCustomerCode());
                                            invoiceList.set(i1, invoice1);
                                            invoiceAdapter.notifyDataSetChanged();
                                    }
                                    invoiceAdapter.notifyDataSetChanged();
                            }
                            }
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
                        if(iCustomer!=0){
                            if(iCustomer!=customer.getiId()){
                                binding.linearInvoice.setVisibility(View.GONE);
                                    EditMode=false;
                                    invoiceEditList.clear();
                                    invoiceList.clear();
                                    invoiceSelectedList.clear();
                                    invoiceSecondList.clear();
                                    binding.amount.setText("");
                                    }
                                    }
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

        if (iDocType == 15) {
            binding.customer.setHint("Select Vendor");
            toolTitle = "Payment History";
        } else if (iDocType == 25) {
            toolTitle = "Receipt History";
        }
        else if (iDocType == 17) {
            toolTitle = "Receipt AdvanceInv History";
        }
        Cursor cursor = helper.getUserCode(userIdS);
        if (cursor.moveToFirst() && cursor.getCount() > 0) {
            userCode = cursor.getString(cursor.getColumnIndex(User.USER_CODE));
        }

        if(Tools.isConnected(requireActivity())) {
                if (EditMode) {
                    if(iDocType==17  && FromInvoice){
                        loadingReceiptFromInvoice();
                    }
                    else {
                        EditValueFromAPI();
                    }
                }
                else {
                    createNewDocument();

            }

        }
        else {
            Toast.makeText(requireActivity(), "NO Internet", Toast.LENGTH_SHORT).show();
            alertDialog.dismiss();
            if(EditMode){
                editfromlocaldb();
            }else {
                Cursor cursor1=helper.getDataFrom_PayRec_Header();
//                int count=cursor1.getCount()+1;

                if(cursor1.getCount()>0) {
                    int count= Tools.getNewDocNoLocally(cursor1);
                    Log.d("statuss",count+"");
                    docNo = "L-"+userCode + "-" + DateFormat.format("MM", new Date()) + "-"  + count;
                }else {
                    docNo ="L-"+ userCode + "-" + DateFormat.format("MM", new Date() )+ "-"  + 1;

                }
            }

            binding.docNo.setText(docNo);

        }
    }

    private void createNewDocument() {
        AndroidNetworking.get("http://"+ new Tools().getIP(requireActivity()) +URLs.GetNextDocNo)
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
                            if(jsonArray.length()>0) {
                                JSONObject jsonObject=jsonArray.getJSONObject(0);
                                docNo=jsonObject.getString("Column1");
                            }
                            binding.docNo.setText(docNo);

                            if(iDocType==17 && FromInvoice){
                                loadingNewReceiptFromInvoice();


                            }else {
                                alertDialog.dismiss();
                            }
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





    private void loadingNewReceiptFromInvoice() {

        AndroidNetworking.get("http://" + new Tools().getIP(requireActivity())+ URLs.GetTransDetails)
                .addQueryParameter("iTransId",String.valueOf(iTransId))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Response_loadEditValue",response.toString());
                        loadAPIValue_for_Edit_from_Invoice(response,iTransId);
                        alertDialog.dismiss();
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("Response_loadEditValue",anError.toString());
                        alertDialog.dismiss();
                        Toast.makeText(requireActivity(), anError.getMessage(), Toast.LENGTH_SHORT).show();
                        NavDirections actions = Sale_Purchase_FragmentDirections.actionSalePurchaseFragmentToSalesPurchaseHistoryFragment(toolTitle).setIDocType(iDocType);
                        navController.navigate(actions);
                    }
                });
    }

    private void loadAPIValue_for_Edit_from_Invoice(JSONObject response, int iRefId) {
        try {
            JSONArray jsonArray = new JSONArray(response.getString("Table"));
            Log.d("HeadArray",jsonArray.length()+"");
            JSONArray jsonArray1=new JSONArray(response.getString("Table1"));
            Log.d("BodyArray1",jsonArray1.length()+"");

            for (int i=0;i<jsonArray.length();i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

//                binding.docNo.setText(jsonObject.getString("sDocNo"));
//                docNo=jsonObject.getString("sDocNo");
                binding.date.setText(jsonObject.getString("sDate"));
                iCustomer = jsonObject.getInt("iAccount1");
                binding.customer.setText(jsonObject.getString("sAccount1"));
//                binding.description.setText(jsonObject.getString("sNarration"));

                if(EditMode){
                    iTransId=iTransID;
                }
                else {
                    iTransId =0;
                }

                binding.date.setEnabled(false);
                binding.customer.setEnabled(false);
            }
            for (int j=0;j<jsonArray1.length();j++) {
                JSONObject jsonObjectInner = jsonArray1.getJSONObject(j);
                binding.amount.setText(jsonObjectInner.getString("fNet"));
                binding.amount.setEnabled(false);

                Log.d("headertags", headerListTags.size() + "");
                for (int k = 0; k < headerListTags.size(); k++) {
                    Log.d("headertags", headerListTags.size() + " " +
                            k + " " + headerListTags.get(k) + " " + jsonObjectInner.getInt("iTag" + headerListTags.get(k))
                            + " ");
                    hashMapHeader.put(headerListTags.get(k), jsonObjectInner.getInt("iTag" + headerListTags.get(k)));
                    autoText_H_list.get(k).setText(jsonObjectInner.getString("sTag" + headerListTags.get(k)));
                    autoText_H_list.get(k).setEnabled(false);
                }
                Invoice invoice=new Invoice();
                invoice.setiTransId(iRefId);
                invoice.setAmount(jsonObjectInner.getDouble("fNet"));
                invoiceSelectedList.add(invoice);
            }
        }catch (Exception e){
        e.printStackTrace();
        }
    }

    private void loadingReceiptFromInvoice() {
        AndroidNetworking.get("http://"+ new Tools().getIP(requireActivity()) +URLs.GetTransReceipt_PaymentDetails_RefId)
                .addQueryParameter("iRefId", String.valueOf(iTransId))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("responseHistoryRefId", response.toString());
                        try {
                            JSONArray jsonArray = new JSONArray(response.getString("Table"));
                            JSONArray jsonArray1=new JSONArray(response.getString("Table1"));
                            JSONArray jsonArray2=new JSONArray(response.getString("Table2"));
                            if(jsonArray.length()==0){
                                createNewDocument();
                            }else {
//                                iRefId=iTransId;
                                String sAttachment=null;
                                for (int i=0;i<jsonArray.length();i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    binding.docNo.setText(jsonObject.getString("sDocNo"));
                                    docNo=jsonObject.getString("sDocNo");
                                    binding.description.setText(jsonObject.getString("sNarration"));
                                    iPaymentMethod = jsonObject.getInt("iPaymentMethod");
                                    if(iPaymentMethod==2) {
                                        binding.paymentSpinner.setSelection(1);
                                        iBank = jsonObject.getInt("iBank");
                                        binding.bankName.setText(jsonObject.getString("sBank"));
                                        binding.checkNo.setText(jsonObject.getString("iChequeNo"));
                                        binding.checkDate.setText(jsonObject.getString("sChequeDate"));
                                        sAttachment=jsonObject.getString("sAttachment");
                                        Log.d("sAttachments",sAttachment);
                                    }
                                    iTransID=jsonObject.getInt("iTransId");
                                }
                                if(iPaymentMethod==2 && !sAttachment.equals("")){
                                    Log.d("resultArray2",jsonArray2.length()+" "+iPaymentMethod);
                                    for(int k=0;k<jsonArray2.length();k++){
                                        JSONObject jsonObject2 = jsonArray2.getJSONObject(k);
                                        String sAttachment2=jsonObject2.getString("sAttachment");
                                        Log.d("sAttachments",sAttachment2);
                                        imageList.add(sAttachment2);
                                        imageAdapter.notifyDataSetChanged();
                                    }
                                }
                                loadingNewReceiptFromInvoice();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("responseHistoryRefId", anError.toString()+anError.getErrorDetail()+anError.getErrorBody());

                    }
                });

    }

    private void editfromlocaldb() {
        Cursor cursorEdit_H = helper.getEditValuesHeaderPayRec(iTransId, iDocType);
        if (cursorEdit_H.moveToFirst() && cursorEdit_H.getCount() > 0) {
            docNo = cursorEdit_H.getString(cursorEdit_H.getColumnIndex(Payment_Receipt_class.S_DOC_NO));
            binding.docNo.setText(docNo);
            binding.date.setText(cursorEdit_H.getString(cursorEdit_H.getColumnIndex(Payment_Receipt_class.S_DATE)));
            iCustomer = cursorEdit_H.getInt(cursorEdit_H.getColumnIndex(Payment_Receipt_class.I_ACCOUNT_1));
            binding.customer.setText(helper.getCustomerUsingId(iCustomer));
            binding.description.setText(cursorEdit_H.getString(cursorEdit_H.getColumnIndex(Payment_Receipt_class.S_NARRATION)));
            binding.amount.setText(cursorEdit_H.getString(cursorEdit_H.getColumnIndex(Payment_Receipt_class.F_AMOUNT)));
            iPaymentMethod = cursorEdit_H.getInt(cursorEdit_H.getColumnIndex(Payment_Receipt_class.I_PAYMENT_METHOD));
            if (iPaymentMethod == 2) {
            binding.paymentSpinner.setSelection(1);
            iBank = cursorEdit_H.getInt(cursorEdit_H.getColumnIndex(Payment_Receipt_class.I_BANK));
            binding.bankName.setText(helper.getBankUsingId(iBank));
            binding.checkNo.setText(cursorEdit_H.getString(cursorEdit_H.getColumnIndex(Payment_Receipt_class.I_CHEQUE_NO)));
            binding.checkDate.setText(cursorEdit_H.getString(cursorEdit_H.getColumnIndex(Payment_Receipt_class.S_CHEQUE_DATE)));
            changeStatus(iTransId,docNo,1);

            String attachments=cursorEdit_H.getString(cursorEdit_H.getColumnIndex(Payment_Receipt_class.S_ATTACHMENT));
                List<String> imgList = new ArrayList<>(Arrays.asList(attachments.split(",")));
                for (int i=0;i<imgList.size();i++){
                File f=new File(imgList.get(i)+"");
                imageList.add(Uri.fromFile(f)+"");
                imageAdapter.notifyDataSetChanged();
                }
        }
    }
        Cursor cursorEdit_B=helper.getEditValuesBodyP_ayRec(iTransId,iDocType);

        if(cursorEdit_B.moveToFirst() && cursorEdit_B.getCount()>0) {
            for (int i = 0; i < cursorEdit_B.getCount(); i++) {
                for (int k = 0; k < headerListTags.size(); k++) {
                    int tagDetails=cursorEdit_B.getInt(cursorEdit_B.getColumnIndex("iTag"+ headerListTags.get(k)));
                    hashMapHeader.put(headerListTags.get(k),tagDetails);
                    Cursor tagNameCursor=helper.getTagName(headerListTags.get(k),tagDetails);
                    Log.d("statuss",tagDetails+" ");
                    if(tagDetails!=0) {
                        autoText_H_list.get(k).setText(tagNameCursor.getString(tagNameCursor.getColumnIndex(TagDetails.S_NAME)));
                    }else {
                        autoText_H_list.get(k).setText("");
                    }
                    }
                cursorEdit_B.moveToNext();
            }
        }
    }

    private void changeStatus(int iTransId, String docNo, int iStatus) {
        if(helper.changeStatus_PayRec(iTransId,docNo,iStatus)){
            Log.d("statusChange","successfully");
        }
    }

    private void EditValueFromAPI() {
        if(Tools.isConnected(requireContext())){
                            AndroidNetworking.get("http://"+ new Tools().getIP(requireActivity()) +URLs.GetTransReceipt_PaymentDetails)
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
            String sAttachment = null;
            JSONArray jsonArray=new JSONArray(response.getString("Table"));
            JSONArray jsonArray1=new JSONArray(response.getString("Table1"));
            JSONArray jsonArray2=new JSONArray(response.getString("Table2"));

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
                    if(iPaymentMethod==2) {
                    binding.paymentSpinner.setSelection(1);
                    iBank = jsonObject.getInt("iBank");
                    binding.bankName.setText(jsonObject.getString("sBank"));
                    binding.checkNo.setText(jsonObject.getString("iChequeNo"));
                    binding.checkDate.setText(jsonObject.getString("sChequeDate"));
                    sAttachment=jsonObject.getString("sAttachment");
                    Log.d("sAttachments",sAttachment);
                    }
                    }

            if(iPaymentMethod==2 && !sAttachment.equals("")){
                    Log.d("resultArray2",jsonArray2.length()+" "+iPaymentMethod);
                    for(int k=0;k<jsonArray2.length();k++){
                    JSONObject jsonObject2 = jsonArray2.getJSONObject(k);
                    String sAttachment2=jsonObject2.getString("sAttachment");
                    Log.d("sAttachments",sAttachment2);
                    imageList.add(sAttachment2);
                    imageAdapter.notifyDataSetChanged();
                    }
                    }



            for (int j=0;j<jsonArray1.length();j++) {
                    JSONObject jsonObjectInner = jsonArray1.getJSONObject(j);
                    for (int k = 0; k < headerListTags.size(); k++) {
                    hashMapHeader.put(headerListTags.get(k), jsonObjectInner.getInt("iTag" + headerListTags.get(k)));
                    autoText_H_list.get(k).setText(jsonObjectInner.getString("sTag" + headerListTags.get(k)));
                    }
                    Invoice invoice=new Invoice();
                    Invoice invoice1 = new Invoice();

                if(jsonObjectInner.getInt("iRefDocId")!=0) {
                    invoice.setiTransId(jsonObjectInner.getInt("iRefDocId"));
                    invoice.setAmount(jsonObjectInner.getDouble("fAmount"));
                    invoice.setInvDate(jsonObjectInner.getString("InvDate"));
                    invoice.setInvNo(jsonObjectInner.getString("InvNo"));

                    invoice1.setiTransId(jsonObjectInner.getInt("iRefDocId"));
                    invoice1.setAmount(jsonObjectInner.getDouble("fAmount"));
                    invoice1.setInvDate(jsonObjectInner.getString("InvDate"));
                    invoice1.setInvNo(jsonObjectInner.getString("InvNo"));
                    Log.d("invoice",jsonObjectInner.getString("iRefDocId"));
                    if(jsonObjectInner.getInt("iRefDocId")!=0){
                        binding.linearInvoice.setVisibility(View.VISIBLE);
                    }
                    invoiceSelectedList.add(invoice);
                    invoiceEditList.add(invoice1);
                    Log.d("invoice",invoiceEditList.get(j).getAmount()+"");
                    invoiceSelectedAdapter.notifyDataSetChanged();
                }
                if(j+1==jsonArray1.length()) {
                                binding.recycleInvoiceHome.setLayoutManager(new LinearLayoutManager(requireContext()));
                                binding.recycleInvoiceHome.setAdapter(invoiceSelectedAdapter);
                                invoiceSelectedAdapter.setOnClickListener(new InvoiceSelectedAdapter.OnClickListener() {
                                @Override
                                public void onItemClick(List<Invoice> list, int position) {
                                if(list.size()==0){
                                binding.linearInvoice.setVisibility(View.GONE);
                                binding.amount.setText("");
                                }else {
                                    onItemClickInvoice(list,position);
                                }
                                }
                                });
                                }

            }

                API_Invoice();

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
                    if   (cursor.getCount() > 0) {
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
                            if(iTagPosition.equals("Header"))
                            {
                                hashMapHeader.put(iTag,iTagDetail);
                            }
                            autocompleteView.dismissDropDown();
                            }
                            });
                    }
                    }
                    }
                    else
                    {
                    tagList.clear();
                    }
                    }
}
