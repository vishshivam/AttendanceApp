// ManageStudentsActivity.java
package com.example.attendo;

import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;

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

        MaterialToolbar toolbar = findViewById(R.id.topAppBarDetail);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
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