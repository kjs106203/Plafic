package com.teamone.plafic;

import android.provider.BaseColumns;

public class DBEntry implements BaseColumns {
    public static final String TABLE_NAME = "plafic";
    public static final String TITLE = "title";
    public static final String DATE = "date";
    public static final String TIME = "time";
    public static final String LOCATION = "location";
    public static final String GEO_X = "geo_x";
    public static final String GEO_Y = "geo_y";

    public static final String SQL_CREATE_TABLE = "CREATE TABLE " +
            TABLE_NAME + " (" + _ID + " INTEGER PRIMARY KEY," +
            TITLE + " TEXT," + DATE + " TEXT," + TIME + " TEXT," +
            LOCATION + " TEXT," + GEO_X + " TEXT," + GEO_Y + " TEXT)";
}
