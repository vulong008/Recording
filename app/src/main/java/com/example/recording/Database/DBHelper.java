package com.example.recording.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.recording.Interfaces.OnDatabaseChangeListener;
import com.example.recording.Models.RecordingItem;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    private Context cconlG;
    public static final String DATABASE_NAME = "saved_recording.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "saved_recording_table";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_PATH = "path";
    private static final String COLUMN_LENGTH = "length";
    private static final String COLUMN_TIME_ADDED = "time_added";
    private static final String COLUMN_SEP = ",";
    private static OnDatabaseChangeListener monDatabaseChangeListener;

    private static final String SQLITE_CREATE_TABLE = "Create table " + TABLE_NAME +
            " (" + "id INTEGER PRIMARY KEY" + " AUTOINCREMENT" + COLUMN_SEP +
            COLUMN_NAME + " TEXT" + COLUMN_SEP
            + COLUMN_PATH + " TEXT" + COLUMN_SEP
            + COLUMN_LENGTH + " INTEGER" + COLUMN_SEP
            + COLUMN_TIME_ADDED + " INTEGER" + ")";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQLITE_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS  " + TABLE_NAME);
    }

    public boolean addRecording(RecordingItem recordingItem) {
        try {
            SQLiteDatabase sqLiteDatabase = getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_NAME, recordingItem.getName());
            contentValues.put(COLUMN_PATH, recordingItem.getPath());
            contentValues.put(COLUMN_LENGTH, recordingItem.getLength());
            contentValues.put(COLUMN_TIME_ADDED, recordingItem.getTime_added());

            sqLiteDatabase.insert(TABLE_NAME, null, contentValues);
            if(monDatabaseChangeListener!=null)
            {
                monDatabaseChangeListener.onNewDatabaseEntryAdd(recordingItem);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<RecordingItem> getAllAudio() {
        ArrayList<RecordingItem> arrayList = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("Select * from " + TABLE_NAME, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(1);
                String path = cursor.getString(2);
                int length = cursor.getInt(3);
                long time = cursor.getLong(4);
                RecordingItem recordingItem = new RecordingItem(name, path, length, time);
                arrayList.add(recordingItem);
            }
            cursor.close();
            return arrayList;
        } else {
            return null;
        }
    }

    public static  void setOnDatabaseChangeListener(OnDatabaseChangeListener listener)
    {
        monDatabaseChangeListener=listener;

    }
}
