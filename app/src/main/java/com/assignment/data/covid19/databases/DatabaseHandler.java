package com.assignment.data.covid19.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.assignment.data.covid19.models.Data;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "contactsManager";
    private static final String TABLE_DATA = "covid_19_data";
    private static final String KEY_CITY_CODE = "CITY_CODE";
    private static final String KEY_STATUS = "STATUS";
    private static final String KEY_COUNTRY = "COUNTRY";
    private static final String KEY_LON = "LON";
    private static final String KEY_CITY = "CITY";
    private static final String KEY_COUNTRY_CODE = "COUNTRY_CODE";
    private static final String KEY_PROVINCE = "PROVINCE";
    private static final String KEY_LAT = "LAT";
    private static final String KEY_CASES = "CASES";
    private static final String KEY_DATE = "DATE";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //3rd argument to be passed is CursorFactory instance
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_DATA +
                "(" +
                KEY_DATE + " TEXT PRIMARY KEY,"
                + KEY_CITY_CODE + " TEXT," + KEY_STATUS + " TEXT," + KEY_COUNTRY + " TEXT,"
                + KEY_LON + " TEXT," + KEY_CITY + " TEXT,"
                + KEY_COUNTRY_CODE + " TEXT," + KEY_PROVINCE + " TEXT,"
                + KEY_LAT + " TEXT," + KEY_CASES + " INTEGER" +
                ")";
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_DATA);
            onCreate(sqLiteDatabase);
        }
    }

    public boolean saveData(Data data) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_CITY_CODE, data.getCityCode());
        values.put(KEY_STATUS, data.getStatus());
        values.put(KEY_COUNTRY, data.getCountry());
        values.put(KEY_LON, data.getLon());
        values.put(KEY_CITY, data.getCity());
        values.put(KEY_COUNTRY_CODE, data.getCountryCode());
        values.put(KEY_PROVINCE, data.getProvince());
        values.put(KEY_LAT, data.getLat());
        values.put(KEY_CASES, data.getCases());
        values.put(KEY_DATE, data.getDate());
        long result = db.insert(TABLE_DATA, null, values);
        Log.d("theS", "saveData: "+result +" "+values);
        db.close();
        return result != -1;
    }

    public List<Data> getAllData() {
        List<Data> dataList = new ArrayList<Data>();
        String selectQuery = "SELECT  * FROM " + TABLE_DATA;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Data data = new Data();
                data.setDate(cursor.getString(0));
                data.setCityCode(cursor.getString(1));
                data.setStatus(cursor.getString(2));
                data.setCountry(cursor.getString(3));
                data.setLon(cursor.getString(4));
                data.setCity(cursor.getString(5));
                data.setCountryCode(cursor.getString(6));
                data.setProvince(cursor.getString(7));
                data.setLon(cursor.getString(8));
                data.setCases(cursor.getInt(9));
                dataList.add(data);
            } while (cursor.moveToNext());
        }
        return dataList;
    }

    public void deleteData(Data data) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_DATA, KEY_DATE + " = ?", new String[]{data.getDate()});
        db.close();
    }

    public boolean exists(String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] columns = { KEY_DATE };
        String selection = KEY_DATE + " =?";
        String[] selectionArgs = { date };
        String limit = "1";

        Cursor cursor = db.query(TABLE_DATA, columns, selection, selectionArgs, null, null, null, limit);
        boolean exists = (cursor.getCount() > 0);
        Log.d("theS", "exists: "+date +" "+exists);
        cursor.close();
        return exists;
    }
}
