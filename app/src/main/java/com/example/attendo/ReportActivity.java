package com.example.attendo;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ReportActivity extends AppCompatActivity {

    private Spinner spinnerBranch, spinnerSemester;
    private RecyclerView recyclerViewReport;
    private ReportAdapter reportAdapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        spinnerBranch = findViewById(R.id.spinnerBranchReport);
        spinnerSemester = findViewById(R.id.spinnerSemesterReport);
        recyclerViewReport = findViewById(R.id.recyclerViewReport);

        dbHelper = new DatabaseHelper(this);

        setupSpinners();
        setupRecyclerView();
    }

    private void setupSpinners() {
        ArrayAdapter<CharSequence> branchAdapter = ArrayAdapter.createFromResource(this,
                R.array.branches_array, android.R.layout.simple_spinner_item);
        branchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBranch.setAdapter(branchAdapter);

        ArrayAdapter<CharSequence> semesterAdapter = ArrayAdapter.createFromResource(this,
                R.array.semesters_array, android.R.layout.simple_spinner_item);
        semesterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSemester.setAdapter(semesterAdapter);

        spinnerBranch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadReport();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinnerSemester.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadReport();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setupRecyclerView() {
        recyclerViewReport.setLayoutManager(new LinearLayoutManager(this));
        reportAdapter = new ReportAdapter(new ArrayList<>());
        recyclerViewReport.setAdapter(reportAdapter);
    }

    private void loadReport() {
        String branch = spinnerBranch.getSelectedItem().toString();
        int semester = Integer.parseInt(spinnerSemester.getSelectedItem().toString());
        Cursor cursor = dbHelper.getAttendanceReport(branch, semester);
        List<ReportItem> reportItems = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                double percentage = cursor.getDouble(cursor.getColumnIndexOrThrow("attendancePercentage"));
                reportItems.add(new ReportItem(name, percentage));
            } while (cursor.moveToNext());
            cursor.close();
        }
        reportAdapter.setReportItems(reportItems);
    }
}