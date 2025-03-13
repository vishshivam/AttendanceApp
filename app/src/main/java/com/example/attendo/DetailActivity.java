package com.example.attendo;// DetailActivity.java

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        dbHelper = new DatabaseHelper(this);
        RecyclerView recyclerViewAttendanceDetails = findViewById(R.id.recyclerViewAttendanceDetails);
        recyclerViewAttendanceDetails.setLayoutManager(new LinearLayoutManager(this));

        int studentId = getIntent().getIntExtra("studentId", -1);
        Log.d("DetailActivity", "Fetching attendance details for studentId: " + studentId);
        if (studentId != -1) {
            // Fetch day-wise attendance details from database using studentId
            List<AttendanceDetail> attendanceDetails = fetchAttendanceDetails(studentId); // Implement this method

            // Set adapter to RecyclerView
            AttendanceDetailAdapter adapter = new AttendanceDetailAdapter(this, attendanceDetails);
            recyclerViewAttendanceDetails.setAdapter(adapter);
        }
    }

    // Implement this method to fetch attendance details from database
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