package com.sangsolutions.sang;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.sangsolutions.sang.Adapter.Accounts.Accounts;
import com.sangsolutions.sang.Adapter.MasterSettings.MasterSettings;
import com.sangsolutions.sang.Adapter.Products.Products;
import com.sangsolutions.sang.Adapter.TransSalePurchase.TransSetting;

public class DatabaseHelper extends SQLiteOpenHelper {

    Context context;
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Sang.db";
    private static final String TABLE_MASTER_SETTINGS = "t1_masterSettings";
    private static final String TABLE_ACCOUNTS = "t1_accounts";
    private static final String TABLE_PRODUCT = "t1_product";
    private static final String TABLE_TRANSACTION_SETTING = "t1_transactionSetting";



    private static final String CREATE_TABLE_MASTER_SETTINGS=" create table if not exists " + TABLE_MASTER_SETTINGS + " (" +
            "" +MasterSettings.I_ID + " INTEGER DEFAULT 0, " +
            "" + MasterSettings.S_NAME + " TEXT(50) DEFAULT null , " +
            "" + MasterSettings.S_ALT_NAME +  " TEXT(50) DEFAULT null  "+ ");";

    private static final String CREATE_TABLE_ACCOUNTS=" create table if not exists " + TABLE_ACCOUNTS + " (" +
            "" + Accounts.I_ID + " INTEGER DEFAULT 0, " +
            "" + Accounts.S_NAME + " TEXT(50) DEFAULT null , " +
            "" + Accounts.S_CODE + " TEXT(50) DEFAULT null , " +
            "" + Accounts.S_ALT_NAME +  " TEXT(50) DEFAULT null  "+ ");";

    private static final String CREATE_TABLE_PRODUCT=" create table if not exists " + TABLE_PRODUCT + " (" +
            "" + Products.I_ID + " INTEGER DEFAULT 0, " +
            "" + Products.S_NAME + " TEXT(50) DEFAULT null , " +
            "" + Products.S_CODE + " TEXT(50) DEFAULT null , " +
            "" + Products.S_ALT_NAME +  " TEXT(50) DEFAULT null  "+ ");";

    private static final String CREATE_TRANSACTION_SETTING=" create table if not exists " + TABLE_TRANSACTION_SETTING + " (" +
            "" + TransSetting.I_DOC_TYPE + " INTEGER DEFAULT 0, " +
            "" + TransSetting.I_TAG_ID + " INTEGER DEFAULT 0, " +
            "" + TransSetting.B_MANDATORY + " TEXT(50) DEFAULT null , " +
            "" + TransSetting.B_VISIBLE +  " TEXT(50) DEFAULT null  "+ ");";

    private SQLiteDatabase db;

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db=db;
        db.execSQL(CREATE_TABLE_MASTER_SETTINGS);
        db.execSQL(CREATE_TABLE_ACCOUNTS);
        db.execSQL(CREATE_TABLE_PRODUCT);
        db.execSQL(CREATE_TRANSACTION_SETTING);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean insertMasterSettings(MasterSettings settings) {

        this.db=getReadableDatabase();
        ContentValues cv=new ContentValues();
        cv.put(MasterSettings.I_ID,settings.getiId());
        cv.put(MasterSettings.S_NAME,settings.getsName());
        cv.put(MasterSettings.S_ALT_NAME,settings.getsAltName());
        float status = db.insert(TABLE_MASTER_SETTINGS, null, cv);
        return status != -1;

    }

    public boolean checkMasterById(String id) {
        this.db=getReadableDatabase();
        Cursor cursor=db.rawQuery("select  * from "+TABLE_MASTER_SETTINGS+
                        " where "+MasterSettings.I_ID+"="+id,null);
        return cursor.getCount() > 0;
    }

    public boolean checkAllDataMaster(MasterSettings settings) {
        this.db=getReadableDatabase();
        float status = 0;
        ContentValues cv=new ContentValues();
        cv.put(MasterSettings.I_ID,settings.getiId());
        cv.put(MasterSettings.S_NAME,settings.getsName());
        cv.put(MasterSettings.S_ALT_NAME,settings.getsAltName());

//        Cursor cursor=db.rawQuery("select  * from "+TABLE_MASTER_SETTINGS+
//                " where "+MasterSettings.I_ID+"="+settings.getiId()+" and "+MasterSettings.S_NAME+
//                " = '"+settings.getsName()+"' and "+ MasterSettings.S_ALT_NAME+" = '"+settings.getsAltName()+"'",null);
//        if(cursor.getCount()>0){
//            return true;
//        }
//        else {
//            if(cursor==null){
        status=db.update(TABLE_MASTER_SETTINGS,cv,MasterSettings.I_ID+" =? ",new String[]{String.valueOf(settings.getiId())});

        //            }
//        }
        return status!=-1;
    }

