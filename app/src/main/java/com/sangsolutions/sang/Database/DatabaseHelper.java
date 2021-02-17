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
    private static final int DATABASE_VERSION = 5;
    private static final String DATABASE_NAME = "Sang.db";
    private static final String TABLE_MASTER_SETTINGS = "t1_masterSettings";
    private static final String TABLE_ACCOUNTS = "t1_accounts";
    private static final String TABLE_PRODUCT = "t1_product";
    private static final String TABLE_TRANSACTION_SETTING = "t1_transactionSetting";
    private static final String TABLE_USER = "t1_user";
    private static  final String TABLE_CURRENT_LOGIN = "t1_currentLogin";
    private static  final String TABLE_TAG_DETAILS = "t1_tag_details";
    private static  final String TABLE_BANK = "t1_bank";


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

    public boolean getTagNameValid(String tagName) {
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
}
