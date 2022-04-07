package com.assignment.data.covid19.preferences;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

public class MySharedPreferences {

    public static void save(Context context, String name, String from, String to) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MySharedPreferences", MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.putString("name",name);
        myEdit.putString("from", from);
        myEdit.putString("to", to);
        myEdit.apply();
    }

    public static String getName(Context context){
        SharedPreferences sh = context.getSharedPreferences("MySharedPreferences", MODE_PRIVATE);
        return sh.getString("name", "");
    }

    public static String getFromDate(Context context){
        SharedPreferences sh = context.getSharedPreferences("MySharedPreferences", MODE_PRIVATE);
        return sh.getString("from", "");
    }

    public static String getToDate(Context context){
        SharedPreferences sh = context.getSharedPreferences("MySharedPreferences", MODE_PRIVATE);
        return sh.getString("to", "");
    }
}
