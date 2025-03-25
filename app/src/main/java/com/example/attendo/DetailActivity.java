package com.example.attendo;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private TextView textViewStudentName, textViewRollNo, textViewBranch, textViewSemester;
    private TextView textViewTotalPresent, textViewTotalAbsent, textViewTotalDays, textViewPercentage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        MaterialToolbar toolbar = findViewById(R.id.topAppBarDetail);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        dbHelper = new DatabaseHelper(this);
        RecyclerView recyclerViewAttendanceDetails = findViewById(R.id.recyclerViewAttendanceDetails);
        recyclerViewAttendanceDetails.setLayoutManager(new LinearLayoutManager(this));

        // Initialize TextViews for student details
        textViewStudentName = findViewById(R.id.textViewStudentName);
        textViewRollNo = findViewById(R.id.textViewRollNo);
        textViewBranch = findViewById(R.id.textViewBranch);
        textViewSemester = findViewById(R.id.textViewSemester);

        // Initialize TextViews for attendance summary
        textViewTotalPresent = findViewById(R.id.textViewTotalPresent);
        textViewTotalAbsent = findViewById(R.id.textViewTotalAbsent);
        textViewTotalDays = findViewById(R.id.textViewTotalDays);
        textViewPercentage = findViewById(R.id.textViewPercentage);


        int studentId = getIntent().getIntExtra("studentId", -1);
        Log.d("DetailActivity", "Fetching attendance details for studentId: " + studentId);
        if (studentId != -1) {
            loadStudentDetails(studentId);
            loadAttendanceSummary(studentId);
            List<AttendanceDetail> attendanceDetails = fetchAttendanceDetails(studentId);
            AttendanceDetailAdapter adapter = new AttendanceDetailAdapter(this, attendanceDetails);
            recyclerViewAttendanceDetails.setAdapter(adapter);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }private void loadStudentDetails(int studentId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] columns = {DatabaseHelper.COLUMN_STUDENT_NAME, DatabaseHelper.COLUMN_STUDENT_ROLL_NO, DatabaseHelper.COLUMN_BRANCH, DatabaseHelper.COLUMN_SEMESTER};
        String selection = DatabaseHelper.COLUMN_STUDENT_ID + " = ?";
        String[] selectionArgs = {String.valueOf(studentId)};
        Cursor cursor = db.query(DatabaseHelper.TABLE_STUDENTS, columns, selection, selectionArgs, null, null, null);
        if (cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_STUDENT_NAME));
            String rollNo = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_STUDENT_ROLL_NO));
            String branch = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BRANCH));
            int semester = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SEMESTER));

            textViewStudentName.setText("Name: " + name);
            textViewRollNo.setText("Roll No: " + rollNo);
            textViewBranch.setText("Branch: " + branch);
            textViewSemester.setText("Semester: " + semester);
        }
        cursor.close();
        db.close();
    }

    private void loadAttendanceSummary(int studentId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT " +
                "COUNT(CASE WHEN " + DatabaseHelper.COLUMN_IS_PRESENT + " = 1 THEN 1 ELSE NULL END) AS present, " +
                "COUNT(CASE WHEN " + DatabaseHelper.COLUMN_IS_PRESENT + " = 0 THEN 1 ELSE NULL END) AS absent, " +
                "COUNT(*) AS total " +
                "FROM " + DatabaseHelper.TABLE_ATTENDANCE + " " +
                "WHERE " + DatabaseHelper.COLUMN_ATTENDANCE_STUDENT_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(studentId)});

        if (cursor.moveToFirst()) {
            int present = cursor.getInt(cursor.getColumnIndexOrThrow("present"));
            int absent = cursor.getInt(cursor.getColumnIndexOrThrow("absent"));
            int total = cursor.getInt(cursor.getColumnIndexOrThrow("total"));

            textViewTotalPresent.setText("Present: " + present);
            textViewTotalAbsent.setText("Absent: " + absent);
            textViewTotalDays.setText("Total Days: " + total);

            if (total > 0) {
                double percentage = (double) present / total * 100;
                textViewPercentage.setText(String.format("Percentage: %.2f%%", percentage));
            } else {
                textViewPercentage.setText("Percentage: 0%");
            }
        }
        cursor.close();
        db.close();
    }

    private List<AttendanceDetail> fetchAttendanceDetails(int studentId) {
        Log.d("DetailActivity", "Fetching attendance details for studentId: " + studentId);

        List<AttendanceDetail> attendanceDetails = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] columns = {DatabaseHelper.COLUMN_DATE, DatabaseHelper.COLUMN_IS_PRESENT};
        String selection = DatabaseHelper.COLUMN_ATTENDANCE_STUDENT_ID + " = ?";
        String[] selectionArgs = {String.valueOf(studentId)};
        String orderBy = DatabaseHelper.COLUMN_DATE + " ASC";

        Cursor cursor = db.query(DatabaseHelper.TABLE_ATTENDANCE, columns, selection, selectionArgs, null, null, orderBy);

        if (cursor.moveToFirst()) {
            do {
                String date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DATE));
                int isPresent = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_IS_PRESENT));
                String status = (isPresent == 1) ? "P" : "A"; // Convert 1/0 to Present/Absent
                Log.d("DetailActivity", "Date: " + date + ", Status: " + status);

                attendanceDetails.add(new AttendanceDetail(date, status));
            } while (cursor.moveToNext());
        }
        Log.d("DetailActivity", "No attendance details found for studentId: " + studentId);

        cursor.close();
        db.close();
        return attendanceDetails;
    }
}