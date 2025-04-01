// StudentAdapter.java
package com.example.attendo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.List;

public class ManageStudentAdapter extends RecyclerView.Adapter<ManageStudentAdapter.ManageStudentViewHolder> {

    private Context context;
    private List<ManageStudent> students;
    private DatabaseHelper dbHelper;

    public ManageStudentAdapter(Context context, List<ManageStudent> students, DatabaseHelper dbHelper) {
        this.context = context;
        this.students = students;
        this.dbHelper = dbHelper;
    }

    @NonNull
    @Override
    public ManageStudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.manage_item, parent, false);
        return new ManageStudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ManageStudentViewHolder holder, int position) {
        ManageStudent student = students.get(position);
        String studentName = student.getName();
        if (!TextUtils.isEmpty(studentName)) { // Check if the name is not empty
            studentName = studentName.toUpperCase();
        }
        holder.textViewManageNumber.setText(String.valueOf(position + 1));
        holder.textViewStudentName.setText(studentName);
        holder.textViewStudentBranch.setText("Branch : "+student.getBranch());
        holder.textViewStudentSemester.setText("Semester : "+(student.getSemester()));
        holder.textViewStudentRollNo.setText("Roll No : "+(student.getRollno()));

        holder.buttonUpdate.setOnClickListener(v -> updateStudent(student));
        holder.buttonDelete.setOnClickListener(v -> deleteStudent(student));


    }
    private void updateStudent(ManageStudent student) {
        Intent intent = new Intent(context, AddStudentActivity.class);
        intent.putExtra("studentId", student.getId());
        context.startActivity(intent);
    }

    private void deleteStudent(ManageStudent student) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomAlertDialogTheme);
        builder.setTitle("Confirm Delete");
        builder.setMessage("Are you sure you want to delete " + student.getName() + "?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            dbHelper.removeStudent(student.getId());
            students.remove(student);
            notifyDataSetChanged();
            Toast.makeText(context, "Student deleted", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("No", null);
        AlertDialog dialog = builder.create();
        builder.show();
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    static class ManageStudentViewHolder extends RecyclerView.ViewHolder {
        TextView textViewManageNumber, textViewStudentName, textViewStudentBranch, textViewStudentSemester,textViewStudentRollNo;
        MaterialButton buttonUpdate, buttonDelete;

        ManageStudentViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewManageNumber = itemView.findViewById(R.id.textViewManageNumber);
            textViewStudentName = itemView.findViewById(R.id.textViewStudentName);
            textViewStudentBranch = itemView.findViewById(R.id.textViewStudentBranch);
            textViewStudentSemester = itemView.findViewById(R.id.textViewStudentSemester);
            textViewStudentRollNo = itemView.findViewById(R.id.textViewStudentRollNo);
            buttonUpdate = itemView.findViewById(R.id.buttonUpdate);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
        }
    }
}