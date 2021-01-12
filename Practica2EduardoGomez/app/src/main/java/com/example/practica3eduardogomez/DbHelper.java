package com.example.practica3eduardogomez;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;

import static com.example.practica3eduardogomez.DatabaseConstants.CREATE_TABLE;
import static com.example.practica3eduardogomez.DatabaseConstants.C_ADDED_TIMESTAMP;
import static com.example.practica3eduardogomez.DatabaseConstants.C_ADDRESS;
import static com.example.practica3eduardogomez.DatabaseConstants.C_APPOINTMENT;
import static com.example.practica3eduardogomez.DatabaseConstants.C_EMAIL;
import static com.example.practica3eduardogomez.DatabaseConstants.C_ID;
import static com.example.practica3eduardogomez.DatabaseConstants.C_IMAGE;
import static com.example.practica3eduardogomez.DatabaseConstants.C_LAST_NAME;
import static com.example.practica3eduardogomez.DatabaseConstants.C_NAME;
import static com.example.practica3eduardogomez.DatabaseConstants.C_PHONE_NUMBER;
import static com.example.practica3eduardogomez.DatabaseConstants.C_UPDATED_TIMESTAMP;
import static com.example.practica3eduardogomez.DatabaseConstants.DB_NAME;
import static com.example.practica3eduardogomez.DatabaseConstants.DB_VERSION;
import static com.example.practica3eduardogomez.DatabaseConstants.DROP;
import static com.example.practica3eduardogomez.DatabaseConstants.TABLE_NAME;

public class DbHelper extends SQLiteOpenHelper {

    private static final String TAG = "ed" ;

    public DbHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //update our database (if we update our app)
        sqLiteDatabase.execSQL(DROP + " " + TABLE_NAME);
        //If we drop our table, we need to create it again
        onCreate(sqLiteDatabase);
    }

    //insert TO DB
    public long InsertRecord(String image, String name, String lastName, String occupation, String address, String phoneNumber,
                             String addedTime, String updatedTime, String appointment){

        //Get the writable database, because we want to add into it
        SQLiteDatabase db = this.getWritableDatabase();

        //Insert data
        ContentValues values = new ContentValues();

        values.put(C_IMAGE, image);
        values.put(C_NAME, name);
        values.put(C_LAST_NAME, lastName);
        values.put(C_EMAIL, occupation);
        values.put(C_ADDRESS, address);
        values.put(C_PHONE_NUMBER, phoneNumber);
        values.put(C_ADDED_TIMESTAMP, addedTime);
        values.put(C_UPDATED_TIMESTAMP, updatedTime);
        values.put(C_APPOINTMENT, appointment);

        long id = db.insert(TABLE_NAME, null, values);

            if (id != -1){
                Log.e(TAG, "InsertRecord: " + "inserted");
            }

        db.close();

        return id;
    }

    //Get all Data from database
    public ArrayList<Contact> GetAllContacts(String orderBy){

        ArrayList<Contact> contactArrayList = new ArrayList<>();
        //Query to select records
        String selectQuery = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + orderBy;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);

        //Loop thought everything
        if (cursor.moveToFirst())
            do {
                Contact contact = new Contact(
                        ""+cursor.getInt(cursor.getColumnIndex(C_ID)),
                        ""+cursor.getString(cursor.getColumnIndex(C_IMAGE)),
                        ""+cursor.getString(cursor.getColumnIndex(C_NAME)),
                        ""+cursor.getString(cursor.getColumnIndex(C_LAST_NAME)),
                        ""+cursor.getString(cursor.getColumnIndex(C_EMAIL)),
                        ""+cursor.getString(cursor.getColumnIndex(C_ADDRESS)),
                        ""+cursor.getString(cursor.getColumnIndex(C_PHONE_NUMBER)),
                        ""+cursor.getString(cursor.getColumnIndex(C_ADDED_TIMESTAMP)),
                        ""+cursor.getString(cursor.getColumnIndex(C_APPOINTMENT))
                );

                //We need to add our record to the list

                contactArrayList.add(contact);
            }while (cursor.moveToNext());

            //close the connection

            db.close();

            return contactArrayList;
    }

    //Search data
    public ArrayList<Contact> SearchContacts(String query){

        ArrayList<Contact> contactArrayList = new ArrayList<>();
        //Query to select records
        String selectQuery = "SELECT * FROM " + TABLE_NAME + " WHERE " + C_NAME + " LIKE '%" + query +"%'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);

        //Loop thought everything
        if (cursor.moveToFirst())
            do {
                Contact contact = new Contact(
                        ""+cursor.getInt(cursor.getColumnIndex(C_ID)),
                        ""+cursor.getString(cursor.getColumnIndex(C_IMAGE)),
                        ""+cursor.getString(cursor.getColumnIndex(C_NAME)),
                        ""+cursor.getString(cursor.getColumnIndex(C_LAST_NAME)),
                        ""+cursor.getString(cursor.getColumnIndex(C_EMAIL)),
                        ""+cursor.getString(cursor.getColumnIndex(C_ADDRESS)),
                        ""+cursor.getString(cursor.getColumnIndex(C_PHONE_NUMBER)),
                        ""+cursor.getString(cursor.getColumnIndex(C_ADDED_TIMESTAMP)),
                        ""+cursor.getString(cursor.getColumnIndex(C_APPOINTMENT))
                );

                //We need to add our record to the list

                contactArrayList.add(contact);
            }while (cursor.moveToNext());

        //close the connection

        db.close();

        return contactArrayList;
    }

    //Get the count of records
    public int GetRecordsCount(){
        String countQuery = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(countQuery,null);

        int count = cursor.getCount();
        cursor.close();

        return count;
    }

    //Update data
    public void UpdateRecord(String id, String image, String name, String lastName, String occupation, String address, String phoneNumber,
                             String addedTime, String updatedTime, String appointment){

        //Get the writable database, because we want to add into it
        SQLiteDatabase db = this.getWritableDatabase();

        //Insert data
        ContentValues values = new ContentValues();

        values.put(C_IMAGE, image);
        values.put(C_NAME, name);
        values.put(C_LAST_NAME, lastName);
        values.put(C_EMAIL, occupation);
        values.put(C_ADDRESS, address);
        values.put(C_PHONE_NUMBER, phoneNumber);
        values.put(C_ADDED_TIMESTAMP, addedTime);
        values.put(C_UPDATED_TIMESTAMP, updatedTime);
        values.put(C_APPOINTMENT, appointment);

        db.update(TABLE_NAME, values, C_ID + " = ?", new String[]{id});
        db.close();
    }

    //Delete using ID
    public void DeleteData(String id){
        SQLiteDatabase db = getReadableDatabase();
            db.delete(TABLE_NAME, C_ID +" = ?", new String[]{id});
        db.close();
    }
    //Delete all records
    public void DeleteAllData(){
        SQLiteDatabase db = getReadableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME);
        db.close();
    }
}
