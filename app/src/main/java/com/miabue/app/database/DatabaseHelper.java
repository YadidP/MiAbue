package com.miabue.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.miabue.app.models.Treatment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "app.db";
    private static final int DATABASE_VERSION = 1;

    // Tabla de tratamientos
    private static final String TABLE_TREATMENTS = "treatments";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_DOSE = "dose";
    private static final String COLUMN_FREQUENCY = "frequency_hours";
    private static final String COLUMN_START_TIME = "start_time";
    private static final String COLUMN_CREATED_DATE = "created_date";
    private static final String COLUMN_IS_ACTIVE = "is_active";
    private static final String COLUMN_LAST_TAKEN = "last_taken";

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_TREATMENTS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT NOT NULL, " +
                COLUMN_DOSE + " TEXT NOT NULL, " +
                COLUMN_FREQUENCY + " INTEGER NOT NULL, " +
                COLUMN_START_TIME + " TEXT NOT NULL, " +
                COLUMN_CREATED_DATE + " TEXT NOT NULL, " +
                COLUMN_IS_ACTIVE + " INTEGER DEFAULT 1, " +
                COLUMN_LAST_TAKEN + " TEXT" +
                ");";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TREATMENTS);
        onCreate(db);
    }

    // Insertar tratamiento
    public long insertTreatment(Treatment treatment) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_NAME, treatment.getName());
        values.put(COLUMN_DOSE, treatment.getDose());
        values.put(COLUMN_FREQUENCY, treatment.getFrequencyHours());
        values.put(COLUMN_START_TIME, treatment.getStartTime());
        values.put(COLUMN_CREATED_DATE, dateFormat.format(treatment.getCreatedDate()));
        values.put(COLUMN_IS_ACTIVE, treatment.isActive() ? 1 : 0);

        long id = db.insert(TABLE_TREATMENTS, null, values);
        db.close();
        return id;
    }

    // Obtener todos los tratamientos activos
    public List<Treatment> getAllActiveTreatments() {
        List<Treatment> treatments = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_TREATMENTS + " WHERE " + COLUMN_IS_ACTIVE + " = 1";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Treatment treatment = cursorToTreatment(cursor);
                treatments.add(treatment);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return treatments;
    }

    // Actualizar última toma de medicamento
    public void updateLastTaken(int treatmentId, Date lastTaken) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_LAST_TAKEN, dateFormat.format(lastTaken));

        db.update(TABLE_TREATMENTS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(treatmentId)});
        db.close();
    }

    // Convertir cursor a Treatment
    private Treatment cursorToTreatment(Cursor cursor) {
        Treatment treatment = new Treatment();
        treatment.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
        treatment.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)));
        treatment.setDose(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DOSE)));
        treatment.setFrequencyHours(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_FREQUENCY)));
        treatment.setStartTime(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_START_TIME)));
        treatment.setActive(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_ACTIVE)) == 1);

        try {
            String createdDateStr = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATED_DATE));
            treatment.setCreatedDate(dateFormat.parse(createdDateStr));

            String lastTakenStr = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LAST_TAKEN));
            if (lastTakenStr != null) {
                treatment.setLastTaken(dateFormat.parse(lastTakenStr));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return treatment;
    }
}