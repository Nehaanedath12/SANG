package com.sangsolutions.sang.Fragment;

import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.android.gms.common.internal.Constants;
import com.sangsolutions.sang.Database.DatabaseHelper;
import com.sangsolutions.sang.R;
import com.sangsolutions.sang.SchedulerJob;
import com.sangsolutions.sang.Tools;
import com.sangsolutions.sang.URLs;
import com.sangsolutions.sang.databinding.FragmentHomeBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HomeFragment extends Fragment {
    FragmentHomeBinding binding;
    NavController navController;
    SchedulerJob schedulerJob;
    DatabaseHelper helper;
    Cursor cursor_iUser;
    String userIdS,today_date;
    List<BarEntry> barEntryList_P;
    List<BarEntry> barEntryList_S;
    SimpleDateFormat formatter;
    List<String >weekList;
    List<String>api_list;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding=FragmentHomeBinding.inflate(getLayoutInflater());

        navController = Navigation.findNavController(requireActivity(),R.id.nav_host_fragment);
        schedulerJob=new SchedulerJob();
        helper=new DatabaseHelper(requireContext());
        barEntryList_P =new ArrayList<>();
        barEntryList_S =new ArrayList<>();
        weekList=new ArrayList<>();
        api_list=new ArrayList<>();

        AndroidNetworking.initialize(getContext());
        cursor_iUser=helper.getUserId();
        if(cursor_iUser!=null && cursor_iUser.moveToFirst()) {
            userIdS = cursor_iUser.getString(cursor_iUser.getColumnIndex("user_Id"));

        }
        formatter = new SimpleDateFormat("yyyy-MM-dd");
        today_date=formatter.format(new Date());

        LocalDate date = LocalDate.parse(today_date);

        for (int i=6;i>=0;i--){
            LocalDate returnValue = date.minusDays(i);
            weekList.add(String.valueOf(returnValue));
        }
        getDataFromAPI("1", barEntryList_P, binding.barChartPurchase);
        getDataFromAPI("2",barEntryList_S,binding.barChartSale);
        return binding.getRoot();
    }

    private void getDataFromAPI(String iDoc, List<BarEntry> barEntryList, BarChart barChart) {
        barEntryList.clear();
        AndroidNetworking.get("http://"+URLs.GetDashTransactionData)
                .addQueryParameter("iDoc",iDoc)
                .addQueryParameter("iUser",userIdS)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                        @Override
                        public void onResponse(JSONArray response) {
                        Log.d("responseHome",response.toString());
                        getValues(response,iDoc,barEntryList,barChart);

                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("responseHome",anError.toString());

                    }
                });
    }

    private void getValues(JSONArray response, String iDoc, List<BarEntry> barEntryList, BarChart barChart) {
        try {
                JSONArray jsonArray=new JSONArray(response.toString());
                for (int j=0;j<weekList.size();j++){
                boolean flag=false;
                for (int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                String Dates=jsonObject.getString("Date");
                String Value=jsonObject.getString("Value");
                 if(Dates.contains(weekList.get(j))){

                         barEntryList.add(new BarEntry(j, Float.parseFloat(Value)));
                         flag=true;
                        }
                        }
            if(!flag){
                barEntryList.add(new BarEntry(j,0));
                }
                }
                settingBarData(barEntryList,iDoc,barChart);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void settingBarData(List<BarEntry> barEntryList, String iDoc, BarChart barChart) {



        BarDataSet barDataSet=new BarDataSet(barEntryList,"week");
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        BarData barData=new BarData(barDataSet);
        barData.setValueFormatter(new MyValueFormatter());
        barData.setBarWidth(0.5f);
        barChart.setData(barData);
        barChart.setScaleEnabled(false);
        Description description=new Description();
        description.setText("No.of Data");
        barChart.setDescription(description);
        XAxis xAxis=barChart.getXAxis();
        barChart.setExtraOffsets(20, 50, 20, 20);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(weekList));
        xAxis.setPosition(XAxis.XAxisPosition.TOP);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setLabelCount(weekList.size());
        xAxis.setLabelRotationAngle(270);
        barChart.invalidate();

    }

    public static class MyValueFormatter extends ValueFormatter{

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            return value+"";
        }
    }
}

