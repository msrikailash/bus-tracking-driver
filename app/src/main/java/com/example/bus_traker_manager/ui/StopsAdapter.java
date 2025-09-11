package com.example.bus_traker_manager.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bus_traker_manager.R;
import com.example.bus_traker_manager.model.Stop;

import java.util.List;

public class StopsAdapter extends RecyclerView.Adapter<StopsAdapter.StopViewHolder> {
    private final List<Stop> stops;
    private final OnStopClickListener listener;

    public interface OnStopClickListener {
        void onStopClick(Stop stop);
        void onEditStop(Stop stop);
        void onDeleteStop(Stop stop);
    }

    public StopsAdapter(List<Stop> stops, OnStopClickListener listener) {
        this.stops = stops;
        this.listener = listener;
    }

    @NonNull
    @Override
    public StopViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_stop, parent, false);
        return new StopViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StopViewHolder holder, int position) {
        Stop stop = stops.get(position);
        holder.bind(stop);
    }

    @Override
    public int getItemCount() {
        return stops.size();
    }

    class StopViewHolder extends RecyclerView.ViewHolder {
        private final TextView stopNameText;
        private final TextView stopCodeText;
        private final TextView descriptionText;
        private final TextView landmarkText;
        private final TextView locationText;
        private final TextView statusText;
        private final TextView scheduleText;
        private final ImageButton editButton;
        private final ImageButton deleteButton;

        public StopViewHolder(@NonNull View itemView) {
            super(itemView);
            stopNameText = itemView.findViewById(R.id.stopNameText);
            stopCodeText = itemView.findViewById(R.id.stopCodeText);
            descriptionText = itemView.findViewById(R.id.descriptionText);
            landmarkText = itemView.findViewById(R.id.landmarkText);
            locationText = itemView.findViewById(R.id.locationText);
            statusText = itemView.findViewById(R.id.statusText);
            scheduleText = itemView.findViewById(R.id.scheduleText);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }

        public void bind(Stop stop) {
            // Basic Information
            stopNameText.setText(stop.getName());
            
            // Stop Code
            if (stop.getStopCode() != null && !stop.getStopCode().isEmpty()) {
                stopCodeText.setText(stop.getStopCode());
                stopCodeText.setVisibility(View.VISIBLE);
            } else {
                stopCodeText.setVisibility(View.GONE);
            }

            // Description
            if (stop.getDescription() != null && !stop.getDescription().isEmpty()) {
                descriptionText.setText(stop.getDescription());
                descriptionText.setVisibility(View.VISIBLE);
            } else {
                descriptionText.setVisibility(View.GONE);
            }

            // Landmark
            if (stop.getLandmark() != null && !stop.getLandmark().isEmpty()) {
                landmarkText.setText("📍 " + stop.getLandmark());
                landmarkText.setVisibility(View.VISIBLE);
            } else {
                landmarkText.setVisibility(View.GONE);
            }

            // Location
            if (stop.getLocation() != null) {
                String locationStr = String.format("📍 %.6f, %.6f", 
                    stop.getLocation().getLatitude(), 
                    stop.getLocation().getLongitude());
                locationText.setText(locationStr);
                
                if (stop.getLocation().getAddress() != null && !stop.getLocation().getAddress().isEmpty()) {
                    locationText.setText(stop.getLocation().getAddress());
                }
            } else {
                locationText.setText("📍 Location not set");
            }

            // Status
            if (stop.getStatus() != null) {
                String status = stop.getStatus().getCurrentStatus();
                statusText.setText("Status: " + status.toUpperCase());
                
                // Set status color
                int statusColor;
                switch (status) {
                    case "reached":
                        statusColor = itemView.getContext().getColor(R.color.accent_green);
                        break;
                    case "pending":
                        statusColor = itemView.getContext().getColor(R.color.accent_yellow);
                        break;
                    case "skipped":
                        statusColor = itemView.getContext().getColor(R.color.accent_red);
                        break;
                    case "maintenance":
                        statusColor = itemView.getContext().getColor(R.color.accent_orange);
                        break;
                    default:
                        statusColor = itemView.getContext().getColor(R.color.accent_yellow);
                        break;
                }
                statusText.setTextColor(statusColor);
            } else {
                statusText.setText("Status: PENDING");
            }

            // Schedule
            if (stop.getSchedule() != null && stop.getSchedule().getMorningPickup() != null) {
                String schedule = String.format("🕐 %s - %s", 
                    stop.getSchedule().getMorningPickup(), 
                    stop.getSchedule().getMorningDrop());
                scheduleText.setText(schedule);
                scheduleText.setVisibility(View.VISIBLE);
            } else {
                scheduleText.setVisibility(View.GONE);
            }

            // Click listeners
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onStopClick(stop);
                }
            });

            editButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditStop(stop);
                }
            });

            deleteButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteStop(stop);
                }
            });
        }
    }
}

