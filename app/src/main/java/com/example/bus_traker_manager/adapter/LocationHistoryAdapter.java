package com.example.bus_traker_manager.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bus_traker_manager.R;
import com.example.bus_traker_manager.model.LocationPoint;
import com.example.bus_traker_manager.util.LocationUtils;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class LocationHistoryAdapter extends RecyclerView.Adapter<LocationHistoryAdapter.LocationHistoryViewHolder> {
    private List<LocationPoint> locationHistory;
    private SimpleDateFormat dateFormat;
    
    public LocationHistoryAdapter(List<LocationPoint> locationHistory, SimpleDateFormat dateFormat) {
        this.locationHistory = locationHistory;
        this.dateFormat = dateFormat;
    }
    
    @NonNull
    @Override
    public LocationHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_location_history, parent, false);
        return new LocationHistoryViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull LocationHistoryViewHolder holder, int position) {
        LocationPoint locationPoint = locationHistory.get(position);
        holder.bind(locationPoint);
    }
    
    @Override
    public int getItemCount() {
        return locationHistory.size();
    }
    
    class LocationHistoryViewHolder extends RecyclerView.ViewHolder {
        private TextView textTimestamp;
        private TextView textLocation;
        private TextView textAccuracy;
        private TextView textSpeed;
        private TextView textBearing;
        
        public LocationHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            textTimestamp = itemView.findViewById(R.id.text_timestamp);
            textLocation = itemView.findViewById(R.id.text_location);
            textAccuracy = itemView.findViewById(R.id.text_accuracy);
            textSpeed = itemView.findViewById(R.id.text_speed);
            textBearing = itemView.findViewById(R.id.text_bearing);
        }
        
        public void bind(LocationPoint locationPoint) {
            textTimestamp.setText(dateFormat.format(new java.util.Date(locationPoint.getTimestamp())));
            textLocation.setText(String.format(Locale.getDefault(), 
                    "%.6f, %.6f", 
                    locationPoint.getLatitude(), 
                    locationPoint.getLongitude()));
            textAccuracy.setText(String.format(Locale.getDefault(), "±%.1f m", locationPoint.getAccuracy()));
            textSpeed.setText(LocationUtils.formatSpeed(locationPoint.getSpeed()));
            textBearing.setText(LocationUtils.formatBearing(locationPoint.getBearing()));
        }
    }
}
