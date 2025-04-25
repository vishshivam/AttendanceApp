package com.example.attendo;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.attendo.AttendanceViewModel;

import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {

    private List<Student> students;
    private AttendanceViewModel viewModel; // Receive the ViewModel
    private Context context;

    public StudentAdapter(List<Student> students, Context context, AttendanceViewModel viewModel) {
        this.students = students;
        this.context = context;
        this.viewModel = viewModel;
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
        if (!TextUtils.isEmpty(studentName)) {
            studentName = studentName.toUpperCase();
        }
        holder.textViewStudentName.setText(studentName);
        holder.textViewStudentId.setText("R188237200" + String.valueOf(student.getId()));
        holder.textViewStudentNumber.setText(String.valueOf(position + 1));

        holder.checkBoxAttendance.setOnCheckedChangeListener(null);
        holder.checkBoxAttendance.setChecked(student.isPresent());
        holder.checkBoxAttendance.setOnCheckedChangeListener((buttonView, isChecked) -> {
            viewModel.updateStudentAttendance(position, isChecked); // Update via ViewModel
        });
    }

    @Override
    public int getItemCount() {
        return students.size();
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