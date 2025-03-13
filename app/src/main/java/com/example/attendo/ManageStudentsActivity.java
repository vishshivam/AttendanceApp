// ManageStudentsActivity.java
package com.example.attendo;

import android.database.Cursor;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ManageStudentsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewManageStudents;
    private ManageStudentAdapter manageStudentAdapter;
    private List<ManageStudent> students;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_students);

        recyclerViewManageStudents = findViewById(R.id.recyclerViewManageStudents);
        dbHelper = new DatabaseHelper(this);
        students = new ArrayList<>();

        recyclerViewManageStudents.setLayoutManager(new LinearLayoutManager(this));
        manageStudentAdapter = new ManageStudentAdapter(this, students, dbHelper);
        recyclerViewManageStudents.setAdapter(manageStudentAdapter);

        loadStudents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadStudents();
    }

    private void loadStudents() {
        students.clear();
        Cursor cursor = dbHelper.getStudents(null, 0); // Get all students
        if (cursor != null && cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_STUDENT_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_STUDENT_NAME));
                String branch = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BRANCH));
                int semester = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SEMESTER));
                students.add(new ManageStudent(id, name, branch, semester));
            } while (cursor.moveToNext());
            cursor.close();
        }
        manageStudentAdapter.notifyDataSetChanged();
    }
}