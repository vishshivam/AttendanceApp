package com.example.attendo;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
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
    private Button btnSaveAttendance, btnBulkAttendance, btnSelectDate;
    private RecyclerView recyclerViewStudents;
    private StudentAdapter studentAdapter;
    private DatabaseHelper dbHelper;
    private Calendar calendar;
    private boolean allPresent = false;
    private String selectedDate;
    private AttendanceViewModel viewModel; // Add ViewModel instance

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
        btnSaveAttendance = findViewById(R.id.btnSaveAttendance);
        btnBulkAttendance = findViewById(R.id.btnBulkAttendance);
        recyclerViewStudents = findViewById(R.id.recyclerViewStudents);
        btnSelectDate = findViewById(R.id.btnSelectDate);

        dbHelper = new DatabaseHelper(this);
        calendar = Calendar.getInstance();

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(AttendanceViewModel.class);

        setupSpinners();
        setupRecyclerView();
        setupDatePicker();
        observeStudentList(); // Observe the LiveData

        // Load initial data if the ViewModel doesn't have it yet
        if (viewModel.getStudentList().getValue() == null) {
            // Get initial selection from spinners
            String initialBranch = spinnerBranch.getSelectedItem().toString();
            int initialSemester = Integer.parseInt(spinnerSemester.getSelectedItem().toString());
            viewModel.loadStudents(initialBranch, initialSemester);
        }

        btnSaveAttendance.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(TakeAttendanceActivity.this, R.style.CustomAlertDialogTheme);
            builder.setTitle("Confirm Save");
            builder.setMessage("Are you sure you want to save the attendance?");
            builder.setPositiveButton("Yes", (dialog, which) -> saveAttendance());
            builder.setNegativeButton("No", null);
            AlertDialog dialog = builder.create();
            dialog.show();
        });

        btnBulkAttendance.setOnClickListener(v -> {
            allPresent = !allPresent;
            viewModel.markAllPresent(allPresent);
            updateButtonText();
        });
        updateButtonText(); // Initial button text setup
    }

    private void observeStudentList() {
        viewModel.getStudentList().observe(this, students -> {
           // Log.d("TakeAttendanceActivity", "observeStudentList: List updated with " + students.size() + " students");

            studentAdapter.setStudents(students);
        });
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
                String branch = spinnerBranch.getSelectedItem().toString();
                int semester = Integer.parseInt(spinnerSemester.getSelectedItem().toString());
                // Check if the ViewModel has data for this selection
                if (viewModel.getStudentList().getValue() == null || !branch.equals(viewModel.getCurrentBranch()) || semester != viewModel.getCurrentSemester()) {
                    loadStudents();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinnerSemester.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String branch = spinnerBranch.getSelectedItem().toString();
                int semester = Integer.parseInt(spinnerSemester.getSelectedItem().toString());
                // Check if the ViewModel has data for this selection
                if (viewModel.getStudentList().getValue() == null || !branch.equals(viewModel.getCurrentBranch()) || semester != viewModel.getCurrentSemester()) {
                    loadStudents();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setupDatePicker() {
        btnSelectDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(TakeAttendanceActivity.this,
                    (view, year, month, dayOfMonth) -> {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        selectedDate = new SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault()).format(calendar.getTime());
                        btnSelectDate.setText(selectedDate);
                    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });
    }

    private void setupRecyclerView() {
        recyclerViewStudents.setLayoutManager(new LinearLayoutManager(this));
        studentAdapter = new StudentAdapter(new ArrayList<>(), this, viewModel); // Pass the ViewModel
        recyclerViewStudents.setAdapter(studentAdapter);
    }

    private void loadStudents() {
        Log.d("TakeAttendanceActivity", "loadStudents() called");

        String branch = spinnerBranch.getSelectedItem().toString();
        int semester = Integer.parseInt(spinnerSemester.getSelectedItem().toString());
        viewModel.loadStudents(branch, semester); // Load students via ViewModel
    }

    private void saveAttendance() {
        List<Student> students = viewModel.getCurrentStudentList(); // Get the latest student list from ViewModel
        if (students == null || students.isEmpty()) {
            Toast.makeText(this, "No students to save attendance for.", Toast.LENGTH_SHORT).show();
            return;
        }

        String branch = spinnerBranch.getSelectedItem().toString();
        int semester = Integer.parseInt(spinnerSemester.getSelectedItem().toString());

        if (selectedDate == null || selectedDate.isEmpty()) {
            Toast.makeText(this, "Please select a date.", Toast.LENGTH_SHORT).show();
            return;
        }

        for (Student student : students) {
            int isPresent = student.isPresent() ? 1 : 0;
            long result = dbHelper.addAttendance(student.getId(), selectedDate, isPresent, branch, semester);
            if (result == -1) {
                Toast.makeText(this, "Duplicate attendance record found for: " + student.getName(), Toast.LENGTH_SHORT).show();
            }
        }
        Toast.makeText(this, "Attendance saved successfully.", Toast.LENGTH_SHORT).show();
    }
}