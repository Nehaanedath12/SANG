package com.sangsolutions.sang;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
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
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.navigation.NavigationView;
import com.sangsolutions.sang.Database.DatabaseHelper;
import com.sangsolutions.sang.Fragment.HomeFragmentDirections;
import com.sangsolutions.sang.Fragment.Sale_Purchase_FragmentDirections;
import com.sangsolutions.sang.Fragment.SalesPurchaseHistoryFragmentDirections;
import com.sangsolutions.sang.databinding.ActivityMainBinding;


public class Home extends AppCompatActivity {
    ActivityMainBinding binding;
    DatabaseHelper helper;
    Toolbar toolbar;
    NavController navController;
    AppBarConfiguration mAppBarConfiguration;
    NavigationView navigationView;
    DrawerLayout drawer;

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
//        else {
//            super.onBackPressed();
//        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);
        helper=new DatabaseHelper(this);

        toolbar=binding.toolbar;
        drawer=binding.drawerLayout;
        navigationView=binding.navView;
        setSupportActionBar(toolbar);
        navController = Navigation.findNavController(this,R.id.nav_host_fragment);


        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.homeFragment, R.id.salesPurchaseHistoryFragment, R.id.purchaseFragment).setDrawerLayout(drawer).build();
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


//        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this,drawer,toolbar,
//                R.string.navigation_drawer_open,R.string.navigation_drawer_close);
//        drawer.addDrawerListener(toggle);
//        toggle.syncState();


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
                }
                });
                    binding.navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    switch(menuItem.getItemId()){

                    case R.id.purchaseFragment:
                    {
                        if(navController.getCurrentDestination().getId() !=R.id.salesPurchaseHistoryFragment){
                            NavDirections  action=HomeFragmentDirections.actionHomeFragmentToSalesPurchaseHistoryFragment().setIDocType(1);
                            navController.navigate(action);
                        }
                        else {
                            NavDirections action=HomeFragmentDirections.actionHomeFragmentToSalesPurchaseHistoryFragment().setIDocType(1);
                            navController.navigateUp();
                            navController.navigate(action);
                        }

                    }
                    break;
                    case R.id.salesPurchaseHistoryFragment:
                    {
                        if(navController.getCurrentDestination().getId() !=R.id.salesPurchaseHistoryFragment){
                            NavDirections action=HomeFragmentDirections.actionHomeFragmentToSalesPurchaseHistoryFragment().setIDocType(2);
                            navController.navigate(action);
                        }
                        else{

                            NavDirections  action=HomeFragmentDirections.actionHomeFragmentToSalesPurchaseHistoryFragment().setIDocType(2);
                            navController.navigateUp();
                            navController.navigate(action);
                        }

                    }
                    break;

                    case R.id.homeFragment:
                    {
                        if (navController.getCurrentDestination().getId() != R.id.homeFragment) {
                            navController.navigateUp();
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