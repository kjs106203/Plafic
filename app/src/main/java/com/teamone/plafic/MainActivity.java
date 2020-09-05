package com.teamone.plafic;

import android.annotation.SuppressLint;
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
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.security.MessageDigest;
import java.sql.Ref;
import java.util.Calendar;
import java.util.Vector;

public class MainActivity extends AppCompatActivity {
    ListView listPlan;
    Vector<String> vectorPlan;
    ArrayAdapter<String> listAdapter;

    NotificationCompat.Builder notiBuilder;
    NotificationManagerCompat notiManager;

    int s_year = 0;
    int s_month = 0;
    int s_day = 0;

    public double longitude;
    public double latitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("ACTIVITY", "MainActivity has created.");
        setContentView(R.layout.activity_main);
        getHashKey();

        Calendar calendar = Calendar.getInstance();
        s_year = calendar.get(Calendar.YEAR);
        s_month = calendar.get(Calendar.MONTH);
        s_day = calendar.get(Calendar.DAY_OF_MONTH);

        CalendarView calendarView = findViewById(R.id.CalendarView);
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {

            s_year = year;
            s_month = month;
            s_day = dayOfMonth;

            refreshPlanList(constructDate(s_year, s_month, s_day));
        });

        listPlan = findViewById(R.id.listPlan);
        vectorPlan = new Vector<>();

        ImageButton btnAddPlan = findViewById(R.id.btnAddPlan);
        btnAddPlan.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), AddPlanActivity.class);
            String str = s_year + "-" + s_month + "-" + s_day;
            intent.putExtra("datestr", str);

            startActivity(intent);
        });

        createNotiChannel();

        notiManager = NotificationManagerCompat.from(this);


        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        notiBuilder = new NotificationCompat.Builder(this, "com.teamone.plafic")
                .setSmallIcon(R.drawable.addicon)
                .setContentTitle("Plafic")
                .setContentText("다음 약속까지 N시간")
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        RefreshThread refreshThread = new RefreshThread(this);
        refreshThread.start();

        refreshPlanList(constructDate(s_year, s_month, s_day));

        ODsayBackend oDsayBackend = new ODsayBackend(getApplicationContext());
        oDsayBackend.requestRoute(longitude, latitude, 127.087869, 36.990480);
    }

    public void onResume() {
        super.onResume();

        refreshPlanList(constructDate(s_year, s_month, s_day));
    }

    private void refreshPlanList(String date) {
        vectorPlan.clear();

        DBManager dbManager = new DBManager(getApplicationContext());

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
        String whereClause = "date = ?";
        String[] whereVal = {date};

        Cursor cursor = db.query(
                DBEntry.TABLE_NAME,
                projection,
                whereClause,
                whereVal,
                null,
                null,
                null
        );

        while (cursor.moveToNext()) {
            String ttl = cursor.getString(cursor.getColumnIndexOrThrow(DBEntry.TITLE));
            String dat = cursor.getString(cursor.getColumnIndexOrThrow(DBEntry.DATE));
            String tim = cursor.getString(cursor.getColumnIndexOrThrow(DBEntry.TIME));
            String loc = cursor.getString(cursor.getColumnIndexOrThrow(DBEntry.LOCATION));
            String x = cursor.getString(cursor.getColumnIndexOrThrow(DBEntry.GEO_X));
            String y = cursor.getString(cursor.getColumnIndexOrThrow(DBEntry.GEO_Y));
            vectorPlan.add(ttl + " " + dat + " " + tim + " " + loc + " " + x + " " + y);
        }

        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, vectorPlan);
        listPlan.setAdapter(listAdapter);

        cursor.close();
    }


    private void getHashKey() {
        try{
            @SuppressLint("PackageManagerGetSignatures")
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

    private String constructDate(int year, int month, int day) {
        StringBuilder builder = new StringBuilder();
        builder.append(year);
        builder.append("-");
        builder.append(month);
        builder.append("-");
        builder.append(day);

        return builder.toString();
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

    public void showNoti(String content, NotificationCompat.Builder builder, NotificationManagerCompat mgr) {
        builder.setContentText(content);

        mgr.notify(2723, builder.build());
    }
}

