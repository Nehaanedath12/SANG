package com.sangsolutions.sang;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.util.TypedValue;

import com.sangsolutions.sang.Database.Sales_purchase_Class;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import io.fotoapparat.result.PhotoResult;

public class Tools {

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    public static boolean isValidIP(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.IP_ADDRESS.matcher(target).matches());
    }




    public String getIP(Context context){
        preferences = context.getSharedPreferences("Settings",Context.MODE_PRIVATE);
        if(preferences!=null){
            return preferences.getString("IP","");
        }
        return "";
    }


    public boolean setIP(Context context, String IP){
        preferences = context.getSharedPreferences("Settings",Context.MODE_PRIVATE);
        editor = preferences.edit();
        if(editor!=null){
            editor.putString("IP",IP).apply();
        }
        return true;
    }
    public static boolean isConnected(Context context){
        try {
            ConnectivityManager cm =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();
        } catch (Exception e) {
            return false;
        }
    }
    public static String checkDigit(int number) {
        return number <= 9 ? "0" + number : String.valueOf(number);
    }

    public static String dateFormat(String dateToFormat){
        DateFormat originalFormat = new SimpleDateFormat("dd-MM-yyyy");
        DateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = originalFormat.parse(dateToFormat);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return targetFormat.format(Objects.requireNonNull(date));
    }

    public int GetPixels(int dp,Context context){
        float px = dp;
        try {
            Resources r = context.getResources();
            px = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    dp,
                    r.getDisplayMetrics()
            );
        }catch (Exception e){
            e.printStackTrace();
        }
        return (int)px;
    }
    public static String savePhoto(Context context, PhotoResult photoResult) {
        String fileName = "IMG_"+System.currentTimeMillis() + ".jpg";
        File folder = new File(context.getExternalFilesDir(null) + File.separator +"temp");

        if (!folder.exists()) {
            Log.d("folder created:", "" + folder.mkdir());
        }
        File file = new File(folder, fileName);
        photoResult.saveToFile(file);
        return file.getAbsolutePath();
    }

    public static String savePhotoURL(Context context, Bitmap photoResult) {
        String fileName = "IMG_"+System.currentTimeMillis() + ".jpg";
        File folder = new File(context.getExternalFilesDir(null) + File.separator +"temp");

        if (!folder.exists()) {
            Log.d("folder created:", "" + folder.mkdir());
        }
        File file = new File(folder, fileName);
//        photoResult.toURI(file);

        try {
            FileOutputStream out = new FileOutputStream(file);
            photoResult.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return file.getAbsolutePath();
    }


    public static String getFileList(String filepath){
        List<String> list = Arrays.asList(filepath.split(","));
        StringBuilder sb = new StringBuilder();
        try {
            String SEPARATOR = "";
            for (int i = 0; i < list.size(); i++) {
                sb.append(SEPARATOR);
                sb.append(list.get(i).substring(list.get(i).lastIndexOf("/") + 1));
                SEPARATOR = ",";
            }
            SEPARATOR = "";
        }catch (Exception e){
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static File CompressImage(File file, Context context) {

        Bitmap b = BitmapFactory.decodeFile(file.getAbsolutePath());
        //Convert bitmap to byte array
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.JPEG, 50, bos);
        byte[] bitmapdata = bos.toByteArray();

        //write the bytes in file
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(file);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }

    public static int getDocNo(JSONArray jsonArray) {
        List<Integer>num=new ArrayList<>();
        for (int i=0;i<jsonArray.length();i++){
            try {
                Log.d("responseee",jsonArray.toString());
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                String sDocNo=jsonObject.getString("sDocNo");
                String result[] = sDocNo.split("-");
                String returnValue = result[result.length - 1];
                num.add(Integer.parseInt(returnValue));
                Log.d("docNumbers",num.get(i).toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Collections.sort(num);

        int value=num.get(num.size() - 1);
        Log.d("docNumberr",num.get(num.size() - 1).toString()+" "+value);
        return value+1;
    }


    public static int getNewDocNoLocally(Cursor cursor1) {
        List<Integer>num=new ArrayList<>();
        if(cursor1.moveToFirst()){
            for (int i=0;i<cursor1.getCount();i++){
                String sDocNo=cursor1.getString(cursor1.getColumnIndex(Sales_purchase_Class.S_DOC_NO));
                String result[] = sDocNo.split("-");
                String returnValue = result[result.length - 1];
                num.add(Integer.parseInt(returnValue));
                Log.d("docNumbers",num.get(i).toString());
                cursor1.moveToNext();
            }
        }
        Collections.sort(num);

        int value=num.get(num.size() - 1);
        Log.d("docNumberr",num.get(num.size() - 1).toString()+" "+value);
        return value+1;
    }


}
