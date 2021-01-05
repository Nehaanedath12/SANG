package com.sangsolutions.sang;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
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
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.sangsolutions.sang.Database.DatabaseHelper;
import com.sangsolutions.sang.Fragment.HomeFragmentDirections;
import com.sangsolutions.sang.Fragment.SalesPurchaseFragmentDirections;
import com.sangsolutions.sang.databinding.ActivityMainBinding;

import java.util.Set;


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
        else {
            super.onBackPressed();
        }
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
                R.id.homeFragment, R.id.salesPurchaseFragment, R.id.purchaseFragment).setDrawerLayout(drawer).build();
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this,drawer,toolbar,
                R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        binding.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutAlert();
            }
        });

        binding.home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (navController.getCurrentDestination().getId() != R.id.homeFragment) {
                    navController.navigateUp();

                }
            }
        });
                    binding.navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    NavDirections action;
                    switch(menuItem.getItemId()){

                    case R.id.purchaseFragment:
                    {
                        if(navController.getCurrentDestination().getId() !=R.id.salesPurchaseFragment){
                            action=HomeFragmentDirections.actionHomeFragmentToSalesPurchaseFragment("Purchase History").setIDocType(1);
                            navController.navigate(action);
                        }
                        else {
                            action=HomeFragmentDirections.actionHomeFragmentToSalesPurchaseFragment("Purchase History").setIDocType(1);
                            navController.navigateUp();
                            navController.navigate(action);
                        }
                    }
                    break;
                    case R.id.salesPurchaseFragment:
                    {
                        if(navController.getCurrentDestination().getId() !=R.id.salesPurchaseFragment){
                            action=HomeFragmentDirections.actionHomeFragmentToSalesPurchaseFragment("Sale History").setIDocType(2);
                            navController.navigate(action);
                        }
                        else{
                            action=HomeFragmentDirections.actionHomeFragmentToSalesPurchaseFragment("Sale History").setIDocType(2);
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
//    @Override
//    public boolean onSupportNavigateUp() {
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
//                || super.onSupportNavigateUp();
//    }
}