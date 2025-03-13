package com.example.attendo;// ReportAdapter.java

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import java.util.List;
import java.util.Locale;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {

    private Context context;
    private List<ReportItem> reportItems;

    public ReportAdapter(Context context, List<ReportItem> reportItems) {
        this.context = context;
        this.reportItems = reportItems;
    }
    // New method to update the data
    public void setReportItems(List<ReportItem> reportItems) {
        this.reportItems = reportItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_report, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        ReportItem reportItem = reportItems.get(position);
        String studentName = reportItem.getName();
        if (!TextUtils.isEmpty(studentName)) { // Check if the name is not empty
            studentName = studentName.toUpperCase();
        }
        holder.textViewReportStudentName.setText(studentName);
        holder.textViewReportPercentage.setText(String.format(Locale.getDefault(), "%.0f%%", reportItem.getPercentage()));
        holder.textViewReportNumber.setText(String.valueOf(position + 1));
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

        holder.buttonDetail.setOnClickListener(v -> {
            int studentId = (int) reportItem.getStudentId();

            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("studentId", studentId);

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return reportItems.size();
    }

    public static class ReportViewHolder extends RecyclerView.ViewHolder {
        TextView textViewReportNumber, textViewReportStudentName, textViewReportStatus, textViewReportPercentage;
        MaterialButton buttonDetail;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewReportNumber = itemView.findViewById(R.id.textViewReportNumber);
            textViewReportStudentName = itemView.findViewById(R.id.textViewReportStudentName);
            textViewReportPercentage = itemView.findViewById(R.id.textViewReportPercentage);
            buttonDetail = itemView.findViewById(R.id.buttonDetail);
        }
    }
}