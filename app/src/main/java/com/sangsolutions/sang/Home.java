package com.sangsolutions.sang;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.sangsolutions.sang.Adapter.TagDetailsAdapter.TagDetails;
import com.sangsolutions.sang.Database.DatabaseHelper;
import com.sangsolutions.sang.Fragment.HomeFragmentDirections;
import com.sangsolutions.sang.Fragment.P_R_ReportFragmentDirections;
import com.sangsolutions.sang.Fragment.PaymentReceiptFragmentDirections;
import com.sangsolutions.sang.Fragment.PaymentReceiptHistoryFragmentDirections;
import com.sangsolutions.sang.Fragment.QuotationHistoryFragmentDirections;
import com.sangsolutions.sang.Fragment.Report_selection_fragmentDirections;
import com.sangsolutions.sang.Fragment.RequestFragmentDirections;
import com.sangsolutions.sang.Fragment.RequestHistoryFragmentDirections;
import com.sangsolutions.sang.Fragment.S_P_OrderFragmentDirections;
import com.sangsolutions.sang.Fragment.S_P_OrderHistoryFragmentDirections;
import com.sangsolutions.sang.Fragment.S_P_ReportFragmentDirections;
import com.sangsolutions.sang.Fragment.Sale_Purchase_FragmentDirections;
import com.sangsolutions.sang.Fragment.SalesPurchaseHistoryFragmentDirections;
import com.sangsolutions.sang.Fragment.SalesPurchaseReturnFragmentDirections;
import com.sangsolutions.sang.Fragment.SalesPurchaseReturnHistoryFragment;
import com.sangsolutions.sang.Fragment.SalesPurchaseReturnHistoryFragmentDirections;
import com.sangsolutions.sang.Fragment.StockCountFragmentDirections;
import com.sangsolutions.sang.Fragment.StockCountHistoryFragment;
import com.sangsolutions.sang.Fragment.StockCountHistoryFragmentDirections;
import com.sangsolutions.sang.databinding.ActivityMainBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class Home extends AppCompatActivity {
    ActivityMainBinding binding;
    DatabaseHelper helper;
    Toolbar toolbar;
    NavController navController;
    AppBarConfiguration mAppBarConfiguration;
    NavigationView navigationView;
    DrawerLayout drawer;
    String userName;
    Cursor tagCursor;
    SchedulerJob schedulerJob;
    List<Integer>tagList;

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }
        else if(navController.getCurrentDestination().getId()==R.id.sale_Purchase_Fragment){
            backAlert();
        }
        else if(navController.getCurrentDestination().getId()==R.id.salesPurchaseHistoryFragment){
           NavDirections action=SalesPurchaseHistoryFragmentDirections.actionSalesPurchaseHistoryFragmentToHomeFragment();
           navController.navigate(action);
        }
        else if(navController.getCurrentDestination().getId()==R.id.homeFragment){
          finish();
        }
        else if(navController.getCurrentDestination().getId()==R.id.report_selection_fragment){
            NavDirections action= Report_selection_fragmentDirections.actionReportSelectionFragmentToHomeFragment();
            navController.navigate(action);
        }
