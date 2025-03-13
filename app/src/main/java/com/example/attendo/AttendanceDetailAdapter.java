package com.example.attendo;// AttendanceDetailAdapter.java

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AttendanceDetailAdapter extends RecyclerView.Adapter<AttendanceDetailAdapter.AttendanceDetailViewHolder> {

    private Context context;
    private List<AttendanceDetail> attendanceDetails;

    public AttendanceDetailAdapter(Context context, List<AttendanceDetail> attendanceDetails) {
        this.context = context;
        this.attendanceDetails = attendanceDetails;
    }

    @NonNull
    @Override
    public AttendanceDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.attendance_detail_item, parent, false);
        return new AttendanceDetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttendanceDetailViewHolder holder, int position) {
        AttendanceDetail attendanceDetail = attendanceDetails.get(position);

        holder.textViewDate.setText(attendanceDetail.getDate());
        holder.textViewStatus.setText(attendanceDetail.getStatus());
        GradientDrawable statusBackground = (GradientDrawable) holder.textViewStatus.getBackground();

        if (attendanceDetail.getStatus().equals("P")) {
            statusBackground.setColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.green_darker));
            holder.textViewStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.white));
        } else if (attendanceDetail.getStatus().equals("A")) {
            statusBackground.setColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.holo_red_light));
            holder.textViewStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.white));

        }
    }

    @Override
    public int getItemCount() {
        return attendanceDetails.size();
    }

    public static class AttendanceDetailViewHolder extends RecyclerView.ViewHolder {
        TextView textViewDate, textViewStatus;

        public AttendanceDetailViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewStatus = itemView.findViewById(R.id.textViewStatus);
        }
    }
}