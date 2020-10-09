package com.senier_project.planner.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.senier_project.planner.R;
import com.senier_project.planner.category_search.Document;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.util.ArrayList;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.LocationViewHolder> {
    Context context;
    ArrayList<Document> items;
    EditText editText;
    RecyclerView recyclerView;
    MapView mMapView;

    public double s_x;
    public double s_y;

    public LocationAdapter(ArrayList<Document> items, Context context, EditText editText, RecyclerView recyclerView, MapView mapView) {
        this.context = context;
        this.items = items;
        this.editText = editText;
        this.recyclerView = recyclerView;
        mMapView = mapView;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public void addItem(Document item) {
        items.add(item);
    }


    public void clear() {
        items.clear();
    }

    @Override
    public long getItemId(int position) {
        return Long.parseLong(items.get(position).getId());
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_location, viewGroup, false);
        return new LocationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationViewHolder holder, int i) {
        final Document model = items.get(i);

        holder.placeNameText.setText(model.getPlaceName());
        holder.addressText.setText(model.getAddressName());

        holder.placeNameText.setOnClickListener(view -> {
            editText.setText(model.getPlaceName());
            recyclerView.setVisibility(View.GONE);

            s_x = Double.parseDouble(model.getX());
            s_y = Double.parseDouble(model.getY()); //검색 위치의 좌표를 double형 변수 x, y에 저장
            MapPoint markerPoint = MapPoint.mapPointWithGeoCoord(s_y, s_x); //검색 위치의 좌표 정보를 포함하는 MapPoint 객체 생성

            mMapView.setMapCenterPoint(markerPoint, true); //검색 위치를 지도의 중심으로 설정

            MapPOIItem marker = new MapPOIItem(); //새로운 마커 객체 생성
            marker.setItemName(model.getPlaceName()); //마커 이름 설정
            marker.setTag(0); //마커 태그 설정 (Kakao API Documentation 참조)
            marker.setMapPoint(markerPoint); //마커 위치 설정
            marker.setMarkerType(MapPOIItem.MarkerType.RedPin); //마커 모양 설정

            mMapView.addPOIItem(marker); //마커를 지도 위에 표시
        });
    }

    public class LocationViewHolder extends RecyclerView.ViewHolder {

        TextView placeNameText;
        TextView addressText;


        public LocationViewHolder(@NonNull final View itemView) {
            super(itemView);


            placeNameText = itemView.findViewById(R.id.ltem_location_tv_placename);
            addressText = itemView.findViewById(R.id.ltem_location_tv_address);
        }
    }
}
