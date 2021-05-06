package com.sangsolutions.sang.Fragment.MasterAccountFragment;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.sangsolutions.sang.Adapter.UnitAdapter;
import com.sangsolutions.sang.Database.CustomerMasterClass;
import com.sangsolutions.sang.Database.DatabaseHelper;
import com.sangsolutions.sang.Home;
import com.sangsolutions.sang.R;
import com.sangsolutions.sang.Tools;
import com.sangsolutions.sang.URLs;
import com.sangsolutions.sang.databinding.FragmentMasterBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class MasterFragment extends Fragment {
   FragmentMasterBinding binding;
    DatabaseHelper helper;
    int iType=0,iId=0;
    int userId;
    NavController navController;
    boolean EditMode;
    AlertDialog alertDialog;
    boolean local=false;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding= FragmentMasterBinding.inflate(getLayoutInflater());
        helper=new DatabaseHelper(requireContext());
        try {
            ((Home)getActivity()).setDrawerEnabled(false);
        }catch (Exception e){
            e.printStackTrace();
        }
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);

        assert getArguments() != null;
        iId = MasterFragmentArgs.fromBundle(getArguments()).getId();
        EditMode = MasterFragmentArgs.fromBundle(getArguments()).getEditMode();
        Log.d("llllll"," "+"iId "+iId+" "+"editMode "+ EditMode +"");

        AlertDialog.Builder builder=new AlertDialog.Builder(requireActivity());
        View view=LayoutInflater.from(requireActivity()).inflate(R.layout.progress_bar,null,false);
        builder.setView(view);
        builder.setCancelable(false);
        alertDialog = builder.create();


        initialValueSettingHeader();

        Cursor cursor_userId=helper.getUserId();
        if(cursor_userId!=null &&cursor_userId.moveToFirst()) {
            userId = cursor_userId.getInt(cursor_userId.getColumnIndex("user_Id"));
        }

        binding.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(requireActivity());
                builder.setTitle("save!")
                        .setMessage("Do you want to save ?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                SAVEmain();
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

    private void SAVEmain() {
        if(!binding.Name.getText().toString().equals("")){
            if(!binding.code.getText().toString().equals("")){
                if(!binding.mobNumber.getText().toString().equals("")){
                    if(!binding.email.getText().toString().equals("")){
                        if(Patterns.EMAIL_ADDRESS.matcher(binding.email.getText().toString()).matches()){
                            save();
                        }else {
                            binding.email.setError("Not Valid");
                        }
                    }else {
                        save();
                    }
                }else {
                    binding.mobNumber.setError("Empty");
                }
            }else {
                binding.code.setError("Not Valid");
            }
        }else {
            binding.Name.setError("Not Valid");
        }
    }

    private void initialValueSettingHeader() {

        List<String> list= Arrays.asList("Vendor","Customer","Vendor/Customer");
        UnitAdapter unitAdapter = new UnitAdapter(list, requireActivity());
        binding.spinner.setAdapter(unitAdapter);

        if(EditMode){
            alertDialog.show();
            if(Tools.isConnected(requireContext())){
                EditValueFromAPI();
            }else {
                alertDialog.dismiss();
                editfromlocaldb();

            }
        }


    }

    private void editfromlocaldb() {
        Cursor cursorEdit = helper.getEditValuesCustomerMaster(iId);

        if (cursorEdit.moveToFirst() && cursorEdit.getCount() > 0) {
            if(cursorEdit.getString(cursorEdit.getColumnIndex(CustomerMasterClass.LOCAL)).equals("1")){
                local=true;
            }

            Log.d("iidd",cursorEdit.getInt(cursorEdit.getColumnIndex(CustomerMasterClass.ID))+"");
            binding.Name.setText(cursorEdit.getString(cursorEdit.getColumnIndex(CustomerMasterClass.NAME)));
            binding.code.setText(cursorEdit.getString(cursorEdit.getColumnIndex(CustomerMasterClass.CODE)));
            binding.altName.setText(cursorEdit.getString(cursorEdit.getColumnIndex(CustomerMasterClass.ALT_NAME)));
            binding.creditDays.setText(cursorEdit.getString(cursorEdit.getColumnIndex(CustomerMasterClass.CREDIT_DAYS)));

            binding.creditAmount.setText(cursorEdit.getString(cursorEdit.getColumnIndex(CustomerMasterClass.CREDIT_AMOUNT)));
            binding.address.setText(cursorEdit.getString(cursorEdit.getColumnIndex(CustomerMasterClass.ADDRESS)));
            binding.city.setText(cursorEdit.getString(cursorEdit.getColumnIndex(CustomerMasterClass.CITY)));
            binding.country.setText(cursorEdit.getString(cursorEdit.getColumnIndex(CustomerMasterClass.COUNTRY)));

            binding.pin.setText(cursorEdit.getString(cursorEdit.getColumnIndex(CustomerMasterClass.PIN_NO)));
            binding.mobNumber.setText(cursorEdit.getString(cursorEdit.getColumnIndex(CustomerMasterClass.MOBILE_NO)));
            binding.PhoneNumber.setText(cursorEdit.getString(cursorEdit.getColumnIndex(CustomerMasterClass.PHONE_NO)));
            binding.fax.setText(cursorEdit.getString(cursorEdit.getColumnIndex(CustomerMasterClass.FAX)));
            binding.webSite.setText(cursorEdit.getString(cursorEdit.getColumnIndex(CustomerMasterClass.WEBSITE)));
            binding.contactPerson.setText(cursorEdit.getString(cursorEdit.getColumnIndex(CustomerMasterClass.CONTACT_PERSON_NO)));

            changeStatus(iId,1);

            iType=cursorEdit.getInt(cursorEdit.getColumnIndex(CustomerMasterClass.I_TYPE));
            if(iType==1){
                binding.spinner.setSelection(0);
            }else if(iType==2){
                binding.spinner.setSelection(1);
            }else if(iType==3){
                binding.spinner.setSelection(2);
            }
        }

    }

    private void changeStatus(int iId, int iStatus) {
        if(helper.changeStatus_CustomerMaster(iId,iStatus)){
            Log.d("statusChange","successfully");
        }
    }

    private void EditValueFromAPI() {
        AndroidNetworking.get("http://" + new Tools().getIP(requireActivity())+ URLs.GetCustomerDetails)
                .addQueryParameter("iId",String.valueOf(iId))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("ResponseEditMaster",response.toString());
                        loadAPIValue_for_Edit(response);

                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("Response_loadEditValue",anError.toString());
                        alertDialog.dismiss();
                        Toast.makeText(requireActivity(), anError.getMessage(), Toast.LENGTH_SHORT).show();
                        NavDirections actions = MasterFragmentDirections.actionMasterFragmentToMasterHistoryFragment();
                        navController.navigate(actions);
                    }
                });
    }

    private void loadAPIValue_for_Edit(JSONObject response) {

        try {
            Log.d("iIdiId","jsonArray.length()"+"");
            JSONArray jsonArray=new JSONArray(response.getString("Table"));
            Log.d("iIdiId",jsonArray.length()+"");
            for (int i=0;i<jsonArray.length();i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Log.d("iIdiId",jsonObject.getInt("iId")+"");
                iId=jsonObject.getInt("iId");
                binding.Name.setText(jsonObject.getString("sName"));
                binding.code.setText(jsonObject.getString("sCode"));
                binding.altName.setText(jsonObject.getString("sAltName"));
                binding.creditDays.setText(jsonObject.getString("iCreditDays"));

                binding.creditAmount.setText(jsonObject.getString("fCreditAmount"));
                binding.address.setText(jsonObject.getString("sAddress"));
                binding.city.setText(jsonObject.getString("sCity"));
                binding.country.setText(jsonObject.getString("sCountry"));

                binding.pin.setText(jsonObject.getString("sPincode"));
                binding.mobNumber.setText(jsonObject.getString("iMobile"));
                binding.PhoneNumber.setText(jsonObject.getString("iPhone"));
                binding.fax.setText(jsonObject.getString("sFax"));
                binding.webSite.setText(jsonObject.getString("sWebsite"));

                binding.contactPerson.setText(jsonObject.getString("sContactPersonNo"));

                iType=jsonObject.getInt("iType");
                if(iType==1){
                    binding.spinner.setSelection(0);
                }else if(iType==2){
                    binding.spinner.setSelection(1);
                }else if(iType==3){
                    binding.spinner.setSelection(2);
                }
                alertDialog.dismiss();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            alertDialog.dismiss();
            Log.d("exception",e.getMessage()+"");
        }
    }

    private void save() {

        if(binding.spinner.getSelectedItem().toString().equals("Vendor")){
            iType=1;
        }
        else if(binding.spinner.getSelectedItem().toString().equals("Customer")) {
            iType=2;
        }
        else if(binding.spinner.getSelectedItem().toString().equals("Vendor/Customer")) {
            iType=3;
        }

        if(Tools.isConnected(requireContext())){

            saveAPI();
        }else {
            saveLocally();
        }


    }

    private void saveAPI() {
        JSONObject jsonObjectMain = new JSONObject();
        try {

            if(local){
                jsonObjectMain.put("iId", 0);
            }else {
                jsonObjectMain.put("iId", iId);
            }
            Log.d("locall","success "+local);
            jsonObjectMain.put("sName",    binding.Name.getText().toString());
            jsonObjectMain.put("sCode",    binding.code.getText().toString());
            jsonObjectMain.put("sAltName",    binding.altName.getText().toString());
            jsonObjectMain.put("iCreditDays",    binding.creditDays.getText().toString());

            jsonObjectMain.put("fCreditAmount",    binding.creditAmount.getText().toString());
            jsonObjectMain.put("sAddress",    binding.address.getText().toString());
            jsonObjectMain.put("sCity",    binding.city.getText().toString());
            jsonObjectMain.put("sCountry",    binding.country.getText().toString());
            jsonObjectMain.put("sPincode",    binding.pin.getText().toString());

            jsonObjectMain.put("iMobile",    binding.mobNumber.getText().toString());
            jsonObjectMain.put("iPhone",    binding.PhoneNumber.getText().toString());
            jsonObjectMain.put("sFax",    binding.fax.getText().toString());
            jsonObjectMain.put("sEmail",    binding.email.getText().toString());
            jsonObjectMain.put("sWebsite",    binding.webSite.getText().toString());

            jsonObjectMain.put("sContactPersonNo",  binding.contactPerson.getText().toString());
            jsonObjectMain.put("iType",iType);
            jsonObjectMain.put("iUser", userId);

            Log.d("MasterCustomer","success"+jsonObjectMain.toString());
            uploadToAPI(jsonObjectMain);
        } catch (JSONException e) {

            Log.d("MasterCustomer",e.getMessage());

        }

    }

    private void uploadToAPI(JSONObject jsonObjectMain) {
        AndroidNetworking.post("http://"+ new Tools().getIP(requireActivity()) + URLs.PostCustomer)
                .addJSONObjectBody(jsonObjectMain)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("responseMaster",response);
                        try {
                            int i=Integer.parseInt(response);
                            if(helper.deleteMasterCustomer(iId)) {
                                Log.d("responseMaster ", "successfully");
                                NavDirections actions = MasterFragmentDirections.actionMasterFragmentToMasterHistoryFragment();
                                navController.navigate(actions);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                            Toast.makeText(requireContext(), e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("responseMaster",anError.getMessage());
                        Toast.makeText(requireContext(), anError.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveLocally() {

        Cursor cursor1=helper.getDataFromCustomerMaster();
        Log.d("legnthh",cursor1.getCount()+"");

        if(!EditMode) {
            local=true;
            if (cursor1.moveToFirst() && cursor1.getCount() > 0) {
                iId = cursor1.getCount();
                Log.d("legntghhiId",iId+"");

            }
        }
        Log.d("legntghhiId",iId+"");
        CustomerMasterClass masterClass=new CustomerMasterClass(
                binding.Name.getText().toString(),
                binding.code.getText().toString(),
                binding.altName.getText().toString(),
                binding.address.getText().toString(),
                binding.city.getText().toString(),
                binding.country.getText().toString(),
                binding.fax.getText().toString(),
                binding.webSite.getText().toString(),
                iType,
                binding.creditDays.getText().toString(),
                binding.creditAmount.getText().toString(),
                binding.pin.getText().toString(),
                binding.mobNumber.getText().toString(),
                binding.PhoneNumber.getText().toString(),
                binding.contactPerson.getText().toString(),
                binding.email.getText().toString(),
                iId,
                DateFormat.format("yyyy-MM-dd HH:mm:ss", new Date())+"",
                0,local);

        if(helper.deleteMasterCustomer(iId)) {
            if (helper.insertMasterCustomer(masterClass)) {
                Toast.makeText(requireContext(), "Master Added", Toast.LENGTH_SHORT).show();
                NavDirections actions = MasterFragmentDirections.actionMasterFragmentToMasterHistoryFragment();
                navController.navigate(actions);
            }
        }
    }
}
