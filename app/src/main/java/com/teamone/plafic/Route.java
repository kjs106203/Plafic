package com.teamone.plafic;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class Route extends AppCompatActivity {


    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.route_search);

        listView = (ListView)findViewById(R.id.listView);

        List<String> list = new ArrayList<>();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, list);

        listView.setAdapter(adapter);

        RouteAdapter adapter = new RouteAdapter();
//        for (경로 추가
//        adapter.add
//        )

    }

    class RouteAdapter extends BaseAdapter {

    }


}
