package com.example.attendo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
        holder.textViewReportStudentName.setText(reportItem.getName());
        holder.textViewReportPercentage.setText(String.format(Locale.getDefault(), "Attendance: %.2f%%", reportItem.getPercentage()));
    }

    @Override
    public int getItemCount() {
        return reportItems.size();
    }

    static class ReportViewHolder extends RecyclerView.ViewHolder {

        TextView textViewReportStudentName, textViewReportPercentage;

        ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewReportStudentName = itemView.findViewById(R.id.textViewReportStudentName);
            textViewReportPercentage = itemView.findViewById(R.id.textViewReportPercentage);
        }
    }
}