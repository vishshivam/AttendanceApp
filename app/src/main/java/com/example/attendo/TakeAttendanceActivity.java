// TakeAttendanceActivity.java

package com.example.attendo;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class TakeAttendanceActivity extends AppCompatActivity {

    private Spinner spinnerBranch, spinnerSemester;
    private Button btnSaveAttendance, btnBulkAttendance;
    private RecyclerView recyclerViewStudents;
    private StudentAdapter studentAdapter;
    private DatabaseHelper dbHelper;
    private Calendar calendar;
    private BulkStudentAdapter bulkStudentAdapter;
    private boolean allPresent = false; // Add this variable
    private TextView textViewDate;


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
        textViewDate = findViewById(R.id.textViewDate);
        btnSaveAttendance = findViewById(R.id.btnSaveAttendance);
        btnBulkAttendance = findViewById(R.id.btnBulkAttendance);
        recyclerViewStudents = findViewById(R.id.recyclerViewStudents);

        dbHelper = new DatabaseHelper(this);
        calendar = Calendar.getInstance();

        setupSpinners();
        setupRecyclerView();
        displayCurrentDate(); // Call the method to display current date.



        btnSaveAttendance.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(TakeAttendanceActivity.this, R.style.CustomAlertDialogTheme);
            builder.setTitle("Confirm Save");
            builder.setMessage("Are you sure you want to save the attendance?");
            builder.setPositiveButton("Yes", (dialog, which) -> saveAttendance());
            builder.setNegativeButton("No", null);
            AlertDialog dialog = builder.create();
            dialog.show();
        });

        btnBulkAttendance.setOnClickListener(v -> markAllPresent());
    }
    private void markAllPresent() {
        allPresent = !allPresent; // Toggle the state
        studentAdapter.markAllPresent(allPresent);
        updateButtonText(); // Update button text
    }
    private void updateButtonText() {
        if (allPresent) {
            btnBulkAttendance.setText("Mark All Absent");
        } else {
            btnBulkAttendance.setText("Mark All Present");
        }
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

    private void displayCurrentDate() {
        calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(calendar.getTime());

        textViewDate.setText(formattedDate);
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

        String branch = spinnerBranch.getSelectedItem().toString();
        int semester = Integer.parseInt(spinnerSemester.getSelectedItem().toString());

        for (Student student : students) {
            int isPresent = student.isPresent() ? 1 : 0;
            String dateFromTextView = textViewDate.getText().toString(); //Get date from textview.

            if (dateFromTextView != null && !dateFromTextView.isEmpty()) { // Null check
                long result = dbHelper.addAttendance(student.getId(), dateFromTextView, isPresent, branch, semester);
                if (result == -1) {
                    Toast.makeText(this, "Duplicate attendance record found for: " + student.getName(), Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(this, "Date is not selected", Toast.LENGTH_SHORT).show();
            }
        }
        Toast.makeText(this, "Attendance saved successfully.", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume () {
        super.onResume();
        loadStudents();
    }

}