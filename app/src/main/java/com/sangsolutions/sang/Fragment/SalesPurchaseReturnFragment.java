package com.sangsolutions.sang.Fragment;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.JsonToken;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
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
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.gson.JsonArray;
import com.sangsolutions.sang.Adapter.BodyAdapter.BodyPart;
import com.sangsolutions.sang.Adapter.BodyAdapter.BodyPartAdapter;
import com.sangsolutions.sang.Adapter.Customer.Customer;
import com.sangsolutions.sang.Adapter.Customer.CustomerAdapter;
import com.sangsolutions.sang.Adapter.Products.Products;
import com.sangsolutions.sang.Adapter.Products.ProductsAdapter;
import com.sangsolutions.sang.Adapter.TagDetailsAdapter.TagDetails;
import com.sangsolutions.sang.Adapter.TagDetailsAdapter.TagDetailsAdapter;
import com.sangsolutions.sang.Adapter.User;
import com.sangsolutions.sang.Database.DatabaseHelper;
import com.sangsolutions.sang.R;
import com.sangsolutions.sang.Tools;
import com.sangsolutions.sang.URLs;
import com.sangsolutions.sang.databinding.FragmentSalesPurchaseReturnBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class SalesPurchaseReturnFragment extends Fragment {

    FragmentSalesPurchaseReturnBinding binding;
    int iDocType,iTransId;
    boolean EditMode;
    String StringDate;
    String docNo;
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

    String token="i";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding=FragmentSalesPurchaseReturnBinding.inflate(getLayoutInflater());
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
        ///////////////////////////////////////////











        //////////////////////////////////////////

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


        return binding.getRoot();
    }

    private void load(JSONObject response) {

    }


    private void initialValueSettingHeader() {
        alertDialog.show();
        numberOfLinesH =0;
        numberOfLinesB =0;
        StringDate=df.format(new Date());
        binding.date.setText(StringDate);
        if (iDocType == 1) {
            binding.customer.setHint("Select Vendor");
            toolTitle = "Purchase Return History";
        } else {
            toolTitle = "Sale Return History";
        }

        if(Tools.isConnected(requireActivity())) {
            if (EditMode) {
                EditValueFromAPI();
            } else {
                Cursor cursor = helper.getUserCode(userIdS);
                if (cursor!=null) {
                    userCode = cursor.getString(cursor.getColumnIndex(User.USER_CODE));
                }


                ///////

                AndroidNetworking.get("http://" + URLs.GetTransReturnSummary)
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
//                                    docNo = userCode + "-" + DateFormat.format("MM", new Date()) + "-" + "000" + Tools.getDocNo(response);
                                    docNo="ooo";
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
                                NavDirections actions = SalesPurchaseReturnFragmentDirections
                                        .actionSalesPurchaseReturnFragmentToSalesPurchaseReturnHistoryFragment(toolTitle,iDocType);
                                navController.navigate(actions);
                            }
                        });
            }
        }else {
            alertDialog.dismiss();
            Toast.makeText(requireActivity(),"No Internet", Toast.LENGTH_SHORT).show();
            NavDirections actions = SalesPurchaseReturnFragmentDirections
                    .actionSalesPurchaseReturnFragmentToSalesPurchaseReturnHistoryFragment(toolTitle,iDocType);
            navController.navigate(actions);
        }

    }

    private void EditValueFromAPI() {

    }
}


















///////////////////////////////
//AndroidNetworking.post("http://185.151.4.167/focus/token")
//        .addBodyParameter("username", "sa")
//        .addBodyParameter("password", "123456")
//        .addBodyParameter("grant_type", "password")
//        .setPriority(Priority.MEDIUM)
//        .build()
//        .getAsJSONObject(new JSONObjectRequestListener() {
//@Override
//public void onResponse(JSONObject response) {
//        Log.d("resultt",response.toString());
//        try {
//        JSONObject jsonObject=new JSONObject(response.toString());
//        token=jsonObject.getString("access_token");
//        Log.d("resultt",token);
//        AndroidNetworking.get("http://185.151.4.167/focus/api/Data/GetResource1")
//        .addHeaders("Authorization","Bearer "+token)
//        .setPriority(Priority.MEDIUM)
//        .build()
//        .getAsString(new StringRequestListener() {
//@Override
//public void onResponse(String response) {
//        Log.d("resultt",response);
//        }
//
//@Override
//public void onError(ANError anError) {
//        Log.d("resultt",anError.toString());
//        }
//        });
//
//
//        } catch (JSONException e) {
//        e.printStackTrace();
//        }
//        }
//@Override
//public void onError(ANError anError) {
//        Log.d("responseTokener",anError.toString());
//        }
//        });
