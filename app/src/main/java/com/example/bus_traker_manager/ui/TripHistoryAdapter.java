package com.example.bus_traker_manager.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bus_traker_manager.R;
import com.example.bus_traker_manager.model.Trip;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TripHistoryAdapter extends RecyclerView.Adapter<TripHistoryAdapter.TripViewHolder> {
    
    private final List<Trip> trips;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    
    public TripHistoryAdapter(List<Trip> trips) {
        this.trips = trips;
    }
    
    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_trip, parent, false);
        return new TripViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {
        Trip trip = trips.get(position);
        holder.bind(trip);
    }
    
    @Override
    public int getItemCount() {
        return trips.size();
    }
    
    class TripViewHolder extends RecyclerView.ViewHolder {
        private final TextView tripIdText;
        private final TextView dateText;
        private final TextView timeText;
        private final TextView statusText;
        private final TextView durationText;
        private final TextView distanceText;
        private final TextView stopsText;
        
        public TripViewHolder(@NonNull View itemView) {
            super(itemView);
            tripIdText = itemView.findViewById(R.id.tripIdText);
            dateText = itemView.findViewById(R.id.dateText);
            timeText = itemView.findViewById(R.id.timeText);
            statusText = itemView.findViewById(R.id.statusText);
            durationText = itemView.findViewById(R.id.durationText);
            distanceText = itemView.findViewById(R.id.distanceText);
            stopsText = itemView.findViewById(R.id.stopsText);
        }
        
        public void bind(Trip trip) {
            tripIdText.setText(trip.getTripId());
            
            if (trip.getActualStartTime() > 0) {
                Date startDate = new Date(trip.getActualStartTime());
                dateText.setText(dateFormat.format(startDate));
                timeText.setText(timeFormat.format(startDate));
            } else {
                dateText.setText("N/A");
                timeText.setText("N/A");
            }
            
            statusText.setText(trip.getStatus());
            durationText.setText(trip.getFormattedDuration());
            
            // Distance calculation would need to be implemented based on route data
            distanceText.setText("N/A");
            
            // Get total stops from stopsProgress
            if (trip.getStopsProgress() != null && !trip.getStopsProgress().isEmpty()) {
                stopsText.setText(String.format("%d stops", trip.getStopsProgress().size()));
            } else {
                stopsText.setText("N/A");
            }
            
            // Set status color
            int statusColor;
            switch (trip.getStatus()) {
                case "completed":
                    statusColor = itemView.getContext().getColor(R.color.accent_green);
                    break;
                case "active":
                    statusColor = itemView.getContext().getColor(R.color.accent_orange);
                    break;
                case "cancelled":
                    statusColor = itemView.getContext().getColor(R.color.accent_red);
                    break;
                default:
                    statusColor = itemView.getContext().getColor(R.color.accent_yellow);
                    break;
            }
            statusText.setTextColor(statusColor);
        }
    }
}

