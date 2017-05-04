package com.example.adiad.articulo;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Lincoln on 05/05/16.
 */
public class PrefManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    // shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "MYAPP";

    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";


    private static final String PERCENTAGE="percentage";

    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    public void setPercentage(String percentage)
    {
        editor.putString(PERCENTAGE,percentage);
        editor.commit();
    }


    public String getPercentage()
    {
        return pref.getString(PERCENTAGE,"my percetage");
    }

}