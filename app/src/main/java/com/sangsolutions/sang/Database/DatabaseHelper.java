package com.sangsolutions.sang.Database;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

import com.sangsolutions.sang.Adapter.BankAdapter.Bank;
import com.sangsolutions.sang.Adapter.Customer.Customer;
import com.sangsolutions.sang.Adapter.MasterSettings.MasterSettings;
import com.sangsolutions.sang.Adapter.Products.Products;
import com.sangsolutions.sang.Adapter.TagDetailsAdapter.TagDetails;
import com.sangsolutions.sang.Adapter.TransSalePurchase.TransSetting;
import com.sangsolutions.sang.Adapter.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DatabaseHelper extends SQLiteOpenHelper {

    Context context;
    private static final int DATABASE_VERSION = 6;
    private static final String DATABASE_NAME = "Sang.db";
    private static final String TABLE_MASTER_SETTINGS = "t1_masterSettings";
    private static final String TABLE_ACCOUNTS = "t1_accounts";
    private static final String TABLE_PRODUCT = "t1_product";
    private static final String TABLE_TRANSACTION_SETTING = "t1_transactionSetting";
    private static final String TABLE_USER = "t1_user";
    private static  final String TABLE_CURRENT_LOGIN = "t1_currentLogin";
    private static  final String TABLE_TAG_DETAILS = "t1_tag_details";
    private static  final String TABLE_BANK = "t1_bank";

    private static  final String TABLE_SALE_PURCHASE_HEADER = "t1_sale_purchase_Header";
    private static  final String TABLE_SALE_PURCHASE_BODY = "t1_sale_purchase_Body";
    private static  final String TABLE_PAYMENT_RECEIPT_HEADER = "t1_payment_receipt_Header";
    private static  final String TABLE_PAYMENT_RECEIPT_BODY = "t1_payment_receipt_body";
    private static  final String TABLE_SALE_PURCHASE_RETURN_HEADER = "t1_sale_purchase_return_Header";
    private static  final String TABLE_SALE_PURCHASE_RETURN_BODY = "t1_sale_purchase_return_Body";

    private static  final String TABLE_SALE_PURCHASE_ORDER_HEADER = "t1_sale_purchase_order_Header";
    private static  final String TABLE_SALE_PURCHASE_ORDER_BODY = "t1_sale_purchase_order_Body";



    private static  final String IID = "iId";
    private static  final String USER_ID = "user_Id";


    private static final String CREATE_TABLE_USER=" create table if not exists " + TABLE_USER + " (" +
            "" + User.I_ID + " INTEGER DEFAULT 0, " +
            "" + User.I_STATUS + " INTEGER DEFAULT 0, " +
            "" + User.S_LOGIN_NAME  + " TEXT(50) DEFAULT null , " +
            "" + User.S_PASSWORD  + " TEXT(50) DEFAULT null , " +
            "" + User.S_USERNAME  + " TEXT(50) DEFAULT null , " +
            "" + User.USER_CODE  + " TEXT(50) DEFAULT null , " +
            "" + User.B_MOB  + " TEXT(50) DEFAULT null , " +
            "" + User.B_WEB  +  " TEXT(50) DEFAULT null  "+ ");";

    private static final String CREATE_TABLE_CURRENT_LOGIN = "create table if not exists  " + TABLE_CURRENT_LOGIN + " (" +
            "" + IID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            "" + USER_ID + " TEXT(20) DEFAULT null  " +
            ")";


    private static final String CREATE_TABLE_MASTER_SETTINGS=" create table if not exists " + TABLE_MASTER_SETTINGS + " (" +
            "" +MasterSettings.I_ID + " INTEGER DEFAULT 0, " +
            "" + MasterSettings.S_NAME + " TEXT(50) DEFAULT null , " +
            "" + MasterSettings.S_ALT_NAME +  " TEXT(50) DEFAULT null  "+ ");";

    private static final String CREATE_TABLE_ACCOUNTS=" create table if not exists " + TABLE_ACCOUNTS + " (" +
            "" + Customer.I_ID + " INTEGER DEFAULT 0, " +
            "" + Customer.S_NAME + " TEXT(50) DEFAULT null , " +
            "" + Customer.S_CODE + " TEXT(50) DEFAULT null , " +
            "" + Customer.S_ALT_NAME +  " TEXT(50) DEFAULT null  "+ ");";

    private static final String CREATE_TABLE_PRODUCT=" create table if not exists " + TABLE_PRODUCT + " (" +
            "" + Products.I_ID + " INTEGER DEFAULT 0, " +
            "" + Products.S_NAME + " TEXT(50) DEFAULT null , " +
            "" + Products.S_CODE + " TEXT(50) DEFAULT null , " +
            "" + Products.S_UNIT + " TEXT(50) DEFAULT null , " +
            "" + Products.S_BARCODE + " TEXT(50) DEFAULT null , " +
            "" + Products.S_ALT_NAME +  " TEXT(50) DEFAULT null  "+ ");";

    private static final String CREATE_TRANSACTION_SETTING=" create table if not exists " + TABLE_TRANSACTION_SETTING + " (" +
            "" + TransSetting.I_DOC_TYPE + " INTEGER DEFAULT 0, " +
            "" + TransSetting.I_TAG_ID + " INTEGER DEFAULT 0, " +
            "" + TransSetting.I_TAG_POSITION + " INTEGER DEFAULT 0, " +
            "" + TransSetting.B_MANDATORY + " TEXT(50) DEFAULT null , " +
            "" + TransSetting.B_VISIBLE +  " TEXT(50) DEFAULT null  "+ ");";

    private static final String CREATE_TABLE_TAG_DETAILS =" create table if not exists " + TABLE_TAG_DETAILS + " (" +
            "" + TagDetails.I_ID + " INTEGER DEFAULT 0, " +
            "" + TagDetails.S_NAME + " TEXT(50) DEFAULT null , " +
            "" + TagDetails.S_CODE + " TEXT(50) DEFAULT null , " +
            "" + TagDetails.I_TYPE + " TEXT(50) DEFAULT null , " +
            "" + TagDetails.S_ALT_NAME +  " TEXT(50) DEFAULT null  "+ ");";

    private static final String CREATE_TABLE_BANK=" create table if not exists " + TABLE_BANK + " (" +
            "" + Bank.I_ID + " INTEGER DEFAULT 0, " +
            "" + Bank.S_NAME + " TEXT(50) DEFAULT null , " +
            "" + Bank.S_CODE + " TEXT(50) DEFAULT null  "+ ");";



    private static final String CREATE_TABLE_SALE_PURCHASE_HEADER =" create table if not exists " + TABLE_SALE_PURCHASE_HEADER + " (" +
            "" + Sales_purchase_Class.I_TRANS_ID + " INTEGER DEFAULT 0, " +
            "" + Sales_purchase_Class.I_ACCOUNT_1 +  " INTEGER DEFAULT 0, " +
            "" + Sales_purchase_Class.I_ACCOUNT_2 +  " INTEGER DEFAULT 0, " +
            "" + Sales_purchase_Class.I_DOC_TYPE + " INTEGER DEFAULT 0, " +
            "" + Sales_purchase_Class.S_DATE +  " TEXT(50) DEFAULT null , "  +
            "" + Sales_purchase_Class.S_NARRATION + " TEXT(50) DEFAULT null , " +
            "" + Sales_purchase_Class.S_DOC_NO + " TEXT(50) DEFAULT null, "  +
            "" + Sales_purchase_Class.PROCESS_TIME + " TEXT(50) DEFAULT null, "  +
            "" + Sales_purchase_Class.STATUS +  " INTEGER DEFAULT 0 " +

            ");";

    private static final String CREATE_TABLE_SALE_PURCHASE_BODY =" create table if not exists " + TABLE_SALE_PURCHASE_BODY + " (" +
            "" + Sales_purchase_Class.I_TAG_1 + " INTEGER DEFAULT 0, " +
            "" + Sales_purchase_Class.I_TAG_2 + " INTEGER DEFAULT 0, " +
            "" + Sales_purchase_Class.I_TAG_3 + " INTEGER DEFAULT 0, " +
            "" + Sales_purchase_Class.I_TAG_4 + " INTEGER DEFAULT 0, " +
            "" + Sales_purchase_Class.I_TAG_5 + " INTEGER DEFAULT 0, " +
            "" + Sales_purchase_Class.I_TAG_6 + " INTEGER DEFAULT 0, " +
            "" + Sales_purchase_Class.I_TAG_7 + " INTEGER DEFAULT 0, " +
            "" + Sales_purchase_Class.I_TAG_8 + " INTEGER DEFAULT 0, " +

            "" + Sales_purchase_Class.I_PRODUCT +  " INTEGER DEFAULT 0, " +
            "" + Sales_purchase_Class.F_QTY+  " INTEGER DEFAULT 0, " +
            "" + Sales_purchase_Class.F_RATE + "  TEXT(50) DEFAULT null , " +
            "" + Sales_purchase_Class.F_DISCOUNT +  " TEXT(50) DEFAULT null , "  +
            "" + Sales_purchase_Class.F_ADD_CHARGES + " TEXT(50) DEFAULT null , " +
            "" + Sales_purchase_Class.F_VAT_PER + " TEXT(50) DEFAULT null ,"  +
            "" + Sales_purchase_Class.F_VAT + " TEXT(50) DEFAULT null ,"  +
            "" + Sales_purchase_Class.S_REMARKS + " TEXT(50) DEFAULT null ,"  +
            "" + Sales_purchase_Class.S_UNITS + " TEXT(50) DEFAULT null ,"  +
            "" + Sales_purchase_Class.F_NET + " TEXT(50) DEFAULT null ,"  +
            "" + Sales_purchase_Class.I_DOC_TYPE + " INTEGER DEFAULT 0, " +
            "" + Sales_purchase_Class.S_DOC_NO + " TEXT(50) DEFAULT null, "  +
            "" + Sales_purchase_Class.I_TRANS_ID + " INTEGER DEFAULT 0 " +

            ");";


    private static final String CREATE_TABLE_PAYMENT_RECEIPT_HEADER=" create table if not exists " + TABLE_PAYMENT_RECEIPT_HEADER + " (" +


            "" + Payment_Receipt_class.I_TRANS_ID + " INTEGER DEFAULT 0, " +
            "" + Payment_Receipt_class.I_ACCOUNT_1 +  " INTEGER DEFAULT 0, " +
            "" + Payment_Receipt_class.I_ACCOUNT_2 +  " INTEGER DEFAULT 0, " +
            "" + Payment_Receipt_class.I_DOC_TYPE + " INTEGER DEFAULT 0, " +
            "" + Payment_Receipt_class.S_DATE +  " TEXT(50) DEFAULT null , "  +
            "" + Payment_Receipt_class.S_NARRATION + " TEXT(50) DEFAULT null , " +
            "" + Payment_Receipt_class.S_DOC_NO + " TEXT(50) DEFAULT null, "  +
            "" + Payment_Receipt_class.PROCESS_TIME + " TEXT(50) DEFAULT null, "  +
            "" + Payment_Receipt_class.STATUS +  " INTEGER DEFAULT 0, " +

            "" + Payment_Receipt_class.I_CHEQUE_NO+  " INTEGER DEFAULT 0, " +
            "" + Payment_Receipt_class.F_AMOUNT +  " TEXT (50) DEFAULT null, " +
            "" + Payment_Receipt_class.I_PAYMENT_METHOD +  " INTEGER DEFAULT 0, " +
            "" + Payment_Receipt_class.I_BANK +  " INTEGER DEFAULT 0, " +
            "" + Payment_Receipt_class.S_CHEQUE_DATE + " TEXT(50) DEFAULT null, " +
            "" + Payment_Receipt_class.S_ATTACHMENT+ " TEXT(50) DEFAULT null" +

            ");";


    private static final String CREATE_TABLE_PAYMENT_RECEIPT_BODY=" create table if not exists " + TABLE_PAYMENT_RECEIPT_BODY + " (" +
            "" + Payment_Receipt_class.I_TAG_1 + " INTEGER DEFAULT 0, " +
            "" + Payment_Receipt_class.I_TAG_2 + " INTEGER DEFAULT 0, " +
            "" + Payment_Receipt_class.I_TAG_3 + " INTEGER DEFAULT 0, " +
            "" + Payment_Receipt_class.I_TAG_4 + " INTEGER DEFAULT 0, " +
            "" + Payment_Receipt_class.I_TAG_5 + " INTEGER DEFAULT 0, " +
            "" + Payment_Receipt_class.I_TAG_6 + " INTEGER DEFAULT 0, " +
            "" + Payment_Receipt_class.I_TAG_7 + " INTEGER DEFAULT 0, " +
            "" + Payment_Receipt_class.I_TAG_8 + " INTEGER DEFAULT 0, " +

            "" + Payment_Receipt_class.I_TRANS_ID + " INTEGER DEFAULT 0, " +
            "" + Payment_Receipt_class.I_DOC_TYPE + " INTEGER DEFAULT 0, " +
            "" + Payment_Receipt_class.S_NARRATION + " TEXT(50) DEFAULT null , " +
            "" + Payment_Receipt_class.S_DOC_NO + " TEXT(50) DEFAULT null, "  +
            "" + Payment_Receipt_class.I_REF_DOC_ID + " INTEGER DEFAULT 0, " +
            "" + Payment_Receipt_class.F_AMOUNT + " TEXT(50) DEFAULT null  " +

            ");";

    private static final String CREATE_TABLE_SALE_PURCHASE_RETURN_HEADER =" create table if not exists " + TABLE_SALE_PURCHASE_RETURN_HEADER + " (" +
            "" + Sales_purchase_Class.I_TRANS_ID + " INTEGER DEFAULT 0, " +
            "" + Sales_purchase_Class.I_ACCOUNT_1 +  " INTEGER DEFAULT 0, " +
            "" + Sales_purchase_Class.I_ACCOUNT_2 +  " INTEGER DEFAULT 0, " +
            "" + Sales_purchase_Class.I_DOC_TYPE + " INTEGER DEFAULT 0, " +
            "" + Sales_purchase_Class.S_DATE +  " TEXT(50) DEFAULT null , "  +
            "" + Sales_purchase_Class.S_NARRATION + " TEXT(50) DEFAULT null , " +
            "" + Sales_purchase_Class.S_DOC_NO + " TEXT(50) DEFAULT null, "  +
            "" + Sales_purchase_Class.PROCESS_TIME + " TEXT(50) DEFAULT null, "  +
            "" + Sales_purchase_Class.STATUS +  " INTEGER DEFAULT 0 " +

            ");";

    private static final String CREATE_TABLE_SALE_PURCHASE_RETURN_BODY =" create table if not exists " + TABLE_SALE_PURCHASE_RETURN_BODY + " (" +
            "" + Sales_purchase_Class.I_TAG_1 + " INTEGER DEFAULT 0, " +
            "" + Sales_purchase_Class.I_TAG_2 + " INTEGER DEFAULT 0, " +
            "" + Sales_purchase_Class.I_TAG_3 + " INTEGER DEFAULT 0, " +
            "" + Sales_purchase_Class.I_TAG_4 + " INTEGER DEFAULT 0, " +
            "" + Sales_purchase_Class.I_TAG_5 + " INTEGER DEFAULT 0, " +
            "" + Sales_purchase_Class.I_TAG_6 + " INTEGER DEFAULT 0, " +
            "" + Sales_purchase_Class.I_TAG_7 + " INTEGER DEFAULT 0, " +
            "" + Sales_purchase_Class.I_TAG_8 + " INTEGER DEFAULT 0, " +

            "" + Sales_purchase_Class.I_PRODUCT +  " INTEGER DEFAULT 0, " +
            "" + Sales_purchase_Class.F_QTY+  " INTEGER DEFAULT 0, " +
            "" + Sales_purchase_Class.F_RATE + "  TEXT(50) DEFAULT null , " +
            "" + Sales_purchase_Class.F_DISCOUNT +  " TEXT(50) DEFAULT null , "  +
            "" + Sales_purchase_Class.F_ADD_CHARGES + " TEXT(50) DEFAULT null , " +
            "" + Sales_purchase_Class.F_VAT_PER + " TEXT(50) DEFAULT null ,"  +
            "" + Sales_purchase_Class.F_VAT + " TEXT(50) DEFAULT null ,"  +
            "" + Sales_purchase_Class.S_REMARKS + " TEXT(50) DEFAULT null ,"  +
            "" + Sales_purchase_Class.S_UNITS + " TEXT(50) DEFAULT null ,"  +
            "" + Sales_purchase_Class.F_NET + " TEXT(50) DEFAULT null ,"  +
            "" + Sales_purchase_Class.I_DOC_TYPE + " INTEGER DEFAULT 0, " +
            "" + Sales_purchase_Class.S_DOC_NO + " TEXT(50) DEFAULT null, "  +
            "" + Sales_purchase_Class.I_TRANS_ID + " INTEGER DEFAULT 0, " +
            "" + Sales_purchase_Class.I_TRANS_RETURN_ID + " INTEGER DEFAULT 0 " +

            ");";


    private static final String CREATE_TABLE_SALE_PURCHASE_ORDER_HEADER=" create table if not exists " + TABLE_SALE_PURCHASE_ORDER_HEADER + " (" +
            "" + Sales_purchase_order_class.I_TRANS_ID + " INTEGER DEFAULT 0, " +
            "" + Sales_purchase_order_class.I_ACCOUNT_1 +  " INTEGER DEFAULT 0, " +
            "" + Sales_purchase_order_class.I_ACCOUNT_2 +  " INTEGER DEFAULT 0, " +
            "" + Sales_purchase_order_class.I_DOC_TYPE + " INTEGER DEFAULT 0, " +
            "" + Sales_purchase_order_class.S_DATE +  " TEXT(50) DEFAULT null , "  +
            "" + Sales_purchase_order_class.S_DEL_DATE +  " TEXT(50) DEFAULT null , "  +
            "" + Sales_purchase_order_class.S_NARRATION + " TEXT(50) DEFAULT null , " +
            "" + Sales_purchase_order_class.S_DOC_NO + " TEXT(50) DEFAULT null, "  +
            "" + Sales_purchase_order_class.PROCESS_TIME + " TEXT(50) DEFAULT null, "  +
            "" + Sales_purchase_order_class.STATUS +  " INTEGER DEFAULT 0 " +

            ");";


    private static final String CREATE_TABLE_SALE_PURCHASE_ORDER_BODY =" create table if not exists " + TABLE_SALE_PURCHASE_ORDER_BODY + " (" +
            "" + Sales_purchase_order_class.I_TAG_1 + " INTEGER DEFAULT 0, " +
            "" + Sales_purchase_order_class.I_TAG_2 + " INTEGER DEFAULT 0, " +
            "" + Sales_purchase_order_class.I_TAG_3 + " INTEGER DEFAULT 0, " +
            "" + Sales_purchase_order_class.I_TAG_4 + " INTEGER DEFAULT 0, " +
            "" + Sales_purchase_order_class.I_TAG_5 + " INTEGER DEFAULT 0, " +
            "" + Sales_purchase_order_class.I_TAG_6 + " INTEGER DEFAULT 0, " +
            "" + Sales_purchase_order_class.I_TAG_7 + " INTEGER DEFAULT 0, " +
            "" + Sales_purchase_order_class.I_TAG_8 + " INTEGER DEFAULT 0, " +

            "" + Sales_purchase_order_class.I_PRODUCT +  " INTEGER DEFAULT 0, " +
            "" + Sales_purchase_order_class.F_QTY+  " INTEGER DEFAULT 0, " +
            "" + Sales_purchase_order_class.S_REMARKS + " TEXT(50) DEFAULT null ,"  +
            "" + Sales_purchase_order_class.S_UNITS + " TEXT(50) DEFAULT null ,"  +
            "" + Sales_purchase_order_class.I_DOC_TYPE + " INTEGER DEFAULT 0, " +
            "" + Sales_purchase_order_class.S_DOC_NO + " TEXT(50) DEFAULT null, "  +
            "" + Sales_purchase_order_class.I_TRANS_ID + " INTEGER DEFAULT 0 " +

            ");";

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
        db.execSQL(CREATE_TABLE_USER);
        db.execSQL(CREATE_TABLE_CURRENT_LOGIN);
        db.execSQL(CREATE_TABLE_TAG_DETAILS);
        db.execSQL(CREATE_TABLE_BANK);
        db.execSQL(CREATE_TABLE_SALE_PURCHASE_HEADER);
        db.execSQL(CREATE_TABLE_SALE_PURCHASE_BODY);
        db.execSQL(CREATE_TABLE_PAYMENT_RECEIPT_HEADER);
        db.execSQL(CREATE_TABLE_PAYMENT_RECEIPT_BODY);
        db.execSQL(CREATE_TABLE_SALE_PURCHASE_RETURN_HEADER);
        db.execSQL(CREATE_TABLE_SALE_PURCHASE_RETURN_BODY);
        db.execSQL(CREATE_TABLE_SALE_PURCHASE_ORDER_BODY);
        db.execSQL(CREATE_TABLE_SALE_PURCHASE_ORDER_HEADER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

//            db.execSQL("DROP TABLE IF EXISTS "+TABLE_USER);
//            db.execSQL("DROP TABLE IF EXISTS "+TABLE_PRODUCT);
//            onCreate(db);

        for (int i = oldVersion; i < newVersion; ++i) {
            String migrationName = String.format("from_%d_to_%d.sql", i, (i + 1));
            Log.d("databasehelper", "Looking for migration file: " + migrationName);
            readAndExecuteSQLScript(db, context, migrationName);
        }
        db.delete(TABLE_CURRENT_LOGIN,null,null);
        onCreate(db);

}

    private void readAndExecuteSQLScript(SQLiteDatabase db, Context ctx, String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return;
        }

        Log.d("Databasehelper", "Script found. Executing...");
        AssetManager assetManager = ctx.getAssets();
        BufferedReader reader = null;

        try {
            InputStream is = assetManager.open(fileName);
            InputStreamReader isr = new InputStreamReader(is);
            reader = new BufferedReader(isr);
            executeSQLScript(db, reader);
        } catch (IOException e) {
            Log.e("Databasehelper", "IOException:", e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e("Databasehelper", "IOException:", e);
                }
            }
        }
    }

    private void executeSQLScript(SQLiteDatabase db, BufferedReader reader) throws IOException {
        String line;
        StringBuilder statement = new StringBuilder();
        try {
            while ((line = reader.readLine()) != null) {
                statement.append(line);
                statement.append("\n");
                if (line.endsWith(";")) {
                    db.execSQL(statement.toString());
                    statement = new StringBuilder();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        float status;
        ContentValues cv=new ContentValues();
        cv.put(MasterSettings.I_ID,settings.getiId());
        cv.put(MasterSettings.S_NAME,settings.getsName());
        cv.put(MasterSettings.S_ALT_NAME,settings.getsAltName());

        status=db.update(TABLE_MASTER_SETTINGS,cv,MasterSettings.I_ID+" =? ",new String[]{String.valueOf(settings.getiId())});

        return status!=-1;
    }

    public boolean insertAccounts(Customer customer) {
        this.db=getReadableDatabase();
        ContentValues cv=new ContentValues();
        cv.put(Customer.I_ID, customer.getiId());
        cv.put(Customer.S_NAME, customer.getsName());
        cv.put(Customer.S_CODE, customer.getsCode());
        cv.put(Customer.S_ALT_NAME, customer.getsAltName());
        float status = db.insert(TABLE_ACCOUNTS, null, cv);
        return status != -1;
    }

    public boolean checkAccountsById(String id) {
        this.db=getReadableDatabase();
        Cursor cursor=db.rawQuery("select  * from "+TABLE_ACCOUNTS+
                " where "+ Customer.I_ID+"="+id,null);
        return cursor.getCount() > 0;
    }

    public boolean checkAllDataAccounts(Customer customer) {
        this.db=getReadableDatabase();
        float status;
        ContentValues cv=new ContentValues();
        cv.put(Customer.I_ID, customer.getiId());
        cv.put(Customer.S_NAME, customer.getsName());
        cv.put(Customer.S_ALT_NAME, customer.getsAltName());
        cv.put(Customer.S_CODE, customer.getsCode());
        status=db.update(TABLE_ACCOUNTS,cv, Customer.I_ID+" =? ",new String[]{String.valueOf(customer.getiId())});
        return status!=-1;

    }

    public boolean insertProducts(Products products) {
        this.db=getReadableDatabase();
        ContentValues cv=new ContentValues();
        cv.put(Products.I_ID,products.getiId());
        cv.put(Products.S_NAME,products.getsName());
        cv.put(Products.S_CODE,products.getsCode());
        cv.put(Products.S_UNIT,products.getsUnit());
        cv.put(Products.S_BARCODE,products.getsBarcode());
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
        cv.put(Products.S_CODE,products.getsCode());
        cv.put(Products.S_UNIT,products.getsUnit());
        cv.put(Products.S_BARCODE,products.getsBarcode());
        status=db.update(TABLE_PRODUCT,cv,Products.I_ID+" =? ",new String[]{String.valueOf(products.getiId())});
        return status!=-1;
    }

    public boolean insertTransSetting(TransSetting transSetting) {

        this.db=getReadableDatabase();
        ContentValues cv=new ContentValues();
        cv.put(TransSetting.I_DOC_TYPE,transSetting.getiDocType());
        cv.put(TransSetting.I_TAG_ID,transSetting.getiTagId());
        cv.put(TransSetting.B_MANDATORY,transSetting.getbMandatory());
        cv.put(TransSetting.B_VISIBLE,transSetting.getbVisible());
        cv.put(TransSetting.I_TAG_POSITION,transSetting.getiTagPosition());
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
        cv.put(TransSetting.B_MANDATORY,transSetting.getbMandatory());
        cv.put(TransSetting.B_VISIBLE,transSetting.getbVisible());
        cv.put(TransSetting.I_TAG_POSITION,transSetting.getiTagPosition());
        status=db.update(TABLE_TRANSACTION_SETTING,cv,TransSetting.I_DOC_TYPE+" = " +
                transSetting.getiDocType()+ " and "+TransSetting.I_TAG_ID+" = "+transSetting.getiTagId(),null);
        return status!=-1;
    }

    public boolean insertUser(User user) {
        this.db=getReadableDatabase();
        ContentValues cv=new ContentValues();
        cv.put(User.I_ID,user.getiId());
        cv.put(User.I_STATUS,user.getiStatus());
        cv.put(User.S_LOGIN_NAME,user.getsLoginName());
        cv.put(User.S_PASSWORD,user.getsPassword());
        cv.put(User.S_USERNAME,user.getsUserName());
        cv.put(User.USER_CODE,user.getUserCode());
        cv.put(User.B_MOB,user.getbMob());
        cv.put(User.B_WEB,user.getbMob());
        float status = db.insert(TABLE_USER, null, cv);
        return status != -1;
    }

    public boolean checkUserById(String id) {
        this.db=getReadableDatabase();
        Cursor cursor=db.rawQuery("select  * from "+TABLE_USER+
                " where "+User.I_ID+"="+id,null);
        return cursor.getCount() > 0;
    }

    public boolean checkAllDataUser(User user) {
        this.db=getReadableDatabase();
        float status ;
        ContentValues cv=new ContentValues();
        cv.put(User.I_ID,user.getiId());
        cv.put(User.I_STATUS,user.getiStatus());
        cv.put(User.S_LOGIN_NAME,user.getsLoginName());
        cv.put(User.S_PASSWORD,user.getsPassword());
        cv.put(User.S_USERNAME,user.getsUserName());
        cv.put(User.USER_CODE,user.getUserCode());
        cv.put(User.B_MOB,user.getbMob());
        cv.put(User.B_WEB,user.getbMob());
        status=db.update(TABLE_USER,cv,User.I_ID+" =? ",new String[]{String.valueOf(user.getiId())});
        return status!=-1;
    }

    public boolean GetUser() {
        this.db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_USER, null);
        return cursor.moveToFirst();
    }


    public boolean loginUser(User u) {
        this.db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_USER + " where " + User.S_LOGIN_NAME + "='" + u.getsLoginName() + "' and " + User.S_PASSWORD + "='" + u.getsPassword() + "'", null);
        if (cursor.getCount() > 0) {
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }
    }

    public boolean InsertCurrentLoginUser(User u) {
        this.db=getReadableDatabase();
        this.db=getWritableDatabase();
        Cursor cursor=db.rawQuery(" select "+User.I_ID+ " from "+TABLE_USER+ " where "+User.S_LOGIN_NAME+"= '"+
                u.getsLoginName()+"' and "+User.S_PASSWORD+"= '"+u.getsPassword()+"'",null);
        cursor.moveToFirst();
        String userId=cursor.getString(cursor.getColumnIndex(User.I_ID));

        ContentValues cv=new ContentValues();
        cv.put(USER_ID,userId);
        long status=db.insert(TABLE_CURRENT_LOGIN,null,cv);
        return status !=-1;
    }

    public boolean GetLoginStatus() {

        this.db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_CURRENT_LOGIN + "", null);
        return cursor.getCount() > 0;
    }

    public boolean deleteCurrentLogin() {
        this.db = getWritableDatabase();
        float status = db.delete(TABLE_CURRENT_LOGIN,null,null);
        return status!=-1;
    }

    public boolean insertMasterTag(TagDetails details) {
        this.db=getReadableDatabase();
        ContentValues cv=new ContentValues();
        cv.put(TagDetails.I_ID,details.getiId());
        cv.put(TagDetails.S_NAME,details.getsName());
        cv.put(TagDetails.S_CODE,details.getsCode());
        cv.put(TagDetails.S_ALT_NAME,details.getsAltName());
        cv.put(TagDetails.I_TYPE,details.getiType());
        float status = db.insert(TABLE_TAG_DETAILS, null, cv);
        return status != -1;
    }

    public boolean checkTagDetailsById(String id,String iType) {
        this.db=getReadableDatabase();
        Cursor cursor=db.rawQuery("select  * from "+TABLE_TAG_DETAILS+
                " where "+TagDetails.I_ID+"="+id+ " and "+TagDetails.I_TYPE+"="+iType,null);
        return cursor.getCount() > 0;
    }

    public boolean checkAllDataMasterTag(TagDetails details) {
        this.db=getReadableDatabase();
        this.db=getWritableDatabase();
        float status;
        ContentValues cv=new ContentValues();
        cv.put(TagDetails.I_ID,details.getiId());
        cv.put(TagDetails.S_NAME,details.getsName());
        cv.put(TagDetails.S_CODE,details.getsCode());
        cv.put(TagDetails.S_ALT_NAME,details.getsAltName());
        cv.put(TagDetails.I_TYPE,details.getiType());
        status=db.update(TABLE_TAG_DETAILS,cv,TagDetails.I_ID+" =? and "+TagDetails.I_TYPE+"=? ",new String[]{String.valueOf(details.getiId()),details.getiType()});
        return status!=-1;
    }

    public Cursor getCustomerbyKeyword(String s) {
        this.db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select distinct  "+ Customer.S_NAME+","+Customer.I_ID+","+
                Customer.S_CODE+" from "+TABLE_ACCOUNTS+" where "+Customer.S_CODE
                +" " + "like '"+s+"%' or "+Customer.S_NAME+" like '"+s+"%' limit 10",null);

        if (cursor.moveToFirst()) {
            return cursor;
        } else {
            return null;
        }

    }


    public Cursor getCustomer() {
        this.db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select distinct  "+ Customer.S_NAME+","+Customer.I_ID+","+
                Customer.S_CODE+" from "+TABLE_ACCOUNTS,null);

        if (cursor.moveToFirst()) {
            return cursor;
        } else {
            return null;
        }

    }



    public Cursor getTransSettings(int iDocType, int tagId) {
        this.db=getReadableDatabase();
        Cursor cursor=db.rawQuery("select "+TransSetting.B_VISIBLE+","+TransSetting.I_TAG_POSITION+","+
                TransSetting.B_MANDATORY+" from "+TABLE_TRANSACTION_SETTING+" where "+TransSetting.I_DOC_TYPE+"="+iDocType+" and "+
                TransSetting.I_TAG_ID+"="+tagId,null);
        if(cursor.getCount()>0){
            return cursor;
        }
        else return null;
    }

    public Cursor getTagbyKeyword(String s, int iTag) {
        String iTagS=String.valueOf(iTag);
        this.db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select iId , sCode,sName ,iType from  t1_tag_details " +
                "  where (sCode like '"+s+"%'  or sName  like '"+s+"%' )  and iType='"+iTagS+"'", null);
        if (cursor.moveToFirst()) {
            return cursor;
        } else {
            return null;
        }
    }

    public Cursor getTagNamebyId(int tagId) {
        this.db = getReadableDatabase();
        Cursor cursor=db.rawQuery("select "+MasterSettings.S_NAME+" from "+TABLE_MASTER_SETTINGS+
                " where "+MasterSettings.I_ID+"="+tagId,null);
        if(cursor.getCount()>0){
            return cursor;
        }
        else return null;
    }

    public Cursor getProductDetailsByBarcode(String productBarCode) {
        this.db = getReadableDatabase();
        Cursor cursor=db.rawQuery("select "+Products.I_ID+","+Products.S_NAME+" from "+
                TABLE_PRODUCT+" where "+Products.S_BARCODE+"='"+productBarCode+"'",null);
        if(cursor.getCount()>0){
            return cursor;
        }else return null;
    }

    public Cursor getProductByKeyword(String productKeyword) {
        this.db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select "+Products.S_NAME+","+Products.I_ID+","+
                Products.S_CODE+" from "+TABLE_PRODUCT+" where "+Products.S_CODE
                +" like '"+productKeyword+"%' or "+Products.S_NAME+" like '"+productKeyword+"%' group by "+Products.S_NAME+" limit 10 ",null);

        if (cursor.moveToFirst()) {
            return cursor;
        } else {
            return null;
        }
    }

    public Cursor getUserId() {
        this.db=getWritableDatabase();
        this.db=getReadableDatabase();
        Cursor cursor=db.rawQuery("select "+USER_ID+" from "+TABLE_CURRENT_LOGIN,null);
        cursor.moveToFirst();
        return cursor;
    }

    public Cursor getTagName(int tagId, int tagDetails) {
        this.db=getWritableDatabase();
        Cursor cursor=db.rawQuery("select "+TagDetails.S_NAME+" from "+TABLE_TAG_DETAILS+
                " where "+TagDetails.I_TYPE+"="+tagId+ " and "+TagDetails.I_ID+"="+tagDetails,null);
        cursor.moveToFirst();
        return cursor;
    }

    public Cursor getTotalTagNumber() {
        this.db=getWritableDatabase();
        Cursor cursor=db.rawQuery("select "+MasterSettings.I_ID+" from "+TABLE_MASTER_SETTINGS,null);
        cursor.moveToFirst();
        return cursor;
    }

    public boolean getProductNameValid(String productName) {
        this.db=getWritableDatabase();
        this.db=getReadableDatabase();
        Cursor cursor=db.rawQuery("select "+Products.I_ID+" from "+TABLE_PRODUCT+" where "+Products.S_NAME
                +" = '"+productName+"'",null);
        return cursor.getCount() > 0;
    }

    public boolean getCustomerNameValid(String customer) {
        this.db=getWritableDatabase();
        this.db=getReadableDatabase();
        Cursor cursor=db.rawQuery("select "+Customer.I_ID+" from "+TABLE_ACCOUNTS+" where "+Customer.S_NAME
                +" = '"+customer+"'",null);
        return cursor.getCount() > 0;
    }

    public boolean isTagValid(String tagName) {
        this.db=getWritableDatabase();
        this.db=getReadableDatabase();
        Cursor cursor=db.rawQuery("select "+TagDetails.I_ID+" from "+TABLE_TAG_DETAILS+" where "+TagDetails.S_NAME
                +" = '"+tagName+"'",null);
        return cursor.getCount() > 0;
    }

    public String getUserName(String userId) {
        this.db=getWritableDatabase();
        Cursor cursor=db.rawQuery("select "+User.S_USERNAME+" from "+TABLE_USER+
                " where "+User.I_ID+"='"+userId+"'",null);
        cursor.moveToFirst();
        return cursor.getString(cursor.getColumnIndex(User.S_USERNAME));
    }


    public String getProductUnitById(int iProduct) {
        this.db=getReadableDatabase();
        Cursor cursor=db.rawQuery("select "+Products.S_UNIT+" from "+TABLE_PRODUCT+" where "+
                Products.I_ID+"="+iProduct,null);
        cursor.moveToFirst();
        return cursor.getString(cursor.getColumnIndex(Products.S_UNIT));
    }

    public Cursor getUserCode(String userIdS) {
        this.db=getWritableDatabase();
        Cursor cursor=db.rawQuery("select "+User.USER_CODE+" from "+TABLE_USER+" where "+User.I_ID+"='"+userIdS+"'",null);
        cursor.moveToFirst();
        return cursor;
    }

    public boolean insertBanks(Bank bank) {
        this.db=getReadableDatabase();
        ContentValues cv=new ContentValues();
        cv.put(Bank.I_ID, bank.getiId());
        cv.put(Bank.S_NAME, bank.getsName());
        cv.put(Bank.S_CODE, bank.getsCode());
        float status = db.insert(TABLE_BANK, null, cv);
        return status != -1;
    }

    public boolean checkAllDataBank(Bank bank) {
        this.db=getReadableDatabase();
        float status;
        ContentValues cv=new ContentValues();
        cv.put(Bank.I_ID, bank.getiId());
        cv.put(Bank.S_NAME, bank.getsName());
        cv.put(Bank.S_CODE, bank.getsCode());
        status=db.update(TABLE_BANK,cv, Bank.I_ID+" =? ",new String[]{String.valueOf(bank.getiId())});
        return status!=-1;    }

    public boolean checkBankById(String id) {
        this.db=getReadableDatabase();
        Cursor cursor=db.rawQuery("select  * from "+TABLE_BANK+
                " where "+ Bank.I_ID+"="+id,null);
        return cursor.getCount() > 0;    }

    public Cursor getBankyKeyword(String s) {
        this.db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select distinct  "+ Bank.S_NAME+","+Bank.I_ID+","+
                Bank.S_CODE+" from "+TABLE_BANK+" where "+Bank.S_CODE
                +" " + "like '"+s+"%' or "+Bank.S_NAME+" like '"+s+"%' limit 10",null);

        if (cursor.moveToFirst()) {
            return cursor;
        } else {
            return null;
        }    }

    public Cursor getTagDetails() {
        this.db=getWritableDatabase();
        Cursor cursor=db.rawQuery("select "+TagDetails.I_ID +" from "+TABLE_TAG_DETAILS,null);
        cursor.moveToFirst();
        return cursor;
    }

    public boolean insert_S_P_Header(Sales_purchase_Class sp_class) {
        this.db=getReadableDatabase();
        ContentValues cv=new ContentValues();

        cv.put(Sales_purchase_Class.I_TRANS_ID,sp_class.getiTransId());
        cv.put(Sales_purchase_Class.S_DOC_NO,sp_class.getsDocNo());
        cv.put(Sales_purchase_Class.I_DOC_TYPE,sp_class.getiDocType());
        cv.put(Sales_purchase_Class.I_ACCOUNT_1,sp_class.getiAccount1());
        cv.put(Sales_purchase_Class.I_ACCOUNT_2,sp_class.getiAccount2());
        cv.put(Sales_purchase_Class.S_NARRATION,sp_class.getsNarration());
        cv.put(Sales_purchase_Class.S_DATE,sp_class.getsDate());
        cv.put(Sales_purchase_Class.PROCESS_TIME,sp_class.getProcessTime());
        cv.put(Sales_purchase_Class.STATUS,sp_class.getStatus());


        float status = db.insert(TABLE_SALE_PURCHASE_HEADER, null, cv);
        return status != -1;
    }

    public boolean insert_S_P_Body(Sales_purchase_Class sp) {
        this.db=getReadableDatabase();
        ContentValues cv=new ContentValues();

        cv.put(Sales_purchase_Class.I_TAG_1,sp.getiTag1());
        cv.put(Sales_purchase_Class.I_TAG_2,sp.getiTag2());
        cv.put(Sales_purchase_Class.I_TAG_3,sp.getiTag3());
        cv.put(Sales_purchase_Class.I_TAG_4,sp.getiTag4());
        cv.put(Sales_purchase_Class.I_TAG_5,sp.getiTag5());
        cv.put(Sales_purchase_Class.I_TAG_6,sp.getiTag6());
        cv.put(Sales_purchase_Class.I_TAG_7,sp.getiTag7());
        cv.put(Sales_purchase_Class.I_TAG_8,sp.getiTag8());

        cv.put(Sales_purchase_Class.I_PRODUCT,sp.getiProduct());
        cv.put(Sales_purchase_Class.F_QTY,sp.getFqty());
        cv.put(Sales_purchase_Class.F_RATE,sp.getfRate());
        cv.put(Sales_purchase_Class.F_DISCOUNT,sp.getfDiscount());
        cv.put(Sales_purchase_Class.F_ADD_CHARGES,sp.getfAddCharges());
        cv.put(Sales_purchase_Class.F_VAT_PER,sp.getFvatPer());
        cv.put(Sales_purchase_Class.F_VAT,sp.getfVat());
        cv.put(Sales_purchase_Class.S_REMARKS,sp.getsRemarks());
        cv.put(Sales_purchase_Class.S_UNITS,sp.getUnit());
        cv.put(Sales_purchase_Class.F_NET,sp.getNet());

        cv.put(Sales_purchase_Class.S_DOC_NO,sp.getsDocNo());
        cv.put(Sales_purchase_Class.I_DOC_TYPE,sp.getiDocType());
        cv.put(Sales_purchase_Class.I_TRANS_ID,sp.getiTransId());

        float status = db.insert(TABLE_SALE_PURCHASE_BODY, null, cv);
        return status != -1;

    }



    public Cursor getDataFromS_P_Header() {
        this.db=getReadableDatabase();
        Cursor cursor=db.rawQuery("select * from "+TABLE_SALE_PURCHASE_HEADER,null);
        cursor.moveToFirst();
        return cursor;
    }

    public String getCustomerUsingId(int iCustomer) {

        this.db=getReadableDatabase();
        Cursor cursor=db.rawQuery("select "+ Customer.S_NAME +" from "+TABLE_ACCOUNTS+" where "+Customer.I_ID+ "= '"+iCustomer+"'",null);
        cursor.moveToFirst();
        String customer=cursor.getString(cursor.getColumnIndex(Customer.S_NAME));
        return customer;
    }




    public Cursor getDataFromS_P_by_Itype(int iDocType) {
        this.db=getReadableDatabase();
        Cursor cursor=db.rawQuery("select * from "+TABLE_SALE_PURCHASE_HEADER+" where "
                +Sales_purchase_Class.I_DOC_TYPE+"='"+iDocType+"' order by "+Sales_purchase_Class.I_TRANS_ID,null);
        cursor.moveToFirst();
        return cursor;

    }

    public Cursor getEditValuesHeaderS_P(int iTransId, int iDocType) {
        this.db=getReadableDatabase();
        Cursor cursor=db.rawQuery("select * from "+TABLE_SALE_PURCHASE_HEADER+" where "
                +Sales_purchase_Class.I_DOC_TYPE+"='"+iDocType+"' and "+Sales_purchase_Class.I_TRANS_ID+"="+"'"+iTransId+"'",null);
        cursor.moveToFirst();
        return cursor;
    }

    public boolean changeStatus_S_P(int transId, String docNo, int iStatus) {
        String sTransId=String.valueOf(transId);
        this.db=getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(Sales_purchase_Class.STATUS,iStatus);
        float status=db.update(TABLE_SALE_PURCHASE_HEADER,cv,  Sales_purchase_Class.I_TRANS_ID + " = ? and " + Sales_purchase_Class.S_DOC_NO + " = ? ", new String[]{sTransId, docNo});
        return  status != -1;
    }

    public Cursor getEditValuesBodyS_P(int iTransId, int iDocType) {
        this.db=getReadableDatabase();
        Cursor cursor=db.rawQuery("select * from "+TABLE_SALE_PURCHASE_BODY+" where "
                +Sales_purchase_Class.I_DOC_TYPE+"='"+iDocType+"' and "+Sales_purchase_Class.I_TRANS_ID+"="+"'"+iTransId+"'",null);
        cursor.moveToFirst();
        return cursor;
    }

    public String getProductNameById(int iProduct) {
        this.db=getReadableDatabase();
        Cursor cursor=db.rawQuery("select "+Products.S_NAME+" from "+TABLE_PRODUCT+" where "
                +Products.I_ID+"='"+iProduct+"'",null);
        cursor.moveToFirst();
        return cursor.getString(cursor.getColumnIndex(Products.S_NAME));
    }



    public Cursor getDataFromS_P_HeaderPost() {

        this.db=getReadableDatabase();
        Cursor cursor=db.rawQuery("select * from "+TABLE_SALE_PURCHASE_HEADER+
                " where status=0 union select * from "+TABLE_SALE_PURCHASE_HEADER+
                " where status=1 and processTime <=( SELECT datetime('now','localtime','-1 hours'));",null);
        cursor.moveToFirst();
        return cursor;
    }

    public boolean delete_S_P_Body(int iDocType, int iTransId) {
        this.db=getReadableDatabase();
        String sDocType=String.valueOf(iDocType);
        String sTransId=String.valueOf(iTransId);
        this.db = getWritableDatabase();
        Cursor cursor=db.rawQuery("select * from "+TABLE_SALE_PURCHASE_BODY,null);

        if(cursor.getCount()>0) {
            float status = db.delete(TABLE_SALE_PURCHASE_BODY, Sales_purchase_Class.I_DOC_TYPE + " =  ? and "
                    + Sales_purchase_Class.I_TRANS_ID + " = ?", new String[]{sDocType, sTransId});
            return status != -1;
        }else
            return true;

    }

    public boolean deleteSP_Header(int iTransId, int iDocType, String sDocNo) {
        String sDocType=String.valueOf(iDocType);
        this.db = getWritableDatabase();

        Cursor cursor=db.rawQuery("select * from "+TABLE_SALE_PURCHASE_HEADER,null);

        if(cursor.getCount()>0) {
            float status = db.delete(TABLE_SALE_PURCHASE_HEADER, Sales_purchase_Class.S_DOC_NO + " =  ? and "
                    + Sales_purchase_Class.I_DOC_TYPE + " = ?", new String[]{sDocNo, sDocType});
            return status != -1;
        }else {
            return true;
        }

    }

    public Cursor getDataFromPaymentReceipt() {
        this.db=getReadableDatabase();
        Cursor cursor=db.rawQuery("select * from "+TABLE_PAYMENT_RECEIPT_HEADER,null);
        cursor.moveToFirst();
        return cursor;

    }

    public boolean deletePayRec_Header(int iTransId, int iDocType, String sDocNo) {
        String sDocType=String.valueOf(iDocType);
        this.db = getWritableDatabase();

        Cursor cursor=db.rawQuery("select * from "+TABLE_PAYMENT_RECEIPT_HEADER,null);

        if(cursor.getCount()>0) {
            float status = db.delete(TABLE_PAYMENT_RECEIPT_HEADER, Payment_Receipt_class.S_DOC_NO + " =  ? and "
                    + Payment_Receipt_class.I_DOC_TYPE + " = ?", new String[]{sDocNo, sDocType});
            return status != -1;
        }else {
            return true;
        }
    }

    public boolean insert_PayRec_Header(Payment_Receipt_class pr_class) {
        this.db=getReadableDatabase();
        ContentValues cv=new ContentValues();

        float status = 0;
        try {
        cv.put(Payment_Receipt_class.I_TRANS_ID,pr_class.getiTransId());
        cv.put(Payment_Receipt_class.S_DOC_NO,pr_class.getsDocNo());
        cv.put(Payment_Receipt_class.I_DOC_TYPE,pr_class.getiDocType());
        cv.put(Payment_Receipt_class.I_ACCOUNT_1,pr_class.getiAccount1());
        cv.put(Payment_Receipt_class.I_ACCOUNT_2,pr_class.getiAccount2());
        cv.put(Payment_Receipt_class.S_NARRATION,pr_class.getsNarration());
        cv.put(Payment_Receipt_class.S_DATE,pr_class.getsDate());
        cv.put(Payment_Receipt_class.PROCESS_TIME,pr_class.getProcessTime());
        cv.put(Payment_Receipt_class.STATUS,pr_class.getStatus());

        cv.put(Payment_Receipt_class.F_AMOUNT,pr_class.getfAmount());
        cv.put(Payment_Receipt_class.I_PAYMENT_METHOD,pr_class.getiPaymentMethod());
        cv.put(Payment_Receipt_class.I_BANK,pr_class.getiBank());
        cv.put(Payment_Receipt_class.I_CHEQUE_NO,pr_class.getiChequeNo());
        cv.put(Payment_Receipt_class.S_ATTACHMENT,pr_class.getsAttachment());
        cv.put(Payment_Receipt_class.S_CHEQUE_DATE,pr_class.getsChequeDate());


         status = db.insert(TABLE_PAYMENT_RECEIPT_HEADER, null, cv);
        }catch (Exception e){
            Log.d("Exceptionn",e.getMessage());

        }
        return status != -1;
    }

    public boolean delete_PayRec_Body(int iDocType, int iTransId) {
        this.db=getReadableDatabase();
        String sDocType=String.valueOf(iDocType);
        String sTransId=String.valueOf(iTransId);
        this.db = getWritableDatabase();
        Cursor cursor=db.rawQuery("select * from "+TABLE_PAYMENT_RECEIPT_BODY,null);

        if(cursor.getCount()>0) {
            float status = db.delete(TABLE_PAYMENT_RECEIPT_BODY, Payment_Receipt_class.I_DOC_TYPE + " =  ? and "
                    + Payment_Receipt_class.I_TRANS_ID + " = ?", new String[]{sDocType, sTransId});
            return status != -1;
        }else
            return true;

    }

    public Cursor getDataFrom_PayRec_Header() {
        this.db=getReadableDatabase();
        Cursor cursor=db.rawQuery("select * from "+TABLE_PAYMENT_RECEIPT_HEADER,null);
        cursor.moveToFirst();
        return cursor;

    }

    public boolean insert_PayRec_Body(Payment_Receipt_class prClass) {

        this.db=getReadableDatabase();
        ContentValues cv=new ContentValues();

        cv.put(Payment_Receipt_class.I_TAG_1,prClass.getiTag1());
        cv.put(Payment_Receipt_class.I_TAG_2,prClass.getiTag2());
        cv.put(Payment_Receipt_class.I_TAG_3,prClass.getiTag3());
        cv.put(Payment_Receipt_class.I_TAG_4,prClass.getiTag4());
        cv.put(Payment_Receipt_class.I_TAG_5,prClass.getiTag5());
        cv.put(Payment_Receipt_class.I_TAG_6,prClass.getiTag6());
        cv.put(Payment_Receipt_class.I_TAG_7,prClass.getiTag7());
        cv.put(Payment_Receipt_class.I_TAG_8,prClass.getiTag8());

        cv.put(Payment_Receipt_class.I_REF_DOC_ID,prClass.getiRefDocId());
        cv.put(Payment_Receipt_class.F_AMOUNT,prClass.getfAmount());

        cv.put(Payment_Receipt_class.S_DOC_NO,prClass.getsDocNo());
        cv.put(Payment_Receipt_class.I_DOC_TYPE,prClass.getiDocType());
        cv.put(Payment_Receipt_class.I_TRANS_ID,prClass.getiTransId());

        float status = db.insert(TABLE_PAYMENT_RECEIPT_BODY, null, cv);
        return status != -1;
    }

    public Cursor getDataFromPayRec_by_Itype(int iDocType) {
        this.db=getReadableDatabase();
        Cursor cursor=db.rawQuery("select * from "+TABLE_PAYMENT_RECEIPT_HEADER+" where "
                +Payment_Receipt_class.I_DOC_TYPE+"='"+iDocType+"' order by "+Payment_Receipt_class.I_TRANS_ID,null);
        cursor.moveToFirst();
        return cursor;
    }

    public Cursor getEditValuesHeaderPayRec(int iTransId, int iDocType) {
        this.db=getReadableDatabase();
        Cursor cursor=db.rawQuery("select * from "+TABLE_PAYMENT_RECEIPT_HEADER+" where "
                +Payment_Receipt_class.I_DOC_TYPE+"='"+iDocType+"' and "+Payment_Receipt_class.I_TRANS_ID+"="+"'"+iTransId+"'",null);
        cursor.moveToFirst();
        return cursor;
    }

    public String getBankUsingId(int iBank) {
        this.db=getReadableDatabase();
        Cursor cursor=db.rawQuery("select "+ Bank.S_NAME +" from "+TABLE_BANK+" where "+Bank.I_ID+ "= '"+iBank+"'",null);
        cursor.moveToFirst();
        String bankName=cursor.getString(cursor.getColumnIndex(Bank.S_NAME));
        return bankName;

    }

    public boolean changeStatus_PayRec(int transId, String docNo, int iStatus) {
        String sTransId=String.valueOf(transId);
        this.db=getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(Payment_Receipt_class.STATUS,iStatus);
        float status=db.update(TABLE_PAYMENT_RECEIPT_HEADER,cv,  Payment_Receipt_class.I_TRANS_ID + " = ? and " + Payment_Receipt_class.S_DOC_NO + " = ? ", new String[]{sTransId, docNo});
        return  status != -1;
    }

    public Cursor getEditValuesBodyP_ayRec(int iTransId, int iDocType) {
        this.db=getReadableDatabase();
        Cursor cursor=db.rawQuery("select * from "+TABLE_PAYMENT_RECEIPT_BODY+" where "
                +Payment_Receipt_class.I_DOC_TYPE+"='"+iDocType+"' and "+Payment_Receipt_class.I_TRANS_ID+"="+"'"+iTransId+"'",null);
        cursor.moveToFirst();
        return cursor;

    }

    public Cursor getDataFrom_PayRec_HeaderPost() {

        this.db=getReadableDatabase();
        Cursor cursor=db.rawQuery("select * from "+TABLE_PAYMENT_RECEIPT_HEADER+
                " where status=0 union select * from "+TABLE_PAYMENT_RECEIPT_HEADER+
                " where status=1 and processTime <=( SELECT datetime('now','localtime','-1 hours'));",null);
        cursor.moveToFirst();
        return cursor;

    }

    public Cursor getDataFromS_P_return_Header() {
        this.db=getReadableDatabase();
        Cursor cursor=db.rawQuery("select * from "+TABLE_SALE_PURCHASE_RETURN_HEADER,null);
        cursor.moveToFirst();
        return cursor;

    }

    public boolean deleteSP_return_Header(int iTransId, int iDocType, String sDocNo) {
        String sDocType=String.valueOf(iDocType);
        this.db = getWritableDatabase();

        Cursor cursor=db.rawQuery("select * from "+TABLE_SALE_PURCHASE_RETURN_HEADER,null);

        if(cursor.getCount()>0) {
            float status = db.delete(TABLE_SALE_PURCHASE_RETURN_HEADER, Sales_purchase_Class.S_DOC_NO + " =  ? and "
                    + Sales_purchase_Class.I_DOC_TYPE + " = ?", new String[]{sDocNo, sDocType});
            return status != -1;
        }else {
            return true;
        }

    }

    public boolean insert_S_P_return_Header(Sales_purchase_Class sp_class) {

        this.db=getReadableDatabase();
        ContentValues cv=new ContentValues();

        cv.put(Sales_purchase_Class.I_TRANS_ID,sp_class.getiTransId());
        cv.put(Sales_purchase_Class.S_DOC_NO,sp_class.getsDocNo());
        cv.put(Sales_purchase_Class.I_DOC_TYPE,sp_class.getiDocType());
        cv.put(Sales_purchase_Class.I_ACCOUNT_1,sp_class.getiAccount1());
        cv.put(Sales_purchase_Class.I_ACCOUNT_2,sp_class.getiAccount2());
        cv.put(Sales_purchase_Class.S_NARRATION,sp_class.getsNarration());
        cv.put(Sales_purchase_Class.S_DATE,sp_class.getsDate());
        cv.put(Sales_purchase_Class.PROCESS_TIME,sp_class.getProcessTime());
        cv.put(Sales_purchase_Class.STATUS,sp_class.getStatus());


        float status = db.insert(TABLE_SALE_PURCHASE_RETURN_HEADER, null, cv);
        return status != -1;
    }

    public boolean insert_S_P_return_Body(Sales_purchase_Class sp) {
        this.db=getReadableDatabase();
        ContentValues cv=new ContentValues();

        cv.put(Sales_purchase_Class.I_TAG_1,sp.getiTag1());
        cv.put(Sales_purchase_Class.I_TAG_2,sp.getiTag2());
        cv.put(Sales_purchase_Class.I_TAG_3,sp.getiTag3());
        cv.put(Sales_purchase_Class.I_TAG_4,sp.getiTag4());
        cv.put(Sales_purchase_Class.I_TAG_5,sp.getiTag5());
        cv.put(Sales_purchase_Class.I_TAG_6,sp.getiTag6());
        cv.put(Sales_purchase_Class.I_TAG_7,sp.getiTag7());
        cv.put(Sales_purchase_Class.I_TAG_8,sp.getiTag8());

        cv.put(Sales_purchase_Class.I_PRODUCT,sp.getiProduct());
        cv.put(Sales_purchase_Class.F_QTY,sp.getFqty());
        cv.put(Sales_purchase_Class.F_RATE,sp.getfRate());
        cv.put(Sales_purchase_Class.F_DISCOUNT,sp.getfDiscount());
        cv.put(Sales_purchase_Class.F_ADD_CHARGES,sp.getfAddCharges());
        cv.put(Sales_purchase_Class.F_VAT_PER,sp.getFvatPer());
        cv.put(Sales_purchase_Class.F_VAT,sp.getfVat());
        cv.put(Sales_purchase_Class.S_REMARKS,sp.getsRemarks());
        cv.put(Sales_purchase_Class.S_UNITS,sp.getUnit());
        cv.put(Sales_purchase_Class.F_NET,sp.getNet());

        cv.put(Sales_purchase_Class.S_DOC_NO,sp.getsDocNo());
        cv.put(Sales_purchase_Class.I_DOC_TYPE,sp.getiDocType());
        cv.put(Sales_purchase_Class.I_TRANS_ID,sp.getiTransId());
        cv.put(Sales_purchase_Class.I_TRANS_RETURN_ID,sp.getiTransReturnId());

        float status = db.insert(TABLE_SALE_PURCHASE_RETURN_BODY, null, cv);
        return status != -1;
    }

    public Cursor getDataFromS_P_return_by_Itype(int iDocType) {
        this.db=getReadableDatabase();
        Cursor cursor=db.rawQuery("select * from "+TABLE_SALE_PURCHASE_RETURN_HEADER+" where "
                +Sales_purchase_Class.I_DOC_TYPE+"='"+iDocType+"' order by "+Sales_purchase_Class.I_TRANS_ID,null);
        cursor.moveToFirst();
        return cursor;
    }

    public boolean delete_S_P_return_Body(int iDocType, int iTransId) {
        this.db=getReadableDatabase();
        String sDocType=String.valueOf(iDocType);
        String sTransId=String.valueOf(iTransId);
        this.db = getWritableDatabase();
        Cursor cursor=db.rawQuery("select * from "+TABLE_SALE_PURCHASE_RETURN_BODY,null);

        if(cursor.getCount()>0) {
            float status = db.delete(TABLE_SALE_PURCHASE_RETURN_BODY, Sales_purchase_Class.I_DOC_TYPE + " =  ? and "
                    + Sales_purchase_Class.I_TRANS_ID + " = ?", new String[]{sDocType, sTransId});
            return status != -1;
        }else
            return true;
    }

    public Cursor getEditValuesHeaderS_P_return(int iTransId, int iDocType) {
        this.db=getReadableDatabase();
        Cursor cursor=db.rawQuery("select * from "+TABLE_SALE_PURCHASE_RETURN_HEADER+" where "
                +Sales_purchase_Class.I_DOC_TYPE+"='"+iDocType+"' and "+Sales_purchase_Class.I_TRANS_ID+"="+"'"+iTransId+"'",null);
        cursor.moveToFirst();
        return cursor;
    }

    public boolean changeStatus_S_P_return(int transId, String docNo, int iStatus) {
        String sTransId=String.valueOf(transId);
        this.db=getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(Sales_purchase_Class.STATUS,iStatus);
        float status=db.update(TABLE_PAYMENT_RECEIPT_HEADER,cv,  Sales_purchase_Class.I_TRANS_ID + " = ? and " + Sales_purchase_Class.S_DOC_NO + " = ? ", new String[]{sTransId, docNo});
        return  status != -1;

    }

    public Cursor getEditValuesBodyS_P_return(int iTransId, int iDocType) {
        this.db=getReadableDatabase();
        Cursor cursor=db.rawQuery("select * from "+TABLE_SALE_PURCHASE_RETURN_BODY+" where "
                +Sales_purchase_Class.I_DOC_TYPE+"='"+iDocType+"' and "+Sales_purchase_Class.I_TRANS_ID+"="+"'"+iTransId+"'",null);
        cursor.moveToFirst();
        return cursor;

    }

    public Cursor getDataFromS_P_return_HeaderPost() {
        this.db=getReadableDatabase();
        Cursor cursor=db.rawQuery("select * from "+TABLE_SALE_PURCHASE_RETURN_HEADER+
                " where status=0 union select * from "+TABLE_SALE_PURCHASE_RETURN_HEADER+
                " where status=1 and processTime <=( SELECT datetime('now','localtime','-1 hours'));",null);
        cursor.moveToFirst();
        return cursor;
    }

    public Cursor getDataFromS_P_Order_Header() {

        this.db=getReadableDatabase();
        Cursor cursor=db.rawQuery("select * from "+TABLE_SALE_PURCHASE_ORDER_HEADER,null);
        cursor.moveToFirst();
        return cursor;

    }

    public boolean deleteSP_Order_Header(int iTransId, int iDocType, String sDocNo) {

        String sDocType=String.valueOf(iDocType);
        this.db = getWritableDatabase();

        Cursor cursor=db.rawQuery("select * from "+TABLE_SALE_PURCHASE_ORDER_HEADER,null);

        if(cursor.getCount()>0) {
            float status = db.delete(TABLE_SALE_PURCHASE_ORDER_HEADER, Sales_purchase_order_class.S_DOC_NO + " =  ? and "
                    + Sales_purchase_order_class.I_DOC_TYPE + " = ?", new String[]{sDocNo, sDocType});
            return status != -1;
        }else {
            return true;
        }
    }

    public boolean insert_S_P_Order_Header(Sales_purchase_order_class sp_class) {

        this.db=getReadableDatabase();
        ContentValues cv=new ContentValues();

        cv.put(Sales_purchase_order_class.I_TRANS_ID,sp_class.getiTransId());
        cv.put(Sales_purchase_order_class.S_DOC_NO,sp_class.getsDocNo());
        cv.put(Sales_purchase_order_class.I_DOC_TYPE,sp_class.getiDocType());
        cv.put(Sales_purchase_order_class.I_ACCOUNT_1,sp_class.getiAccount1());
        cv.put(Sales_purchase_order_class.I_ACCOUNT_2,sp_class.getiAccount2());
        cv.put(Sales_purchase_order_class.S_NARRATION,sp_class.getsNarration());
        cv.put(Sales_purchase_order_class.S_DATE,sp_class.getsDate());
        cv.put(Sales_purchase_order_class.S_DEL_DATE,sp_class.getsDelDate());
        cv.put(Sales_purchase_order_class.PROCESS_TIME,sp_class.getProcessTime());
        cv.put(Sales_purchase_order_class.STATUS,sp_class.getStatus());


        float status = db.insert(TABLE_SALE_PURCHASE_ORDER_HEADER, null, cv);
        return status != -1;
    }

    public boolean delete_S_P_Order_Body(int iDocType, int iTransId) {
        this.db=getReadableDatabase();
        String sDocType=String.valueOf(iDocType);
        String sTransId=String.valueOf(iTransId);
        this.db = getWritableDatabase();
        Cursor cursor=db.rawQuery("select * from "+TABLE_SALE_PURCHASE_ORDER_BODY,null);

        if(cursor.getCount()>0) {
            float status = db.delete(TABLE_SALE_PURCHASE_ORDER_BODY, Sales_purchase_order_class.I_DOC_TYPE + " =  ? and "
                    + Sales_purchase_order_class.I_TRANS_ID + " = ?", new String[]{sDocType, sTransId});
            return status != -1;
        }else
            return true;
    }

    public boolean insert_S_P_Order_Body(Sales_purchase_order_class sp_Order) {
        this.db=getReadableDatabase();
        ContentValues cv=new ContentValues();

        cv.put(Sales_purchase_order_class.I_TAG_1,sp_Order.getiTag1());
        cv.put(Sales_purchase_order_class.I_TAG_2,sp_Order.getiTag2());
        cv.put(Sales_purchase_order_class.I_TAG_3,sp_Order.getiTag3());
        cv.put(Sales_purchase_order_class.I_TAG_4,sp_Order.getiTag4());
        cv.put(Sales_purchase_order_class.I_TAG_5,sp_Order.getiTag5());
        cv.put(Sales_purchase_order_class.I_TAG_6,sp_Order.getiTag6());
        cv.put(Sales_purchase_order_class.I_TAG_7,sp_Order.getiTag7());
        cv.put(Sales_purchase_order_class.I_TAG_8,sp_Order.getiTag8());

        cv.put(Sales_purchase_order_class.I_PRODUCT,sp_Order.getiProduct());
        cv.put(Sales_purchase_order_class.F_QTY,sp_Order.getFqty());
        cv.put(Sales_purchase_order_class.S_REMARKS,sp_Order.getsRemarks());
        cv.put(Sales_purchase_order_class.S_UNITS,sp_Order.getUnit());

        cv.put(Sales_purchase_order_class.S_DOC_NO,sp_Order.getsDocNo());
        cv.put(Sales_purchase_order_class.I_DOC_TYPE,sp_Order.getiDocType());
        cv.put(Sales_purchase_order_class.I_TRANS_ID,sp_Order.getiTransId());

        float status = db.insert(TABLE_SALE_PURCHASE_ORDER_BODY, null, cv);
        return status != -1;
    }

    public Cursor getEditValuesHeaderS_P_Order(int iTransId, int iDocType) {

        this.db=getReadableDatabase();
        Cursor cursor=db.rawQuery("select * from "+TABLE_SALE_PURCHASE_ORDER_HEADER+" where "
                +Sales_purchase_order_class.I_DOC_TYPE+"='"+iDocType+"' and "+Sales_purchase_order_class.I_TRANS_ID+"="+"'"+iTransId+"'",null);
        cursor.moveToFirst();
        return cursor;
    }

    public boolean changeStatus_S_P_Order(int transId, String docNo, int iStatus) {
        String sTransId=String.valueOf(transId);
        this.db=getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(Sales_purchase_order_class.STATUS,iStatus);
        float status=db.update(TABLE_SALE_PURCHASE_ORDER_HEADER,cv,  Sales_purchase_order_class.I_TRANS_ID +
                " = ? and " + Sales_purchase_order_class.S_DOC_NO + " = ? ", new String[]{sTransId, docNo});
        return  status != -1;
    }

    public Cursor getEditValuesBodyS_P_Order(int iTransId, int iDocType) {
        this.db=getReadableDatabase();
        Cursor cursor=db.rawQuery("select * from "+TABLE_SALE_PURCHASE_ORDER_BODY+" where "
                +Sales_purchase_order_class.I_DOC_TYPE+"='"+iDocType+"' and "+Sales_purchase_order_class.I_TRANS_ID+"="+"'"+iTransId+"'",null);
        cursor.moveToFirst();
        return cursor;
    }

    public boolean deleteSP_Header_Order(int iTransId, int iDocType, String sDocNo) {

        String sDocType=String.valueOf(iDocType);
        this.db = getWritableDatabase();

        Cursor cursor=db.rawQuery("select * from "+TABLE_SALE_PURCHASE_ORDER_HEADER,null);

        if(cursor.getCount()>0) {
            float status = db.delete(TABLE_SALE_PURCHASE_ORDER_HEADER, Sales_purchase_order_class.S_DOC_NO + " =  ? and "
                    + Sales_purchase_order_class.I_DOC_TYPE + " = ?", new String[]{sDocNo, sDocType});
            return status != -1;
        }else {
            return true;
        }
    }

    public boolean delete_S_P_Body_Order(int iDocType, int iTransId) {
        this.db=getReadableDatabase();
        String sDocType=String.valueOf(iDocType);
        String sTransId=String.valueOf(iTransId);
        this.db = getWritableDatabase();
        Cursor cursor=db.rawQuery("select * from "+TABLE_SALE_PURCHASE_ORDER_BODY,null);

        if(cursor.getCount()>0) {
            float status = db.delete(TABLE_SALE_PURCHASE_ORDER_BODY, Sales_purchase_order_class.I_DOC_TYPE + " =  ? and "
                    + Sales_purchase_order_class.I_TRANS_ID + " = ?", new String[]{sDocType, sTransId});
            return status != -1;
        }else
            return true;

    }

    public Cursor getDataFromS_P_Order_by_Itype(int iDocType) {
        this.db=getReadableDatabase();
        Cursor cursor=db.rawQuery("select * from "+TABLE_SALE_PURCHASE_ORDER_HEADER+" where "
                +Sales_purchase_order_class.I_DOC_TYPE+"='"+iDocType+"' order by "+Sales_purchase_order_class.I_TRANS_ID,null);
        cursor.moveToFirst();
        return cursor;
    }

    public Cursor getDataFromS_P_Order_HeaderPost() {

        this.db=getReadableDatabase();
        Cursor cursor=db.rawQuery("select * from "+TABLE_SALE_PURCHASE_ORDER_HEADER+
                " where status=0 union select * from "+TABLE_SALE_PURCHASE_ORDER_HEADER+
                " where status=1 and processTime <=( SELECT datetime('now','localtime','-1 hours'));",null);
        cursor.moveToFirst();
        return cursor;
    }
}
