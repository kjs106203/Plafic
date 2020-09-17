package com.teamone.plafic;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.BaseColumns;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

public class RefreshThread extends Thread {
    MainActivity m_parent;
    private final LocationListener locationListener;
    private LocationManager locationManager;

    double longitude;
    double latitude;

    ODsayBackend oDsayBackend;

    public RefreshThread(MainActivity parent) {
        m_parent = parent;

        locationListener = location -> {};
        locationManager = (LocationManager) m_parent.getSystemService(Context.LOCATION_SERVICE);

        try {
            if (ContextCompat.checkSelfPermission(m_parent.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(m_parent, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("PERMISSION", "PERMISSION ERROR");
        }

        oDsayBackend = new ODsayBackend(m_parent.getApplicationContext());
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (ContextCompat.checkSelfPermission(m_parent.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(m_parent, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 101);
                }

                Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if (loc != null) {
                    longitude = loc.getLongitude();
                    latitude = loc.getLatitude();

                    m_parent.longitude = longitude;
                    m_parent.latitude = latitude;
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("PERMISSION", "PERMISSION ERROR");
            }

            DBManager dbManager = new DBManager(m_parent.getApplicationContext());

            SQLiteDatabase db = dbManager.getReadableDatabase();
            String[] projection = {
                    BaseColumns._ID,
                    DBEntry.TITLE,
                    DBEntry.DATE,
                    DBEntry.TIME,
                    DBEntry.LOCATION,
                    DBEntry.GEO_X,
                    DBEntry.GEO_Y
            };

            Cursor cursor = db.query(
                    DBEntry.TABLE_NAME,
                    projection,
                    null,
                    null,
                    null,
                    null,
                    null
            );

            while (cursor.moveToNext()) {
                String ttl = cursor.getString(cursor.getColumnIndexOrThrow(DBEntry.TITLE));
                String dat = cursor.getString(cursor.getColumnIndexOrThrow(DBEntry.DATE));
                String tim = cursor.getString(cursor.getColumnIndexOrThrow(DBEntry.TIME));
                String x = cursor.getString(cursor.getColumnIndexOrThrow(DBEntry.GEO_X));
                String y = cursor.getString(cursor.getColumnIndexOrThrow(DBEntry.GEO_Y));

                JSONArray path = oDsayBackend.requestRoute(longitude, latitude, Double.parseDouble(x), Double.parseDouble(y));

                Vector<Integer> totaltimes = new Vector<>();

                if (path != null) {
                    Log.d("PATH", "NOT NULL");
                    for (int i = 0; i < path.length(); i++) {
                        try {
                            totaltimes.add(path.getJSONObject(i).getJSONObject("info").getInt("totalTime"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("JSON_ERROR", "JSON error occured.");
                        }
                    }

                    int longesttime = 0;

                    for (int time : totaltimes) {
                        if (time > longesttime) {
                            longesttime = time;
                        }

                        Date date = new Date();

                        try {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String datestr = dat + " " + tim + ":00";
                            Log.d("DATE", datestr);
                            date = dateFormat.parse(datestr);
                            Log.d("DATE_PARSED", date.toString());
                        } catch (ParseException e) {
                            e.printStackTrace();
                            Log.e("PARSE", "Parse error occured.");
                        }

                        long datelong = date.getTime() / 1000;
                        Log.d("TIME_COMPARE", String.valueOf(datelong));

                        long currenttime = System.currentTimeMillis() / 1000;
                        Log.d("TIME_COMPARE", String.valueOf(currenttime));

                        if (datelong - (time * 60) < currenttime) {
                            m_parent.showNoti("'" + ttl + "' 약속을 위해 지금 출발해야해요.");
                        }
                    }
                }
            }

            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Log.e("REFRESH_TH", "Thread interrupted.");
            }
        }
    }
}
