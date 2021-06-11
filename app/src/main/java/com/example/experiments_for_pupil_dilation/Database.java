package com.example.experiments_for_pupil_dilation;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class Database extends SQLiteOpenHelper
{

    private static final int DATABASE_VERSION = 1;
    private static String DATABASE_NAME = "Details.db";
    private static String TABLE_NAME = "Username";
    private static String USERNAME = "Username";
    private static String EXPERIMENTNAME = "ExperimentName";

    public Database(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME +"(Username TEXT, ExperimentName TEXT)";
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    public void addData(String username, String experiment_name)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(USERNAME, username);
        values.put(EXPERIMENTNAME, experiment_name);
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    private void updateData(String username, String experiment_name)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USERNAME , username);
        String selection = EXPERIMENTNAME + " = ?";
        String[] selectionArgs = {experiment_name};
        db.update(TABLE_NAME,values,selection,selectionArgs);
        db.close();
    }

    private Cursor getCursor()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM "+TABLE_NAME;
        Cursor result = db.rawQuery(query,null);
        return result;
    }

    public String[][] getData()
    {
        Cursor result = getCursor();
        int size = result.getCount();

        String[][] data = new String[size][2];
        int row = 0 ;
        while(result.moveToNext())
        {
            data[row][0] = result.getString(0);
            data[row][1] = result.getString(1);
            row++;
        }
        result.close();
        return data;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1)
    {

    }

}
