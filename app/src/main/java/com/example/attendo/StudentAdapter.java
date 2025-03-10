package com.example.attendo;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
        holder.textViewStudentName.setText(student.getName());
        holder.textViewStudentId.setText(String.valueOf(student.getId()));
        holder.checkBoxAttendance.setChecked(student.isPresent());

        holder.checkBoxAttendance.setOnCheckedChangeListener((buttonView, isChecked) -> {
            student.setPresent(isChecked);
        });

        holder.itemView.setOnLongClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Are you sure you want to delete " + student.getName() + "?");
            builder.setPositiveButton("Yes", (dialog, which) -> {
                dbHelper.removeStudent(student.getId());
                students.remove(position);
                notifyItemRemoved(position);
                Toast.makeText(context, "Student deleted.", Toast.LENGTH_SHORT).show();
            });
            builder.setNegativeButton("No", null);
            builder.show();
            return true;
        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddStudentActivity.class);
            intent.putExtra("studentId", student.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    static class StudentViewHolder extends RecyclerView.ViewHolder {

        TextView textViewStudentName, textViewStudentId;
        CheckBox checkBoxAttendance;

        StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewStudentName = itemView.findViewById(R.id.textViewStudentName);
            textViewStudentId = itemView.findViewById(R.id.textViewStudentId);
            checkBoxAttendance = itemView.findViewById(R.id.checkBoxAttendance);
        }
    }
}