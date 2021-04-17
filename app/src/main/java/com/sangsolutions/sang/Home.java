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

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.sangsolutions.sang.Adapter.ExpandableListAdapter;
import com.sangsolutions.sang.Adapter.ExpandedMenuModel;
import com.sangsolutions.sang.Adapter.TagDetailsAdapter.TagDetails;
import com.sangsolutions.sang.Database.DatabaseHelper;
import com.sangsolutions.sang.Fragment.HomeFragmentDirections;
import com.sangsolutions.sang.Fragment.MasterFragment;
import com.sangsolutions.sang.Fragment.MasterFragmentDirections;
import com.sangsolutions.sang.Fragment.MasterHistoryFragmentDirections;
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
import com.sangsolutions.sang.Fragment.SalesPurchaseReturnHistoryFragmentDirections;
import com.sangsolutions.sang.Fragment.StockCountFragmentDirections;
import com.sangsolutions.sang.Fragment.StockCountHistoryFragmentDirections;
import com.sangsolutions.sang.Service.PostSalePurchaseService;
import com.sangsolutions.sang.databinding.ActivityMainBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


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
    SchedulePost schedulepost;
    SchedulerJob schedulerJob;
    List<Integer>tagList;
    int lockmode;
    ExpandableListAdapter mMenuAdapter;
    ExpandableListView expandableList;
    List<ExpandedMenuModel> listDataHeader;
    HashMap<ExpandedMenuModel, List<String>> listDataChild;





    public void setDrawerEnabled(boolean enabled){
        if(enabled){
           lockmode=  DrawerLayout.LOCK_MODE_UNLOCKED;
        }else {
           lockmode= DrawerLayout.LOCK_MODE_LOCKED_CLOSED;
        }
        drawer.setDrawerLockMode(lockmode);
    }

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
        else if(navController.getCurrentDestination().getId()==R.id.masterFragment){
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

        schedulepost =new SchedulePost();
        schedulerJob = new SchedulerJob();

        toolbar=binding.toolbar;
        drawer=binding.drawerLayout;
        navigationView=binding.navView;
        expandableList=binding.navigationmenu;
        setSupportActionBar(toolbar);


        navController = Navigation.findNavController(this,R.id.nav_host_fragment);

        View headerView=navigationView.getHeaderView(0);
        TextView textHeader=headerView.findViewById(R.id.username);
        Cursor cursor=helper.getUserId();

        if(cursor!=null && cursor.moveToFirst()) {
            userName = helper.getUserName(cursor.getString(cursor.getColumnIndex("user_Id")));
            textHeader.setText(userName);
        }

        prepareListData();

        mMenuAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild, binding.navigationmenu,0);

        // setting list adapter
        expandableList.setAdapter(mMenuAdapter);

        expandableList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                Log.d("DEBUG_G", "submenu item clicked "+i+" "+i1+" "+l);

                if(i==1){
                    switch (i1){
                        case 0:{
                            tagCursor=helper.getTagDetails();
                            if(tagCursor.getCount()>0) {
                                    NavDirections  action=HomeFragmentDirections.actionHomeFragmentToSalesPurchaseHistoryFragment("Sale History").setIDocType(20);
                                    navController.navigate(R.id.homeFragment);
                                    navController.navigate(action);
                            }else {
                                Toast.makeText(Home.this, "please sync  Data's", Toast.LENGTH_SHORT).show();
                            }

                        }break;

                        case 1:{
                            tagCursor = helper.getTagDetails();
                            if (tagCursor.getCount() > 0) {
                                        NavDirections action = HomeFragmentDirections.actionHomeFragmentToPaymentReceiptHistoryFragment("Receipt History", 25);
                                        navController.navigate(R.id.homeFragment);
                                        navController.navigate(action);
                                } else {
                                    Toast.makeText(Home.this, "please sync  Data's", Toast.LENGTH_SHORT).show();
                                }
                        }
                        break;

                        case 2:{
                            tagCursor = helper.getTagDetails();
                            if (tagCursor.getCount() > 0) {
                                NavDirections action = HomeFragmentDirections.actionHomeFragmentToSalesPurchaseReturnHistoryFragment("Sales Return History", 21);
                                navController.navigate(R.id.homeFragment);
                                navController.navigate(action);

                            } else {
                                Toast.makeText(Home.this, "please sync  Data's", Toast.LENGTH_SHORT).show();
                            }
                        }break;

                        case 3:{
                            tagCursor = helper.getTagDetails();
                            if (tagCursor.getCount() > 0) {
                                NavDirections action = HomeFragmentDirections.actionHomeFragmentToSPOrderHistoryFragment(22, "Sales Order History");
                                navController.navigate(R.id.homeFragment);
                                navController.navigate(action);
                            }
                            else {
                                Toast.makeText(Home.this, "please sync Data's", Toast.LENGTH_SHORT).show();
                            }

                        }break;
                        case 4:{
                            tagCursor = helper.getTagDetails();
                            if (tagCursor.getCount() > 0) {
                                NavDirections action = HomeFragmentDirections.actionHomeFragmentToRequestHistoryFragment(23, "Enquiry History");
                                navController.navigate(R.id.homeFragment);
                                navController.navigate(action);
                            }
                            else {
                                Toast.makeText(Home.this, "please sync  Data's", Toast.LENGTH_SHORT).show();
                            }
                        }break;

                        case 5:{
                            tagCursor = helper.getTagDetails();
                            if (tagCursor.getCount() > 0) {
                                NavDirections action = HomeFragmentDirections
                                        .actionHomeFragmentToQuotationHistoryFragment(24, "Sales Quotation History");
                                navController.navigate(R.id.homeFragment);
                                navController.navigate(action);
                            }
                            else {
                                Toast.makeText(Home.this, "please sync Datas", Toast.LENGTH_SHORT).show();
                            }
                        }break;
                        case 6:{
                            tagCursor=helper.getTagDetails();
                            if(tagCursor.getCount()>0) {

                                NavDirections action = HomeFragmentDirections.actionHomeFragmentToSalesPurchaseHistoryFragment("Advance Invoice History").setIDocType(16);
                                navController.navigate(R.id.homeFragment);
                                navController.navigate(action);
//                            NavDirections action= HomeFragmentDirections.actionHomeFragmentToAdvanceInvoiceHistoryFragment();
//                            navController.navigate(R.id.homeFragment);
//                            navController.navigate(action);
                                drawer.closeDrawer(GravityCompat.START);
                                return true;
                            }else {
                                Toast.makeText(Home.this, "please sync Datas", Toast.LENGTH_SHORT).show();
                            }
                        }break;
                        case 7:{
                            tagCursor = helper.getTagDetails();
                            if (tagCursor.getCount() > 0) {
                                NavDirections action = HomeFragmentDirections.actionHomeFragmentToPaymentReceiptHistoryFragment("Receipt AdvanceInv History", 17);
                                navController.navigate(R.id.homeFragment);
                                navController.navigate(action);
                            } else {
                                Toast.makeText(Home.this, "please sync  Data's", Toast.LENGTH_SHORT).show();
                            }
                        }
                        break;
                    }
                    drawer.closeDrawer(GravityCompat.START);
                    return true;
                }

                if(i==2){
                    switch (i1){
                        case 0:{
                            tagCursor=helper.getTagDetails();
                            if(tagCursor.moveToFirst() && tagCursor.getCount()>0) {
                                    NavDirections action = HomeFragmentDirections.actionHomeFragmentToSalesPurchaseHistoryFragment("Purchase History").setIDocType(10);
                                    navController.navigate(R.id.homeFragment);
                                    navController.navigate(action);
                            }else {
                                Toast.makeText(Home.this, "please sync  Data's", Toast.LENGTH_SHORT).show();
                            }
                        }break;


                        case 1:{
                            tagCursor=helper.getTagDetails();
                            if(tagCursor.getCount()>0) {
                                    NavDirections action=HomeFragmentDirections.actionHomeFragmentToPaymentReceiptHistoryFragment("Payment History",15);
                                    navController.navigate(R.id.homeFragment);
                                    navController.navigate(action);

                            }else {
                                Toast.makeText(Home.this, "please sync  Data's", Toast.LENGTH_SHORT).show();
                            }
                        }break;

                        case 2:{
                            tagCursor = helper.getTagDetails();
                            if (tagCursor.getCount() > 0) {
                                NavDirections action = HomeFragmentDirections.actionHomeFragmentToSalesPurchaseReturnHistoryFragment("Purchase Return History", 11);
                                navController.navigate(R.id.homeFragment);
                                navController.navigate(action);
                            } else {
                                Toast.makeText(Home.this, "please sync  Data's", Toast.LENGTH_SHORT).show();
                            }
                        }break;

                        case 3:{
                            tagCursor = helper.getTagDetails();
                            if (tagCursor.getCount() > 0) {
                                NavDirections action = HomeFragmentDirections.actionHomeFragmentToSPOrderHistoryFragment(12, "Purchase Order History");
                                navController.navigate(R.id.homeFragment);
                                navController.navigate(action);
                            }else {
                                Toast.makeText(Home.this, "please sync  Data's", Toast.LENGTH_SHORT).show();
                            }
                        }break;

                        case 4:{
                            tagCursor = helper.getTagDetails();
                            if (tagCursor.getCount() > 0) {
                                NavDirections action = HomeFragmentDirections.actionHomeFragmentToRequestHistoryFragment(13, "Request History");
                                navController.navigate(R.id.homeFragment);
                                navController.navigate(action);
                            }
                            else {
                                Toast.makeText(Home.this, "please sync Data's", Toast.LENGTH_SHORT).show();
                            }
                        }break;

                        case  5:{
                            tagCursor = helper.getTagDetails();
                            if (tagCursor.getCount() > 0) {
                                NavDirections action = HomeFragmentDirections
                                        .actionHomeFragmentToQuotationHistoryFragment(14, "Purchase Quotation History");
                                navController.navigate(R.id.homeFragment);
                                navController.navigate(action);
                            }
                            else {
                                Toast.makeText(Home.this, "please sync  Data's", Toast.LENGTH_SHORT).show();
                            }

                        }break;


                    }

                    drawer.closeDrawer(GravityCompat.START);
                    return true;
                }
            return false;
            }
        });
        expandableList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                Log.d("DEBUG_G", "heading clicked "+i);

                switch (i){
                    case 0:{
                            navController.navigate(R.id.homeFragment);
                            drawer.closeDrawer(GravityCompat.START);

                    }break;
                    case 3:{
                        if(Tools.isConnected(Home.this)) {
                            syncTag();
                            drawer.closeDrawer(GravityCompat.START);
                            return true;
                        }
                        else {
                            Snackbar snackbar=Snackbar.make(getWindow().getDecorView().getRootView(),"Offline",Snackbar.LENGTH_LONG);
                            snackbar.setTextColor(Color.WHITE);
                            snackbar.setBackgroundTint(Color.RED);
                            snackbar.show();
                        }
                    }break;
                    case 4:{
                        tagCursor = helper.getTagDetails();
                        if (tagCursor.getCount() > 0) {
                            NavDirections action = HomeFragmentDirections
                                    .actionHomeFragmentToStockCountHistoryFragment(40);
                            navController.navigate(R.id.homeFragment);
                            navController.navigate(action);
                            drawer.closeDrawer(GravityCompat.START);
                            return true;
                        }
                        else {
                            Toast.makeText(Home.this, "please sync Datas", Toast.LENGTH_SHORT).show();
                        }
                    }break;
                    case 5:{
                        tagCursor=helper.getTagDetails();
                        if(tagCursor.getCount()>0) {
                                NavDirections action= HomeFragmentDirections.actionHomeFragmentToReportSelectionFragment();
                                navController.navigate(R.id.homeFragment);
                                navController.navigate(action);
                            drawer.closeDrawer(GravityCompat.START);
                            return true;
                        }else {
                            Toast.makeText(Home.this, "please sync Datas", Toast.LENGTH_SHORT).show();
                        }
                    }break;
                    case 6:{
                        NavDirections action= HomeFragmentDirections.actionHomeFragmentToMasterHistoryFragment();
                        navController.navigate(R.id.homeFragment);
                        navController.navigate(action);
                        drawer.closeDrawer(GravityCompat.START);
                    }
                    break;

                }
                return false;
            }
        });

        final Handler handler = new Handler();
        final int delay = 1000*30; // 1000 milliseconds == 1 second
        handler.postDelayed(new Runnable() {
            public void run() {
                Log.d("kkk","kk");
                if(Tools.isConnected(Home.this)){
                    Log.d("kkk","connected");
                    schedulepost.Post_CustomerMaster(Home.this);
                    schedulepost.Post_SalePurchase(Home.this);
                    schedulepost.Post_PaymentReceipt(Home.this);
                    schedulepost.Post_SalePurchase_Return(Home.this);
                    schedulepost.Post_SalePurchase_Order(Home.this);
                    schedulepost.Post_EnquiryRequest(Home.this);
                    schedulepost.Post_Quotation(Home.this);

                    schedulepost.Post_StockCount(Home.this);

                }
                handler.postDelayed(this, delay);
            }
        }, delay);



        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.homeFragment, R.id.salesPurchaseHistoryFragment,
                R.id.purchaseFragment,R.id.report_selection_fragment,
                R.id.paymentReceiptHistoryFragment,R.id.ReceiptHistoryFragment,
                R.id.salesPurchaseReturnHistoryFragment,R.id.PurchaseReturnHistoryFragment,
                R.id.s_P_OrderHistoryFragment,R.id.requestHistoryFragment,R.id.quotationHistoryFragment,
                R.id.stockCountHistoryFragment,R.id.masterHistoryFragment,R.id.advanceInvoiceHistoryFragment)
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

                else  if(navController.getCurrentDestination().getId()==R.id.requestFragment){
                    NavDirections action= RequestFragmentDirections.actionRequestFragmentToHomeFragment();
                    navController.navigate(action);
                }
                else if(navController.getCurrentDestination().getId()==R.id.requestHistoryFragment){
                    NavDirections action=RequestHistoryFragmentDirections.actionRequestHistoryFragmentToHomeFragment();
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
                else if(navController.getCurrentDestination().getId()==R.id.masterFragment){
                    NavDirections action= MasterFragmentDirections.actionMasterFragmentToHomeFragment();
                    navController.navigate(action);
                }
                else if(navController.getCurrentDestination().getId()==R.id.masterHistoryFragment){
                    NavDirections action=  MasterHistoryFragmentDirections.actionMasterHistoryFragmentToHomeFragment();
                    navController.navigate(action);
                }


                }
                });