    public boolean insertAccounts(Accounts accounts) {
        this.db=getReadableDatabase();
        ContentValues cv=new ContentValues();
        cv.put(Accounts.I_ID,accounts.getiId());
        cv.put(Accounts.S_NAME,accounts.getsName());
        cv.put(Accounts.S_CODE,accounts.getsCode());
        cv.put(Accounts.S_ALT_NAME,accounts.getsAltName());
        float status = db.insert(TABLE_ACCOUNTS, null, cv);
        return status != -1;
    }

    public boolean checkAccountsById(String id) {
        this.db=getReadableDatabase();
        Cursor cursor=db.rawQuery("select  * from "+TABLE_ACCOUNTS+
                " where "+Accounts.I_ID+"="+id,null);
        return cursor.getCount() > 0;
    }

    public boolean checkAllDataAccounts(Accounts accounts) {
        this.db=getReadableDatabase();
        float status = 0;
        ContentValues cv=new ContentValues();
        cv.put(Accounts.I_ID,accounts.getiId());
        cv.put(Accounts.S_NAME,accounts.getsName());
        cv.put(Accounts.S_ALT_NAME,accounts.getsAltName());
        cv.put(Accounts.S_CODE,accounts.getsCode());
        status=db.update(TABLE_ACCOUNTS,cv,Accounts.I_ID+" =? ",new String[]{String.valueOf(accounts.getiId())});
        return status!=-1;

    }

    public boolean insertProducts(Products products) {
        this.db=getReadableDatabase();
        ContentValues cv=new ContentValues();
        cv.put(Products.I_ID,products.getiId());
        cv.put(Products.S_NAME,products.getsName());
        cv.put(Products.S_CODE,products.getsCode());
        cv.put(Products.S_ALT_NAME,products.getsAltName());
        float status = db.insert(TABLE_PRODUCT, null, cv);
        return status != -1;
    }

    public boolean checkProductsById(String id) {
        this.db=getReadableDatabase();
        Cursor cursor=db.rawQuery("select  * from "+TABLE_PRODUCT+
                " where "+Products.I_ID+"="+id,null);
        return cursor.getCount() > 0;    }

    public boolean checkAllDataProducts(Products products) {
        this.db=getReadableDatabase();
        float status ;
        ContentValues cv=new ContentValues();
        cv.put(Products.I_ID,products.getiId());
        cv.put(Products.S_NAME,products.getsName());
        cv.put(Products.S_ALT_NAME,products.getsAltName());
        cv.put(Products.S_CODE,products.getsAltName());
        status=db.update(TABLE_ACCOUNTS,cv,Products.I_ID+" =? ",new String[]{String.valueOf(products.getiId())});
        return status!=-1;
    }

    public boolean insertTransSetting(TransSetting transSetting) {

        this.db=getReadableDatabase();
        ContentValues cv=new ContentValues();
        cv.put(TransSetting.I_DOC_TYPE,transSetting.getiDocType());
        cv.put(TransSetting.I_TAG_ID,transSetting.getiTagId());
        cv.put(TransSetting.B_MANDATORY,transSetting.isbMandatory());
        cv.put(TransSetting.B_VISIBLE,transSetting.isbVisible());
        float status = db.insert(TABLE_TRANSACTION_SETTING, null, cv);
        return status != -1;
    }

    public boolean checkTransSettingById(String iDocType, String iTagId) {
        this.db=getReadableDatabase();
        Cursor cursor=db.rawQuery("select  * from "+TABLE_TRANSACTION_SETTING+
                " where "+TransSetting.I_DOC_TYPE+"=? and "+TransSetting.I_TAG_ID+"=? ",new String[]{iDocType,iTagId});
        return cursor.getCount() > 0;
    }

    public boolean checkAllDataTransSetting(TransSetting transSetting) {
        this.db=getReadableDatabase();
        float status ;
        ContentValues cv=new ContentValues();
        cv.put(TransSetting.I_DOC_TYPE,transSetting.getiDocType());
        cv.put(TransSetting.I_TAG_ID,transSetting.getiTagId());
        cv.put(TransSetting.B_MANDATORY,transSetting.isbMandatory());
        cv.put(TransSetting.B_VISIBLE,transSetting.isbVisible());
        status=db.update(TABLE_TRANSACTION_SETTING,cv,TransSetting.I_DOC_TYPE+" =? and "+TransSetting.I_TAG_ID+"=?",
                new String[]{String.valueOf(transSetting.getiDocType()),String.valueOf(transSetting.getiDocType())});
        return status!=-1;
    }
}
