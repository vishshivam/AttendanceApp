package com.example.attendo;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {

    private List<Student> students;
    private DatabaseHelper dbHelper;
    private Context context;

    public StudentAdapter(List<Student> students, Context context, DatabaseHelper dbHelper) {
        this.students = students;
        this.context = context;
        this.dbHelper = dbHelper;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
        notifyDataSetChanged();
    }

    public List<Student> getStudents() {
        return students;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        Student student = students.get(position);
        String studentName = student.getName();
        if (!TextUtils.isEmpty(studentName)) { // Check if the name is not empty
            studentName = studentName.toUpperCase();
        }
        holder.textViewStudentName.setText(studentName);
        holder.textViewStudentId.setText("R188237200"+String.valueOf(student.getId()));


        // Set the serial number
        holder.textViewStudentNumber.setText(String.valueOf(position + 1));

        holder.checkBoxAttendance.setOnCheckedChangeListener(null);
        holder.checkBoxAttendance.setChecked(student.isPresent());
        holder.checkBoxAttendance.setOnCheckedChangeListener((buttonView, isChecked) -> {
            student.setPresent(isChecked);
        });
    }

    @Override
    public int getItemCount() {
        return students.size();
    }
    public void markAllPresent(boolean present) {
        for (Student student : students) {
            student.setPresent(present);
        }
        notifyDataSetChanged();
    }

    static class StudentViewHolder extends RecyclerView.ViewHolder {

        TextView textViewStudentName, textViewStudentId, textViewStudentNumber;
        CheckBox checkBoxAttendance;

        StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewStudentNumber = itemView.findViewById(R.id.textViewStudentNumber);
            textViewStudentName = itemView.findViewById(R.id.textViewStudentName);
            textViewStudentId = itemView.findViewById(R.id.textViewStudentId);
            checkBoxAttendance = itemView.findViewById(R.id.checkBoxAttendance);
        }
    }
}