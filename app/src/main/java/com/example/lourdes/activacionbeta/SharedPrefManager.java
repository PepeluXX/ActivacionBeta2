package com.example.lourdes.activacionbeta;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Lourdes on 20/11/2017.
 */

public class SharedPrefManager {

    private static Context mCtx;
    private static SharedPrefManager mInstance;
    private static final String SHARED_PREF_NAME="mi_caja_de_datos";
    private static final String KEY_TOKEN = "token";

    private SharedPrefManager(Context context)
    {
        mCtx = context;
    }

    public static synchronized SharedPrefManager getInstance(Context context){
        if(mInstance==null)
            mInstance = new SharedPrefManager(context);
        return mInstance;
    }

    public boolean storeToken(String token){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_TOKEN,token);
        editor.apply();
        return true;

    }

    public String getToken(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_TOKEN,null);
    }
}
