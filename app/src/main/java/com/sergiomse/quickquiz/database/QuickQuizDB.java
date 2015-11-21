package com.sergiomse.quickquiz.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.sergiomse.quickquiz.model.Folder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sergiomse@gmail.com on 21/11/2015.
 */
public class QuickQuizDB {

    public static final String DATABASE_NAME = "quickquiz.db";
    public static final String DATABASE_TABLE = "folders";
    public static final int DATABASE_VERSION = 1;

    public static final String[] COLS = new String[] {"_id", "name", "parent", "root"};



    /**
     *
     */
    private static class QuickQuizDBOpenHelper extends SQLiteOpenHelper {

        private static final String TAG = QuickQuizDBOpenHelper.class.getName();

        private Context context;

        private static final String CREATE_TABLE = "create table " +
                DATABASE_TABLE + " (" +
                "_id integer primary key autoincrement, " +
                "name text, " +
                "parent integer," +
                "root integer);";

        public QuickQuizDBOpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            this.context = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE);

            //insert root folder
            ContentValues values = new ContentValues();
            values.put("name", "root");
            values.put("parent", 0);
            values.put("root", 1);
            db.insert(DATABASE_TABLE, null, values);

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.d(TAG, "Database upgrade");
        }
    }



    private QuickQuizDBOpenHelper helper;
    private SQLiteDatabase db;

    public QuickQuizDB(Context context) {
        helper = new QuickQuizDBOpenHelper(context);
        establishDb();
    }

    public void establishDb() {
        if(db == null) {
            db = helper.getWritableDatabase();
        }
    }

    public void cleanup() {
        if(db != null) {
            db.close();
            db = null;
        }
    }

    public Folder getFolderByColumnValue(String column, String value) {
        Folder folder = null;

        Cursor c = null;
        if (value != null ) {
            c = db.query(DATABASE_TABLE, COLS, column + "=?", new String[]{value}, null, null, null);
        } else {
            c = db.query(DATABASE_TABLE, COLS, column + " is null", null, null, null, null);
        }
        if(c.moveToNext()) {
            folder = new Folder();
            folder.setId(c.getLong(0));
            folder.setName(c.getString(1));
            folder.setParentId(c.getLong(2));
            folder.setIsRoot(c.getInt(3) == 1);
        }
        c.close();

        return folder;
    }

    public List<Folder> getFolderListByColumnValue(String column, String value) {
        List<Folder> folders = new ArrayList<>();

        Cursor c = null;
        if (value != null ) {
            c = db.query(DATABASE_TABLE, COLS, column + "=?", new String[]{value}, null, null, null);
        } else {
            c = db.query(DATABASE_TABLE, COLS, column + " is null", null, null, null, null);
        }
        while(c.moveToNext()) {
            Folder folder = new Folder();
            folder.setId(c.getLong(0));
            folder.setName(c.getString(1));
            folder.setParentId(c.getLong(2));
            folder.setIsRoot(c.getInt(3) == 1);

            folders.add(folder);
        }
        c.close();

        return folders;
    }

    public void insertFolder(Folder folder) {
        ContentValues values = new ContentValues();
        values.put("name", folder.getName());
        values.put("parent", folder.getParentId());
        values.put("root", folder.isRoot() ? 1 : 0);
        db.insert(DATABASE_TABLE, null, values);
    }
}
