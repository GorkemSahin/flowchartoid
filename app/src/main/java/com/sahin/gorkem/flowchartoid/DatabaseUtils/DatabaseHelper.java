package com.sahin.gorkem.flowchartoid.DatabaseUtils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gorkem on 5/18/2018.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "diagrams_db";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Diagram.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Diagram.TABLE_NAME);
        onCreate(db);
    }

    public long insertDiagram (String name, String data, String destination) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Diagram.COLUMN_NAME, name);
        values.put(Diagram.COLUMN_DATA, data);
        values.put(Diagram.COLUMN_DEST, destination);
        long id = db.insert(Diagram.TABLE_NAME, null, values);
        db.close();
        return id;
    }

    public Diagram getDiagram (String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(Diagram.TABLE_NAME,
                new String[]{Diagram.COLUMN_ID, Diagram.COLUMN_NAME,
                        Diagram.COLUMN_DATA, Diagram.COLUMN_DEST, Diagram.COLUMN_TIMESTAMP},
                Diagram.COLUMN_NAME + "=?",
                new String[]{name}, null, null, null, null);
        if (cursor == null || cursor.getCount() == 0) {
            return null;
        }
        cursor.moveToFirst();
        Diagram diagram = new Diagram(
                cursor.getInt(cursor.getColumnIndex(Diagram.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(Diagram.COLUMN_NAME)),
                cursor.getString(cursor.getColumnIndex(Diagram.COLUMN_DATA)),
                cursor.getString(cursor.getColumnIndex(Diagram.COLUMN_DEST)),
                cursor.getString(cursor.getColumnIndex(Diagram.COLUMN_TIMESTAMP)));
        cursor.close();
        return diagram;
    }

    public List<Diagram> getAllDiagrams() {
        List<Diagram> notes = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + Diagram.TABLE_NAME + " ORDER BY " +
                Diagram.COLUMN_TIMESTAMP + " DESC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Diagram diagram = new Diagram();
                diagram.setId(cursor.getInt(cursor.getColumnIndex(Diagram.COLUMN_ID)));
                diagram.setName(cursor.getString(cursor.getColumnIndex(Diagram.COLUMN_NAME)));
                diagram.setData(cursor.getString(cursor.getColumnIndex(Diagram.COLUMN_DATA)));
                diagram.setDestination(cursor.getString(cursor.getColumnIndex(Diagram.COLUMN_DEST)));
                diagram.setTimestamp(cursor.getString(cursor.getColumnIndex(Diagram.COLUMN_TIMESTAMP)));
                notes.add(diagram);
            } while (cursor.moveToNext());
        }
        db.close();
        return notes;
    }

    public int getDiagramsCount() {
        String countQuery = "SELECT  * FROM " + Diagram.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public int updateDiagram(String name, String data) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Diagram.COLUMN_DATA, data);
        return db.update(Diagram.TABLE_NAME, values, Diagram.COLUMN_NAME + " = ?",
                new String[]{String.valueOf(name)});
    }

    public void deleteDiagram(Diagram diagram) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Diagram.TABLE_NAME, Diagram.COLUMN_ID + " = ?",
                new String[]{String.valueOf(diagram.getId())});
        db.close();
    }

}
