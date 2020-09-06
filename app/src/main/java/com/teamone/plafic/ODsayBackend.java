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

public class ODsayBackend {
    String apiKey = "inV61SM+ZAXovckEz4mF1RQKg5j7T9bAJvvpxOvvvI4";
    ODsayService oDsayService;

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

    public JSONArray requestRoute(double sx, double sy, double ex, double ey) {
        String s_sx = String.valueOf(sx);
        String s_sy = String.valueOf(sy);
        String s_ex = String.valueOf(ex);
        String s_ey = String.valueOf(ey);

        final JSONArray[] pathArray = new JSONArray[1];

        oDsayService.requestSearchPubTransPath(s_sx, s_sy, s_ex, s_ey, "0", "0", "0", new OnResultCallbackListener() {
            @Override
            public void onSuccess(ODsayData oDsayData, API api) {
                try {
                    JSONObject resjson = oDsayData.getJson().getJSONObject("result");
                    Log.d("JSON", resjson.toString());

                    pathArray[0] = resjson.getJSONArray("path");

                    for (int i = 0; i < pathArray[0].length(); i++) {
                        int time = pathArray[0].getJSONObject(i).getJSONObject("info").getInt("totalTime");
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
        });

        return pathArray[0];
    }
}