//                    binding.navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
//                    @Override
//                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
//                    switch(menuItem.getItemId()){
//
//                }
//                drawer.closeDrawer(GravityCompat.START);
//                return true;
//            }
//            });
            }

    private void prepareListData() {
        listDataHeader = new ArrayList<ExpandedMenuModel>();
        listDataChild = new HashMap<ExpandedMenuModel, List<String>>();

        ExpandedMenuModel item = new ExpandedMenuModel();
        item.setIconName("Home");
        item.setIconImg(R.drawable.ic_home);
        // Adding data header
        listDataHeader.add(item);

        ExpandedMenuModel item1 = new ExpandedMenuModel();
        item1.setIconName("Sales");
        item1.setIconImg(R.drawable.ic_sales);
        // Adding data header
        listDataHeader.add(item1);

        ExpandedMenuModel item2 = new ExpandedMenuModel();
        item2.setIconName("Purchase");
        item2.setIconImg(R.drawable.ic_purchase);
        listDataHeader.add(item2);

        ExpandedMenuModel item3 = new ExpandedMenuModel();
        item3.setIconName("Sync");
        item3.setIconImg(R.drawable.ic_sync);
        listDataHeader.add(item3);

        ExpandedMenuModel item4= new ExpandedMenuModel();
        item4.setIconName("StockCount");
        item4.setIconImg(R.drawable.ic_stock_count);
        listDataHeader.add(item4);


        ExpandedMenuModel item5 = new ExpandedMenuModel();
        item5.setIconName("Report");
        item5.setIconImg(R.drawable.ic_report);
        listDataHeader.add(item5);

        ExpandedMenuModel item6 = new ExpandedMenuModel();
        item6.setIconName("Master Setting");
        item6.setIconImg(R.drawable.ic_profile);
        // Adding data header
        listDataHeader.add(item6);

//        ExpandedMenuModel item7 = new ExpandedMenuModel();
//        item7.setIconName("Advance Invoice");
//        item7.setIconImg(R.drawable.ic_sales);
//        // Adding data header
//        listDataHeader.add(item7);



        // Adding child data
        List<String> heading1 = new ArrayList<String>();
        heading1.add("Sales");
        heading1.add("Receipt");
        heading1.add("Sales Return");
        heading1.add("Sales Order");
        heading1.add("Enquiry");
        heading1.add("Sales Quotation");
        heading1.add("Advance Invoice");
        heading1.add("Receipt Advance Invoice");

        List<String> heading2 = new ArrayList<String>();
        heading2.add("Purchase");
        heading2.add("Payment");
        heading2.add("Purchase Return");
        heading2.add("Purchase Order");
        heading2.add("Request");
        heading2.add("Purchase Quotation");

        listDataChild.put(listDataHeader.get(1), heading1);// Header, Child data
        listDataChild.put(listDataHeader.get(2), heading2);

    }




    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void syncTag() {

        Log.d("ipppp","new Tools().getIP(Login.this)");

//        schedulerJob.SyncToken(this);
        schedulerJob.SyncMasterSettings(Home.this);
        schedulerJob.SyncAccounts(Home.this);
        schedulerJob.SyncProduct(Home.this);
        schedulerJob.SyncTransSalePurchase(Home.this);
        schedulerJob.SyncBank(Home.this);
        schedulerJob.syncMasterTagDetails(Home.this);
        schedulerJob.SyncUser(Home.this);
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
            else if(navController.getCurrentDestination().getId()==R.id.requestFragment){
                backAlert();
                return true;
            }
            else if(navController.getCurrentDestination().getId()==R.id.masterFragment){
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