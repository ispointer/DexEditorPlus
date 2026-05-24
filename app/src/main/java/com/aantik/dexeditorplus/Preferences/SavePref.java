package com.aantik.dexeditorplus.Preferences;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.Contract;

/**
 * @UXT
 * @w3schools
 */

public class SavePref {
    private static final String PREF_NAME = "DexEditorPlus";

    // String based methods (Cleaner)
    public static boolean getBool(@NonNull Context context, String key, boolean def) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getBoolean(key, def);
    }

    public static void setBool(@NonNull Context context, String key, boolean val) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit().putBoolean(key, val).apply();
    }

    @Contract("_, _, !null -> !null")
    public static String getStr(@NonNull Context context, String key, String def) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getString(key, def);
    }

    public static void setStr(@NonNull Context context, String key, String val) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit().putString(key, val).apply();
    }

    /**
     *
     *   Integer Legacy bSpecific
     *   @https://www.w3schools.com/
     */

    public static boolean getBoolPref(@NonNull final Context context, final int keyResId) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME + keyResId, Context.MODE_PRIVATE);
        return prefs.getBoolean(PREF_NAME + keyResId, (keyResId == 1000));
    }

    public static void setBoolPref(@NonNull final Context context, final int keyResId, final boolean value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME + keyResId, Context.MODE_PRIVATE).edit();
        editor.putBoolean(PREF_NAME + keyResId, value);
        editor.apply();
    }

    public static String getStrPref(@NonNull final Context context, final int keyResId, final String defaultValue) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME + keyResId, Context.MODE_PRIVATE);
        return prefs.getString(PREF_NAME + keyResId, defaultValue);
    }

    public static void setStrPref(@NonNull Context context, int keyResId, String value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME + keyResId, Context.MODE_PRIVATE).edit();
        editor.putString(PREF_NAME + keyResId, value);
        editor.apply();
    }

    public static int getIntPref(@NonNull final Context context, final int keyResId) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME + keyResId, Context.MODE_PRIVATE);
        return prefs.getInt(PREF_NAME + keyResId, 0);
    }

    public static void setIntPref(@NonNull final Context context, final int keyResId, final int value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME + keyResId, Context.MODE_PRIVATE).edit();
        editor.putInt(PREF_NAME + keyResId, value);
        editor.apply();
    }

    public static float getFloatPref(@NonNull final Context context, final int keyResId) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME + keyResId, Context.MODE_PRIVATE);
        return prefs.getFloat(PREF_NAME + keyResId, 0.0f);
    }

    public static void setFloatPref(@NonNull final Context context, final int keyResId, final float value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME + keyResId, Context.MODE_PRIVATE).edit();
        editor.putFloat(PREF_NAME + keyResId, value);
        editor.apply();
    }

    public static void clearStrPref(@NonNull Context context, int keyResId) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME + keyResId, Context.MODE_PRIVATE).edit();
        editor.remove(PREF_NAME + keyResId);
        editor.apply();
    }
}
