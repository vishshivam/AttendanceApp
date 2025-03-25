package com.example.attendo;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class AddStudentActivity extends AppCompatActivity {

    private TextInputEditText editTextStudentName,editTextStudentRollNo;
    private Spinner spinnerBranch, spinnerSemester;
    private MaterialButton btnAddStudent;
    private DatabaseHelper dbHelper;
    private long studentId;
    private boolean isUpdateMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);

        MaterialToolbar toolbar = findViewById(R.id.topAppBarDetail);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        editTextStudentName = findViewById(R.id.editTextStudentName);
        editTextStudentRollNo = findViewById(R.id.editTextStudentRollNo);
        spinnerBranch = findViewById(R.id.spinnerBranch);
        spinnerSemester = findViewById(R.id.spinnerSemester);
        btnAddStudent = findViewById(R.id.btnAddStudent);

        dbHelper = new DatabaseHelper(this);

        setupSpinners();

        // Check if we are in update mode
        studentId = getIntent().getLongExtra("studentId", -1);
        Log.d("DEBUG", "Received Student ID: " + studentId);

        if (studentId != -1) {
            isUpdateMode = true;
            loadStudentData(studentId);
            btnAddStudent.setText("Update Student");
        }

        btnAddStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isUpdateMode) {
                    updateStudent();
                } else {
                    addStudent();
                }
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupSpinners() {
        ArrayAdapter<CharSequence> branchAdapter = ArrayAdapter.createFromResource(this,
                R.array.branch_array, android.R.layout.simple_spinner_item);
        branchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBranch.setAdapter(branchAdapter);

        ArrayAdapter<CharSequence> semesterAdapter = ArrayAdapter.createFromResource(this,
                R.array.semester_array, android.R.layout.simple_spinner_item);
        semesterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSemester.setAdapter(semesterAdapter);
    }

    private void loadStudentData(long studentId) {
        Cursor cursor = dbHelper.getStudent(studentId);
        if (cursor != null && cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_STUDENT_NAME));
            String branch = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BRANCH));
            int semester = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SEMESTER));

            editTextStudentName.setText(name);
            setSpinnerSelection(spinnerBranch, branch);
            setSpinnerSelection(spinnerSemester, String.valueOf(semester));

            cursor.close();
        }
    }

    private void setSpinnerSelection(Spinner spinner, String value) {
        ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) spinner.getAdapter();
        if (adapter != null) {
            int position = adapter.getPosition(value);
            if (position != -1) {
                spinner.setSelection(position);
            }
        }
    }

    private void addStudent() {
        String name = editTextStudentName.getText().toString().trim();
        String rollno = editTextStudentRollNo.getText().toString().trim();
        String branch = spinnerBranch.getSelectedItem().toString();
        int semester = Integer.parseInt(spinnerSemester.getSelectedItem().toString());

        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter student name.", Toast.LENGTH_SHORT).show();
            return;
        }

        long id = dbHelper.addStudent(name, rollno,branch, semester);

        if (id != -1) {
            Toast.makeText(this, "Student added successfully.", Toast.LENGTH_SHORT).show();
            editTextStudentName.setText("");
        } else {
            Toast.makeText(this, "Failed to add student.", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateStudent() {
        String name = editTextStudentName.getText().toString().trim();
        String rollno = editTextStudentRollNo.getText().toString().trim();
        String branch = spinnerBranch.getSelectedItem().toString();
        int semester = Integer.parseInt(spinnerSemester.getSelectedItem().toString());

        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter student name.", Toast.LENGTH_SHORT).show();
            return;
        }

        int rowsAffected = dbHelper.updateStudent(studentId, name, rollno,branch, semester);

        if (rowsAffected > 0) {
            Toast.makeText(this, "Student updated successfully.", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to update student.", Toast.LENGTH_SHORT).show();
        }
    }
}