package com.example.attendo;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TakeAttendanceActivity extends AppCompatActivity {

    private Spinner spinnerBranch, spinnerSemester;
    private Button btnDatePicker, btnSaveAttendance;
    private RecyclerView recyclerViewStudents;
    private StudentAdapter studentAdapter;
    private DatabaseHelper dbHelper;
    private Calendar calendar;
    private String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_attendance);

        MaterialToolbar toolbar = findViewById(R.id.topAppBarDetail);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        spinnerBranch = findViewById(R.id.spinnerBranch);
        spinnerSemester = findViewById(R.id.spinnerSemester);
        btnDatePicker = findViewById(R.id.btnDatePicker);
        btnSaveAttendance = findViewById(R.id.btnSaveAttendance);
        recyclerViewStudents = findViewById(R.id.recyclerViewStudents);

        dbHelper = new DatabaseHelper(this);
        calendar = Calendar.getInstance();

        setupSpinners();
        setupDatePicker();
        setupRecyclerView();

        btnSaveAttendance.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(TakeAttendanceActivity.this, R.style.CustomAlertDialogTheme);
            builder.setTitle("Confirm Save");
            builder.setMessage("Are you sure you want to save the attendance?");
            builder.setPositiveButton("Yes", (dialog, which) -> {
                // Proceed with saving attendance here
                saveAttendance(); // Call your saveAttendance method
            });
            builder.setNegativeButton("No", null);
            AlertDialog dialog = builder.create();
            dialog.show();
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

        spinnerBranch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadStudents();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinnerSemester.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadStudents();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setupDatePicker() {
        btnDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(TakeAttendanceActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                calendar.set(Calendar.YEAR, year);
                                calendar.set(Calendar.MONTH, month);
                                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());
                                btnDatePicker.setText(selectedDate);
                            }
                        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });
        selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());
        btnDatePicker.setText(selectedDate);
    }

    private void setupRecyclerView() {
        recyclerViewStudents.setLayoutManager(new LinearLayoutManager(this));
        studentAdapter = new StudentAdapter(new ArrayList<>(), this, dbHelper);
        recyclerViewStudents.setAdapter(studentAdapter);
    }

    private void loadStudents() {
        String branch = spinnerBranch.getSelectedItem().toString();
        int semester = Integer.parseInt(spinnerSemester.getSelectedItem().toString());
        Cursor cursor = dbHelper.getStudents(branch, semester);
        List<Student> students = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_STUDENT_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_STUDENT_NAME));
                students.add(new Student(id, name));
            } while (cursor.moveToNext());
            cursor.close();
        }
        studentAdapter.setStudents(students);
    }

    private void saveAttendance() {
        List<Student> students = studentAdapter.getStudents();
        if (students.isEmpty()) {
            Toast.makeText(this, "No students to save attendance for.", Toast.LENGTH_SHORT).show();
            return;
        }

        for (Student student : students) {
            int isPresent = student.isPresent() ? 1 : 0;
            dbHelper.addAttendance(student.getId(), selectedDate, isPresent);
        }
        Toast.makeText(this, "Attendance saved successfully.", Toast.LENGTH_SHORT).show();
    }
    //Inside TakeAttendanceActivity.java
    @Override
    protected void onResume() {
        super.onResume();
        loadStudents(); // Reload the student list
    }
}
