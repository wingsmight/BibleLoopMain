package com.wingsmight.bibleloop;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;


public class SaveLoadData
{
    private static Context mainContext;


    public static void SetSaveExistPoem(TypePoem typePoem, String poemTitle, boolean isSave)
    {
        mainContext = MainActivity.GetContext();

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(mainContext);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(poemTitle + typePoem, isSave);
        editor.apply();
    }
    public static boolean LoadExistPoem(TypePoem typePoem, String poemTitle)
    {
        mainContext = MainActivity.GetContext();

        SharedPreferences sharedPref = PreferenceManager
                .getDefaultSharedPreferences(mainContext);
        return sharedPref.getBoolean(poemTitle + typePoem, false);
    }

    public static void SaveIsPayment(boolean isPayment)
    {
        mainContext = MainActivity.GetContext();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mainContext);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isPayment", isPayment);
        editor.apply();
    }
    public static boolean LoadIsPayment()
    {
        mainContext = MainActivity.GetContext();

        SharedPreferences sharedPref = PreferenceManager
                .getDefaultSharedPreferences(mainContext);
        return sharedPref.getBoolean("isPayment", false);
    }
}
