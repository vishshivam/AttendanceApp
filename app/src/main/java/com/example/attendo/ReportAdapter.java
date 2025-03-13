// ReportAdapter.java
package com.example.attendo;

import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import java.util.List;
import java.util.Locale;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {

    private List<ReportItem> reportItems;

    public ReportAdapter(List<ReportItem> reportItems) {
        this.reportItems = reportItems;
    }

    public void setReportItems(List<ReportItem> reportItems) {
        this.reportItems = reportItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_report, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        ReportItem reportItem = reportItems.get(position);
        Log.d("ReportAdapter", "onBindViewHolder: Item = " + reportItem.getName());
        holder.textViewReportStudentName.setText(reportItem.getName());
        holder.textViewReportPercentage.setText(String.format(Locale.getDefault(), "%.0f%%", reportItem.getPercentage()));
        holder.textViewReportNumber.setText(String.valueOf(position + 1));
        holder.textViewReportStatus.setText(reportItem.getStatus());

        String status = reportItem.getStatus();
        if (status.equals("Present")) {
            holder.textViewReportStatus.setText("P");
            holder.textViewReportStatus.setBackgroundResource(R.drawable.status_background);
        } else {
            holder.textViewReportStatus.setText("A");
            holder.textViewReportStatus.setBackgroundResource(R.drawable.status_background_absent);
        }

        GradientDrawable percentageBackground = (GradientDrawable) holder.textViewReportPercentage.getBackground();
        float percentage = (float) reportItem.getPercentage();

        if (percentage >= 80) {
            percentageBackground.setColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.green_darker));
            holder.textViewReportPercentage.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.white));
        } else if (percentage >= 60) {
            percentageBackground.setColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.report_yellow));
            holder.textViewReportPercentage.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.black));
        } else {
            percentageBackground.setColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.holo_red_light));
            holder.textViewReportPercentage.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.white));
        }
    }

    @Override
    public int getItemCount() {
        return reportItems.size();
    }

    static class ReportViewHolder extends RecyclerView.ViewHolder {

        TextView textViewReportStudentName, textViewReportPercentage, textViewReportNumber, textViewReportStatus;

        ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewReportNumber = itemView.findViewById(R.id.textViewReportNumber);
            textViewReportStudentName = itemView.findViewById(R.id.textViewReportStudentName);
            textViewReportPercentage = itemView.findViewById(R.id.textViewReportPercentage);
            textViewReportStatus = itemView.findViewById(R.id.textViewReportStatus);
        }
    }
}