package com.example.mcc.supertime.NoteBook;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class NoteDataBaseHelper extends SQLiteOpenHelper {

    public static final String CreateNote =
            "create table note ("
                    + "id integer primary key autoincrement, "
                    + "content text , "
                    + "finish text , "
                    + "date text)";

    public NoteDataBaseHelper(Context context) {
        super(context, "note", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CreateNote);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
