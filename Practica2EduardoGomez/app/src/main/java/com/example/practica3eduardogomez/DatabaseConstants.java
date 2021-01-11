package com.example.practica3eduardogomez;

public class DatabaseConstants {

    //Db name

    //ED note: Uppercase is the convention for constant variables

    public static final String DB_NAME = "Records";

    //Db Table Name
    public static final String TABLE_NAME = "MY_CONTACTS";

    //Drop the table
    public static final String DROP = "DROP TABLE IF EXISTS";

    //Db version
    public static final int DB_VERSION = 1;

    //Columns and fields of the table
    public static final String C_ID = "ID";
    public static final String C_IMAGE = "IMAGE";
    public static final String C_NAME = "NAME";
    public static final String C_LAST_NAME = "LAST_NAME";
    public static final String C_EMAIL = "EMAIL";
    public static final String C_ADDRESS = "ADDRESS";
    public static final String C_PHONE_NUMBER = "PHONE_NUMBER";
    public static final String C_BIRTH = "BIRTHDAY";

    //These two will be used by my filter
    public static final String C_ADDED_TIMESTAMP = "ADDED_TIME_STAMP";
    public static final String C_UPDATED_TIMESTAMP = "UPDATED_TIME_STAMP";
    //Create our Query
    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
            + C_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + C_IMAGE + " TEXT,"
            + C_NAME + " TEXT,"
            + C_LAST_NAME + " TEXT,"
            + C_EMAIL + " TEXT,"
            + C_ADDRESS + " TEXT,"
            + C_PHONE_NUMBER + " TEXT,"
            + C_ADDED_TIMESTAMP + " TEXT,"
            + C_UPDATED_TIMESTAMP + " TEXT"
            + C_BIRTH + " TEXT"
            + ")";


}
