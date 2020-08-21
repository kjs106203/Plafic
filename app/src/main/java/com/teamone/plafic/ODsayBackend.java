package com.teamone.plafic;

import android.content.Context;
import android.util.Log;

import com.odsay.odsayandroidsdk.API;
import com.odsay.odsayandroidsdk.ODsayData;
import com.odsay.odsayandroidsdk.ODsayService;
import com.odsay.odsayandroidsdk.OnResultCallbackListener;

public class ODsayBackend {
    String apiKey = "inV61SM+ZAXovckEz4mF1RQKg5j7T9bAJvvpxOvvvI4";

    public ODsayBackend(Context context) {
        ODsayService oDsayService = ODsayService.init(context, apiKey);

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
}
