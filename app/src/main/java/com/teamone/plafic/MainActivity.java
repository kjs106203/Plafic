package com.teamone.plafic;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.CalendarView;

import androidx.appcompat.app.AppCompatActivity;

import java.security.MessageDigest;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getHashKey();
        CalendarView calendarView = (CalendarView)findViewById(R.id.CalendarView);
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Intent intent = new Intent(getApplicationContext(), AddPlanActivity.class);
            String str = year + "-" + month + "-" + dayOfMonth;
            intent.putExtra("datestr", str);

            startActivity(intent);
        });

        Button btnAddPlan = findViewById(R.id.btnAddPlan);
        btnAddPlan.setOnClickListener(view -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

            Intent intent = new Intent(getApplicationContext(), AddPlanActivity.class);
            String str = year + "-" + month + "-" + dayOfMonth;
            intent.putExtra("datestr", str);

            startActivity(intent);
        });

        ODsayBackend oDsayBackend = new ODsayBackend(getApplicationContext());

    }


    private void getHashKey(){
        try{
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String key = new String(Base64.encode(md.digest(), 0));
                Log.d("Hash key :",key);
            }
        } catch (Exception e){
            Log.e("name not found", e.toString());
        }
    }

}

