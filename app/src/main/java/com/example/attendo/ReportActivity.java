// ReportActivity.java
package com.example.attendo;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ReportActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private RecyclerView recyclerViewReport;
    private ReportAdapter reportAdapter;
    private List<ReportItem> allReportItems;
    private Spinner spinnerBranch, spinnerSemester;
    private Button buttonDate, buttonGenerateReport;
    private Calendar selectedDate;
    private SimpleDateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        dbHelper = new DatabaseHelper(this);
        recyclerViewReport = findViewById(R.id.recyclerViewReport);
        spinnerBranch = findViewById(R.id.spinnerBranch);
        spinnerSemester = findViewById(R.id.spinnerSemester);
        buttonDate = findViewById(R.id.buttonDate);
        buttonGenerateReport = findViewById(R.id.buttonGenerateReport);

        setupSpinners();
        setupRecyclerView();
        loadAllReports();

        selectedDate = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        buttonDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        buttonGenerateReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterReports();
            }
        });
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

    private void setupRecyclerView() {
        recyclerViewReport.setLayoutManager(new LinearLayoutManager(this));
        reportAdapter = new ReportAdapter(this,new ArrayList<>());
        recyclerViewReport.setAdapter(reportAdapter);
    }

    private void loadAllReports() {
        allReportItems = getAllReportData();
    }

    private List<ReportItem> getAllReportData() {
        Cursor cursor = dbHelper.getStudents(null, 0);
        List<ReportItem> reportItems = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                long studentId = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_STUDENT_ID));
                if (cursor.isNull(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_STUDENT_ID))) {
                    continue;
                }
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_STUDENT_NAME));
                String branch = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BRANCH));
                int semester = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SEMESTER));
                double percentage = calculateAttendancePercentage(studentId);
                reportItems.add(new ReportItem(name, percentage, "", branch, semester, studentId));
            } while (cursor.moveToNext());
            cursor.close();
        }
        for (ReportItem item : reportItems) {
        }
        return reportItems;
    }

    private double calculateAttendancePercentage(long studentId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_ATTENDANCE + " WHERE " + DatabaseHelper.COLUMN_ATTENDANCE_STUDENT_ID + " = ?";
        Cursor totalCursor = db.rawQuery(query, new String[]{String.valueOf(studentId)});
        int totalCount = 0;
        if (totalCursor != null && totalCursor.moveToFirst()) {
            totalCount = totalCursor.getInt(0);
            totalCursor.close();
        }

        query = "SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_ATTENDANCE + " WHERE " + DatabaseHelper.COLUMN_ATTENDANCE_STUDENT_ID + " = ? AND " + DatabaseHelper.COLUMN_IS_PRESENT + " = 1";
        Cursor presentCursor = db.rawQuery(query, new String[]{String.valueOf(studentId)});
        int presentCount = 0;
        if (presentCursor != null && presentCursor.moveToFirst()) {
            presentCount = presentCursor.getInt(0);
            presentCursor.close();
        }

        if (totalCount == 0) {
            return 0.0;
        }
        return (double) presentCount / totalCount * 100.0;
    }

    private void filterReports() {
        String branch = spinnerBranch.getSelectedItem().toString();
        String semesterString = spinnerSemester.getSelectedItem().toString();
        int semester = Integer.parseInt(semesterString);
        String date = dateFormat.format(selectedDate.getTime());

        List<ReportItem> filteredList = new ArrayList<>();
        if (allReportItems != null) {
            for (ReportItem item : allReportItems) {
                if (item.getBranch().equals(branch) && item.getSemester() == semester) {
                    if (item.getStudentId() != 0) {
                        String status = getAttendanceStatus(item.getStudentId(), date);
                        filteredList.add(new ReportItem(item.getName(), item.getPercentage(), status, item.getBranch(), item.getSemester(), item.getStudentId()));
                    }
                }
            }
        } else {
        }
        for (ReportItem item : filteredList) {
        }
        reportAdapter.setReportItems(filteredList);
    }

    private String getAttendanceStatus(long studentId, String date) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT " + DatabaseHelper.COLUMN_IS_PRESENT + " FROM " + DatabaseHelper.TABLE_ATTENDANCE + " WHERE " + DatabaseHelper.COLUMN_ATTENDANCE_STUDENT_ID + " = ? AND " + DatabaseHelper.COLUMN_DATE + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(studentId), date});
        String status = "Absent";
        if (cursor != null && cursor.moveToFirst()) {
            int isPresent =cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_IS_PRESENT));
            status = isPresent == 1 ? "Present" : "Absent";
            cursor.close();
        }
        return status;
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        selectedDate.set(Calendar.YEAR, year);
                        selectedDate.set(Calendar.MONTH, month);
                        selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        buttonDate.setText(dateFormat.format(selectedDate.getTime()));
                    }
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }
}