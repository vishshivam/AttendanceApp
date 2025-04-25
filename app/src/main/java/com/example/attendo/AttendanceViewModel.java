package com.example.attendo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.attendo.Student; // Make sure this import is correct
import com.example.attendo.DatabaseHelper; // Import your DatabaseHelper
import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class AttendanceViewModel extends AndroidViewModel {

    private MutableLiveData<List<Student>> studentListLiveData = new MutableLiveData<>();
    private String currentBranch;
    private int currentSemester;
    private DatabaseHelper dbHelper;

    public AttendanceViewModel(Application application) {
        super(application);
        dbHelper = new DatabaseHelper(application);
    }

    public LiveData<List<Student>> getStudentList() {
        return studentListLiveData;
    }

    public String getCurrentBranch() {
        return currentBranch;
    }

    public int getCurrentSemester() {
        return currentSemester;
    }

    public void loadStudents(String branch, int semester) {
        Log.d("AttendanceViewModel", "loadStudents(" + branch + ", " + semester + ") called");
        currentBranch = branch;
        currentSemester = semester;
        Cursor cursor = dbHelper.getStudents(branch, semester);
        List<Student> students = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_STUDENT_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_STUDENT_NAME));
                students.add(new Student(id, name, false));
            } while (cursor.moveToNext());
            cursor.close();
        }
        studentListLiveData.setValue(students);
        Log.d("AttendanceViewModel", "loadStudents: LiveData updated with " + students.size() + " students");
    }

    public void updateStudentAttendance(int position, boolean isPresent) {
        List<Student> currentList = studentListLiveData.getValue();
        if (currentList != null && position < currentList.size()) {
            currentList.get(position).setPresent(isPresent);
            studentListLiveData.setValue(currentList);
        }
    }

    public List<Student> getCurrentStudentList() {
        return studentListLiveData.getValue();
    }

    public void markAllPresent(boolean present) {
        List<Student> currentList = studentListLiveData.getValue();
        if (currentList != null) {
            for (Student student : currentList) {
                student.setPresent(present);
            }
            studentListLiveData.setValue(currentList);
        }
    }
}