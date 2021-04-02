package com.sangsolutions.sang.Fragment;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
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
import com.sangsolutions.sang.Adapter.MasterSettings.MasterSettings;
import com.sangsolutions.sang.Adapter.RequestHistoryAdapter.RequestClass;
import com.sangsolutions.sang.Adapter.RequestHistoryAdapter.RequestHistoryAdapter;
import com.sangsolutions.sang.Adapter.SalesPurchaseHistoryAdapter.SalesPurchaseHistory;
import com.sangsolutions.sang.Adapter.TransSalePurchase.TransSetting;
import com.sangsolutions.sang.Database.DatabaseHelper;
import com.sangsolutions.sang.Home;
import com.sangsolutions.sang.R;
import com.sangsolutions.sang.Tools;
import com.sangsolutions.sang.URLs;
import com.sangsolutions.sang.databinding.FragmentHistoryStockCountBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class StockCountHistoryFragment extends Fragment {
    FragmentHistoryStockCountBinding binding;
    int iDocType;
    String title;
    NavController navController;
    List<RequestClass> historyList;
    RequestHistoryAdapter historyAdapter;
    AlertDialog alertDialog;
    DatabaseHelper helper;
    String userIdS=null;
    String toolTitle;
    boolean selectionActive = false;
    Animation slideUp, slideDown;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding=FragmentHistoryStockCountBinding.inflate(getLayoutInflater());
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        try {
            ((Home)getActivity()).setDrawerEnabled(true);
        }catch (Exception e){
            e.printStackTrace();
        }

        helper=new DatabaseHelper(requireContext());
        historyList=new ArrayList<>();
        historyAdapter=new RequestHistoryAdapter(requireActivity(),historyList);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        slideDown = AnimationUtils.loadAnimation(getContext(), R.anim.move_down);
        slideUp = AnimationUtils.loadAnimation(getContext(), R.anim.move_up);
        binding.fabDelete.setVisibility(View.GONE);
        binding.fabClose.setVisibility(View.GONE);
        binding.fabAdd.setVisibility(View.VISIBLE);

        Cursor cursor_userId=helper.getUserId();
        if(cursor_userId!=null &&cursor_userId.moveToFirst()) {
            userIdS = cursor_userId.getString(cursor_userId.getColumnIndex("user_Id"));
        }

        assert getArguments() != null;
        iDocType = StockCountHistoryFragmentArgs.fromBundle(getArguments()).getIDocType();


        AlertDialog.Builder builder=new AlertDialog.Builder(requireActivity());
        View view=LayoutInflater.from(requireActivity()).inflate(R.layout.progress_bar,null,false);
        builder.setView(view);
        builder.setCancelable(false);
        alertDialog = builder.create();

        getHistoryDatas();

        binding.fabClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeSelection();
            }
        });
        binding.fabDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteAlert();
            }
        });

        binding.fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavDirections action=StockCountHistoryFragmentDirections
                        .actionStockCountHistoryFragmentToStockCountFragment()
                        .setEditMode(false).setIDocType(iDocType).setITransId(0);

                navController.navigate(action);
            }
        });

        return binding.getRoot();
    }

    private void deleteAlert() {

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Delete?")
                .setMessage("Do you want to Delete " + historyAdapter.getSelectedItemCount() + " items?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DeleteItems();

                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    private void DeleteItems() {

        List<Integer> listSelectedItem = historyAdapter.getSelectedItems();
        for (int i =0;i<listSelectedItem.size();i++) {
            for (int j =0;j<historyList.size();j++) {
                if (listSelectedItem.get(i) == j) {
                    deleteFromAPI(historyList.get(j).getiTransId());
                }
            }

            if (i + 1 == listSelectedItem.size()) {
                getHistoryDatas();
                historyAdapter.notifyDataSetChanged();
                closeSelection();
            }
        }
    }

    private void getHistoryDatas() {

        alertDialog.show();
        AndroidNetworking.get("http://"+ new Tools().getIP(requireActivity())+  URLs.GetTransStockSummary)
                .addQueryParameter("iDocType",String.valueOf(iDocType))
                .addQueryParameter("iUser",userIdS)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("responseHistory",response.toString());

                        loadDatas(response);

                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("responseHistory",anError.toString());

                        alertDialog.dismiss();
                    }
                });
    }

    private void loadDatas(JSONArray response) {

        historyList.clear();
        historyAdapter.notifyDataSetChanged();

        try {
            JSONArray jsonArray = new JSONArray(response.toString());

            if(jsonArray.length()==0){
                alertDialog.dismiss();
            }

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                RequestClass history=new RequestClass();
                history.setiTransId(jsonObject.getInt(SalesPurchaseHistory.I_TRANS_ID));
                history.setsDocNo(jsonObject.getString(SalesPurchaseHistory.S_DOC_NO));
                history.setsDate(jsonObject.getString(SalesPurchaseHistory.S_DATE));

                historyList.add(history);
                historyAdapter.notifyDataSetChanged();
                if(i+1==jsonArray.length()){
                    alertDialog.dismiss();
                    binding.recyclerView.setAdapter(historyAdapter);

                    historyAdapter.setOnClickListener(new RequestHistoryAdapter.OnClickListener() {
                        @Override
                        public void onItemClick(int iTransId, int position) {
                            if(!selectionActive) {
                                if (Tools.isConnected(requireContext())) {
                                    NavDirections action=StockCountHistoryFragmentDirections
                                            .actionStockCountHistoryFragmentToStockCountFragment()
                                            .setIDocType(iDocType).setITransId(iTransId).setEditMode(true);
                                    navController.navigate(action);
                                } else {
                                    Toast.makeText(requireContext(), "No Internet", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else {
                                enableActionMode(position);
                            }
                        }

                        @Override
                        public void onItemLongClick(int position) {
                            enableActionMode(position);
                            selectionActive = true;
                        }

                        @Override
                        public void onDeleteClick(int iTransId) {
                            deleteFromAPI(iTransId);
                        }

                        @Override
                        public void onPDFclick(int iTransId, int position) {

                            pdfLoading(iTransId);
                        }
                    });

                }
            }
        } catch (JSONException e) {
            Log.d("exceptionn",e.getMessage());
            e.printStackTrace();
        }
    }

    private void pdfLoading(int iTransId) {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        } else {
            pdfGetData(iTransId);
        }
    }

    private void pdfGetData(int iTransId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("PDF")
                .setMessage("Do you want to save pdf?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pdfGeneration(iTransId);

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
    }

    private void deleteFromAPI(int iTransId) {
        AndroidNetworking.get("http://"+ new Tools().getIP(requireActivity())+  URLs.DeleteTransStock)
                .addQueryParameter("iTransId", String.valueOf(iTransId))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("response_delete",response);

                        getHistoryDatas();
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("response_delete",anError.toString()+ anError.getErrorDetail()+anError.getErrorBody());

                    }
                });
    }

    private void enableActionMode(int position) {
        toggleSelection(position);
    }

    private void toggleSelection(int position) {
        historyAdapter.toggleSelection(position);
        int count = historyAdapter.getSelectedItemCount();

        if (count == 1 && binding.fabDelete.getVisibility() != View.VISIBLE) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    binding.fabAdd.startAnimation(slideDown);
                    binding.fabAdd.setVisibility(View.GONE);

                    binding.fabDelete.startAnimation(slideUp);
                    binding.fabClose.startAnimation(slideUp);
                    binding.fabDelete.setVisibility(View.VISIBLE);
                    binding.fabClose.setVisibility(View.VISIBLE);
                }
            }, 300);
        }

        if (count == 0) {
            closeSelection();
        }
    }

    private void closeSelection() {

        historyAdapter.clearSelections();
        binding.fabAdd.setVisibility(View.VISIBLE);
        binding.fabAdd.startAnimation(slideUp);
        binding.fabDelete.startAnimation(slideDown);
        binding.fabClose.startAnimation(slideDown);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.fabDelete.setVisibility(View.GONE);
                binding.fabClose.setVisibility(View.GONE);
            }
        }, 300);
        selectionActive = false;
    }



    private void pdfGeneration(int iTransId) {

        if(Tools.isConnected(requireContext())){
            AndroidNetworking.get("http://" + new Tools().getIP(requireActivity())+ URLs.GetTransStockDetails)
                    .addQueryParameter("iTransId",String.valueOf(iTransId))
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("Response_loadEditValue",response.toString());
                            loadAPIValue_for_pdf(response);

                        }

                        @Override
                        public void onError(ANError anError) {
                            Log.d("Response_loadEditValue",anError.toString());
                            alertDialog.dismiss();
                            Toast.makeText(requireActivity(), anError.getMessage(), Toast.LENGTH_SHORT).show();
                            NavDirections actions =StockCountFragmentDirections
                                    .actionStockCountFragmentToStockCountHistoryFragment(iDocType);
                        }
                    });
        }else {
            Toast.makeText(requireActivity(), "NO Internet", Toast.LENGTH_SHORT).show();
            alertDialog.dismiss();
        }
    }

    private void loadAPIValue_for_pdf(JSONObject response) {
        JSONArray jsonArray1 = null;
        JSONArray jsonArray;
        List<Integer> headerListTags =new ArrayList<>();
        List<Integer>bodyListTags=new ArrayList<>();

        File file;
        Bitmap bmp,scalebm;



        bmp= BitmapFactory.decodeResource(getResources(),R.drawable.sang_logo);
        scalebm=Bitmap.createScaledBitmap(bmp,30,30,false);


        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();
        PdfDocument.PageInfo myPageinfo = new PdfDocument
                .PageInfo.Builder(595, 842, 1).create();

        PdfDocument.Page myPage = pdfDocument.startPage(myPageinfo);
        Canvas canvas = myPage.getCanvas();
        int startYPosition = 20;

        canvas.drawBitmap(scalebm,400,startYPosition,paint);
        paint.setTextSize(15f);
        paint.setFakeBoldText(true);
        canvas.drawText("Sang Solutions", 440, startYPosition+20, paint);
        paint.setFakeBoldText(false);
        paint.setTextSize(25f);
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("Stock Count", 20, startYPosition += 20, paint);

        try {
             jsonArray = new JSONArray(response.getString("Table"));
            Log.d("HeadArray",jsonArray.length()+"");

            paint.setTextSize(15);
            for (int i=0;i<jsonArray.length();i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                canvas.drawText("DocNo", 20, startYPosition += 50, paint);
                canvas.drawText(": "+jsonObject.getString("sDocNo"), 120, startYPosition, paint);

                canvas.drawText("Date", 20,  startYPosition += 30 , paint);
                canvas.drawText(": "+jsonObject.getString("sDate"), 120, startYPosition, paint);

                canvas.drawText("Stock Date", 20, startYPosition += 30, paint);
                canvas.drawText(": "+jsonObject.getString("sStockDate"), 120, startYPosition, paint);

                canvas.drawText("Narration", 20,  startYPosition += 30 , paint);
                canvas.drawText(": "+jsonObject.getString("sNarration"), 120, startYPosition, paint);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }


        for (int tagId=1;tagId<=8;tagId++){
            Cursor cursor=helper.getTransSettings(iDocType,tagId);
            if(cursor!=null ){
                cursor.moveToFirst();

                String iTagPosition=cursor.getString(cursor.getColumnIndex(TransSetting.I_TAG_POSITION));
                String visibility=cursor.getString(cursor.getColumnIndex(TransSetting.B_VISIBLE));

                Cursor cursor1=helper.getTagNamebyId(tagId);
                cursor1.moveToFirst();
                if(iTagPosition.equals("1")){
                    if(visibility.equals("true")){
                        headerListTags.add(tagId);
//                        canvas.drawText(cursor1.getString(cursor1.getColumnIndex(MasterSettings.S_NAME))+" :",
//                                20, startYPosition += 30, paint);
                    }
                }
                else if(iTagPosition.equals("2")){
                    if(visibility.equals("true")){
                        bodyListTags.add(tagId);
                    }

                }
            }
        }
        try {
             jsonArray1 = new JSONArray(response.getString("Table1"));
            Log.d("BodyArray1",jsonArray1.length()+"");
                JSONObject jsonObjectInner = jsonArray1.getJSONObject(0);

                for (int k = 0; k < headerListTags.size(); k++) {
                    Log.d("headerListTag_size",headerListTags.size()+"");

                    Cursor cursor1=helper.getTagNamebyId(headerListTags.get(k));
                    Log.d("headerListTag_size",cursor1.getCount()+"");

                    cursor1.moveToFirst();
                    Log.d("headerListTags",cursor1.getString(cursor1.getColumnIndex(MasterSettings.S_NAME))+" "+jsonObjectInner.getString("sTag" + headerListTags.get(k)));
                    canvas.drawText(cursor1.getString(cursor1.getColumnIndex(MasterSettings.S_NAME)),
                            20, startYPosition+=30, paint);
                    canvas.drawText(": "+ jsonObjectInner.getString("sTag" + headerListTags.get(k)),
                            120, startYPosition, paint);

                }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("exceptionn",e.getMessage());
        }

        int endXPosition = myPageinfo.getPageWidth() - 20;
        int startXPosition=20;
        paint.setTextSize(10);
        paint.setFakeBoldText(true);
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("S.no ", startXPosition, startYPosition += 50, paint);
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("Product Name ", startXPosition+=30, startYPosition, paint);
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("Unit", startXPosition+=135, startYPosition, paint);
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("Quantity", startXPosition+=30, startYPosition, paint);

        for (int k = 0; k < bodyListTags.size(); k++) {
            Cursor cursor1=helper.getTagNamebyId(bodyListTags.get(k));
            cursor1.moveToFirst();
            startXPosition+=50;
            canvas.drawText(cursor1.getString(cursor1.getColumnIndex(MasterSettings.S_NAME)),
                    startXPosition, startYPosition, paint);
            startXPosition+=50;
        }
        canvas.drawLine(20,startYPosition+3,endXPosition,startYPosition+3,paint);
        paint.setFakeBoldText(false);
        assert jsonArray1 != null;
        if(jsonArray1.length()<=17) {
            loadFirstPage(jsonArray1.length(),startXPosition,startYPosition,jsonArray1,canvas,bodyListTags,pdfDocument,paint,myPage);

        }else if(jsonArray1.length()>17){
            loadFirstPage(17,startXPosition,startYPosition,jsonArray1,canvas,bodyListTags,pdfDocument,paint,myPage);
            int page=(int) Math.ceil(jsonArray1.length()/26.0);

            int start=18;
            Log.d("pagee",page+"");
            for (int j=1;j<=page;j++){
                PdfDocument.PageInfo myPageinfo1 = new PdfDocument
                        .PageInfo.Builder(595, 842, 1).create();

                PdfDocument.Page myPage1 = pdfDocument.startPage(myPageinfo1);
                Canvas canvas1 = myPage1.getCanvas();

                int stop=start+26;
                Log.d("pagee",page+" "+start+" "+stop);
                if(stop>jsonArray1.length()) {
                    stop = jsonArray1.length();
                }

                try {
                    startYPosition=20;
                    for (int i = start; i <stop; i++) {
                        startXPosition = 20;
                        JSONObject jsonObjectInner = jsonArray1.getJSONObject(i);
                        canvas1.drawText(String.valueOf(i), startXPosition, startYPosition += 30, paint);
                        Log.d("ppppppppp",jsonObjectInner.getString("sProduct").length()+"");
                        if(jsonObjectInner.getString("sProduct").length()>20){
                            int lengthProduct=jsonObjectInner.getString("sProduct").length();
                            canvas1.drawText(jsonObjectInner.getString("sProduct").substring(0,20), startXPosition+=30, startYPosition, paint);
                            canvas1.drawText(jsonObjectInner.getString("sProduct").substring(20,lengthProduct), startXPosition, startYPosition+10, paint);
                        }else {
                            canvas1.drawText(jsonObjectInner.getString("sProduct"), startXPosition+=30, startYPosition, paint);

                        }
                        canvas1.drawText(jsonObjectInner.getString("sUnits"), startXPosition += 135, startYPosition, paint);
                        canvas1.drawText(String.valueOf(jsonObjectInner.getInt("fQty")), startXPosition += 30, startYPosition, paint);


                        for (int k = 0; k < bodyListTags.size(); k++) {
                            Log.d("exception1","stop"+"");
                            Cursor cursor1 = helper.getTagNamebyId(bodyListTags.get(k));
                            cursor1.moveToFirst();
                            startXPosition += 50;
                            if(jsonObjectInner.getString("sTag" + bodyListTags.get(k)).length()>19){
                                int length=jsonObjectInner.getString("sTag" + bodyListTags.get(k)).length();
                                canvas1.drawText(jsonObjectInner.getString("sTag" + bodyListTags.get(k)).substring(0,19),
                                        startXPosition, startYPosition, paint);
                                canvas1.drawText(jsonObjectInner.getString("sTag" + bodyListTags.get(k)).substring(19,length),
                                        startXPosition, startYPosition+10, paint);
                            }else {
                                canvas1.drawText(jsonObjectInner.getString("sTag" + bodyListTags.get(k)),
                                        startXPosition, startYPosition, paint);
                            }
                            startXPosition += 50;
                        }
                    }
                    pdfDocument.finishPage(myPage1);
                    start+=26;
                } catch (JSONException e) {
                    Log.d("exceptionn",e.getMessage());
                    e.printStackTrace();
                }
            }

        }


        file=new File(Environment.getExternalStorageDirectory().getAbsolutePath(),"/StockCountPDF.pdf");
        try {
            pdfDocument.writeTo(new FileOutputStream(file));
            Toast.makeText(requireContext(), "PDF Created", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "PDF Not Created", Toast.LENGTH_SHORT).show();

        }
        pdfDocument.close();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri apkURI = FileProvider.getUriForFile(requireContext(),getContext().getPackageName() + ".provider", file);
            intent.setDataAndType(apkURI, "application/pdf");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            intent.setDataAndType(Uri.fromFile(file), "application/pdf");
        }
        startActivity(intent);
    }

    private void loadFirstPage(int length, int startXPosition, int startYPosition, JSONArray jsonArray1, Canvas canvas, List<Integer> bodyListTags, PdfDocument pdfDocument, Paint paint, PdfDocument.Page myPage) {

        try {
            for (int j = 0; j < length; j++) {
                 startXPosition = 20;
                JSONObject jsonObjectInner = jsonArray1.getJSONObject(j);
                canvas.drawText(String.valueOf(j + 1), startXPosition, startYPosition += 30, paint);
                if(jsonObjectInner.getString("sProduct").length()>20){
                    int lengthProduct=jsonObjectInner.getString("sProduct").length();
                    canvas.drawText(jsonObjectInner.getString("sProduct").substring(0,20), startXPosition+=30, startYPosition, paint);
                    canvas.drawText(jsonObjectInner.getString("sProduct").substring(20,lengthProduct), startXPosition, startYPosition+10, paint);

                }else {
                    canvas.drawText(jsonObjectInner.getString("sProduct"), startXPosition+=30, startYPosition, paint);

                }
                canvas.drawText(jsonObjectInner.getString("sUnits"), startXPosition += 135, startYPosition, paint);
                canvas.drawText(String.valueOf(jsonObjectInner.getInt("fQty")), startXPosition += 30, startYPosition, paint);


                for (int k = 0; k < bodyListTags.size(); k++) {
                    Cursor cursor1 = helper.getTagNamebyId(bodyListTags.get(k));
                    cursor1.moveToFirst();
                    startXPosition += 50;
                    if(jsonObjectInner.getString("sTag" + bodyListTags.get(k)).length()>19){
                        int lengthTag=jsonObjectInner.getString("sTag" + bodyListTags.get(k)).length();
                        canvas.drawText(jsonObjectInner.getString("sTag" + bodyListTags.get(k)).substring(0,19),
                                startXPosition, startYPosition, paint);
                        canvas.drawText(jsonObjectInner.getString("sTag" + bodyListTags.get(k)).substring(19,lengthTag),
                                startXPosition, startYPosition+10, paint);
                    }else {
                        canvas.drawText(jsonObjectInner.getString("sTag" + bodyListTags.get(k)),
                                startXPosition, startYPosition, paint);
                    }


                    startXPosition += 50;
                }
            }
            pdfDocument.finishPage(myPage);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