//        else if(navController.getCurrentDestination().getId()==R.id.reportFragment){
//            backAlert();
//        }
//        else if(navController.getCurrentDestination().getId()==R.id.p_R_ReportFragment){
//            backAlert();
//        }
//        else if(navController.getCurrentDestination().getId()==R.id.s_P_Return_ReportFragment2){
//            backAlert();
//        }
        else if(navController.getCurrentDestination().getId()==R.id.paymentReceiptHistoryFragment){
            NavDirections action= PaymentReceiptHistoryFragmentDirections.actionPaymentReceiptHistoryFragmentToHomeFragment();
            navController.navigate(action);
        }
        else if(navController.getCurrentDestination().getId()==R.id.paymentReceiptFragment){
            backAlert();
        }
        else if(navController.getCurrentDestination().getId()==R.id.salesPurchaseReturnFragment){
            backAlert();
        }
        else if(navController.getCurrentDestination().getId()==R.id.salesPurchaseReturnHistoryFragment){
            NavDirections action= SalesPurchaseReturnHistoryFragmentDirections.actionSalesPurchaseReturnHistoryFragmentToHomeFragment();
            navController.navigate(action);
        }
        else if(navController.getCurrentDestination().getId()==R.id.s_P_OrderFragment){
            backAlert();
        }
        else if(navController.getCurrentDestination().getId()==R.id.s_P_OrderHistoryFragment){
            NavDirections action=S_P_OrderHistoryFragmentDirections.actionSPOrderHistoryFragmentToHomeFragment();
            navController.navigate(action);
        }
        else if(navController.getCurrentDestination().getId()==R.id.requestHistoryFragment){
            NavDirections action= RequestHistoryFragmentDirections.actionRequestHistoryFragmentToHomeFragment();
            navController.navigate(action);
        }
        else if(navController.getCurrentDestination().getId()==R.id.requestFragment){
            backAlert();
        }
        else if(navController.getCurrentDestination().getId()==R.id.quotationHistoryFragment){
            NavDirections action=QuotationHistoryFragmentDirections.actionQuotationHistoryFragmentToHomeFragment();
            navController.navigate(action);
        }
        else if(navController.getCurrentDestination().getId()==R.id.quotationFragment){
            backAlert();
        }
        else if(navController.getCurrentDestination().getId()==R.id.stockCountHistoryFragment){
            NavDirections action= StockCountHistoryFragmentDirections.actionStockCountHistoryFragmentToHomeFragment();
            navController.navigate(action);
        }
        else if(navController.getCurrentDestination().getId()==R.id.stockCountFragment){
            backAlert();
        }


    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);
        helper=new DatabaseHelper(this);

        schedulerJob=new SchedulerJob();
        toolbar=binding.toolbar;
        drawer=binding.drawerLayout;
        navigationView=binding.navView;
        setSupportActionBar(toolbar);


        navController = Navigation.findNavController(this,R.id.nav_host_fragment);

        View headerView=navigationView.getHeaderView(0);
        TextView textHeader=headerView.findViewById(R.id.username);
        Cursor cursor=helper.getUserId();

        if(cursor!=null && cursor.moveToFirst()) {
            userName = helper.getUserName(cursor.getString(cursor.getColumnIndex("user_Id")));
            textHeader.setText(userName);
        }


        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.homeFragment, R.id.salesPurchaseHistoryFragment,
                R.id.purchaseFragment,R.id.report_selection_fragment,
                R.id.paymentReceiptHistoryFragment,R.id.ReceiptHistoryFragment,
                R.id.salesPurchaseReturnHistoryFragment,R.id.PurchaseReturnHistoryFragment,
                R.id.s_P_OrderHistoryFragment,R.id.requestHistoryFragment,R.id.quotationHistoryFragment,
                R.id.stockCountHistoryFragment)
                .setDrawerLayout(drawer).build();
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


        binding.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutAlert();
            }
        });

        binding.home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (navController.getCurrentDestination().getId() == R.id.sale_Purchase_Fragment) {
                   NavDirections action=Sale_Purchase_FragmentDirections.actionSalePurchaseFragmentToHomeFragment();
                    navController.navigate(action);
                }
                else if(navController.getCurrentDestination().getId() == R.id.salesPurchaseHistoryFragment){
                    NavDirections action= SalesPurchaseHistoryFragmentDirections.actionSalesPurchaseHistoryFragmentToHomeFragment();
                    navController.navigate(action);
                }
                else if(navController.getCurrentDestination().getId()==R.id.report_selection_fragment){
                    NavDirections actions=Report_selection_fragmentDirections.actionReportSelectionFragmentToHomeFragment();
                    navController.navigate(actions);
                }
                else if(navController.getCurrentDestination().getId()==R.id.reportFragment){
                    NavDirections action= S_P_ReportFragmentDirections.actionReportFragmentToHomeFragment();
                    navController.navigate(action);
                }

                else  if(navController.getCurrentDestination().getId()==R.id.p_R_ReportFragment){
                    NavDirections action= P_R_ReportFragmentDirections.actionPRReportFragmentToHomeFragment();
                    navController.navigate(action);
                }
                else if(navController.getCurrentDestination().getId()==R.id.paymentReceiptHistoryFragment){
                    NavDirections action=PaymentReceiptHistoryFragmentDirections.actionPaymentReceiptHistoryFragmentToHomeFragment();
                    navController.navigate(action);
                }
                else if(navController.getCurrentDestination().getId()==R.id.paymentReceiptFragment){
                    NavDirections action= PaymentReceiptFragmentDirections.actionPaymentReceiptFragmentToHomeFragment();
                    navController.navigate(action);
                }
                else if(navController.getCurrentDestination().getId()==R.id.salesPurchaseReturnHistoryFragment){
                    NavDirections action= SalesPurchaseReturnHistoryFragmentDirections.actionSalesPurchaseReturnHistoryFragmentToHomeFragment();
                    navController.navigate(action);
                }
                else if(navController.getCurrentDestination().getId()==R.id.salesPurchaseReturnFragment){
                    NavDirections action= SalesPurchaseReturnFragmentDirections.actionSalesPurchaseReturnFragmentToHomeFragment();
                    navController.navigate(action);
                }
                else if(navController.getCurrentDestination().getId()==R.id.s_P_OrderFragment){
                    NavDirections action= S_P_OrderFragmentDirections.actionSPOrderFragmentToHomeFragment();
                    navController.navigate(action);
                }
                else if(navController.getCurrentDestination().getId()==R.id.s_P_OrderHistoryFragment){
                    NavDirections action= S_P_OrderHistoryFragmentDirections.actionSPOrderHistoryFragmentToHomeFragment();
                    navController.navigate(action);
                }

                else if(navController.getCurrentDestination().getId()==R.id.quotationHistoryFragment){
                    NavDirections action= QuotationHistoryFragmentDirections
                            .actionQuotationHistoryFragmentToHomeFragment();
                    navController.navigate(action);
                }
                else if(navController.getCurrentDestination().getId()==R.id.stockCountHistoryFragment) {
                    NavDirections action = StockCountHistoryFragmentDirections.actionStockCountHistoryFragmentToHomeFragment();
                    navController.navigate(action);
                }
                else if(navController.getCurrentDestination().getId()==R.id.stockCountFragment){
                    NavDirections action= StockCountFragmentDirections.actionStockCountFragmentToHomeFragment();
                    navController.navigate(action);
                }

                }
                });


                    binding.navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    switch(menuItem.getItemId()){



                    case R.id.purchaseFragment:
                    {
                        tagCursor=helper.getTagDetails();
                        if(tagCursor.moveToFirst() && tagCursor.getCount()>0) {
                            if (navController.getCurrentDestination().getId() != R.id.salesPurchaseHistoryFragment) {
                                NavDirections action = HomeFragmentDirections.actionHomeFragmentToSalesPurchaseHistoryFragment("Purchase Summary").setIDocType(10).setToolTitle("Purchase Summary");
                                navController.navigate(R.id.homeFragment);
                                navController.navigate(action);
                            } else {
                                NavDirections action = HomeFragmentDirections.actionHomeFragmentToSalesPurchaseHistoryFragment("Purchase Summary").setIDocType(10);
                                navController.navigate(R.id.homeFragment);
                                navController.navigate(action);
                            }
                        }else {
                            Toast.makeText(Home.this, "please sync tags", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                    case R.id.salesPurchaseHistoryFragment:
                    {
                        tagCursor=helper.getTagDetails();
                        if(tagCursor.getCount()>0) {
                        if(navController.getCurrentDestination().getId() !=R.id.salesPurchaseHistoryFragment){
                            NavDirections action=HomeFragmentDirections.actionHomeFragmentToSalesPurchaseHistoryFragment("Sale Summary").setIDocType(20);
                            navController.navigate(R.id.homeFragment);
                            navController.navigate(action);
                        }
                        else{
                            NavDirections  action=HomeFragmentDirections.actionHomeFragmentToSalesPurchaseHistoryFragment("Sale Summary").setIDocType(20);
                            navController.navigate(R.id.homeFragment);
                            navController.navigate(action);
                        }
                        }else {
                            Toast.makeText(Home.this, "please sync tags", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;

                        case R.id.paymentReceiptHistoryFragment:    {
                            tagCursor=helper.getTagDetails();
                            if(tagCursor.getCount()>0) {
                            if(navController.getCurrentDestination().getId()!=R.id.paymentReceiptHistoryFragment){
                                NavDirections action=HomeFragmentDirections.actionHomeFragmentToPaymentReceiptHistoryFragment("Payment History",15);
                                navController.navigate(R.id.homeFragment);
                                navController.navigate(action);
                            }
                            else {
                                NavDirections action=HomeFragmentDirections.actionHomeFragmentToPaymentReceiptHistoryFragment("Payment History",15);
                                navController.navigate(R.id.homeFragment);
                                navController.navigate(action);
                            }
                            }else {
                                Toast.makeText(Home.this, "please sync tags", Toast.LENGTH_SHORT).show();
                            }
                        }
                        break;


                        case R.id.salesPurchaseReturnHistoryFragment:{
                                NavDirections action=HomeFragmentDirections.actionHomeFragmentToSalesPurchaseReturnHistoryFragment("Sales Return History",21);
                                navController.navigate(R.id.homeFragment);
                                navController.navigate(action);

                        }
                        break;
                        case R.id.PurchaseReturnHistoryFragment:{
                            NavDirections action=HomeFragmentDirections.actionHomeFragmentToSalesPurchaseReturnHistoryFragment("Purchase Return History",11);
                            navController.navigate(R.id.homeFragment);
                            navController.navigate(action);
                        }
                        break;


                        case R.id.s_P_OrderHistoryFragment:{
                            NavDirections action=HomeFragmentDirections.actionHomeFragmentToSPOrderHistoryFragment(22,"Sales Order History");
                            navController.navigate(R.id.homeFragment);
                            navController.navigate(action);
                        }
                        break;

                        case R.id.Purchase_OrderHistoryFragment:{
                            NavDirections action=HomeFragmentDirections.actionHomeFragmentToSPOrderHistoryFragment(12,"Purchase Order History");
                            navController.navigate(R.id.homeFragment);
                            navController.navigate(action);
                        }
                        break;

                        case R.id.requestHistoryFragment:{
                            NavDirections action=HomeFragmentDirections.actionHomeFragmentToRequestHistoryFragment(13,"Request History");
                            navController.navigate(R.id.homeFragment);
                            navController.navigate(action);
                        }
                        break;
                        case R.id.enquiryrequestHistoryFragment:{
                            NavDirections action=HomeFragmentDirections.actionHomeFragmentToRequestHistoryFragment(23,"Enquiry History");
                            navController.navigate(R.id.homeFragment);
                            navController.navigate(action);
                        }
                        break;

                        case R.id.quotationHistoryFragment:{
                            NavDirections action=HomeFragmentDirections
                                    .actionHomeFragmentToQuotationHistoryFragment(14,"Purchase Quotation History");
                            navController.navigate(R.id.homeFragment);
                            navController.navigate(action);
                        }
                        break;



                        case R.id.SalesquotationHistoryFragment:{
                            NavDirections action=HomeFragmentDirections
                                    .actionHomeFragmentToQuotationHistoryFragment(24,"Sales Quotation History");
                            navController.navigate(R.id.homeFragment);
                            navController.navigate(action);
                        }
                        break;

                        case R.id.stockCountHistoryFragment:{
                            NavDirections action=HomeFragmentDirections
                                    .actionHomeFragmentToStockCountHistoryFragment(40);
                            navController.navigate(R.id.homeFragment);
                            navController.navigate(action);
                        }
                        break;

                        case R.id.ReceiptHistoryFragment:    {
                            tagCursor=helper.getTagDetails();
                            if(tagCursor.getCount()>0) {
                            if(navController.getCurrentDestination().getId()!=R.id.paymentReceiptHistoryFragment){
                                NavDirections action=HomeFragmentDirections.actionHomeFragmentToPaymentReceiptHistoryFragment("Receipt History",25);
                                navController.navigate(R.id.homeFragment);
                                navController.navigate(action);
                            }
                            else {
                                NavDirections action=HomeFragmentDirections.actionHomeFragmentToPaymentReceiptHistoryFragment("Receipt History",25);
                                navController.navigate(R.id.homeFragment);
                                navController.navigate(action);
                            }
                            }else {
                                Toast.makeText(Home.this, "please sync tags", Toast.LENGTH_SHORT).show();
                            }
                        }
                        break;

                    case R.id.homeFragment:
                            {
                            if (navController.getCurrentDestination().getId() != R.id.homeFragment) {
                                navController.navigate(R.id.homeFragment);
                            }
                            }

                        break;

                        case R.id.report_selection_fragment:{
                            tagCursor=helper.getTagDetails();
                            if(tagCursor.getCount()>0) {
                            if(navController.getCurrentDestination().getId()!=R.id.report_selection_fragment){
                                NavDirections action= HomeFragmentDirections.actionHomeFragmentToReportSelectionFragment();
                                navController.navigate(R.id.homeFragment);
                                navController.navigate(action);
                            }
                            else {
                                NavDirections action= HomeFragmentDirections.actionHomeFragmentToReportSelectionFragment();
                                navController.navigateUp();
                                navController.navigate(action);
                            }
                            }else {
                                Toast.makeText(Home.this, "please sync tags", Toast.LENGTH_SHORT).show();
                            }
                        }
                        break;

                        case R.id.syncNav:{
                            if(Tools.isConnected(Home.this)) {
                                syncTag();
                            }
                            else {
                                Snackbar snackbar=Snackbar.make(getWindow().getDecorView().getRootView(),"Offline",Snackbar.LENGTH_LONG);
                                snackbar.setTextColor(Color.WHITE);
                                snackbar.setBackgroundTint(Color.RED);
                                snackbar.show();
                            }
                        }
                        break;
                    case R.id.Logout:
                        logoutAlert();

                }
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
            });
            }

    private void syncTag() {
        AndroidNetworking.initialize(this);
        for (int i=1;i<=8;i++) {
            GetTag_Details(i);
            if(i==8){
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    public void run() {
                        Toast.makeText(Home.this, "TAG Synced", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    private void GetTag_Details(int iType) {
        AndroidNetworking.get("http://"+URLs.GetMasterTagDetails)
                .addQueryParameter("iType",String.valueOf(iType))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("responseTag",response.toString());
                        loadTagData(response,String.valueOf(iType));
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("response",anError.toString());
                    }
                });
    }

    private void loadTagData(JSONObject response, String iType) {
        tagList=new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(response.getString("Data"));
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                TagDetails details = new TagDetails(
                        jsonObject.getString(TagDetails.S_CODE),
                        jsonObject.getString(TagDetails.S_NAME),
                        jsonObject.getString(TagDetails.S_ALT_NAME),
                        jsonObject.getInt(TagDetails.I_ID),
                        iType);
                tagList.add(jsonObject.getInt(TagDetails.I_ID));

                if (helper.checkTagDetailsById(jsonObject.getString(TagDetails.I_ID), iType)) {
                    if (helper.checkAllDataMasterTag(details)) {

                        Log.d("successTag","updated successfully");
                    }
                } else if (helper.insertMasterTag(details)) {
                    Log.d("successTag", "tag details  added successfully " + i);
                }
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void logoutAlert() {

                            AlertDialog.Builder builder=new AlertDialog.Builder(Home.this);
                            builder.setTitle("Logout!")
                            .setMessage("Do you want to Logout ?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            if(helper.deleteCurrentLogin()){
                            startActivity(new Intent(Home.this,Login.class));
                            finish();
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
    @Override
    public boolean onSupportNavigateUp() {
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
            if(navController.getCurrentDestination().getId()==R.id.sale_Purchase_Fragment){
            backAlert();
            return true;
            }
            else if(navController.getCurrentDestination().getId()==R.id.paymentReceiptFragment){
                backAlert();
                return true;
            }else if(navController.getCurrentDestination().getId()==R.id.salesPurchaseReturnFragment){
                backAlert();
                return true;
            }
            else if(navController.getCurrentDestination().getId()==R.id.s_P_OrderFragment){
                backAlert();
                return true;
            }
            else if(navController.getCurrentDestination().getId()==R.id.quotationFragment){
                backAlert();
                return true;
            }
            else if(navController.getCurrentDestination().getId()==R.id.stockCountFragment){
                backAlert();
                return true;
            }
            else {
            return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                    || super.onSupportNavigateUp();
            }
            }

                private void backAlert() {
                AlertDialog.Builder builder=new AlertDialog.Builder(Home.this);
                builder.setTitle("Close!")
                .setMessage("Do you want to close without saving ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                          @Override
                            public void onClick(DialogInterface dialog, int which) {
//                        action= Sale_Purchase_FragmentDirections.actionSalePurchaseFragmentToSalesPurchaseHistoryFragment().setIDocType(1);
//                        navController.navigate(action);
                            navController.navigateUp();
                            }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                    }).create().show();

    }
}