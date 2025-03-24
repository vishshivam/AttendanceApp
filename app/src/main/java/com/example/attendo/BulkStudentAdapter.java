// BulkStudentAdapter.java
package com.example.attendo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class BulkStudentAdapter extends RecyclerView.Adapter<BulkStudentAdapter.BulkStudentViewHolder> {
    private List<Student> students;

    public BulkStudentAdapter(List<Student> students) {
        this.students = students;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void markAllPresent() {
        for (Student student : students) {
            student.setPresent(true);
        }
        notifyDataSetChanged();
    }

    public void markAllAbsent() {
        for (Student student : students) {
            student.setPresent(false);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BulkStudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student, parent, false);
        return new BulkStudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BulkStudentViewHolder holder, int position) {
        Student student = students.get(position);
        holder.studentName.setText(student.getName());
        holder.studentCheckBox.setChecked(student.isPresent());
        holder.studentCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> student.setPresent(isChecked));
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    public static class BulkStudentViewHolder extends RecyclerView.ViewHolder {
        TextView studentName;
        CheckBox studentCheckBox;

        public BulkStudentViewHolder(@NonNull View itemView) {
            super(itemView);
            studentName = itemView.findViewById(R.id.textViewStudentName);
            studentCheckBox = itemView.findViewById(R.id.checkBoxAttendance);
        }
    }
}