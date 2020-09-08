package com.teamone.plafic;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.util.Log;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class AddPlanActivity extends AppCompatActivity {

    int s_year;
    int s_month;
    int s_day;

    int s_hour;
    int s_min;

    double x;
    double y;

    EditText txtTitle;
    EditText txtLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_plan);

        txtTitle = findViewById(R.id.txtTitle);
        txtLocation = findViewById(R.id.txtLocation);

        Intent intent = getIntent();
        String datestr = intent.getExtras().getString("datestr");
        int t_year = Integer.parseInt(datestr.split("-")[0]);
        int t_month = Integer.parseInt(datestr.split("-")[1]);
        int t_day = Integer.parseInt(datestr.split("-")[2]);

        s_year = t_year;
        s_month = t_month;
        s_day = t_day;

        DatePicker datePicker = findViewById(R.id.datepicker);
        datePicker.init(t_year, t_month, t_day, (dp, y, m, d) -> {
            s_year = y;
            s_month = m;
            s_day = d;
        });

        Calendar calendar = Calendar.getInstance();
        int n_hour = calendar.get(Calendar.HOUR_OF_DAY);
        int n_minute = calendar.get(Calendar.MINUTE);

        s_hour = n_hour;
        s_min = n_minute;

        TimePicker timePicker = findViewById(R.id.timepicker);
        timePicker.setIs24HourView(true);
        timePicker.setHour(n_hour);
        timePicker.setMinute(n_minute);
        timePicker.setOnTimeChangedListener((tp, h, m) -> {
            s_hour = h;
            s_min = m;
        });

        EditText txtLocation = findViewById(R.id.txtLocation);
        txtLocation.setOnClickListener(view -> {
            Intent activityMap = new Intent(getApplicationContext(), MapActivity.class);
            startActivityForResult(activityMap, 1);
        });

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(view -> {
            finish();
        });

        Button btnAdd = findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(view -> {
            DBManager dbManager = new DBManager(getApplicationContext());
            SQLiteDatabase db = dbManager.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(DBEntry.TITLE, txtTitle.getText().toString());
            s_month++;
            String strdate = String.valueOf(s_year) + "-" + String.valueOf(s_month) + "-" +
                    String.valueOf(s_day);
            values.put(DBEntry.DATE, strdate);
            String strtime = String.valueOf(s_hour) + ":" + String.valueOf(s_min);
            values.put(DBEntry.TIME, strtime);
            values.put(DBEntry.LOCATION, txtLocation.getText().toString());
            values.put(DBEntry.GEO_X, String.valueOf(x));
            values.put(DBEntry.GEO_Y, String.valueOf(y));

            db.insert(DBEntry.TABLE_NAME, null, values);

            finish();
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        txtLocation.setText(data.getStringExtra("location"));
        x = data.getDoubleExtra("x", 127.00);
        y = data.getDoubleExtra("y", 37.56);
    }

}


