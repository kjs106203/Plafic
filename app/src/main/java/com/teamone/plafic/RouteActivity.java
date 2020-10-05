package com.teamone.plafic;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import com.odsay.odsayandroidsdk.ODsayData;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RouteActivity extends AppCompatActivity {
    ODsayBackend oDsayBackend;
    ListView listView;

    private LocationManager locationManager;

    double longitude = 0;
    double latitude = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        Intent intent = getIntent();
        String strItem = intent.getExtras().getString("strItem");

        double dst_longitude = Double.parseDouble(strItem.split(" ")[4]);
        double dst_latitude = Double.parseDouble(strItem.split(" ")[5]);

        oDsayBackend = ODsayBackend.getInstance(getApplicationContext());

        listView = (ListView)findViewById(R.id.listRoute);
        List<String> list = new ArrayList<>();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 101);
        }

        Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (loc != null) {
            longitude = loc.getLongitude();
            latitude = loc.getLatitude();
        }

        JSONArray path = oDsayBackend.requestRoute(longitude, latitude, dst_longitude, dst_latitude);

        for (int i = 0; i < path.length(); i++) {
            StringBuilder strpath = new StringBuilder();
            strpath.append("\n");
            Log.d("SUBPATH", String.valueOf(i));
            try {
                JSONArray subPath = path.getJSONObject(i).getJSONArray("subPath");
                JSONArray info = oDsayBackend.pathArray;

                for (int j = 0; j < subPath.length(); j++) {

                    JSONObject item = subPath.getJSONObject(j);
                    Log.d("SUBPATH", item.toString());

                    if (item.getInt("trafficType") == 3) {
                        strpath.append("걷기 : " + String.valueOf(item.getInt("distance")) + "m\n");
                    } else {
                        if (item.getInt("trafficType") == 1) {
                            strpath.append("지하철 : ");
                        } else {
                            strpath.append("버스 : ");
                        }
                        JSONArray lanes = item.getJSONArray("lane");
                        for (int l = 0; l < lanes.length(); l++) {
                            strpath.append(lanes.getJSONObject(l).getString("busNo") + " \n"  + String.valueOf(item.getString("startName")) + " 탑승 \n" + String.valueOf(item.getString("endName")) + " 하차");
                        }
                        strpath.append("\n");
                        JSONArray stations = item.getJSONObject("passStopList").getJSONArray("stations");
                    }
                }
                int time = info.getJSONObject(i).getJSONObject("info").getInt("totalTime");
                int pay = info.getJSONObject(i).getJSONObject("info").getInt("payment");
                strpath.append("소요시간 : " + String.valueOf(time) + "분\n");
                strpath.append("금액 : " + String.valueOf(pay) + "원\n");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            list.add(strpath.toString());
        }

        Log.d("ROUTE_PATH", path.toString());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);
    }
}
