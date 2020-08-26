package com.teamone.plafic;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.util.Base64;
import android.util.Log;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.ListAdapter;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Vector;

public class MainActivity extends AppCompatActivity {
    ListView listPlan;
    Vector<String> vectorPlan;
    ArrayAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("ACTIVITY", "MainActivity has created.");
        setContentView(R.layout.activity_main);
        getHashKey();
        CalendarView calendarView = (CalendarView)findViewById(R.id.CalendarView);
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Intent intent = new Intent(getApplicationContext(), AddPlanActivity.class);
            month++;
            String str = year + "-" + month + "-" + dayOfMonth;
            intent.putExtra("datestr", str);

            startActivity(intent);
        });

        listPlan = findViewById(R.id.listPlan);
        vectorPlan = new Vector<>();

        ImageButton btnAddPlan = findViewById(R.id.btnAddPlan);
        btnAddPlan.setOnClickListener(view -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

            Intent intent = new Intent(getApplicationContext(), AddPlanActivity.class);
            String str = year + "-" + month + "-" + dayOfMonth;
            intent.putExtra("datestr", str);

            startActivity(intent);
        });

        ODsayBackend oDsayBackend = new ODsayBackend(getApplicationContext());

        createNotiChannel();
        showNoti();
    }

    public void onResume() {
        super.onResume();

        refreshPlanList();
    }

    private void refreshPlanList() {
        vectorPlan.clear();

        DBManager dbManager = new DBManager(getApplicationContext());

        SQLiteDatabase db = dbManager.getReadableDatabase();
        String[] projection = {
                BaseColumns._ID,
                DBEntry.TITLE,
                DBEntry.DATE,
                DBEntry.TIME,
                DBEntry.LOCATION
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
            String loc = cursor.getString(cursor.getColumnIndexOrThrow(DBEntry.LOCATION));
            vectorPlan.add(ttl + " " + dat + " " + tim + " " + loc);
        }

        listAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, vectorPlan);
        listPlan.setAdapter(listAdapter);

        cursor.close();
    }


    private void getHashKey() {
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

    private void createNotiChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Plafic";
            String description = "Plafic";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("com.teamone.plafic", name, importance);
            channel.setDescription(description);

            NotificationManager notiManager = getSystemService(NotificationManager.class);
            notiManager.createNotificationChannel(channel);
        }
    }

    private void showNoti() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder notiBuilder = new NotificationCompat.Builder(this, "com.teamone.plafic")
                .setSmallIcon(R.drawable.addicon)
                .setContentTitle("Plafic")
                .setContentText("다음 약속까지 N시간")
                .setStyle(new NotificationCompat.BigTextStyle().bigText("다음 약속까지 N시간"))
                .setOngoing(true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notiManager = NotificationManagerCompat.from(this);
        notiManager.notify(2723, notiBuilder.build());
    }
}

