package com.teamone.plafic;

import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class RouteAdapter extends BaseAdapter {

    private TextView titleTextView;
    private TextView contentTextView;

    private ArrayList<ODsayBackend> listViewItemList = new ArrayList<ODsayBackend>();
}
