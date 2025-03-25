// DatabaseHelper.java

package com.example.attendo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "AttendanceDB";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_STUDENTS = "students";
    public static final String TABLE_ATTENDANCE = "attendance";

    public static final String COLUMN_STUDENT_ID = "student_id";
    public static final String COLUMN_STUDENT_NAME = "name";
    public static final String COLUMN_BRANCH = "branch";
    public static final String COLUMN_SEMESTER = "semester";

    public static final String COLUMN_ATTENDANCE_ID = "id";
    public static final String COLUMN_ATTENDANCE_STUDENT_ID = "student_id";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_IS_PRESENT = "is_present";
    public static final String COLUMN_ATTENDANCE_BRANCH = "branch";
    public static final String COLUMN_ATTENDANCE_SEMESTER = "semester";
    // Add Roll Number Column
    public static final String COLUMN_STUDENT_ROLL_NO = "roll_no";

    private static final String CREATE_TABLE_STUDENTS =
            "CREATE TABLE " + TABLE_STUDENTS + "(" +
                    COLUMN_STUDENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_STUDENT_NAME + " TEXT," +
                    COLUMN_STUDENT_ROLL_NO + " TEXT," + // Add Roll Number
                    COLUMN_BRANCH + " TEXT," +
                    COLUMN_SEMESTER + " INTEGER" +
                    ")";


    private static final String CREATE_TABLE_ATTENDANCE =
            "CREATE TABLE " + TABLE_ATTENDANCE + "(" +
                    COLUMN_ATTENDANCE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_ATTENDANCE_STUDENT_ID + " INTEGER," +
                    COLUMN_DATE + " TEXT," +
                    COLUMN_IS_PRESENT + " INTEGER, " +
                    COLUMN_ATTENDANCE_BRANCH + " TEXT, " +
                    COLUMN_ATTENDANCE_SEMESTER + " INTEGER, " +
                    "FOREIGN KEY (" + COLUMN_ATTENDANCE_STUDENT_ID + ") REFERENCES " + TABLE_STUDENTS + "(" + COLUMN_STUDENT_ID + ")" +
                    ")";
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_STUDENTS);
        db.execSQL(CREATE_TABLE_ATTENDANCE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ATTENDANCE);
        onCreate(db);
    }


    public long addStudent(String name, String rollNo, String branch, int semester) { //Add rollNo
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_STUDENT_NAME, name);
        values.put(COLUMN_STUDENT_ROLL_NO, rollNo); //Add rollNo
        values.put(COLUMN_BRANCH, branch);
        values.put(COLUMN_SEMESTER, semester);
        long id = db.insert(TABLE_STUDENTS, null, values);
        db.close();
        return id;
    }

    public void removeStudent(long studentId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_STUDENTS, COLUMN_STUDENT_ID + " = ?", new String[]{String.valueOf(studentId)});
        db.delete(TABLE_ATTENDANCE, COLUMN_ATTENDANCE_STUDENT_ID + " = ?", new String[]{String.valueOf(studentId)});
        db.close();
    }

    public Cursor getStudents(String branch, int semester) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query;
        String[] selectionArgs;

        if (branch == null && semester == 0) {
            query = "SELECT * FROM " + TABLE_STUDENTS;
            selectionArgs = null;
        } else if (branch == null) {
            query = "SELECT * FROM " + TABLE_STUDENTS + " WHERE " + COLUMN_SEMESTER + " = ?";
            selectionArgs = new String[]{String.valueOf(semester)};
        } else {
            query = "SELECT * FROM " + TABLE_STUDENTS + " WHERE " + COLUMN_BRANCH + " = ? AND " + COLUMN_SEMESTER + " = ?";
            selectionArgs = new String[]{branch, String.valueOf(semester)};
        }

        Log.d("DatabaseHelper", "getStudents: Query = " + query);
        Log.d("DatabaseHelper", "getStudents: Selection Args = " + java.util.Arrays.toString(selectionArgs));

        return db.rawQuery(query, selectionArgs);
    }


    public long addAttendance(long studentId, String date, int isPresent, String branch, int semester) {
        if (isAttendanceDuplicate(studentId, date)) { // Modified duplicate check
            return -1; // Indicate duplicate record
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ATTENDANCE_STUDENT_ID, studentId);
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_IS_PRESENT, isPresent);
        values.put(COLUMN_ATTENDANCE_BRANCH, branch);
        values.put(COLUMN_ATTENDANCE_SEMESTER, semester);
        long id = db.insert(TABLE_ATTENDANCE, null, values);
        db.close();
        return id;
    }

    public boolean isAttendanceDuplicate(long studentId, String date) {
        Log.d("DatabaseHelper", "isAttendanceDuplicate: date = " + date);
        if (date == null) {
            return false;
        }
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_ATTENDANCE_ID};
        String selection = COLUMN_ATTENDANCE_STUDENT_ID + " = ? AND " + COLUMN_DATE + " = ?";
        String[] selectionArgs = {String.valueOf(studentId), date};
        Cursor cursor = db.query(TABLE_ATTENDANCE, columns, selection, selectionArgs, null, null, null);
        boolean duplicate = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return duplicate;
    }
    // Add this method to get the attendance summary for a student
    public Cursor getAttendanceSummary(long studentId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " +
                "COUNT(CASE WHEN " + COLUMN_IS_PRESENT + " = 1 THEN 1 ELSE NULL END) AS present, " +
                "COUNT(CASE WHEN " + COLUMN_IS_PRESENT + " = 0 THEN 1 ELSE NULL END) AS absent, " +
                "COUNT(*) AS total " +
                "FROM " + TABLE_ATTENDANCE + " " +
                "WHERE " + COLUMN_ATTENDANCE_STUDENT_ID + " = ?";
        return db.rawQuery(query, new String[]{String.valueOf(studentId)});
    }

    public Cursor getAttendanceReport(String branch, int semester) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + TABLE_STUDENTS + "." + COLUMN_STUDENT_ID + ", " + TABLE_STUDENTS + "." + COLUMN_STUDENT_NAME + ", " +
                "SUM(CASE WHEN " + TABLE_ATTENDANCE + "." + COLUMN_IS_PRESENT + " = 1 THEN 1 ELSE 0 END) * 100.0 / COUNT(" + TABLE_ATTENDANCE + "." + COLUMN_IS_PRESENT + ") AS attendancePercentage " +
                "FROM " + TABLE_STUDENTS + " LEFT JOIN " + TABLE_ATTENDANCE + " ON " + TABLE_STUDENTS + "." + COLUMN_STUDENT_ID + " = " + TABLE_ATTENDANCE + "." + COLUMN_ATTENDANCE_STUDENT_ID + " " +
                "WHERE " + TABLE_STUDENTS + "." + COLUMN_BRANCH + " = ? AND " + TABLE_STUDENTS + "." + COLUMN_SEMESTER + " = ? " +
                "GROUP BY " + TABLE_STUDENTS + "." + COLUMN_STUDENT_ID;
        return db.rawQuery(query, new String[]{branch, String.valueOf(semester)});
    }

    public int getStudentCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT count(*) FROM " + TABLE_STUDENTS, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    public int updateStudent(long studentId, String name, String rollNo, String branch, int semester) { //Add rollNo
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_STUDENT_NAME, name);
        values.put(COLUMN_STUDENT_ROLL_NO, rollNo); //Add rollNo
        values.put(COLUMN_BRANCH, branch);
        values.put(COLUMN_SEMESTER, semester);
        return db.update(TABLE_STUDENTS, values, COLUMN_STUDENT_ID + " = ?", new String[]{String.valueOf(studentId)});
    }

    public Cursor getStudent(long studentId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_STUDENTS + " WHERE " + COLUMN_STUDENT_ID + " = ?", new String[]{String.valueOf(studentId)});
    }
}