package com.example.attendo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;


public class MainActivity extends AppCompatActivity {

    private MaterialButton btnTakeAttendance;
    private MaterialButton btnViewReport;
    private MaterialButton btnAddStudent;
    private MaterialButton btnManageStudent;

    private TextView studentCountTextView;
    private DatabaseHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        btnTakeAttendance = findViewById(R.id.btnTakeAttendance);
        btnViewReport = findViewById(R.id.btnViewReport);
        btnAddStudent = findViewById(R.id.btnAddStudent);
        btnManageStudent = findViewById(R.id.btnManageStudent);
        studentCountTextView = findViewById(R.id.studentCountTextView);
        dbHelper = new DatabaseHelper(this);

        updateStudentCount();

        btnTakeAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TakeAttendanceActivity.class);
                startActivity(intent);
            }
        });

        btnViewReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ReportActivity.class);
                startActivity(intent);
            }
        });

        btnAddStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddStudentActivity.class);
                startActivity(intent);
            }
        });
        btnManageStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ManageStudentsActivity.class);
                startActivity(intent);

            }
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        updateStudentCount();
    }

    private void updateStudentCount() {
        int studentCount = dbHelper.getStudentCount();
        studentCountTextView.setText("Total Students: " + studentCount);
    }
}