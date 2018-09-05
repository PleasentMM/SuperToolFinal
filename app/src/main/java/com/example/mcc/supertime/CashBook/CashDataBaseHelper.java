package com.example.mcc.supertime.CashBook;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CashDataBaseHelper extends SQLiteOpenHelper {

    public static final String CreateCash = "create table cash ("
            + "id integer primary key autoincrement, "
            + "content text , "
            + "date text ,"
            + "cost integer)";

    public CashDataBaseHelper(Context context) {
        super(context, "cash", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CreateCash);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
