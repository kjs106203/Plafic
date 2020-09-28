package com.teamone.plafic;

import android.content.Context;
import android.util.Log;

import com.odsay.odsayandroidsdk.API;
import com.odsay.odsayandroidsdk.ODsayData;
import com.odsay.odsayandroidsdk.ODsayService;
import com.odsay.odsayandroidsdk.OnResultCallbackListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ODsayBackend implements OnResultCallbackListener {
    private static ODsayBackend uniqueInstance = null;

    String apiKey = "inV61SM+ZAXovckEz4mF1RQKg5j7T9bAJvvpxOvvvI4";
    ODsayService oDsayService;
    JSONArray pathArray;

    public ODsayBackend(Context context) {
        oDsayService = ODsayService.init(context, apiKey);

        oDsayService.setConnectionTimeout(5000);
        oDsayService.setReadTimeout(5000);

        oDsayService.requestBusLaneDetail("12018", new OnResultCallbackListener() {
            @Override
            public void onSuccess(ODsayData oDsayData, API api) {
                Log.d("APIRES", oDsayData.toString());
            }

            @Override
            public void onError(int i, String s, API api) {
                Log.e("APIERR", "API FAILED.");
            }
        });
    }

    public static ODsayBackend getInstance(Context context) {
        if (uniqueInstance == null) {
            uniqueInstance = new ODsayBackend(context);
        }

        return uniqueInstance;
    }

    public JSONArray requestRoute(double sx, double sy, double ex, double ey) {
        String s_sx = String.valueOf(sx);
        String s_sy = String.valueOf(sy);
        String s_ex = String.valueOf(ex);
        String s_ey = String.valueOf(ey);

        oDsayService.requestSearchPubTransPath(s_sx, s_sy, s_ex, s_ey, "0", "", "0", this);

        return pathArray;
    }

    @Override
    public void onSuccess(ODsayData oDsayData, API api) {
        try {
            JSONObject resjson = oDsayData.getJson().getJSONObject("result");
            Log.d("JSON", resjson.toString());

            pathArray = resjson.getJSONArray("path");

            for (int i = 0; i < pathArray.length(); i++) {
                Log.d("JSON_PATH", String.valueOf(pathArray.get(i)));
                int time = pathArray.getJSONObject(i).getJSONObject("info").getInt("totalTime");
                Log.d("ROUTE", String.valueOf(time));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("JSON_ERROR", "JSON error occured.");
        }
    }

    @Override
    public void onError(int i, String s, API api) {

    }
}
