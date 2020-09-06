package com.teamone.plafic;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONException;

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

                //m_parent.showNoti(String.valueOf(longitude) + " / " + String.valueOf(latitude), m_parent.notiBuilder, m_parent.notiManager);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("PERMISSION", "PERMISSION ERROR");
            }

            JSONArray path = oDsayBackend.requestRoute(longitude, latitude, 127.087869, 36.990480);

            Vector<Integer> totaltimes = new Vector<>();

            if (path != null) {
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
                }

                m_parent.showNoti(String.valueOf(longesttime), m_parent.notiBuilder, m_parent.notiManager);
            }

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Log.e("REFRESH_TH", "Thread interrupted.");
            }
        }
    }
}
