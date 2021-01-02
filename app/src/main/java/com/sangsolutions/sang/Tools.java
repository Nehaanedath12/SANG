package com.sangsolutions.sang;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Patterns;

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


}
